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
import org.bridgedb.rdf.RdfConfig;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.statistics.ProfileInfo;
import org.bridgedb.uri.Profile;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.ws.WsConstants;
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
    
    private final String statsBlock() throws BridgeDBException {
    		OverallStatistics statistics = uriMapper.getOverallStatistics();
    		StringBuilder sb = new StringBuilder();
    		sb.append("<div id=\"navBar\"><h2>Service statistics</h2>");
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
    		sb.append("<li>Number of profiles: ");
    		sb.append(statistics.getNumberOfProfiles());
    		sb.append("</li>");
    		sb.append("<li>Last update: ");
    		sb.append(idMapper.getCapabilities().getProperty("LastUpdates"));
    		sb.append("</li>");
    		sb.append("</ul></p></div>");        
    		return sb.toString();
    }
    
    private final String uriMappingForm() throws BridgeDBException {
    	StringBuilder sb = new StringBuilder();
    	sb.append("<form method=\"get\" action=\"/");
        sb.append(getServiceName());
    	sb.append("/");
    	sb.append(WsUriConstants.MAP);
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
    	sb.append(generateProfileSelector());
    	sb.append("<p><input type=\"submit\" value=\"Submit\"/></p>");
    	sb.append("<p>Note: If the new page does not open click on the address bar and press enter</p>");
    	sb.append("</fieldset></form>");
    	return sb.toString();
    }

	private String generateProfileSelector() throws BridgeDBException {
		List<ProfileInfo> profiles = uriMapper.getProfiles();
		StringBuilder sb = new StringBuilder("<p><select name=\"");
    	sb.append(WsUriConstants.PROFILE_URI);
    	sb.append("\">");
	   	sb.append("<option value=\"");
    	sb.append(Profile.getAllProfile());
    	sb.append("\">Default profile</option>");
		for (ProfileInfo profile : profiles) {
			sb.append("<option value=\"");
			sb.append(profile.getUri());
			sb.append("\">");
			sb.append(profile.getName());
			sb.append("</option>");
		}
    	sb.append("</select>");
    	return sb.toString();
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
        StringBuilder sb = topAndSide("IMS Mapping Service",  httpServletRequest);

        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
                + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\"/>");
        sb.append("<head>" +
        		"<title>OPS IMS</title>" +
        		"<style>" +
        		"#container { width: 90%; margin: 10px auto; background-color: #fff; color: #333; border: 1px solid gray; line-height: 130%; font-family: perpetua, garamond, serif; font-size: 110%; min-width: 40em; }" +
        		"#top { padding: .5em; background-color: #808080; border-bottom: 1px solid gray; }" +
        		"#top h1 { padding: .25em .5em .25em .5em; margin-left: 200px; margin-bottom: 0; margin-right: 0; margin-top: 0 }" +
        		"#top a { text-decoration: none; color: #ffffff; }"+
        		"#navBar { float: left; width: 200px; margin: 0em; padding: 5px; min-width: 200px; border-right: 1px solid gray; } " +
        		"#content { margin-left: 210px; border-left: 1px solid gray; padding: 1em; min-width: 20em; min-height: 500px; }" +
        		"fieldset {border: 1px solid #781351;width: 20em}" + 
        		"legend { color: #fff; background: #ffa20c; border: 1px solid #781351; padding: 2px 6px }" +
        		"</style>" +
        		"</head><body>");
        sb.append("<div id=\"container\">");
        sb.append("<div id=\"top\">");
        sb.append("<a href=\"http://www.cs.manchester.ac.uk/\">" +
        		"<img style=\"float: left; border: none; padding: 0px; margin: 0px;\" " +
        		"src=\"http://www.manchester.ac.uk/media/corporate/theuniversityofmanchester/assets/images/logomanchester.gif\" " +
        		"alt=\"The University of Manchester\" height=\"50\"></img></a>");
        sb.append("<a href=\"http://www.openphacts.org/\">" +
        		"<img style=\"float: right; border: none; padding: 0px; margin: 0px;\" " +
        		"src=\"http://www.openphacts.org/images/stories/banner.jpg\" " +
        		"alt=\"Open PHACTS\" height=\"50\"></img></a>");
        sb.append("<h1>Open PHACTS Identity Mapping Service</h1>");
        sb.append("</div>");
        
        sb.append(statsBlock());
        
        sb.append("<div id=\"content\">");
        sb.append("<p>Welcome to the Identity Mapping Service. </p>");        
                
        sb.append("\n<p>A List of which mappings we current have can be found at ");
        sb.append("<a href=\"/");
        sb.append(getServiceName());
        sb.append("/getMappingInfo\">Mapping Info Page</a></p>");
        
        sb.append(uriMappingForm());
        
        sb.append("<h2>Usage Information</h2>");
        sb.append("\n<p>The Main OPS method is <a href=\"/");
        sb.append(getServiceName());
        sb.append("/api/#");
        sb.append(WsUriConstants.MAP);
        sb.append("\">");
        sb.append(WsUriConstants.MAP);
        sb.append("<dd>List the URIs that map to this URI</dd>");
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


