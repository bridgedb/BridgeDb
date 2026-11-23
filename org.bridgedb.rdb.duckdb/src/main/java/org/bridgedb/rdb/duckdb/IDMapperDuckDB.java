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
package org.bridgedb.rdb.duckdb;

import org.bridgedb.BridgeDb;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.rdb.SimpleGdbFactory;

/**
 * DuckDB plugin for org.bridgedb.rdb. Loading this class registers the protocol idmapper-duckdb.
 * <p>
 * Loading org.bridgedb.rdb.IDMapperRdb also loads this class, if it is available.
 */
public final class IDMapperDuckDB
{
	private static final String DRIVER = "org.duckdb.DuckDBDriver";

	// Must not initialize IDMapperRdb, as that class initializes this one.
	static
	{
		try
		{
			// not found automatically in OSGi, nor in fat jars with several JDBC drivers
			Class.forName(DRIVER);
		}
		catch (ClassNotFoundException e)
		{
			// must not break org.bridgedb.rdb, so this is only reported when connecting
		}
		BridgeDb.register ("idmapper-duckdb", new DriverDuckDB());
	}

	/** private constructor to prevent instantiation. */
	private IDMapperDuckDB() { }

	private static final class DriverDuckDB implements org.bridgedb.Driver
	{
		/** private constructor to prevent instantiation. */
		private DriverDuckDB() { }

		/** {@inheritDoc} */
		public IDMapper connect(String location) throws IDMapperException
		{
			try
			{
				Class.forName(DRIVER);
			}
			catch (ClassNotFoundException e)
			{
				throw new IDMapperException(
					"Could not find the DuckDB JDBC driver (" + DRIVER + ") on the classpath", e);
			}
			return SimpleGdbFactory.createInstance(location, "jdbc:duckdb:" + location);
		}
	}
}
