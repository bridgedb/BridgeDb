// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
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
package org.bridgedb;

import java.sql.Connection;
import java.sql.SQLException;

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
	 * Type for gene database
	 */
	public static final int TYPE_GDB = 0;
	/**
	 * Type for expression database
	 */
	public static final int TYPE_GEX = 1;

	public abstract Connection createConnection(String dbName, int props) throws DataException;	
	
	/**
	 * Close the given connection
	 * @param con The connection to be closed
	 * @throws Exception
	 */
	public void closeConnection(Connection con) throws DataException 
	{
		closeConnection(con, PROP_NONE);
	}
	
	/**
	 * Close the given connection, and optionally finalize it after creation (using {@link #PROP_FINALIZE})
	 * @param con The connection to be closed
	 * @param props Close properties (one of {@link #PROP_NONE}, {@link #PROP_FINALIZE} or {@link #PROP_RECREATE})
	 * @throws Exception
	 */
	public void closeConnection(Connection con, int props) throws DataException 
	{
		try
		{
			con.close();
		}
		catch (SQLException e)
		{
			throw new DataException (e);
		}
	}
	
	private int dbType;

	/**
	 * Set the database type (one of {@link #TYPE_GDB} or {@link #TYPE_GEX})
	 * @param type The type of the database that will be used for this class
	 */
	public void setDbType(int type) { dbType = type; }
	
	/**
	 * Get the database type (one of {@link #TYPE_GDB} or {@link #TYPE_GEX})
	 * return The type of the database that is used for this class
	 */
	public int getDbType() { return dbType; }
	

		
	/**
	 * This method is called to finalize the given database after creation
	 * (e.g. set read-only, archive files). The database name needs to returned, this
	 * may change when finalizing the database modifies the storage type (e.g. from directory
	 * to single file).
	 * The database connection needs to be closed before running this method.
	 * @param dbName The name of the database to finalize	
	 * @throws Exception
	 * @return The name of the finalized database
	 */
	public abstract String finalizeNewDatabase(String dbName) throws DataException;
		
	/**
	 * This method may be implemented when the database files need to be
	 * compacted or defragmented after creation of a new database. It will be called
	 * after all data is added to the database.
	 * @param con A connection to the database
	 * @throws SQLException
	 */
	public void compact(Connection con) throws DataException
	{
		//May be implemented by subclasses
	}
	

}
