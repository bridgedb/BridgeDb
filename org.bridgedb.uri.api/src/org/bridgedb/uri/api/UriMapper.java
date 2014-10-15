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
package org.bridgedb.uri.api;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.pairs.IdSysCodePair;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.statistics.SourceInfo;
import org.bridgedb.statistics.SourceTargetInfo;
import org.bridgedb.utils.BridgeDBException;

/**
 * Base interface for all Uri mapping methods.
 * Has methods for basic functionality such as looking up cross-references and backpage text.
 * 
 * Similar to the IDMapper interface except treats Uris as first class citizens.
 * Also adds the notion of Lens.
 * To keep code size small Uris are represented as Strings.
 *
 * <p>
 * The Parameter Xref sourceRef provides the entity to get cross-references for. 
 * <br>
 * If no mappings are known for this Xref an empty set is returned.
 * Similarly apply the other parameters could result in an empty set.
 * <br>
 * If sourceRef is null, has a null Id or a null DataSource a empty set is returned.
 * 
 * <p>
 * The Parameter String sourceUri provides the entity to get cross-references for. 
 * <br>
 * If no Uri Pattern is known that matches this Uri an empty set is returned.
 * Strings which do not represent Uris will not match any Uri pattern so byu the avove rule will return an empty set.
 * If sourceUri is null, or empty an empty set is returned.
 * 
 * <p>
 * The Parameter String lensUri is used to determine which mappings to include. @see Lens
 * This allows the Mapper to include weaker mappings such as broader than, and related to.
 * <br>
 * If no Lens is specified than the default Lens is used. @see Lens.getDefaultLens()
 *
 * <p>
 * The parameters DataSource... tgtDataSources and DataSource tgtDataSource refer to the target ID types/data sources. 
 * Only Xrefs/Uris with this/these DataSource(s) are returned.
 * See below of action on nulls and empty arrays.
 * 
 * <p>
 * The parameters Set<String> tgtUriPattern refer to the target pattern. 
 * Only Uris with this/these Patterns are returned.
 * See below of action on nulls and empty arrays.
 *
 * <p>
 * The parameter graph is an alternative to tgtUriPattern(s). 
 * Instead of suppling the UriPatterns the OpenRdf context/graph name is supplied instead.
 * The UriPatterns specified in graph.properties are then used.
 * 
 * <p> Setting the parameters tgtDataSources or graph and tgtUriPatterns to null
 *     will result in all mappings being returned, (depending only on the other parameters)
 *     A empty set (for tgtDataSources or tgtUriPatterns) is also ignored.
 * 
 * <br>
 * Individual nulls in the array are ignored. 
 * In other words the same result is returned as if the array did not have the null value.
 * However, A non null Set with just one or more null returns an empty set.
 * 
 *<p>
 * The parameter includeXrefResults will determine if the mapping result will include Xrefs
 * If the source is a Xref or if the target DataSources are provided 
 *    the result will always include Xref information. 
 * However for pure Uri and UriPattern based calls the Xref information 
 *    is only added if specifically requested.
 * If null includeXrefResults will be assumed as false,
 *
 * <p>
 * The Parameter includeUriResults will determine if the mapping result will include Uris.
 * If the source is an URI or if the target UriPatterns or graph are provided 
 *    the result will always include URI information. 
 * However for pure Xref target DataSources based calls the URI information 
 *    is only added if specifically requested. 
 * If null includeUriResults will be assumed as false,
 *
 * <p>
 * The Parameter allRoutes will instruct the mapper to find all routes between A and B
 * The default behaviour is to stop as soon as a single route to a target has been found,
 *     ignoring any farther routes (which will normally be of the same length or longer)
 * However setting allRoutes will cause the mapper to include all routes, 
 *     this could slow the call down and add extra near duplicate mappings.
 * If null allRoutes will be assumed as false, even if showVias is set.
 * 
 * @param showVias @see Class java docs

 */
public interface UriMapper extends IDMapper{
    
    /**
     * Get all cross-references for the given entity, restricting the
     * result to contain only references from the given Lens and set of data sources,
     * and only results which match the given Lens. 
     * @param sourceXref @see Class java docs. 
     * @param lensUri @see Class java docs. 
     * @param tgtDataSources @see Class java docs. 
     *    
     * @return A Set containing the cross references, or an empty
     * Set when no cross references could be found. This method does not return null.
     * @throws IDMapperException if the mapping service is (temporarily) unavailable 
     */
    public Set<Xref> mapID(Xref sourceXref, String lensUri, Collection<DataSource> tgtDataSources) throws BridgeDBException;

