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
package org.bridgedb.ws;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;
import org.bridgedb.IDMapperException;
import org.bridgedb.rdf.RdfConfig;
import org.bridgedb.rdf.RdfFactory;

/**
 *
 * @author Christian
 */
public class WSOpsApi extends WSCoreApi {
        
    public WSOpsApi() {      
    }
            
    protected void describeParameter(StringBuilder sb){
        super.describeParameter(sb);

        sb.append("<h3>Ops Exstension Parameters</h3>");
        sb.append("<ul>");
        sb.append("<dt><a name=\"URL\">URL</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Limits the results to ones with this URL.</li>");
            sb.append("<li>String Format</li>");
            sb.append("<li>Do NOT include the @gt and @lt seen arround URIs in RDF</li>");
            sb.append("<li>Only one URL parameters is supported.</li>");
            sb.append("</ul>");
         sb.append("<dt><a name=\"targetURISpace\">targetURISpace</a></dt>");
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
        sb.append("<dt><a href=\"#mapURL\">mapURL</a></dt>");
        sb.append("<dd>List the URLs that map to this URL</dd>");
        sb.append("<dt><a href=\"#URLExists\">URLExists</a></dt>");
        sb.append("<dd>State if the URL is know to the Mapping Service or not</dd>");
        if (freeSearchSupported){
            sb.append("<dt><a href=\"#URLSearch\">URLSearch</a></dt>");
            sb.append("<dd>Searches for URLs that have this ending.</dd>");    
        } else {
            sb.append("<dt>URLSearch</dt>");
            sb.append("<dd>This is currently not supported.</dd>");            
        }
        ///toXref
        sb.append("<dt><a href=\"#mapping\">mapping</a></dt>");
        sb.append("<dd>Returns the mapping for with the specific id</dd>");
        ///getSampleSourceURLs
        //getMappingStatistics
        //getMappingSetInfos
        sb.append("<dt><a href=\"#dataSource\">dataSource</a></dt>");
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
        sb.append("<h3><a name=\"mapURL\">mapURL</h3>");
            sb.append("<ul>");
            sb.append("<li>List the URLs that map to this URL</li>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#URL\">URL</a></li>");
                sb.append("</ul>");
            sb.append("<li>Optional arguments</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#targetURISpace\">targetURISpace</a></li> ");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                sb.append("mapURL?URL=");
                sb.append(URLEncoder.encode(URL1, "UTF-8"));
                sb.append("\">");
                sb.append(RdfConfig.getTheBaseURI());
                sb.append("mapURL?URL=");
                sb.append(URL1);
                sb.append("</a></li>");    
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                sb.append("mapURL?URL=");
                sb.append(URLEncoder.encode(URL2, "UTF-8"));
                for (String URISpace:URI2Spaces){
                    sb.append("&targetURISpace=");
                    sb.append(URLEncoder.encode(URISpace, "UTF-8"));
                }
                sb.append("\">");
                sb.append(RdfConfig.getTheBaseURI());
                sb.append("mapURL?URL=");
                sb.append(URL2);
                for (String URISpace:URI2Spaces){
                    sb.append("&targetURISpace=");
                    sb.append(URISpace);
                }
                sb.append("</a></li>");    
            sb.append("</ul>");
    }
    
    private void describe_URLExists(StringBuilder sb, String URL) throws UnsupportedEncodingException, IDMapperException{
        sb.append("<h3><a name=\"URLExists\">URLExists</h3>");
            sb.append("<ul>");
            sb.append("<li>State if the URL is know to the Mapping Service or not</li>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#URL\">URL</a></li>");
                sb.append("<ul>");
                sb.append("<li>Currently limited to single URI</li>");
                sb.append("</ul>");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(RdfConfig.getTheBaseURI());
                    sb.append("URLExists?URL=");
                    sb.append(URLEncoder.encode(URL, "UTF-8"));
                    sb.append("\">");
                    sb.append("URLExists?URL=");
                    sb.append(URL);
                    sb.append("</a></li>");    
            sb.append("</ul>");
    }
    
