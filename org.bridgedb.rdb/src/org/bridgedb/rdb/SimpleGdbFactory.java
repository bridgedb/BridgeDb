// BridgeDb,
// An abstraction layer for identifer mapping services, both local and online.
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
import java.sql.ResultSet;
import java.sql.SQLException;

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
	 * @param dbName The file containing the Gene Database. 
	 * @param con An SQL Connection to the database
	 * @param props PROP_RECREATE if you want to create a new database, overwriting any existing ones. Otherwise, PROP_NONE.
	 * @return a new Gdb
	 * @throws IDMapperException on failure
	*/
	public static SimpleGdb createInstance(String dbName, Connection con) throws IDMapperException
	{
		if(dbName == null) throw new NullPointerException();	

		int version = 0;
		try 
		{
			ResultSet r = con.createStatement().executeQuery("SELECT schemaversion FROM info");
			if(r.next()) version = r.getInt(1);
		} 
		catch (SQLException e) 
		{
			//Ignore, older db's don't even have schema version
		}			
		
		switch (version)
		{
		case 2:
			return new SimpleGdbImpl2(dbName, con);
		case 3:
			return new SimpleGdbImpl3(dbName, con);
		//NB add future schema versions here
		default:
			throw new IDMapperException ("Unrecognized schema version '" + version + "', please make sure you have the latest " +
					"version of this software and databases");
		}		
	}
}
