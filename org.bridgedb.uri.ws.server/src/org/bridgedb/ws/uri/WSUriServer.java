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
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.bridgedb.DataSource;
import org.bridgedb.rdf.BridgeDbRdfTools;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.uri.api.MappingsBySet;
import org.bridgedb.uri.api.SetMappings;
import org.bridgedb.uri.api.UriMapper;
import org.bridgedb.uri.lens.Lens;
import org.bridgedb.uri.lens.LensTools;
import org.bridgedb.uri.tools.GraphResolver;
import org.bridgedb.uri.tools.UriResultsAsRDF;
import org.bridgedb.uri.ws.WsUriConstants;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.ws.templates.WebTemplates;
import org.openrdf.model.Statement;

/**
 *
 * @author Christian
 */
public class WSUriServer extends WSAPI implements ServletContextListener{
    
    private static boolean EXCLUDE_GRAPH = false;
    private static boolean INCLUDE_GRAPH = true;

    private ServletContext context;

    private static final HashMap<String,Response> setMappings = new HashMap<String,Response>();

    static final Logger logger = Logger.getLogger(WSUriServer.class);

    public WSUriServer()  throws BridgeDBException   {
        this(SQLUriMapper.getExisting());
    }

    public WSUriServer(UriMapper uriMapper) throws BridgeDBException   {
        super(uriMapper);
        logger.info("WsUriServer setup");        
    }
    