     /**
	 * Get all Uris mapped to the given Uri, restricting the
	 * result to contain only references which match the given UriPatterns and Lens.
     * 
	 * @param sourceUri @see Class java docs. 
     * @param lensUri @see Class java docs. 
     * @param graph  @see Class java docs. 
     * @param tgtUriPatterns @see Class java docs.
	 * @return A Set containing the Uris, or an empty
	 * Set when no cross references could be found. This method does not return null.
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
     */
    public Set<String> mapUri(String sourceUri, String lensUri, String graph, Collection<String> tgtUriPatterns) 
            throws BridgeDBException;

    /**
	 * Get all Uris mapped to the given entity, restricting the
	 * result to contain only references which match the UriPatterns and Lens.
     * 
	 * @param sourceXref @see Class java docs. 
     * @param lensUri @see Class java docs. 
     * @param graph  @see Class java docs. 
     * @param tgtUriPatterns @see Class java docs.
	 * @return A Set containing the cross references, or an empty
	 * Set when no cross references could be found. This method does not return null.
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
     */
    public Set<String> mapUri(Xref sourceXref, String lensUri, String graph, Collection<String> tgtUriPatterns) 
            throws BridgeDBException;

    public MappingsBySysCodeId mapUriBySysCodeId (String sourceUri, String lensUri, String graph, Collection<String> tgtUriPatterns) 
            throws BridgeDBException;

    public MappingsBySysCodeId mapUriBySysCodeId (Collection<String> sourceUri, String lensUri, String graph, Collection<String> tgtUriPatterns) 
            throws BridgeDBException;

    public MappingsBySet mapBySet(Collection<String> sourceUris, String lensUri, String graph, Collection<String> tgtUriPatterns) 
           throws BridgeDBException;

    /**
    * Get the set of mappings based the parameters supplied.
    * 
    * @param sourceXref @see Class java docs. 
    * @param lensUri @see Class java docs. 
    * @param includeUriResults @see Class java docs
    * @param allRoutes @see Class java docs.
    * @param showVias @see Class java docs
    * @param tgtDataSources @see Class java docs. 
    *    
    * @return A Set containing the mappings or an empty et when no cross references could be found. 
    *    This method does not return null.
    * @throws IDMapperException if the mapping service is (temporarily) unavailable 
    *///Boolean allRoutes, boolean showVias, 
    public Set<Mapping> mapFull(Xref sourceXref, String lensUri, 
            Boolean includeUriResults, 
            Collection<DataSource> tgtDataSources) 
            throws BridgeDBException;

    /**
     * Get the set of mappings based the parameters supplied.
     * 
     * @param sourceXref @see Class java docs. 
     * @param lensUri @see Class java docs. 
     * @param allRoutes @see Class java docs.
     * @param showVias @see Class java docs
     * @param graph  @see Class java docs. 
     * @param tgtUriPatterns @see Class java docs. 
     *    
     * @return A Set containing the mappings or an empty et when no cross references could be found. 
     *    This method does not return null.
     * @throws IDMapperException if the mapping service is (temporarily) unavailable 
     */
    public Set<Mapping> mapFull(Xref sourceXref, String lensUri,
            //Boolean allRoutes, Boolean showVias, 
            String graph, Collection<String> tgtUriPatterns) 
            throws BridgeDBException;

    /**
     * Get the set of mappings based the parameters supplied.
     * 
     * @param sourceUri @see Class java docs. 
     * @param lensUri @see Class java docs. 
     * @param allRoutes @see Class java docs.
     * @param showVias @see Class java docs
     * @param tgtDataSources @see Class java docs. 
     *    
     * @return A Set containing the mappings or an empty et when no cross references could be found. 
     *    This method does not return null.
     * @throws IDMapperException if the mapping service is (temporarily) unavailable 
     */
    public Set<Mapping> mapFull(String sourceUri, String lensUri, 
            //Boolean allRoutes, Boolean showVias, 
            Collection<DataSource> tgtDataSources) 
            throws BridgeDBException;

