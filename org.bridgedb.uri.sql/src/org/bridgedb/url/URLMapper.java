// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
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
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.statistics.ProfileInfo;
import org.bridgedb.utils.BridgeDBException;

/**
 * Base interface for all URL mapping methods.
 * Has methods for basic functionality such as looking up cross-references and backpage text.
 * 
 * Similar to the IDMapper interface except treats URLs as first class citizens.
 * To keep code size small URLs are represented as Strings.
 */
public interface URLMapper extends IDMapper{
//TODO: Improve javadoc!
    
    
    /**
	 * Get all cross-references for the given entity, restricting the
	 * result to contain only references from the given set of data sources.
	 * @param ref the entity to get cross-references for. 
     * @param tgtDataSources target ID types/data sources. Set this to null if you 
     *   want to retrieve all results.
	 * @return A Set containing the cross references, or an empty
	 * Set when no cross references could be found. This method does not return null.
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
	 */
	public Set<Xref> mapID (Xref ref, String profileURL, DataSource... tgtDataSources) throws BridgeDBException;

    public Set<Mapping> mapFull (Xref sourceXref, String profileURL) 
            throws BridgeDBException;

	public Set<Mapping> mapFull (Xref sourceXref, String profileURL, DataSource... tgtDataSources) 
            throws BridgeDBException;

    public Set<Mapping> mapFull (Xref sourceXref, String profileURL, UriPattern... tgtUriPattern) 
            throws BridgeDBException;

	public Set<Mapping> mapFull (String sourceUri, String profileURL) 
            throws BridgeDBException;

    public Set<Mapping> mapFull (String sourceUri, String profileURL, DataSource... tgtDataSources) 
            throws BridgeDBException;

	public Set<Mapping> mapFull (String sourceUri, String profileURL, UriPattern... tgtUriPattern) 
            throws BridgeDBException;

    /**
     * Similar to mapURL(String URL, String... targetURISpaces) except that more that one sourceURL can be provided 
     *   and the result is a mapping from each input sourceURL to the set off its mapped URLs.
     * <p>
     * @See mapURL(String URL, String... targetURISpaces) for more details
     * @param sourceURLs One or more URL as a String
     * @param profileURL the URL of the profile to use when retrieving mappings
     * @param targetURISpaces (Optional) Target UriSpaces that can be included in the result.
     *    Not including any TartgetURRSpace results in all mapped/ cross-references URLs to be returned. 
     * @return A map of each of the sourceURLs to the Set of URLs (as String) that would have been returned byu calling
     *    mapURL(sourceURL, targetURISpaces) individually.
	 * @throws BridgeDBException Could be because the mapping service is (temporarily) unavailable 
     */
    public Map<String, Set<String>> mapURL(Collection<String> sourceURLs, String profileURL, String... targetURISpaces) 
            throws BridgeDBException;

    /**
     * Similar to mapURL(String URL, String... targetURISpaces) except that the result will be a set of URLMappings.
     * <p>
     * @See mapURL(String URL, String... targetURISpaces) for more details or the method.
     * @See URLMappings for details of what is included in the Results.
	 * @param sourceURL the URL to get mappings/cross-references for. 
	 * @param profileURL the URL of the profile to use when retrieving mappings
     * @param targetURISpaces (Optional) Target UriSpaces that can be included in the result. 
     *    Not including any TartgetURRSpace results in all mapped/ cross-references URLs to be returned.
	 * @return A Set containing the URL (in URLMapping Objects) that have been mapped/ cross referenced.
	 * @throws BridgeDBException Could be because the mapping service is (temporarily) unavailable 
     */
    public Set<Mapping> mapURLFull(String URL, String profileURL, String... targetURISpaces) 
            throws BridgeDBException;

    /**
	 * Get all mappings/cross-references for the given URL, restricting the
	 * result to contain only URLs from the given set of UriSpaces.
     * <p>
     * Result will include the sourceURL (even if uriExists(sourceUrl) would return null),
     *    if and only it has one of the targetURISpaces (or targetURISpaces is empty)
     *    Result will be empty if no mapping/ cross references could be found. 
     *    This method should never return null.
     * <p>
     * Similar to the mapID method in IDMapper.
     * 
	 * @param sourceURL the URL to get mappings/cross-references for. 
	 * @param profileURL the URL of the profile to use when retrieving mappings
     * @param targetURISpaces (Optional) Target UriSpaces that can be included in the result. 
     *    Not including any TartgetURRSpace results in all mapped/ cross-references URLs to be returned.
	 * @return A Set containing the URL (as Strings) that have been mapped/ cross referenced.
	 * @throws BridgeDBException Could be because the mapping service is (temporarily) unavailable 
	 */
	public Set<String> mapURL (String URL, String profileURL, String... targetURISpaces) throws BridgeDBException;
	
