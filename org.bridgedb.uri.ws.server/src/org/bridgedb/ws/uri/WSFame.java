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
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
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
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.statistics.LensInfo;
import org.bridgedb.uri.Mapping;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.ws.WsUriConstants;

/**
 * This class provides the Reposnse Frame including Top and Sidebar 
 * 
 * @author Christian
 */
public class WSFame extends WSUriInterfaceService {
    
    protected final NumberFormat formatter;
        
    static final Logger logger = Logger.getLogger(WSFame.class);

    public WSFame()  throws BridgeDBException   {
        super();
        URL resource = this.getClass().getClassLoader().getResource(""); 
        formatter = NumberFormat.getInstance();
        if (formatter instanceof DecimalFormat) {
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setGroupingSeparator(',');
            ((DecimalFormat) formatter).setDecimalFormatSymbols(dfs);
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
    @Path("/ims-api")
    public Response imsApiPage(@Context HttpServletRequest httpServletRequest) throws BridgeDBException, UnsupportedEncodingException {
        //Long start = new Date().getTime();
        StringBuilder sb = topAndSide("IMS API",  httpServletRequest);
 
        Mapping mapping1 = uriMapper.getMapping(1);
       // DataSource dataSource1 = DataSource.getBySystemCode(mapping1.getSourceSysCode());
        Xref sourceXref1 = mapping1.getSource();
        String sourceSysCode1 = sourceXref1.getDataSource().getSystemCode();
        String sourceUri1 = mapping1.getSourceUri().iterator().next();
        String tragetSysCode1 = mapping1.getTarget().getDataSource().getSystemCode();
        String text1 = sourceXref1.getId();

        Mapping mapping2 = uriMapper.getMapping(2);
        Xref sourceXref2 =  mapping2.getSource();
        String sourceUri2 = mapping2.getSourceUri().iterator().next();
        String targetUri2 = mapping2.getTargetUri().iterator().next();    
        String targetUriSpace2 = targetUri2.substring(0, targetUri2.length()-sourceXref2.getId().length());
                
        boolean freeSearchSupported = idMapper.getCapabilities().isFreeSearchSupported(); 
        Set<String> keys = idMapper.getCapabilities().getKeys();

        WSUriApi api = new WSUriApi();

        sb.append("<h2>Support services include:<h2>");
        sb.append("<dl>");      
        api.introduce_IDMapper(sb, freeSearchSupported);
        api.introduce_IDMapperCapabilities(sb, keys, freeSearchSupported);     
        api.introduce_URIMapper(sb, freeSearchSupported);
        api.introduce_Info(sb);
        sb.append("</dl>");
        sb.append("</p>");
        
        api.describeParameter(sb);        
        
        api.describe_IDMapper(sb, sourceXref1, tragetSysCode1, sourceXref2, freeSearchSupported);
        api.describe_IDMapperCapabilities(sb, sourceXref1, tragetSysCode1, keys, freeSearchSupported);
        api.describe_UriMapper(sb, sourceXref1, sourceUri1, sourceXref2, sourceUri2, targetUriSpace2, 
                text1, 1, sourceSysCode1, freeSearchSupported);
        api.describe_Info(sb, sourceXref1, sourceSysCode1, tragetSysCode1);
        api.describe_Graphviz(sb);
        
        sb.append("</body></html>");
        //ystem.out.println("Done "+ (new Date().getTime() - start));
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }
    
    protected StringBuilder topAndSide(String header, HttpServletRequest httpServletRequest) throws BridgeDBException{
        StringBuilder sb = header(header);
        top(sb, header);      
        sideBar(sb, httpServletRequest);
        sb.append("<div id=\"content\">");
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
        sb.append("<title>");
        sb.append(header);
        sb.append("</title>\n");
        style(sb);
        toggler(sb);
        sb.append("</head><body>");
        return sb;
    }

    protected void style(StringBuilder sb) throws BridgeDBException{
        sb.append("<style>\n");
        sb.append("#container { width: 100%; margin: 10px auto; background-color: #fff; color: #333; border: ");
            sb.append("1px solid gray; line-height: 130%; font-family: perpetua, garamond, serif; font-size: 110%; ");
            sb.append("min-width: 40em; }\n");
        sb.append("#top { padding: .5em; background-color: #808080; border-bottom: 1px solid gray; }\n");
        sb.append("#top h1 { padding: .25em .5em .25em .5em; margin-left: 200px; margin-bottom: 0; margin-right: 0; margin-top: 0 }\n");
        sb.append("#top a { text-decoration: none; color: #ffffff; }\n");
        sb.append("#navBar { float: left; width: 200px; margin: 0em; padding: 5px; min-width: 200px; border-right: 1px solid gray; min-height: 100%} \n");
        sb.append("#content { margin-left: 210px; border-left: 1px solid gray; padding: 1em; min-width: 20em; min-height: 500px; }\n");
        sb.append("#footer { clear:both; }\n");
        sb.append("fieldset {border: 1px solid #781351;width: 20em}\n");
        sb.append("legend { color: #fff; background: #ffa20c; border: 1px solid #781351; padding: 2px 6px }\n");
        sb.append("</style>\n");
        sb.append("<style type=\"text/css\">");
        sb.append("	.texthotlink, .texthotlink_hilight { width: 150px; font-size: 85%; padding: .25em; cursor: ");
            sb.append("pointer; color: black; font-family: Arial, sans-serif;	}\n");
        sb.append("	.texthotlink_hilight {background-color: #fff6ac;}\n");
        sb.append("		.menugroup { font-size: 150%; font-weight: bold; padding-top: .25em; }\n");
        sb.append("		input { background-color: #EEEEFF; } body, td { background-color: white; font-family: sans-serif; }\n");
        sb.append("	</style>\n");            
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
        sb.append("<h1>");
        sb.append(header);
        sb.append("</h1>");
        sb.append("</div>");   
    }
    
    protected void sideBar(StringBuilder sb, HttpServletRequest httpServletRequest) throws BridgeDBException{
        sb.append("<div id=\"navBar\">");
        addSideBarMiddle(sb, httpServletRequest);
        sb.append("</div>\n");        
    }
    
    /**
     * Allows Super classes to add to the side bar
     */
    protected void addSideBarMiddle(StringBuilder sb, HttpServletRequest httpServletRequest) throws BridgeDBException{
        addSideBarIMS(sb, httpServletRequest);
        addSideBarStatisitics(sb, httpServletRequest);
    }
    
    /**
     * Allows Super classes to add to the side bar
     */
    protected void addSideBarIMS(StringBuilder sb, HttpServletRequest httpServletRequest) throws BridgeDBException{
        sb.append("<div class=\"menugroup\">OPS Identity Mapping Service</div>");
        addSideBarItem(sb, "ims-home", "Home", httpServletRequest);
        addSideBarItem(sb, "getMappingInfo", "Mappings Summary", httpServletRequest);
        addSideBarItem(sb, "graphviz", "Mappings Summary in Graphviz format",  httpServletRequest);
        addSideBarItem(sb, "ims-api", "IMS API", httpServletRequest);
    }

    /**
     * Allows Super classes to add to the side bar
     */
    protected void addSideBarStatisitics(StringBuilder sb, HttpServletRequest httpServletRequest) throws BridgeDBException{
        OverallStatistics statistics = uriMapper.getOverallStatistics();
        sb.append("\n<div class=\"menugroup\">Statisitics</div>");
        addSideBarItem(sb, "getMappingInfo", formatter.format(statistics.getNumberOfMappings()) + " Mappings", httpServletRequest);
        addSideBarItem(sb, "getMappingInfo", formatter.format(statistics.getNumberOfMappingSets()) + " Mapping Sets", httpServletRequest);
        addSideBarItem(sb, "getSupportedSrcDataSources", formatter.format(statistics.getNumberOfSourceDataSources()) 
                + " Source Data Sources", httpServletRequest);
        addSideBarItem(sb, "getMappingInfo", formatter.format(statistics.getNumberOfPredicates()) + " Predicates", httpServletRequest);
        addSideBarItem(sb, "getSupportedTgtDataSources", formatter.format(statistics.getNumberOfTargetDataSources()) 
                + " Target Data Sources ", httpServletRequest);
    }
    
    /**
     * Adds an item to the SideBar for this service
     */
    protected void addSideBarItem(StringBuilder sb, String page, String name, HttpServletRequest httpServletRequest) throws BridgeDBException{
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

    protected void footerAndEnd(StringBuilder sb) throws BridgeDBException{
        sb.append("</div>\n<div id=\"footer\">");
        sb.append("This site is run by <a href=\"https://wiki.openphacts.org/index.php/User:Christian\">Christian Brenninkmeijer</a>.");
        sb.append("\n<div></body></html>");
    }

	public void generateLensSelector(StringBuilder sb) throws BridgeDBException {
		List<LensInfo> lenses = uriMapper.getLens();
        sb.append("<p>");
    	sb.append(WsUriConstants.LENS_URI);
        sb.append("<select name=\"");
    	sb.append(WsUriConstants.LENS_URI);
    	sb.append("\">");
		for (LensInfo lens : lenses) {
			sb.append("<option value=\"");
			sb.append(lens.getUri());
			sb.append("\">");
			sb.append(lens.getName());
			sb.append("</option>");
		}
    	sb.append("</select>\n");
	}


}


