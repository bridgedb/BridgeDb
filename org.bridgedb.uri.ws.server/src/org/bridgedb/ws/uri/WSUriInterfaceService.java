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

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import org.bridgedb.Xref;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.statistics.SourceInfo;
import org.bridgedb.statistics.SourceTargetInfo;
import org.bridgedb.uri.api.Mapping;
import org.bridgedb.uri.api.MappingsBySet;
import org.bridgedb.uri.api.MappingsBySysCodeId;
import org.bridgedb.uri.api.SetMappings;
import org.bridgedb.uri.api.UriMapper;
import org.bridgedb.uri.lens.Lens;
import org.bridgedb.uri.lens.LensTools;
import org.bridgedb.uri.ws.WSUriInterface;
import org.bridgedb.uri.ws.WsUriConstants;
import org.bridgedb.uri.ws.bean.DataSourceUriPatternBean;
import org.bridgedb.uri.ws.bean.LensBean;
import org.bridgedb.uri.ws.bean.LensesBean;
import org.bridgedb.uri.ws.bean.MappingSetInfoBean;
import org.bridgedb.uri.ws.bean.MappingSetInfosBean;
import org.bridgedb.uri.ws.bean.MappingsBean;
import org.bridgedb.uri.ws.bean.MappingsBySetBean;
import org.bridgedb.uri.ws.bean.OverallStatisticsBean;
import org.bridgedb.uri.ws.bean.SourceInfosBean;
import org.bridgedb.uri.ws.bean.SourceTargetInfosBean;
import org.bridgedb.uri.ws.bean.UriExistsBean;
import org.bridgedb.uri.ws.bean.UriMappings;
import org.bridgedb.uri.ws.bean.UriSearchBean;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.ws.WSCoreService;
import org.bridgedb.ws.WsConstants;
import org.bridgedb.ws.bean.XrefBean;
import org.bridgedb.ws.templates.WebTemplates;

@Path("/")
public class WSUriInterfaceService extends WSCoreService implements WSUriInterface {

    protected UriMapper uriMapper;
//    protected LinksetInterfaceMinimal linksetInterface;
//    private String validationTypeString;
    public final String MIME_TYPE = "mimeType";
    public final String STORE_TYPE = "storeType";
    public final String VALIDATION_TYPE = "validationType";
    public final String INFO = "info"; 
    public final String FILE = "file";     
    public final String NO_RESULT = null;
    
    protected final NumberFormat formatter;
    static final Logger logger = Logger.getLogger(WSUriInterfaceService.class);

    public WSUriInterfaceService(UriMapper uriMapper) throws BridgeDBException {
        super(uriMapper);
        this.uriMapper = uriMapper;
       formatter = configFormatter();
        logger.info("WS Service running using supplied uriMapper");
    }

    private NumberFormat configFormatter() throws BridgeDBException   {
        NumberFormat numberFormat = NumberFormat.getInstance();
        if (numberFormat instanceof DecimalFormat) {
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setGroupingSeparator(',');
            ((DecimalFormat) numberFormat).setDecimalFormatSymbols(dfs);
        }
        return numberFormat;
    }
        