    /**
     * Get the set of mappings based the parameters supplied.
     * 
     * @param sourceUri @see Class java docs. 
     * @param lensUri @see Class java docs. 
     * @param includeXrefResults @see Class java docs.
     * @param allRoutes @see Class java docs.
     * @param showVias @see Class java docs
     * @param graph  @see Class java docs. 
     * @param tgtUriPatterns @see Class java docs. 
     *    
     * @return A Set containing the mappings or an empty et when no cross references could be found. 
     *    This method does not return null.
     * @throws IDMapperException if the mapping service is (temporarily) unavailable 
     *///Boolean allRoutes, Boolean showVias,
    public Set<Mapping> mapFull(String sourceUri, String lensUri, 
            Boolean includeXrefResults,  
            String graph, Collection<String> tgtUriPatterns)
            throws BridgeDBException;

    /**
     * Check whether an URI is known by the given mapping source. 
     * <p>
     * This is an optionally supported operation.
     * @param uri URI to check
     * @return if the URI exists, false if not
     * @throws BridgeDBException if failed, UnsupportedOperationException if it's not supported by the Driver.
     */
    public boolean uriExists(String uri) throws BridgeDBException;

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
    public Set<String> uriSearch (String text, int limit) throws BridgeDBException;

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
     * @param uri A uri as a String
     * @return The Xref implementation of this uri, or null if it is not known 
     * @throws BridgeDBException Only for an SQl exception
     */
    public Xref toXref(String uri) throws BridgeDBException;
    
    public IdSysCodePair toIdSysCodePair(String uri) throws BridgeDBException;
    
    /*
     * Gets a Sample of mappings.
     * 
     * Main use is for writing the api description page
     * @return 5 mapping which includes both source and traget Uris
     */
   public List<Mapping> getSampleMapping() throws BridgeDBException;
    
    /**
     * Obtains some general high level statistics about the data held.
     * 
     * @See OverallStatistics for an exact description of what is returned.
     * @param lensUri  @see Class java docs.
     * @return high level statistics
     * @throws BridgeDBException 
     */
    public OverallStatistics getOverallStatistics(String lensUri) throws BridgeDBException;

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
     * @param sourceSysCode  @see Class java docs.
     * @param targetSysCode System Code of the Target DataSource
     * @param lensUri (optional) Uri for the Lens (may be just the Id part of the URI
     * @return Information for each Mapping Set
     * @throws BridgeDBException 
     */
     public List<MappingSetInfo> getMappingSetInfos(String sourceSysCode, String targetSysCode, String lensUri) throws BridgeDBException;
    
    /*
     * Obtains some statistics for each Source in the data 
     * <p>
     * @See MappingSourceInfo for details of exactly what is returned
     * @param lensUri (optional) Uri for the Lens (may be just the Id part of the URI
     * @return Information for each Mapping Set
     * @throws BridgeDBException 
     */
    public List<SourceInfo> getSourceInfos(String lensUri) throws BridgeDBException;
    
    /*
     * Obtains some statistics for each Target that this Source maps to in the data 
     * <p>
     * @See MappingSourceInfo for details of exactly what is returned
     * @param sourceSysCode System Code of the Source DataSource
     * @param lensUri (optional) Uri for the Lens (may be just the Id part of the URI
     * @return Information for each Mapping Set
     * @throws BridgeDBException 
     */
    public List<SourceTargetInfo> getSourceTargetInfos(String sourceSysCode, String lensUri) throws BridgeDBException;
    
    /**
     * Obtains the Set of one or more UriPatterns that are considered valid(have been registered) for this DataSource.
     * @param dataSource The SysCode of the DataSource 
     * @return UriPatterns (As Strings) in the nameSpace + "$id" + postfix format.
     * @throws BridgeDBException 
     */
    public Set<String> getUriPatterns(String dataSource) throws BridgeDBException;

    /**
     * Returns the SQL_COMPAT_VERSION.
     * 
     * This is mainly designed as a test method to check that the underlying SQL engine is up and running.
     * As SQL_COMPAT_VERSION is stored in a separate table with one row and one column so will be very fast.
     * 
     * @return The SQL_COMPAT_VERSION version. But more importantly a positive integer.
     */
    public int getSqlCompatVersion() throws BridgeDBException;
    
    public Set<String> getJustifications() throws BridgeDBException;

}
