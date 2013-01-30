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
import java.util.HashSet;
import java.util.Iterator;
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
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.url.Mapping;
import org.bridgedb.utils.BridgeDBException;

/**
 * This class provides the Reposnse Frame including Top and Sidebar 
 * 
 * @author Christian
 */
public class WSFame extends WSUriInterfaceService {
    
    protected final NumberFormat formatter;
        
    static final Logger logger = Logger.getLogger(WSFame.class);

    private final String serviceName1;
    
    public WSFame()  throws BridgeDBException   {
        super();
        URL resource = this.getClass().getClassLoader().getResource(""); 
        serviceName1 = getResourceName();
        formatter = NumberFormat.getInstance();
        if (formatter instanceof DecimalFormat) {
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setGroupingSeparator(',');
            ((DecimalFormat) formatter).setDecimalFormatSymbols(dfs);
        }
    }
        
    private String getResourceName(){
        URL resource = this.getClass().getClassLoader().getResource(""); 
        String path = resource.toString();
        if (path.contains("/webapps/") && path.contains("/WEB-INF/")){
            int start = path.lastIndexOf("/webapps/") + 9;
            String name = path.substring(start, path.lastIndexOf("/WEB-INF/"));
            logger.info("ResourceName = " + name);
            return name;
        }
        if (!path.endsWith("/test-classes/")){
            logger.warn("Unable to get resource name from " + path);
        }
        return getDefaultResourceName();
    }
    
    public final String getServiceName(){
        return serviceName1;
    }
    
    /**
     * Backup in case getResourceName fails.
     * 
     * Super classes will need to insert their own war name.
     * @return war name.
     */
    public String getDefaultResourceName(){
        return "OPS-IMS";
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
 
        List<Mapping> mappings = urlMapper.getSampleMapping(); 
        Mapping mapping1 = mappings.get(0);
        DataSource dataSource1 = DataSource.getBySystemCode(mapping1.getSourceSysCode());
        Xref firstSourceXref = new Xref (mapping1.getSourceId(), dataSource1);
        String sysCode = firstSourceXref.getDataSource().getSystemCode();
        Mapping mapping2 = mappings.get(1);
        DataSource dataSource2 = DataSource.getBySystemCode(mapping1.getSourceSysCode());
        Xref secondSourceXref =  new Xref (mapping2.getSourceId(), dataSource2);
        Set<Xref> firstMaps;
        try{ 
            firstMaps = idMapper.mapID(firstSourceXref);
        } catch (IDMapperException e){
            throw BridgeDBException.convertToBridgeDB(e);
        }

        Iterator<Xref> setIterator = firstMaps.iterator();
        while (setIterator.hasNext()) {
            Xref xref = setIterator.next();
            if (xref.getDataSource() == firstSourceXref.getDataSource()){
                setIterator.remove();
            }
        }
        Set<String> keys = idMapper.getCapabilities().getKeys();
        Mapping mapping3 = mappings.get(2);
        String SourceUrl3 = mapping3.getSourceURL().iterator().next();
        String text = SQLUrlMapper.getId(SourceUrl3);
        Mapping mapping4 = mappings.get(3);
        String sourceUrl4 = mapping4.getSourceURL().iterator().next();
        Mapping mapping5 = mappings.get(4);
        int mappingId = mapping5.getId();
        HashSet<String> URI2Spaces = new HashSet<String>();
        String targetURL = mapping5.getTargetURL().iterator().next();
        URI2Spaces.add(SQLUrlMapper.getUriSpace(targetURL));            
        boolean freeSearchSupported = idMapper.getCapabilities().isFreeSearchSupported(); 

        sb.append("\n<p><a href=\"/");
        sb.append(getServiceName());
        sb.append("\">Home Page</a></p>");
                
        sb.append("\n<p>");
        WSUriApi api = new WSUriApi();

        sb.append("<h2>Support services include:<h2>");
        sb.append("<dl>");      
        api.introduce_IDMapper(sb, freeSearchSupported);
        api.introduce_IDMapperCapabilities(sb, keys, freeSearchSupported);     
        api.introduce_URLMapper(sb, freeSearchSupported);
        api.introduce_Info(sb);
        sb.append("</dl>");
        sb.append("</p>");
        
        api.describeParameter(sb);        
        
        api.describe_IDMapper(sb, firstSourceXref, firstMaps, secondSourceXref, freeSearchSupported);
        api.describe_IDMapperCapabilities(sb, firstSourceXref, firstMaps, keys, freeSearchSupported);
        api.describe_URLMapper(sb, SourceUrl3, sourceUrl4, URI2Spaces, text, mappingId, sysCode, freeSearchSupported);
        api.describe_Info(sb, firstSourceXref, firstMaps);
        api.describe_Graphviz(sb);
        
        sb.append("</body></html>");
        //ystem.out.println("Done "+ (new Date().getTime() - start));
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }
    