    /**
	 * Get all mappings/cross-references for the given Xref, restricting the
	 * result to contain only URLs from the given set of UriSpaces.
     * <p>
     * Result will include the sourceURL (even if uriExists(sourceUrl) would return null),
     *    if and only it has one of the targetURISpaces (or targetURISpaces is empty)
     *    Result will be empty if no mapping/ cross references could be found. 
     *    This method should never return null.
     * <p>
     * Similar to the mapID method in IDMapper.
     * 
	 * @param ref the entity to get cross-references for. 
     * @param targetURISpaces (Optional) Target UriSpaces that can be included in the result. 
     *    Not including any TartgetURRSpace results in all mapped/ cross-references URLs to be returned.
	 * @return A Set containing the URL (as Strings) that have been mapped/ cross referenced.
	 * @throws BridgeDBException Could be because the mapping service is (temporarily) unavailable 
	 */
	public Set<String> mapToURLs (Xref ref, String profileURL, String... targetURISpaces) throws BridgeDBException;

    /**
	 * Get all mappings/cross-references for the given Xref, restricting the
	 * result to contain only URLs from the given set of UriSpaces.
     * <p>
     * Result will include the sourceURL (even if uriExists(sourceUrl) would return null),
     *    if and only it has one of the targetURISpaces (or targetURISpaces is empty)
     *    Result will be empty if no mapping/ cross references could be found. 
     *    This method should never return null.
     * <p>
     * Similar to the mapID method in IDMapper.
     * 
     * @param srcXrefs source Xref, containing ID and ID type/data source
     * @param targetURISpaces (Optional) Target UriSpaces that can be included in the result. 
     *    Not including any TartgetURRSpace results in all mapped/ cross-references URLs to be returned.
     * @return A map of each of the xREF to the Set of URLs (as String) that would have been returned by calling
     *    mapURL(Xref, targetURISpaces) individually.
	 * @throws BridgeDBException Could be because the mapping service is (temporarily) unavailable 
	 */
	public  Map<Xref, Set<String>>  mapToURLs (Collection<Xref> srcXrefs, String profileURL, String... targetURISpaces) 
            throws BridgeDBException;

    /**
	 * Get all mappings/cross-references for the given Xref, restricting the
	 * result to contain only URLs from the given set of UriSpaces.
     * <p>
     * Result will include the sourceURL (even if uriExists(sourceUrl) would return null),
     *    if and only it has one of the targetURISpaces (or targetURISpaces is empty)
     *    Result will be empty if no mapping/ cross references could be found. 
     *    This method should never return null.
     * <p>
     * Similar to the mapID method in IDMapper.
     * 
	 * @param ref the entity to get cross-references for. 
     * @param targetURISpaces (Optional) Target UriSpaces that can be included in the result. 
     *    Not including any TartgetURRSpace results in all mapped/ cross-references URLs to be returned.
	 * @return A Set containing the URL (in ToURLMapping Objects) that have been mapped/ cross referenced.
	 * @throws BridgeDBException Could be because the mapping service is (temporarily) unavailable 
     */
    public Set<Mapping> mapToURLsFull(Xref ref, String profileURL, String... targetURISpaces) throws BridgeDBException;

    /**
     * Check whether an URL is known by the given mapping source. 
     * <p>
     * This is an optionally supported operation.
     * @param url URL to check
     * @return if the URL exists, false if not
     * @throws BridgeDBException if failed, UnsupportedOperationException if it's not supported by the Driver.
     */
    public boolean uriExists(String URL) throws BridgeDBException;

    /**
     * Free text search for matching symbols or identifiers.
     * 
     * Similar to the freeSearch meathod in IDMapper.
     * 
     * @param text text to search
     * @param limit up limit of number of hits
     * @return a set of hit references
     * @throws BridgeDBException if failed
     */
    public Set<String> urlSearch (String text, int limit) throws BridgeDBException;

    /**
     * Identical to IDMapper method.
     * @return capacities of the ID mapper
     */
    public IDMapperCapabilities getCapabilities();
    
    /**
     * dispose any resources (such as open database connections) associated
     * with this IDMapper.
     * Identical to IDMapper method.
     * @throws BridgeDBException if the associated resources could not be freed.
     */
    public void close() throws BridgeDBException;
    
    /**
     * Use this method to check if the IDMapper is still valid.
     * Identical to IDMapper method.
     * @return false after the close() method is called on this object, true otherwise 
     */
    public boolean isConnected();
    
