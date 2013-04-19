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
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.statistics.LensInfo;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.ws.WsUriConstants;

/**
 *
 * @author Christian
 */
public class WSOpsServer extends WSLinksetService{
    
    static final Logger logger = Logger.getLogger(WSUriInterfaceService.class);

    public WSOpsServer()  throws BridgeDBException   {
        super();
        logger.info("WsOpsServer setup");        
    }
    
    private final void uriMappingForm(StringBuilder sb, HttpServletRequest httpServletRequest) throws BridgeDBException {
    	sb.append("<form method=\"get\" action=\"/");
        sb.append(httpServletRequest.getContextPath());
    	sb.append("/");
    	sb.append(WsUriConstants.MAP_URI);
    	sb.append("\">");
    	sb.append("<fieldset>");
    	sb.append("<legend>Mapper</legend>");
    	sb.append("<p><label for=\"");
    	sb.append(WsUriConstants.URI);
    	sb.append("\">Input URI</label>");
    	sb.append("<input type=\"text\" id=\"");
    	sb.append(WsUriConstants.URI);
    	sb.append("\" name=\"");
    	sb.append(WsUriConstants.URI);
    	sb.append("\" style=\"width:80%\"/></p>");
    	generateLensSelector(sb);
    	sb.append("<p><input type=\"submit\" value=\"Submit\"/></p>");
    	sb.append("<p>Note: If the new page does not open click on the address bar and press enter</p>");
    	sb.append("</fieldset></form>\n");
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
        return imsHome(httpServletRequest);
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
    @Path("/ims-home")
    public Response imsHome(@Context HttpServletRequest httpServletRequest) throws BridgeDBException, UnsupportedEncodingException {
        if (logger.isDebugEnabled()){
            logger.debug("imsHome called");
        }
        StringBuilder sb = topAndSide ("Open PHACTS Identity Mapping Service", httpServletRequest);
        
        sb.append("<p>Welcome to the Identity Mapping Service. </p>");        
                
        sb.append("\n<p>A List of which mappings we current have can be found at ");
        sb.append("<a href=\"/");
        sb.append(httpServletRequest.getContextPath());
        sb.append("/getMappingInfo\">Mapping Info Page</a></p>");
        
        uriMappingForm(sb, httpServletRequest);
        
        sb.append("<h2>Usage Information</h2>");
        sb.append("\n<p>The Main OPS method are: <ul>");
        sb.append("\n<dt><a href=\"/");
        sb.append(httpServletRequest.getContextPath());
        sb.append("/api/#");
        sb.append(WsUriConstants.MAP_URI);
        sb.append("\">");
        sb.append(WsUriConstants.MAP_URI);
        sb.append("<dt><dd>List the URIs that map to this/these URI(s)</dd>");
        sb.append("\n<dt><a href=\"/");
        sb.append(httpServletRequest.getContextPath());
        sb.append("/api/#");
        sb.append(WsUriConstants.MAP);
        sb.append("\">");
        sb.append(WsUriConstants.MAP);
        sb.append("<dt><dd>List the full Mappings to this URI/Xref</dd>");
        sb.append("</ul>");
        sb.append("\n<p><a href=\"/");
        sb.append(httpServletRequest.getContextPath());
        sb.append("/api\">API Page</a></p>");
        footerAndEnd(sb);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
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
    
}