    protected StringBuilder topAndSide(String header, HttpServletRequest httpServletRequest) throws BridgeDBException{
        StringBuilder sb = new StringBuilder(HEADER_TO_TITLE);
        sb.append(header);
        sb.append(HEADER_AFTER_TITLE);
        sb.append(TOGGLER);
        sb.append(HEADER_END);
        sb.append(BODY);
        sb.append(TOP_LEFT);
        sb.append(header);
        sb.append(TOP_RIGHT);
        sb.append(SIDE_BAR_BEGIN);
        addSideBarMiddle(sb, httpServletRequest);
        sb.append(SIDE_BAR_END);
        return sb;
    }
    
    /**
     * Allows Super classes to add to the side bar
     */
    protected void addSideBarMiddle(StringBuilder sb, HttpServletRequest httpServletRequest) throws BridgeDBException{
        addSideBarIMS(sb);
        addSideBarStatisitics(sb);
    }
    
    /**
     * Allows Super classes to add to the side bar
     */
    protected void addSideBarIMS(StringBuilder sb) throws BridgeDBException{
        sb.append("<div class=\"menugroup\">OPS Identity Mapping Service</div>");
        addSideBarItem(sb, "", "Home");
        addSideBarItem(sb, "getMappingInfo", "Mappings Summary");
        addSideBarItem(sb, "graphviz", "Mappings Summary in Graphviz format");
        addSideBarItem(sb, "ims-api", "IMS API");
    }

    /**
     * Allows Super classes to add to the side bar
     */
    protected void addSideBarStatisitics(StringBuilder sb) throws BridgeDBException{
        OverallStatistics statistics = urlMapper.getOverallStatistics();
        sb.append("\n<div class=\"menugroup\">Statisitics</div>");
        addSideBarItem(sb, "getMappingInfo", formatter.format(statistics.getNumberOfMappings()) + " Mappings");
        addSideBarItem(sb, "getMappingInfo", formatter.format(statistics.getNumberOfMappingSets()) + " Mapping Sets");
        addSideBarItem(sb, "getSupportedSrcDataSources", formatter.format(statistics.getNumberOfSourceDataSources()) 
                + " Source Data Sources");
        addSideBarItem(sb, "getMappingInfo", formatter.format(statistics.getNumberOfPredicates()) + " Predicates");
        addSideBarItem(sb, "getSupportedTgtDataSources", formatter.format(statistics.getNumberOfTargetDataSources()) 
                + " Target Data Sources ");
    }
    
    /**
     * Adds an item to the SideBar for this service
     */
    protected void addSideBarItem(StringBuilder sb, String page, String name) throws BridgeDBException{
        sb.append("\n<div id=\"menu");
        sb.append(page);
        sb.append("_text\" class=\"texthotlink\" ");
        sb.append("onmouseout=\"DHTML_TextRestore('menu");
        sb.append(page);
        sb.append("_text'); return true; \" ");
        sb.append("onmouseover=\"DHTML_TextHilight('menu");
        sb.append(page);
        sb.append("_text'); return true; \" ");
        sb.append("onclick=\"document.location = &quot;/");
        sb.append(getServiceName());
        sb.append("/");
        sb.append(page);
        sb.append("&quot;;\">");
        sb.append(name);
        sb.append("</div>");
     }

