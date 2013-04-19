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
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.bridgedb.linkset.rdf.RdfReader;
import org.bridgedb.statistics.DataSetInfo;
import org.bridgedb.statistics.LensInfo;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;
import org.bridgedb.ws.WsUriConstants;
import org.bridgedb.ws.bean.LensBean;

/**
 * This class adds the html versions of the SQLUrlMapper methods
 * @author Christian
 */
public class WSVoidService extends WSFame{
    
    static final Logger logger = Logger.getLogger(WSUriInterfaceService.class);

    public WSVoidService()  throws BridgeDBException   {
        super();
    }
            
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/" + WsUriConstants.GET_MAPPING_INFO)
    public Response getMappingInfo(@QueryParam(WsUriConstants.SOURCE_DATASOURCE_SYSTEM_CODE) String scrCode,
            @QueryParam(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE) String targetCode,
            @QueryParam(WsUriConstants.LENS_URI) String lensUri,
            @Context HttpServletRequest httpServletRequest) 
            throws BridgeDBException, UnsupportedEncodingException {
        List<MappingSetInfo> mappingSetInfos = uriMapper.getMappingSetInfos(scrCode, targetCode, lensUri);
        String lensName;
        if (lensUri != null && !lensUri.isEmpty()){
            LensInfo lensInfo = uriMapper.getLens(lensUri);
            lensName = lensInfo.getName();
        } else {
            lensName = "Default";
        }
        StringBuilder sb = topAndSide("IMS Mapping Summary for " + lensName + " Lens",  httpServletRequest);
        if (mappingSetInfos.isEmpty()){
            sb.append("\n<h1> No mapping found between ");
            MappingSetTableMaker.addDataSourceLink(sb, new DataSetInfo(scrCode,scrCode), httpServletRequest);
            sb.append(" and ");
            MappingSetTableMaker.addDataSourceLink(sb, new DataSetInfo(targetCode,targetCode), httpServletRequest);
            sb.append(" using lens ");
            sb.append(lensUri);
            sb.append("</h1>");
        } else {
            sb.append("\n<p>Warning summary lines are just a sum of the mappings from all mapping files.");
            sb.append("So if various sources include the same mapping it will be counted multiple times. </p>");
            sb.append("\n<p>Click on the arrows in the first column to expand or contract the table.</p>");
            MappingSetTableMaker.addTable(sb, mappingSetInfos, httpServletRequest);
        }
        footerAndEnd(sb);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/" + WsUriConstants.LENS) 
	public Response getLenses(@Context HttpServletRequest httpServletRequest) throws BridgeDBException {
		List<LensInfo> lenses = uriMapper.getLens();
        StringBuilder sb = topAndSide("Lens Summary",  httpServletRequest);
        sb.append("<table border=\"1\">");
        sb.append("<tr>");
        sb.append("<th>Name</th>");
        sb.append("<th>URI</th>");
		for (LensInfo lens:lenses) {
            sb.append("<tr><td>");
            sb.append(lens.getName());
            sb.append("</td><td><a href=\"");
            sb.append(lens.getUri());
            sb.append("\">");
            sb.append(lens.getUri());
            sb.append("</a></td>");        
		}
        sb.append("</tr></table>");
        sb.append("<p><a href=\"");
        sb.append(httpServletRequest.getContextPath());
        sb.append("/");
        sb.append(WsUriConstants.LENS);
        sb.append(WsUriConstants.XML);
        sb.append("\">");
        sb.append("XML Format");
        sb.append("</a></p>");        
        footerAndEnd(sb);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
	}
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/" + WsUriConstants.GRAPHVIZ)
    public Response graphvizDot(@QueryParam(WsUriConstants.LENS_URI) String lensUri) 
            throws BridgeDBException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        List<MappingSetInfo> rawProvenaceinfos = uriMapper.getMappingSetInfos(null, null, lensUri);
        SourceTargetCounter sourceTargetCounter = new SourceTargetCounter(rawProvenaceinfos);
        sb.append("digraph G {");
        for (MappingSetInfo info:sourceTargetCounter.getSummaryInfos()){
            if (info.getSource().compareTo(info.getTarget()) < 0 ){
                sb.append("\"");
                sb.append(info.getSource().getFullName());
                sb.append("\" -> \"");
                sb.append(info.getTarget().getFullName());
                sb.append("\" [dir = both, label=\"");
                sb.append(formatter.format(info.getNumberOfLinks()) + "(" + info.getStringId() + ")"); 
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
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/mappingSet")
    public String mappingSet() throws BridgeDBException {
        throw new BridgeDBException("Parameter id is missing");
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/mappingSet/{id}")
    public String mappingSet(@PathParam("id") String idString) throws BridgeDBException {
        if (idString == null || idString.isEmpty()){
            throw new BridgeDBException("Parameter id is missing!");
        }
        Integer id = Integer.parseInt(idString);
        return new RdfReader(StoreType.LIVE).getLinksetRDF(id);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/linkset")
    public String linkset() throws BridgeDBException {
        throw new BridgeDBException("Parameter id is missing");
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/linkset/{id}")
    public String linksetSet(@PathParam("id") String idString) throws BridgeDBException {
        if (idString == null || idString.isEmpty()){
            throw new BridgeDBException("Parameter id is missing!");
        }
        Integer id = Integer.parseInt(idString);
        return new RdfReader(StoreType.LIVE).getLinksetRDF(id);
    }
    
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/linkset/{id}/{resource}")
    public String linksetSet(@PathParam("id") String idString, @PathParam("resource") String resource) throws BridgeDBException {
        if (idString == null || idString.isEmpty()){
            throw new BridgeDBException("Parameter id is missing!");
        }
        Integer id = Integer.parseInt(idString);
        return new RdfReader(StoreType.LIVE).getLinksetRDF(id);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/void")
    public String voidInfo() throws BridgeDBException {
        throw new BridgeDBException("Parameter id is missing");
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/void/{id}")
    public String voidInfo(@PathParam("id") String idString) throws BridgeDBException {
        if (idString == null || idString.isEmpty()){
            throw new BridgeDBException("Parameter id is missing");
        }
        Integer id = Integer.parseInt(idString);
        return new RdfReader(StoreType.LIVE).getVoidRDF(id);
    }

}


