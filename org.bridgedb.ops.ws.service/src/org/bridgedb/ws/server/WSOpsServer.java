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


import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import org.apache.log4j.Logger;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.metadata.validator.ValidationType;
import org.bridgedb.mysql.MySQLSpecific;
import org.bridgedb.rdf.RdfConfig;
import org.bridgedb.rdf.RdfReader;
import org.bridgedb.rdf.RdfWrapper;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.rdf.RdfFactory;
import org.bridgedb.rdf.StatementReader;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.statistics.ProfileInfo;
import org.bridgedb.url.URLMapping;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.IpConfig;
import org.bridgedb.utils.Reporter;
import org.bridgedb.utils.StoreType;
import org.bridgedb.ws.bean.ProfileBean;
import org.bridgedb.ws.WSLinksetService;
import org.bridgedb.ws.WSOpsInterfaceService;
import org.openrdf.rio.RDFFormat;

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
    
    private final String statsBlock() throws IDMapperException {
    		OverallStatistics statistics = urlMapper.getOverallStatistics();
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
    
    private final String uriMappingForm() throws IDMapperException {
    	StringBuilder sb = new StringBuilder();
    	sb.append("<form method=\"get\" action=\"/OPS-IMS/mapURL\">");
    	sb.append("<fieldset>");
    	sb.append("<legend>URL Mapper</legend>");
    	sb.append("<p><label for=\"URL\">Input URI</label>");
    	sb.append("<input type=\"text\" id=\"URL\" name=\"URL\" style=\"width:80%\"/></p>");
    	sb.append(generateProfileSelector());
    	sb.append("<p><input type=\"submit\" value=\"Submit\"/></p>");
    	sb.append("<p>Note: If the new page does not open click on the address bar and press enter</p>");
    	sb.append("</fieldset></form>");
    	return sb.toString();
    }

	private String generateProfileSelector() throws IDMapperException {
		List<ProfileInfo> profiles = urlMapper.getProfiles();
		StringBuilder sb = new StringBuilder("<p><select name=\"profileURL\">");
	   	sb.append("<option value=\"");
    	sb.append(RdfConfig.getProfileURI(0));
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
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response welcomeMessage() throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("welcomeMessage called!");
                }
        StringBuilder sb = new StringBuilder();
        StringBuilder sbInnerPure;
        StringBuilder sbInnerEncoded;

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
                
        sb.append("<p>A list of which mappings we currently have can be found at ");
        sb.append("<a href=\"/OPS-IMS/getMappingInfo\">Mapping Info Page</a></p>");
        
        sb.append(uriMappingForm());
        
        sb.append("<h2>Usage Information</h2>");
        sb.append("<p>The main OPS method is <a href=\"/OPS-IMS/api/#mapByURLs\">mapByURLs</a></dt>");
        sb.append("<dd>List the URLs that map to this URL</dd>");
        sb.append("<p><a href=\"/OPS-IMS/api\">API Page</a></p>");
        sb.append("</div></div></body></html>");
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
        //FIXME: Note sure where the second value should come from here!
        Set<URLMapping> mappings2 = urlMapper.mapURLFull(URL2, "0");
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
        sb.append("\n<p><a href=\"/OPS-IMS\">Home Page</a></p>");
                
        sb.append("\n<p>");
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
    
    //InWSLinksetService
    public Response validateVoid(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType) throws IDMapperException, UnsupportedEncodingException {
        return null;
    }

    //InWSLinksetService
    public Response validateVoidGet(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType) throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

    //InWSLinksetService
    public Response validateTurtleVoid(@FormDataParam("file") InputStream uploadedInputStream) 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

    //InWSLinksetService
    public Response validateTurtleVoidGet() 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

    //InWSLinksetService
    public Response validateRdfXmlVoid(@FormDataParam("file") InputStream uploadedInputStream) 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

    //InWSLinksetService
    public Response validateRdfXmlVoidGet() 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

    //InWSLinksetService
    public Response validateNTriplesVoid(@FormDataParam("file") InputStream uploadedInputStream) 
            throws IDMapperException, UnsupportedEncodingException {
        return null;
    }

    //InWSLinksetService
    public Response validateNTriplesVoidGet() 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

    //InWSLinksetService
    public Response validateLinkSet(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType) throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }
    
    //InWSLinksetService
    public Response validateLinkSetGet(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType) throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

    //InWSLinksetService
    public Response validateTurtleLinkSet(@FormDataParam("file") InputStream uploadedInputStream) 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

    //InWSLinksetService
    public Response validateTurtleLinkSetGet() 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

    //InWSLinksetService
    public Response validateRdfXmlLinkSet(@FormDataParam("file") InputStream uploadedInputStream) 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

    //InWSLinksetService
    public Response validateRdfXmlLinkSetGet() 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

    //InWSLinksetService
    public Response validateNTriplesLinkSet(@FormDataParam("file") InputStream uploadedInputStream) 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

    //InWSLinksetService
    public Response validateNTriplesLinkSetGet() 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

    //InWSLinksetService
    public Response validateMinimum(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType) throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }
    
    //InWSLinksetService
    public Response validateMinimumGet(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType) throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

    //InWSLinksetService
    public Response validateTurtleMinimum(@FormDataParam("file") InputStream uploadedInputStream) 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

    //InWSLinksetService
    public Response validateTurtleMinimumGet() 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

    //InWSLinksetService
    public Response validateRdfXmlMinimum(@FormDataParam("file") InputStream uploadedInputStream) 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

    //InWSLinksetService
    public Response validateRdfXmlMinimumGet() 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

    //InWSLinksetService
    public Response validateNTriplesMinimum(@FormDataParam("file") InputStream uploadedInputStream) 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

    //InWSLinksetService
    public Response validateNTriplesMinimumGet() 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

    //InWSLinksetService
    public Response validateRdf(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType) throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }
    
   //InWSLinksetService
     public Response validateRdfGet(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType) throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

   //InWSLinksetService
     public Response validateTurtleRdf(@FormDataParam("file") InputStream uploadedInputStream) 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

   //InWSLinksetService
     public Response validateTurtleRdfGet() 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

   //InWSLinksetService
     public Response validateRdfXmlRdf(@FormDataParam("file") InputStream uploadedInputStream) 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

   //InWSLinksetService
     public Response validateRdfXmlRdfGet() 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

   //InWSLinksetService
     public Response validateNTriplesRdf(@FormDataParam("file") InputStream uploadedInputStream) 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

   //InWSLinksetService
     public Response validateNTriplesRdfGet() 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

   //InWSLinksetService
     public Response validateFileIndexGet(@Context HttpServletRequest hsr) 
            throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

   //InWSLinksetService
     private Response validate(String info, String mimeType, ValidationType validationType) throws IDMapperException, UnsupportedEncodingException {
       String report = null;
        return null;    
    }

    //InWSLinksetService
    private Response validate(InputStream input, RDFFormat format, ValidationType validationType) throws IDMapperException, UnsupportedEncodingException {
       String report = null;
        return null;    
    }

    //InWSLinksetService
    private Response loadString(String source, String info, RDFFormat format, ValidationType validationType) 
            throws IDMapperException{
        return null;    
    }
    
    //in WSUrlService()
    public Response getMappingInfo() throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }
    
    //in WSUrlService()
    public int compare(MappingSetInfo o1, MappingSetInfo o2) {
        return 1/0;    
    }

    //in WSUrlService()
    public Response mappingTotal() throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }
    
     //in WSUrlService()
    public Response graphvizDot() throws IDMapperException, UnsupportedEncodingException {
        return null;    
    }

     //in WSUrlService()
    public String mappingSet() throws IDMapperException {
        return null;    
    }

    //in WSUrlService()
    public String mappingSet(@PathParam("id") String idString) throws IDMapperException {
        return null;    
    }

    //in WSUrlService()
    public String linkset() throws IDMapperException {
        return null;    
    }

    //in WSUrlService()
    public String linksetSet(@PathParam("id") String idString) throws IDMapperException {
        return null;    
    }
    
     //in WSUrlService()
    public String linksetSet(@PathParam("id") String idString, @PathParam("resource") String resource) throws IDMapperException {
        return null;    //
    }

     //in WSUrlService()
    public String voidInfo() throws IDMapperException {
        return null;    //
    }

    //in WSUrlService()
    public String voidInfo(@PathParam("id") String idString) throws IDMapperException {
        return null;    
    }

    //in WSFame
    public StringBuilder topAndSide(String header){
        return null;    
    }
    
    //in WSLinksetService
    private void addForm(StringBuilder sb, ValidationType validationType, String info, String report) throws BridgeDBException{
    }
    
    //WSLinksetService
    private void addFileForm(StringBuilder sb, ValidationType validationType, String report) throws BridgeDBException{
    }

   //in WSLinksetService
    private void addValidationExplanation(StringBuilder sb, ValidationType validationType) throws BridgeDBException{
    }

   //in WSLinksetService
    private void addFormStart(StringBuilder sb, ValidationType validationType) throws BridgeDBException{
    }
    
   //in WSLinksetService
    private void addFileLine(StringBuilder sb, ValidationType validationType, RDFFormat format) throws BridgeDBException{
    }
    
   //in WSLinksetService
    private void addFormStart(StringBuilder sb, ValidationType validationType, RDFFormat format) throws BridgeDBException{
    }
    
   //in WSLinksetService
    private void addReport(StringBuilder sb, ValidationType validationType, String report){
    }
    
    //in WSFame
    private final String HEADER_START = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" " 
            ;
    //in WSFame
    private final String TOGGLER ="<script language=\"javascript\">\n"
           ;;
            //in WSFame
    private final String HEADER_END = "	<style type=\"text/css\">"
            + "</head>\n";            
    //out
    private final String HEADER = HEADER_START + HEADER_END;
    //out
    private final String TOGGLE_HEADER = HEADER_START + TOGGLER + HEADER_END;
    //in WSFame
    private final String BODY ="<body style=\"margin: 0px\">";
    //in WSFame
    private final String TOP_LEFT ="	<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n"
            + "			<td style=\"font-size: 200%; font-weight: bold; font-family: Arial;\">\n";
    //in WSFame
    private final String TOP_RIGHT = "         </td>"
            + "	</table>";
    //in WSFame
    private final String SIDE_BAR = "	<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">"
            + "			<td style=\"border-top: 1px solid #D5D5FF; width:100%\">";
    //in WSFame
    private final String FORM_OUTPUT_FORMAT = " \n<p>Output Format:"
            + " </p>";
   //in WSLinksetService
    private final String FORM_MINE_TYPE = " \n<p>Mime Type:"
            + " </p>";
   //in WSLinksetService
    private final String FORM_INFO_START = "\n<p><textarea rows=\"15\" name=\"info\" style=\"width:100%; background-color: #EEEEFF;\">";
   //in WSLinksetService
    private final String FORM_INFO_END = "</textarea></p>";
   //in WSLinksetService
    private final String FORM_SUBMIT = " <input type=\"submit\" value=\"Validate!\"></input></form>";
   //in WSLinksetService
    private final String FORM_NOTE ="    Note: If the new page does not open click on the address and press enter</p>"
            + "</form>";
   //in WSLinksetService
    private final String URI_MAPPING_FORM = "<form method=\"get\" action=\"/QueryExpander/mapURI\">"
            + "</form>";
    //in WSFame
    private final String MAIN_END = "			</td>"
            + "	</div>";
    //in WSFame
    private final String BODY_END = "</body>"
            + "</html>";
    //in WSFame
    private final String END = MAIN_END + BODY_END;

   //in WSLinksetService
	public Response checkIpAddress(@Context HttpServletRequest hsr) throws IOException, IDMapperException {
        return null;
	}

   //in WSLinksetService
	public Response uploadFile(
        //TODO work out why the FormDataContentDisposition is null
		 @FormDataParam("file") InputStream uploadedInputStream,
         @FormDataParam("file") FormDataContentDisposition fileDetail,
         @Context HttpServletRequest hsr
       ) throws IOException {
        return null;
	}

   //in WSLinksetService
	public Response uploadFile2(
        //TODO work out why the FormDataContentDisposition is null
		 @FormDataParam("file") InputStream uploadedInputStream,
         @FormDataParam("file") FormDataContentDisposition fileDetail,
         @FormParam(MIME_TYPE)String mimeType
       ) throws IOException {
        return null;
	}
}


