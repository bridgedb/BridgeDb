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
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.bridgedb.Xref;
import org.bridgedb.rdf.BridgeDbRdfTools;
import org.bridgedb.uri.api.Mapping;
import org.bridgedb.uri.api.SetMappings;
import org.bridgedb.uri.api.UriMapper;
import org.bridgedb.uri.tools.GraphResolver;
import org.bridgedb.uri.lens.Lens;
import org.bridgedb.uri.lens.LensTools;
import org.bridgedb.uri.tools.RegexUriPattern;
import org.bridgedb.uri.tools.UriListener;
import org.bridgedb.uri.ws.WsUriConstants;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.ws.WsConstants;

/**
 * This class provides the API documentation
 * @author Christian
 */
public class WSAPI extends WSUriInterfaceService {
            
    private static final String ID_CODE = "id_code";
    private static final String FIRST_ID_PARAMETER = "?" + WsConstants.ID + "=";
    private static final String ID_PARAMETER = "&" + WsConstants.ID + "=";
    private static final String DATASOURCE_SYSTEM_CODE_PARAMETER = "&" + WsConstants.DATASOURCE_SYSTEM_CODE + "=";
    private final static String FIRST_SOURCE_PARAMETER = "?" + WsConstants.SOURCE_DATASOURCE_SYSTEM_CODE + "=";
    private final static String TARGET_PARAMETER = "&" + WsConstants.TARGET_DATASOURCE_SYSTEM_CODE + "=";
    private final static String FIRST_TEXT_PARAMETER = "?" + WsConstants.TEXT + "=";
    private final static String LIMIT5_PARAMETER = "&" + WsConstants.LIMIT + "=5";
    private final static String FIRST_URI_PARAMETER = "?" + WsUriConstants.URI + "=";
    private final static String TARGET_DATASOURCE_SYSTEM_CODE_PARAMETER = "&" + WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE + "=";
    private final static String GRAPH_PARAMETER = "&" + WsUriConstants.GRAPH + "=";
    private final static String TARGET_URI_PATTERN_PARAMETERX = "&" + WsUriConstants.TARGET_URI_PATTERN + "=";
    private final static String EXAMPLE_GRAPH = "ApiExampleGraph"; 

    protected UriListener uriListener;

    private HashMap<String,String> apiStrings = new HashMap<String,String>();
    
    static final Logger logger = Logger.getLogger(WSAPI.class);

    public WSAPI(UriMapper uriMapper)  throws BridgeDBException   {
        super(uriMapper);
        if (uriMapper instanceof UriListener){
            uriListener = (UriListener)uriMapper;
        } else {
            uriListener = null;
        }
     }
        
