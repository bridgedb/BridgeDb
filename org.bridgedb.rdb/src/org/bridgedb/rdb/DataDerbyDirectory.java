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

import java.sql.DriverManager;
import java.sql.SQLException;

import org.bridgedb.IDMapperException;


//import org.pathvisio.debug.Logger;

/**
   Implementation of DBConnector using the Derby Driver,
   with the database stored as multiple files in a directory
*/
public class DataDerbyDirectory extends DataDerby
{	
	String lastDbName;
		
	public String finalizeNewDatabase(String dbName) throws IDMapperException
	{
		try
		{
			DriverManager.getConnection("jdbc:derby:" + FileUtilsGdb.removeExtension(dbName) + ";shutdown=true");
		}
		catch(SQLException e)
		{
			if (e.getSQLState().equals ("08006"))
			{
				// this exception is acutally expected, see
			    // http://db.apache.org/derby/docs/10.3/getstart/rwwdactivity3.html
//				Logger.log.info ("Database shudown cleanly");
			}
			else throw new IDMapperException (e);
		}
		return dbName;
	}	
}
