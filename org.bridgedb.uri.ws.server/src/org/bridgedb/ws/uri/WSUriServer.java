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

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.core.Variant.VariantListBuilder;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.bridgedb.DataSource;
import org.bridgedb.rdf.BridgeDbRdfTools;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.uri.api.Mapping;
import org.bridgedb.uri.api.MappingsBySet;
import org.bridgedb.uri.api.UriMapper;
import org.bridgedb.uri.lens.Lens;
import org.bridgedb.uri.lens.LensTools;
import org.bridgedb.uri.tools.DirectStatementMaker;
import org.bridgedb.uri.tools.GraphResolver;
import org.bridgedb.uri.tools.RegexUriPattern;
import org.bridgedb.uri.tools.StatementMaker;
import org.bridgedb.uri.ws.WsUriConstants;
import org.bridgedb.uri.ws.bean.URISpacesInGraphBean;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.ws.WsConstants;
import org.bridgedb.ws.templates.WebTemplates;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFFormat;

import com.sun.jersey.core.header.MediaTypes;

/**
 *
 * @author Christian
 */
public class WSUriServer extends WSAPI {
    
    private static boolean EXCLUDE_GRAPH = false;
    private static boolean INCLUDE_GRAPH = true;
    private final String GET_BASE_URI_FROM_CONTEXT = null;
    private final String DO_NOT_CONVERT_TO_RDF = null;
    private final boolean XREF_DATA_NOT_REQUIRED = false;

    private static final HashMap<String,Response> setMappings = new HashMap<String,Response>();
    private final StatementMaker statementMaker;
    
    static final Logger logger = Logger.getLogger(WSUriServer.class);

    static List<RDFFormat> rdfFormats = new ArrayList<>();
    {{
    	rdfFormats.add(RDFFormat.N3);
    	rdfFormats.add(RDFFormat.NQUADS);
    	rdfFormats.add(RDFFormat.NTRIPLES);
    	rdfFormats.add(RDFFormat.JSONLD);
    	rdfFormats.add(RDFFormat.RDFA);
    	rdfFormats.add(RDFFormat.RDFJSON);
    	rdfFormats.add(RDFFormat.RDFXML);
    	rdfFormats.add(RDFFormat.TRIG);
    	rdfFormats.add(RDFFormat.TRIX);
    	rdfFormats.add(RDFFormat.TURTLE);
    }}

    public WSUriServer()  throws BridgeDBException   {
        this(SQLUriMapper.getExisting(), new DirectStatementMaker());
    }