    /**
     * API page for the IMS methods.
     * 
     * Warning may not be completely up to date.
     * 
     * @param httpServletRequest
     * @return
     * @throws BridgeDBException
     * @throws UnsupportedEncodingException 
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/" + WsUriConstants.BRIDGEDB_API)
    public Response imsApiPage(@Context HttpServletRequest httpServletRequest) throws BridgeDBException, UnsupportedEncodingException {
        String contextPath = httpServletRequest.getContextPath() + "/";
        String apiString = apiStrings.get(contextPath);
        if (apiString == null){
            StringBuilder sb = new StringBuilder();
            showSummary(sb);
            showParameters(sb, contextPath);
            showMethods(sb, contextPath);
            sb.append("</body></html>");
            apiString = sb.toString();
            apiStrings.put(contextPath, apiString);
        }
        StringBuilder sb = topAndSide("Api",  httpServletRequest);
        return Response.ok(sb.toString() + apiString, MediaType.TEXT_HTML).build();
    }
    
     /**
     * Forwarding page for "/api".
     * 
     * This is expected to be over written by the IMS/ QueryExpander and any other extension.
     * @param httpServletRequest
     * @return
     * @throws BridgeDBException
     * @throws UnsupportedEncodingException 
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/api")
    public Response apiPage(@Context HttpServletRequest httpServletRequest) throws BridgeDBException, UnsupportedEncodingException {
        return imsApiPage(httpServletRequest);
    }
    
    private void showSummary(StringBuilder sb) {
        sb.append("<h2>Support services include:<h2>");
        sb.append("<dl>");      
        introduce(sb);
        sb.append("</dl>");
        sb.append("</p>\n");
    }

    private void introduce(StringBuilder sb) {
        boolean freeSearchSupported = idMapper.getCapabilities().isFreeSearchSupported(); 
        Set<String> keys = idMapper.getCapabilities().getKeys();

        introduce_IDMapper(sb, freeSearchSupported);
        introduce_IDMapperCapabilities(sb, keys, freeSearchSupported);     
        introduce_URIMapper(sb, freeSearchSupported);
        introduce_Info(sb);
    }

    private void showParameters(StringBuilder sb, String contextPath) throws BridgeDBException {
        sb.append("<h2>Parameters </h2>");
        sb.append("The following parametes may be applicable to the methods. ");
        sb.append("See the indiviual method description for which are required and which are optional.");
        sb.append("Their behaviour is consitant across all methods.\n");
        describeParameter(sb, contextPath);
    }

    private void showMethods(StringBuilder sb, String contextPath) throws BridgeDBException, UnsupportedEncodingException {
        List<Mapping> mappings = uriMapper.getSampleMapping();
        Mapping mapping1 = mappings.get(1);
       // DataSource dataSource1 = DataSource.getBySystemCode(mapping1.getSourceSysCode());
        Xref sourceXref1 = mapping1.getSource();
        String sourceSysCode1 = sourceXref1.getDataSource().getSystemCode();
        String sourceUri1 = mapping1.getSourceUri().iterator().next();
        String tragetSysCode1 = mapping1.getTarget().getDataSource().getSystemCode();
        String text1 = sourceXref1.getId();

        Mapping mapping2 = mappings.get(2);
        Xref sourceXref2 =  mapping2.getSource();
        String sourceUri2 = mapping2.getSourceUri().iterator().next();
        String targetUri2 = mapping2.getTargetUri().iterator().next(); 
        Xref targetXref2 = mapping2.getTarget();
        String targetUriSpace2;
        if (uriListener != null){
            RegexUriPattern pattern = uriListener.toUriPattern(targetUri2);
            GraphResolver.addMapping(EXAMPLE_GRAPH, pattern);
            targetUriSpace2 = pattern.getUriPattern();
        } else {
            targetUriSpace2 = targetUri2.substring(0, targetUri2.length()- targetXref2.getId().length());
            GraphResolver.addMapping(EXAMPLE_GRAPH, targetUriSpace2);
        }
        boolean freeSearchSupported = idMapper.getCapabilities().isFreeSearchSupported(); 
        Set<String> keys = idMapper.getCapabilities().getKeys();

        describe_IDMapper(sb, contextPath, sourceXref1, tragetSysCode1, sourceXref2, freeSearchSupported);
        describe_IDMapperCapabilities(sb, contextPath, sourceXref1, tragetSysCode1, keys, freeSearchSupported);
        describe_UriMapper(sb, contextPath, sourceXref1, tragetSysCode1, sourceUri1, sourceXref2, sourceUri2, 1, targetUriSpace2, 
                text1, 1, sourceSysCode1, freeSearchSupported);
//       dddd;
        sb.append("<h2>Support methods");
        describe_SourceInfos(sb, contextPath);
        describe_SourceTargetInfos(sb, contextPath, sourceSysCode1);
        describe_MappingSet(sb, contextPath, sourceXref1, sourceSysCode1, tragetSysCode1);
        describe_Graphviz(sb, contextPath);   
        describe_Lens(sb, contextPath);
        describe_LensRdf(sb, contextPath);
     }
    
    protected void describeParameter(StringBuilder sb, String contextPath) throws BridgeDBException  {
        describeCoreParameter(sb);
        describeUriParameter(sb, contextPath);
    }
    
    private void describeCoreParameter(StringBuilder sb) {
        sb.append("<h3>BridgeDB Parameters</h3>\n");
        sb.append("<ul>\n");
        sb.append("<dt><a name=\"id_code\">");
                sb.append(WsConstants.ID);
                sb.append(" and ");
                sb.append(WsConstants.DATASOURCE_SYSTEM_CODE);
                sb.append("</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Limits the results to ones with this/these Xrefs.</li>");
            sb.append("<li>");
                    sb.append(WsConstants.DATASOURCE_SYSTEM_CODE);
                    sb.append(" is the SystemCode of the Xref's DataSource)</li>");
            sb.append("<li>");
                    sb.append(WsConstants.ID);
                    sb.append(" is the identifier part of the Xref</li>");
            sb.append("<li>Typically There can be multiple \"");
                    sb.append(WsConstants.ID);
                    sb.append("\" and \"");
                    sb.append(WsConstants.DATASOURCE_SYSTEM_CODE);
                    sb.append("\" values</li>");
                sb.append("<ul>");
                sb.append("<li>There must be at least one of each.</li>");                
                sb.append("<li>There must be the same number of each.</li>");                
                sb.append("<li>They will be paired by order.</li>");                
                sb.append("<li>If multiple Xref's have the same DataSource their code must be repeated.</li>");                
                sb.append("</ul>");
            sb.append("</ul>");           
            sb.append("<li>Note: Other methods may obtain a different ");           
                    sb.append(WsConstants.ID);           
                    sb.append(" by following the method name with a slash // ");
                    sb.append(WsConstants.ID);
                    sb.append(". These do not require a \"");
                    sb.append(WsConstants.DATASOURCE_SYSTEM_CODE);
                    sb.append("\"</li>\n");
        sb.append("<dt><a name=\"key\">key</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Selects which property to return.</li>");
            sb.append("<li>Only one key parameter is supported.</li>");
            sb.append("</ul>\n");
        sb.append("<dt><a name=\"");
                sb.append(WsConstants.LIMIT);
                sb.append("\">");
                sb.append(WsConstants.LIMIT);
                sb.append("</a></dt>");
                sb.append("<ul>");
            sb.append("<li>Limits the number of results.</li>");
            sb.append("<li>Must be a positive Integer in String Format</li>");
            sb.append("<li>If less than ");
                    sb.append(WsConstants.LIMIT);
                    sb.append("results are availabe ");
                    sb.append(WsConstants.LIMIT);
                    sb.append(" will have no effect.</li>");
            sb.append("<li>Only one ");
                    sb.append(WsConstants.LIMIT);
                    sb.append(" parameter is supported.</li>");
            sb.append("<li>If no ");
                    sb.append(WsConstants.LIMIT);
                    sb.append(" is set a default ");
                    sb.append(WsConstants.LIMIT);
                    sb.append(" will be used.</li>");
            sb.append("<li>If too high a ");
                    sb.append(WsConstants.LIMIT);
                    sb.append(" is set the default ");
                    sb.append(WsConstants.LIMIT);
                    sb.append(" will be used.</li>");
            sb.append("<li>To obtain a full data dump please contact the admins.</li>");
            sb.append("</ul>\n");
        sb.append("<dt><a name=\"");
                sb.append(WsConstants.SOURCE_DATASOURCE_SYSTEM_CODE);
                sb.append("\">");
                sb.append(WsConstants.SOURCE_DATASOURCE_SYSTEM_CODE);
                sb.append("</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Limits the results to ones with those Source Xref's DataSource has this sysCode.</li>");
            sb.append("<li>String Format</li>");
            sb.append("<li>Typically there must be exactly one ");
                    sb.append(WsConstants.SOURCE_DATASOURCE_SYSTEM_CODE);
                    sb.append(" when used..</li>");
            sb.append("</ul>\n");
        sb.append("<dt><a name=\"");
                sb.append(WsConstants.TARGET_DATASOURCE_SYSTEM_CODE);
                sb.append("\">");
                sb.append(WsConstants.TARGET_DATASOURCE_SYSTEM_CODE);
                sb.append("</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Limits the results to ones with those Target Xref's DataSource has this sysCode.</li>");
            sb.append("<li>String Format</li>");
            sb.append("<li>Typically there can but need not be more than one.</li>");
            sb.append("</ul>");
         sb.append("<dt><a name=\"");
                    sb.append(WsConstants.TEXT);
                    sb.append("\">");
                    sb.append(WsConstants.TEXT);
                    sb.append("</a></dt>");
            sb.append("<ul>");
            sb.append("<li>A bit of text that will be searched for.</li>");
            sb.append("<li>String Format</li>");
            sb.append("<li>Only one text parameter is supported.</li>");
            sb.append("<li>Note this is for searching for text in Identifiers not for mapping between text and Identifiers.</li>");
            sb.append("</ul>");      
        sb.append("</ul>\n");
   }

   private void describeUriParameter(StringBuilder sb, String contextPath) throws BridgeDBException {
        sb.append("<h3>Ops Exstension Parameters</h3>");
        sb.append("<ul>\n");
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
            sb.append("</ul>\n");
        sb.append("<dt><a name=\"");
                sb.append(WsUriConstants.LENS_URI);
                sb.append("\">");
                sb.append(WsUriConstants.LENS_URI);
                sb.append("</a></dt>");
            sb.append("<ul>");
                sb.append("<li>If not provided the default lens is used. Except in the ");
                    refLlens(sb);
                    sb.append("</li>");
                sb.append("<li> Use ");
                    this.refLlens(sb);
                    sb.append("  method to get a list of the Lens.<li>");
            sb.append("</ul>\n");        
        sb.append("<dt><a name=\"");
                sb.append(WsUriConstants.LENS_GROUP);
                sb.append("\">");
                sb.append(WsUriConstants.LENS_GROUP);
                sb.append("</a></dt>");
            sb.append("<ul>");
            sb.append("<li>If not provided the ");
                sb.append(LensTools.PUBLIC_GROUP_NAME);
                sb.append(" groups is used </li>");
            sb.append("<li>A list of the current groups is available from <a href=\"");
                sb.append(contextPath);
                sb.append(WsUriConstants.LENS_GROUP);
                sb.append("\">");
                sb.append(contextPath);
                sb.append(WsUriConstants.LENS_GROUP);
                sb.append("</a></li>");    
                sb.append("</li>");
            sb.append("</ul>\n");        
        sb.append("<dt><a name=\"");
                sb.append(WsUriConstants.GRAPH);
                sb.append("\">");
                sb.append(WsUriConstants.GRAPH);
                sb.append("</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Limits the results to ones required for this OpenRdf context/graph.</li>");
            sb.append("<li>This works by looking up the required ");
            sb.append(WsUriConstants.TARGET_URI_PATTERN);
            sb.append("(s) in the graph.properties configuration file.<li>");
            sb.append("<li>There can only one parameter ");
            sb.append(WsUriConstants.GRAPH);
            sb.append(" and ONLY if NO paramters ");
            sb.append(WsUriConstants.TARGET_URI_PATTERN);
            sb.append(" are supplied.</li>");
            sb.append("</ul>\n");       
        sb.append("<dt><a name=\"");
                sb.append(WsUriConstants.TARGET_URI_PATTERN);
                sb.append("\">");
                sb.append(WsUriConstants.TARGET_URI_PATTERN);
                sb.append("</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Limits the results to ones with URIs with this pattern.</li>");
            sb.append("<li>The URI pattern must match on of the Patterns specified in the System. It will not with just any the URI startWith.</li>");
            sb.append("<li>String Format</li>");
            sb.append("<li>Do NOT include the @gt and @lt seen arround URIs in RDF</li>");
            sb.append("<li>Typically there can but need not be more than one.</li>");
            sb.append("</ul>\n");
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
        sb.append("<dt><a name=\"");
                sb.append(WsUriConstants.RDF_FORMAT);
                sb.append("\">");
                sb.append(WsUriConstants.RDF_FORMAT);
                sb.append("</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Specifies the RDF format that should be used.</li>");
            sb.append("<ul>");         
                sb.append("<li>WARNING: Not all RDF formats supports contexts/graphs.</li>");
                sb.append("<li>So some formats will not have the mapppings in Set contexts.</li>");
                sb.append("</ul>");
            sb.append("<li>String Format</li>");
            sb.append("<li>Legal values are:</li><ul>");
                for (String formatName:BridgeDbRdfTools.getAvaiableWriters()){
                    sb.append("<li>");
                    sb.append(formatName);
                    sb.append("</li>");
                }
                sb.append("</ul>");
            sb.append("<li>Only one format can be specified.</li>");
            sb.append("</ul>\n");
         sb.append("</ul>\n");
        sb.append("<dt><a name=\"");
                sb.append(WsUriConstants.INCLUDE_XREF_RESULTS);
                sb.append("\">");
                sb.append(WsUriConstants.INCLUDE_XREF_RESULTS);
                sb.append("</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Ask for the results to include Bridgb Xref results.</li>");
            sb.append("<li>Only effects calls that would not naturally return Xref results</li>");
            sb.append("<ul>");
                sb.append("<li>Always considered true for any call with ");
                    parameterID_CODE(sb);
                    sb.append(" paramtered set");
                sb.append("<li>Always considered true for any call with ");
                    parameterTargetCode(sb);
                    sb.append(" paramters set");
            sb.append("</ul>");
            sb.append("<li>In all other cases defaults to false.</li>");
            sb.append("</ul>\n");
        sb.append("<dt><a name=\"");
                sb.append(WsUriConstants.INCLUDE_URI_RESULTS);
                sb.append("\">");
                sb.append(WsUriConstants.INCLUDE_URI_RESULTS);
                sb.append("</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Ask for the results to include URI results.</li>");
            sb.append("<li>Only effects calls that would not naturally return URI results</li>");
            sb.append("<ul>");
                sb.append("<li>Always considered true for any call with ");
                    parameterUri(sb);
                    sb.append(" paramter set");
                sb.append("<li>Always considered true for any call with ");
                    parameterGraph(sb);
                    sb.append(" paramters set");
                sb.append("<li>Always considered true for any call with ");
                    parameterTargetPattern(sb);
                    sb.append(" paramters set");
            sb.append("</ul>");
            sb.append("<li>In all other cases defaults to false.</li>");
            sb.append("</ul>\n");
   }

   protected final void introduce_IDMapper(StringBuilder sb, boolean freeSearchSupported) {
        sb.append("<dt><a href=\"#");
                sb.append(WsConstants.MAP_ID);
                sb.append("\">");
                sb.append(WsConstants.MAP_ID);
                sb.append("</a></dt>\n");
        sb.append("<dd>List the Xrefs that map to these Xrefs</dd>\n");
        sb.append("<dt><a href=\"#");
                sb.append(WsConstants.XREF_EXISTS);
                sb.append("\">");
                sb.append(WsConstants.XREF_EXISTS);
                sb.append("</a></dt>\n");
        sb.append("<dd>State if the Xref is know to the Mapping Service or not</dd>");   
        sb.append("<dt><a href=\"#");
                sb.append(WsConstants.FREE_SEARCH);
                sb.append("\">");
                sb.append(WsConstants.FREE_SEARCH);
                sb.append("</a></dt>\n");
        if (freeSearchSupported){
            sb.append("<dd>Searches for Xrefs that have this id.</dd>\n");
        } else {
            sb.append("<dd>This is currently not supported.</dd>\n");      
        }
        sb.append("<dt><a href=\"#");
                sb.append(WsConstants.GET_CAPABILITIES);
                sb.append("\">");
                sb.append(WsConstants.GET_CAPABILITIES);
                sb.append("</a></dt>\n");
        sb.append("<dd>Gives the Capabilitles as defined by BridgeDB.</dd>");
        sb.append("<dt>Close()</a></dt>");
        sb.append("<dd>Not supported as clients should not be able to close the server.</dd>");
        sb.append("<dt>isConnected</dt>");
        sb.append("<dd>Not supported as Close() is not allowed</dd>\n");
    }

    protected void describe_IDMapper(StringBuilder sb, String contextPath, Xref sourceXref1, String tragetSysCode1, Xref sourceXref2,
            boolean freeSearchSupported) throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h2>Implementations of BridgeDB's IDMapper methods</h2>\n");

        describe_mapID(sb, contextPath, sourceXref1, tragetSysCode1, sourceXref2);    
        describe_xrefExists(sb, contextPath, sourceXref1);
        if (freeSearchSupported){
            describe_freeSearch(sb, contextPath, sourceXref1);
        }
        describe_getCapabilities(sb, contextPath); 
        sb.append("<h3>Other IDMapper Functions</h3>");
        sb.append("<dl>");
        sb.append("<dt>Close()</a></dt>");
        sb.append("<dd>Not supported as clients should not be able to close the server.</dd>");
        sb.append("<dt>isConnected</dt>");
        sb.append("<dd>Not supported as Close() is not allowed</dd>");
        sb.append("</dl>\n");
    }
    
    private void describe_mapID(StringBuilder sb, String contextPath, Xref sourceXref1, String tragetSysCode1, Xref sourceXref2) 
            throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h3><a name=\"");
                    sb.append(WsConstants.MAP_ID);
                    sb.append("\">");
                    sb.append(WsConstants.MAP_ID);
                    sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>List the Xrefs that map to these Xrefs</li>");
            sb.append("<li>Implements:  Map&ltXref, Set&ltXref&gt&gt mapID(Collection&ltXref&gt srcXrefs, DataSource... tgtDataSources)</li>");
            sb.append("<li>Implements:  Set&ltXref&gt mapID(Xref srcXrefs, DataSource... tgtDataSources)</li>");
            appendXmlJsonHtml(sb); 
            sb.append("<li>Required arguements: (Only Source Xref considered)</li>");
                sb.append("<ul>");
                parameterID_CODE(sb);
                sb.append("</ul>");
            sb.append("<li>Optional arguments</li>");
                sb.append("<ul>");
                parameterTargetCode(sb);
                sb.append("</ul>");        
            mapExamplesXrefbased(sb, contextPath, WsConstants.MAP_ID, sourceXref1, tragetSysCode1, sourceXref2);
            sb.append("</ul>\n");
    }
    
    private void describe_xrefExists(StringBuilder sb, String contextPath, Xref xref1) throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h3><a name=\"");
                sb.append(WsConstants.XREF_EXISTS);
                sb.append("\">");
                sb.append(WsConstants.XREF_EXISTS);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  boolean xrefExists(Xref xref)</li>");
            sb.append("<li>State if the Xref is know to the Mapping Service or not</li>");
            appendXmlJson(sb);
            sb.append("<li>Required arguements: (Considers both Source and target Xrefs</li>");
                sb.append("<ul>");
                parameterID_CODE(sb);
                sb.append("<li>Currently only a single ");
                        sb.append(WsConstants.ID);
                        sb.append(" and single ");
                        sb.append(WsConstants.DATASOURCE_SYSTEM_CODE);
                        sb.append(" supported.</li>");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                sb.append(contextPath);
                    sb.append(WsConstants.XREF_EXISTS);
                    sb.append(FIRST_ID_PARAMETER);
                    sb.append(URLEncoder.encode(xref1.getId(), "UTF-8"));
                    sb.append(DATASOURCE_SYSTEM_CODE_PARAMETER);
                    sb.append(URLEncoder.encode(xref1.getDataSource().getSystemCode(), "UTF-8"));
                    sb.append("\">");
                    sb.append(WsConstants.XREF_EXISTS);
                    sb.append(FIRST_ID_PARAMETER);
                    sb.append(xref1.getId());
                    sb.append(FIRST_ID_PARAMETER);
                    sb.append(xref1.getDataSource().getSystemCode());
                    sb.append("</a></li>");    
            sb.append("</ul>\n");
    }
    
    private void describe_freeSearch(StringBuilder sb, String contextPath, Xref xref1) throws UnsupportedEncodingException, BridgeDBException{
         sb.append("<h3><a name=\"");
                sb.append(WsConstants.FREE_SEARCH);
                sb.append("\">");
                sb.append(WsConstants.FREE_SEARCH);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>Searches for Xrefs that have this id.</li>");
            sb.append("<li>Implements:  Set&ltXref&gt freeSearch (String text, int limit)</li>");
            appendXmlJsonHtml(sb); 
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                parameterTextLimit(sb);
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(contextPath);
                    sb.append(WsConstants.FREE_SEARCH);
                    sb.append(FIRST_TEXT_PARAMETER);
                    sb.append(URLEncoder.encode(xref1.getId(), "UTF-8"));
                    sb.append(LIMIT5_PARAMETER);
                    sb.append("\">");
                    sb.append(WsConstants.FREE_SEARCH);
                    sb.append(FIRST_TEXT_PARAMETER);
                    sb.append(xref1.getId());
                    sb.append(LIMIT5_PARAMETER);
                    sb.append("</a></li>");    
            sb.append("</ul>\n");
    }
    
  protected final void introduce_IDMapperCapabilities(StringBuilder sb, Set<String> keys, boolean freeSearchSupported) {
        sb.append("<dt><a href=\"#");
                sb.append(WsConstants.IS_FREE_SEARCH_SUPPORTED);
                sb.append("\">");
                sb.append(WsConstants.IS_FREE_SEARCH_SUPPORTED);
                sb.append("</a></dt>");
        if (freeSearchSupported){
            sb.append("<dd>Returns True as freeSearch and URLSearch are supported.</dd>");
        } else {
            sb.append("<dd>Returns False because underlying IDMappper does not support freeSearch or URLSearch.</dd>");                
        }        
        sb.append("<dt><a href=\"#");
                sb.append(WsConstants.GET_SUPPORTED_SOURCE_DATA_SOURCES);
                sb.append("\">");
                sb.append(WsConstants.GET_SUPPORTED_SOURCE_DATA_SOURCES);
                sb.append("</a></dt>");
        sb.append("<dd>Returns Supported Source (BridgeDB)DataSource(s).</dd>");
        sb.append("<dt><a href=\"#");
                sb.append(WsConstants.GET_SUPPORTED_TARGET_DATA_SOURCES);
                sb.append("\">");
                sb.append(WsConstants.GET_SUPPORTED_TARGET_DATA_SOURCES);
                sb.append("</a></dt>");
        sb.append("<dd>Returns Supported Target (BridgeDB)DataSource(s).</dd>");
        sb.append("<dt><a href=\"#");
                sb.append(WsConstants.IS_MAPPING_SUPPORTED);
                sb.append("\">");
                sb.append(WsConstants.IS_MAPPING_SUPPORTED);
                sb.append("</a></dt>");
        sb.append("<dd>States if two DataSources are mapped at least once.</dd>");
        sb.append("<dt><a href=\"#");
                sb.append(WsConstants.PROPERTY);
                sb.append("\">");
                sb.append(WsConstants.PROPERTY);
                sb.append("/key</a></dt>");
        if (keys.isEmpty()){
            sb.append("<dd>There are currently no properties supported.</dd>");
        } else {
            sb.append("<dd>Returns The property value for this key.</dd>");
        }
        sb.append("<dt><a href=\"#");
                sb.append(WsConstants.GET_KEYS);
                sb.append("\">");
                sb.append(WsConstants.GET_KEYS);
                sb.append("</a></dt>");
        if (keys.isEmpty()){
            sb.append("<dd>There are currently no properties supported.</dd>\n");
        } else {
            sb.append("<dd>Returns The keys and their property value.</dd>\n");
        }
    }
  
    private void describe_getCapabilities(StringBuilder sb, String contextPath) throws BridgeDBException {
         sb.append("<h3><a name=\"");
                sb.append(WsConstants.GET_CAPABILITIES);
                sb.append("\">");
                sb.append(WsConstants.GET_CAPABILITIES);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  IDMapperCapabilities getCapabilities()</li>");
            sb.append("<li>Gives the Capabilitles as defined by BridgeDB.</li>");
            appendXmlJson(sb); 
            sb.append("<li>Example: <a href=\"");
                    sb.append(contextPath);
                    sb.append(WsConstants.GET_CAPABILITIES);
                    sb.append("\">");
                    sb.append(WsConstants.GET_CAPABILITIES);
                    sb.append("</a></li>");    
            sb.append("</ul>\n");
    }
    
    protected void describe_IDMapperCapabilities(StringBuilder sb, String contextPath, Xref xref1, String tragetSysCode1, Set<String> keys, 
            boolean freeSearchSupported) throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h2>Implementations of BridgeDB's IDMapperCapabilities methods</h2>\n");
        describe_isFreeSearchSupported(sb, contextPath, freeSearchSupported);
        describe_getSupportedDataSources(sb, contextPath);
        describe_isMappingSupported(sb, contextPath, xref1, tragetSysCode1); 
        describe_getProperty(sb, contextPath, keys);            
        describe_getKeys(sb, contextPath, keys);
    }
    
    private void describe_isFreeSearchSupported(StringBuilder sb, String contextPath, boolean freeSearchSupported) throws BridgeDBException {
         sb.append("<h3><a name=\"");
                sb.append(WsConstants.IS_FREE_SEARCH_SUPPORTED);
                sb.append("\">");
                sb.append(WsConstants.IS_FREE_SEARCH_SUPPORTED);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  boolean isFreeSearchSupported()</li>");
            if (freeSearchSupported){
                sb.append("<li>Returns True as freeSearch and URISearch are supported.</li>");
            } else {
                sb.append("<li>Returns False because underlying IDMappper does not support freeSearch or URISearch.</li>");                
            }
            appendXmlJson(sb); 
            sb.append("<li>Example: <a href=\"");
                    sb.append(contextPath);
                    sb.append(WsConstants.IS_FREE_SEARCH_SUPPORTED);
                    sb.append("\">");
                    sb.append(WsConstants.IS_FREE_SEARCH_SUPPORTED);
                    sb.append("</a></li>");    
            sb.append("</ul>\n");
    }
    
    private void describe_getSupportedDataSources(StringBuilder sb, String contextPath) throws BridgeDBException {
         sb.append("<h3><a name=\"");
                sb.append(WsConstants.GET_SUPPORTED_SOURCE_DATA_SOURCES);
                sb.append("\">");
                sb.append(WsConstants.GET_SUPPORTED_SOURCE_DATA_SOURCES);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  Set&ltDataSource&gt  getSupportedSrcDataSources()</li>");
            sb.append("<li>Returns Supported Source (BridgeDB)DataSource(s).</li>");
            appendXmlJsonHtml(sb); 
            sb.append("<li>Example: <a href=\"");
                    sb.append(contextPath);
                    sb.append(WsConstants.GET_SUPPORTED_SOURCE_DATA_SOURCES);
                    sb.append("\">");
                    sb.append(WsConstants.GET_SUPPORTED_SOURCE_DATA_SOURCES);
                    sb.append("</a></li>");    
            sb.append("</ul>\n");
          
         sb.append("<h3><a name=\"");
                sb.append(WsConstants.GET_SUPPORTED_TARGET_DATA_SOURCES);
                sb.append("\">");
                sb.append(WsConstants.GET_SUPPORTED_TARGET_DATA_SOURCES);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  Set&ltDataSource&gt  getSupportedTgtDataSources()</li>");
            sb.append("<li>Returns Supported Target (BridgeDB)DataSource(s).</li>");
            appendXmlJsonHtml(sb); 
            sb.append("<li>Example: <a href=\"");
                    sb.append(contextPath);
                    sb.append(WsConstants.GET_SUPPORTED_TARGET_DATA_SOURCES);
                    sb.append("\">");
                    sb.append(WsConstants.GET_SUPPORTED_TARGET_DATA_SOURCES);
                    sb.append("</a></li>");    
            sb.append("</ul>\n");
    }
    
    private void describe_isMappingSupported(StringBuilder sb, String contextPath, Xref sourceXref1, String targetSysCode) 
            throws UnsupportedEncodingException, BridgeDBException{
         sb.append("<h3><a name=\"");
                sb.append(WsConstants.IS_MAPPING_SUPPORTED);
                sb.append("\">");
                sb.append(WsConstants.IS_MAPPING_SUPPORTED);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>States if two DataSources are mapped at least once.</li>");
            sb.append("<li>Implements:  boolean isMappingSupported(DataSource src, DataSource tgt)</li>");
            appendXmlJson(sb); 
            sb.append("<li>Required arguements: (One of each)</li>");
                sb.append("<ul>");
                parameterSourceCode(sb);
                parameterTargetCode(sb);
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(contextPath);
                    sb.append(WsConstants.IS_MAPPING_SUPPORTED);
                    sb.append(FIRST_SOURCE_PARAMETER);
                    sb.append(sourceXref1.getDataSource().getSystemCode());
                    sb.append(TARGET_PARAMETER);
                    sb.append(targetSysCode);
                    sb.append("\">");
                    sb.append(WsConstants.IS_MAPPING_SUPPORTED);
                    sb.append(FIRST_SOURCE_PARAMETER);
                    sb.append(URLEncoder.encode(sourceXref1.getDataSource().getSystemCode(), "UTF-8"));
                    sb.append(TARGET_PARAMETER);
                    sb.append(URLEncoder.encode(targetSysCode, "UTF-8"));
                    sb.append("</a></li>");    
            sb.append("</ul>\n");
    }

    private void describe_getProperty(StringBuilder sb, String contextPath, Set<String> keys) 
            throws UnsupportedEncodingException, BridgeDBException{
         sb.append("<h3><a name=\"");
                sb.append(WsConstants.PROPERTY);
                sb.append("\">");
                sb.append(WsConstants.PROPERTY);
                sb.append("/key</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  String getProperty(String key)</li>");
            sb.append("<li>Returns The property value for this key.</li>");
            appendXmlJsonHtml(sb); 
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li>Place the actual key after the /</li> ");
                sb.append("</ul>");
            if (keys.isEmpty()){
                sb.append("<li>There are currently no properties supported</li>");
            } else {
                sb.append("<li>Example: <a href=\"");
                    sb.append(contextPath);
                        sb.append(WsConstants.PROPERTY);
                        sb.append("/");
                        sb.append(keys.iterator().next());
                        sb.append("\">");
                        sb.append(WsConstants.PROPERTY);
                        sb.append("/");
                        sb.append(URLEncoder.encode(keys.iterator().next(), "UTF-8"));
                        sb.append("</a></li>");    
            }
            sb.append("</ul>\n");
    }
    
    private void describe_getKeys(StringBuilder sb, String contextPath, Set<String> keys) throws BridgeDBException{
         sb.append("<h3><a name=\"");
                sb.append(WsConstants.GET_KEYS);
                sb.append("\">");
                sb.append(WsConstants.GET_KEYS);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  Set<String> getKeys()</li>");
            sb.append("<li>Returns The keys and their property value.</li>");
            appendXmlJsonHtml(sb); 
            if (keys.isEmpty()){
                sb.append("<li>There are currently no properties supported</li>");
            } else {
                sb.append("<li>Example: <a href=\"");
                    sb.append(contextPath);
                        sb.append(WsConstants.GET_KEYS);
                        sb.append("\">");
                        sb.append(WsConstants.GET_KEYS);
                        sb.append("</a></li>");    
            }
            sb.append("</ul>\n");
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
        sb.append("<dt><a href=\"#");
                sb.append(WsUriConstants.MAP_BY_SET);
                sb.append("\">");
                sb.append(WsUriConstants.MAP_BY_SET);
                sb.append("</a></dt>");
        sb.append("<dd>List the URIs that map to this/these URIs organised into Sets and Source Target pairs.</dd>");
        sb.append("<dt><a href=\"#");
                sb.append(WsUriConstants.MAP_BY_SET + WsUriConstants.RDF);
                sb.append("\">");
                sb.append(WsUriConstants.MAP_BY_SET + WsUriConstants.RDF);
                sb.append("</a></dt>");
        sb.append("<dd>Same data as ");
            refMapBySet(sb);
            sb.append(" but presented at RDF.</dd>");
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
                sb.append(WsUriConstants.DATA_SOURCE);
                sb.append("\">");
                sb.append(WsUriConstants.DATA_SOURCE);
                sb.append("</a></dt>");
        sb.append("<dd>Returns the DataSource and associated UriSpace(s) with a specific id</dd>\n");
        sb.append("<dt><a href=\"#");
                sb.append(WsUriConstants.TO_URIS);
                sb.append("\">");
                sb.append(WsUriConstants.TO_URIS);
                sb.append("</a></dt>");
        sb.append("<dd>Returns the URIs associated with a Xref (specified as <a href=\"#");
            sb.append(ID_CODE);
            sb.append("\">");
            sb.append(WsConstants.ID);
            sb.append("</a> and <a href=\"#");
            sb.append(ID_CODE);
            sb.append("\">");
            sb.append(WsConstants.DATASOURCE_SYSTEM_CODE);
            sb.append("</a>)</dd>\n");
        sb.append("<dt><a href=\"#");
                sb.append(WsUriConstants.TO_XREF);
                sb.append("\">");
                sb.append(WsUriConstants.TO_XREF);
                sb.append("</a></dt>");
        sb.append("<dd>Retrns the Xref for this URI</dd>\n");
    }
    
    protected final void describe_UriMapper(StringBuilder sb, String contextPath, Xref sourceXref1, String tragetSysCode1, String sourceUri1, Xref sourceXref2, 
            String sourceUri2, int x, String targetUriSpace2, String text, int mappingId, String sysCode, boolean freeSearchSupported) 
            throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h2>URI based methods</h2>\n");
        describe_map(sb, contextPath, sourceXref1, tragetSysCode1, sourceUri1, sourceXref2, sourceUri2, targetUriSpace2);
        describe_mapUri(sb, contextPath, sourceUri1, sourceUri2, targetUriSpace2);
        describe_mapBySet(sb, contextPath, sourceUri1, sourceUri2, targetUriSpace2);
        describe_mapBySetRDF(sb, contextPath, sourceUri1, sourceUri2, targetUriSpace2);
        describe_uriExists(sb, contextPath, sourceUri1);
        if (freeSearchSupported) {
            describe_uriSearch(sb, contextPath, text); 
        }
        describe_dataSource(sb, contextPath, sysCode);
        describe_toUris(sb, contextPath, sourceXref1);
        describe_toXref(sb, contextPath, sourceUri1);
    }
        
    private void describe_map(StringBuilder sb, String contextPath, Xref sourceXref1, String tragetSysCode1, String sourceUri1, Xref sourceXref2, 
            String sourceUri2, String targetUriSpace2) throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.MAP);
                sb.append("\">");
                sb.append(WsUriConstants.MAP);
                sb.append("</a></h3>");
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
            sb.append("<li>Note: it is not allowed to use both <a href=\"#");
                sb.append(WsUriConstants.GRAPH);
                sb.append("\">");
                sb.append(WsUriConstants.GRAPH);
                sb.append("</a> and <a href=\"#");
                sb.append(WsUriConstants.TARGET_URI_PATTERN);
                sb.append("\">");
                sb.append(WsUriConstants.TARGET_URI_PATTERN);
                sb.append("</a>. If both are supplied an error is thrown.</li> ");
            appendXmlJsonHtml(sb); 
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li>URI based</li>");
                    sb.append("<ul>");
                    parameterUri(sb);
                    sb.append("</ul>");
                sb.append("<li>Xref based</li>");
                    sb.append("<ul>");
                    parameterID_CODE(sb);
                    sb.append("</ul>");
                sb.append("</ul>");
            sb.append("<li>Optional arguments</li>");
                sb.append("<ul>");
                parameterLensUri(sb);
                parameterIncludeXrefResults(sb);
                parameterIncludeUriResults(sb);
                parameterGraph(sb);
                parameterTargetPattern(sb);
                parameterTargetCode(sb);
                sb.append("</ul>");
        mapExamplesXrefbased(sb, contextPath, WsUriConstants.MAP, sourceXref1, tragetSysCode1, sourceXref2);
        mapExamplesUriBased(sb, contextPath, WsUriConstants.MAP, sourceUri1, sourceUri2, targetUriSpace2);
        sb.append("</ul>\n");
    }
    
    private void describe_mapUri(StringBuilder sb, String contextPath, String sourceUri1, String sourceUri2, String targetUriSpace2) 
            throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.MAP_URI);
                sb.append("\">");
                sb.append(WsUriConstants.MAP_URI);
                sb.append("</a></h3>");
        sb.append("<ul>");
            sb.append("<li>List the URIs that map to this URI(s)</li>");
            sb.append("<li>Note: it is not allowed to use both <a href=\"#");
                sb.append(WsUriConstants.GRAPH);
                sb.append("\">");
                sb.append(WsUriConstants.GRAPH);
                sb.append("</a> and <a href=\"#");
                sb.append(WsUriConstants.TARGET_URI_PATTERN);
                sb.append("\">");
                sb.append(WsUriConstants.TARGET_URI_PATTERN);
                sb.append("</a>. If both are supplied an error is thrown.</li> ");
            appendXmlJsonAndHtml(sb); 
            sb.append("<li>Required arguements:</li>");
            sb.append("<ul>");
                parameterUri(sb);
                sb.append("<ul><li>In Contrast to other methods multiple values may be provided</li></ul>");  
                sb.append("</ul>");
            sb.append("<li>Optional arguments</li><ul>");
                parameterLensUri(sb);
                parameterGraph(sb);
                parameterTargetPattern(sb);
                sb.append("</ul>");
            mapExamplesUriBased(sb, contextPath, WsUriConstants.MAP_URI, sourceUri1, sourceUri2, targetUriSpace2);
            sb.append("</ul>\n");
    }           

    private void describe_mapBySet(StringBuilder sb, String contextPath, String sourceUri1, String sourceUri2, String targetUriSpace2) 
            throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.MAP_BY_SET);
                sb.append("\">");
                sb.append(WsUriConstants.MAP_BY_SET);
                sb.append("</a></h3>");
        sb.append("<ul>");
            sb.append("<li>List the URIs that map to this/these URIs organised into Sets and Source Target pairs.</li>");
            appendXmlJsonHtml(sb); 
            sb.append("<li>Arguements same as:");
                refMapUri(sb);
            sb.append("</li>");
            mapExamplesUriBased(sb, contextPath, WsUriConstants.MAP_BY_SET, sourceUri1, sourceUri2, targetUriSpace2);
            sb.append("</ul>\n");
    }           

    private void describe_mapBySetRDF(StringBuilder sb, String contextPath, String sourceUri1, String sourceUri2, String targetUriSpace2) 
            throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.MAP_BY_SET + WsUriConstants.RDF);
                sb.append("\">");
                sb.append(WsUriConstants.MAP_BY_SET + WsUriConstants.RDF);
                sb.append("</a></h3>");
        sb.append("<ul>");
            sb.append("<li>Same data as ");
                refMapBySet(sb);
                sb.append(" but presented at RDF.</dd>");
            appendTextHtml(sb); 
 
            sb.append("<li>Includes all the same arguements as :");
                refMapUri(sb);
                sb.append(" and ");
                refMapBySet(sb);
                sb.append("</li>");
            sb.append("<li>Plus </li><ul>");
                parameterRdfFormat(sb);
                sb.append("</li></ul>");
            mapExamplesUriBased(sb, contextPath, WsUriConstants.MAP_BY_SET + WsUriConstants.RDF, sourceUri1, sourceUri2, targetUriSpace2);
            for (String formatName:BridgeDbRdfTools.getAvaiableWriters()){
                mapExamplesUriBasedFormatted(sb, contextPath, WsUriConstants.MAP_BY_SET + WsUriConstants.RDF, sourceUri1, formatName);
            }
            sb.append("</ul>\n");
    }           

    private void describe_uriExists(StringBuilder sb, String contextPath, String uri) throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.URI_EXISTS);
                sb.append("\">");
                sb.append(WsUriConstants.URI_EXISTS);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>State if the URI is know to the Mapping Service or not</li>");
            appendXmlJson(sb); 
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                parameterUri(sb);
                sb.append("<ul>");
                sb.append("<li>Currently limited to single URI</li>");
                sb.append("</ul>");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(contextPath);
                    sb.append(WsUriConstants.URI_EXISTS);
                    sb.append(FIRST_URI_PARAMETER);
                    sb.append(URLEncoder.encode(uri, "UTF-8"));
                    sb.append("\">");
                    sb.append(WsUriConstants.URI_EXISTS);
                    sb.append(FIRST_URI_PARAMETER);
                    sb.append(uri);
                    sb.append("</a></li>");    
            sb.append("</ul>\n");
    }
    
    private void describe_uriSearch(StringBuilder sb, String contextPath, String text) throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.URI_SEARCH);
                sb.append("\">");
                sb.append(WsUriConstants.URI_SEARCH);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>Searches for URIs that have this ending.</li>");
            appendXmlJsonHtml(sb); 
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                parameterTextLimit(sb);
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(contextPath);
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
            sb.append("</ul>\n");        
    }
       
    private final void introduce_Info(StringBuilder sb) {
        sb.append("<dt><a href=\"#");
                sb.append(WsUriConstants.SOURCE_INFOS);
                sb.append("\">");
                sb.append(WsUriConstants.SOURCE_INFOS);
                sb.append("</a></dt>");
        sb.append("<dd>Brings up a table of all the Sources in the system.</dd>");
        sb.append("<dt><a href=\"#");
                sb.append(WsUriConstants.SOURCE_TARGET_INFOS);
                sb.append("\">");
                sb.append(WsUriConstants.SOURCE_TARGET_INFOS);
                sb.append("</a></dt>");
        sb.append("<dd>Brings up a table of all the Targets for a particular Source.</dd>");
        sb.append("<dt><a href=\"#");
                sb.append(SetMappings.METHOD_NAME);
                sb.append("\">");
                sb.append(SetMappings.METHOD_NAME);
                sb.append("</a></dt>");
        sb.append("<dd>Brings up a table of all the mappings in the system for a particular Source and Target.</dd>");
        //sb.append("<dt><a href=\"#");
        //        sb.append(WsUriConstants.GRAPHVIZ);
        //        sb.append("\">");
        //        sb.append(WsUriConstants.GRAPHVIZ);
        //        sb.append("</a></dt>");
        //sb.append("<dd>Brings up the getMappingInfo as graphviz input</dd>\n");           
        sb.append("<dt>");
                refLlens(sb);
                sb.append("</dt>");
        sb.append("<dd>Brings up a list of the Supported Lens</dd>\n");           
        sb.append("<dt><a href=\"#");
                sb.append(Lens.METHOD_NAME + WsUriConstants.RDF);
                sb.append("\">");
                sb.append(Lens.METHOD_NAME + WsUriConstants.RDF);
                sb.append("</a></dt>");
        sb.append("<ul>");
            sb.append("<li>Brings up the supported Lens in RDF format</li>\n");           
    }
    
    private final void describe_SourceInfos(StringBuilder sb, String contextPath) 
            throws BridgeDBException, UnsupportedEncodingException {
        sb.append("<h3><a name=\"");
            sb.append(WsUriConstants.SOURCE_INFOS);
            sb.append("\">");
            sb.append(WsUriConstants.SOURCE_INFOS);
            sb.append("</a></h3></dt>");
        sb.append("<ul>");
            sb.append("<li>Brings up a table of all the Sources in the system.</li>");
             sb.append("<li>Optional arguments</li>");
            sb.append("<ul>");
                parameterLensUri(sb);
            sb.append("</ul>");           
            addSourceExample(sb, contextPath, "Default Lens", false, false); 
            addSourceExample(sb, contextPath, "All Lens", false, true); 
            //addSourceExample(sb, contextPath, "XML version", true, false); 
        sb.append("</ul>");           
    }
   /*         sb.append("<dt><a href=\"#");
                sb.append(WsUriConstants.SOURCE_INFOS);
                sb.append("\">");
                sb.append(WsUriConstants.SOURCE_INFOS);
                sb.append("</a></dt>");
        sb.append("<dd>Brings up a table of all the Sources in the system.</dd>");
*/
    