    private MappingsBean mapFull(String id, String scrCode, String uri, String lensUri, 
            Boolean includeXrefResults, Boolean includeUriResults,
            List<String> targetCodes, String graph, List<String> targetUriPatterns) throws BridgeDBException {
        if (logger.isDebugEnabled()){
            logger.debug("map called! ");
            if (id != null){
                logger.debug("id = " + id);
            }
            if (scrCode != null){
                logger.debug("   scrCode = " + scrCode);             
            }
            if (uri != null){
                logger.debug("   uri = " + uri);             
            }
            logger.debug("   lensUri = " + lensUri);
            if (targetCodes!= null && !targetCodes.isEmpty()){
                logger.debug("   targetCodes = " + targetCodes);
            }
            if (targetUriPatterns!= null && !targetUriPatterns.isEmpty()){
                logger.debug("   targetUriPatterns = " + targetUriPatterns);
            }
        }
        Collection<DataSource> targetDataSources = getDataSources(targetCodes);
         if (id == null){
            if (scrCode != null) {
                throw new BridgeDBException (WsConstants.DATASOURCE_SYSTEM_CODE + " parameter " + scrCode 
                        + " should only be used together with " + WsConstants.ID + " parameter "); 
            }
            if (uri == null){
                throw new BridgeDBException ("Please provide either a " + WsConstants.ID + " or a "
                        + WsUriConstants.URI + " parameter.");                 
            }  
            return mapFull(uri, lensUri, includeXrefResults, targetDataSources, graph, targetUriPatterns);
        } else {
            if (uri != null){
                throw new BridgeDBException ("Please provide either a " + WsConstants.ID + " or a "
                        + WsConstants.DATASOURCE_SYSTEM_CODE + " parameter, but not both.");                 
            }
            if (scrCode == null) {
                throw new BridgeDBException (WsConstants.ID + " parameter must come with a " 
                        + WsConstants.DATASOURCE_SYSTEM_CODE + " parameter."); 
            }
        }
        return mapFull(id, scrCode, lensUri, includeUriResults, targetDataSources, graph, targetUriPatterns);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsUriConstants.MAP)
    @Override
    public Response map(
            @QueryParam(WsConstants.ID) String id,
            @QueryParam(WsConstants.DATASOURCE_SYSTEM_CODE) String scrCode,
            @QueryParam(WsUriConstants.URI) String uri,
            @QueryParam(WsUriConstants.LENS_URI) String lensUri,
            @QueryParam(WsUriConstants.INCLUDE_XREF_RESULTS) Boolean includeXrefResults, 
            @QueryParam(WsUriConstants.INCLUDE_URI_RESULTS) Boolean includeUriResults,
            @QueryParam(WsConstants.TARGET_DATASOURCE_SYSTEM_CODE) List<String> targetCodes,
            @QueryParam(WsUriConstants.GRAPH) String graph,
            @QueryParam(WsUriConstants.TARGET_URI_PATTERN) List<String> targetUriPatterns) throws BridgeDBException {
        MappingsBean result = mapFull(id, scrCode, uri, lensUri, 
                includeXrefResults, includeUriResults, 
                targetCodes, graph, targetUriPatterns);
        if (noConentOnEmpty & result.asMappings().isEmpty()){
            return Response.status(Response.Status.NO_CONTENT).build();
        } 
        return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.MAP)
    public Response mapJson(
            @QueryParam(WsConstants.ID) String id,
            @QueryParam(WsConstants.DATASOURCE_SYSTEM_CODE) String scrCode,
    		@QueryParam(WsUriConstants.URI) String uri,
     		@QueryParam(WsUriConstants.LENS_URI) String lensUri,
            @QueryParam(WsUriConstants.INCLUDE_XREF_RESULTS) Boolean includeXrefResults, 
            @QueryParam(WsUriConstants.INCLUDE_URI_RESULTS) Boolean includeUriResults,
            @QueryParam(WsConstants.TARGET_DATASOURCE_SYSTEM_CODE) List<String> targetCodes,
            @QueryParam(WsUriConstants.GRAPH) String graph,
            @QueryParam(WsUriConstants.TARGET_URI_PATTERN) List<String> targetUriPatterns) throws BridgeDBException {
        MappingsBean result = mapFull(id, scrCode, uri, lensUri, 
                 includeXrefResults, includeUriResults, 
                targetCodes, graph, targetUriPatterns);
        if (noConentOnEmpty & result.asMappings().isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(result, MediaType.APPLICATION_JSON_TYPE).build();
    }
 
    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + WsUriConstants.MAP)
    public Response mapHtml(
            @QueryParam(WsConstants.ID) String id,
            @QueryParam(WsConstants.DATASOURCE_SYSTEM_CODE) String scrCode,
            @QueryParam(WsUriConstants.URI) String uri,
            @QueryParam(WsUriConstants.LENS_URI) String lensUri,
            @QueryParam(WsUriConstants.INCLUDE_XREF_RESULTS) Boolean includeXrefResults, 
            @QueryParam(WsUriConstants.INCLUDE_URI_RESULTS) Boolean includeUriResults,
            @QueryParam(WsConstants.TARGET_DATASOURCE_SYSTEM_CODE) List<String> targetCodes,
            @QueryParam(WsUriConstants.GRAPH) String graph,
            @QueryParam(WsUriConstants.TARGET_URI_PATTERN) List<String> targetUriPatterns,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        MappingsBean result = mapFull (id, scrCode, uri, lensUri, 
                includeXrefResults, includeUriResults, 
                targetCodes, graph, targetUriPatterns);
        if (noConentOnEmpty & result.asMappings().isEmpty()){
            return noContentWrapper(httpServletRequest);
        } 
        return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
    }
    
    private UriMappings mapUriInner(List<String> uris, String lensUri, String graph, List<String> targetUriPatterns) 
            throws BridgeDBException {
       if (logger.isDebugEnabled()){
            logger.debug("map called! ");
            logger.debug("   uri = " + uris);             
            logger.debug("   lensUri = " + lensUri);
            if (targetUriPatterns!= null && !targetUriPatterns.isEmpty()){
                logger.debug("   targetUriPatterns = " + targetUriPatterns);
            }
       }
       Set<String> results = new HashSet<String>();
       for(String single:uris){
           results.addAll(uriMapper.mapUri(single, lensUri, graph, targetUriPatterns));
       }
       return UriMappings.asBean(results);
    }

