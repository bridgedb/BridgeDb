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
package org.bridgedb.ws;


import org.bridgedb.ws.server.*;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.bridgedb.IDMapperException;
import org.bridgedb.rdf.RdfConfig;
import org.bridgedb.rdf.RdfReader;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;

/**
 * This class adds the html versions of the SQLUrlMapper methods
 * @author Christian
 */
public class WSUrlService extends WSFame implements Comparator<MappingSetInfo>{
    
    protected NumberFormat formatter;
        
    static final Logger logger = Logger.getLogger(WSOpsInterfaceService.class);

    public WSUrlService()  throws IDMapperException   {
        super();
        formatter = NumberFormat.getInstance();
        if (formatter instanceof DecimalFormat) {
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setGroupingSeparator(',');
            ((DecimalFormat) formatter).setDecimalFormatSymbols(dfs);
        }
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
        sb.append("\n<p>Warning there many not be Distint mappings but just a sum of the mappings from all mapping files.");
        sb.append("So if various sources include the same mapping it will be counted multiple times. </p>");
        sb.append("\n<p>");
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
        sb.append("\n<p>Warning there many not be Distint mappings but just a sum of the mappings from all mapping files.");
        sb.append("So if various sources include the same mapping it will be counted multiple times. </p>");
        sb.append("\n<p>");
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
        throw new BridgeDBException("Parameter id is missing");
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/mappingSet/{id}")
    public String mappingSet(@PathParam("id") String idString) throws IDMapperException {
        if (idString == null || idString.isEmpty()){
            throw new BridgeDBException("Parameter id is missing!");
        }
        Integer id = Integer.parseInt(idString);
        return new RdfReader(StoreType.LIVE).getLinksetRDF(id);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/linkset")
    public String linkset() throws IDMapperException {
        throw new BridgeDBException("Parameter id is missing");
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/linkset/{id}")
    public String linksetSet(@PathParam("id") String idString) throws IDMapperException {
        if (idString == null || idString.isEmpty()){
            throw new BridgeDBException("Parameter id is missing!");
        }
        Integer id = Integer.parseInt(idString);
        return new RdfReader(StoreType.LIVE).getLinksetRDF(id);
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/linkset/{id}/{resource}")
    public String linksetSet(@PathParam("id") String idString, @PathParam("resource") String resource) throws IDMapperException {
        throw new BridgeDBException("id= "+ idString + " resource = " + resource);
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
        throw new BridgeDBException("Parameter id is missing");
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/void/{id}")
    public String voidInfo(@PathParam("id") String idString) throws IDMapperException {
        if (idString == null || idString.isEmpty()){
            throw new BridgeDBException("Parameter id is missing");
        }
        Integer id = Integer.parseInt(idString);
        return new RdfReader(StoreType.LIVE).getVoidRDF(id);
    }

}