    private void addSourceExample(StringBuilder sb, String contextPath, String exampleName, boolean xml, boolean lens) throws UnsupportedEncodingException{
        sb.append("<li>Example (");
        sb.append(exampleName);
        sb.append("): <a href=\"");
        sb.append(contextPath);
        sb.append(WsUriConstants.SOURCE_INFOS);
        if (xml){
            sb.append(WsUriConstants.XML);
        }
        if (lens){
            sb.append("?");
            sb.append(WsUriConstants.LENS_URI);
            sb.append("=");
            sb.append(Lens.ALL_LENS_NAME);                    
        }
        sb.append("\">");
        sb.append(WsUriConstants.SOURCE_INFOS);
        if (xml){
            sb.append(WsUriConstants.XML);
        }
        if (lens){
            sb.append("?");
            sb.append(WsUriConstants.LENS_URI);
            sb.append("=");
            sb.append(Lens.ALL_LENS_NAME);                    
        }
        sb.append("</a></li>");    
    }
            
    private final void describe_SourceTargetInfos(StringBuilder sb, String contextPath, String sourceSysCode) 
            throws BridgeDBException, UnsupportedEncodingException {
        sb.append("<h3><a name=\"");
            sb.append(WsUriConstants.SOURCE_TARGET_INFOS);
            sb.append("\">");
            sb.append(WsUriConstants.SOURCE_TARGET_INFOS);
            sb.append("</a></h3></dt>");
        sb.append("<ul>");
            sb.append("<li>Brings up a table of all the Targets for a particular Source.</li>");
            sb.append("<li>Required arguement:</li>");
            sb.append("<ul>");
                parameterSourceCode(sb);
            sb.append("</ul>");           
            sb.append("<li>Optional arguments</li>");
            sb.append("<ul>");
                parameterLensUri(sb);
            sb.append("</ul>");           
            addSourceTargetExample(sb, contextPath, "Default Lens", false, false, sourceSysCode); 
            addSourceTargetExample(sb, contextPath, "All Lens", false, true, sourceSysCode); 
            //addSourceTargetExample(sb, contextPath, "XML version", true, false, sourceSysCode); 
        sb.append("</ul>");           
    }
     
