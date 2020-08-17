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
package org.bridgedb.rdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bridgedb.IDMapperException;

/**
 * Factory class that instantiates the correct SimpleGdb implementation
 * for a new / existing gdb,
 * based on the schema version.
 * Use this class for creating / connecting to any SimpleGdb database.
 */
public final class SimpleGdbFactory 
{
	/** private constructor prevents instantiation. */
	private SimpleGdbFactory() {};
	
	/**
	 * Opens a connection to the Gene Database located in the given file.
	 * <p>
	 * Use this instead of constructor to create an instance of SimpleGdb that matches the schema version.
	 * @param connectionString a JDBC Connection string 
	 * @return a new Gdb
	 * @throws IDMapperException on failure
	*/
	public static SimpleGdb createInstance(String dbName, String connectionString) throws IDMapperException
	{
		if(connectionString == null) throw new NullPointerException();	

		int version = 0;
		Connection con = null;
		ResultSet r = null;
		Statement stmt = null;
		
		try
		{
			try 
			{
				con = DriverManager.getConnection(connectionString);
				stmt = con.createStatement();
			} 
			catch (SQLException e) 
			{
				throw new IDMapperException("Could not connect to database: " + e.getMessage(), e);
			}
			try 
			{
				r = stmt.executeQuery("SELECT schemaversion FROM info");
				if(r.next()) version = r.getInt(1);
			} 
			catch (SQLException e) 
			{
				throw new IDMapperException("Database schema error, info table or schemaversion column missing: " + e.getMessage(), e);
			}
		}
		finally
		{
			if (r != null) try { r.close(); } catch (SQLException ignore) {}
			if (stmt != null) try { stmt.close(); } catch (SQLException ignore) {}
			if (con != null) try { con.close(); } catch (SQLException ignore) {}
		}

		switch (version)
		{
		case 2:
			return new SimpleGdbImpl2(dbName, connectionString);
		case 3:
			return new SimpleGdbImpl3(dbName, connectionString);
		//NB add future schema versions here
		case 4:
			throw new IDMapperException ("Schema version 4 requires BridgeDb 3.0 or higher");
		default:
			throw new IDMapperException ("Unrecognized schema version '" + version + "', please make sure you have the latest " +
					"version of this software and databases");
		}		
	}
}
