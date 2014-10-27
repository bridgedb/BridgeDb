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
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.bridgedb.Xref;
import org.bridgedb.uri.api.Mapping;
import org.bridgedb.uri.api.UriMapper;
import org.bridgedb.uri.tools.GraphResolver;
import org.bridgedb.uri.tools.RegexUriPattern;
import org.bridgedb.uri.tools.UriListener;
import org.bridgedb.uri.ws.WsUriConstants;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.ws.templates.WebTemplates;

/**
 * This class provides the API documentation
 * @author Christian
 */
public class WSAPI extends WSUriInterfaceService {
            
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
        String contextPath = httpServletRequest.getContextPath();
        String apiString = apiStrings.get(contextPath);
        if (apiString == null){
            apiString = createApiPage(contextPath);
            apiStrings.put(contextPath, apiString);
        }
        StringBuilder sb = topAndSide ("bridgeDB API", httpServletRequest);
        sb.append(apiString);
        //sb.append("<h2>").append(WebTemplates.API_SCRIPT).append(" "+apiString.length()).append("</h2>");
        footerAndEnd(sb);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }
    
    private String createApiPage(String contextPath) throws BridgeDBException, UnsupportedEncodingException{
        List<Mapping> mappings = uriMapper.getSampleMapping();
        Mapping mapping1 = mappings.get(1);
       // DataSource dataSource1 = DataSource.getBySystemCode(mapping1.getSourceSysCode());
        Xref sourceXref1 = mapping1.getSource();
        String id1 = sourceXref1.getId();
        String sourceSysCode1 = sourceXref1.getDataSource().getSystemCode();
        String sourceUri1 = mapping1.getSourceUri().iterator().next();
        String tragetSysCode1 = mapping1.getTarget().getDataSource().getSystemCode();

        Mapping mapping2 = mappings.get(2);
        Xref sourceXref2 =  mapping2.getSource();
        String id2 = sourceXref2.getId();
        String sourceSysCode2 = sourceXref2.getDataSource().getSystemCode();
        Xref targetXref2 =  mapping2.getTarget();
        String sourceUri2 = mapping2.getSourceUri().iterator().next();
        String targetUri2 = mapping2.getTargetUri().iterator().next(); 
        String sourceUriSpace2;
        String targetUriSpace2;
        
        if (uriListener != null){
            RegexUriPattern pattern = uriListener.toUriPattern(sourceUri2);
            sourceUriSpace2 = pattern.getUriPattern();
            pattern = uriListener.toUriPattern(targetUri2);
            GraphResolver.addMapping(EXAMPLE_GRAPH, pattern);
            targetUriSpace2 = pattern.getUriPattern();
        } else {
            sourceUriSpace2 = sourceUri2.substring(0, sourceUri2.length()- sourceXref1.getId().length());
            targetUriSpace2 = targetUri2.substring(0, targetUri2.length()- targetXref2.getId().length());
            GraphResolver.addMapping(EXAMPLE_GRAPH, targetUriSpace2);
        }
        boolean freeSearchSupported = idMapper.getCapabilities().isFreeSearchSupported(); 
        Set<String> keys = idMapper.getCapabilities().getKeys();

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("contextPath", contextPath);
        
        //Examples for mapping 1
        velocityContext.put("uri1", sourceUri1);
        velocityContext.put("id1", id1);
        velocityContext.put("code1", sourceSysCode1);
        velocityContext.put("sourceCode1", sourceSysCode1);
        velocityContext.put("targetCode1", tragetSysCode1);
        
        //Exmples for mapping 2
        velocityContext.put("uri2", sourceUri2);
        velocityContext.put("id2", id2);
        velocityContext.put("code2", sourceSysCode2);
        velocityContext.put("sourceUriSpace2", sourceUriSpace2);
        velocityContext.put("targetUriSpace2", targetUriSpace2);
        
        velocityContext.put("graph", EXAMPLE_GRAPH);
        return WebTemplates.getForm(velocityContext, WebTemplates.API_SCRIPT);        
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
        
}