    private void describe_URLSearch(StringBuilder sb, String URL) throws UnsupportedEncodingException, IDMapperException{
        sb.append("<h3><a name=\"URLSearch\">URLSearch</h3>");
            sb.append("<ul>");
            sb.append("<li>Searches for URLs that have this ending.</li>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#text\">text</a></li>");
                sb.append("<li><a href=\"#limit\">limit</a> (default available)</li>");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(RdfConfig.getTheBaseURI());
                    sb.append("URLSearch?text=");
                    sb.append(URLEncoder.encode(URL, "UTF-8"));
                    sb.append("&limit=5");
                    sb.append("\">");
                    sb.append("URLSearch?text=");
                    sb.append(URL);
                    sb.append("&limit=5");
                    sb.append("</a></li>");    
            sb.append("</ul>");        
    }
       
    protected final void introduce_Info(StringBuilder sb) {
        sb.append("<dt><a href=\"#getMappingInfo\">getMappingInfo</a></dt>");
        sb.append("<dd>Brings up a table of all the mappings in the system by URISpace</dd>");
        sb.append("<dt><a href=\"#graphviz\">graphviz</a></dt>");
        sb.append("<dd>Brings up the getMappingInfo as graphviz input</dd>");           
    }

    protected final void describe_Info(StringBuilder sb) throws IDMapperException {
        sb.append("<h2>URL based methods");
        sb.append("<h3><a name=\"getMappingInfo\">getMappingInfo</h3>");
            sb.append("<ul>");
            sb.append("<li>Brings up a table of all the mappings in the system by URISpace</li>");
            sb.append("<li>No arguements</li>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(RdfConfig.getTheBaseURI());
                sb.append("getMappingInfo\">getMappingInfo</a></li>");    
            sb.append("</ul>");        
        sb.append("<h3><a name=\"graphviz\">graphviz</h3>");
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
                sb.append("graphviz\">graphviz</a></li>");    
            sb.append("</ul>");        
    
    }

   private void describe_mapping(StringBuilder sb, int mappingId) throws IDMapperException {
         sb.append("<h3><a name=\"mapping\">mapping/id</h3>");
            sb.append("<ul>");
            sb.append("<li>Obtian a mapping</li>");
            sb.append("<li>Required arguements: </li>");
                sb.append("<ul>");
                sb.append("<li>Place the mapping's ID after the /</li> ");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                    sb.append("mapping/");
                    sb.append(mappingId);
                    sb.append("\">");
                    sb.append("mapping/");
                    sb.append(mappingId);
                    sb.append("</a></li>");    
            sb.append("</ul>");        
    }
   
   private void describe_dataSource(StringBuilder sb, String sysCode) 
           throws UnsupportedEncodingException, IDMapperException {
         sb.append("<h3><a name=\"dataSource\">dataSource/id</h3>");
            sb.append("<ul>");
            sb.append("<li>Obtian a dataSource</li>");
            sb.append("<li>Required arguements: </li>");
                sb.append("<ul>");
                sb.append("<li>Returns the DataSource and associated UriSpace(s) with a specific id.</li> ");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                    sb.append("dataSource/");
                    sb.append(URLEncoder.encode(sysCode, "UTF-8"));
                    sb.append("\">");
                    sb.append("dataSource/");
                    sb.append(sysCode);
                    sb.append("</a></li>");    
            sb.append("</ul>");        
   }

   private void describe_getOverallStatistics(StringBuilder sb) 
            throws UnsupportedEncodingException, IDMapperException{
         sb.append("<h3><a name=\"getOverallStatistics\">getOverallStatistics</h3>");
            sb.append("<ul>");
            sb.append("<li>Returns some high level statistics. </li>");
                sb.append("<ul>");
                sb.append("<li>Same as shown on homepage.</li> ");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                    sb.append("getOverallStatistics");
                    sb.append("\">");
                    sb.append("getOverallStatistics");
                    sb.append("</a></li>");    
            sb.append("</ul>");        
   }
 
}


