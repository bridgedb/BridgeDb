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
package org.bridgedb.url;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.MappingSetStatistics;

/**
 * Base interface for all URL mapping methods.
 * Has methods for basic functionality such as looking up cross-references and backpage text.
 * 
 * Similar to the IDMapper interface except treats URLs as first class citizens.
 * To keep code size small URLs are represented as Strings.
 */
public interface URLMapper extends IDMapper{

	/**
	 * Get all cross-references for a set of entities, restricting the
	 * result to contain only references from the given set of name spaces.
     * Supports one-to-one mapping and one-to-many mapping.
     * 
     * Similar to the mapID method in IDMapper.
     * 
     * @param sourceURLs source URLs, Strings that DataSource can split into prefix, id and postfix
     * @param targetURISpaces target name spaces (prefix) that can be included in the resulst. Set this to null
     *   if you want to retrieve all results.
     * @return a map from source URIs to target URIs's. The map is not guaranteed
     *    to contain a result for each srcURIs you pass in. This method will never
     *    return null however.
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
     */
    public Map<String, Set<String>> mapURL(Collection<String> sourceURLs, String... targetURISpaces) throws IDMapperException;

    public Set<URLMapping> mapURLFull(String sourceURL, String... targetURISpaces) throws IDMapperException;

    /**
	 * Get all cross-references for the given entity, restricting the
	 * result to contain only references from the given set of name spaces.
     * 
     * Similar to the mapID method in IDMapper.
     * 
	 * @param sourceURL the entity to get cross-references for. 
     * @param targetURISpaces target name spaces (prefix) that can be included in the resulst. Set this to null
     *   if you want to retrieve all results.
	 * @return A Set containing the cross references, or an empty
	 * Set when no cross references could be found. This method does not return null.
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
	 */
	public Set<String> mapURL (String sourceURL, String... targetURISpaces) throws IDMapperException;
	
    /**
     * Check whether an URL is known by the given mapping source. This is an optionally supported operation.
     * @param url URL to check
     * @return if the URL exists, false if not
     * @throws IDMapperException if failed, UnsupportedOperationException if it's not supported by the Driver.
     */
    public boolean uriExists(String URL) throws IDMapperException;

    /**
     * free text search for matching symbols or identifiers.
     * 
     * Similar to the freeSearch meathod in IDMapper
     * @param text text to search
     * @param limit up limit of number of hits
     * @return a set of hit references
     * @throws IDMapperException if failed
     */
    public Set<String> urlSearch (String text, int limit) throws IDMapperException;

    /**
     * Identical to IDMapper method.
     * @return capacities of the ID mapper
     */
    public IDMapperCapabilities getCapabilities();
    
    /**
     * dispose any resources (such as open database connections) associated
     * with this IDMapper.
     * Identical to IDMapper method.
     * @throws IDMapperException if the associated resources could not be freed.
     */
    public void close() throws IDMapperException;
    
    /**
     * Use this method to check if the IDMapper is still valid.
     * Identical to IDMapper method.
     * @return false after the close() method is called on this object, true otherwise 
     */
    public boolean isConnected();
    
    public Xref toXref(String URL) throws IDMapperException;
    
    public URLMapping getMapping(int id)  throws IDMapperException;
    
    /**
     * Gets a Sample of Source URls.
     * 
     * Main use is for writing the api description page
     * @return 
     */
    public Set<String> getSampleSourceURLs() throws IDMapperException;
    
    public  MappingSetStatistics getMappingSetStatistics() throws IDMapperException;

    public List<MappingSetInfo> getMappingSetInfos() throws IDMapperException;
    
    public Set<String> getUriSpaces(String sysCode) throws IDMapperException;

}
