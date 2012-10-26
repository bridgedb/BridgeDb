// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.metadata.validator.ValidationType;
import org.bridgedb.mysql.MySQLSpecific;
import org.bridgedb.rdf.RdfConfig;
import org.bridgedb.rdf.RdfReader;
import org.bridgedb.rdf.RdfFactory;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.url.URLMapping;
import org.bridgedb.utils.Reporter;
import org.bridgedb.utils.StoreType;
import org.bridgedb.ws.WSOpsService;

/**
 *
 * @author Christian
 */
public class WSOpsServer extends WSOpsService implements Comparator<MappingSetInfo>{
    
    private NumberFormat formatter;
    
    public WSOpsServer()  throws IDMapperException   {
        SQLAccess sqlAccess = SqlFactory.createSQLAccess(StoreType.LIVE);
        urlMapper = new SQLUrlMapper(false, sqlAccess, new MySQLSpecific());
        idMapper = urlMapper;
        formatter = NumberFormat.getInstance();
        if (formatter instanceof DecimalFormat) {
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setGroupingSeparator(',');
            ((DecimalFormat) formatter).setDecimalFormatSymbols(dfs);
        }
        Reporter.report("WsOpsServer setup");        
      }
            
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response welcomeMessage() throws IDMapperException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        StringBuilder sbInnerPure;
        StringBuilder sbInnerEncoded;

        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
                + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\"/>");
        sb.append("<head><title>OPS IMS</title></head><body>");
        sb.append("<h1>Open PHACTS Identity Mapping Service</h1>");
        sb.append("<p>Welcome to the prototype Identity Mapping Service. </p>");
       
        OverallStatistics statistics = urlMapper.getOverallStatistics();
        sb.append("<p>Currently the service includes: ");
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
        
        sb.append("<p>The links where last updated ");
        sb.append(idMapper.getCapabilities().getProperty("LastUpdates"));
        sb.append("</p>");
                
        sb.append("<p>A List of which mappings we current have can be found at ");
        sb.append("<a href=\"/OPS-IMS/getMappingInfo\">Mapping Info Page</a></p>");
        
        sb.append("<p>The Main OPS method is <a href=\"/OPS-IMS/api/#mapByURLs\">mapByURLs</a></dt>");
        sb.append("<dd>List the URLs that map to this URL</dd>");
        sb.append("<p><a href=\"/OPS-IMS/api\">API Page</a></p>");
        sb.append("</body></html>");
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/api")
    public Response apiPage() throws IDMapperException, UnsupportedEncodingException {
        //Long start = new Date().getTime();
        StringBuilder sb = new StringBuilder();
 
        Set<String> urls = urlMapper.getSampleSourceURLs();  
        Iterator<String> urlsIt = urls.iterator();
        Xref first = urlMapper.toXref(urlsIt.next());
        String sysCode = first.getDataSource().getSystemCode();
        Xref second =  urlMapper.toXref(urlsIt.next());
        Set<Xref> firstMaps = idMapper.mapID(first);
        Set<String> keys = idMapper.getCapabilities().getKeys();
        String URL1 = urlsIt.next();
        String text = SQLUrlMapper.getId(URL1);
        String URL2 = urlsIt.next();
        Set<URLMapping> mappings2 = urlMapper.mapURLFull(URL2);
        HashSet<String> URI2Spaces = new HashSet<String>();
        int mappingId = 0;
        for (URLMapping mapping:mappings2){
            if (mapping.getId() != null){
                mappingId = mapping.getId();
            }
            String targetURL = mapping.getTargetURLs().iterator().next();
            URI2Spaces.add(SQLUrlMapper.getUriSpace(targetURL));            
        }
        boolean freeSearchSupported = idMapper.getCapabilities().isFreeSearchSupported(); 

        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
                + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\"/>");
        sb.append("<head><title>OPS IMS</title></head><body>");
        sb.append("<h1>Open PHACTS Identity Mapping Service</h1>");
        sb.append("<p><a href=\"/OPS-IMS\">Home Page</a></p>");
                
        sb.append("<p>");
        WSOpsApi api = new WSOpsApi();

        sb.append("Support services include:");
        sb.append("<dl>");      
        api.introduce_IDMapper(sb, freeSearchSupported);
        api.introduce_IDMapperCapabilities(sb, keys, freeSearchSupported);     
        api.introduce_URLMapper(sb, freeSearchSupported);
        api.introduce_Info(sb);
        sb.append("</dl>");
        sb.append("</p>");
        
        api.describeParameter(sb);        
        
        api.describe_IDMapper(sb, first, firstMaps, second, freeSearchSupported);
        api.describe_IDMapperCapabilities(sb, first, firstMaps, keys, freeSearchSupported);
        api.describe_URLMapper(sb, URL1, URL2, URI2Spaces, text, mappingId, sysCode, freeSearchSupported);
        api.describe_Info(sb);
        
        sb.append("</body></html>");
        //ystem.out.println("Done "+ (new Date().getTime() - start));
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/validateVoid")
    public Response validateVoid() throws IDMapperException, UnsupportedEncodingException {
        StringBuilder sb = topAndSide("Query Expander Demo Page");
        addForm(sb, ValidationType.DATASETVOID);
        sb.append(END);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/validateLinkSet")
    public Response validateLinkSet() throws IDMapperException, UnsupportedEncodingException {
        StringBuilder sb = topAndSide("Query Expander Demo Page");
        addForm(sb, ValidationType.DATASETVOID);
        sb.append(END);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/getMappingInfo")
    public Response getMappingInfo() throws IDMapperException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        List<MappingSetInfo> mappingSetInfos = urlMapper.getMappingSetInfos();
        Collections.sort(mappingSetInfos, this);
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
                + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\"/>");
        sb.append("<head><title>OPS IMS</title></head><body>");
        sb.append("<h1>Open PHACTS Identity Mapping Counter per NameSpaces</h1>");
        sb.append("<p>Warning there many not be Distint mappings but just a sum of the mappings from all mapping files.");
        sb.append("So if various sources include the same mapping it will be counted multiple times. </p>");
        sb.append("<p>");
        sb.append("<table border=\"1\">");
        sb.append("<tr>");
        sb.append("<th>Source Data Source</th>");
        sb.append("<th>Target Data source</th>");
        sb.append("<th>Sum of Mappings</th>");
        sb.append("<th>Id</th>");
        sb.append("<th>Transative</th>");
        sb.append("</tr>");
        for (MappingSetInfo info:mappingSetInfos){
            sb.append("<tr>");
            sb.append("<td><a href=\"");
            sb.append(RdfConfig.getTheBaseURI());
            sb.append("dataSource/");
            sb.append(info.getSourceSysCode());
            sb.append("\">");
            sb.append(info.getSourceSysCode());
            sb.append("</a></td>");
            sb.append("<td><a href=\"");
            sb.append(RdfConfig.getTheBaseURI());
            sb.append("dataSource/");
            sb.append(info.getTargetSysCode());
            sb.append("\">");
            sb.append(info.getTargetSysCode());
            sb.append("</a></td>");
            sb.append("<td align=\"right\">");
            sb.append(formatter.format(info.getNumberOfLinks()));
            sb.append("</td>");
            sb.append("<td><a href=\"");
            sb.append(RdfConfig.getTheBaseURI());
            sb.append("mappingSet/");
            sb.append(info.getId());
            sb.append("\">");
            sb.append(info.getId());
            sb.append("</a></td>");
            sb.append("<td>");
            sb.append(info.isTransitive());
            sb.append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>"); 
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }
    
    @Override
    public int compare(MappingSetInfo o1, MappingSetInfo o2) {
        int test = o1.getSourceSysCode().compareTo(o2.getSourceSysCode());
        if (test != 0) return test;
        return o1.getTargetSysCode().compareTo(o2.getTargetSysCode());
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/getMappingTotal")
    public Response mappingTotal() throws IDMapperException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        List<MappingSetInfo> rawProvenaceinfos = urlMapper.getMappingSetInfos();
        SourceTargetCounter sourceTargetCounter = new SourceTargetCounter(rawProvenaceinfos);
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
                + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\"/>");
        sb.append("<head><title>OPS IMS</title></head><body>");
        sb.append("<h1>Open PHACTS Identity Mapping Counter per NameSpaces</h1>");
        sb.append("<p>Warning there many not be Distint mappings but just a sum of the mappings from all mapping files.");
        sb.append("So if various sources include the same mapping it will be counted multiple times. </p>");
        sb.append("<p>");
        sb.append("<table border=\"1\">");
        sb.append("<tr>");
        sb.append("<th>Source Data Source</th>");
        sb.append("<th>Target Data Source</th>");
        sb.append("<th>Sum of Mappings</th>");
        sb.append("</tr>");
        for (MappingSetInfo info:sourceTargetCounter.getSummaryInfos()){
            sb.append("<tr>");
            sb.append("<td><a href=\"");
            sb.append(RdfConfig.getTheBaseURI());
            sb.append("dataSource/");
            sb.append(info.getSourceSysCode());
            sb.append("\">");
            sb.append(info.getSourceSysCode());
            sb.append("</a></td>");
            sb.append("<td><a href=\"");
            sb.append(RdfConfig.getTheBaseURI());
            sb.append("dataSource/");
            sb.append(info.getTargetSysCode());
            sb.append("\">");
            sb.append(info.getTargetSysCode());
            sb.append("</a></td>");
            sb.append("<td align=\"right\">");
            sb.append(formatter.format(info.getNumberOfLinks()));
            sb.append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>"); 
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/graphviz")
    public Response graphvizDot() throws IDMapperException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        List<MappingSetInfo> rawProvenaceinfos = urlMapper.getMappingSetInfos();
        SourceTargetCounter sourceTargetCounter = new SourceTargetCounter(rawProvenaceinfos);
        sb.append("digraph G {");
        for (MappingSetInfo info:sourceTargetCounter.getSummaryInfos()){
            if (info.getSourceSysCode().compareTo(info.getTargetSysCode()) < 0 ){
                sb.append("\"");
                sb.append(info.getSourceSysCode());
                sb.append("\" -> \"");
                sb.append(info.getTargetSysCode());
                sb.append("\" [dir = both, label=\"");
                sb.append(formatter.format(info.getNumberOfLinks())); 
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
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/mappingSet")
    public String mappingSet() throws IDMapperException {
        throw new IDMapperException("Parameter id is missing");
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/mappingSet/{id}")
    public String mappingSet(@PathParam("id") String idString) throws IDMapperException {
        if (idString == null || idString.isEmpty()){
            throw new IDMapperException("Parameter id is missing!");
        }
        Integer id = Integer.parseInt(idString);
        return new RdfReader(StoreType.LIVE).getLinksetRDF(id);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/linkset")
    public String linkset() throws IDMapperException {
        throw new IDMapperException("Parameter id is missing");
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/linkset/{id}")
    public String linksetSet(@PathParam("id") String idString) throws IDMapperException {
        if (idString == null || idString.isEmpty()){
            throw new IDMapperException("Parameter id is missing!");
        }
        Integer id = Integer.parseInt(idString);
        return new RdfReader(StoreType.LIVE).getLinksetRDF(id);
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/linkset/{id}/{resource}")
    public String linksetSet(@PathParam("id") String idString, @PathParam("resource") String resource) throws IDMapperException {
        throw new IDMapperException("id= "+ idString + " resource = " + resource);
        //if (idString == null || idString.isEmpty()){
       //     throw new IDMapperException("Parameter id is missing!");
        //}
        //Integer id = Integer.parseInt(idString);
        //return new RdfReader(StoreType.LIVE).getLinksetRDF(id);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/void")
    public String voidInfo() throws IDMapperException {
        throw new IDMapperException("Parameter id is missing");
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/void/{id}")
    public String voidInfo(@PathParam("id") String idString) throws IDMapperException {
        if (idString == null || idString.isEmpty()){
            throw new IDMapperException("Parameter id is missing");
        }
        Integer id = Integer.parseInt(idString);
        return new RdfReader(StoreType.LIVE).getVoidRDF(id);
    }

    private StringBuilder topAndSide(String header){
        StringBuilder sb = new StringBuilder(HEADER);
        sb.append(BODY);
        sb.append(TOP_LEFT);
        sb.append(header);
        sb.append(TOP_RIGHT);
        sb.append(SIDE_BAR);
        return sb;
    }
    
    private void addForm(StringBuilder sb, ValidationType validationType){
        sb.append("<p>Use this page to validate a ");
        switch (validationType){
            case DATASETVOID: {
                sb.append("VOID descripition.");
                break;
            }
            case LINKS: {
                sb.append("Linkset.");
                break;
            } default:{
                sb.append("ERROR ON PAGE REPORT TO CHRISTIAN");
            }
        }
        sb.append(".</p>");
        
        sb.append("<p>This is an early prototype and subject to change!</p> ");
        
        sb.append("<form method=\"post\" action=\"/OPS-IMS/");
        switch (validationType){
            case DATASETVOID: {
                sb.append("validateStringAsVoid");
                break;
            }
            case LINKS: {
                sb.append("validateStringAsLinkSet");
                break;
            } default:{
                sb.append("ERROR ON PAGE REPORT TO CHRISTIAN");
            }
        }
        sb.append("\">");

        sb.append(FORM_OUTPUT_FORMAT);
        sb.append(FORM_MINE_TYPE);
        sb.append(FORM_INFO_START);
        sb.append(FORM_INFO_END);
        sb.append(FORM_SUBMIT);        
    }
    
    private final String HEADER_START = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
            + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
            + "<html xmlns:v=\"urn:schemas-microsoft-com:vml\">\n"
            + "<head>\n"
            + " <title>"
            + "     Manchester University OpenPhacts Query Expander"
            + "	</title>\n"
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
    private final String HEADER = HEADER_START + HEADER_END;
    private final String TOGGLE_HEADER = HEADER_START + TOGGLER + HEADER_END;
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
    private final String SIDE_BAR = "	<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">"
            + "		<tr valign=\"top\">"
            + "			<td style=\"border-top: 1px solid #D5D5FF\">"
            + "				<div class=\"menugroup\">Query Expander</div>"
            + "				<div id=\"menuQueryExpanderHome_text\" class=\"texthotlink\" "
            + "                   onmouseout=\"DHTML_TextRestore('menuQueryExpanderHome_text'); return true; \" "
            + "                   onmouseover=\"DHTML_TextHilight('menuQueryExpanderHome_text'); return true; \" "
            + "                   onclick=\"document.location = &quot;/QueryExpander&quot;;\">Home</div>"
            + "				<div id=\"menuQueryExpanderAPI_text\" class=\"texthotlink\" "
            + "                   onmouseout=\"DHTML_TextRestore('menuQueryExpanderAPI_text'); return true; \" "
            + "                   onmouseover=\"DHTML_TextHilight('menuQueryExpanderAPI_text'); return true; \" "
            + "                   onclick=\"document.location = &quot;/QueryExpander/api&quot;;\">API</div>"
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
            + "                   Check Mapping for an URI</div>"            
            + "				<div class=\"menugroup\">OPS Identity Mapping Service</div>"
            + "				<div id=\"menuOpsHome_text\" class=\"texthotlink\" "
            + "                   onmouseout=\"DHTML_TextRestore('menuOpsHome_text'); return true; \" "
            + "                   onmouseover=\"DHTML_TextHilight('menuOpsHome_text'); return true; \" "
            + "                   onclick=\"document.location = &quot;/OPS-IMS&quot;;\">Home</div>"
            + "				<div id=\"menuOpsInfo_text\" class=\"texthotlink\" "
            + "                   onmouseout=\"DHTML_TextRestore('menuOpsInfo_text'); return true; \" "
            + "                   onmouseover=\"DHTML_TextHilight('menuOpsInfo_text'); return true; \" "
            + "                   onclick=\"document.location = &quot;/OPS-IMS/getMappingInfo&quot;;\">"
            + "                   Mappings Summary</div>"
            + "				<div id=\"menuGraphviz_text\" class=\"texthotlink\" "
            + "                   onmouseout=\"DHTML_TextRestore('menuGraphviz_text'); return true; \" "
            + "                   onmouseover=\"DHTML_TextHilight('menuGraphviz_text'); return true; \" "
            + "                   onclick=\"document.location = &quot;/OPS-IMS/graphviz&quot;;\">"
            + "                   Mappings Summary in Graphviz format</div>"
            + "				<div id=\"menuOpsApi_text\" class=\"texthotlink\" "
            + "                   onmouseout=\"DHTML_TextRestore('menuOpsApi_text'); return true; \" "
            + "                   onmouseover=\"DHTML_TextHilight('menuOpsApi_text'); return true; \" "
            + "                   onclick=\"document.location = &quot;/OPS-IMS/api&quot;;\">API</div>"
            + "			</td>"
            + "			<td width=\"5\" style=\"border-right: 1px solid #D5D5FF\"></td>"
            + "			<td style=\"border-top: 1px solid #D5D5FF; width:100%\">";
    private final String FORM_OUTPUT_FORMAT = " <p>Output Format:"
            + "     <select size=\"1\" name=\"format\">"
            + "         <option value=\"html\">HTML page</option>"
            + "         <option value=\"xml\">XML/JASON</option>"
            + " 	</select>"
            + " </p>";
    private final String FORM_MINE_TYPE = " <p>Mime Type:"
            + "     <select size=\"1\" name=\"mimeType\">"
            + "         <option value=\"application/x-turtle\">Turtle (mimeType=application/x-turtle; ext=ttl)</option>"
            + "         <option value=\"text/plain\">N-Triples (mimeType=text/plain; ext=nt)</option>"
            + "         <option value=\"application/rdf+xml\">RDF/XML (mimeType=application/rdf+xml; ext=rdf, rdfs, owl, xml</option>"
            + " 	</select>"
            + " </p>";
    private final String FORM_INFO_START = "<p><textarea rows=\"15\" name=\"info\" style=\"width:100%; background-color: #EEEEFF;\">";
    private final String FORM_INFO_END = "</textarea></p>";
    private final String FORM_SUBMIT = " <p><input type=\"submit\" value=\"Validate!\"></input> "
            + "    Note: If the new page does not open click on the address and press enter</p>"
            + "</form>";
    private final String URI_MAPPING_FORM = "<form method=\"get\" action=\"/QueryExpander/mapURI\">"
            + " <p>Input URI (URI to be looked up in Identity Mapping Service.)"
            + "     (see <a href=\"/QueryExpander/api#inputURI\">API</a>)</p>"
            + " <p><input type=\"text\" name=\"inputURI\" style=\"width:100%\"/></p>"
            + " <p>Graph/Context (Graph value to limit the returned URIs)"
            + "     (see <a href=\"/QueryExpander/api#graph\">API</a>)</p>"
            + " <p><input type=\"text\" name=\"graph\" style=\"width:100%\"/></p>"
            + " <p><input type=\"submit\" value=\"Expand!\"></input> "
            + "    Note: If the new page does not open click on the address and press enter</p>"
            + "</form>";
    private final String MAIN_END = "			</td>"
            + "		</tr>"
            + "	</table>"
            + "	<div style=\"border-top: 1px solid #D5D5FF; padding: .5em; font-size: 80%;\">"
            + "		This site is run by <a href=\"https://wiki.openphacts.org/index.php/User:Christian\">Christian Brenninkmeijer</a>."
            + "	</div>";
    private final String BODY_END = "</body>"
            + "</html>";
    private final String END = MAIN_END + BODY_END;
    
}


