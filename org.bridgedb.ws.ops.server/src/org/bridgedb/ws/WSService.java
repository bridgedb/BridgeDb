package org.bridgedb.ws;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.bridgedb.ws.bean.OverallStatisticsBean;
import org.bridgedb.ws.bean.ProvenanceBean;
import org.bridgedb.ws.bean.URLBean;
import org.bridgedb.ws.bean.XrefBean;
import java.util.List;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.ops.OpsMapper;
import org.bridgedb.ops.ProvenanceInfo;
import org.bridgedb.result.URLMapping;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.ws.bean.OverallStatisticsBeanFactory;
import org.bridgedb.ws.bean.ProvenanceFactory;
import org.bridgedb.ws.bean.URLMappingBean;
import org.bridgedb.ws.bean.URLMappingBeanFactory;
import org.bridgedb.ws.bean.XrefBeanFactory;

@Path("/")
public class WSService extends WSCoreService implements WSInterface {

    protected OpsMapper opsMapper;
    
    /**
     * Defuault constuctor for super classes.
     * 
     * Super classes will have the responsibilites of setting up the idMapper.
     */
    protected WSService(){
    }
    
    public WSService(IDMapper idMapper) {
        super(idMapper);
        this.opsMapper = (OpsMapper)idMapper;    
    }
    
    @Context 
    public UriInfo uriInfo;

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getMappings") 
    public List<URLMappingBean> getMappings(
            @QueryParam("URL") List<String> URLs,
            @QueryParam("sourceURL") List<String> sourceURLs,            
            @QueryParam("targetURL") List<String> targetURLs,            
            @QueryParam("nameSpace") List<String> nameSpaces,
            @QueryParam("sourceNameSpace") List<String> sourceNameSpaces,
            @QueryParam("targetNameSpace") List<String> targetNameSpaces,
            @QueryParam("provenanceId") List<String> provenanceIds,
            @DefaultValue("0") @QueryParam("position") String positionString, 
            @DefaultValue("100") @QueryParam("limit") String limitString,
            @DefaultValue("false") @QueryParam("full") Boolean full) {
        if (provenanceMapper == null){
            throw new UnsupportedOperationException("Underlying IDMapper does not support URLMapperProvenance.");
        }
        List<URLMapping> mappings = new ArrayList<URLMapping>();
        boolean ok = true;;
        Integer position = null;
        try {
            position = new Integer(positionString);
        } catch (NumberFormatException ex){
            mappings.add(new URLMapping("Illegal non integer position " + positionString));   
            ok = false;
        }
        Integer limit = null;
        if (limitString != null){
            try {
                limit = new Integer(limitString);
            } catch (NumberFormatException ex){
                mappings.add(new URLMapping("Illegal non integer limit " + limitString));                  
                ok = false;
            }
        }       
        if (ok){
            mappings.addAll(opsMapper.getMappings(URLs, sourceURLs, targetURLs, 
                    nameSpaces, sourceNameSpaces, targetNameSpaces, provenanceIds, position, limit));
        }
        if (mappings.isEmpty()){
            StringBuilder parameters = new StringBuilder();
            for (String URL:URLs){
               parameters.append(" URL="); 
               parameters.append(URL);
            }
            for (String sourceURL:sourceURLs){
               parameters.append(" sourceURL="); 
               parameters.append(sourceURL);
            }
            for (String targetURL:targetURLs){
               parameters.append(" targetURL="); 
               parameters.append(targetURL);
            }
            for (String nameSpace:nameSpaces){
               parameters.append(" nameSpace="); 
               parameters.append(nameSpace);
            }
            for (String sourceNameSpace:sourceNameSpaces){
               parameters.append(" sourceNameSpace="); 
               parameters.append(sourceNameSpace);
            }
            for (String targetNameSpace:targetNameSpaces){
               parameters.append(" targetNameSpace="); 
               parameters.append(targetNameSpace);
            }
            for (String provenanceId:provenanceIds){
               parameters.append(" provenanceId="); 
               parameters.append(provenanceId);
            }
            if (position != null){
               parameters.append(" position="); 
               parameters.append(position);         
            }
            if (limit != null){
               parameters.append(" limit="); 
               parameters.append(limit);         
            }
            if (full != null){
               parameters.append(" full="); 
               parameters.append(full);         
            }
            mappings.add(new URLMapping("No mappings found for :" + parameters.toString()));  
        }
        List<URLMappingBean> beans = new ArrayList<URLMappingBean>();
        for (URLMapping mapping:mappings){
            URLMappingBean bean = URLMappingBeanFactory.asBean(mapping, full);
            beans.add(bean);
        }
        return beans;
    }
    
    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getMapping/{id}")
    public URLMappingBean getMapping(@PathParam("id") String idString) {
        Integer id = Integer.parseInt(idString);
        URLMapping mapping = opsMapper.getMapping(id);
        return URLMappingBeanFactory.asBean(mapping, true);
    }

