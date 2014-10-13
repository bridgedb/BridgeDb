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
package org.bridgedb.uri.ws;

import java.util.List;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.ws.WSCoreInterface;

/**
 *
 * @author Christian
 */
public interface WSUriInterface extends WSCoreInterface{

    /**
     *
     * @param id
     * @param scrCode
     * @param uri
     * @param lensUri
     * @param targetCodes
     * @param graph
     * @param targetUriPattern
     * @return A MappingsBean wrapped in a Response
     * @throws BridgeDBException
     */
    public Response map(String id, String scrCode, String uri, String lensUri, 
            Boolean includeXrefResults, Boolean includeUriResults,
            List<String> targetCodes, String graph, List<String> targetUriPattern) throws BridgeDBException;

    /**
     *
     * @param uri
     * @param lensUri
     * @param graph
     * @param targetUriPattern
     * @return A MappingsBySetBean wrapped in a Response
     * @throws BridgeDBException
     */
    public Response mapBySet(List<String> uri, String lensUri, String graph, List<String> targetUriPattern) 
            throws BridgeDBException;

    /**
     *
     * @param Uri
     * @return An UriExistsBean wrapped in a Response
     * @throws BridgeDBException
     */
    public Response UriExists(String Uri) throws BridgeDBException;

    /**
     *
     * @param text
     * @param limitString
     * @return An UriSearchBean wrapped in a Response
     * @throws BridgeDBException
     */
    public Response UriSearch(String text, String limitString) throws BridgeDBException;

    /**
     *
     * @param Uri
     * @return A XrefBean wrapped in a Response
     * @throws BridgeDBException
     */
    public Response toXref(String Uri) throws BridgeDBException;

    /**
     *
     * @param lensUri
     * @return An OverallStatisticsBean wrapped in a Response
     * @throws BridgeDBException
     */
    public Response getOverallStatistics(String lensUri) throws BridgeDBException;

    /**
     *
     * @param sourceSysCode
     * @param targetSysCode
     * @param lensUri
     * @return A MappingSetInfosBean wrapped in a Response
     * @throws BridgeDBException
     */
    public Response getMappingSetInfos(String sourceSysCode, String targetSysCode, String lensUri) 
            throws BridgeDBException;

    public Response getSourceInfos(String lensUri)throws BridgeDBException;

    public Response getSourceTargetInfos(String sourceSysCode, String lensUri) throws BridgeDBException;

    /**
     *
     * @param mappingSetId
     * @return A MappingSetInfoBean wrapped in a Response
     * @throws BridgeDBException
     */
    public Response getMappingSetInfo(String mappingSetId) throws BridgeDBException;

    /**
     *
     * @param dataSource
     * @return A DataSourceUriPatternBean wrapped in a Response
     * @throws BridgeDBException
     */
    public Response getDataSource(String dataSource) throws BridgeDBException;
    
    //public ValidationBean validateString(String info, String mimeTypee, String storeType, String validationType, 
    //        String includeWarnings) throws BridgeDBException;

    /**
     * A simple method to test WebService and underlying SQl engine are running.
     * @return A String wrapped in a Response
     * @throws BridgeDBException
     */
    public Response getSqlCompatVersion() throws BridgeDBException;
     
    /**
     *
     * @param uris
     * @param lensUri
     * @param graph
     * @param targetUriPatterns
     * @return A UriMappings wrapped in a Response
     * @throws BridgeDBException
     */
    public Response mapUri(List<String> uris, String lensUri, String graph, List<String> targetUriPatterns) 
            throws BridgeDBException;
    
    /**
     *
     * @param id
     * @return A LensBean wrapped in a Response
     * @throws BridgeDBException
     */
    public Response getLens(@PathParam("id") String id) throws BridgeDBException;

 	/**
     *
     * @param lensUri
     * @return A LensesBean wrapped in a Response
     * @throws BridgeDBException
     */
    public Response getLenses(String lensUri, String lensGroup) throws BridgeDBException;


 }
