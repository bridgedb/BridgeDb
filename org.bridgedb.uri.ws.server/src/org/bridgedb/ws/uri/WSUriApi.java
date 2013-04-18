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
package org.bridgedb.ws.uri;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;
import org.bridgedb.Xref;
import org.bridgedb.rdf.RdfConfig;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.ws.WsConstants;
import org.bridgedb.ws.WsUriConstants;

/**
 *
 * @author Christian
 */
public class WSUriApi extends WSCoreApi {
     
    private final String FIRST_URI_PARAMETER = "?" + WsUriConstants.URI + "=";
    private final String TARGET_DATASOURCE_SYSTEM_CODE_PARAMETER = "&" + WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE + "=";
    private final String TARGET_URI_PATTERN_PARAMETER = "&" + WsUriConstants.TARGET_URI_PATTERN + "=";
    
    public WSUriApi() {      
    }
            
    @Override
    protected void describeParameter(StringBuilder sb){
        super.describeParameter(sb);

        sb.append("<h3>Ops Exstension Parameters</h3>");
        sb.append("<ul>");
        sb.append("<dt><a name=\"");
                sb.append(WsUriConstants.URI);
                sb.append("\">");
                sb.append(WsUriConstants.URI);
                sb.append("</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Limits the results to ones with this URI.</li>");
            sb.append("<li>String Format</li>");
            sb.append("<li>Do NOT include the @gt and @lt seen arround URIs in RDF</li>");
            sb.append("<li>Only one ");
                    sb.append(WsUriConstants.URI);
                    sb.append(" parameters is supported.</li>");
            sb.append("</ul>");
        sb.append("<dt><a name=\"");
                sb.append(WsUriConstants.LENS_URI);
                sb.append("\">");
                sb.append(WsUriConstants.LENS_URI);
                sb.append("</a></dt>");
            sb.append("<ul>");
            sb.append("<li>If not provided the default lens is used.</li>");
            sb.append("<li>While the current API includes this parameter there is not yet any lens based data.</li>");
            sb.append("<li>It it not recommended to use this parameter except for testing until farther notice.</li>");
            sb.append("</ul>");        
        sb.append("<dt><a name=\"");
                sb.append(WsUriConstants.TARGET_URI_PATTERN);
                sb.append("\">");
                sb.append(WsUriConstants.TARGET_URI_PATTERN);
                sb.append("</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Limits the results to ones with URIs with this pattern.</li>");
            sb.append("<li>The URISpace of a URI is one defined when the mapping is loaded, not any with which the URI startWith.</li>");
            sb.append("<li>String Format</li>");
            sb.append("<li>Do NOT include the @gt and @lt seen arround URIs in RDF</li>");
            sb.append("<li>Typically there can but need not be more than one.</li>");
            sb.append("</ul>");
         sb.append("<dt><a name=\"");
                sb.append(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE);
                sb.append("\">");
                sb.append(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE);
                sb.append("</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Acts in exactly the same way as non URI based methods.</li>");
            sb.append("<li>Note: If both ");
                sb.append(WsUriConstants.TARGET_URI_PATTERN);
                sb.append(" and  ");
                sb.append(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE);
                sb.append(" are specified the result is the union of results of running this method twice with each paramteter individually.");
            sb.append("<li>String Format</li>");
            sb.append("<li>Do NOT include the @gt and @lt seen arround URIs in RDF</li>");
            sb.append("<li>Typically there can but need not be more than one.</li>");
            sb.append("</ul>");
         sb.append("</ul>");
   }

