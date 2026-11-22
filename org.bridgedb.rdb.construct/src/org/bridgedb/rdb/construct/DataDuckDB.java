// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
// Copyright 2006-2009 BridgeDb developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.rdb.construct;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bridgedb.IDMapperException;

/**
   DBConnector implementation using the DuckDB driver, with the database
   stored in a single DuckDB file.
   While creating, the data is stored in a temporary directory.
   This directory will be cleaned up when the database is finalized.
*/
public class DataDuckDB extends DBConnector
{
	public static final String DB_FILE_EXT_GDB = "bridge";
	public static final String DB_FILE_EXT_GEX = "pgex";

	private static final String DB_NAME_IN_TMPDIR = "database";

	String getDbExt() {
		switch(getDbType()) {
		case TYPE_GDB: return DB_FILE_EXT_GDB;
		case TYPE_GEX: return DB_FILE_EXT_GEX;
		default: return "";
		}
	}

	// name of db, what it will be when the db is finalized.
	private String finalDbName;

	// while making a database, it is created in a temporary directory,
	private File tempDbFile = null;
	private File tempDbParentdir = null;

	private boolean finalized;

	/**
	 * Generates a correct JDBC connection string.
	 * @return the JDBC connection string.
	 */
	private String getDbUrl()
	{
		// unlike Derby, DuckDB keeps a whole database in a single file, so there is
		// no need to wrap it in a jar/zip archive to get a single-file distributable.
		return "jdbc:duckdb:" + (finalized ? finalDbName : tempDbFile);
	}

	/**
	 * @param props one of PROP_NONE or PROP_RECREATE. PROP_NONE will
	 *   lead to a normal connection, PROP_RECREATE will lead to destroying
	 *   the old database and creating a clean new one.
	 * @param dbName is the file that will be produced finally.
	 * If dbName doesn't end with the right extension, the right extension will be added.
	 * @return the JDBC database Connection.
	 * @throws IDMapperException if the database connection could not be made,
	 * 	or if the DuckDB driver could not be loaded.
	 */
	public Connection createConnection(String dbName, int props) throws IDMapperException
	{
		boolean recreate = (props & PROP_RECREATE) != 0;

		// make sure the final Db name ends with the right extension.
		finalDbName = dbName;
		finalized = !recreate;

		if(recreate)
		{
			finalDbName = dbName.endsWith(getDbExt()) ? dbName : dbName + "." + getDbExt();
			try
			{
				tempDbParentdir = FileUtilsGdb.createTempDir("duckdb", ".tmp");
				tempDbFile = new File (tempDbParentdir, DB_NAME_IN_TMPDIR + "." + getDbExt());
			}
			catch (IOException e)
			{
				throw new IDMapperException (e);
			}
		}

		try
		{
			Class.forName("org.duckdb.DuckDBDriver");
		}
		catch (ClassNotFoundException e)
		{
			throw new IDMapperException (
				"Could not find the DuckDB JDBC driver (org.duckdb.DuckDBDriver) on the classpath", e);
		}

		String url = getDbUrl();

		Connection con;
		try
		{
			con = DriverManager.getConnection(url);
		}
		catch (SQLException e)
		{
			throw new IDMapperException (e);
		}

		return con;
	}

	public Connection createNewDatabaseConnection(String dbName) throws IDMapperException
	{
		return createConnection(FileUtilsGdb.removeExtension(dbName), PROP_RECREATE);
	}

	public String finalizeNewDatabase(String dbName) throws IDMapperException
	{
		if (finalized) return finalDbName; // already finalized.

		// move the finished database file to its final location and clear the temp dir.
		try
		{
			File dest = new File (finalDbName);
			if (dest.exists()) dest.delete();
			Files.move(tempDbFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e)
		{
			throw new IDMapperException (e);
		}

		FileUtilsGdb.deleteRecursive(tempDbParentdir);

		finalized = true;

		//Return new database file
		return finalDbName;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * DuckDB cannot commit a transaction that alters a table after updating it, as happens when
	 * setting a second value. So the value is set as the column default, which also fills the
	 * existing row, the only one in this table. DDL has no parameters, hence the quoting.
	 */
	@Override
	void addInfo(Connection con, String key, String value, int schemaVersion) throws SQLException
	{
		String literal = value == null ? "NULL" : "'" + value.replace("'", "''") + "'";
		try (Statement st = con.createStatement())
		{
			st.execute("ALTER TABLE info ADD COLUMN " + key + " VARCHAR (50) DEFAULT " + literal);
		}
	}

	public void closeConnection(Connection con) throws IDMapperException
	{
		closeConnection(con, PROP_NONE);
	}

	/**
	 * Close the connection to this database.
	 * @param con JDBC Connection object
	 * @param props Passing PROP_FINALIZE for props will force a checkpoint before closing, so that
	 * all changes are flushed from the write-ahead log into the database file (necessary before the
	 * file is moved to its final location by {@link #finalizeNewDatabase}).
	 * @throws IDMapperException if the database could not be closed.
	 */
	public void closeConnection(Connection con, int props) throws IDMapperException
	{
		if(con != null)
		{
			if (props == PROP_FINALIZE)
			{
				try
				{
					// CHECKPOINT refuses to run while the current transaction still has
					// uncommitted local changes (e.g. from createGdbIndices(), which may run
					// after the caller's last explicit commit() but before autoCommit is
					// restored), so make sure nothing is left pending first.
					if (!con.getAutoCommit())
					{
						con.commit();
					}
					Statement sh = con.createStatement();
					sh.execute("CHECKPOINT");
					sh.close();
				}
				catch (SQLException se)
				{
					throw new IDMapperException (se);
				}
			}
			try
			{
				con.close();
				con = null;
			}
			catch (SQLException se)
			{
				throw new IDMapperException (se);
			}
		}
	}

}