    /**
     * Service to convert a uri to its BridgeDB Xref version if it is a known uri pattern
     * <p>
     * The uri will be compared to all known uri patterns and if known the primary DataSource will be returned.
     * <p>
     * Where the same uri pattern has been registered with several DataSources 
     * and no primary DataSource has been declared  
     * a Datasource based on the registered uri pattern will be/ have been created.
     * <p>
     * Where the uri does not match any registered pattern and exception is thrown.
     * This behaviour was selected as there is no known algorithm that will 
     * always correctly split a uri into prefix, id and postfix
     * 
     * @param URL A URL as a String
     * @return The Xref implementation of this URL. 
     * @throws BridgeDBException If the uri does not match a registered pattern
     */
    public Xref toXref(String uri) throws BridgeDBException;
    
    /**
     * Obtains the URLMapping information of the mapping of this id.
     * <p>
     * @See URLMappings for details of what is included in the Results.
     * <p>
     * The behaviour of this method if called with a non existance id is still to be determinded.
     * @param id Identifier of the mapping
     * @return a URLMapping with information about this mapping
     * @throws BridgeDBException 
     */
    public Mapping getMapping(int id)  throws BridgeDBException;
        
    /**
     * Gets a Sample of mappings.
     * 
     * Main use is for writing the api description page
     * @return 5 URLs that would return true for the method urlExists(URL)
     */
    public List<Mapping> getSampleMapping() throws BridgeDBException;
    
    /**
     * Obtains some general high level statistics about the data held.
     * 
     * @See OverallStatistics for an exact description of what is returned.
     * @return high level statistics
     * @throws BridgeDBException 
     */
    public OverallStatistics getOverallStatistics() throws BridgeDBException;

    /*
     * Obtains some statistics for one MappingSet in the data.
     * <p>
     * @See MappingSetInfo for details of exactky what is returned
     * @param mappingSetId Id of mapping set for which info is required
     * @return Info for the Mapping Set identified by this id
     * @throws BridgeDBException 
     */
    public MappingSetInfo getMappingSetInfo(int mappingSetId) throws BridgeDBException;
    
    /*
     * Obtains some statistics for each MappingSet in the data from the source to the target
     * <p>
     * @See MappingSetInfo for details of exactly what is returned
     * @param sourceSysCode System Code of the Source DataSource
     * @param targetSysCode System Code of the Target DataSource
     * @return Information for each Mapping Set
     * @throws BridgeDBException 
     */
     public List<MappingSetInfo> getMappingSetInfos(String sourceSysCode, String targetSysCode) throws BridgeDBException;
    
    /**
     * Obtains the Set of one or more UrlSpaces that are considered valid(have been registered) for this DataSource.
     * @param dataSource The SysCode of the DataSource 
     * @return UriSpaces (As Strings) of the UriSpace registered for this DataSource.
     * @throws BridgeDBException 
     */
    public Set<String> getUriSpaces(String dataSource) throws BridgeDBException;

	/**
	 * Obtains the Set of Profiles currently registered.
	 * @See {@link ProfileInfo} for details of exactly what is returned
	 * @return Information for each Profile
	 * @throws BridgeDbSqlException 
	 */
	public List<ProfileInfo> getProfiles() throws BridgeDBException;

	/**
	 * Obtains the information about a specific profile.
	 * @see {@link ProfileInfo} for details of exactly what is returned.
	 * @param profileURI The URI of the profile to look up
	 * @return Information about the specified profile
	 * @throws BridgeDbSqlException
	 */
	public ProfileInfo getProfile(String profileURI) throws BridgeDBException;

    /**
     * Obtains the Set of one or more UrlSpaces that are considered valid(have been registered) for the Source DataSource.
     * 
     * Looks for the mapping set, finds the Source DataSource and returns the UriSpaces for that DataSource.
     * @param mappingSet The id of the mappingSet to check.
     * @return UriSpaces (As Strings) of the UriSpace registered for this DataSource.
     * @throws BridgeDBException 
     */
    public Set<String> getSourceUriSpace(int mappingSetId) throws BridgeDBException;

    /**
     * Obtains the Set of one or more UrlSpaces that are considered valid(have been registered) for the target DataSource.
     * 
     * Looks for the mapping set, finds the Target DataSource and returns the UriSpaces for that DataSource.
     * @param mappingSet The id of the mappingSet to check.
     * @return UriSpaces (As Strings) of the UriSpace registered for this DataSource.
     * @throws BridgeDBException 
     */
    public Set<String> getTargetUriSpace(int mappingSetId) throws BridgeDBException;
    
    /**
     * Returns the SQL_COMPAT_VERSION.
     * 
     * This is mainly designed as a test method to check that the underlying SQL engine is up and running.
     * As SQL_COMPAT_VERSION is stored in a separate table with one row and one column so will be very fast.
     * 
     * @return The SQL_COMPAT_VERSION version. But more importantly a positive integer.
     */
    public int getSqlCompatVersion() throws BridgeDBException;
}