    private void addSourceTargetExample(StringBuilder sb, String contextPath, String exampleName, boolean xml, boolean lens, String sourceSysCode) throws UnsupportedEncodingException{
        sb.append("<li>Example (");
        sb.append(exampleName);
        sb.append("): <a href=\"");
        sb.append(contextPath);
        sb.append(WsUriConstants.SOURCE_TARGET_INFOS);
        if (xml){
            sb.append(WsUriConstants.XML);
        }
        sb.append(FIRST_SOURCE_PARAMETER);
        sb.append(URLEncoder.encode(sourceSysCode, "UTF-8"));
        if (lens){
            sb.append("&");
            sb.append(WsUriConstants.LENS_URI);
            sb.append("=");
            sb.append(Lens.ALL_LENS_NAME);                    
        }
        sb.append("\">");
        sb.append(WsUriConstants.SOURCE_TARGET_INFOS);
        if (xml){
            sb.append(WsUriConstants.XML);
        }
        sb.append(FIRST_SOURCE_PARAMETER);
        sb.append(sourceSysCode);
        if (lens){
            sb.append("&");
            sb.append(WsUriConstants.LENS_URI);
            sb.append("=");
            sb.append(Lens.ALL_LENS_NAME);                    
        }
        sb.append("</a></li>");    
    }

