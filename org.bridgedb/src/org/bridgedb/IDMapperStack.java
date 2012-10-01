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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

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
	private List<IDMapper> gdbs = new CopyOnWriteArrayList<IDMapper>();

	private Map<DataSource, PathCollection> mappingGraph; // paths hashed by
																// the the
																// paths' source
																// (starting
																// node)
	private Map<DataSource, PathCollection> targetMap; // paths hashed by
															// the the paths'
															// target (terminal
															// node)

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
            try {
				rebuildDataSourcesMap(); // re-build the mapping graph
			} catch (IDMapperException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
    	try {
			rebuildDataSourcesMap();
		} catch (IDMapperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				
			if(isTransitive){	
				if(IDMapperStack.this.mappingGraph==null){
					IDMapperStack.this.rebuildDataSourcesMap();
				}
				
				if( mappingGraph.get(src)==null ) {
					return false; 
				}

				for (Path path : mappingGraph.get(src).closedList) {
					if (path.size() > 0) {
						Edge lastEdge = path.get(path.size() - 1);
						if (lastEdge.target == tgt) {
							return true;
						}
					}
				}
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
		
		// first round
		Set<Xref> result = new HashSet<Xref>();
		Set<DataSource> dsFilter = new HashSet<DataSource>(Arrays
				.asList(resultDs));
		DataSource dataSource = ref.getDataSource();

		if( resultDs.length == 0 ) {
			return result;
		}
				
		if (targetMap == null) {
			rebuildDataSourcesMap();
		}

		for (DataSource key : targetMap.keySet()) {
			for (Path path : targetMap.get(key).closedList) {
				if (path.size() > 0) {
					Edge lastEdge = path.get(path.size() - 1);
					DataSource firstDataSource = path.get(0).source;
					DataSource lastTarget = lastEdge.target;
					if ( firstDataSource == dataSource && key==lastTarget && 
							dsFilter.contains(lastTarget)) 
					{
						for (Xref j : mapID(ref, path))
						{
							if (dsFilter.contains(j.getDataSource())) 
								result.add(j);
						}
					}
				}
			}
		}

		return result;
	}

		
	/**
	 * 
	 * Map ID transitively using specified sequence of DataSources given by path.
	 * 
	 * @param Xref
	 *            Xref to be mapped
	 * @param path
	 *            Sequence of DataSources to be mapped, given as a Path.
	 */
	private Set<Xref> mapID(Xref xref, Path path) throws IDMapperException {
		Set<Xref> result = new HashSet<Xref>();
		
		if (path == null || path.size() <= 0) {
			return result;
		}
		
		Edge e = path.get(0);
		result = e.label.mapID(xref, e.target);

		for (int i = 1; i < path.size(); i++) {
			e = path.get(i);
			if( e == null || e.source == null || e.target == null || e.label == null ) {
				throw new IDMapperException();
			}
			if( ! e.label.isConnected() ) {
				return new HashSet<Xref>();
			}
			Map<Xref, Set<Xref>> tmp = e.label.mapID(result, e.target);
			result.clear();
			for (Xref key : tmp.keySet()) {				
				result.addAll(tmp.get(key));
			}
		}
		
		return result;
	}

	
	// convenience method
	private Set<Xref> getSubsetMatchingDataSource( Set<Xref> set, DataSource source ) {
		Set<Xref> subset = new HashSet<Xref>();
		for( Xref x: subset ) {
			if( x.getDataSource() == source ) {
				subset.add(x);
			}
		}
		return subset;
	}
	
	
	/**
	 * Initialize the mapGraph before calling rebuildPath().
	 * 
	 * @return Hash that contains all relevant information on maps between
	 *         DataSources of all IDMappers in this IDMapperStack. Reflexive
	 *         maps (DataSourced X -> DataSource X) are ignored. The map will
	 *         contain only DataSources that are connected.
	 *         
	 * @throws IDMapperException
	 */
	private Map<DataSource, PathCollection> initMap()
			throws IDMapperException {

		mappingGraph = new HashMap<DataSource, PathCollection>();

		// add each DataSource to a PathCollection
		for (IDMapper idm : gdbs) {
			if (idm != null && idm.isConnected()) {
				IDMapperCapabilities capas = idm.getCapabilities();
				for (DataSource src : capas.getSupportedSrcDataSources()) {
					for (DataSource tgt : capas.getSupportedTgtDataSources()) {
						if (capas.isMappingSupported(src, tgt) && src != tgt) {
							Edge edge = new Edge(src, tgt, idm);
							Path path = new Path();
							path.add(edge);
							if (mappingGraph.get(src) == null) {
								PathCollection paths = new PathCollection(src);
								paths.openList = new ArrayList<Path>();
								paths.openList.add(path);
								mappingGraph.put(src, paths);
							} else {
								mappingGraph.get(src).openList.add(path);
							}
						}
					}
				}
			}
		}

		return mappingGraph;
	}


	/**
	 *  Populate the targetMap. the targetMap allows to look up
	 *  a list of all paths that end on a given target.
	 **/
	private void rebuildTargetMap() {
		
		targetMap = new HashMap<DataSource,PathCollection>();
		
		for( DataSource key : mappingGraph.keySet() ){
			for ( Path path : mappingGraph.get(key).closedList ) {
				if( path.size() > 0 ) {
					Edge lastEdge = path.get( path.size() - 1);;
					DataSource tgt = lastEdge.target;
					List<Path> list;
					if(targetMap.get(tgt)==null) {
						PathCollection collection = new PathCollection(tgt);
						targetMap.put( tgt, collection );
						list = collection.closedList;
					}
					else {
						list = targetMap.get(tgt).closedList;
					}	
					list.add(path);
				}
			}
		}
	}
	

	
	/**
	 * Rebuild the DataSources map that contains information on all mappings
	 * supported by this IDMapperStack.
	 * 
	 * @throws IDMapperException
	 * 
	 * */
	public void rebuildDataSourcesMap() throws IDMapperException {

		initMap(); // initialize map
		boolean isDone = false;
		while (!isDone) { // keep adding paths while there are new Edges for the
							// graph
			isDone = rebuildPaths();
		}
		rebuildTargetMap();
	}

	
	/** 
	 * Rebuild the path information for transitive maps.
	 *
	 * Calculate all non-cyclic paths on the connection graph. 
	 * Each path connects two DataSources. This is the implementation
	 * of the algorithm for finding all paths we are interested in.
	 * 
	 * @param true, if path-building process is completed, i.e., 
	 *        if all open lists are empty.
	 */
	private boolean rebuildPaths() {
		
		HashMap<DataSource,List<Path>> extendedPaths = new HashMap<DataSource,List<Path>>(); 
		
		// create extended paths for every DataSource
		for( DataSource key : mappingGraph.keySet() ) {
			
			PathCollection collection = mappingGraph.get(key);
			List<Path> extendedPathsForDataSource = new ArrayList<Path>();

			for (Path path : collection.openList) {

				DataSource lastElement = path.get(path.size() - 1).target;
				List<Path> extensions = mappingGraph.get(lastElement).getExtendingPaths(path);
					
				path.toBeMoved = true;
				for( Path extension : extensions ) {
					// add extending Path to openList
					Path newPath = new Path( path, extension );
					if( !collection.contains(newPath) ) {
						path.toBeMoved = false;
						if(!extendedPathsForDataSource.contains( newPath )){
							extendedPathsForDataSource.add( newPath );
						}
					}
				}		
			}
			
			if(extendedPathsForDataSource.size()>0){
				extendedPaths.put( key, extendedPathsForDataSource );
			}
		}
		
		/* update map in two steps:
		 *      (1) move paths from open list to closed list,
		 *      (2) add new extended paths into open list. 
		 */
		
		// move marked paths from open list to closed list
		for( DataSource key : mappingGraph.keySet() ) {
			PathCollection collection = mappingGraph.get(key);
			List<Path> openList = collection.openList;
			List<Path> closedList = collection.closedList;
			List<Path> swapList = new ArrayList<Path>();
			for( Path p : openList ) {
				if(p.toBeMoved) {
					closedList.add(p);
				}
				else {
					swapList.add(p);
				}
			}
			collection.openList = swapList;
		}
		

		// move new extended paths into open list
		for( DataSource key : extendedPaths.keySet() ) {
			for( Path extendedPath : extendedPaths.get(key) ) {
				mappingGraph.get(key).openList.add(extendedPath); 
			}
		}
		
		// check for empty list
		boolean allOpenListsEmpty = true;
		for( DataSource key : mappingGraph.keySet() ) {
			if( !mappingGraph.get(key).openList.isEmpty() ) {
				allOpenListsEmpty = false;
				break;
			}
		}
		
		return allOpenListsEmpty;   // the map is complete, when all openList are empty
	}


	/** 
	 * This is for testing. Removed as soon as the code is considered mature.
	 */
	private void printMap( HashMap<DataSource,PathCollection> map) {
		for( DataSource ds : map.keySet() ) {
			PathCollection collection = map.get(ds);
			System.out.println("DataSource: " + ds);
			System.out.println("OL: " + collection.openList);
			System.out.println("CL: " + collection.closedList + "\n");
		}
		System.out.println();		
	}
	

	/* An Edge is an edge in the graph that represents all possible,
	 * transitive (and loop free) mappings supported by the IDMapper Stack.
	 * An edge connects two DataSources (source and target) by an IDMapper
	 * from the gdbs. Every Edge is directed to point from source to target.
	 * Note that for internal convenience, two Edges are considered equal
	 * if they connect the same DataSources irrespective of the direction
	 * of the Edge. 
	 */
	protected class Edge {
		DataSource source;
		DataSource target;
		IDMapper label;
		
		public Edge(DataSource source, DataSource target, IDMapper label) {
			this.source = source;
			this.target = target;
			this.label = label;
		}

		public String toString() {
			return " -> " + target;
		}
		
		
		/** This Edge is equal to another object, if the other object is an Edge
		 *  and both Edges have the same label AND also the same nodes. The direction
		 *  of the Edges does not matter.
		 *  
		 *  @param o Another Edge
		 */
		public boolean equals( Object o ) {
			if( ! (o instanceof Edge) ) {
				return false;
			}
			Edge e = (Edge) o;
			return (this.label == e.label && this.source == e.source && this.target == e.target)
					|| (this.label == e.label && this.source == e.target && this.target == e.source);			
		}
		
	}
	
	/**
	 *  A Path is a vector of Edges in the graph that describe the relationships between the
	 *  (transitive) maps supported by the IDMapperStack. 
	 * 
	 */
	protected class Path extends Vector<Edge> {
		
		private static final long serialVersionUID = 1L;

		protected boolean toBeMoved = false;  // flag for iterations in rebuilPath()

		protected Path() {
			super();
		}
		
		protected Path( Path p, Path q ) {
			addAll(p);
			addAll(q);
		}

		/**
		 * @param other Some other path
		 * @return true iff this path contains not element of the other path
		 */
		protected boolean isLoopFreeExtension( Path other ) {

			boolean returnValue = true;
			for( Edge e : other ){
				if( this.contains( e ) ) {
					returnValue = false;
					break;
				}
			}
			return returnValue;
		}
		
		public String toString(){
			String returnValue = "";
			if(!isEmpty()){
				returnValue += this.get(0).source;
			}
			for ( Edge e : this ) {
					returnValue += e;
			}
			return returnValue;
		}
		
		
		/**
		 *  @param other Other path to be compared to.
		 *  @return true iff this path is equal to the other path.
		 *  	Two pathes are equal, if they have the same number of edges,
		 *  	each edge has the same source, target and label, and 
		 *  	the edges occur in the same order.
		 */
		public boolean equals( Object other ) {
			if( !(other instanceof Path) ) {
				return false;
			}
			Path path = (Path) other;
			if( this.size() != path.size() ) {
				return false;
			}
			
			boolean returnValue = true;
			for (int i = 0; i < size(); i++) {
				Edge myEdge = get(i);
				Edge otherEdge = path.get(i);
				if (myEdge.source != otherEdge.source
						|| myEdge.target != otherEdge.target
						|| myEdge.label != otherEdge.label) {
					returnValue = false;
					break;
				}
			}
			return returnValue;
		}
		
	}
	
	
	/**
	 * This is class is a data structure for collecting all possible
	 * transitive, non-loopy mappings supported by this IDMapperStack.
	 */
	protected class PathCollection {
		public List<Path> openList;
		public List<Path> closedList;
		DataSource dataSource;
		
		public PathCollection( DataSource dataSource ) {
			this.dataSource = dataSource;
			openList = new ArrayList<Path>();
			closedList = new ArrayList<Path>();
		}
		
		/** True if equal path is already contained in open or closed list. */
		public boolean contains(Path path) {
			return openList.contains(path) || closedList.contains(path);
		}

		public String toString(){
			return "OL: " + openList + "; CL: " + closedList;
		}
		
		public List<Path> getExtendingPaths( Path otherPath ) {

			List<Path> list = new ArrayList<Path>();			

			for( Path p : openList ) {
				if( p.isLoopFreeExtension(otherPath) ) {
					list.add(p);
				}
			}

			for( Path p : closedList ) {
				if( p.isLoopFreeExtension(otherPath) ) {
					list.add(p);
				}
			}

			return list;
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
