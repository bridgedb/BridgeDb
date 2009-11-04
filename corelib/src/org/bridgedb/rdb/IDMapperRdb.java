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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bridgedb.AttributeMapper;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/**
 * Interface for all classes that provide Gdb-like functionality,
 * such as looking up cross-references and backpage text.
 */
public abstract class IDMapperRdb implements IDMapper, AttributeMapper
{
	static
	{
		BridgeDb.register ("idmapper-pgdb", new DriverPgdb());
		BridgeDb.register ("idmapper-derbyclient", new DriverClient());
	}
	
	private static final class DriverPgdb implements org.bridgedb.Driver
	{
		/** private constructor to prevent instantiation. */
		private DriverPgdb() { } 
		
		/** {@inheritDoc} */
		public IDMapper connect(String location) throws IDMapperException 
		{
			return SimpleGdbFactory.createInstance(location, new DataDerby(), 0);
		}
	}
	
	private static final class DriverClient implements org.bridgedb.Driver
	{
		/** private constructor to prevent instantiation. */
		private DriverClient() { } 
		
		/** {@inheritDoc} */
		public IDMapper connect(String location) throws IDMapperException 
		{
			return SimpleGdbFactory.createInstance(location, new DBConnectorDerbyServer(), 0);
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
		final Map<Xref, Set<Xref>> result = new HashMap<Xref, Set<Xref>>();
		for (Xref src : srcXrefs)
		{
			final Set<Xref> refs = mapID(src, tgtDataSources);
			if (refs.size() > 0)
				result.put (src, refs);
		}
		return result;
	}	
}
