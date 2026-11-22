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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bridgedb.IDMapperException;


/**
 * DBConnector is used by SimpleGex and SimpleGdb
 * to perform operations
 * such as creating a new database
 * or establishing a connection
 * that are different for Derby, Hsqldb, etc.
 * 
 * This class implements only non-GUI functionality.
 * There is a derived DBConnSwt class that also 
 * includes dialogs to set the parameters
 * for opening / creating.
 */
public abstract class DBConnector 
{
	public static final int PROP_NONE = 0;
	public static final int PROP_RECREATE = 4;
	public static final int PROP_FINALIZE = 8;
	
	/**
	 * Type for gene database.
	 */
	public static final int TYPE_GDB = 0;
	/**
	 * Type for expression database.
	 */
	public static final int TYPE_GEX = 1;

	public abstract Connection createConnection(String dbName, int props) throws IDMapperException;	
	
	/**
	 * Close the given connection.
	 * @param con The connection to be closed
	 * @throws IDMapperException when there was a database error
	 */
	public void closeConnection(Connection con) throws IDMapperException 
	{
		closeConnection(con, PROP_NONE);
	}
	
	/**
	 * Close the given connection, and optionally finalize it after creation (using {@link #PROP_FINALIZE}).
	 * @param con The connection to be closed
	 * @param props Close properties (one of {@link #PROP_NONE}, {@link #PROP_FINALIZE} or {@link #PROP_RECREATE})
	 * @throws IDMapperException when there was a database error
	 */
	public void closeConnection(Connection con, int props) throws IDMapperException 
	{
		try
		{
			con.close();
		}
		catch (SQLException e)
		{
			throw new IDMapperException (e);
		}
	}
	
	/**
	 * Add a column with the given value to the info table.
	 * @param con A connection to the database
	 * @param key The name of the column, already validated
	 * @param value The value for the column
	 * @param schemaVersion The schema version of the row to set the value for
	 * @throws SQLException when there was a database error
	 */
	void addInfo(Connection con, String key, String value, int schemaVersion) throws SQLException
	{
		try (PreparedStatement alter = con.prepareStatement(
				"ALTER TABLE info ADD COLUMN " + key + " VARCHAR (50)"))
		{
			alter.execute();
		}
		try (PreparedStatement update = con.prepareStatement(
				"UPDATE info SET " + key + " = ? WHERE schemaversion = " + schemaVersion))
		{
			update.setString(1, value);
			update.execute();
		}
	}

	/**
	 * Inserts rows into a table with a primary key. A refused row must not affect the others.
	 * By default, it is left to the database to refuse rows.
	 */
	static class UniqueKeys
	{
		/**
		 * @param pst The INSERT statement, with all parameters set
		 * @param key The values of the primary key
		 * @throws SQLException when the row was not inserted, for example as it is a duplicate
		 */
		void insert(PreparedStatement pst, String... key) throws SQLException
		{
			pst.executeUpdate();
		}
	}

	/**
	 * @param con A connection to the database
	 * @param table The table to insert into
	 * @param columns The columns of the primary key
	 * @return The object to insert rows into the table with
	 * @throws SQLException when there was a database error
	 */
	UniqueKeys getUniqueKeys(Connection con, String table, String... columns) throws SQLException
	{
		return new UniqueKeys();
	}

	/**
	 * Commit the current transaction.
	 * @param con A connection to the database
	 * @throws SQLException when the data was not committed
	 */
	void commit(Connection con) throws SQLException
	{
		con.commit();
	}

	private int dbType;

	/**
	 * Set the database type (one of {@link #TYPE_GDB} or {@link #TYPE_GEX}).
	 * @param type The type of the database that will be used for this class
	 */
	public void setDbType(int type) { dbType = type; }
	
	/**
	 * Get the database type (one of {@link #TYPE_GDB} or {@link #TYPE_GEX}).
	 * @return The type of the database that is used for this class
	 */
	public int getDbType() { return dbType; }
	
	/**
	 * This method is called to finalize the given database after creation
	 * (e.g. set read-only, archive files). The database name needs to returned, this
	 * may change when finalizing the database modifies the storage type (e.g. from directory
	 * to single file).
	 * The database connection needs to be closed before running this method.
	 * @param dbName The name of the database to finalize	
	 * @return The name of the finalized database
	 * @throws IDMapperException when there was a database error
	 */
	public abstract String finalizeNewDatabase(String dbName) throws IDMapperException;
		
	/**
	 * This method may be implemented when the database files need to be
	 * compacted or defragmented after creation of a new database. It will be called
	 * after all data is added to the database.
	 * @param con A connection to the database
	 * @throws IDMapperException when there was a database error
	 */
	public void compact(Connection con) throws IDMapperException
	{
		//May be implemented by subclasses
	}
	

}
