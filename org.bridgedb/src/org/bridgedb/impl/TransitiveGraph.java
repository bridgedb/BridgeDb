package org.bridgedb.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/**
 * Helper for caluclating paths in IDMapperStack in transitive mode
 */
public class TransitiveGraph
{
	/** all possible paths indexed by their source (starting node) */
	private Map<DataSource, Set<Path> > sourceMap = new HashMap<DataSource, Set<Path>>();
	
	/** all possible paths indexed by their target (end node) */
	private Map<DataSource, Set<Path> > targetMap = new HashMap<DataSource, Set<Path>>(); 
	
	/** Constructor: immediately starts calculating paths */
	public TransitiveGraph(List<IDMapper> gdbs) throws IDMapperException
	{
		Set<Path> openSet = getDirectPaths(gdbs); // initialize map
		indexPaths(openSet);
		while (openSet.size() > 0)
		{ // keep adding paths while there are new Edges for the
							// graph
			openSet = getPathExtensions(openSet);
		}		
	}
	
	/** 
	 * Calculates a new set of paths that consists of non-cyclic extensions 
	 * of the input set.
	 * @param openSet input set to extend
	 * @returns all valid extensions of the input set. Returns an empty set if there are no valid extensions.
	 */
	private Set<Path> getPathExtensions(Set<Path> openSet) 
	{		
		Set<Path> result = new HashSet<Path>();

		// create extended paths for every DataSource
		Iterator<Path> i = openSet.iterator();

		// take items one by one from the open set ...
		while (i.hasNext())
		{			
			Path path = i.next();
			i.remove();
			
			// ... and find valid extensions for it
			Set<Path> extensions = findValidExtensions(path);			
			result.addAll(extensions);
		}

		// index the new paths by source and target
		indexPaths(result);
		
		// return the newly created extensions
		return result; 
	}

	/**
	 * Update the target and source maps with the given set of new, valid paths
	 */
	private void indexPaths(Set<Path> set)
	{
		for (Path p : set)
		{
			DataSource source = p.getSource();
			DataSource target = p.getTarget();
			
			InternalUtils.multiMapPut(sourceMap, source, p);
			InternalUtils.multiMapPut(targetMap, target, p);
		}
	}

	/**
	 * find valid extensions of a given path. This looks up 
	 * all paths that have a source that matches the current target,
	 * and filters out the ones that introduce a cycle. 
	 */
	private Set<Path> findValidExtensions(Path path)
	{
		Set<Path> result = new HashSet<Path>();
		DataSource lastElement = path.getTarget();
		
		for( Path extension : sourceMap.get(lastElement) ) {
			if (extension.isLoopFreeExtension(path) ) {
				Path newPath = new Path( path, extension );
				result.add(newPath);
			}
		}
		
		return result;
	}


