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
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.rdf.RdfConfig;
import org.bridgedb.ws.WsUriConstants;

/**
 *
 * @author Christian
 */
public class WSUriApi extends WSCoreApi {
     
    private final String FIRST_URL_PARAMETER = "?" + WsUriConstants.URL + "=";
    private final String TARGET_URI_SPACE_PARAMETER = "&" + WsUriConstants.TARGET_URI_SPACE + "=";
    
    public WSUriApi() {      
    }
            
    protected void describeParameter(StringBuilder sb){
        super.describeParameter(sb);

        sb.append("<h3>Ops Exstension Parameters</h3>");
        sb.append("<ul>");
        sb.append("<dt><a name=\"");
                sb.append(WsUriConstants.URL);
                sb.append("\">");
                sb.append(WsUriConstants.URL);
                sb.append("</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Limits the results to ones with this URL.</li>");
            sb.append("<li>String Format</li>");
            sb.append("<li>Do NOT include the @gt and @lt seen arround URIs in RDF</li>");
            sb.append("<li>Only one ");
                    sb.append(WsUriConstants.URL);
                    sb.append(" parameters is supported.</li>");
            sb.append("</ul>");
         sb.append("<dt><a name=\"");
                sb.append(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE);
                sb.append("\">");
                sb.append(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE);
                sb.append("</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Limits the results to ones with URLs in this/these URISpace(s) as a target.</li>");
            sb.append("<li>The URISpace of a URL is one defined when the mapping is loaded, not any with which the URL startWith.</li>");
            sb.append("<li>String Format</li>");
            sb.append("<li>Do NOT include the @gt and @lt seen arround URIs in RDF</li>");
            sb.append("<li>Typically there can but need not be more than one.</li>");
            sb.append("</ul>");
         sb.append("</ul>");
   }

    protected final void introduce_URLMapper(StringBuilder sb, boolean freeSearchSupported) {
        sb.append("<dt><a href=\"#");
                sb.append(WsUriConstants.MAP_URL);
                sb.append("\">");
                sb.append(WsUriConstants.MAP_URL);
                sb.append("</a></dt>");
        sb.append("<dd>List the URLs that map to this URL</dd>");
        sb.append("<dt><a href=\"#");
                sb.append(WsUriConstants.URL_EXISTS);
                sb.append("\">");
                sb.append(WsUriConstants.URL_EXISTS);
                sb.append("</a></dt>");
        sb.append("<dd>State if the URL is know to the Mapping Service or not</dd>");
        if (freeSearchSupported){
            sb.append("<dt><a href=\"#");
                    sb.append(WsUriConstants.URL_SEARCH);
                    sb.append("\">");
                    sb.append(WsUriConstants.URL_SEARCH);
                    sb.append("</a></dt>");
            sb.append("<dd>Searches for URLs that have this ending.</dd>");    
        } else {
            sb.append("<dt>");
                    sb.append(WsUriConstants.URL_SEARCH);
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
        ///getSampleSourceURLs
        //getMappingStatistics
        //getMappingSetInfos
        sb.append("<dt><a href=\"#");
                sb.append(WsUriConstants.DATA_SOURCE);
                sb.append("\">");
                sb.append(WsUriConstants.DATA_SOURCE);
                sb.append("</a></dt>");
        sb.append("<dd>Returns the DataSource and associated UriSpace(s) with a specific id</dd>");
    }
    
    protected final void describe_URLMapper(StringBuilder sb, String URL1, String URL2, Set<String> URI2Spaces, 
            String text, int mappingId, String sysCode, boolean freeSearchSupported) 
            throws UnsupportedEncodingException, IDMapperException{
        sb.append("<h2>URL based methods</h2>");
        describe_mapURL(sb, URL1, URL2, URI2Spaces);
        describe_URLExists(sb, URL1);
        if (freeSearchSupported) {
            describe_URLSearch(sb, text); 
        }
        describe_mapping(sb, mappingId);
        describe_dataSource(sb, sysCode);
    }
        
    private void describe_mapURL(StringBuilder sb, String URL1, String URL2, Set<String> URI2Spaces) 
            throws UnsupportedEncodingException, IDMapperException{
        sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.MAP_URL);
                sb.append("\">");
                sb.append(WsUriConstants.MAP_URL);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>List the URLs that map to this URL</li>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#");
                        sb.append(WsUriConstants.URL);
                        sb.append("\">");
                        sb.append(WsUriConstants.URL_EXISTS);
                        sb.append("</a></li>");
                sb.append("</ul>");
            sb.append("<li>Optional arguments</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#");
                        sb.append(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE);
                        sb.append("\">");
                        sb.append(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE);
                        sb.append("</a></li> ");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                sb.append(WsUriConstants.MAP_URL);
                sb.append(FIRST_URL_PARAMETER);
                sb.append(URLEncoder.encode(URL1, "UTF-8"));
                sb.append("\">");
                sb.append(RdfConfig.getTheBaseURI());
                sb.append(WsUriConstants.MAP_URL);
                sb.append(FIRST_URL_PARAMETER);
                sb.append(URL1);
                sb.append("</a></li>");    
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                sb.append(WsUriConstants.MAP_URL);
                sb.append(FIRST_URL_PARAMETER);
                sb.append(URLEncoder.encode(URL2, "UTF-8"));
                for (String URISpace:URI2Spaces){
                    sb.append(TARGET_URI_SPACE_PARAMETER);
                    sb.append(URLEncoder.encode(URISpace, "UTF-8"));
                }
                sb.append("\">");
                sb.append(RdfConfig.getTheBaseURI());
                sb.append(WsUriConstants.MAP_URL);
                sb.append(FIRST_URL_PARAMETER);
                sb.append(URL2);
                for (String URISpace:URI2Spaces){
                    sb.append(TARGET_URI_SPACE_PARAMETER);
                    sb.append(URISpace);
                }
                sb.append("</a></li>");    
            sb.append("</ul>");
    }
    