    /**
     * Welcome page for the Serivce.
     * 
     * Expected to be overridden by the QueryExpander
     * 
     * @param httpServletRequest
     * @return
     * @throws BridgeDBException
     * @throws UnsupportedEncodingException 
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response welcomeMessage(@Context HttpServletRequest httpServletRequest) throws BridgeDBException, UnsupportedEncodingException {
        if (logger.isDebugEnabled()){
            logger.debug("welcomeMessage called!");
        }
        return bridgeDbHome(httpServletRequest);
    }

    /**
     * Welcome page for the Service.
     * 
     * Expected to be overridden by the QueryExpander
     * 
     * @param httpServletRequest
     * @return
     * @throws BridgeDBException
     * @throws UnsupportedEncodingException 
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/" + WsUriConstants.BRIDGEDB_HOME)
    public Response bridgeDbHome(@Context HttpServletRequest httpServletRequest) throws BridgeDBException, UnsupportedEncodingException {
        if (logger.isDebugEnabled()){
            logger.debug("bridgeDbHome called");
        }

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("targetUriPatterns", UriPattern.getUriPatterns());
        velocityContext.put("lenses", LensTools.getLens());
        String mapUriScripts = WebTemplates.getForm(velocityContext, WebTemplates.SELECTORS_SCRIPTS);
        StringBuilder sb = topAndSide ("Home page for BridgeDB WebServer", mapUriScripts, httpServletRequest);
        
        String mapUriForm = mapUriForm(EXCLUDE_GRAPH, httpServletRequest);
        
        velocityContext.put("api", WsUriConstants.BRIDGEDB_API);
        velocityContext.put("contextPath", httpServletRequest.getContextPath());
        velocityContext.put("getMappingInfo", SetMappings.METHOD_NAME);
        velocityContext.put("map",WsUriConstants.MAP);
        velocityContext.put("mapURI", WsUriConstants.MAP_URI);
        velocityContext.put("mapUriForm", mapUriForm);
        sb.append( WebTemplates.getForm(velocityContext, WebTemplates.BRIDGEDB_HOME));
         footerAndEnd(sb);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/mapURI") 
    public Response mapURI(@Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        if (logger.isDebugEnabled()){
            logger.debug("mapURI called");
        }

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("targetUriPatterns", UriPattern.getUriPatterns());
        velocityContext.put("lenses", LensTools.getLens());
        String mapUriScripts = WebTemplates.getForm(velocityContext, WebTemplates.SELECTORS_SCRIPTS);
        StringBuilder sb = topAndSide ("mapURI Service", mapUriScripts, httpServletRequest);
        sb.append(mapUriForm(INCLUDE_GRAPH, httpServletRequest));
        footerAndEnd(sb);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }
    
    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + WsUriConstants.DATA_SOURCE + "/{id}")
    public Response getDataSourceHtml(@PathParam("id") String id,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        DataSource ds = DataSource.getExistingBySystemCode(id);
        if (noConentOnEmpty & ds == null){
            return noContentWrapper(httpServletRequest);
        } 
        Set<String> uriPatterns = uriMapper.getUriPatterns(id);
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("dataSource", ds);
        velocityContext.put("id", "$id");
        velocityContext.put("Patterns", uriPatterns);
        String dataSourceInfo = WebTemplates.getForm(velocityContext, WebTemplates.DATA_SOURCE_SCRIPT);
        StringBuilder sb = topAndSide ("Data Source " + id + " Summary", httpServletRequest);
        sb.append(dataSourceInfo);
        footerAndEnd(sb);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/" + WsUriConstants.SOURCE_INFOS) 
    public Response getSourceInfosHtml(@QueryParam(WsUriConstants.LENS_URI) String lensUri,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        if (logger.isDebugEnabled()){
            logger.debug("getSourceInfosHtml called");
        }
        return getSourceInfosHtml(lensUri, httpServletRequest, null);
    }
    
    private Response getSourceInfosHtml(String lensUri, HttpServletRequest httpServletRequest, String message) throws BridgeDBException {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("SourceInfos", uriMapper.getSourceInfos(lensUri));
        velocityContext.put("lens", lensUri);
        velocityContext.put("contextPath", httpServletRequest.getContextPath() );
        velocityContext.put("message", message);
        String sourceInfo = WebTemplates.getForm(velocityContext, WebTemplates.SOURCE_INFO_SCRIPT);
        StringBuilder sb = topAndSide ("Data Source Summary", httpServletRequest);
        sb.append(sourceInfo);
        footerAndEnd(sb);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/" + WsUriConstants.SOURCE_TARGET_INFOS) 
    public Response getSourceTargetInfosHtml(@QueryParam(WsUriConstants.SOURCE_DATASOURCE_SYSTEM_CODE) String scrCode,
            @QueryParam(WsUriConstants.LENS_URI) String lensUri,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        if (logger.isDebugEnabled()){
            logger.debug("getSourceTargetInfosHtml called with " + scrCode);
        }
        if (scrCode == null || scrCode.isEmpty()){
            return getSourceInfosHtml(lensUri, httpServletRequest, 
                    "Due to the size of your request only a summary by source is being shown");
        }
        return getSourceTargetInfosHtml(scrCode, lensUri, httpServletRequest, null);
    }

    private Response getSourceTargetInfosHtml(String scrCode, String lensUri,
            HttpServletRequest httpServletRequest, String message) throws BridgeDBException {        
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("SourceTargetInfos", uriMapper.getSourceTargetInfos(scrCode, lensUri));
        velocityContext.put("scrCode", scrCode);
        velocityContext.put("contextPath", httpServletRequest.getContextPath() );
        velocityContext.put("lens", lensUri);
        velocityContext.put("message", message);
        String sourceTargetInfo = WebTemplates.getForm(velocityContext, WebTemplates.SOURCE_TARGET_INFO_SCRIPT);
        StringBuilder sb = topAndSide ("Data Source Summary for " + scrCode, httpServletRequest);
        sb.append(sourceTargetInfo);
        footerAndEnd(sb);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + SetMappings.METHOD_NAME) 
    public Response getMappingSetInfosHtml(@QueryParam(WsUriConstants.SOURCE_DATASOURCE_SYSTEM_CODE) String scrCode,
            @QueryParam(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE) String targetCode,
            @QueryParam(WsUriConstants.LENS_URI) String lensUri,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        if (logger.isDebugEnabled()){
            logger.debug("getMappingSetInfosHtml called with " + scrCode + " and " + targetCode);
        }
        if (scrCode == null || scrCode.isEmpty()){
            return getSourceInfosHtml(lensUri, httpServletRequest, "Due to the size of your request only a summary by source is being shown");
        }
        if (targetCode == null || targetCode.isEmpty()){
            return getSourceTargetInfosHtml(scrCode, lensUri, httpServletRequest, 
                    "Due to the size of your request only a summary by target is being shown");
        }
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("MappingSetInfos", uriMapper.getMappingSetInfos(scrCode, targetCode, lensUri));
        velocityContext.put("scrCode", scrCode);
        velocityContext.put("targetCode", targetCode);
        velocityContext.put("contextPath", httpServletRequest.getContextPath() );
        velocityContext.put("lens", lensUri);
        String mappingSetInfo = WebTemplates.getForm(velocityContext, WebTemplates.MAPPING_SET_INFO_SCRIPT);
        StringBuilder sb = topAndSide ("Mapping Summary for " + scrCode + " -> " + targetCode, httpServletRequest);
        sb.append(mappingSetInfo);
        footerAndEnd(sb);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + SetMappings.METHOD_NAME + "/{id}")
    public Response getMappingSetInfo(@PathParam("id") String idString,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {  
        if (idString == null) {
            throw new BridgeDBException("Path parameter missing.");
        }
        if (idString.isEmpty()) {
            throw new BridgeDBException("Path parameter may not be null.");
        }
        int id = Integer.parseInt(idString);
        MappingSetInfo info = uriMapper.getMappingSetInfo(id);
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("mappingSetInfo", info);
        String mappingSetInfo = WebTemplates.getForm(velocityContext, WebTemplates.MAPPING_SET_SCRIPT);
        StringBuilder sb = topAndSide ("Mapping Set " + id, httpServletRequest);
        sb.append(mappingSetInfo);
        footerAndEnd(sb);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }
    
    private String mapUriForm(boolean includeGraph, HttpServletRequest httpServletRequest) throws BridgeDBException{
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("targetUriPatterns", UriPattern.getUriPatterns());
        velocityContext.put("lenses", LensTools.getLens());        
        velocityContext.put("contextPath", httpServletRequest.getContextPath());
        velocityContext.put("defaultLens", LensTools.byId(Lens.DEFAULT_LENS_NAME));
        velocityContext.put("formatName", WsUriConstants.FORMAT);
        if (includeGraph){
            velocityContext.put("graphName", WsUriConstants.GRAPH); 
            velocityContext.put("graphs", GraphResolver.knownGraphs());
        }
        velocityContext.put("lenses", LensTools.getLens());
        velocityContext.put("lensURIName", WsUriConstants.LENS_URI);
        velocityContext.put("mapURI", WsUriConstants.MAP_URI);
        velocityContext.put("targetUriPatternName", WsUriConstants.TARGET_URI_PATTERN);
        velocityContext.put("URI", WsUriConstants.URI);
        return WebTemplates.getForm(velocityContext, WebTemplates.MAP_URI_FORM);
    }
    
     /**
     * Forwarding page for "/api".
     * 
     * This is expected to be overwirriten by the QueryExpander
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
    
    /**
     * @deprecated 
     * @param sb
     * @param mappingSetInfos
     * @param httpServletRequest
     * @throws BridgeDBException 
     */
    protected void addMappingTable(StringBuilder sb, List<MappingSetInfo> mappingSetInfos, HttpServletRequest httpServletRequest) 
            throws BridgeDBException{
        MappingSetTableMaker maker = new MappingSetTableMaker(mappingSetInfos, httpServletRequest);
        maker.tableMaker(sb);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/" + Lens.METHOD_NAME) 
	public Response getLensesHtml(@QueryParam(WsUriConstants.LENS_URI)  String lensUri,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        List<Lens> lenses = getTheLens(lensUri);
        StringBuilder sb = topAndSide("Lens Summary",  httpServletRequest);
        sb.append("\n<table border=\"1\">");
        sb.append("<tr>");
        sb.append("<th>Name</th>");
        sb.append("<th>URI</th>");
        sb.append("<th>Description</th></tr>\n");
		for (Lens lens:lenses) {
            sb.append("<tr><td>");
            sb.append(lens.getName());
            sb.append("</td><td><a href=\"");
            sb.append(lens.toUri(httpServletRequest.getContextPath()));
            sb.append("\">");
            sb.append(lens.toUri(httpServletRequest.getContextPath()));
            sb.append("</a></td><td>").append(lens.getDescription()).append("</td></tr>\n");        
		}
        sb.append("</table>");
        sb.append("<p><a href=\"");
        sb.append(httpServletRequest.getContextPath());
        sb.append("/");
        sb.append(Lens.METHOD_NAME);
        sb.append(WsUriConstants.XML);
        sb.append("\">");
        sb.append("XML Format");
        sb.append("</a></p>\n");        
        footerAndEnd(sb);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
	}
    
    /**
     * Not longer works as it did not scale
     * @deprecated Will now always throw an Exception
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/" + WsUriConstants.GRAPHVIZ)
    public Response graphvizDot(@QueryParam(WsUriConstants.LENS_URI) String lensUri) 
            throws BridgeDBException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        List<MappingSetInfo> rawProvenaceinfos = uriMapper.getMappingSetInfos(null, null, lensUri);
        SourceTargetCounter sourceTargetCounter = new SourceTargetCounter(rawProvenaceinfos);
        sb.append("digraph G {");
        for (MappingSetInfo info:sourceTargetCounter.getSummaryInfos()){
            if (info.getSource().compareTo(info.getTarget()) < 0 ){
                sb.append("\"");
                sb.append(info.getSource().getFullName());
                sb.append("\" -> \"");
                sb.append(info.getTarget().getFullName());
                sb.append("\" [dir = both, label=\"");
                sb.append(formatter.format(info.getNumberOfLinks()) + "(" + info.getStringId() + ")"); 
                sb.append("\"");
                if (info.isTransitive()){
                    sb.append(", style=dashed");
                }
                sb.append("];\n");
            }
        }
        sb.append("}"); 
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }
    
    @GET
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/" + WsUriConstants.MAP_BY_SET + WsUriConstants.RDF)
    public Response mapBySetRdfText(@QueryParam(WsUriConstants.URI) List<String> uris,
     		@QueryParam(WsUriConstants.LENS_URI) String lensUri,
            @QueryParam(WsUriConstants.GRAPH) String graph,
            @QueryParam(WsUriConstants.TARGET_URI_PATTERN) List<String> targetUriPatterns,
            @QueryParam(WsUriConstants.RDF_FORMAT) String formatName,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        MappingsBySet mappingsBySet = mapBySetInner(uris, lensUri, graph, targetUriPatterns);
        if (mappingsBySet.isEmpty()){
            return Response.noContent().build();
        } else {
            String rdf = UriResultsAsRDF.toRDF(mappingsBySet, formatName, getBaseUri(httpServletRequest));     
            return Response.ok(rdf, MediaType.TEXT_PLAIN_TYPE).build();
        }
    }
    
    private void generateTextarea(StringBuilder sb, String fieldName, String text) {
        sb.append("<p>").append(fieldName);
    	sb.append("<br/><textarea rows=\"40\" name=\"").append(fieldName)
                .append("\" style=\"width:100%; background-color: #EEEEFF;\">");
        if (text != null){
            sb.append(text);
        }
        sb.append("</textarea></p>\n");
    }
    
    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + WsUriConstants.MAP_BY_SET + WsUriConstants.RDF)
    public Response mapBySetRdfHtml(@QueryParam(WsUriConstants.URI) List<String> uris,
     		@QueryParam(WsUriConstants.LENS_URI) String lensUri,
            @QueryParam(WsUriConstants.GRAPH) String graph,
            @QueryParam(WsUriConstants.TARGET_URI_PATTERN) List<String> targetUriPatterns,
            @QueryParam(WsUriConstants.RDF_FORMAT) String formatName,
            @Context HttpServletRequest httpServletRequest
            ) throws BridgeDBException {
        MappingsBySet mappingsBySet = mapBySetInner(uris, lensUri, graph, targetUriPatterns);
        StringBuilder sb = topAndSide("HTML friendly " + WsUriConstants.MAP_BY_SET + WsUriConstants.RDF + " Output",  httpServletRequest);
        sb.append("<h2>Warning unlike ");
        sb.append(WsUriConstants.MAP_BY_SET);
        sb.append(" this method does not include any protential mapping to self.</h2>");
        sb.append("<h4>Use MediaType.TEXT_PLAIN to remove HTML stuff</h4>");
        sb.append("<p>Warning MediaType.TEXT_PLAIN version returns status 204 if no mappings found.</p>");
        generateTextarea(sb, "RDF", UriResultsAsRDF.toRDF(mappingsBySet, formatName, getBaseUri(httpServletRequest)));
        footerAndEnd(sb);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // TODO Auto-generated method stub	
    }
    
    @GET
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/" + Lens.METHOD_NAME + WsUriConstants.RDF) 
    public Response lensRdfText(@QueryParam(WsUriConstants.RDF_FORMAT) String formatName,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        Set<Statement> statements = LensTools.getLensAsRdf(getBaseUri(httpServletRequest));
        if (statements.isEmpty()){
            return Response.noContent().build();
        } else {
            String rdf = BridgeDbRdfTools.writeRDF(statements, formatName);     
            return Response.ok(rdf, MediaType.TEXT_PLAIN_TYPE).build();
        }
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + Lens.METHOD_NAME + WsUriConstants.RDF) 
    public Response lensRdfHtml(@QueryParam(WsUriConstants.RDF_FORMAT) String formatName,
            @Context HttpServletRequest httpServletRequest
            ) throws BridgeDBException {
        Set<Statement> statements = LensTools.getLensAsRdf(getBaseUri(httpServletRequest));
        StringBuilder sb = topAndSide("HTML friendly " + WsUriConstants.MAP_BY_SET + WsUriConstants.RDF + " Output",  httpServletRequest);
        sb.append("<h2>Warning unlike ");
        sb.append(WsUriConstants.MAP_BY_SET);
        sb.append(" this method does not include any protential mapping to self.</h2>");
        sb.append("<h4>Use MediaType.TEXT_PLAIN to remove HTML stuff</h4>");
        sb.append("<p>Warning MediaType.TEXT_PLAIN version returns status 204 if no mappings found.</p>");
        String rdf = BridgeDbRdfTools.writeRDF(statements, formatName);   
        generateTextarea(sb, "RDF", rdf);
        footerAndEnd(sb);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }
    
    /**
     * Listen for servlet initialization in web.xml and set the context for use in
     * the velocity templates
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        this.context = servletContextEvent.getServletContext();
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + WsUriConstants.MAP_BY_SET)
    public Response mapBySetHtml(@QueryParam(WsUriConstants.URI) List<String> uris,
     		@QueryParam(WsUriConstants.LENS_URI) String lensUri,
            @QueryParam(WsUriConstants.GRAPH) String graph,
            @QueryParam(WsUriConstants.TARGET_URI_PATTERN) List<String> targetUriPatterns,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        Response result = mapBySet(uris, lensUri, graph, targetUriPatterns);
        if (noConentOnEmpty & result.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            return noContentWrapper(httpServletRequest);
        }
        return result;
    }

    @Override
    protected Response noContentWrapper(HttpServletRequest httpServletRequest) {
        StringBuilder sb = topAndSide ("Empty Reply", httpServletRequest);
        sb.append("<h1>Reply is an Empty Set or Empty Object</h1>\n");
        sb.append("<h2>Note: The XML and Json versions of this request simply return status 204 (No Context)</h2>");
        footerAndEnd(sb);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
   }

    private String getBaseUri(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() + ":" + httpServletRequest.getServerPort() + httpServletRequest.getRequestURI();
    }
}