    protected final void introduce_URIMapper(StringBuilder sb, boolean freeSearchSupported) {
        sb.append("<dt><a href=\"#");
                sb.append(WsUriConstants.MAP);
                sb.append("\">");
                sb.append(WsUriConstants.MAP);
                sb.append("</a></dt>");
        sb.append("<dd>List the full mappings to this URI (or Xref)</dd>");
        sb.append("<dt><a href=\"#");
                sb.append(WsUriConstants.MAP_URI);
                sb.append("\">");
                sb.append(WsUriConstants.MAP_URI);
                sb.append("</a></dt>");
        sb.append("<dd>List the URIs that map to this/these URIs</dd>");
         sb.append("<dt>");
                sb.append(WsUriConstants.MAP_URL);
                sb.append("</a></dt>");
        sb.append("<dd>DEPRICATED: Forwards call to");
        sb.append(WsUriConstants.MAP);
        sb.append("</dd>");
        sb.append("<dt><a href=\"#");
                sb.append(WsUriConstants.URI_EXISTS);
                sb.append("\">");
                sb.append(WsUriConstants.URI_EXISTS);
                sb.append("</a></dt>");
        sb.append("<dd>State if the URI is know to the Mapping Service or not</dd>");
        if (freeSearchSupported){
            sb.append("<dt><a href=\"#");
                    sb.append(WsUriConstants.URI_SEARCH);
                    sb.append("\">");
                    sb.append(WsUriConstants.URI_SEARCH);
                    sb.append("</a></dt>");
            sb.append("<dd>Searches for URIs that have this ending.</dd>");    
        } else {
            sb.append("<dt>");
                    sb.append(WsUriConstants.URI_SEARCH);
                    sb.append("</a></dt>");

            sb.append("<dd>This is currently not supported.</dd>");            
        }
        ///toXref
        sb.append("<dt><a href=\"#");
                sb.append(WsUriConstants.MAPPING);
                sb.append("\">");
                sb.append(WsUriConstants.MAPPING);
                sb.append("</a></dt>");
        sb.append("<dd>Returns the mapping for with the specific id</dd>");
        sb.append("<dt><a href=\"#");
                sb.append(WsUriConstants.DATA_SOURCE);
                sb.append("\">");
                sb.append(WsUriConstants.DATA_SOURCE);
                sb.append("</a></dt>");
        sb.append("<dd>Returns the DataSource and associated UriSpace(s) with a specific id</dd>");
    }
    
