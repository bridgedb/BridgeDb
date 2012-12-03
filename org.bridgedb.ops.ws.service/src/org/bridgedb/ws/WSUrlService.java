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


import java.io.UnsupportedEncodingException;
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
import org.bridgedb.IDMapperException;
import org.bridgedb.rdf.RdfReader;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;
import org.bridgedb.ws.server.SourceTargetCounter;

/**
 * This class adds the html versions of the SQLUrlMapper methods
 * @author Christian
 */
public class WSUrlService extends WSFame{
    
    static final Logger logger = Logger.getLogger(WSOpsInterfaceService.class);

    public WSUrlService()  throws IDMapperException   {
        super();
    }
            
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/getMappingInfo")
    public Response getMappingInfo(@QueryParam(WsOpsConstants.SOURCE_DATASOURCE_SYSTEM_CODE) String scrCode,
            @QueryParam(WsOpsConstants.TARGET_DATASOURCE_SYSTEM_CODE) String targetCode,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
        List<MappingSetInfo> mappingSetInfos = urlMapper.getMappingSetInfos(scrCode, targetCode);
        StringBuilder sb = topAndSide("IMS Mapping Service",  httpServletRequest);
        sb.append("\n<p>Warning summary lines are just a sum of the mappings from all mapping files.");
        sb.append("So if various sources include the same mapping it will be counted multiple times. </p>");
        MappingSetTableMaker.addTable(sb, mappingSetInfos);
        sb.append("</body></html>");
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/" + WsOpsConstants.GRAPHVIZ)
    public Response graphvizDot() throws IDMapperException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        List<MappingSetInfo> rawProvenaceinfos = urlMapper.getMappingSetInfos(null, null);
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


