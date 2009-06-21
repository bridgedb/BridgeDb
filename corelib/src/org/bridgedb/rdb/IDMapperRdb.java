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
package org.bridgedb.rdb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.Driver;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.XrefWithSymbol;

/**
 * Interface for all classes that provide Gdb-like functionality,
 * such as looking up cross-references and backpage text.
 */
public abstract class IDMapperRdb implements IDMapper
{
	static
	{
		BridgeDb.register ("idmapper-pgdb", new Driver());
	}
	
	private static class Driver implements org.bridgedb.Driver
	{
		private Driver() { } // prevent outside instantiation
				
		public IDMapper connect(String location) throws IDMapperException 
		{
			return SimpleGdbFactory.createInstance(location, new DataDerby(), 0);
		}
	}
	
	/**
	 * Check whether a connection to the database exists.
	 * @return true if a connection exists, false if not
	 * 
	 * A connection will not exist only 
	 * after the close() method is called.
	 * implementing classes should create a connection in
	 * the constructor, and throw an exception at that moment
	 * if a connection is not possible.
	 */
	public abstract boolean isConnected();

	/**
	 * Gets the name of the currently used gene database.
	 * @return the database name as specified in the connection string
	 */
	public abstract String getDbName();

	/**
	 * Gets the backpage info for the given gene id for display on BackpagePanel.
	 * @param ref The gene to get the backpage info for
	 * @return String with the backpage info, null if the gene was not found
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
	 */
	public abstract String getBpInfo(Xref ref) throws IDMapperException;
	
	/**
	 * Get all cross-references for the given id/code pair, restricting the
	 * result to contain only references from database with the given system
	 * code.
	 * @param idc The id/code pair to get the cross references for
	 * @return A {@link List} containing the cross references, or an empty
	 * {@link List} when no cross references could be found
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
	 */
	public abstract List<Xref> getCrossRefs(Xref idc) throws IDMapperException;

	/**
	 * Get all cross-references for the given id/code pair, restricting the
	 * result to contain only references from database with the given system
	 * code.
	 * @param idc The id/code pair to get the cross references for
	 * @param resultDs The system code to restrict the results to
	 * @return An {@link List} containing the cross references, or an empty
	 * ArrayList when no cross references could be found
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
	 */
	public abstract List<Xref> getCrossRefs (Xref idc, DataSource resultDs) throws IDMapperException;

	/**
	 * Get a list of cross-references for the given attribute name/value pair. This
	 * can be used to e.g. get the xref for a gene symbol.
	 * @param attrName	The attribute name (e.g. 'Symbol')
	 * @param attrValue	The attribute value (e.g. 'TP53')
	 * @return A list with the cross-references that have this attribute name/value, or an
	 * empty list if no cross-references could be found for this attribute name/value.
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
	 */
	public abstract List<Xref> getCrossRefsByAttribute(String attrName, String attrValue) throws IDMapperException;

	/**
	 * Closes the connection to the Gene Database if possible.
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
	 */
	public abstract void close() throws IDMapperException;

	/**
	 * Get up to limit suggestions for a symbol autocompletion.
	 * @param text text query, prefix of symbol
	 * @param limit will return up to limit results.
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
	 */
	public abstract List<String> getSymbolSuggestions(String text, int limit) throws IDMapperException;

	
	/**
	 * Get up to limit suggestions for a identifier autocompletion.
	 * @param text text query, prefix of id
	 * @param limit will return up to limit results.
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
	 */
	public abstract List<Xref> getIdSuggestions(String text, int limit) throws IDMapperException;
	
	/**
	 * free text search for matching symbols or identifiers
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
	 */
	public abstract List<XrefWithSymbol> freeSearchWithSymbol (String text, int limit) throws IDMapperException;

	/**
	 * free text search for matching symbols or identifiers
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable
	 */
	public Set<Xref> freeSearch(String text, int limit) throws IDMapperException 
	{
		// turn XrefWithSymbol list into Xref list
		Set<Xref> result = new HashSet<Xref>();
		for (XrefWithSymbol i : freeSearchWithSymbol(text, limit))
		{
			result.add(i.asXref());
		}
		return result;
	}
	
	/**
	 * Map a set of Xrefs at once.
	 * @param tgtDataSources only return xrefs from these DataSources.
	 * @param srcXrefs the cross-references that should be mapped
	 * @return Map of source to destination refs. The keys will be a subset of the srcXrefs argument
	 * Implemented using multiple calls of getCrossRefs().
	 * May be overridden if there is a more efficient implementation possible.
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable
	 */
	public Map<Xref, Set<Xref>> mapID(Set<Xref> srcXrefs, Set<DataSource> tgtDataSources) throws IDMapperException 
	{
		final Map<Xref, Set<Xref>> result = new HashMap<Xref, Set<Xref>>();
		for (Xref src : srcXrefs)
		{
			final Set<Xref> refs = new HashSet<Xref>();
			for (Xref dest : getCrossRefs(src))
			{
				if (tgtDataSources.contains(dest.getDataSource()))
				{
					refs.add (dest);
				}
			}
			if (refs.size() > 0)
				result.put (src, refs);
		}
		return result;
	}	

}
