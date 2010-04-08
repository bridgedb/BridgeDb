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
package org.bridgedb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * combines multiple {@link IDMapper}'s in a stack.
 * <p>
 * The behavior of the {@link IDMapper} interface implementations
 * differs per method:
 * if the method returns a single result, usually it is 
 * from the first child database that has a sensible result.
 * This also means that the child databases have a definitive
 * ordering: the first one shadows the second one for some results.
 * <p>
 * If the method returns a list, IDMapperStack joins
 * the result from all connected child databases together.
 */
public class IDMapperStack implements IDMapper, AttributeMapper
{
	private List<IDMapper> gdbs = new ArrayList<IDMapper>();

	/**
	 * Create a fresh IDMapper from a connectionString and add it to the stack.
	 * @param connectionString connectionString for configuring the new IDMapper
	 * @return the newly created IDMapper
	 * @throws IDMapperException when the connection failed.
	 */
	public IDMapper addIDMapper(String connectionString) throws IDMapperException
	{
		IDMapper idMapper = BridgeDb.connect(connectionString);
		addIDMapper(idMapper);
		return idMapper;
	}

	/**
	 * Add an existing IDMapper to the stack.
	 * @param idMapper IDMapper to be added.
	 */
    public void addIDMapper(IDMapper idMapper)
    {
        if (idMapper!=null) {
            gdbs.add(idMapper);
        }
    }
    
    private boolean isTransitive = false;
    
    /**
     * Set Transitivity mode, where all mappings are combined to infer
     * second degree mappings.
     * @param value true or false
     */
    public void setTransitive(boolean value)
    {
    	isTransitive = value;
    }
    
    /**
     * @return true if the stack is in transitive mode
     */
    public boolean getTransitive()
    {
    	return isTransitive;
    }
    
	/**
	 * Remove an idMapper from the stack.
	 * @param idMapper IDMapper to be removed.
	 */
    public void removeIDMapper(IDMapper idMapper)
    {
    	gdbs.remove(idMapper);
    }

	/**
	 * closes all child databases. 
	 * @throws IDMapperException when closing failed for one of the databases. It will still try to 
	 * 	close all child databases even if one throws an exception. However, only the last exception will be thrown.
	 */
	public void close() throws IDMapperException 
	{
		IDMapperException postponed = null;
		for (IDMapper child : gdbs)
		{
			if (child != null)
			{
				try
				{
					child.close();
					child = null; // garbage collect
				}
				catch (IDMapperException ex)
				{
					postponed = ex;
				}
			}
		}
		if (postponed != null)
		{
			throw postponed;
		}
	}

	/** {@inheritDoc} */
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
	 * @return true if at least one of the child services
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

	private final IDMapperCapabilities caps = new IDMapperStackCapabilities();
	
	private class IDMapperStackCapabilities implements IDMapperCapabilities
	{
		/**
		 * @return union of DataSources supported by child services
		 * @throws IDMapperException when one of the services was unavailable
		 */
		public Set<DataSource> getSupportedSrcDataSources() throws IDMapperException
		{
			final Set<DataSource> result = new HashSet<DataSource>();
			for (IDMapper idm : IDMapperStack.this.gdbs)
			{
                Set<DataSource> dss = null;
                dss = idm.getCapabilities().getSupportedSrcDataSources();
                if (dss!=null) {
                    result.addAll (dss);
                }
			}
			return result;
		}

		/**
		 * @return union of DataSources supported by child services
		 * @throws IDMapperException when one of the services was unavailable
		 */
		public Set<DataSource> getSupportedTgtDataSources() throws IDMapperException
		{
			final Set<DataSource> result = new HashSet<DataSource>();
			for (IDMapper idm : IDMapperStack.this.gdbs)
			{
				Set<DataSource> dss = null;
                dss = idm.getCapabilities().getSupportedTgtDataSources();

                if (dss!=null) {
                    result.addAll (dss);
                }
			}
			return result;
		}