    private final void describe_MappingSet(StringBuilder sb, String contextPath, Xref first, String sourceSysCode, String targetSysCode) 
            throws BridgeDBException, UnsupportedEncodingException {
        sb.append("<h3><a name=\"");
                sb.append(SetMappings.METHOD_NAME);
                sb.append("\">");
                sb.append(SetMappings.METHOD_NAME);
                sb.append("</h3>");
        sb.append("<ul>");
            sb.append("<li>Brings up a table of all the mappings in the system for a particular Source and Target.</li>");
            sb.append("<li>Or Brings up the information for one mapping.</li>");
            appendXmlJsonHtml(sb);
            sb.append("<li>For a Single Mapping set append /Id.</li>");
            sb.append("<ul>");
                sb.append("<li>Example (one Set): <a href=\"");
                    sb.append(contextPath);
                    sb.append(SetMappings.METHOD_NAME);
                    sb.append("/1\">");
                    sb.append(SetMappings.METHOD_NAME);
                    sb.append("/1</a></li>");    
            sb.append("</ul>");
            sb.append("<li>For a Summary table for a Source Target pair.</li>");
            sb.append("<ul>");
                sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                    parameterSourceCode(sb);
                    parameterTargetCode(sb);
                    sb.append("<li>Note: These are required arguements Since April 2014 as this method did not scale well</li>");
                sb.append("</ul>");           
                sb.append("<li>Optional arguments</li>");
                sb.append("<ul>");
                    parameterLensUri(sb);
                sb.append("</ul>");           
                addMappingSetExample(sb, contextPath, "Default Lens", false, false, sourceSysCode, targetSysCode); 
                addMappingSetExample(sb, contextPath, "All Lens", false, true, sourceSysCode, targetSysCode); 
                addMappingSetExample(sb, contextPath, "XML version", true, false, sourceSysCode, targetSysCode); 
                addMappingSetExample(sb, contextPath, "All Lens XML version", true, false, sourceSysCode, targetSysCode); 
            sb.append("</ul>");
        sb.append("</ul>\n");
    }  

