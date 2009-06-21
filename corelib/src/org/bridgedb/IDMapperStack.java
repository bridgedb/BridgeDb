// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
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
package org.bridgedb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * combines multiple {@link IDMapperRdb}'s in a stack.
 * <p>
 * The behavior of the {@link IDMapperRdb} interface implementations
 * differs per method:
 * if the method returns a single result, usually it is 
 * from the first child database that has a sensible result.
 * This also means that the child databases have a definitive
 * ordering: the first one shadows the second one for some results.
 * <p>
 * If the method returns a list, DoubleGdb joins
 * the result from all connected child databases together.
 */
public class IDMapperStack implements IDMapper
{
	private List<IDMapper> gdbs = new ArrayList<IDMapper>();

	public void addIDMapper(String connectionString) throws IDMapperException
	{
		IDMapper idMapper = BridgeDb.connect(connectionString);
		gdbs.add(idMapper);
	}

	/**
	 * closes all child databases. 
	 */
	public void close() throws IDMapperException 
	{
		for (IDMapper child : gdbs)
		{
			if (child != null)
			{
				child.close();
				child = null; // garbage collect
			}
		}
	}

	/**
	 * Check if the reference exists in either one of the 
	 * child databases
	 * @throws IDMapperException 
	 */
	public boolean xrefExists(Xref xref) throws IDMapperException 
	{
		for (IDMapper child : gdbs)
		{
			if (child != null && child.isConnected())
			{
				if(child.xrefExists(xref)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns true if at least one of the child databases
	 * are connected.
	 */
	public boolean isConnected() 
	{
		for (IDMapper child : gdbs)
		{
			if (child != null && child.isConnected())
			{
				return true;
			}
		}
		return false;
	}

	private final IDMapperCapabilities caps = new IDMapperCapabilities()
	{
		public Set<DataSource> getSupportedSrcDataSources() 
		{
			final Set<DataSource> result = new HashSet<DataSource>();
			for (IDMapper idm : IDMapperStack.this.gdbs)
			{
				result.addAll (idm.getCapabilities().getSupportedSrcDataSources());
			}
			return result;
		}

		public Set<DataSource> getSupportedTgtDataSources() 
		{
			final Set<DataSource> result = new HashSet<DataSource>();
			for (IDMapper idm : IDMapperStack.this.gdbs)
			{
				result.addAll (idm.getCapabilities().getSupportedTgtDataSources());
			}
			return result;
		}

		public boolean isFreeSearchSupported() 
		{
			// returns true if any returns true
			// TODO: not sure if this is the right logic?
			for (IDMapper idm : IDMapperStack.this.gdbs)
			{
				if (idm.getCapabilities().isFreeSearchSupported())
					return true;
			}
			return false;
		}
	};
	
	public IDMapperCapabilities getCapabilities() 
	{
		return caps;
	}

	public Set<Xref> freeSearch(String text, int limit)
			throws IDMapperException 
	{
		Set<Xref> result = new HashSet<Xref>();
		
		for (IDMapper child : gdbs)
		{
			if (child != null && child.isConnected())
			{
				result.addAll (child.freeSearch(text, limit));
			}
		}
		return result;
	}

	public Map<Xref, Set<Xref>> mapID(Set<Xref> srcXrefs,
			Set<DataSource> tgtDataSources) throws IDMapperException 
	{
		Map<Xref, Set<Xref>> result = new HashMap<Xref, Set<Xref>>();
		
		for (IDMapper child : gdbs)
		{
			if (child != null && child.isConnected())
			{
				for (Map.Entry<Xref, Set<Xref>> entry : child.mapID(srcXrefs, tgtDataSources).entrySet())
				{
					Set<Xref> resultSet = result.get (entry.getKey());
					if (resultSet == null) 
					{
						resultSet = new HashSet<Xref>();
						result.put (entry.getKey(), resultSet);
					}
					resultSet.addAll (entry.getValue());
				}
			}
		}
		return result;
	}
}