                /** {@inheritDoc} */
                public boolean isMappingSupported(DataSource src, DataSource tgt)
                                throws IDMapperException {
                    for (IDMapper idm : IDMapperStack.this.gdbs)
                    {
                        if (idm.getCapabilities().isMappingSupported(src, tgt)) {
                            return true;
                        }
                    }
                    return false;
                }

		/**
		 * @return true if free search is supported by one of the children
		 */
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

		/** {@inheritDoc} */
		public Set<String> getKeys() 
		{
			return Collections.emptySet();
		}

		/** {@inheritDoc} */
		public String getProperty(String key) 
		{
			return null;
		}
	};
	
	/**
	 * @return an object describing the capabilities of the combined stack of services.
	 */
	public IDMapperCapabilities getCapabilities() 
	{
		return caps;
	}

	/** {@inheritDoc} */
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

	/** {@inheritDoc} */
	public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs,
			DataSource... tgtDataSources) throws IDMapperException 
	{
		if (isTransitive)
		{
			return mapIDtransitive(srcXrefs, tgtDataSources);
		}
		else
		{
			return mapIDnormal(srcXrefs, tgtDataSources);
		}
	}

	/**
	 * helper method to map Id's in non-transitive mode.
	 * @param srcXrefs mapping source
	 * @param tgtDataSources target data sources
	 * @return mapping result
	 * @throws IDMapperException if one of the children fail
	 */
	private Map<Xref, Set<Xref>> mapIDnormal(Collection<Xref> srcXrefs,
			DataSource... tgtDataSources) throws IDMapperException 
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

	/**
	 * helper method to map Id's in transitive mode.
	 * @param srcXrefs mapping source
	 * @param tgtDataSources target data sources
	 * @return mapping result
	 * @throws IDMapperException if one of the children fail
	 */
	private Map<Xref, Set<Xref>> mapIDtransitive(Collection<Xref> srcXrefs,
			DataSource... tgtDataSources) throws IDMapperException 
	{
		// Current implementation just repeatedly calls mapIDTransitive (Xref, Set<Ds>) 
		// It may be possible to rearrange loops to optimize for fewer database calls.
		Map <Xref, Set<Xref>> result = new HashMap<Xref, Set<Xref>>();
		for (Xref ref: srcXrefs)
		{
			result.put (ref, mapIDtransitive(ref, tgtDataSources));
		}
		return result;
	}

	/** {@inheritDoc} */
	public Set<String> getAttributes(Xref ref, String attrname)
			throws IDMapperException 
	{
		Set<String> result = new HashSet<String>();
		for (IDMapper child : gdbs)
		{
			if (child != null && child instanceof AttributeMapper && child.isConnected())
			{
				result.addAll (((AttributeMapper)child).getAttributes(ref, attrname));
			}
		}
		return result;
	}

	/**
	 * @return true if free attribute search is supported by one of the children
	 */
	public boolean isFreeAttributeSearchSupported()
	{
		// returns true if any returns true
		// TODO: not sure if this is the right logic?
		for (IDMapper child : IDMapperStack.this.gdbs)
		{
		        if (child != null && child instanceof AttributeMapper)
		        {
		        	if (((AttributeMapper)child).isFreeAttributeSearchSupported())
					return true;
		        }
		}
		return false;
	}

	/** {@inheritDoc} */
	public Map<Xref, String> freeAttributeSearch (String query, String attrType, int limit) throws IDMapperException
	{
		Map<Xref, String> result = null;
		for (IDMapper child : gdbs)
		{
			if (child != null && child instanceof AttributeMapper && child.isConnected()
				&& ((AttributeMapper)child).isFreeAttributeSearchSupported())
			{
				Map<Xref, String> childResult = 
					((AttributeMapper)child).freeAttributeSearch(query, attrType, limit);
				if (result == null) 
					result = childResult;
				else
				{
					for (Xref ref : childResult.keySet())
					{
						if (!result.containsKey(ref))
							result.put (ref, childResult.get(ref));
					}
				}
			}
		}
		return result;
	}
	
	/** @return concatenation of toString of each child */
	@Override public String toString()
	{
		String result = "";
		boolean first = true;
		for (IDMapper child : gdbs)
		{
			if (!first) result += ", "; 
			first = false;
			result += child.toString();
		}
		return result;
	}
	
	/** @return number of child databases */
	public int getSize()
	{
		return gdbs.size();
	}
	
	/**
	 * @param index in the range 0 <= index < getSize() 
	 * @return the IDMapper at the given position */
	public IDMapper getIDMapperAt(int index)
	{
		return gdbs.get(index);
	}

	/** {@inheritDoc} */
	public Set<Xref> mapID(Xref ref, DataSource... resultDs) throws IDMapperException 
	{
		if (isTransitive)
		{
			return mapIDtransitive (ref, resultDs);
		}
		else
		{
			return mapIDnormal (ref, resultDs);
		}
	}
	
	
	/**
	 * helper method to map Id's in transitive mode.
	 * @param ref Xref to map
	 * @param resultDs target data sources
	 * @return mapping result
	 * @throws IDMapperException if one of the children fail
	 */
	private Set<Xref> mapIDtransitive(Xref ref, DataSource... resultDs) throws IDMapperException 
	{
		// first round
		Set<Xref> result = new HashSet<Xref>();
		Set<DataSource> dsFilter = new HashSet<DataSource>(Arrays.asList(resultDs));
		for (IDMapper i : gdbs)
		{
			if (i == null || !i.isConnected()) continue;
			
			// map ref in IDMapper i
			Set<Xref> round1 = i.mapID(ref);

			for (Xref r1 : round1)
			{
				if (resultDs.length == 0 || dsFilter.contains(r1.getDataSource()))
				{
					result.add (r1);
				}
			}

			// then map the result of that in all IDMappers j (j != i)
			for (IDMapper j : gdbs)
			{
				if (j == null || !j.isConnected()) continue;
				if (i == j) continue;
				
				Map<Xref, Set<Xref>> round2 = j.mapID(round1, resultDs); 
				for (Xref key : round2.keySet())
				{
					result.addAll (round2.get (key));
				}
			}			
		}
		return result;
	}
	
	/**
	 * helper method to map Id's in transitive mode.
	 * @param ref Xref to map
	 * @param resultDs target data sources
	 * @return mapping result
	 * @throws IDMapperException if one of the children fail
	 */
	private Set<Xref> mapIDnormal(Xref ref, DataSource... resultDs) throws IDMapperException 
	{
		Set<Xref> result = new HashSet<Xref>();
		for (IDMapper child : gdbs)
		{
			if (child != null && child.isConnected())
			{
				result.addAll (child.mapID(ref, resultDs));
			}
		}
		return result;
	}

	/** {@inheritDoc} */
	public Set<String> getAttributeSet() throws IDMapperException 
	{
		Set<String> result = new HashSet<String>();
		for (IDMapper child : gdbs)
		{
			if (child != null && child instanceof AttributeMapper && child.isConnected())
			{
				result.addAll (((AttributeMapper)child).getAttributeSet());
			}
		}
		return result;
	}

	/** {@inheritDoc} */
	public Map<String, Set<String>> getAttributes(Xref ref)
			throws IDMapperException 
	{
		Map<String, Set<String>> result = new HashMap<String, Set<String>>();
		for (IDMapper child : gdbs)
		{
			if (child != null && child instanceof AttributeMapper && child.isConnected())
			{
				for (Map.Entry<String, Set<String>> entry :
					((AttributeMapper)child).getAttributes(ref).entrySet())
				{
					Set<String> thisSet;
					if (!result.containsKey(entry.getKey()))	
					{
						thisSet = new HashSet<String>();
						result.put (entry.getKey(), thisSet); 
					}
					else
					{
						thisSet = result.get(entry.getKey());
					}
					thisSet.addAll(entry.getValue());
				}
			}
		}
		return result;
	}
	
}
