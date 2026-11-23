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
     * @param id - identifier
     * @param scrCode - source code
     * @param uri - the uri link
     * @param lensUri - the lens uri
     * @param targetCodes - target system code
     * @param graph - graph
     * @param includeXrefResults - whether include xref results
     * @param targetUriPattern - target uri pattern
     * @return A MappingsBean wrapped in a Response
     * @throws BridgeDBException - exception class
     */
    public Response map(String id, String scrCode, String uri, String lensUri, 
            Boolean includeXrefResults, Boolean includeUriResults,
            List<String> targetCodes, String graph, List<String> targetUriPattern) throws BridgeDBException;

    /**
     *
     * @param uri - the uri link
     * @param lensUri - the lens uri
     * @param graph - graph
     * @param targetUriPattern - target uri pattern
     * @return A MappingsBySetBean wrapped in a Response
     * @throws BridgeDBException - exception class
     */
    public Response mapBySet(List<String> uri, String lensUri, String graph, List<String> targetUriPattern) 
            throws BridgeDBException;

    /**
     *
     * @param Uri - the uri
     * @return An UriExistsBean wrapped in a Response
     * @throws BridgeDBException - the exception class
     */
    public Response UriExists(String Uri) throws BridgeDBException;

    /**
     *
     * @param text - search text
     * @param limitString - limit string
     * @return An UriSearchBean wrapped in a Response
     * @throws BridgeDBException - exception class
     */
    public Response UriSearch(String text, String limitString) throws BridgeDBException;

    /**
     *
     * @param Uri - uri link
     * @return A XrefBean wrapped in a Response
     * @throws BridgeDBException - exception class
     */
    public Response toXref(String Uri) throws BridgeDBException;

    /**
     * Converts a Xref (represented as id and DataSource scrCode to the known
     * @param id - identifier
     * @param scrCode - database code
     * @return the response mapped to uris
     * @throws BridgeDBException  - exception class
     */
    public Response toUris(String id, String scrCode) throws BridgeDBException;

    /**
     *
     * @param lensUri - lens uri
     * @return An OverallStatisticsBean wrapped in a Response
     * @throws BridgeDBException - exception class
     */
    public Response getOverallStatistics(String lensUri) throws BridgeDBException;

    /**
     *
     * @param sourceSysCode - source database system code
     * @param targetSysCode - target database system code
     * @param lensUri - lens uri
     * @return A MappingSetInfosBean wrapped in a Response
     * @throws BridgeDBException - exception class
     */
    public Response getMappingSetInfos(String sourceSysCode, String targetSysCode, String lensUri) 
            throws BridgeDBException;

    public Response getSourceInfos(String lensUri)throws BridgeDBException;

    public Response getSourceTargetInfos(String sourceSysCode, String lensUri) throws BridgeDBException;

    /**
     *
     * @param mappingSetId - mapping set identifier
     * @return A MappingSetInfoBean wrapped in a Response
     * @throws BridgeDBException - exception class
     */
    public Response getMappingSetInfo(String mappingSetId) throws BridgeDBException;

    /**
     *
     * @param dataSource - datasource name
     * @return A DataSourceUriPatternBean wrapped in a Response
     * @throws BridgeDBException - exception class
     */
    public Response getDataSource(String dataSource) throws BridgeDBException;
    
    //public ValidationBean validateString(String info, String mimeTypee, String storeType, String validationType, 
    //        String includeWarnings) throws BridgeDBException;

    /**
     * A simple method to test WebService and underlying SQl engine are running.
     * @return A String wrapped in a Response
     * @throws BridgeDBException - exception class
     */
    public Response getSqlCompatVersion() throws BridgeDBException;
     
    /**
     *
     * @param uris - URI link
     * @param lensUri - lens URI
     * @param graph - graph
     * @param targetUriPatterns - target URI pattenrs
     * @return A UriMappings wrapped in a Response
     * @throws BridgeDBException - exception class
     */
    public Response mapUri(List<String> uris, String lensUri, String graph, List<String> targetUriPatterns) 
            throws BridgeDBException;
    
    /**
     *
     * @param id - identifier
     * @return A LensBean wrapped in a Response
     * @throws BridgeDBException - exception class
     */
    public Response getLens(@PathParam("id") String id) throws BridgeDBException;

 	/**
     *
     * @param lensUri - lens URI
     * @param lensGroup - lens group
     * @return A LensesBean wrapped in a Response
     * @throws BridgeDBException - exception class
     */
    public Response getLenses(String lensUri, String lensGroup) throws BridgeDBException;


 }