    private MappingsBySysCodeId mapUriBySysCodeId(List<String> uris, String lensUri, String graph, List<String> targetUriPatterns) 
            throws BridgeDBException {
       if (logger.isDebugEnabled()){
            logger.debug("mapUriBySysCodeId called! ");
            logger.debug("   uri = " + uris);             
            logger.debug("   lensUri = " + lensUri);
            logger.debug("   graph = " + graph);
            if (targetUriPatterns!= null && !targetUriPatterns.isEmpty()){
                logger.debug("   targetUriPatterns = " + targetUriPatterns);
            }
       }
       return uriMapper.mapUriBySysCodeId(lensUri, lensUri, graph, targetUriPatterns);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsUriConstants.MAP_URI)
    @Override
    public Response mapUri(
    		@QueryParam(WsUriConstants.URI) List<String> uris,
     		@QueryParam(WsUriConstants.LENS_URI) String lensUri,
            @QueryParam(WsUriConstants.GRAPH) String graph,
            @QueryParam(WsUriConstants.TARGET_URI_PATTERN) List<String> targetUriPatterns) throws BridgeDBException {
        UriMappings result = mapUriInner(uris, lensUri, graph, targetUriPatterns);
        if (noConentOnEmpty & result.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.MAP_URI)
    public Response mapUriJson(
    		@QueryParam(WsUriConstants.URI) List<String> uris,
     		@QueryParam(WsUriConstants.LENS_URI) String lensUri,
            @QueryParam(WsUriConstants.GRAPH) String graph,
            @QueryParam(WsUriConstants.TARGET_URI_PATTERN) List<String> targetUriPatterns) throws BridgeDBException {
        UriMappings result = mapUriInner(uris, lensUri, graph, targetUriPatterns);
        if (noConentOnEmpty & result.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(result, MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + WsUriConstants.MAP_URI)
    public Response mapUriHtml(
    		@QueryParam(WsUriConstants.URI) List<String> uris,
     		@QueryParam(WsUriConstants.LENS_URI) String lensUri,
            @QueryParam(WsUriConstants.GRAPH) String graph,
            @QueryParam(WsUriConstants.TARGET_URI_PATTERN) List<String> targetUriPatterns,
            @QueryParam(WsUriConstants.FORMAT) String format,
            @Context HttpServletRequest httpServletRequest) {
        Set<Mapping> mappings = new HashSet<Mapping>();
        try {
            if (uris == null || uris.isEmpty()){
                throw new BridgeDBException(WsUriConstants.URI + " parameter missing!");
            }
            for (String uri:uris){
                mappings.addAll(uriMapper.mapFull(uri, lensUri, true, graph, targetUriPatterns));
            }
        } catch (BridgeDBException exception) {
            return  mapUriHtmlResult(uris, lensUri, graph, targetUriPatterns, format, httpServletRequest, null, exception);
        }
        uris.remove("");
        targetUriPatterns.remove("");
        if (format == null || format.isEmpty()){
            format = MediaType.TEXT_HTML;
        }
        if (format.equals("application/xml")){
            if (noConentOnEmpty & mappings.isEmpty()){
                return noContentWrapper(httpServletRequest);
            } 
            
            return Response.ok(UriMappings.toBean(mappings), MediaType.APPLICATION_XML_TYPE).build();
        }
        else if (format.equals("application/json")){
            if (noConentOnEmpty & mappings.isEmpty()){
                return noContentWrapper(httpServletRequest);
            } 
            return Response.ok(UriMappings.toBean(mappings), MediaType.APPLICATION_JSON_TYPE).build();
        }
        return  mapUriHtmlResult(uris, lensUri, graph, targetUriPatterns, format, httpServletRequest, mappings, null);
    }

    private Response mapUriHtmlResult(List<String> uris, String lensUri, String graph, List<String> targetUriPatterns,
            String format, HttpServletRequest httpServletRequest, Collection<Mapping> mappingSet, BridgeDBException exception) {
        uris.remove("");
        targetUriPatterns.remove("");
        StringBuilder sb = topAndSide ("Identity Mapping Service", httpServletRequest);        
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("contextPath", httpServletRequest.getContextPath());
        velocityContext.put("sourceUris", uris);
        velocityContext.put("URI", WsUriConstants.URI);
        velocityContext.put("lensURI", lensUri);
        velocityContext.put("lensURIName", WsUriConstants.LENS_URI);
        velocityContext.put("defaultLensName", Lens.DEFAULT_LENS_NAME); 
        velocityContext.put("targetUriPatterns", targetUriPatterns);
        velocityContext.put("targetUriPatternName", WsUriConstants.TARGET_URI_PATTERN);
        velocityContext.put("graph", graph);
        velocityContext.put("graphName", WsUriConstants.GRAPH);
        if (mappingSet != null){            
            ArrayList<Mapping> mappingList = new ArrayList<Mapping>(mappingSet);
            Collections.sort(mappingList);
            velocityContext.put("mappings",mappingList);
         } else {
            velocityContext.put("mappings",new ArrayList<Mapping>());            
            velocityContext.put("exception", exception.getMessage());
        }
        sb.append( WebTemplates.getForm(velocityContext, WebTemplates.MAP_URI_RESULTS)); 
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

    @POST
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + WsUriConstants.MAP_URI)
    public Response mapUriHtmlPost(
    		@FormParam(WsUriConstants.URI) List<String> uris,
     		@FormParam(WsUriConstants.LENS_URI) String lensUri,
            @FormParam(WsUriConstants.GRAPH) String graph,
            @FormParam(WsUriConstants.FORMAT) String format,
            @FormParam(WsUriConstants.TARGET_URI_PATTERN) List<String> targetUriPatterns,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        return mapUriHtml(uris, lensUri, graph, targetUriPatterns, format, httpServletRequest);
    }

    protected final MappingsBySet mapBySetInner(List<String> uris, String lensUri, String graph, List<String> targetUriPatterns) throws BridgeDBException {
        HashSet<String> uriSet = new HashSet<String>(uris);
        return uriMapper.mapBySet(uriSet, lensUri, graph, targetUriPatterns);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsUriConstants.MAP_BY_SET)
    public Response mapBySet(@QueryParam(WsUriConstants.URI) List<String> uris,
     		@QueryParam(WsUriConstants.LENS_URI) String lensUri,
            @QueryParam(WsUriConstants.GRAPH) String graph,
            @QueryParam(WsUriConstants.TARGET_URI_PATTERN) List<String> targetUriPatterns) throws BridgeDBException {
        MappingsBySet mappingsBySet = mapBySetInner(uris, lensUri, graph, targetUriPatterns);
        if (noConentOnEmpty & mappingsBySet.isEmpty()){
            return Response.noContent().build();
        } 
        MappingsBySetBean result = new MappingsBySetBean(mappingsBySet);
        return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.MAP_BY_SET)
    public Response mapBySetJson(@QueryParam(WsUriConstants.URI) List<String> uris,
     		@QueryParam(WsUriConstants.LENS_URI) String lensUri,
            @QueryParam(WsUriConstants.GRAPH) String graph,
            @QueryParam(WsUriConstants.TARGET_URI_PATTERN) List<String> targetUriPatterns) throws BridgeDBException {
        MappingsBySet mappingsBySet = mapBySetInner(uris, lensUri, graph, targetUriPatterns);
        if (noConentOnEmpty & mappingsBySet.isEmpty()){
            return Response.noContent().build();
        } 
        MappingsBySetBean result = new MappingsBySetBean(mappingsBySet);
        return Response.ok(result, MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + WsUriConstants.MAP_BY_SET)
    public Response mapBySetHtml(@QueryParam(WsUriConstants.URI) List<String> uris,
     		@QueryParam(WsUriConstants.LENS_URI) String lensUri,
            @QueryParam(WsUriConstants.GRAPH) String graph,
            @QueryParam(WsUriConstants.TARGET_URI_PATTERN) List<String> targetUriPatterns,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        MappingsBySet mappingsBySet = mapBySetInner(uris, lensUri, graph, targetUriPatterns);
        if (noConentOnEmpty & mappingsBySet.isEmpty()){
            return noContentWrapper(httpServletRequest);
        } 
        MappingsBySetBean result = new MappingsBySetBean(mappingsBySet);
        return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
    }
 
    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsUriConstants.URI_EXISTS)
    @Override
    public Response UriExists(@QueryParam(WsUriConstants.URI) String URI) throws BridgeDBException {
        if (URI == null) throw new BridgeDBException(WsUriConstants.URI + " parameter missing.");
        if (URI.isEmpty()) throw new BridgeDBException(WsUriConstants.URI + " parameter may not be null.");
        boolean exists = uriMapper.uriExists(URI);
        UriExistsBean bean = new UriExistsBean(URI, exists);
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.URI_EXISTS)
    public Response UriExistsJson(@QueryParam(WsUriConstants.URI) String URI) throws BridgeDBException {
        if (URI == null) throw new BridgeDBException(WsUriConstants.URI + " parameter missing.");
        if (URI.isEmpty()) throw new BridgeDBException(WsUriConstants.URI + " parameter may not be null.");
        boolean exists = uriMapper.uriExists(URI);
        UriExistsBean bean = new UriExistsBean(URI, exists);
        return Response.ok(bean, MediaType.APPLICATION_JSON_TYPE).build();
    }
    //No html as never no context

    private UriSearchBean UriSearchInner(String text,String limitString) throws BridgeDBException {
        if (text == null) throw new BridgeDBException(WsUriConstants.TEXT + " parameter missing.");
        if (text.isEmpty()) throw new BridgeDBException(WsUriConstants.TEXT + " parameter may not be null.");
        UriSearchBean bean;
        if (limitString == null || limitString.isEmpty()){
            Set<String> uris = uriMapper.uriSearch(text, Integer.MAX_VALUE);
            return new UriSearchBean(text, uris);
        } else {
            int limit = Integer.parseInt(limitString);
            Set<String> uris = uriMapper.uriSearch(text, limit);
            return new UriSearchBean(text, uris);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsUriConstants.URI_SEARCH)
    @Override
    public Response UriSearch(@QueryParam(WsUriConstants.TEXT) String text,
            @QueryParam(WsUriConstants.LIMIT) String limitString) throws BridgeDBException {
        UriSearchBean bean = UriSearchInner(text, limitString);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.URI_SEARCH)
    public Response UriSearchJson(@QueryParam(WsUriConstants.TEXT) String text,
            @QueryParam(WsUriConstants.LIMIT) String limitString) throws BridgeDBException {
        UriSearchBean bean = UriSearchInner(text, limitString);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(bean, MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + WsUriConstants.URI_SEARCH)
    public Response UriSearchHtml(@QueryParam(WsUriConstants.TEXT) String text,
            @QueryParam(WsUriConstants.LIMIT) String limitString,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        UriSearchBean bean = UriSearchInner(text, limitString);
        if (noConentOnEmpty & bean.isEmpty()){
            return noContentWrapper(httpServletRequest);
        } 
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    }

    public XrefBean toXrefInner(String URI) throws BridgeDBException {
        if (URI == null) throw new BridgeDBException(WsUriConstants.URI + " parameter missing.");
        if (URI.isEmpty()) throw new BridgeDBException(WsUriConstants.URI + " parameter may not be null.");
        Xref xref = uriMapper.toXref(URI);
        return XrefBean.asBean(xref);
    }
   
    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsUriConstants.TO_XREF)
    @Override
    public Response toXref(@QueryParam(WsUriConstants.URI) String URI) throws BridgeDBException {     
        XrefBean bean = toXrefInner(URI);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsUriConstants.TO_URIS)
    @Override
    public Response toUris(@QueryParam(WsConstants.ID) String id,
            @QueryParam(WsConstants.DATASOURCE_SYSTEM_CODE) String scrCode) throws BridgeDBException{
        UriMappings result = toUrisInner(id, scrCode);
        if (noConentOnEmpty & result.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.TO_URIS)
    public Response toUrisJSon(@QueryParam(WsConstants.ID) String id,
            @QueryParam(WsConstants.DATASOURCE_SYSTEM_CODE) String scrCode) throws BridgeDBException{
        UriMappings result = toUrisInner(id, scrCode);
        if (noConentOnEmpty & result.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(result, MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + WsUriConstants.TO_URIS)
    public Response toUrisHtml(@QueryParam(WsConstants.ID) String id,
            @QueryParam(WsConstants.DATASOURCE_SYSTEM_CODE) String scrCode,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException{
        UriMappings result = toUrisInner(id, scrCode);
        Set<String> uris = result.getTargetUri();
        StringBuilder sb = topAndSide ("Identity Mapping Service", httpServletRequest);        
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("contextPath", httpServletRequest.getContextPath());
        velocityContext.put("id", id);
        velocityContext.put("sysCode", scrCode);
        velocityContext.put("uris", uris);
        sb.append( WebTemplates.getForm(velocityContext, WebTemplates.TO_URIS_SCRIPT)); 
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

    public UriMappings toUrisInner(String id, String scrCode) throws BridgeDBException{
        if (logger.isDebugEnabled()){
            logger.debug("toUris called! ");
            logger.debug("id = " + id);
            logger.debug("srCode = " + scrCode);             
        }
        if (id == null){
            throw new BridgeDBException ("Please provide  a " + WsConstants.ID + " parameter.");     
        }
        if (scrCode == null) {
            throw new BridgeDBException ("Please provide  a " + WsConstants.DATASOURCE_SYSTEM_CODE  + " parameter."); 
        }
        DataSource dataSource = DataSource.getExistingBySystemCode(scrCode);
        Xref sourceXref = new Xref(id, dataSource);
        Set<String> results = uriMapper.toUris(sourceXref);
        return UriMappings.asBean(results);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.TO_XREF)
    public Response toXrefJson(@QueryParam(WsUriConstants.URI) String URI) throws BridgeDBException {     
        XrefBean bean = toXrefInner(URI);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(bean, MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + WsUriConstants.TO_XREF)
    public Response toXrefHtml(@QueryParam(WsUriConstants.URI) String URI,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {     
        XrefBean bean = toXrefInner(URI);
        if (noConentOnEmpty & bean.isEmpty()){
            return noContentWrapper(httpServletRequest);
        } 
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsUriConstants.GET_OVERALL_STATISTICS) 
    @Override
    public Response getOverallStatistics(@QueryParam(WsUriConstants.LENS_URI) String lensUri) 
            throws BridgeDBException {
        OverallStatistics overallStatistics = uriMapper.getOverallStatistics(lensUri);
        OverallStatisticsBean bean = OverallStatisticsBean.asBean(overallStatistics);
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.GET_OVERALL_STATISTICS) 
    public Response getOverallStatisticsJson(@QueryParam(WsUriConstants.LENS_URI) String lensUri) 
            throws BridgeDBException {
        OverallStatistics overallStatistics = uriMapper.getOverallStatistics(lensUri);
        OverallStatisticsBean bean = OverallStatisticsBean.asBean(overallStatistics);
        return Response.ok(bean, MediaType.APPLICATION_JSON_TYPE).build();
    }
    
    @Override
    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsUriConstants.SOURCE_INFOS) 
    public Response getSourceInfos(@QueryParam(WsUriConstants.LENS_URI) String lensUri) throws BridgeDBException {
        SourceInfosBean bean = getSourceInfosInner(lensUri);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.SOURCE_INFOS) 
    public Response getSourceInfosJson(@QueryParam(WsUriConstants.LENS_URI) String lensUri) throws BridgeDBException {
        SourceInfosBean bean = getSourceInfosInner(lensUri);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(bean, MediaType.APPLICATION_JSON_TYPE).build();
    }

    private SourceInfosBean getSourceInfosInner(String lensUri) throws BridgeDBException {
        List<SourceInfo> infos = uriMapper.getSourceInfos(lensUri);
        SourceInfosBean bean = new SourceInfosBean();
        for (SourceInfo info:infos){
            bean.addSourceInfo(info);
        }
        return bean;
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsUriConstants.SOURCE_TARGET_INFOS) 
    public Response getSourceTargetInfos(@QueryParam(WsUriConstants.SOURCE_DATASOURCE_SYSTEM_CODE) String scrCode,
            @QueryParam(WsUriConstants.LENS_URI) String lensUri) throws BridgeDBException {
        SourceTargetInfosBean bean = getSourceTargetInfosInner(scrCode, lensUri);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.SOURCE_TARGET_INFOS) 
    public Response getSourceTargetInfosJson(@QueryParam(WsUriConstants.SOURCE_DATASOURCE_SYSTEM_CODE) String scrCode,
            @QueryParam(WsUriConstants.LENS_URI) String lensUri) throws BridgeDBException {
        SourceTargetInfosBean bean = getSourceTargetInfosInner(scrCode, lensUri);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(bean, MediaType.APPLICATION_JSON_TYPE).build();
    }

    private SourceTargetInfosBean getSourceTargetInfosInner(String scrCode, String lensUri) throws BridgeDBException {
        List<SourceTargetInfo> infos = uriMapper.getSourceTargetInfos(scrCode, lensUri);
        SourceTargetInfosBean bean = new SourceTargetInfosBean();
        for (SourceTargetInfo info:infos){
            bean.addSourceTargetInfo(info);
        }
        return bean;
    }

    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsUriConstants.MAPPING_SET + WsUriConstants.XML) 
    public Response getMappingSetInfosXML(@QueryParam(WsUriConstants.SOURCE_DATASOURCE_SYSTEM_CODE) String scrCode,
            @QueryParam(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE) String targetCode,
     		@QueryParam(WsUriConstants.LENS_URI) String lensUri) throws BridgeDBException {
        return getMappingSetInfos(scrCode, targetCode, lensUri);
    }
    
    private MappingSetInfosBean getMappingSetInfosInner(String scrCode, String targetCode, String lensUri) throws BridgeDBException {
        List<MappingSetInfo> infos = uriMapper.getMappingSetInfos(scrCode, targetCode, lensUri);
        MappingSetInfosBean bean = new MappingSetInfosBean();
        for (MappingSetInfo info:infos){
            bean.addMappingSetInfo(info);
        }
        return bean;
    }
    
    @Override
    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsUriConstants.MAPPING_SET) 
    public Response getMappingSetInfos(@QueryParam(WsUriConstants.SOURCE_DATASOURCE_SYSTEM_CODE) String scrCode,
            @QueryParam(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE) String targetCode,
     		@QueryParam(WsUriConstants.LENS_URI) String lensUri) throws BridgeDBException {
        MappingSetInfosBean bean = getMappingSetInfosInner(scrCode, targetCode, lensUri);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.MAPPING_SET) 
    public Response getMappingSetInfosJson(@QueryParam(WsUriConstants.SOURCE_DATASOURCE_SYSTEM_CODE) String scrCode,
            @QueryParam(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE) String targetCode,
     		@QueryParam(WsUriConstants.LENS_URI) String lensUri) throws BridgeDBException {
        MappingSetInfosBean bean = getMappingSetInfosInner(scrCode, targetCode, lensUri);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(bean, MediaType.APPLICATION_JSON_TYPE).build();
    }

	private LensBean getLensInner(String id) throws BridgeDBException {
 		Lens lens = LensTools.byId(id);
		return new LensBean(lens, null);
  	}

    @GET
	@Produces({MediaType.APPLICATION_XML})
	@Path(Lens.URI_PREFIX + "{id}")
    @Override
	public Response getLens(@PathParam("id") String id) throws BridgeDBException {
		LensBean bean = getLensInner(id);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
	}
    
    @GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path(Lens.URI_PREFIX + "{id}")
	public Response getLensJson(@PathParam("id") String id) throws BridgeDBException {
		LensBean bean = getLensInner(id);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(bean, MediaType.APPLICATION_JSON_TYPE).build();
	}
    
    @GET
	@Produces({MediaType.TEXT_HTML})
	@Path(Lens.URI_PREFIX + "{id}")
	public Response getLensHtml(@PathParam("id") String id,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {
		LensBean bean = getLensInner(id);
        if (noConentOnEmpty & bean.isEmpty()){
            return noContentWrapper(httpServletRequest);
        } 
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
	}
    
    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + Lens.METHOD_NAME + WsUriConstants.XML) 
    public Response getLensesXML(@QueryParam(WsUriConstants.LENS_URI) String lensUri,
                @QueryParam(WsUriConstants.LENS_GROUP) String lensGroup) throws BridgeDBException {
        return getLenses(lensUri, lensGroup);
    }

    protected List<Lens> getTheLens(String lensUri, String lensGroup) throws BridgeDBException{
        if (lensUri == null || lensUri.isEmpty()){
            return  LensTools.getLens(lensGroup);
        } else {
            Lens lens = LensTools.byId(lensUri);
            List<Lens> lenses = new ArrayList<Lens>();
            lenses.add(lens);  
            return lenses;
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + Lens.METHOD_NAME) 
    @Override
    public Response getLenses(@QueryParam(WsUriConstants.LENS_URI) String lensUri,
                @QueryParam(WsUriConstants.LENS_GROUP) String lensGroup) throws BridgeDBException {
        List<Lens> lenses = getTheLens(lensUri, lensGroup);
		LensesBean bean = new LensesBean(lenses, null);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
	}
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + Lens.METHOD_NAME) 
 	public Response getLensesJson(@QueryParam(WsUriConstants.LENS_URI) String lensUri,
                @QueryParam(WsUriConstants.LENS_GROUP) String lensGroup) throws BridgeDBException {
        List<Lens> lenses = getTheLens(lensUri, lensGroup);
		LensesBean bean = new LensesBean(lenses, null);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(bean, MediaType.APPLICATION_JSON_TYPE).build();
	}
    
    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + Lens.METHOD_NAME) 
 	public Response getLensesHtml(@QueryParam(WsUriConstants.LENS_URI) String lensUri,
                @QueryParam(WsUriConstants.LENS_GROUP) String lensGroup,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        List<Lens> lenses = getTheLens(lensUri, lensGroup);
		LensesBean bean = new LensesBean(lenses, null);
        if (noConentOnEmpty & bean.isEmpty()){
            return noContentWrapper(httpServletRequest);
        } 
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
	}
    
    private MappingSetInfoBean getMappingSetInfoInner(String idString) throws BridgeDBException {
        if (idString == null) {
            throw new BridgeDBException("Path parameter missing.");
        }
        if (idString.isEmpty()) {
            throw new BridgeDBException("Path parameter may not be null.");
        }
        int id = Integer.parseInt(idString);
        MappingSetInfo info = uriMapper.getMappingSetInfo(id);
        return new MappingSetInfoBean(info);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsUriConstants.MAPPING_SET + "/{id}")
    @Override
    public Response getMappingSetInfo(@PathParam("id") String idString) throws BridgeDBException {  
        MappingSetInfoBean bean = getMappingSetInfoInner(idString);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.MAPPING_SET + "/{id}")
    public Response getMappingSetInfoJson(@PathParam("id") String idString) throws BridgeDBException {  
        MappingSetInfoBean bean = getMappingSetInfoInner(idString);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(bean, MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
    @Path("/" + WsUriConstants.DATA_SOURCE)
    public Response getDataSource() throws BridgeDBException {
        throw new BridgeDBException("id path parameter missing.");
    }

    private DataSourceUriPatternBean getDataSourceInner(String id) throws BridgeDBException {
        if (id == null) {
            throw new BridgeDBException("Path parameter missing.");
        }
        if (id.isEmpty()) {
            throw new BridgeDBException("Path parameter may not be null.");
        }
        DataSource ds = DataSource.getBySystemCode(id);
        return new DataSourceUriPatternBean(ds, uriMapper.getUriPatterns(id));
    }
    
    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Override
    @Path("/" + WsUriConstants.DATA_SOURCE + "/{id}")
    public Response getDataSource(@PathParam("id") String id) throws BridgeDBException {
        DataSourceUriPatternBean bean = getDataSourceInner(id);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.DATA_SOURCE + "/{id}")
    public Response getDataSourceJson(@PathParam("id") String id) throws BridgeDBException {
        DataSourceUriPatternBean bean = getDataSourceInner(id);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        } 
        return Response.ok(bean, MediaType.APPLICATION_JSON_TYPE).build();
    }
    
    @GET
    @Produces({MediaType.TEXT_PLAIN})
    @Override
    @Path("/" + WsUriConstants.SQL_COMPAT_VERSION)
    public Response getSqlCompatVersion() throws BridgeDBException {
        return Response.ok(uriMapper.getSqlCompatVersion(), MediaType.TEXT_PLAIN).build();
    }

    //****** Support functions *****
   
    private MappingsBean mapFull(String uri, String lensUri, Boolean includeXrefResults, 
            Collection<DataSource> targetDataSources, String graph, Collection<String> targetPatterns) 
            throws BridgeDBException {
        if (includeXrefResults == null){
            includeXrefResults = false;
        }
        Set<Mapping> mappings;
        if (targetDataSources.isEmpty()){
            //if (targetPatterns == null){
            //    mappings = uriMapper.mapFull(uri, lensUri, includeXrefResults, graph);
            //} else {
                mappings = mapFull(uri, lensUri, includeXrefResults, graph, targetPatterns);
            //}
        } else {
            mappings = mapFull (uri, lensUri, targetDataSources);
            if (targetPatterns != null && !targetPatterns.isEmpty()){
                //Include Xref results as targetDataSources set
                mappings.addAll(mapFull(uri, lensUri, true, graph, targetPatterns));                
            } 
        }
        return new MappingsBean(mappings); 
    }
    
    /**
     * 
     * @param id
     * @param scrCode
     * @param lensUri
     * @param includeUriResults
     * @param targetDataSources
     * @param graph
     * @param targetPatterns
     * @return
     * @throws BridgeDBException
     * @throws IllegalStateException if targetCode is not known
     */
    private MappingsBean mapFull(String id, String scrCode, String lensUri, 
            Boolean includeUriResults, 
            Collection<DataSource> targetDataSources, String graph, Collection<String> targetPatterns) 
            throws BridgeDBException {
        DataSource dataSource = DataSource.getExistingBySystemCode(scrCode);
        Xref sourceXref = new Xref(id, dataSource);
        if (includeUriResults == null){
            includeUriResults = false;
        }
        Set<Mapping> mappings;
        if (targetPatterns == null ||targetPatterns.isEmpty()){
            mappings = mapFull(sourceXref, lensUri, includeUriResults, targetDataSources);
        } else {
            mappings = mapFull(sourceXref, lensUri, graph, targetPatterns); 
            if (!targetDataSources.isEmpty()){
                //Include UriResults as targetPatterns set
                mappings.addAll(mapFull(sourceXref, lensUri, true, targetDataSources));
           }
        }
        return new MappingsBean(mappings); 
    }

    /**
     * 
     * @param targetCodes
     * @return 
     * @throws IllegalStateException if targetCode is not known
     */
    private Set<DataSource> getDataSources(List<String> targetCodes){
        HashSet<DataSource> targets = new HashSet<DataSource>();
        if (targetCodes != null){
            for (String targetCode:targetCodes){
                if (targetCode != null && !targetCode.isEmpty()){
                    targets.add(DataSource.getExistingBySystemCode(targetCode));
                } else {
                    targets.add(null);
                }
            }
        }
        return targets;        
    }
            
    /**
     * Returns both Xref and Uri results.
     * 
     * @param sourceUri
     * @param lensUri
     * @param targetDataSources
     * @return
     * @throws BridgeDBException 
     */
    private Set<Mapping> mapFull(String sourceUri, String lensUri, Collection<DataSource> targetDataSources) throws BridgeDBException{
        return uriMapper.mapFull(sourceUri, lensUri, targetDataSources);
    }
    
    /**
     * Returns Xref results and Uri results only if requested.
     * 
     * @param sourceXref
     * @param lensUri
     * @param includeUriResults
     * @param targetDataSources
     * @return
     * @throws BridgeDBException 
     */
    private Set<Mapping> mapFull(Xref sourceXref, String lensUri,
            boolean includeUriResults, 
            Collection<DataSource> targetDataSources) throws BridgeDBException{
        return uriMapper.mapFull(sourceXref, lensUri, includeUriResults, targetDataSources);
    }
    
    /**
     * Returns Uri results and Xref only if requested.
     * 
     * @param sourceUri
     * @param lensUri
     * @param includeXrefResults
     * @param graph
     * @param targetUriPattern
     * @return
     * @throws BridgeDBException 
     */
    private Set<Mapping> mapFull(String sourceUri, String lensUri, 
            boolean includeXrefResults, String graph, Collection<String> targetUriPattern) 
            throws BridgeDBException{
        return uriMapper.mapFull(sourceUri, lensUri, includeXrefResults, graph, targetUriPattern);
    }
    
    /**
     * Returns both Xref and Uri results.
     * 
     * @param sourceXref
     * @param lensUri
     * @param graph
     * @param targetUriPattern
     * @return
     * @throws BridgeDBException 
     */
    private Set<Mapping> mapFull(Xref sourceXref, String lensUri, String graph, Collection<String> targetUriPattern) throws BridgeDBException{
        return uriMapper.mapFull(sourceXref, lensUri, graph, targetUriPattern);
    }

    private String trim(String original){
        String result = original.trim();
        while (result.startsWith("\"")){
            result = result.substring(1);
        }
        while (result.endsWith("\"")){
            result = result.substring(0,result.length()-1);
        }
        return result.trim();
    }
    
    protected final void validateInfo(String info) throws BridgeDBException{
        if (info == null){
            throw new BridgeDBException (INFO + " parameter may not be null");
        }
        if (info.trim().isEmpty()){
            throw new BridgeDBException (INFO + " parameter may not be empty");
        }        
    }
    
    void validateInputStream(InputStream inputStream) throws BridgeDBException {
        if (inputStream == null){
            throw new BridgeDBException (FILE + " parameter may not be null");
        }
    }

     protected String serviceName(){
        return "BridgeDb ";
    }
    
    public StringBuilder topAndSide(String header, HttpServletRequest httpServletRequest) {
        return topAndSide(header, "function loadAll(){}\n", httpServletRequest);
    }
    
    public StringBuilder topAndSide(String header, String scriptOther, HttpServletRequest httpServletRequest) {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("TITLE", serviceName() + header);
        velocityContext.put("SCRIPT_OTHER", scriptOther);            
        StringBuilder sb = new StringBuilder(WebTemplates.getForm(velocityContext, WebTemplates.FRAME));
        sideBar(sb, httpServletRequest);
        sb.append("<div id=\"content\">");
        return sb;
    }
    
    protected void sideBar(StringBuilder sb, HttpServletRequest httpServletRequest) {
        sb.append("<div id=\"navBar\">");
        addSideBarMiddle(sb, httpServletRequest);
        addSideBarStatisitics(sb, httpServletRequest);
        sb.append("</div>\n");        
    }
    
    /**
     * Allows Super classes to add to the side bar
     */
    public void addSideBarMiddle(StringBuilder sb, HttpServletRequest httpServletRequest){
        addSideBarBridgeDb(sb, httpServletRequest);
    }
    
    /**
     * Allows Super classes to add to the side bar
     */
    protected void addSideBarBridgeDb(StringBuilder sb, HttpServletRequest httpServletRequest) {
        sb.append("<div class=\"menugroup\">BridgeDb Service</div>");
        addSideBarItem(sb, WsUriConstants.BRIDGEDB_HOME, "Home", httpServletRequest);
        String allMappingInfo = WsUriConstants.SOURCE_INFOS + "?" + WsUriConstants.LENS_URI + "=" + Lens.ALL_LENS_NAME;
        addSideBarItem(sb, allMappingInfo,"All Mappings Summary", httpServletRequest);
        addSideBarItem(sb,  WsUriConstants.SOURCE_INFOS, "Default Mappings Summary", httpServletRequest);
        //String allGraphwiz = WsUriConstants.GRAPHVIZ + "?" + WsUriConstants.LENS_URI + "=" + Lens.ALL_LENS_NAME;
        //addSideBarItem(sb, allGraphwiz, "All Mappings Graphviz",  httpServletRequest);
        //addSideBarItem(sb, WsUriConstants.GRAPHVIZ, "Default Mappings Graphviz",  httpServletRequest);
        addSideBarItem(sb, Lens.METHOD_NAME, Lens.METHOD_NAME,  httpServletRequest);
        addSideBarItem(sb, WsUriConstants.URI_SPACES_PER_GRAPH, "UriSpace(s) per Graph", httpServletRequest);
        addSideBarItem(sb, WsUriConstants.BRIDGEDB_API, "Api", httpServletRequest);
    }

    /**
     * Allows Super classes to add to the side bar
     */
    protected void addSideBarStatisitics(StringBuilder sb, HttpServletRequest httpServletRequest) {
        try {
            OverallStatistics statistics = uriMapper.getOverallStatistics(Lens.DEFAULT_LENS_NAME);
            //sb.append("\n<div class=\"menugroup\">Default Statisitics</div>");
            //addSideBarItem(sb, "getMappingInfo", formatter.format(statistics.getNumberOfMappings()) + " Mappings", httpServletRequest);
            //addSideBarItem(sb, "getMappingInfo", formatter.format(statistics.getNumberOfMappingSets()) + " Mapping Sets", httpServletRequest);
            //addSideBarItem(sb, "getSupportedSrcDataSources", formatter.format(statistics.getNumberOfSourceDataSources()) 
            //        + " Source Data Sources", httpServletRequest);
            //addSideBarItem(sb, "getMappingInfo", formatter.format(statistics.getNumberOfPredicates()) + " Predicates", httpServletRequest);
            //addSideBarItem(sb, "getSupportedTgtDataSources", formatter.format(statistics.getNumberOfTargetDataSources()) 
             //       + " Target Data Sources ", httpServletRequest);
            statistics = uriMapper.getOverallStatistics(Lens.ALL_LENS_NAME);
            //sb.append("\n<div class=\"menugroup\">All Statisitics</div>");
            sb.append("\n<div class=\"menugroup\">Statisitics</div>");
            addSideBarItem(sb, "getMappingInfo", formatter.format(statistics.getNumberOfMappings()) + " Mappings", httpServletRequest);
            addSideBarItem(sb, "getMappingInfo", formatter.format(statistics.getNumberOfMappingSets()) + " Mapping Sets", httpServletRequest);
            addSideBarItem(sb, "getSupportedSrcDataSources", formatter.format(statistics.getNumberOfSourceDataSources()) 
                    + " Source Data Sources", httpServletRequest);
            addSideBarItem(sb, "getMappingInfo", formatter.format(statistics.getNumberOfPredicates()) + " Predicates", httpServletRequest);
            addSideBarItem(sb, "getSupportedTgtDataSources", formatter.format(statistics.getNumberOfTargetDataSources()) 
                    + " Target Data Sources ", httpServletRequest);
            addSideBarItem(sb, Lens.METHOD_NAME, formatter.format(statistics.getNumberOfLenses())
                    + " Lenses ", httpServletRequest);
        } catch (BridgeDBException ex) {
            sb.append("\nStatisitics currenlty unavailable.");
            logger.error("Error getting statistics.", ex);
        }
    }
    
    /**
     * Adds an item to the SideBar for this service
     */
    public void addSideBarItem(StringBuilder sb, String page, String name, HttpServletRequest httpServletRequest) {
        sb.append("\n<div id=\"menu");
        sb.append(page);
        sb.append("_text\" class=\"texthotlink\" ");
        sb.append("onmouseout=\"DHTML_TextRestore('menu");
        sb.append(page);
        sb.append("_text'); return true; \" ");
        sb.append("onmouseover=\"DHTML_TextHilight('menu");
        sb.append(page);
        sb.append("_text'); return true; \" ");
        sb.append("onclick=\"document.location = &quot;");
        sb.append(httpServletRequest.getContextPath());
        sb.append("/");
        sb.append(page);
        sb.append("&quot;;\">");
        sb.append(name);
        sb.append("</div>");
     }

    protected void footerAndEnd(StringBuilder sb) {
        sb.append("</div>\n<div id=\"footer\">");
        sb.append("\n<div></body></html>");
    }

    public void generateLensSelector(StringBuilder sb, HttpServletRequest httpServletRequest) throws BridgeDBException {
        List<Lens> lenses = LensTools.getLens(LensTools.ALL_GROUP_NAME);
        sb.append("<p>");
    	sb.append(WsUriConstants.LENS_URI);
        sb.append("<select name=\"");
    	sb.append(WsUriConstants.LENS_URI);
    	sb.append("\">");
		for (Lens lens : lenses) {
			sb.append("<option value=\"");
 			sb.append(lens.toUri(httpServletRequest.getContextPath()));
			sb.append("\">");
			sb.append(lens.getName());
			sb.append("</option>");
		}
    	sb.append("</select>\n");
	}

}
