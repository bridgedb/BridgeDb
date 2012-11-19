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
import org.bridgedb.rdf.RdfFactory;
import org.bridgedb.rdf.StatementReader;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.url.URLMapping;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.IpConfig;
import org.bridgedb.utils.Reporter;
import org.bridgedb.utils.StoreType;
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
        sb.append("<head><title>OPS IMS</title></head><body>");
        sb.append("<h1>Open PHACTS Identity Mapping Service</h1>");
        sb.append("\n<p>Welcome to the prototype Identity Mapping Service. </p>");
       
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
        sb.append("<a href=\"/OPS-IMS/getMappingInfo\">Mapping Info Page</a></p>");
        
        sb.append("\n<p>The Main OPS method is <a href=\"/OPS-IMS/api/#mapByURLs\">mapByURLs</a></dt>");
        sb.append("<dd>List the URLs that map to this URL</dd>");
        sb.append("\n<p><a href=\"/OPS-IMS/api\">API Page</a></p>");
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
    
}