    protected final void describe_UriMapper(StringBuilder sb, Xref sourceXref1, String sourceUri1, Xref sourceXref2, 
            String sourceUri2, String targetUriSpace2, String text, int mappingId, String sysCode, boolean freeSearchSupported) 
            throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h2>URI based methods</h2>");
        describe_map(sb, sourceXref1, sourceUri1, sourceXref2, sourceUri2, targetUriSpace2);
        describe_mapUri(sb, sourceUri1, sourceUri2, targetUriSpace2);
        describe_uriExists(sb, sourceUri1);
        if (freeSearchSupported) {
            describe_uriSearch(sb, text); 
        }
        describe_mapping(sb, mappingId);
        describe_dataSource(sb, sysCode);
    }
        
    private void describe_map(StringBuilder sb, Xref sourceXref1, String sourceUri1, Xref sourceXref2, 
            String sourceUri2, String targetUriSpace2) throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.MAP);
                sb.append("\">");
                sb.append(WsUriConstants.MAP);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>List the full mappings to this URI or Xref</li>");
            sb.append("<li>WARNING: Providing both URI and Xref parameters always causes an Exception. Even if they match!</li>");
            sb.append("<li>Note: it is not recommened to use both <a href=\"#");
                sb.append(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE);
                sb.append("\">");
                sb.append(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE);
                sb.append("</a> and <a href=\"#");
                sb.append(WsUriConstants.TARGET_URI_PATTERN);
                sb.append("\">");
                sb.append(WsUriConstants.TARGET_URI_PATTERN);
                sb.append("</a>. If both are supplied the result is the union of the calls with each individually.</li> ");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li>URI based</li>");
                sb.append("<ul>");
                    sb.append("<li><a href=\"#");
                        sb.append(WsUriConstants.URI);
                        sb.append("\">");
                        sb.append(WsUriConstants.URI);
                        sb.append("</a></li>");
                    sb.append("</ul>");
                sb.append("<li>Xref based</li>");
                sb.append("<ul>");
                    sb.append("<li><a href=\"#");
                        sb.append(ID_CODE);
                        sb.append("\">");
                        sb.append(WsConstants.ID);
                        sb.append("</a></li>");
                    sb.append("<li><a href=\"#");
                        sb.append(ID_CODE);
                        sb.append("\">");
                        sb.append(WsConstants.DATASOURCE_SYSTEM_CODE);
                        sb.append("</a></li>");
                   sb.append("</ul>");
                sb.append("</ul>");
            sb.append("<li>Optional arguments</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#");
                        sb.append(WsUriConstants.LENS_URI);
                        sb.append("\">");
                        sb.append(WsUriConstants.LENS_URI);
                        sb.append("</a></li> ");
                sb.append("<li><a href=\"#");
                        sb.append(WsUriConstants.TARGET_URI_PATTERN);
                        sb.append("\">");
                        sb.append(WsUriConstants.TARGET_URI_PATTERN);
                        sb.append("</a></li> ");
                sb.append("<li><a href=\"#");
                        sb.append(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE);
                        sb.append("\">");
                        sb.append(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE);
                        sb.append("</a></li> ");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                sb.append(WsUriConstants.MAP);
                sb.append(FIRST_URI_PARAMETER);
                sb.append(URLEncoder.encode(sourceUri1, "UTF-8"));
                sb.append("\">");
                sb.append(RdfConfig.getTheBaseURI());
                sb.append(WsUriConstants.MAP);
                sb.append(FIRST_URI_PARAMETER);
                sb.append(sourceUri1);
                sb.append("</a></li>");    
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                sb.append(WsUriConstants.MAP);
                sb.append(FIRST_URI_PARAMETER);
                sb.append(URLEncoder.encode(sourceUri2, "UTF-8"));
                sb.append(TARGET_URI_PATTERN_PARAMETER);
                sb.append(URLEncoder.encode(targetUriSpace2, "UTF-8"));
                sb.append("\">");
                sb.append(RdfConfig.getTheBaseURI());
                sb.append(WsUriConstants.MAP);
                sb.append(FIRST_URI_PARAMETER);
                sb.append(sourceUri2);
                sb.append(TARGET_URI_PATTERN_PARAMETER);
                sb.append(targetUriSpace2);
                sb.append("</a></li>");    
                sb.append("<li>There is currently no Profile Example as there in no Profile Data Loaded. </li>");
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                    StringBuilder front = new StringBuilder(WsConstants.MAP_ID);
                    StringBuilder sbInnerPure = new StringBuilder(WsConstants.MAP_ID);
                    StringBuilder sbInnerEncoded = new StringBuilder(WsConstants.MAP_ID);
                    sbInnerPure.append(FIRST_ID_PARAMETER);
                    sbInnerEncoded.append(FIRST_ID_PARAMETER);
                    sbInnerPure.append(sourceXref1.getId());
                    sbInnerEncoded.append(sourceXref1.getId());
                    sbInnerPure.append(DATASOURCE_SYSTEM_CODE_PARAMETER);
                    sbInnerEncoded.append(DATASOURCE_SYSTEM_CODE_PARAMETER);
                    sbInnerPure.append(sourceXref1.getDataSource().getSystemCode());
                    sbInnerEncoded.append(URLEncoder.encode(sourceXref1.getDataSource().getSystemCode(), "UTF-8"));
                    sbInnerPure.append(ID_PARAMETER);
                    sbInnerEncoded.append(ID_PARAMETER);
                    sbInnerPure.append(sourceXref2.getId());
                    sbInnerEncoded.append(URLEncoder.encode(sourceXref2.getId(), "UTF-8"));
                    sbInnerPure.append(DATASOURCE_SYSTEM_CODE_PARAMETER);
                    sbInnerEncoded.append(DATASOURCE_SYSTEM_CODE_PARAMETER);
                    sbInnerPure.append(sourceXref2.getDataSource().getSystemCode());
                    sbInnerEncoded.append(URLEncoder.encode(sourceXref2.getDataSource().getSystemCode(), "UTF-8"));
                    sb.append(sbInnerEncoded.toString());
                    sb.append("\">");
                    sb.append(sbInnerPure.toString());
                    sb.append("</a></li>");    
            sb.append("</ul>");
    }
    
    private void describe_mapUri(StringBuilder sb, String sourceUri1, String sourceUri2, String targetUriSpace2) 
            throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.MAP_URI);
                sb.append("\">");
                sb.append(WsUriConstants.MAP_URI);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>List the URIs that map to this URI(s)</li>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                 sb.append("<ul>");
                    sb.append("<li><a href=\"#");
                        sb.append(WsUriConstants.URI);
                        sb.append("\">");
                        sb.append(WsUriConstants.URI);
                        sb.append("</a></li>");
                    sb.append("<ul><li>In Contrast to other methods multiple values may be provided</li></ul>");  
                    sb.append("</ul>");
            sb.append("<li>Optional arguments</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#");
                        sb.append(WsUriConstants.LENS_URI);
                        sb.append("\">");
                        sb.append(WsUriConstants.LENS_URI);
                        sb.append("</a></li> ");
                sb.append("<li><a href=\"#");
                        sb.append(WsUriConstants.TARGET_URI_PATTERN);
                        sb.append("\">");
                        sb.append(WsUriConstants.TARGET_URI_PATTERN);
                        sb.append("</a></li> ");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                sb.append(WsUriConstants.MAP_URI);
                sb.append(FIRST_URI_PARAMETER);
                sb.append(URLEncoder.encode(sourceUri1, "UTF-8"));
                sb.append("\">");
                sb.append(RdfConfig.getTheBaseURI());
                sb.append(WsUriConstants.MAP_URI);
                sb.append(FIRST_URI_PARAMETER);
                sb.append(sourceUri1);
                sb.append("</a></li>");    
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                sb.append(WsUriConstants.MAP_URI);
                sb.append(FIRST_URI_PARAMETER);
                sb.append(URLEncoder.encode(sourceUri1, "UTF-8"));
                sb.append("&");
                sb.append(WsUriConstants.URI);
                sb.append("=");
                sb.append(URLEncoder.encode(sourceUri2, "UTF-8"));
                sb.append("\">");
                sb.append(RdfConfig.getTheBaseURI());
                sb.append(WsUriConstants.MAP_URI);
                sb.append(FIRST_URI_PARAMETER);
                sb.append(sourceUri1);
                sb.append("&");
                sb.append(WsUriConstants.URI);
                sb.append("=");
                sb.append(sourceUri2);
                sb.append("</a></li>");    
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                sb.append(WsUriConstants.MAP_URI);
                sb.append(FIRST_URI_PARAMETER);
                sb.append(URLEncoder.encode(sourceUri2, "UTF-8"));
                sb.append(TARGET_URI_PATTERN_PARAMETER);
                sb.append(URLEncoder.encode(targetUriSpace2, "UTF-8"));
                sb.append("\">");
                sb.append(RdfConfig.getTheBaseURI());
                sb.append(WsUriConstants.MAP_URI);
                sb.append(FIRST_URI_PARAMETER);
                sb.append(sourceUri2);
                sb.append(TARGET_URI_PATTERN_PARAMETER);
                sb.append(targetUriSpace2);
                sb.append("</a></li>");    
                sb.append("<li>There is currently no Profile Example as there in no Profile Data Loaded. </li>");
            sb.append("</ul>");
    }
    private void describe_uriExists(StringBuilder sb, String uri) throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.URI_EXISTS);
                sb.append("\">");
                sb.append(WsUriConstants.URI_EXISTS);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>State if the URI is know to the Mapping Service or not</li>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#");
                        sb.append(WsUriConstants.URI);
                        sb.append("\">");
                        sb.append(WsUriConstants.URI);
                        sb.append("</a></li>");
                sb.append("<ul>");
                sb.append("<li>Currently limited to single URI</li>");
                sb.append("</ul>");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(RdfConfig.getTheBaseURI());
                    sb.append(WsUriConstants.URI_EXISTS);
                    sb.append(FIRST_URI_PARAMETER);
                    sb.append(URLEncoder.encode(uri, "UTF-8"));
                    sb.append("\">");
                    sb.append(WsUriConstants.URI_EXISTS);
                    sb.append(FIRST_URI_PARAMETER);
                    sb.append(uri);
                    sb.append("</a></li>");    
            sb.append("</ul>");
    }
    
    private void describe_uriSearch(StringBuilder sb, String text) throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.URI_SEARCH);
                sb.append("\">");
                sb.append(WsUriConstants.URI_SEARCH);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>Searches for URIs that have this ending.</li>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#");
                        sb.append(WsUriConstants.TEXT);
                        sb.append("\">");
                        sb.append(WsUriConstants.TEXT);
                        sb.append("</a></li>");
                sb.append("<li><a href=\"#");
                        sb.append(WsUriConstants.LIMIT);
                        sb.append("\">");
                        sb.append(WsUriConstants.LIMIT);
                        sb.append("</a> (default available)</li>");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(RdfConfig.getTheBaseURI());
                    sb.append(WsUriConstants.URI_SEARCH);
                    sb.append(FIRST_TEXT_PARAMETER);
                    sb.append(URLEncoder.encode(text, "UTF-8"));
                    sb.append(LIMIT5_PARAMETER);
                    sb.append("\">");
                    sb.append(WsUriConstants.URI_SEARCH);
                    sb.append(FIRST_TEXT_PARAMETER);
                    sb.append(text);
                    sb.append(LIMIT5_PARAMETER);
                    sb.append("</a></li>");    
            sb.append("</ul>");        
    }
       
    protected final void introduce_Info(StringBuilder sb) {
        sb.append("<dt><a href=\"#");
                sb.append(WsUriConstants.GET_MAPPING_INFO);
                sb.append("\">");
                sb.append(WsUriConstants.GET_MAPPING_INFO);
                sb.append("</a></dt>");
        sb.append("<dd>Brings up a table of all the mappings in the system by URISpace</dd>");
        sb.append("<dt><a href=\"#");
                sb.append(WsUriConstants.GRAPHVIZ);
                sb.append("\">");
                sb.append(WsUriConstants.GRAPHVIZ);
                sb.append("</a></dt>");
        sb.append("<dd>Brings up the getMappingInfo as graphviz input</dd>");           
    }

    protected final void describe_Info(StringBuilder sb, Xref first, String sourceSysCode, String targetSysCode) 
            throws BridgeDBException, UnsupportedEncodingException {
        sb.append("<h2>Support methods");
        sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.GET_MAPPING_INFO);
                sb.append("\">");
                sb.append(WsUriConstants.GET_MAPPING_INFO);
                sb.append("</h3>");
                sb.append("<ul>");
            sb.append("<li>Brings up a table/List of mappings in the system by URISpaces</li>");
            sb.append("<li>Optional arguments</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#");
                        sb.append(WsUriConstants.SOURCE_DATASOURCE_SYSTEM_CODE);
                        sb.append("\">");
                        sb.append(WsUriConstants.SOURCE_DATASOURCE_SYSTEM_CODE);
                        sb.append("</a></li>");
                sb.append("<li><a href=\"#");
                        sb.append(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE);
                        sb.append("\">");
                        sb.append(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE);
                        sb.append("</a> (default available)</li>");
                sb.append("</ul>");           
            sb.append("<li>Example: <a href=\"");
                    sb.append(RdfConfig.getTheBaseURI());
                    sb.append(WsUriConstants.GET_MAPPING_INFO);
                    sb.append("\">");
                    sb.append(WsUriConstants.GET_MAPPING_INFO);
                    sb.append("</a></li>");    
            sb.append("<li>XML Example: <a href=\"");
                    sb.append(RdfConfig.getTheBaseURI());
                    sb.append(WsUriConstants.GET_MAPPING_INFO);
                    sb.append(WsUriConstants.XML);
                    sb.append("\">");
                    sb.append(WsUriConstants.GET_MAPPING_INFO);
                    sb.append(WsUriConstants.XML);
                    sb.append("</a></li>");    
            sb.append("<li>Example: <a href=\"");
                    sb.append(RdfConfig.getTheBaseURI());
                    sb.append(WsUriConstants.GET_MAPPING_INFO);
                    sb.append(FIRST_SOURCE_PARAMETER);
                    sb.append(sourceSysCode);
                    sb.append(TARGET_PARAMETER);
                    sb.append(targetSysCode);
                    sb.append("\">");
                    sb.append(WsUriConstants.GET_MAPPING_INFO);
                    sb.append(FIRST_SOURCE_PARAMETER);
                    sb.append(URLEncoder.encode(sourceSysCode, "UTF-8"));
                    sb.append(TARGET_PARAMETER);
                    sb.append(URLEncoder.encode(targetSysCode, "UTF-8"));
                    sb.append("</a></li>");    
            sb.append("</ul>");
    }  
            
    protected final void describe_Graphviz(StringBuilder sb) throws BridgeDBException, UnsupportedEncodingException {
        sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.GRAPHVIZ);
                sb.append("\">");
                sb.append(WsUriConstants.GRAPHVIZ);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>Brings up the getMappingInfo as graphviz input.</li>");
            sb.append("<li>This method is underdevelopment. Formatting suggestions from Graphviz exports highly welcome.</li>");
            sb.append("<li>This output can then used to create an image of the URISpaces mapped.</li>");
                sb.append("<ul>");
                sb.append("<li>Requires graphviz to be installed on your machine</li>");
                sb.append("<li>Save the output in a file. (ex imsMappings.dot)</li>");
                sb.append("<li>Call graphviz (ex dot -Tgif imsMappings.dot -o imsMappings.gif)</li>");
                sb.append("<li>Open output in your favourite viewer</li>");
                sb.append("</ul>");
            sb.append("<li>No arguements</li>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(RdfConfig.getTheBaseURI());
                sb.append(WsUriConstants.GRAPHVIZ);
                sb.append("\">");
                sb.append(WsUriConstants.GRAPHVIZ);
                sb.append("</a></li>");    
            sb.append("</ul>");        
    }

   private void describe_mapping(StringBuilder sb, int mappingId) throws BridgeDBException {
         sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.MAPPING);
                sb.append("\">");
                sb.append(WsUriConstants.MAPPING);
                sb.append("/id</h3>");
            sb.append("<ul>");
            sb.append("<li>Obtian a mapping</li>");
            sb.append("<li>Required arguements: </li>");
                sb.append("<ul>");
                sb.append("<li>Place the mapping's ID after the /</li> ");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                    sb.append(WsUriConstants.MAPPING);
                    sb.append("/");
                    sb.append(mappingId);
                    sb.append("\">");
                    sb.append(WsUriConstants.MAPPING);
                    sb.append("/");
                    sb.append(mappingId);
                    sb.append("</a></li>");    
            sb.append("</ul>");        
    }
   
   private void describe_dataSource(StringBuilder sb, String sysCode) 
           throws UnsupportedEncodingException, BridgeDBException {
         sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.DATA_SOURCE);
                sb.append("\">");
                sb.append(WsUriConstants.DATA_SOURCE);
                sb.append("/id</h3>");
            sb.append("<ul>");
            sb.append("<li>Obtian a dataSource</li>");
            sb.append("<li>Required arguements: </li>");
                sb.append("<ul>");
                sb.append("<li>Returns the DataSource and associated UriSpace(s) with a specific id.</li> ");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(RdfConfig.getTheBaseURI());
                    sb.append(WsUriConstants.DATA_SOURCE);
                    sb.append("/");
                    sb.append(URLEncoder.encode(sysCode, "UTF-8"));
                    sb.append("\">");
                    sb.append(WsUriConstants.DATA_SOURCE);
                    sb.append("/");
                    sb.append(sysCode);
                    sb.append("</a></li>");    
            sb.append("</ul>");        
   }

   private void describe_getOverallStatistics(StringBuilder sb) 
            throws UnsupportedEncodingException, BridgeDBException{
         sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.GET_OVERALL_STATISTICS);
                sb.append("\">");
                sb.append(WsUriConstants.GET_OVERALL_STATISTICS);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>Returns some high level statistics. </li>");
                sb.append("<ul>");
                sb.append("<li>Same as shown on homepage.</li> ");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                    sb.append(WsUriConstants.GET_OVERALL_STATISTICS);
                    sb.append("\">");
                    sb.append(WsUriConstants.GET_OVERALL_STATISTICS);
                    sb.append("</a></li>");    
            sb.append("</ul>");        
   }
 
}


