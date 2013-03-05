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
    
    private final void statsBlock1(StringBuilder sb) throws BridgeDBException {
        OverallStatistics statistics = uriMapper.getOverallStatistics();
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
        sb.append("</ul></p></div>\n");        
    }
    
        /**
     * Allows Super classes to add to the side bar
     */
    protected void statsBlock(StringBuilder sb) throws BridgeDBException{
        OverallStatistics statistics = uriMapper.getOverallStatistics();
        sb.append("<div id=\"navBar\"><h2>Service statistics</h2>");
        sb.append("\n<div class=\"menugroup\">Statisitics</div>");
        addSideBarItem(sb, "getMappingInfo", formatter.format(statistics.getNumberOfMappings()) + " Mappings");
        addSideBarItem(sb, "getMappingInfo", formatter.format(statistics.getNumberOfMappingSets()) + " Mapping Sets");
        addSideBarItem(sb, "getSupportedSrcDataSources", formatter.format(statistics.getNumberOfSourceDataSources()) 
                + " Source Data Sources");
        addSideBarItem(sb, "getMappingInfo", formatter.format(statistics.getNumberOfPredicates()) + " Predicates");
        addSideBarItem(sb, "getSupportedTgtDataSources", formatter.format(statistics.getNumberOfTargetDataSources()) 
                + " Target Data Sources ");
        sb.append("</div>\n");        
    }

    private final void uriMappingForm(StringBuilder sb) throws BridgeDBException {
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
    	generateProfileSelector(sb);
    	sb.append("<p><input type=\"submit\" value=\"Submit\"/></p>");
    	sb.append("<p>Note: If the new page does not open click on the address bar and press enter</p>");
    	sb.append("</fieldset></form>\n");
    }

	private void generateProfileSelector(StringBuilder sb) throws BridgeDBException {
		List<ProfileInfo> profiles = uriMapper.getProfiles();
        sb.append("<p><select name=\"");
    	sb.append(WsUriConstants.PROFILE_URI);
    	sb.append("\">");
		for (ProfileInfo profile : profiles) {
			sb.append("<option value=\"");
			sb.append(profile.getUri());
			sb.append("\">");
			sb.append(profile.getName());
			sb.append("</option>");
		}
    	sb.append("</select>\n");
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
        StringBuilder sb = topAndSide ("Open PHACTS Identity Mapping Service"); //topAndSide("IMS Mapping Service",  httpServletRequest);
        
        sb.append("<div id=\"content\">");
        sb.append("<p>Welcome to the Identity Mapping Service. </p>");        
                
        sb.append("\n<p>A List of which mappings we current have can be found at ");
        sb.append("<a href=\"/");
        sb.append(getServiceName());
        sb.append("/getMappingInfo\">Mapping Info Page</a></p>");
        
        uriMappingForm(sb);
        
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

    protected StringBuilder topAndSide(String header) throws BridgeDBException{
        StringBuilder sb = header(header);
        top(sb, header);      
        statsBlock(sb);
        return sb;
    }
    protected StringBuilder header(String header) throws BridgeDBException{
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" ");
        sb.append("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\"/>");
        sb.append("<head>\n");
        sb.append("<title>OPS IMS</title>\n");
        sb.append("<style>\n");
        sb.append("#container { width: 90%; margin: 10px auto; background-color: #fff; color: #333; border: ");
            sb.append("1px solid gray; line-height: 130%; font-family: perpetua, garamond, serif; font-size: 110%; ");
            sb.append("min-width: 40em; }\n");
        sb.append("#top { padding: .5em; background-color: #808080; border-bottom: 1px solid gray; }\n");
        sb.append("#top h1 { padding: .25em .5em .25em .5em; margin-left: 200px; margin-bottom: 0; margin-right: 0; margin-top: 0 }\n");
        sb.append("#top a { text-decoration: none; color: #ffffff; }\n");
        sb.append("#navBar { float: left; width: 200px; margin: 0em; padding: 5px; min-width: 200px; border-right: 1px solid gray; } \n");
        sb.append("#content { margin-left: 210px; border-left: 1px solid gray; padding: 1em; min-width: 20em; min-height: 500px; }\n");
        sb.append("fieldset {border: 1px solid #781351;width: 20em}\n");
        sb.append("legend { color: #fff; background: #ffa20c; border: 1px solid #781351; padding: 2px 6px }\n");
        sb.append("</style>\n");
        sb.append("<style type=\"text/css\">");
        sb.append("	.texthotlink, .texthotlink_hilight { width: 150px; font-size: 85%; padding: .25em; cursor: ");
            sb.append("pointer; color: black; font-family: Arial, sans-serif;	}\n");
        sb.append("	.texthotlink_hilight {background-color: #fff6ac;}\n");
        sb.append("		.menugroup { font-size: 90%; font-weight: bold; padding-top: .25em; }\n");
        sb.append("		input { background-color: #EEEEFF; } body, td { background-color: white; font-family: sans-serif; }\n");
        sb.append("	</style>\n");            
        toggler(sb);
        sb.append("</head><body>");
        return sb;
    }

    protected void toggler(StringBuilder sb) throws BridgeDBException{
        sb.append("<script language=\"javascript\">\n");
        sb.append("		function getObj(id) {\n");
        sb.append("			return document.getElementById(id)\n");
        sb.append("		}\n");
        sb.append("		function DHTML_TextHilight(id) {\n");
        sb.append("			getObj(id).classNameOld = getObj(id).className;\n");
        sb.append("			getObj(id).className = getObj(id).className + \"_hilight\";\n");
        sb.append("		}\n");
        sb.append("		function DHTML_TextRestore(id) {\n");
        sb.append("			if (getObj(id).classNameOld != \"\")\n");
        sb.append("				getObj(id).className = getObj(id).classNameOld;\n");
        sb.append("		}\n");
        sb.append("     function getItem(id){\n");
        sb.append("         var itm = false;\n");
        sb.append("         if(document.getElementById)\n");
        sb.append("             itm = document.getElementById(id);\n");
        sb.append("         else if(document.all)\n");
        sb.append("             itm = document.all[id];\n");
        sb.append("         else if(document.layers)\n");
        sb.append("             itm = document.layers[id];\n");
        sb.append("         return itm;\n");
        sb.append("    }\n\n");
        sb.append("    function toggleItem(id)\n");
        sb.append("{\n");
        sb.append("    itm = getItem(id);\n");
        sb.append("    if(!itm)\n");
        sb.append("        return false;\n");
        sb.append("    if(itm.style.display == 'none')\n");
        sb.append("        itm.style.display = '';\n");
        sb.append("    else\n");
        sb.append("        itm.style.display = 'none';\n");
        sb.append("    return false;\n");
        sb.append("}\n\n");
        sb.append("function hideDetails()\n");
        sb.append("{\n");
        sb.append("     toggleItem('ops')\n");
        sb.append("     toggleItem('sparql')\n");
        sb.append("     return true;\n");
        sb.append("}\n\n");
        sb.append("</script>\n");
    }

    protected void top(StringBuilder sb, String header) throws BridgeDBException{
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


