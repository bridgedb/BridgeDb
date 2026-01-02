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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.bridgedb.AttributeMapper;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.XrefIterator;
import org.bridgedb.impl.InternalUtils;

/**
 * Interface for all classes that provide Gdb-like functionality,
 * such as looking up cross-references and backpage text.
 */
public abstract class IDMapperRdb implements IDMapper, AttributeMapper, XrefIterator
{
	static
	{
		BridgeDb.register ("idmapper-pgdb", new DriverPgdb());
		BridgeDb.register ("idmapper-derbyclient", new DriverClient());
		BridgeDb.register ("idmapper-jdbc", new DriverJdbc());
	}
	
	private static final class DriverPgdb implements org.bridgedb.Driver
	{
		/** private constructor to prevent instantiation. */
		private DriverPgdb() { } 
		
		/** {@inheritDoc} */
		public IDMapper connect(String location) throws IDMapperException 
		{
			String url = "jdbc:derby:jar:(" + location + ")database";
			return SimpleGdbFactory.createInstance(location, url);
		}
	}

	private static final class DriverJdbc implements org.bridgedb.Driver
	{
		/** private constructor to prevent instantiation. */
		private DriverJdbc() { } 
		
		/** {@inheritDoc} */
		public IDMapper connect(String location) throws IDMapperException 
		{
			String url = "jdbc:" + location;
			return SimpleGdbFactory.createInstance(location, url);
		}
	}

	private static final class DriverClient implements org.bridgedb.Driver
	{
		/** private constructor to prevent instantiation. */
		private DriverClient() { } 
		
		/** {@inheritDoc} */
		public IDMapper connect(String location) throws IDMapperException 
		{
			try
			{
	            Map<String, String> args = 
	            	InternalUtils.parseLocation(location, "host", "port");

	            if (!args.containsKey("BASE")) 
	            	throw new IllegalArgumentException("Expected species name in connection string: " + location);

	            String host = args.containsKey("host") ? args.get("host") : "wikipathways.org";
	            String port = args.containsKey("port") ? args.get("port") : "1527";

	            try {
	    			Class.forName("org.apache.derby.jdbc.ClientDriver"); // Derby 10.4
	    			System.out.println("Derby ClientDriver loaded (for Derby 10.4)");
	            } catch (ClassNotFoundException e) {
	            	// ignore, probably we're running this with Derby 10.14 or higher
	            }
	            try {
	            	Class.forName("org.apache.derby.jdbc.EmbeddedDriver"); // Derby 10.14 or higher
	            	System.out.println("Derby ClientDriver loaded (for Derby 10.14+)");
	            } catch (ClassNotFoundException e) {
	            	// ignore, probably we're running this with Derby lower than 10.14
	            }

	            Properties sysprop = System.getProperties();
				sysprop.setProperty("derby.storage.tempDirectory", System.getProperty("java.io.tmpdir"));
				sysprop.setProperty("derby.stream.error.file", File.createTempFile("derby",".log").toString());
				
				String url = "jdbc:derby://" + host + ":" + port + "/" + args.get("BASE");
				return SimpleGdbFactory.createInstance(location, url);
			}
			catch (IOException e)
			{
				throw new IDMapperException (e);
			}
		}
	}
	
	/**
	 * Gets the name of te currently used gene database.
	 * @return the database name as specified in the connection string
	 */
	public abstract String getDbName();

	/** @return the database name, i.e. the full path to the pgdb. */
	@Override public String toString() { return getDbName(); } 
	
	/** {@inheritDoc} */
	public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources) throws IDMapperException 
	{
		return InternalUtils.mapMultiFromSingle(this, srcXrefs, tgtDataSources);
	}
}