    public WSUriServer(UriMapper uriMapper, StatementMaker statementMaker) throws BridgeDBException   {
        super(uriMapper);
        logger.info("WsUriServer setup");    
        this.statementMaker = statementMaker;
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
        velocityContext.put("lenses", LensTools.getLens(LensTools.ALL_GROUP_NAME));
        String mapUriScripts = WebTemplates.getForm(velocityContext, WebTemplates.SELECTORS_SCRIPTS);
        
        String mapUriForm = mapUriForm(EXCLUDE_GRAPH, httpServletRequest);
        
        velocityContext.put("api", WsUriConstants.BRIDGEDB_API);
        velocityContext.put("contextPath", httpServletRequest.getContextPath());
        velocityContext.put("getMappingInfo", WsUriConstants.MAPPING_SET);
        velocityContext.put("map",WsUriConstants.MAP);
        velocityContext.put("mapURI", WsUriConstants.MAP_URI);
        velocityContext.put("mapUriForm", mapUriForm);

        String mainBody = WebTemplates.getForm(velocityContext, WebTemplates.BRIDGEDB_HOME); 
        String fullPage = this.createHtmlPage("Home page for BridgeDb WebServer", mainBody, httpServletRequest);
        return Response.ok(fullPage, MediaType.TEXT_HTML).build();        
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
        velocityContext.put("lenses", LensTools.getLens(LensTools.ALL_GROUP_NAME));

        String mainBody = WebTemplates.getForm(velocityContext, WebTemplates.SELECTORS_SCRIPTS); 
        String fullPage = this.createHtmlPage("mapURI Service", mainBody, httpServletRequest);
        return Response.ok(fullPage, MediaType.TEXT_HTML).build();       
    }
    
    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + WsUriConstants.DATA_SOURCE + "/{id}")
    public Response getDataSourceHtml(@PathParam("id") String id,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        DataSource ds = DataSource.getExistingBySystemCode(id);
        if (noContentOnEmpty & ds == null){
            return noContentWrapper(httpServletRequest);
        } 
        Set<String> uriPatterns = uriMapper.getUriPatterns(id);
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("dataSource", ds);
        velocityContext.put("id", "$id");
        velocityContext.put("Patterns", uriPatterns);

        String mainBody = WebTemplates.getForm(velocityContext, WebTemplates.DATA_SOURCE_SCRIPT); 
        String fullPage = this.createHtmlPage("Data Source " + id + " Summary", mainBody, httpServletRequest);
        return Response.ok(fullPage, MediaType.TEXT_HTML).build();       
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

        String mainBody = WebTemplates.getForm(velocityContext, WebTemplates.SOURCE_INFO_SCRIPT); 
        String fullPage = this.createHtmlPage("Data Source Summary", mainBody, httpServletRequest);
        return Response.ok(fullPage, MediaType.TEXT_HTML).build();       
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

        String mainBody = WebTemplates.getForm(velocityContext, WebTemplates.SOURCE_TARGET_INFO_SCRIPT); 
        String fullPage = this.createHtmlPage("Data Source Summary for " + scrCode, mainBody, httpServletRequest);
        return Response.ok(fullPage, MediaType.TEXT_HTML).build();       
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + WsUriConstants.MAPPING_SET) 
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

        String mainBody = WebTemplates.getForm(velocityContext, WebTemplates.MAPPING_SET_INFO_SCRIPT);
        String title = "Mapping Summary for " + scrCode + " -> " + targetCode;
        String fullPage = this.createHtmlPage(title, mainBody, httpServletRequest);
        return Response.ok(fullPage, MediaType.TEXT_HTML).build();       
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + WsUriConstants.MAPPING_SET + "/{id}")
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

        String mainBody = WebTemplates.getForm(velocityContext, WebTemplates.MAPPING_SET_SCRIPT); 
        String fullPage = this.createHtmlPage("Mapping Set " + id, mainBody, httpServletRequest);
        return Response.ok(fullPage, MediaType.TEXT_HTML).build();       
    }
    
    @GET
    @Path("/" + WsUriConstants.MAPPING_SET + WsUriConstants.RDF + "/{id}")
    @Produces(MediaType.TEXT_HTML)
    public Response getMappingSetRdfHtml(@PathParam("id") String idString,  @Context HttpServletRequest httpServletRequest) 
            throws BridgeDBException{
        return getMappingSetRdfHtml(idString, GET_BASE_URI_FROM_CONTEXT, DO_NOT_CONVERT_TO_RDF, httpServletRequest);
    }
    
    @GET
    @Path("/" + WsUriConstants.MAPPING_SET + WsUriConstants.RDF + "/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getMappingSetRdfText(@PathParam("id") String idString,  
    		@Context Request request,
    		@Context HttpServletRequest httpServletRequest) 
            throws BridgeDBException{
        return getMappingSetRdfText(idString, GET_BASE_URI_FROM_CONTEXT, DO_NOT_CONVERT_TO_RDF, request, httpServletRequest);
    }
    
    @GET
    @Path("/" + WsUriConstants.MAPPING_SET + WsUriConstants.RDF)
    @Produces({MediaType.TEXT_PLAIN, 
    	"text/turtle",
    	"application/rdf+xml",
    	"application/n-triples",
    	"application/ld+json",
    	"application/trig"
    	})    
    public Response getMappingSetRdfText(@QueryParam(WsConstants.ID) String idString, 
            @QueryParam(WsUriConstants.BASE_URI) String baseUri,
            @QueryParam(WsUriConstants.RDF_FORMAT) String formatName,
            @Context Request request,
            @Context HttpServletRequest httpServletRequest) 
            throws BridgeDBException{
        baseUri = checkBaseUri(baseUri, httpServletRequest);
        String context = checkContext(baseUri, httpServletRequest);
        Set<Statement> statements = getMappingSetStatements(idString, baseUri, context);
        if (noContentOnEmpty & statements.isEmpty()){
            return Response.noContent().build();
        } 
        
        List<MediaType> types = new ArrayList<MediaType>();
        for (RDFFormat f: rdfFormats){
        	types.add(MediaType.valueOf(f.getDefaultMIMEType()));
        	types.addAll(MediaTypes.createMediaTypes(f.getMIMETypes().toArray(new String[]{})));
        }
        List<Variant> variants = VariantListBuilder.newInstance().mediaTypes(types.toArray(new MediaType[]{})).build();        
        Variant variant = request.selectVariant(variants);

        RDFFormat format = RDFFormat.matchMIMEType(variant.getMediaType().toString(), rdfFormats).get();
        String rdfInfo = BridgeDbRdfTools.writeRDF(statements, format); 
        return Response.ok(rdfInfo, MediaType.TEXT_PLAIN_TYPE).build();
    }    
    


    @GET
    @Path("/" + WsUriConstants.MAPPING_SET + WsUriConstants.RDF)
    @Produces(MediaType.TEXT_HTML)
    public Response getMappingSetRdfHtml(@QueryParam(WsConstants.ID) String idString, 
            @QueryParam(WsUriConstants.BASE_URI) String baseUri,
            @QueryParam(WsUriConstants.RDF_FORMAT) String formatName,
            @Context HttpServletRequest httpServletRequest) 
            throws BridgeDBException{        
        StringBuilder sb = new StringBuilder();
        baseUri = checkBaseUri(baseUri, httpServletRequest);
        String context = checkContext(baseUri, httpServletRequest);
        Set<Statement> statements = getMappingSetStatements(idString, baseUri, context);
        sb.append("<h4>Use MediaType.TEXT_PLAIN to return remove HTML stuff</h4>");
        sb.append("<p>Warning MediaType.TEXT_PLAIN version returns status 204 if no mappings found.</p>");
        RDFFormat format = RDFFormat.matchMIMEType(formatName, rdfFormats).get();
        if (formatName != null || formatName != null){
            generateTextarea(sb, "RDF", BridgeDbRdfTools.writeRDF(statements, format));
        } else {
            sb.append("<p>Warning MediaType.TEXT_PLAIN version returns RDF using the default format even if no format specified.</p>");
            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("statements", statements);
            velocityContext.put("subject", WsUriConstants.MAPPING_SET);
            sb.append(WebTemplates.getForm(velocityContext, WebTemplates.RDF_QUAD_SCRIPT));
        }        

        String fullPage = this.createHtmlPage("MappingSet " + idString, sb.toString(), httpServletRequest);
        return Response.ok(fullPage, MediaType.TEXT_HTML).build();       
    }
    
    private int[] splitId(String idString) throws BridgeDBException{
        String[] stringIds = idString.split("_");
        int[] ids = new int[stringIds.length];
        for (int i = 0; i< ids.length; i++){
            try{
                ids[i] = Integer.parseInt(stringIds[i]);
            } catch (NumberFormatException ex){
                throw new BridgeDBException("Illegal id String: " + idString 
                        + " Expected 1 or more numbers seperated by underscore. ", ex);
            }
        } 
        return ids;
    }
    
    /**
     * Gets known Statements about the mappingSet.
     * 
     * This method is expected to be overwritten by services like the Open PHACTS IMS 
     * that have better rdf data about the mappingSet
     * 
     * @param idString
     * @param baseUri
     * @return
     * @throws BridgeDBException 
     */
    protected Set<Statement> getMappingSetStatements(String idString, String baseUri, String context) 
            throws BridgeDBException{
        if (idString == null || idString.isEmpty()){
            throw new BridgeDBException (WsConstants.ID + " parameter is missing");
        }
        int[] ids = splitId(idString);
        if (ids.length == 1){
            return statementMaker.asRDF(uriMapper.getMappingSetInfo(ids[0]), baseUri, context);
        } else  {
            Set<Statement> statements = new HashSet<Statement>();
            for (int id:ids){
                statements.addAll(statementMaker.asRDF(uriMapper.getMappingSetInfo(id), baseUri, context));
            }
            return statements;       
        }       
     }
    
    private Set<Statement> getMappingSetStatements(Set<Mapping> mappings, String baseUri, String context) throws BridgeDBException {
        Set<Statement> results = new HashSet<Statement>();
        for (Mapping mapping:mappings){
            if (mapping.getPredicate() != null){
                results.addAll(getMappingSetStatements(mapping.getMappingSetId(), baseUri, context));
            }
        }
        return results;
    }

    private String checkBaseUri(String baseUri, HttpServletRequest httpServletRequest) 
            throws BridgeDBException{
        if (baseUri == null || baseUri.isEmpty()){
            StringBuffer url = httpServletRequest.getRequestURL();
            return url.substring(0, url.length()- httpServletRequest.getPathInfo().length() + 1);
        }
        return baseUri;       
    }
    
    private String checkContext(String baseUri, HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getQueryString() == null){
            return baseUri + httpServletRequest.getPathInfo();
        } else {
            return baseUri + httpServletRequest.getPathInfo() + "?" +  httpServletRequest.getQueryString();       
        }
    }
    
    @GET
    @Path("/" + WsUriConstants.MAP_URI + WsUriConstants.RDF)
    @Produces(MediaType.TEXT_HTML)
    public Response mapUriRdfHtml(
    		@QueryParam(WsUriConstants.URI) List<String> uris,
     		@QueryParam(WsUriConstants.LENS_URI) String lensUri,
            @QueryParam(WsUriConstants.GRAPH) String graph,
            @QueryParam(WsUriConstants.TARGET_URI_PATTERN) List<String> targetUriPatterns, 
            @QueryParam(WsUriConstants.BASE_URI) String baseUri,
            @QueryParam(WsUriConstants.RDF_FORMAT) String formatName,
            @QueryParam(WsUriConstants.LINKSET_INFO) Boolean linksetInfo,
            @QueryParam(WsUriConstants.OVERRIDE_PREDICATE_URI) String overridePredicateURI,
            @Context HttpServletRequest httpServletRequest) 
            throws BridgeDBException{  
        Set<Statement> statements = this.mapUriRdfInner(uris, lensUri, graph, targetUriPatterns, baseUri, formatName, 
                linksetInfo, overridePredicateURI, httpServletRequest);
        String fullPage;
        if (formatName != null || formatName != null){
            StringBuilder sb = new StringBuilder();
            RDFFormat format = RDFFormat.matchMIMEType(formatName, rdfFormats).get();
            generateTextarea(sb, "RDF", BridgeDbRdfTools.writeRDF(statements, format));
            fullPage = this.createHtmlPage(WsUriConstants.MAP_URI + " as RDF", sb.toString(), httpServletRequest);
        } else {
            String contextPath = httpServletRequest.getContextPath();
            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("statements", statements);
            velocityContext.put("subject", WsUriConstants.MAP_URI);
            velocityContext.put("contextPath", contextPath);        
            String table = WebTemplates.getForm(velocityContext, WebTemplates.RDF_QUAD_SCRIPT);
            fullPage = this.createTablePage(WsUriConstants.MAP_URI + " as RDF", table, httpServletRequest);
        }        
        return Response.ok(fullPage, MediaType.TEXT_HTML).build();       
    }
        
    //Did not work when tried
    private void addTableCss(StringBuilder sb){
        VelocityContext velocityContext = new VelocityContext();
        sb.append(WebTemplates.getForm(velocityContext, WebTemplates.TABLE_CSS));  
    }
    
    //Did not work when tried
    private void addJQuery(StringBuilder sb){
        VelocityContext velocityContext = new VelocityContext();
        sb.append(WebTemplates.getForm(velocityContext, WebTemplates.JQUERY_SCRIPT));  
    }
    
    private void addDataTable(StringBuilder sb){
        VelocityContext velocityContext = new VelocityContext();
        sb.append(WebTemplates.getForm(velocityContext, WebTemplates.DATATABLE_SCRIPT));  
    }
    
    @GET
    @Path("/" + WsUriConstants.MAP_URI + WsUriConstants.RDF)
    @Produces(MediaType.TEXT_PLAIN)
    public Response mapUriRdfText(
    		@QueryParam(WsUriConstants.URI) List<String> uris,
     		@QueryParam(WsUriConstants.LENS_URI) String lensUri,
            @QueryParam(WsUriConstants.GRAPH) String graph,
            @QueryParam(WsUriConstants.TARGET_URI_PATTERN) List<String> targetUriPatterns, 
            @QueryParam(WsUriConstants.BASE_URI) String baseUri,
            @QueryParam(WsUriConstants.RDF_FORMAT) String formatName,
            @QueryParam(WsUriConstants.LINKSET_INFO) Boolean linksetInfo,
            @QueryParam(WsUriConstants.OVERRIDE_PREDICATE_URI) String overridePredicateURI,
            @Context HttpServletRequest httpServletRequest) 
            throws BridgeDBException{  
        Set<Statement> statements = this.mapUriRdfInner(uris, lensUri, graph, targetUriPatterns, baseUri, formatName, 
                linksetInfo, overridePredicateURI, httpServletRequest);
        if (noContentOnEmpty & statements.isEmpty()){
            return Response.noContent().build();
        }
        RDFFormat format = RDFFormat.matchMIMEType(formatName, rdfFormats).get();
        String rdfInfo = BridgeDbRdfTools.writeRDF(statements, format); 
        return Response.ok(rdfInfo, MediaType.TEXT_PLAIN_TYPE).build();
    }
    
    private Set<Statement> mapUriRdfInner(List<String> uris, String lensUri, String graph, List<String> targetUriPatterns, 
            String baseUri, String formatName,
            Boolean linksetInfo, String overridePredicateURI, HttpServletRequest httpServletRequest) throws BridgeDBException{ 
        boolean addLinks;
        if (linksetInfo == null){
            addLinks = false;
        } else {
            addLinks = linksetInfo;
        }
        if (overridePredicateURI != null && overridePredicateURI.isEmpty()) {
        	overridePredicateURI = null;
        }
        Set<Mapping> mappings;
        if (uris.size() == 1){
            mappings = uriMapper.mapFull(uris.iterator().next(), lensUri, XREF_DATA_NOT_REQUIRED, graph, targetUriPatterns);
        } else {
            mappings = new HashSet<Mapping>();
            for(String single:uris){
                mappings.addAll(uriMapper.mapFull(single, lensUri, XREF_DATA_NOT_REQUIRED, graph, targetUriPatterns));
            }
        }
        baseUri = checkBaseUri(baseUri, httpServletRequest);
        String context = checkContext(baseUri, httpServletRequest);
        Set<Statement> statements = statementMaker.asRDF(mappings, baseUri, addLinks, overridePredicateURI);
        if (formatName != null || formatName != null){
        	RDFFormat format = RDFFormat.matchMIMEType(formatName, rdfFormats).get();
        	if (linksetInfo != null && linksetInfo){
                if (format.supportsContexts()){
                    statements.addAll(getMappingSetStatements(mappings, baseUri, context));
                }
            }
        } else {
            if (linksetInfo != null && linksetInfo){
                statements.addAll(getMappingSetStatements(mappings, baseUri, context));
            }
        }        
        return statements;
    }
    
    private String mapUriForm(boolean includeGraph, HttpServletRequest httpServletRequest) throws BridgeDBException{
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("targetUriPatterns", UriPattern.getUriPatterns());
        velocityContext.put("lenses", LensTools.getLens(LensTools.ALL_GROUP_NAME));        
        velocityContext.put("contextPath", httpServletRequest.getContextPath());
        velocityContext.put("defaultLens", LensTools.byId(Lens.DEFAULT_LENS_NAME));
        velocityContext.put("formatName", WsUriConstants.FORMAT);
        if (includeGraph){
            velocityContext.put("graphName", WsUriConstants.GRAPH); 
            velocityContext.put("graphs", GraphResolver.knownGraphs());
        }
        velocityContext.put("lenses", LensTools.getLens(LensTools.ALL_GROUP_NAME));
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
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/" + Lens.METHOD_NAME) 
    public Response getLensesHtml(@QueryParam(WsUriConstants.LENS_URI)  String lensUri,
                @QueryParam(WsUriConstants.LENS_GROUP) String lensGroup,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        List<Lens> lenses = getTheLens(lensUri, lensGroup);
        StringBuilder sb = new StringBuilder();
        if (lensUri != null && !lensUri.isEmpty()){
           sb.append("<h2>For ").append(WsUriConstants.LENS_URI).append("=").append(lensUri).append("</h2>");
        }else if (lensGroup != null && !lensGroup.isEmpty()){
           sb.append("<h2>For ").append(WsUriConstants.LENS_GROUP).append("=").append(lensGroup).append("</h2>");
        } else {
           sb.append("<h2>For the public lens </h2>");            
        }
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("lenses", lenses);
        velocityContext.put("contextPath", httpServletRequest.getContextPath()); 
        velocityContext.put("dataSourceMethod", WsUriConstants.DATA_SOURCE + "/"); 
        sb.append(WebTemplates.getForm(velocityContext, WebTemplates.LENS));
        sb.append("<p><a href=\"");
        sb.append(httpServletRequest.getContextPath());
        sb.append("/");
        sb.append(Lens.METHOD_NAME);
        sb.append(WsUriConstants.XML);
        if (lensUri != null && !lensUri.isEmpty()){
            sb.append("?");
            sb.append(WsUriConstants.LENS_URI);
            sb.append("=");
            sb.append(lensUri);
        } else if (lensGroup != null && !lensGroup.isEmpty()){
            sb.append("?");
            sb.append(WsUriConstants.LENS_GROUP);
            sb.append("=");
            sb.append(lensGroup);
        }
        sb.append("\">");
        sb.append("XML Format");
        sb.append("</a></p>\n");        
        addLensGroups(sb, httpServletRequest);

        String fullPage = createHtmlPage("Lens Summary", sb.toString(), httpServletRequest);
        return Response.ok(fullPage, MediaType.TEXT_HTML).build();       
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/" + WsUriConstants.LENS_GROUP) 
    public Response getLensGroup(@Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        StringBuilder sb = new StringBuilder();
        addLensGroups(sb, httpServletRequest);
        String fullPage = createHtmlPage("Lens Groups", sb.toString(), httpServletRequest);
        return Response.ok(fullPage, MediaType.TEXT_HTML).build();       
    }

    private void addLensGroups(StringBuilder sb, HttpServletRequest httpServletRequest){
        Set<String> lensGroups = LensTools.getLensGroups();
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("lensGroups", lensGroups);
        velocityContext.put("lensCall", httpServletRequest.getContextPath() + "/" 
                + Lens.METHOD_NAME + "?" + WsUriConstants.LENS_GROUP + "=");            
        sb.append(WebTemplates.getForm(velocityContext, WebTemplates.LENS_GROUP));
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
    @Deprecated
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
            Set<Statement> statements = statementMaker.asRDF(mappingsBySet, getBaseUri(httpServletRequest), 
                    WsUriConstants.MAPPING_SET + WsUriConstants.RDF);
            RDFFormat format = RDFFormat.matchMIMEType(formatName, rdfFormats).get();
            String rdf =BridgeDbRdfTools.writeRDF(statements, format);
            return Response.ok(rdf, MediaType.TEXT_PLAIN_TYPE).build();
        }
    }
    
    protected final void generateTextarea(StringBuilder sb, String fieldName, String text) {
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
    @Deprecated
    public Response mapBySetRdfHtml(@QueryParam(WsUriConstants.URI) List<String> uris,
     		@QueryParam(WsUriConstants.LENS_URI) String lensUri,
            @QueryParam(WsUriConstants.GRAPH) String graph,
            @QueryParam(WsUriConstants.TARGET_URI_PATTERN) List<String> targetUriPatterns,
            @QueryParam(WsUriConstants.RDF_FORMAT) String formatName,
            @Context HttpServletRequest httpServletRequest
            ) throws BridgeDBException {
        MappingsBySet mappingsBySet = mapBySetInner(uris, lensUri, graph, targetUriPatterns);
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>Warning unlike ");
        sb.append(WsUriConstants.MAP_BY_SET);
        sb.append(" this method does not include any protential mapping to self.</h2>");
        sb.append("<h4>Use MediaType.TEXT_PLAIN to remove HTML stuff</h4>");
        sb.append("<p>Warning MediaType.TEXT_PLAIN version returns status 204 if no mappings found.</p>");
        String fullBaseUri = getBaseUri(httpServletRequest);
        int pureLenght = fullBaseUri.length() - WsUriConstants.MAP_BY_SET.length() - WsUriConstants.RDF.length();
        String pureBaseUri = fullBaseUri.substring(0, pureLenght);
        Set<Statement> statements = statementMaker.asRDF(mappingsBySet, pureBaseUri, 
                WsUriConstants.MAPPING_SET + WsUriConstants.RDF);
        RDFFormat format = RDFFormat.matchMIMEType(formatName, rdfFormats).get();
        String rdf =BridgeDbRdfTools.writeRDF(statements, format);
        generateTextarea(sb, "RDF", rdf);

        String title = "HTML friendly " + WsUriConstants.MAP_BY_SET + WsUriConstants.RDF + " Output";
        String fullPage = this.createHtmlPage(title, sb.toString(), httpServletRequest);
        return Response.ok(fullPage, MediaType.TEXT_HTML).build();       
    }

    @GET
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/" + Lens.METHOD_NAME + WsUriConstants.RDF) 
    public Response lensRdfText(@QueryParam(WsUriConstants.RDF_FORMAT) String formatName,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        Set<Statement> statements = LensTools.getLensAsRdf(getBaseUri(httpServletRequest), LensTools.ALL_GROUP_NAME);
        if (statements.isEmpty()){
            return Response.noContent().build();
        } else {
        	RDFFormat format = RDFFormat.matchMIMEType(formatName, rdfFormats).get();
            String rdf = BridgeDbRdfTools.writeRDF(statements, format);
            return Response.ok(rdf, MediaType.TEXT_PLAIN_TYPE).build();
        }
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + Lens.METHOD_NAME + WsUriConstants.RDF) 
    public Response lensRdfHtml(@QueryParam(WsUriConstants.RDF_FORMAT) String formatName,
            @Context HttpServletRequest httpServletRequest
            ) throws BridgeDBException {
        Set<Statement> statements = LensTools.getLensAsRdf(getBaseUri(httpServletRequest), LensTools.ALL_GROUP_NAME);
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>Warning unlike ");
        sb.append(WsUriConstants.MAP_BY_SET);
        sb.append(" this method does not include any protential mapping to self.</h2>");
        sb.append("<h4>Use MediaType.TEXT_PLAIN to remove HTML stuff</h4>");
        sb.append("<p>Warning MediaType.TEXT_PLAIN version returns status 204 if no mappings found.</p>");
        RDFFormat format = RDFFormat.matchMIMEType(formatName, rdfFormats).get();
        String rdf = BridgeDbRdfTools.writeRDF(statements, format);
        generateTextarea(sb, "RDF", rdf);

        String title = "HTML friendly " + WsUriConstants.MAP_BY_SET + WsUriConstants.RDF + " Output";
        String fullPage = this.createHtmlPage(title, sb.toString(), httpServletRequest);
        return Response.ok(fullPage, MediaType.TEXT_HTML).build();       
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
        if (noContentOnEmpty & result.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            return noContentWrapper(httpServletRequest);
        }
        return result;
    }

    @Override
    protected Response noContentWrapper(HttpServletRequest httpServletRequest) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>Reply is an Empty Set or Empty Object</h1>\n");
        sb.append("<h2>Note: The XML and Json versions of this request simply return status 204 (No Context)</h2>");

        String fullPage = this.createHtmlPage("Empty Reply", sb.toString(), httpServletRequest);
        return Response.ok(fullPage, MediaType.TEXT_HTML).build();       
   }

    private String getBaseUri(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() + ":" + httpServletRequest.getServerPort() + httpServletRequest.getRequestURI();
    }
    
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.URI_SPACES_PER_GRAPH) 
    public List<URISpacesInGraphBean> URISpacesPerGraphAsXML() throws BridgeDBException {
        Map<String, Set<RegexUriPattern>> mappings = 
                GraphResolver.getInstance().getAllowedUriPatterns();
        ArrayList<URISpacesInGraphBean> results = new ArrayList<URISpacesInGraphBean>();
        for (String graph:mappings.keySet()){
           Set<String> patternStrings = new HashSet<String>();
           for (RegexUriPattern pattern:mappings.get(graph)){
               patternStrings.add(pattern.getUriPattern());
           }
           results.add(new URISpacesInGraphBean(graph, patternStrings));
        }
        return results;
    }
    
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.URI_SPACES_PER_GRAPH) 
    public List<URISpacesInGraphBean> URISpacesPerGraphAsXMLGet() throws BridgeDBException {
        return URISpacesPerGraphAsXML();
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/" + WsUriConstants.URI_SPACES_PER_GRAPH) 
    public Response URISpacesPerGraphAsHtml(@Context HttpServletRequest httpServletRequest) 
            throws BridgeDBException {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("mappings", GraphResolver.getInstance().getAllowedUriPatterns());
        String mainBody = WebTemplates.getForm(velocityContext, WebTemplates.GRAPH_INFO_SCRIPT);
        String fullPage = this.createHtmlPage("URI Spaces per Graph", mainBody, httpServletRequest);
        return Response.ok(fullPage, MediaType.TEXT_HTML).build();       
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/" + WsUriConstants.URI_SPACES_PER_GRAPH) 
    public Response URISpacesPerGraphAsHtmlGet(@Context HttpServletRequest httpServletRequest) 
            throws BridgeDBException {
       return URISpacesPerGraphAsHtml(httpServletRequest); 
    }

    @GET
    @Path("/file/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getFile(@PathParam("id") String idString) throws MalformedURLException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream input = classLoader.getResourceAsStream(idString);
        ResponseBuilder response;
        if (input != null){
            response = Response.ok();
            response.header("Content-Disposition", "attachment; filename=" + idString);
            response.entity(input);
        } else {
            String message = "File : " + idString  + " not found";
            response = Response.status(Response.Status.NOT_FOUND).entity(message);
        }
        return response.build();
    }
 
    @GET
    @Path("/image/{id}")
	@Produces("image/png")
	public Response getImage(@PathParam("id") String idString) throws MalformedURLException {
        return getFile(idString);
    }
 
}


