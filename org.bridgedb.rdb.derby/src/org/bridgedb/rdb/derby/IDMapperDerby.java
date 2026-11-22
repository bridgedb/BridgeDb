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
package org.bridgedb.rdb.derby;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.bridgedb.BridgeDb;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.impl.InternalUtils;
import org.bridgedb.rdb.SimpleGdbFactory;

/**
 * Derby plugin for org.bridgedb.rdb. Loading this class registers the protocols
 * idmapper-pgdb and idmapper-derbyclient.
 * <p>
 * Loading org.bridgedb.rdb.IDMapperRdb also loads this class, if it is available.
 */
public final class IDMapperDerby
{
	// Must not initialize IDMapperRdb, as that class initializes this one.
	static
	{
		BridgeDb.register ("idmapper-pgdb", new DriverPgdb());
		BridgeDb.register ("idmapper-derbyclient", new DriverClient());
	}

	/** private constructor to prevent instantiation. */
	private IDMapperDerby() { }

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
}
