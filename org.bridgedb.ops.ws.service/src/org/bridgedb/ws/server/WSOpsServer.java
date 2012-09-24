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
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.mysql.MySQLSpecific;
import org.bridgedb.rdf.RdfReader;
import org.bridgedb.rdf.RdfStoreType;
import org.bridgedb.rdf.RdfWrapper;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.url.URLMapping;
import org.bridgedb.utils.Reporter;
import org.bridgedb.ws.WSOpsService;

/**
 *
 * @author Christian
 */
public class WSOpsServer extends WSOpsService implements Comparator<MappingSetInfo>{
    
    private NumberFormat formatter;
    
    public WSOpsServer()  throws IDMapperException   {
        SQLAccess sqlAccess = SqlFactory.createSQLAccess();
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
            sb.append(RdfWrapper.getTheBaseURI());
            sb.append("dataSource/");
            sb.append(info.getSourceSysCode());
            sb.append("\">");
            sb.append(info.getSourceSysCode());
            sb.append("</a></td>");
            sb.append("<td><a href=\"");
            sb.append(RdfWrapper.getTheBaseURI());
            sb.append("dataSource/");
            sb.append(info.getTargetSysCode());
            sb.append("\">");
            sb.append(info.getTargetSysCode());
            sb.append("</a></td>");
            sb.append("<td align=\"right\">");
            sb.append(formatter.format(info.getNumberOfLinks()));
            sb.append("</td>");
            sb.append("<td><a href=\"");
            sb.append(RdfWrapper.getTheBaseURI());
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
            sb.append(RdfWrapper.getTheBaseURI());
            sb.append("dataSource/");
            sb.append(info.getSourceSysCode());
            sb.append("\">");
            sb.append(info.getSourceSysCode());
            sb.append("</a></td>");
            sb.append("<td><a href=\"");
            sb.append(RdfWrapper.getTheBaseURI());
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
            throw new IDMapperException("Parameter id is missing");
        }
        Integer id = Integer.parseInt(idString);
        return new RdfReader(RdfStoreType.MAIN).getRDF(id);
    }


}