    private final String HEADER_TO_TITLE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
            + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
            + "<html xmlns:v=\"urn:schemas-microsoft-com:vml\">\n"
            + "<head>\n"
            + " <title>"
            + "     Manchester University OpenPhacts ";
    private final String HEADER_AFTER_TITLE = "	</title>\n"
            + "	<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\"></meta>\n"
            + "	<script>"
            + "		function getObj(id) {"
            + "			return document.getElementById(id)"
            + "		}"
            + "		function DHTML_TextHilight(id) {"
            + "			getObj(id).classNameOld = getObj(id).className;"
            + "			getObj(id).className = getObj(id).className + \"_hilight\";"
            + "		}"
            + "		function DHTML_TextRestore(id) {"
            + "			if (getObj(id).classNameOld != \"\")"
            + "				getObj(id).className = getObj(id).classNameOld;"
            + "		}"
            + "	</script>\n";
    private final String TOGGLER ="<script language=\"javascript\">\n"
            + "function getItem(id)\n"
            + "{\n"
            + "    var itm = false;\n"
            + "    if(document.getElementById)\n"
            + "        itm = document.getElementById(id);\n"
            + "    else if(document.all)\n"
            + "        itm = document.all[id];\n"
            + "     else if(document.layers)\n"
            + "        itm = document.layers[id];\n"
            + "    return itm;\n"
            + "}\n\n"
            + "function toggleItem(id)\n"
            + "{\n"
            + "    itm = getItem(id);\n"
            + "    if(!itm)\n"
            + "        return false;\n"
            + "    if(itm.style.display == 'none')\n"
            + "        itm.style.display = '';\n"
            + "    else\n"
            + "        itm.style.display = 'none';\n"
            + "    return false;\n"
            + "}\n\n"
            + "function hideDetails()\n"
            + "{\n"
            + "     toggleItem('ops')\n"
            + "     toggleItem('sparql')\n"
            + "     return true;\n"
            + "}\n\n"
            + "</script>\n";
    private final String HEADER_END = "	<style type=\"text/css\">"
            + "		.texthotlink, .texthotlink_hilight {"
            + "			width: 150px;"
            + "			font-size: 85%;"
            + "			padding: .25em;"
            + "			cursor: pointer;"
            + "			color: black;"
            + "			font-family: Arial, sans-serif;"
            + "		}"
            + "		.texthotlink_hilight {"
            + "			background-color: #fff6ac;"
            + "		}"
            + "		.menugroup {"
            + "			font-size: 90%;"
            + "			font-weight: bold;"
            + "			padding-top: .25em;"
            + "		}"
            + "		input { background-color: #EEEEFF; }"
            + "		body, td {"
            + "			background-color: white;"
            + "			font-family: sans-serif;"
            + "		}"
            + "	</style>\n"
            + "</head>\n";            
    private final String BODY ="<body style=\"margin: 0px\">";
    private final String TOP_LEFT ="	<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n"
            + "		<tr valign=\"top\">\n"
            + "			<td style=\"background-color: white;\">"
            + "				<a href=\"http://www.openphacts.org/\">"
            + "                 <img style=\"border: none; padding: 0px; margin: 0px;\" "
            + "                     src=\"http://www.openphacts.org/images/stories/banner.jpg\" "
            + "                     alt=\"Open PHACTS\" height=\"50\">"
            + "                 </img>"
            + "             </a>"
            + "			</td>\n"
            + "			<td style=\"font-size: 200%; font-weight: bold; font-family: Arial;\">\n";
    private final String TOP_RIGHT = "         </td>"
            + "			<td style=\"background-color: white;\">"
            + "				<a href=\"http://www.cs.manchester.ac.uk//\">"
            + "                 <img style=\"border: none; padding: 0px; margin: 0px;\" align=\"right\" "
            + "                     src=\"http://www.manchester.ac.uk/media/corporate/theuniversityofmanchester/assets/images/logomanchester.gif\" "
            + "                    alt=\"The University of Manchester\" height=\"50\">"
            + "                 </img>"
            + "             </a>"
            + "			</td>"
            + "		</tr>"
            + "	</table>";
    private final String SIDE_BAR_BEGIN = "	<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">"
            + "		<tr valign=\"top\">"
            + "			<td style=\"border-top: 1px solid #D5D5FF\">";
    private final String SIDE_BAR_QUERY_EXPANDER = "<div class=\"menugroup\">Query Expander</div>"
            + "				<div id=\"menuQueryExpanderHome_text\" class=\"texthotlink\" "
            + "                   onmouseout=\"DHTML_TextRestore('menuQueryExpanderHome_text'); return true; \" "
            + "                   onmouseover=\"DHTML_TextHilight('menuQueryExpanderHome_text'); return true; \" "
            + "                   onclick=\"document.location = &quot;/QueryExpander&quot;;\">Home</div>"
            + "				<div id=\"menuQueryExpanderAPI_text\" class=\"texthotlink\" "
            + "                   onmouseout=\"DHTML_TextRestore('menuQueryExpanderAPI_text'); return true; \" "
            + "                   onmouseover=\"DHTML_TextHilight('menuQueryExpanderAPI_text'); return true; \" "
            + "                   onclick=\"document.location = &quot;/QueryExpander/ims-api&quot;;\">API</div>"
            + "				<div id=\"menuQueryExpanderExamples_text\" class=\"texthotlink\" "
            + "                   onmouseout=\"DHTML_TextRestore('menuQueryExpanderExamples_text'); return true; \" "
            + "                   onmouseover=\"DHTML_TextHilight('menuQueryExpanderExamples_text'); return true; \" "
            + "                   onclick=\"document.location = &quot;/QueryExpander/examples&quot;;\">Examples</div>"
            + "				<div id=\"menuQueryExpanderURISpacesPerGraph_text\" class=\"texthotlink\" "
            + "                   onmouseout=\"DHTML_TextRestore('menuQueryExpanderURISpacesPerGraph_text'); return true; \" "
            + "                   onmouseover=\"DHTML_TextHilight('menuQueryExpanderURISpacesPerGraph_text'); return true; \" "
            + "                   onclick=\"document.location = &quot;/QueryExpander/URISpacesPerGraph&quot;;\">"
            + "                   URISpaces per Graph</div>"
            + "				<div id=\"menuQueryExpanderMapURI_text\" class=\"texthotlink\" "
            + "                   onmouseout=\"DHTML_TextRestore('menuQueryExpanderMapURI_text'); return true; \" "
            + "                   onmouseover=\"DHTML_TextHilight('menuQueryExpanderMapURI_text'); return true; \" "
            + "                   onclick=\"document.location = &quot;/QueryExpander/mapURI&quot;;\">"
            + "                   Check Mapping for an URI</div>";          
    
    private final String SIDE_BAR_END =
              "			</td>"
            + "			<td width=\"5\" style=\"border-right: 1px solid #D5D5FF\"></td>"
            + "			<td style=\"border-top: 1px solid #D5D5FF; width:100%\">";
    
    final String FORM_OUTPUT_FORMAT = " \n<p>Output Format:"
            + "     <select size=\"1\" name=\"format\">"
            + "         <option value=\"html\">HTML page</option>"
            + "         <option value=\"xml\">XML/JASON</option>"
            + " 	</select>"
            + " </p>";
    private final String MAIN_END = "			</td>"
            + "		</tr>"
            + "	</table>"
            + "	<div style=\"border-top: 1px solid #D5D5FF; padding: .5em; font-size: 80%;\">"
            + "		This site is run by <a href=\"https://wiki.openphacts.org/index.php/User:Christian\">Christian Brenninkmeijer</a>."
            + "	</div>";
    private final String BODY_END = "</body>"
            + "</html>";
    final String END = MAIN_END + BODY_END;


}


