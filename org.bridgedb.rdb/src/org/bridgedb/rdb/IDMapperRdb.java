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

import java.util.Collection;
import java.util.Map;
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
		// BridgeDb.register() is not thread-safe, so let the plugins finish registering first
		try
		{
			// optional plugin, loaded by name to not depend on it
			Class.forName("org.bridgedb.rdb.derby.IDMapperDerby");
		}
		catch (ClassNotFoundException e)
		{
			// the Derby plugin is not used, so there is no idmapper-pgdb or idmapper-derbyclient
		}
		try
		{
			// optional plugin, loaded by name to not depend on it
			Class.forName("org.bridgedb.rdb.duckdb.IDMapperDuckDB");
		}
		catch (ClassNotFoundException e)
		{
			// the DuckDB plugin is not used, so there is no idmapper-duckdb
		}
		BridgeDb.register ("idmapper-jdbc", new DriverJdbc());
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
