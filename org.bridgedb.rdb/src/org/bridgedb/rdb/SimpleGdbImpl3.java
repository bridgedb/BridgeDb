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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/** {@inheritDoc} */
class SimpleGdbImpl3 extends SimpleGdbImplCommon
{		
	private static final int GDB_COMPAT_VERSION = 3; //Preferred schema version

	/**
	 * Opens a connection to the Gene Database located in the given file.
	 * A new instance of this class is created automatically.
	 * @param dbName The file containing the Gene Database. 
	 * @param con An existing java SQL connection
	 * @param props PROP_RECREATE if you want to create a new database (possibly overwriting an existing one) 
	 * 	or PROP_NONE if you want to connect read-only
	 * @throws IDMapperException when the database could not be created or connected to
	 */
	public SimpleGdbImpl3(String dbName, String connectionString) throws IDMapperException
	{
		super(dbName, connectionString);
		checkSchemaVersion();
	}
	
	/**
	 * look at the info table of the current database to determine the schema version.
	 * @throws IDMapperException when looking up the schema version failed
	 */
	private void checkSchemaVersion() throws IDMapperException 
	{
		int version = 0;
		try 
		{
			ResultSet r = getConnection().createStatement().executeQuery("SELECT schemaversion FROM info");
			if(r.next()) version = r.getInt(1);
		} 
		catch (SQLException e) 
		{
			//Ignore, older db's don't even have schema version
		}
		if(version != GDB_COMPAT_VERSION) 
		{
			throw new IDMapperException ("Implementation and schema version mismatch");
		}
	}

	/** {@inheritDoc} */
	public Set<String> getAttributes(Xref ref, String attrname)
			throws IDMapperException 
	{
		Set<String> result = new HashSet<String>();
		final QueryLifeCycle pst = qAttribute;
		synchronized (pst)
		{
			try {
				pst.init();
				pst.setString (1, ref.getId());
				pst.setString (2, ref.getDataSource().getSystemCode());
				pst.setString (3, attrname);
				ResultSet r = pst.executeQuery();
				if (r.next())
				{
					result.add (r.getString(1));
				}
				return result;
			} catch	(SQLException e) { throw new IDMapperException (e); } // Database unavailable
			finally {pst.cleanup(); }
		}
	}

	/** {@inheritDoc} */
	public Map<String, Set<String>> getAttributes(Xref ref)
			throws IDMapperException 
	{
		Map<String, Set<String>> result = new HashMap<String, Set<String>>();				
		final QueryLifeCycle pst = qAllAttributes;
		synchronized (pst)
		{
			try {
				pst.init();
				pst.setString (1, ref.getId());
				pst.setString (2, ref.getDataSource().getSystemCode());
				ResultSet r = pst.executeQuery();
				while (r.next())
				{
					String key = r.getString(1);
					String value = r.getString(2);
					if (result.containsKey (key))
					{
						result.get(key).add (value);
					}
					else
					{
						Set<String> valueSet = new HashSet<String>();
						valueSet.add (value);
						result.put (key, valueSet);
					}
				}
				return result;
			} catch	(SQLException e) { throw new IDMapperException ("Xref:" + ref, e); } // Database unavailable
			finally {pst.cleanup(); }
		}
	}
}