    private void addMappingSetExample(StringBuilder sb, String contextPath, String exampleName, boolean xml, boolean lens, String sourceSysCode, String targetSysCode) throws UnsupportedEncodingException{
        sb.append("<li>Example (");
        sb.append(exampleName);
        sb.append("): <a href=\"");
        sb.append(contextPath);
        sb.append(SetMappings.METHOD_NAME);
        if (xml){
            sb.append(WsUriConstants.XML);
        }
        sb.append(FIRST_SOURCE_PARAMETER);
        sb.append(URLEncoder.encode(sourceSysCode, "UTF-8"));
        sb.append(TARGET_PARAMETER);
        sb.append(URLEncoder.encode(targetSysCode, "UTF-8"));
        if (lens){
            sb.append("&");
            sb.append(WsUriConstants.LENS_URI);
            sb.append("=");
            sb.append(Lens.ALL_LENS_NAME);                    
        }
        sb.append("\">");
        sb.append(SetMappings.METHOD_NAME);
        if (xml){
            sb.append(WsUriConstants.XML);
        }
        sb.append(FIRST_SOURCE_PARAMETER);
        sb.append(sourceSysCode);
        sb.append(TARGET_PARAMETER);
        sb.append(targetSysCode);
        if (lens){
            sb.append("&");
            sb.append(WsUriConstants.LENS_URI);
            sb.append("=");
            sb.append(Lens.ALL_LENS_NAME);                    
        }
        sb.append("</a></li>");    
    }
            
