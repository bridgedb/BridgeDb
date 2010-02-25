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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/** {@inheritDoc} */
class SimpleGdbImpl2 extends SimpleGdbImplCommon
{		
	private static final int GDB_COMPAT_VERSION = 2; //Preferred schema version
	
	private final SimpleGdb.QueryLifeCycle qBackpage = new SimpleGdb.QueryLifeCycle(
			"SELECT backpageText FROM datanode " +
			" WHERE id = ? AND code = ?"
		);

	/** 
	 * get Backpage info. In Schema v2, this was not stored in 
	 * the attribute table but as a separate column, so this is treated
	 * as a special case. This method is called by <pre>getAttribute (ref, "Backpage")</pre>
	 * @param ref the entity to get backpage info for.
	 * @return Backpage info as string
	 * @throws IDMapperException when database is unavailable
	 */
	private String getBpInfo(Xref ref) throws IDMapperException 
	{
		final QueryLifeCycle pst = qBackpage;
		synchronized (pst)
		{
			try {
				pst.init();
				pst.setString (1, ref.getId());
				pst.setString (2, ref.getDataSource().getSystemCode());
				ResultSet r = pst.executeQuery();
				String result = null;
				if (r.next())
				{
					result = r.getString(1);
				}
				return result;
			} catch	(SQLException e) { throw new IDMapperException (e); } //Gene not found
			finally {pst.cleanup(); }
		}
	}

	/**
	 * Opens a connection to the Gene Database located in the given file.
	 * A new instance of this class is created automatically.
	 * @param dbName The file containing the Gene Database. 
	 * @param con An existing SQL Connector.
	 * @param props PROP_RECREATE if you want to create a new database (possibly overwriting an existing one) 
	 * 	or PROP_NONE if you want to connect read-only
	 * @throws IDMapperException when the database could not be created or connected to
	 */
	public SimpleGdbImpl2(String dbName, String connectionString) throws IDMapperException
	{
		super (dbName, connectionString);
		
		if(dbName == null) throw new NullPointerException();		
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
	
	private static final Map<String, String> ATTRIBUTES_FROM_BACKPAGE;
	
	static
	{
		ATTRIBUTES_FROM_BACKPAGE = new HashMap<String, String>();
		ATTRIBUTES_FROM_BACKPAGE.put ("Chromosome", "<TH>Chr:<TH>([^<]*)<");
		ATTRIBUTES_FROM_BACKPAGE.put ("Description", "<TH>Description:<TH>([^<]*)<");
		ATTRIBUTES_FROM_BACKPAGE.put ("Synonyms", "<TH>Synonyms:<TH>([^<]*)<");
		ATTRIBUTES_FROM_BACKPAGE.put ("Symbol", "<TH>(?:Gene Symbol|Metabolite):<TH>([^<]*)<");
		ATTRIBUTES_FROM_BACKPAGE.put ("BrutoFormula", "<TH>Bruto Formula:<TH>([^<]*)<");
	}

	/** {@inheritDoc} */
	public Set<String> getAttributes(Xref ref, String attrname)
			throws IDMapperException 
	{
		Set<String> result = new HashSet<String>();
		final QueryLifeCycle pst = qAttribute;
		
		if (ATTRIBUTES_FROM_BACKPAGE.containsKey(attrname))
		{
			String bpInfo = getBpInfo(ref);
			if (bpInfo != null)
			{
				Pattern pat = Pattern.compile(ATTRIBUTES_FROM_BACKPAGE.get (attrname));
				Matcher matcher = pat.matcher(bpInfo);
				if (matcher.find())
				{
					result.add (matcher.group(1));
				}
			}
		}
		
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
			} catch	(SQLException e) { throw new IDMapperException ("Xref:" + ref + ", Attribute: " + attrname, e); } // Database unavailable
			finally {pst.cleanup(); }
		}
	}

	/** {@inheritDoc} */
	public Map<String, Set<String>> getAttributes(Xref ref)
			throws IDMapperException 
	{
		Map<String, Set<String>> result = new HashMap<String, Set<String>>();
		final QueryLifeCycle pst = qAllAttributes;
				
		String bpInfo = getBpInfo(ref);
		if (bpInfo != null)
		{
			for (String attrname : ATTRIBUTES_FROM_BACKPAGE.keySet())
			{
				Pattern pat = Pattern.compile(ATTRIBUTES_FROM_BACKPAGE.get (attrname));
				Matcher matcher = pat.matcher(bpInfo);
				if (matcher.find())
				{
					Set<String> attrSet = new HashSet<String>();
					attrSet.add (matcher.group(1));
					result.put (attrname, attrSet);
				}
			}
		}
		
		synchronized (pst)
		{
			try {
				pst.init();
				pst.setString (1, ref.getId());
				pst.setString (2, ref.getDataSource().getSystemCode());
				ResultSet r = pst.executeQuery();
				if (r.next())
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
