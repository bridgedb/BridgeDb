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
package org.bridgedb.ws.server;


import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.bridgedb.IDMapperException;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.ws.WSLinksetService;
import org.bridgedb.ws.WSOpsInterfaceService;
import org.bridgedb.ws.WsOpsConstants;

/**
 *
 * @author Christian
 */
public class WSOpsServer extends WSLinksetService{
    
    static final Logger logger = Logger.getLogger(WSOpsInterfaceService.class);

    public WSOpsServer()  throws IDMapperException   {
        super();
        logger.info("WsOpsServer setup");        
    }
       
    /**
     * Welcome page for the Serivce.
     * 
     * Expected to be overridden by the QueryExpander
     * 
     * @param httpServletRequest
     * @return
     * @throws IDMapperException
     * @throws UnsupportedEncodingException 
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response welcomeMessage(@Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
        if (logger.isDebugEnabled()){
            logger.debug("welcomeMessage called!");
        }
        StringBuilder sb = topAndSide("IMS Mapping Service",  httpServletRequest);
       
        OverallStatistics statistics = urlMapper.getOverallStatistics();
        sb.append("\n<p>Currently the service includes: ");
        sb.append("<ul>");
            sb.append("<li>");
                sb.append(formatter.format(statistics.getNumberOfMappings()));
                sb.append(" Mappings</li>");
            sb.append("<li>From ");
                sb.append(formatter.format(statistics.getNumberOfMappingSets()));
                sb.append(" Mapping Sets</li>");
            sb.append("<li>Covering ");
                sb.append(formatter.format(statistics.getNumberOfSourceDataSources()));
                sb.append(" Source Data Sources</li>");
            sb.append("<li>Using ");
                sb.append(formatter.format(statistics.getNumberOfPredicates()));
                sb.append(" Predicates</li>");
            sb.append("<li>Mapping to ");
                sb.append(formatter.format(statistics.getNumberOfTargetDataSources()));
                sb.append(" Target Data Sources</li>");
        sb.append("</ul></p>");
        
        sb.append("\n<p>The links where last updated ");
        sb.append(idMapper.getCapabilities().getProperty("LastUpdates"));
        sb.append("</p>");
                
        sb.append("\n<p>A List of which mappings we current have can be found at ");
        sb.append("<a href=\"/");
        sb.append(getServiceName());
        sb.append("/getMappingInfo\">Mapping Info Page</a></p>");
        
        sb.append("\n<p>The Main OPS method (when not using the QueryExpander on the same machine) is <a href=\"/");
        sb.append(getServiceName());
        sb.append("/api/#");
        sb.append(WsOpsConstants.MAP_URL);
        sb.append("\">");
        sb.append(WsOpsConstants.MAP_URL);
        sb.append("<dd>List the URLs that map to this URL</dd>");
        sb.append("\n<p><a href=\"/");
        sb.append(getServiceName());
        sb.append("/api\">API Page</a></p>");
        sb.append("</body></html>");
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

    /**
     * Forwarding page for "/api".
     * 
     * This is expected to be overwirriten by the QueryExpander
     * @param httpServletRequest
     * @return
     * @throws IDMapperException
     * @throws UnsupportedEncodingException 
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/api")
    public Response apiPage(@Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
        return imsApiPage(httpServletRequest);
    }
    
}