    private final void describe_Graphviz(StringBuilder sb, String contextPath) throws BridgeDBException, UnsupportedEncodingException {
        sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.GRAPHVIZ);
                sb.append("\">");
                sb.append(WsUriConstants.GRAPHVIZ);
                sb.append("</h3>");
            sb.append("<h4>This method will now always throw an Exception! It did not scale.</h4>");  
            //sb.append("<ul>");
            //sb.append("<li>Brings up the getMappingInfo as graphviz input.</li>");
            //sb.append("<li>This method is underdevelopment. Formatting suggestions from Graphviz exports highly welcome.</li>");
            //sb.append("<li>This output can then used to create an image of the URISpaces mapped.</li>");
            //    sb.append("<ul>");
            //    sb.append("<li>Requires graphviz to be installed on your machine</li>");
            //    sb.append("<li>Save the output in a file. (ex imsMappings.dot)</li>");
            //    sb.append("<li>Call graphviz (ex dot -Tgif imsMappings.dot -o imsMappings.gif)</li>");
            //    sb.append("<li>Open output in your favourite viewer</li>");
            //    sb.append("</ul>");
            //sb.append("<li>This method is only for media type HTML. But output is plain text.</li>");
            //sb.append("<li>No arguements</li>");
            //sb.append("<li>Example: <a href=\"");
            //        sb.append(contextPath);
            //    sb.append(WsUriConstants.GRAPHVIZ);
            //    sb.append("\">");
            //    sb.append(WsUriConstants.GRAPHVIZ);
            //    sb.append("</a></li>");    
            //sb.append("</ul>\n");        
    }
   
    private void describe_dataSource(StringBuilder sb, String contextPath, String sysCode) 
           throws UnsupportedEncodingException, BridgeDBException {
         sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.DATA_SOURCE);
                sb.append("\">");
                sb.append(WsUriConstants.DATA_SOURCE);
                sb.append("/id</h3>");
            sb.append("<ul>");
            sb.append("<li>Obtain a dataSource</li>");
            appendXmlJsonAndHtml(sb); 
            sb.append("<li>Required arguements: </li>");
                sb.append("<ul>");
                sb.append("<li>Returns the DataSource and associated UriSpace(s) with a specific id.</li> ");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(contextPath);
                    sb.append(WsUriConstants.DATA_SOURCE);
                    sb.append("/");
                    sb.append(URLEncoder.encode(sysCode, "UTF-8"));
                    sb.append("\">");
                    sb.append(WsUriConstants.DATA_SOURCE);
                    sb.append("/");
                    sb.append(sysCode);
                    sb.append("</a></li>");    
            sb.append("</ul>\n");        
    }

    private void describe_toUris(StringBuilder sb, String contextPath, Xref xref) throws UnsupportedEncodingException {
         sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.TO_URIS);
                sb.append("\">");
                sb.append(WsUriConstants.TO_URIS);
                sb.append("</h3>");
            sb.append("<ul>");
                sb.append("<li>Returns the URIs associated with a Xref</li>");
                appendXmlJsonAndHtml(sb); 
                sb.append("<li>Required arguements: </li><ul>");
                    this.parameterID_CODE(sb);
                    sb.append("</ul>");
                sb.append("<li>Example: <a href=\"");
                    sb.append(contextPath);
                    sb.append(WsUriConstants.TO_URIS);
                    sb.append(FIRST_ID_PARAMETER);
                    sb.append(xref.getId());
                    sb.append(DATASOURCE_SYSTEM_CODE_PARAMETER);
                    sb.append(URLEncoder.encode(xref.getDataSource().getSystemCode(), "UTF-8"));
                    sb.append("\">");
                    sb.append(WsUriConstants.TO_URIS);
                    sb.append(FIRST_ID_PARAMETER);
                    sb.append(xref.getId());
                    sb.append(DATASOURCE_SYSTEM_CODE_PARAMETER);
                    sb.append(xref.getDataSource().getSystemCode());
                    sb.append("</a></li>\n");    
           sb.append("</ul>\n");        
    }
    
        private void describe_toXref(StringBuilder sb, String contextPath, String uri) 
            throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h3><a name=\"");
                sb.append(WsUriConstants.TO_XREF);
                sb.append("\">");
                sb.append(WsUriConstants.TO_XREF);
                sb.append("</a></h3>");
        sb.append("<ul>");
            sb.append("<li>Retrns the Xref for this URI</li>");
            appendXmlJsonHtml(sb); 
            sb.append("<li>Required arguements:</li>");
            sb.append("<ul>");
                parameterUri(sb);
                sb.append("</ul>");    
            this.mapExamplesUriBased1(sb, contextPath, WsUriConstants.TO_XREF, uri);
            sb.append("</ul>\n");
    }           

    private void describe_getOverallStatistics(StringBuilder sb, String contextPath) 
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
                sb.append(contextPath);
                    sb.append(WsUriConstants.GET_OVERALL_STATISTICS);
                    sb.append("\">");
                    sb.append(WsUriConstants.GET_OVERALL_STATISTICS);
                    sb.append("</a></li>");    
            sb.append("</ul>\n");        
    }

    private void describe_Lens(StringBuilder sb, String contextPath)
            throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h3><a name=\"");
        sb.append(Lens.METHOD_NAME);
        sb.append("\">");
        sb.append(Lens.METHOD_NAME);
        sb.append("</a></h3>");
        sb.append("<ul>");
            sb.append("<li>Brings up a list of the Supported Lens");
            sb.append("<li>Optional arguments</li><ul>");
                parameterLensUri(sb);
                sb.append("<ul>");
                    sb.append("<ll> If provided ");
                    refLensGroup(sb);
                    sb.append(" is ignored.</li>");
                sb.append("</ul>");
                parameterLensGroup(sb);
                sb.append("<ul>");
                    sb.append("<ll> If neither this nor ");
                    refLensUri(sb);
                    sb.append(" is provided the group ");
                    sb.append(LensTools.PUBLIC_GROUP_NAME);
                    sb.append(" is used. </li>");
                sb.append("</ul>");
            sb.append("</ul>");
            lensExamples(sb, Lens.METHOD_NAME,  contextPath, null);
        sb.append("</ul>\n");
    }        

    private void lensExamples(StringBuilder sb, String method, String contextPath, String formatName){
        sb.append("<li>");
        if (formatName != null){
            sb.append(formatName).append(" ");
        }
        sb.append("Examples: <ul>");
        sb.append("<li>No Parameter call <a href=\"");
            sb.append(contextPath);
            sb.append(method);
            addRdfFormat(sb,"?", formatName);
            sb.append("\">");
            sb.append(method);
            addRdfFormat(sb,"?", formatName);
            sb.append("</a></li>");    
        sb.append("<li>Get all the Lenses <a href=\"");
            sb.append(contextPath);
            sb.append(method);
            sb.append("?");
            sb.append(WsUriConstants.LENS_GROUP);
            sb.append("=");
            sb.append(LensTools.ALL_GROUP_NAME);
            addRdfFormat(sb,"&", formatName);
            sb.append("\">");
            sb.append(method);
            sb.append("?");
            sb.append(WsUriConstants.LENS_GROUP);
            sb.append("=");
            sb.append(LensTools.ALL_GROUP_NAME);
            addRdfFormat(sb,"&", formatName);
            sb.append("</a></li>");    
        sb.append("<li>Get all the default Lens. <a href=\"");
            sb.append(contextPath);
            sb.append(method);
            sb.append("?");
            sb.append(WsUriConstants.LENS_URI);
            sb.append("=");
            sb.append(Lens.DEFAULT_LENS_NAME);
            addRdfFormat(sb,"&", formatName);
            sb.append("\">");
            sb.append(method);
            sb.append("?");
            sb.append(WsUriConstants.LENS_URI);
            sb.append("=");
            sb.append(Lens.DEFAULT_LENS_NAME);
            addRdfFormat(sb,"&", formatName);
            sb.append("</a></li></ul>");    
    }

    private void addRdfFormat(StringBuilder sb, String prefix, String formatName){ 
        if (formatName != null){
            sb.append(prefix);
            sb.append(WsUriConstants.RDF_FORMAT);
            sb.append("=");
            sb.append(formatName);
        }
    }

    private void describe_LensRdf(StringBuilder sb, String contextPath)
            throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h3><a name=\"");
        sb.append(Lens.METHOD_NAME + WsUriConstants.RDF);
        sb.append("\">");
        sb.append(Lens.METHOD_NAME +  WsUriConstants.RDF);
        sb.append("</a></h3>");
        sb.append("<ul>");
            sb.append("<li>Same data as ");
                refLlens(sb);
                sb.append(" but presented at RDF.</dd>");
            sb.append("<li>Argument</li><ul>");
                parameterRdfFormat(sb);
                sb.append("</li></ul>");
             sb.append("<li>Optional arguments</li><ul>");
                parameterLensUri(sb);
                parameterLensGroup(sb);
                sb.append("<li>See:");
                    refLlens(sb);
                   sb.append(" for argument behaviour and defaults</li><ul>");
                sb.append("</ul>");
            sb.append("</ul>");
            for (String formatName:BridgeDbRdfTools.getAvaiableWriters()){
                lensExamples(sb, Lens.METHOD_NAME + WsUriConstants.RDF,  contextPath, formatName);
            }
        sb.append("</ul>\n");
    }        
            
    private void parameterID_CODE(StringBuilder sb){
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
    }
    
    private void parameterSourceCode(StringBuilder sb){
        sb.append("<li><a href=\"#");
        sb.append(WsUriConstants.SOURCE_DATASOURCE_SYSTEM_CODE);
        sb.append("\">");
        sb.append(WsUriConstants.SOURCE_DATASOURCE_SYSTEM_CODE);
        sb.append("</a></li>");
    }
    
    private void parameterTargetCode(StringBuilder sb){
        sb.append("<li><a href=\"#");
        sb.append(WsConstants.TARGET_DATASOURCE_SYSTEM_CODE);
        sb.append("\">");
        sb.append(WsConstants.TARGET_DATASOURCE_SYSTEM_CODE);
        sb.append("</a></li> ");
    }
    
    private void parameterUri(StringBuilder sb){
        sb.append("<li><a href=\"#");
        sb.append(WsUriConstants.URI);
        sb.append("\">");
        sb.append(WsUriConstants.URI);
        sb.append("</a></li>");
    }

    private void parameterLensUri(StringBuilder sb){
        sb.append("<li>");
        this.refLensUri(sb);
        sb.append("</li>");
    }

    private void parameterLensGroup(StringBuilder sb){
        sb.append("<li>");
        this.refLensGroup(sb);
        sb.append("</li> ");
    }

    private void parameterGraph(StringBuilder sb){
        sb.append("<li><a href=\"#");
        sb.append(WsUriConstants.GRAPH);
        sb.append("\">");
        sb.append(WsUriConstants.GRAPH);
        sb.append("</a></li> ");
    }

    private void parameterIncludeXrefResults(StringBuilder sb){
        sb.append("<li><a href=\"#");
        sb.append(WsUriConstants.INCLUDE_XREF_RESULTS);
        sb.append("\">");
        sb.append(WsUriConstants.INCLUDE_XREF_RESULTS);
        sb.append("</a></li> ");
    }

    private void parameterIncludeUriResults(StringBuilder sb){
        sb.append("<li><a href=\"#");
        sb.append(WsUriConstants.INCLUDE_URI_RESULTS);
        sb.append("\">");
        sb.append(WsUriConstants.INCLUDE_URI_RESULTS);
        sb.append("</a></li> ");
    }

    private void parameterTargetPattern(StringBuilder sb){
        sb.append("<li><a href=\"#");
        sb.append(WsUriConstants.TARGET_URI_PATTERN);
        sb.append("\">");
        sb.append(WsUriConstants.TARGET_URI_PATTERN);
        sb.append("</a></li> ");
    }

    private void parameterTextLimit(StringBuilder sb){
        sb.append("<li><a href=\"#");
            sb.append(WsConstants.TEXT);
            sb.append("\">");
            sb.append(WsConstants.TEXT);
            sb.append("</a></li>");
        sb.append("<li><a href=\"#");
            sb.append(WsConstants.LIMIT);
            sb.append("\">");
            sb.append(WsConstants.LIMIT);
            sb.append("</a> (default available)</li>");
    }

   private void parameterRdfFormat(StringBuilder sb){
        sb.append("<li><a href=\"#");
        sb.append(WsUriConstants.RDF_FORMAT);
        sb.append("\">");
        sb.append(WsUriConstants.RDF_FORMAT);
        sb.append("</a></li> ");
    }
   
    private void refMapUri(StringBuilder sb){
        sb.append("<a href=\"#");
        sb.append(WsUriConstants.MAP_URI);
        sb.append("\">");
        sb.append(WsUriConstants.MAP_URI);
        sb.append("</a>");
    }

    private void refMapBySet(StringBuilder sb){
        sb.append("<a href=\"#");
        sb.append(WsUriConstants.MAP_BY_SET);
        sb.append("\">");
        sb.append(WsUriConstants.MAP_BY_SET);
        sb.append("</a>");
    }

    private void refLlens(StringBuilder sb){
        sb.append("<a href=\"#");
        sb.append(Lens.METHOD_NAME);
        sb.append("\">");
        sb.append(Lens.METHOD_NAME);
        sb.append("</a>");
    }
    
    private void refLensUri(StringBuilder sb){
        sb.append("<a href=\"#");
        sb.append(WsUriConstants.LENS_URI);
        sb.append("\">");
        sb.append(WsUriConstants.LENS_URI);
        sb.append("</a>");
    }

    private void refLensGroup(StringBuilder sb){
        sb.append("<a href=\"#");
        sb.append(WsUriConstants.LENS_GROUP);
        sb.append("\">");
        sb.append(WsUriConstants.LENS_GROUP);
        sb.append("</a> ");
    }

    private void mapExamplesXrefbased(StringBuilder sb, String contextPath, String methodName, Xref sourceXref1, String tragetSysCode1, Xref sourceXref2) 
            throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<li>Example: <a href=\"");
            sb.append(contextPath);
            StringBuilder front = new StringBuilder(methodName);
            StringBuilder sbInnerPure = new StringBuilder(methodName);
            StringBuilder sbInnerEncoded = new StringBuilder(methodName);
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
            sb.append("</a></li>\n");    
        sb.append("<li>Example: <a href=\"");
            sb.append(contextPath);
            String targetPart = "&" + WsConstants.TARGET_DATASOURCE_SYSTEM_CODE + "=";
            sbInnerPure.append(targetPart);
            sbInnerEncoded.append(targetPart);
            sbInnerPure.append(tragetSysCode1);
            sbInnerEncoded.append(URLEncoder.encode(tragetSysCode1, "UTF-8"));
            sb.append(sbInnerEncoded.toString());
            sb.append("\">");
            sb.append(sbInnerPure.toString());
            sb.append("</a></li>");                    
     }
    
    private void mapExamplesUriBased(StringBuilder sb, String contextPath, String methodName, String sourceUri1, String sourceUri2, String targetUriSpace2) 
            throws UnsupportedEncodingException, BridgeDBException{
        mapExamplesUriBased1(sb, contextPath, methodName, sourceUri1);
        mapExamplesUriBased2(sb, contextPath, methodName, sourceUri1, sourceUri2);
        sb.append("<li>The by ");
        sb.append(WsUriConstants.GRAPH);
        sb.append(" and by ");
        sb.append(WsUriConstants.TARGET_URI_PATTERN);
        sb.append(" methods provide the same result as ");
        sb.append(EXAMPLE_GRAPH);
        sb.append(" has been set to the just the single ");
        sb.append(WsUriConstants.TARGET_URI_PATTERN);
        sb.append(" ");
        sb.append(targetUriSpace2);
        sb.append("</li>");
        mapExamplesUriBasedWithGraph(sb, contextPath, methodName, sourceUri2);
        mapExamplesUriBasedWithTarget(sb, contextPath, methodName, sourceUri2, targetUriSpace2);
        mapExamplesUriBased4(sb, contextPath, methodName, sourceUri1);
    }
    
    private void mapExamplesUriBased1(StringBuilder sb, String contextPath, String methodName, String sourceUri1) 
            throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<li>Example: <a href=\"");
        sb.append(contextPath);
        sb.append(methodName);
        sb.append(FIRST_URI_PARAMETER);
        sb.append(URLEncoder.encode(sourceUri1, "UTF-8"));
        sb.append("\">");
        sb.append(methodName);
        sb.append(FIRST_URI_PARAMETER);
        sb.append(sourceUri1);
        sb.append("</a></li>");    
    }
    
    private void mapExamplesUriBased2(StringBuilder sb, String contextPath, String methodName, String sourceUri1, String sourceUri2) 
            throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<li>Example: <a href=\"");
        sb.append(contextPath);
        sb.append(methodName);
        sb.append(FIRST_URI_PARAMETER);
        sb.append(URLEncoder.encode(sourceUri1, "UTF-8"));
        sb.append("&");
        sb.append(WsUriConstants.URI);
        sb.append("=");
        sb.append(URLEncoder.encode(sourceUri2, "UTF-8"));
        sb.append("\">");
        sb.append(methodName);
        sb.append(FIRST_URI_PARAMETER);
        sb.append(sourceUri1);
        sb.append("&");
        sb.append(WsUriConstants.URI);
        sb.append("=");
        sb.append(sourceUri2);
        sb.append("</a></li>");    
    }
    
    private void mapExamplesUriBasedWithGraph(StringBuilder sb, String contextPath, String methodName, 
            String sourceUri2) throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<li>Example: <a href=\"");
        sb.append(contextPath);
        sb.append(methodName);
        sb.append(FIRST_URI_PARAMETER);
        sb.append(URLEncoder.encode(sourceUri2, "UTF-8"));
        sb.append(GRAPH_PARAMETER);
        sb.append(URLEncoder.encode(EXAMPLE_GRAPH, "UTF-8"));
        sb.append("\">");
        sb.append(methodName);
        sb.append(FIRST_URI_PARAMETER);
        sb.append(sourceUri2);
        sb.append(GRAPH_PARAMETER);
        sb.append(EXAMPLE_GRAPH);
        sb.append("</a></li>");    
    }

   private void mapExamplesUriBasedWithTarget(StringBuilder sb, String contextPath, String methodName, String sourceUri2, String targetUriSpace2) 
            throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<li>Example: <a href=\"");
        sb.append(contextPath);
        sb.append(methodName);
        sb.append(FIRST_URI_PARAMETER);
        sb.append(URLEncoder.encode(sourceUri2, "UTF-8"));
        sb.append(TARGET_URI_PATTERN_PARAMETERX);
        sb.append(URLEncoder.encode(targetUriSpace2, "UTF-8"));
        sb.append("\">");
        sb.append(methodName);
        sb.append(FIRST_URI_PARAMETER);
        sb.append(sourceUri2);
        sb.append(TARGET_URI_PATTERN_PARAMETERX);
        sb.append(targetUriSpace2);
        sb.append("</a></li>");    
    }
    
    private void addDefaultLens(StringBuilder sb) throws BridgeDBException{
        sb.append("&");
        sb.append(WsUriConstants.LENS_URI);
        sb.append("=");
        sb.append(Lens.DEFAULT_LENS_NAME);
    }
    
    private void mapExamplesUriBased4(StringBuilder sb, String contextPath, String methodName, String sourceUri1) 
            throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<li>Default Lens: <a href=\"");
        sb.append(contextPath);
        sb.append(methodName);
        sb.append(FIRST_URI_PARAMETER);
        sb.append(URLEncoder.encode(sourceUri1, "UTF-8"));
        addDefaultLens(sb);
        sb.append("\">");
        sb.append(methodName);
        sb.append(FIRST_URI_PARAMETER);
        sb.append(sourceUri1);
        addDefaultLens(sb);
        sb.append("</a></li>");    
    }           

    private void mapExamplesUriBasedFormatted(StringBuilder sb, String contextPath, String methodName, String sourceUri1, 
            String RdfFormat) throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<li>Example: <a href=\"");
        sb.append(contextPath);
        sb.append(methodName);
        sb.append(FIRST_URI_PARAMETER);
        sb.append(URLEncoder.encode(sourceUri1, "UTF-8"));
        addDefaultLens(sb);
        sb.append("&");
        sb.append(WsUriConstants.RDF_FORMAT);
        sb.append("=");
        sb.append(RdfFormat);
        sb.append("\">");
        sb.append(methodName);
        sb.append(FIRST_URI_PARAMETER);
        sb.append(sourceUri1);
        addDefaultLens(sb);
        sb.append("&");
        sb.append(WsUriConstants.RDF_FORMAT);
        sb.append("=");
        sb.append(RdfFormat);
        sb.append("\">");
        sb.append("</a></li>");    
    }

    private void appendTextHtml(StringBuilder sb) {
        sb.append("<li>This method is available for media types TEXT.</li>");
        if (noConentOnEmpty){
            sb.append("<ul>");
            sb.append("<li>Status 204 (no Content) is never returned.</li>");
            sb.append("</ul>");
        }
        sb.append("<li>Request for media type HTML will return same text in a window.</li>");
        if (noConentOnEmpty){
            sb.append("<ul>");
            sb.append("<li>Empty RDF is returned if no data available.</li>");
            sb.append("</ul>");
        }
    }
 
    private void appendXmlJson(StringBuilder sb) {
        sb.append("<li>This method is available for media types XML and JSON.</li>");
        if (noConentOnEmpty){
            sb.append("<ul>");
            sb.append("<li>Status 204 (no Content) is never returned.</li>");
            sb.append("</ul>");
        }
        sb.append("<li>There is not media type HTML.</li>");
    }

    private void appendXmlJsonHtml(StringBuilder sb) {
        sb.append("<li>This method is available for media types XML, JSON and HTML.</li>");
        if (noConentOnEmpty){
            sb.append("<ul>");
            sb.append("<li>Status 204 (no Content) is return if no data available.</li>");
            sb.append("</ul>");
        }
        sb.append("<li>Request for media type HTML will a html formated page.</li>");
        if (noConentOnEmpty){
            sb.append("<ul>");
            sb.append("<li>An warning page is returned if no data available.</li>");
            sb.append("</ul>");
        }
    }
    
    private void appendXmlJsonAndHtml(StringBuilder sb) {
        sb.append("<li>This method is available for media types XML and JSON.</li>");
        if (noConentOnEmpty){
            sb.append("<ul>");
            sb.append("<li>Status 204 (no Content) is return if no data available.</li>");
            sb.append("</ul>");
        }
        sb.append("<li>Request for media type HTML will return XML Format.</li>");
        if (noConentOnEmpty){
            sb.append("<ul>");
            sb.append("<li>An warning page is returned if no data available.</li>");
            sb.append("</ul>");
        }
    }
}

