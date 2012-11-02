// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
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
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.statistics.ProfileInfo;

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
	 * @throws IDMapperException Could be because the mapping service is (temporarily) unavailable 
     */
    public Map<String, Set<String>> mapURL(Collection<String> sourceURLs, String profileURL, String... targetURISpaces) throws IDMapperException;

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
	 * @throws IDMapperException Could be because the mapping service is (temporarily) unavailable 
     */
    public Set<URLMapping> mapURLFull(String URL, String profileURL, String... targetURISpaces) throws IDMapperException;

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
	 * @throws IDMapperException Could be because the mapping service is (temporarily) unavailable 
	 */
	public Set<String> mapURL (String URL, String profileURL, String... targetURISpaces) throws IDMapperException;
	
    /**
     * Check whether an URL is known by the given mapping source. 
     * <p>
     * This is an optionally supported operation.
     * @param url URL to check
     * @return if the URL exists, false if not
     * @throws IDMapperException if failed, UnsupportedOperationException if it's not supported by the Driver.
     */
    public boolean uriExists(String URL) throws IDMapperException;

    /**
     * Free text search for matching symbols or identifiers.
     * 
     * Similar to the freeSearch meathod in IDMapper.
     * 
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
    
    /**
     * Service to convert a URL to its BridgeDB Xref version.
     * <p>
     * Behaviour of this method in cases where the UriSpace of the URL has not yet been registered is still to be dettermined.
     * @param URL A URL as a String
     * @return The Xref implementation of this URL. 
     * @throws IDMapperException 
     */
    public Xref toXref(String URL) throws IDMapperException;
    
    /**
     * Obtains the URLMapping information of the mapping of this id.
     * <p>
     * @See URLMappings for details of what is included in the Results.
     * <p>
     * The behaviour of this method if called with a non existance id is still to be determinded.
     * @param id Identifier of the mapping
     * @return a URLMapping with information about this mapping
     * @throws IDMapperException 
     */
    public URLMapping getMapping(int id)  throws IDMapperException;
    
    /**
     * Gets a Sample of Source URls.
     * 
     * Main use is for writing the api description page
     * @return 5 URLs that would return true for the method urlExists(URL)
     */
    public Set<String> getSampleSourceURLs() throws IDMapperException;
    
    /**
     * Obtains some general high level statistics about the data held.
     * 
     * @See OverallStatistics for an exact description of what is returned.
     * @return high level statistics
     * @throws IDMapperException 
     */
    public OverallStatistics getOverallStatistics() throws IDMapperException;

    /*
     * Obtains some statistics for one MappingSet in the data.
     * <p>
     * @See MappingSetInfo for details of exactky what is returned
     * @param mappingSetId Id of mapping set for which info is required
     * @return Info for the Mapping Set identified by this id
     * @throws IDMapperException 
     */
    public MappingSetInfo getMappingSetInfo(int mappingSetId) throws IDMapperException;
    
    /**
     * Obtains some statistics for each MappingSet in the data.
     * <p>
     * @See MappingSetInfo for details of exactly what is returned
     * @return Information for each Mapping Set
     * @throws IDMapperException 
     */
    public List<MappingSetInfo> getMappingSetInfos() throws IDMapperException;
    //There is currently no method for obtaining a single mapping set info but this can be added if required.
    
    /**
     * Obtains the Set of one or more UrlSpaces that are considered valid(have been registered) for this DataSource.
     * @param dataSource The SysCode of the DataSource 
     * @return UriSpaces (As Strings) of the UriSpace registered for this DataSource.
     * @throws IDMapperException 
     */
    public Set<String> getUriSpaces(String dataSource) throws IDMapperException;

	/**
	 * Obtains the Set of Profiles currently registered.
	 * @See {@link ProfileInfo} for details of exactly what is returned
	 * @return Information for each Profile
	 * @throws BridgeDbSqlException 
	 */
	public List<ProfileInfo> getProfiles() throws BridgeDbSqlException;

	/**
	 * Obtains the information about a specific profile.
	 * @see {@link ProfileInfo} for details of exactly what is returned.
	 * @param profileURI The URI of the profile to look up
	 * @return Information about the specified profile
	 * @throws BridgeDbSqlException
	 */
	public ProfileInfo getProfile(String profileURI) throws BridgeDbSqlException;

    /**
     * Obtains the Set of one or more UrlSpaces that are considered valid(have been registered) for the Source DataSource.
     * 
     * Looks for the mapping set, finds the Source DataSource and returns the UriSpaces for that DataSource.
     * @param mappingSet The id of the mappingSet to check.
     * @return UriSpaces (As Strings) of the UriSpace registered for this DataSource.
     * @throws IDMapperException 
     */
    public Set<String> getSourceUriSpace(int mappingSetId) throws IDMapperException;

    /**
     * Obtains the Set of one or more UrlSpaces that are considered valid(have been registered) for the target DataSource.
     * 
     * Looks for the mapping set, finds the Target DataSource and returns the UriSpaces for that DataSource.
     * @param mappingSet The id of the mappingSet to check.
     * @return UriSpaces (As Strings) of the UriSpace registered for this DataSource.
     * @throws IDMapperException 
     */
    public Set<String> getTargetUriSpace(int mappingSetId) throws IDMapperException;

}