    /* Removed due to scale issues
     * @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getXrefs")
    public List<XrefBean> getXrefs(
            @QueryParam("dataSourceSysCode") ArrayList<String> dataSourceSysCodes, 
            @QueryParam("provenanceId")  List<String> provenanceIds, 
            @DefaultValue("0") @QueryParam("position") String positionString, 
            @DefaultValue("100") @QueryParam("limit") String limitString) throws IDMapperException{
        ArrayList<DataSource> dataSources = new ArrayList<DataSource>();
        for (String dataCode: dataSourceSysCodes){
            dataSources.add(DataSource.getBySystemCode(dataCode));
        }
        Integer position = new Integer(positionString);
        Integer limit = new Integer(limitString);
        List<Xref> xrefs = opsMapper.getXrefs(dataSources, provenanceIds, position, limit);
        List<XrefBean> beans = new ArrayList<XrefBean>();
        for (Xref xref:xrefs){
            beans.add(XrefBeanFactory.asBean(xref));
        }
        return beans;
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getURLs")
    public List<URLBean> getURLs(
            @QueryParam("nameSpace")List<String> nameSpaces,             
            @QueryParam("provenanceId")  List<String> provenanceIds, 
            @DefaultValue("0") @QueryParam("position") String positionString, 
            @DefaultValue("100") @QueryParam("limit") String limitString) throws IDMapperException{
        Integer position = new Integer(positionString);
        Integer limit = new Integer(limitString);
        List<String> URLs = opsMapper.getURLs(nameSpaces, provenanceIds, position, limit);
        List<URLBean> beans = new ArrayList<URLBean>();
        for (String URL:URLs){
            URLBean bean = new URLBean();
            bean.setURL(URL);
            beans.add(bean);
        }
        return beans;
    }*/

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getOverallStatistics") 
    public OverallStatisticsBean getOverallStatistics() throws IDMapperException {
        OverallStatistics overallStatistics = opsMapper.getOverallStatistics();
        return OverallStatisticsBeanFactory.asBean(overallStatistics);
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getProvenanceInfos") 
    public List<ProvenanceBean> getProvenanceInfos() throws IDMapperException {
        List<ProvenanceInfo> infos = opsMapper.getProvenanceInfos();
        ArrayList<ProvenanceBean> beans = new ArrayList<ProvenanceBean>();
        for (ProvenanceInfo info:infos){
            beans.add(ProvenanceFactory.asBean(info));
        }
        return beans;
    }
    
    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getProvenanceInfo/{id}")
    public ProvenanceBean getProvenanceInfo(@PathParam("id") String idString) throws IDMapperException{
        ProvenanceInfo info = opsMapper.getProvenanceInfo(idString);
        return ProvenanceFactory.asBean(info);
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getSampleSourceURLs") 
    public List<URLBean> getSampleSourceURLs() throws IDMapperException {
        List<String> URLs = opsMapper.getSampleSourceURLs();
        List<URLBean> beans = new ArrayList<URLBean>();
        for (String URL:URLs){
            URLBean bean = new URLBean();
            bean.setURL(URL);
            beans.add(bean);
        }
        return beans;
    }

}