	/** 
	 * This is for testing. May be removed in the future
	 */
	public void printMap(Map<DataSource, Set<Path> > map) 
	{
		for( DataSource ds : map.keySet() ) {
			Set<Path> collection = map.get(ds);
			System.out.println("DataSource: " + ds);
			System.out.println(" - " + collection + "\n");
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
	private class Edge 
	{
		public DataSource source;
		public DataSource target;
		public IDMapper label;
		
		public Edge(DataSource source, DataSource target, IDMapper label) {
			this.source = source;
			this.target = target;
			this.label = label;
		}

		public String toString() {
			return " -> " + target;
		}
		
		@Override
		public int hashCode()
		{
			// NB: source and target may be swapped and it's still equal
			return 3 * (source.hashCode() + target.hashCode()) + 
					5 * label.hashCode();
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
	 */
	private class Path
	{	
		private final List<Edge> delegate = new ArrayList<Edge>();
		private final Set<IDMapper> mappers = new HashSet<IDMapper>();
		
		Path (Edge first) 
		{	
			delegate.add(first); 
			mappers.add(first.label);
		}
		
		Path (Path p, Path q) 
		{
			delegate.addAll(p.delegate);
			delegate.addAll(q.delegate);
			for (Edge e : p.delegate)
				mappers.add(e.label);
			for (Edge e : q.delegate)
				mappers.add(e.label);
		}
		
		public DataSource getSource() { return delegate.get(0).source; }
		public DataSource getTarget() { return delegate.get(delegate.size()-1).target; }

		/**
		 * @param other Some other path
		 * @return true if this path is a non-cyclic extension of the other path.
		 *   a path is considered "cyclic" when it uses the same IDMapper twice.
		 *   (note that this is not the only possible definition of cyclic. But it is
		 *   a convenient definition as it culls the number of paths, and thus
		 *   reduces combinatorial problems)
		 */
		protected boolean isLoopFreeExtension( Path other ) 
		{
			/*
			 // see the commented code here for another possible definition of cyclic: 
			for (Edge e : other.delegate)
			{
				if (delegate.contains(e)) 
					return false;
			}
			return true;
			 * 
			 */
			for (IDMapper m : other.mappers)
				if (mappers.contains(m)) return false;
			return true;
		}
		
		public String toString()
		{
			String returnValue = "";
			if(!delegate.isEmpty()){
				returnValue += this.get(0).source;
			}
			for ( Edge e : delegate ) {
					returnValue += e;
			}
			return returnValue;
		}
		
		@Override
		public int hashCode()
		{
			int[] primes = new int[] { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31 };
			int result = 0;
			for (int i = 0; i < size(); ++i)
				result += primes[i % primes.length] * get(i).hashCode();
			return result;
		}
			
		/**
		 *  @param other Other path to be compared to.
		 *  @return true if this path is equal to the other path.
		 *  	Two paths are equal, if they have the same number of edges,
		 *  	each edge has the same source, target and label, and 
		 *  	the edges occur in the same order.
		 */
		public boolean equals( Object other ) 
		{
			if (this == other) return true; 
			if (!(other instanceof Path)) return false;
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

		private Edge get(int i)
		{
			return delegate.get(i);
		}

		private int size()
		{
			return delegate.size();
		}

	}

	/**
	 * Create a list of all direct (i.e. one-step) paths. 
	 * 
	 * @return Hash that contains all relevant information on maps between
	 *         DataSources of all IDMappers in this IDMapperStack. Reflexive
	 *         maps (DataSourced X -> DataSource X) are ignored. The map will
	 *         contain only DataSources that are connected.
	 *         
	 * @throws IDMapperException
	 */
	private Set<Path> getDirectPaths(List<IDMapper> gdbs)
			throws IDMapperException {

		Set<Path> result = new HashSet<Path>();
		
		// add each DataSource to a PathCollection
		for (IDMapper idm : gdbs) {
			if (idm != null && idm.isConnected()) {
				IDMapperCapabilities capas = idm.getCapabilities();
				for (DataSource src : capas.getSupportedSrcDataSources()) {
					for (DataSource tgt : capas.getSupportedTgtDataSources()) {
						if (capas.isMappingSupported(src, tgt) && src != tgt) {
							Edge edge = new Edge(src, tgt, idm);
							Path path = new Path(edge);
							result.add(path);
						}
					}
				}
			}
		}
		return result;
	}

	public boolean isTransitiveMappingSupported(DataSource src, DataSource tgt) throws IDMapperException
	{
		if (!(sourceMap.containsKey(src) && targetMap.containsKey(tgt))) 
			return false; 
		
		for (Path path : sourceMap.get(src)) 
		{
			if (path.getTarget() == tgt)
				return true;
		}
		
		return false;
	}

	public Set<Xref> mapIDtransitiveTargetted(Xref ref, Set<DataSource> dsFilter)
			throws IDMapperException
	{
		DataSource srcDs = ref.getDataSource();

		Set<Xref> result = new HashSet<Xref>();
		for (DataSource tgtDs : targetMap.keySet()) {
			for (Path path : targetMap.get(tgtDs)) {
				DataSource pathSource = path.getSource();
				DataSource pathTarget = path.getTarget();
				if ( pathSource == srcDs && tgtDs == pathTarget && 
						dsFilter.contains(pathTarget)) 
				{
					for (Xref j : mapID(ref, path))
					{
						if (dsFilter.contains(j.getDataSource())) 
							result.add(j);
					}
				}
			}
		}
		return result;
	}

	public Set<Xref> mapIDtransitiveUntargetted(Xref ref) throws IDMapperException
	{
		Set<Xref> result = new HashSet<Xref>();
		DataSource dataSource = ref.getDataSource();
		if (!sourceMap.containsKey(dataSource)) return result;
		for (Path path : sourceMap.get(dataSource)) 
		{
			if (path.size() > 0) {
				for (Xref j : mapID(ref, path))
				{
					result.add(j);
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

}