    private void describe_URLExists(StringBuilder sb, String URL) throws UnsupportedEncodingException, IDMapperException{
        sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.URL_EXISTS);
                sb.append("\">");
                sb.append(WsUriConstants.URL_EXISTS);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>State if the URL is know to the Mapping Service or not</li>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#");
                        sb.append(WsUriConstants.URL);
                        sb.append("\">");
                        sb.append(WsUriConstants.URL);
                        sb.append("</a></li>");
                sb.append("<ul>");
                sb.append("<li>Currently limited to single URI</li>");
                sb.append("</ul>");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(RdfConfig.getTheBaseURI());
                    sb.append(WsUriConstants.URL_EXISTS);
                    sb.append(FIRST_URL_PARAMETER);
                    sb.append(URLEncoder.encode(URL, "UTF-8"));
                    sb.append("\">");
                    sb.append(WsUriConstants.URL_EXISTS);
                    sb.append(FIRST_URL_PARAMETER);
                    sb.append(URL);
                    sb.append("</a></li>");    
            sb.append("</ul>");
    }
    
    private void describe_URLSearch(StringBuilder sb, String URL) throws UnsupportedEncodingException, IDMapperException{
        sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.URL_SEARCH);
                sb.append("\">");
                sb.append(WsUriConstants.URL_SEARCH);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>Searches for URLs that have this ending.</li>");
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
                    sb.append(WsUriConstants.URL_SEARCH);
                    sb.append(FIRST_TEXT_PARAMETER);
                    sb.append(URLEncoder.encode(URL, "UTF-8"));
                    sb.append(WsUriConstants.URL_SEARCH);
                    sb.append(LIMIT5_PARAMETER);
                    sb.append("\">");
                    sb.append(WsUriConstants.URL_SEARCH);
                    sb.append(FIRST_TEXT_PARAMETER);
                    sb.append(URL);
                    sb.append(WsUriConstants.URL_SEARCH);
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

    protected final void describe_Info(StringBuilder sb, Xref first, Set<Xref> firstMaps) throws IDMapperException, UnsupportedEncodingException {
        sb.append("<h2>URL based methods");
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
                    String tgtSysCode = firstMaps.iterator().next().getDataSource().getSystemCode();
                    sb.append(RdfConfig.getTheBaseURI());
                    sb.append(WsUriConstants.GET_MAPPING_INFO);
                    sb.append(FIRST_SOURCE_PARAMETER);
                    sb.append(first.getDataSource().getSystemCode());
                    sb.append(TARGET_PARAMETER);
                    sb.append(firstMaps.iterator().next().getDataSource().getSystemCode());
                    sb.append("\">");
                    sb.append(WsUriConstants.GET_MAPPING_INFO);
                    sb.append(FIRST_SOURCE_PARAMETER);
                    sb.append(URLEncoder.encode(first.getDataSource().getSystemCode(), "UTF-8"));
                    sb.append(TARGET_PARAMETER);
                    sb.append(URLEncoder.encode(firstMaps.iterator().next().getDataSource().getSystemCode(), "UTF-8"));
                    sb.append("</a></li>");    
            sb.append("</ul>");
    }  
            
    protected final void describe_Graphviz(StringBuilder sb) throws IDMapperException, UnsupportedEncodingException {
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

   private void describe_mapping(StringBuilder sb, int mappingId) throws IDMapperException {
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
           throws UnsupportedEncodingException, IDMapperException {
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
            throws UnsupportedEncodingException, IDMapperException{
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


