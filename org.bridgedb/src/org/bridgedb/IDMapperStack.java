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
package org.bridgedb;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bridgedb.impl.TransitiveGraph;

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
 * <p>
 * Transitive maps are deduced from the IDMappers, added to this 
 * IDMapperStack. Transitive maps are enabled as soon as the transitivity 
 * is set to be true.
 * <p>  
 * In order to calculate the deduced transitive mappings, we build
 * a graph that represents the possible mappings supported by
 * the IDMappers in this IDMapperStack. The nodes of this graph are
 * DataSources, the Edges are IDMappers. Possible mappings are
 * loop free paths in this graph. We consider a path p to be loop free 
 * if no data source in p is reached twice by the same IDMapper.
 * <p>
 * The mapping graph for transitive maps is retained and re-calculated
 * whenever an IDMapper is added or removed from this IDMapperStack.
 * 
 */
public class IDMapperStack implements IDMapper, AttributeMapper
{
	// reference shared with TransitiveGraph
	private final List<IDMapper> gdbs = new CopyOnWriteArrayList<IDMapper>();

	/** Helper class for calculating transitive paths */
	private TransitiveGraph transitiveGraph = null;
	
	private TransitiveGraph getTransitiveGraph() throws IDMapperException
	{
		if (transitiveGraph == null)
			transitiveGraph = new TransitiveGraph(gdbs);
		return transitiveGraph;
	}

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
            transitiveGraph = null; // trigger rebuild.
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
	 * Automatically rebuilds the mapping graph.
	 * 
	 * @param idMapper IDMapper to be removed.
	 */
    public void removeIDMapper(IDMapper idMapper)
    {
    	gdbs.remove(idMapper);
    	transitiveGraph = null; // trigger rebuild
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
			
			if(isTransitive)
			{
				return getTransitiveGraph().isTransitiveMappingSupported(src, tgt);
			}
			
			else {
			for (IDMapper idm : IDMapperStack.this.gdbs) {
				if (idm.getCapabilities().isMappingSupported(src, tgt)) {
					return true;
				}
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
	
	
	public Map<Xref, Set<String>> freeAttributeSearchEx (String query, String attrType, int limit) throws IDMapperException
	{
		Map<Xref, Set<String>> result = new HashMap<Xref, Set<String>>();
		for (IDMapper child : gdbs)
		{
			if (child != null && child instanceof AttributeMapper && child.isConnected()
				&& ((AttributeMapper)child).isFreeAttributeSearchSupported())
			{
				Map<Xref, Set<String>> childResult = 
					((AttributeMapper)child).freeAttributeSearchEx(query, attrType, limit);
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
	 * Helper method to map Id's in transitive mode.
	 * Relies on pre-calculated mapping graph
	 * in order to deduce transitively mappings.
	 * 
	 * @param ref Xref to map
	 * @param resultDs target data sources
	 * @return mapping result
	 * @throws IDMapperException if one of the children fail
	 */
	private Set<Xref> mapIDtransitive(Xref ref, DataSource... resultDs)
			throws IDMapperException {
		
		if( resultDs.length == 0 ) 
		{
			return getTransitiveGraph().mapIDtransitiveUntargetted(ref);
		}
		else
		{
			Set<DataSource> dsFilter = new HashSet<DataSource>(Arrays
					.asList(resultDs));
			Set<Xref> result = getTransitiveGraph().mapIDtransitiveTargetted(ref, dsFilter);
			return result;
		}
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
	
	/** get all mappers */
	public List<IDMapper> getMappers()
	{
		return gdbs;
	}

}
