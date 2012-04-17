package org.bridgedb.ws;

import java.util.ArrayList;
import org.bridgedb.ws.bean.DataSourceStatisticsBean;
import org.bridgedb.ws.bean.ProvenanceStatisticsBean;
import org.bridgedb.ws.bean.URLsBean;
import org.bridgedb.ws.bean.XrefBean;
import java.util.List;
import java.util.Set;
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
import org.bridgedb.iterator.URLByPosition;
import org.bridgedb.iterator.XrefByPosition;
import org.bridgedb.provenance.ProvenanceMapper;
import org.bridgedb.result.URLMapping;
import org.bridgedb.sql.FullMapper;
import org.bridgedb.statistics.ProvenanceStatistics;
import org.bridgedb.ws.bean.URLMappingBean;
import org.bridgedb.ws.bean.URLMappingBeanFactory;
import org.bridgedb.ws.bean.XrefBeanFactory;

@Path("/")
public class WSService extends WSCoreService implements WSInterface {

    protected XrefByPosition byXrefPosition;
    protected URLByPosition byURLPosition;
    protected FullMapper fullMapper;
    
    /**
     * Defuault constuctor for super classes.
     * 
     * Super classes will have the responsibilites of setting up the idMapper.
     */
    protected WSService(){
    }
    
    public WSService(IDMapper idMapper) {
        super(idMapper);
        if (idMapper instanceof XrefByPosition){
            this.byXrefPosition = (XrefByPosition)idMapper;
        } else {
            this.byXrefPosition = null;
        }
        if (idMapper instanceof URLByPosition){
            this.byURLPosition = (URLByPosition)idMapper;
        } else {
            this.byURLPosition = null;
        }
        if (idMapper instanceof FullMapper){
            this.fullMapper = (FullMapper)idMapper;
        } else {
            this.fullMapper = null;
        }
        
    }
    
    @Context 
    public UriInfo uriInfo;

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getXrefByPosition")
    public List<XrefBean> getXrefByPosition(
            @QueryParam("code") String code, 
            @QueryParam("position") Integer position, 
            @QueryParam("limit") Integer limit) throws IDMapperException {
        if (this.byXrefPosition == null) {
            throw new UnsupportedOperationException("Underlying IDMapper does not support getXrefByPosition.");
        }
        if (position == null) throw new IDMapperException ("\"position\" parameter can not be null");
        if (code == null){
            if (limit == null){
                Xref xref = byXrefPosition.getXrefByPosition(position);
                return xrefToListXrefBeans(xref);
            } else {
                Set<Xref> xrefs = byXrefPosition.getXrefByPosition(position, limit);
                return setXrefToListXrefBeans(xrefs);
            }
        } else {
            DataSource dataSource = DataSource.getBySystemCode(code);
            if (limit == null){
                Xref xref = byXrefPosition.getXrefByPosition(dataSource, position);
                return xrefToListXrefBeans(xref);
            } else {
                Set<Xref> xrefs = byXrefPosition.getXrefByPosition(dataSource, position, limit);
                return setXrefToListXrefBeans(xrefs);
            }            
        }
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getURLByPosition")
    public URLsBean getURLByPosition( 
            @QueryParam("nameSpace") String nameSpace, 
            @QueryParam("position") Integer position, 
            @QueryParam("limit") Integer limit) throws IDMapperException {
         if (this.byURLPosition == null) {
            throw new UnsupportedOperationException("Underlying IDMapper does not support getURLByPosition.");
        }
        if (position == null) throw new IDMapperException ("\"position\" parameter can not be null");
        if (nameSpace == null){
            if (limit == null){
                String url = byURLPosition.getURLByPosition(position);
                return new URLsBean(url);
            } else {
                Set<String> urls = byURLPosition.getURLByPosition(position, limit);
                return new URLsBean(urls);
            }
        } else {
            if (limit == null){
                String url = byURLPosition.getURLByPosition(nameSpace, position);
                return new URLsBean(url);
            } else {
                Set<String> urls = byURLPosition.getURLByPosition(nameSpace, position, limit);
                return new URLsBean(urls);
            }
        }
    }

    private List<XrefBean> xrefToListXrefBeans(Xref xref){
        ArrayList<XrefBean> results = new ArrayList<XrefBean>();
        results.add(XrefBeanFactory.asBean(xref));
        return results;        
    }
    
    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getMappings") 
    public List<URLMappingBean> getMappings(
            @QueryParam("id") List<String> idStrings,
            @QueryParam("URL") List<String> URLs,
            @QueryParam("sourceURL") List<String> sourceURLs,            
            @QueryParam("targetURL") List<String> targetURLs,            
            @QueryParam("nameSpace") List<String> nameSpaces,
            @QueryParam("sourceNameSpace") List<String> sourceNameSpaces,
            @QueryParam("targetNameSpace") List<String> targetNameSpaces,
            @QueryParam("provenanceId") List<String> provenanceIds,
            @QueryParam("position") String positionString, 
            @DefaultValue("100") @QueryParam("limit") String limitString,
            @DefaultValue("false") @QueryParam("full") Boolean full) {
        if (provenanceMapper == null){
            throw new UnsupportedOperationException("Underlying IDMapper does not support URLMapperProvenance.");
        }
        List<URLMapping> mappings = new ArrayList<URLMapping>();
        for (String idString:idStrings ) {
            try {
                Integer id = Integer.parseInt(idString);
                mappings.add(fullMapper.getMapping(id));        
            } catch (NumberFormatException ex){
                mappings.add(new URLMapping("Illegal non integer id " + idString));                  
            }
        }
        boolean ok = true;;
        Integer position = null;
        if (positionString != null){
            try {
                position = new Integer(positionString);
            } catch (NumberFormatException ex){
                mappings.add(new URLMapping("Illegal non integer position " + positionString));                  
            }
        }
        Integer limit = null;
        if (limitString != null){
            try {
                limit = new Integer(limitString);
            } catch (NumberFormatException ex){
                mappings.add(new URLMapping("Illegal non integer limit " + limitString));                  
            }
        }       
        mappings.addAll(fullMapper.getMappings(URLs, sourceURLs, targetURLs, 
                nameSpaces, sourceNameSpaces, targetNameSpaces, provenanceIds, position, limit));
        if (mappings.isEmpty()){
            StringBuilder parameters = new StringBuilder();
            for (String id:idStrings){
               parameters.append(" id="); 
               parameters.append(id);
            }
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
    
    /*
    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getMapping/{id}") 
    public URLMappingBeanImpl getMapping(
            @PathParam("id") Integer id)  {
        if (provenanceMapper == null){
            throw new UnsupportedOperationException("Underlying IDMapper does not support URLMapperProvenance.");
        }
        if (id == null) return new URLMappingBeanImpl("id parameter missig");
        URLMapping mapping = provenanceMapper.getMapping(id);
        return new URLMappingBeanImpl(mapping);
    }

    
    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getURLMappings")
    public List<URLMappingBean> getURLMappings(
            @QueryParam("URL") String URL,
            @QueryParam("tgtNameSpace") List<String> tgtNameSpace) throws IDMapperException {
        if (URL == null) throw new IDMapperException ("\"URL\" parameter missing");
        if (URL.isEmpty()) throw new IDMapperException ("\"URL\" parameter missing");            
        Set<URLMapping> mappings;
        if (tgtNameSpace == null ){
            mappings = provenanceMapper.getURLMappings(URL);
        } else {
            mappings = provenanceMapper.getURLMappings(URL, tgtNameSpace.toArray(new String[0]));
        }
        List<URLMappingBean> beans = new ArrayList<URLMappingBean>();
        for (URLMapping mapping:mappings){
            URLMappingBeanImpl bean = new URLMappingBeanImpl(mapping);
            beans.add(bean);
        }
        return beans;
    }
    */
    
    /*
      @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getProvenance/{id}")
    public ProvenanceStatisticsBean getProvenance(
            @PathParam("id") Integer id) throws IDMapperException {
        if (provenanceMapper == null){
            throw new UnsupportedOperationException("Underlying IDMapper does not support URLMapperProvenance.");
        }
       if (id == null) throw new IDMapperException("id parameter missig");
        ProvenanceStatistics stats = provenanceMapper.getProvenanceLink(id);
        ProvenanceStatisticsBean result = new ProvenanceStatisticsBean(stats);
        return result;
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getProvenanceByPosition")
    public ProvenanceStatisticsBean getProvenanceByPosition(
            @QueryParam("position") Integer position) throws IDMapperException {
        if (provenanceMapper == null){
            throw new UnsupportedOperationException("Underlying IDMapper does not support URLMapperProvenance.");
        }
        if (position == null) throw new IDMapperException("position parameter missig");
        ProvenanceStatistics stats = provenanceMapper.getProvenanceByPosition(position);
        ProvenanceStatisticsBean result = new ProvenanceStatisticsBean(stats);
        return result;
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getProvenancesByPosition")
    public List<ProvenanceStatisticsBean> getProvenanceByPosition(
            @QueryParam("position") Integer  position, 
            @QueryParam("limit") Integer limit) throws IDMapperException {
        if (provenanceMapper == null){
            throw new UnsupportedOperationException("Underlying IDMapper does not support URLMapperProvenance.");
        }
        if (position == null) throw new IDMapperException("position parameter missig");
        if (limit == null) throw new IDMapperException("limit parameter missig");
        List<ProvenanceStatistics> stats = provenanceMapper.getProvenanceByPosition(position, limit);
        List<ProvenanceStatisticsBean> beans = new ArrayList<ProvenanceStatisticsBean>();
        for (ProvenanceStatistics stat:stats){
            beans.add(new ProvenanceStatisticsBean(stat));
        }
        return beans;
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getSourceProvenanceByNameSpace")
    public List<ProvenanceStatisticsBean> getSourceProvenanceByNameSpace(
            @QueryParam("nameSpace") String nameSpace) throws IDMapperException {
        if (provenanceMapper == null){
            throw new UnsupportedOperationException("Underlying IDMapper does not support URLMapperProvenance.");
        }
        if (nameSpace == null || nameSpace.isEmpty()) throw new IDMapperException("nameSpace parameter missig");
        Set<ProvenanceStatistics> stats = provenanceMapper.getSourceProvenanceByNameSpace(nameSpace);
        List<ProvenanceStatisticsBean> beans = new ArrayList<ProvenanceStatisticsBean>();
        for (ProvenanceStatistics stat:stats){
            beans.add(new ProvenanceStatisticsBean(stat));
        }
        return beans;
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getTargetProvenanceByNameSpace")
    public List<ProvenanceStatisticsBean> getTargetProvenanceByNameSpace(
            @QueryParam("nameSpace") String nameSpace) throws IDMapperException {
        if (provenanceMapper == null){
            throw new UnsupportedOperationException("Underlying IDMapper does not support URLMapperProvenance.");
        }
        if (nameSpace == null || nameSpace.isEmpty()) throw new IDMapperException("nameSpace parameter missig");
        Set<ProvenanceStatistics> stats = provenanceMapper.getTargetProvenanceByNameSpace(nameSpace);
        List<ProvenanceStatisticsBean> beans = new ArrayList<ProvenanceStatisticsBean>();
        for (ProvenanceStatistics stat:stats){
            beans.add(new ProvenanceStatisticsBean(stat));
        }
        return beans;
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getDataSourceStatistics")
    public DataSourceStatisticsBean getDataSourceStatistics(
            @QueryParam("code") String code) throws IDMapperException {
        if (provenanceMapper == null){
            throw new UnsupportedOperationException("Underlying IDMapper does not support URLMapperProvenance.");
        }
        if (code == null || code.isEmpty()) throw new IDMapperException("code parameter missig");
        DataSource dataSource = DataSource.getBySystemCode(code);
        DataSourceStatistics stats = provenanceMapper.getDataSourceStatistics(dataSource);
        return new DataSourceStatisticsBean(stats);
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getDataSourceStatisticsByAPosition")
    public DataSourceStatisticsBean getDataSourceStatisticsByPosition(
            @QueryParam("position") Integer position) throws IDMapperException {
        if (provenanceMapper == null){
            throw new UnsupportedOperationException("Underlying IDMapper does not support URLMapperProvenance.");
        }
        if (position == null) throw new IDMapperException("position parameter missig");
        DataSourceStatistics stats = provenanceMapper.getDataSourceStatisticsByPosition(position);             
        return new DataSourceStatisticsBean(stats);
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getDataSourceStatisticsByPosition")
    public List<DataSourceStatisticsBean> getDataSourceStatisticsByPosition(
            @QueryParam("position") Integer position, 
            @QueryParam("limit") Integer limit) throws IDMapperException {
        if (provenanceMapper == null){
            throw new UnsupportedOperationException("Underlying IDMapper does not support URLMapperProvenance.");
        }
        if (position == null) throw new IDMapperException("position parameter missig");
        if (limit == null) throw new IDMapperException("limit parameter missig");
        List<DataSourceStatistics> stats = provenanceMapper.getDataSourceStatisticsByPosition(position, limit);
        List<DataSourceStatisticsBean> beans = new ArrayList<DataSourceStatisticsBean>();
        for (DataSourceStatistics stat:stats){
            beans.add(new DataSourceStatisticsBean(stat));
        }
        return beans;
    }
*/

    @Override
    public ProvenanceStatisticsBean getProvenance(Integer id) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ProvenanceStatisticsBean getProvenanceByPosition(Integer position) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ProvenanceStatisticsBean> getProvenanceByPosition(Integer position, Integer limit) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ProvenanceStatisticsBean> getSourceProvenanceByNameSpace(String nameSpace) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ProvenanceStatisticsBean> getTargetProvenanceByNameSpace(String nameSpace) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DataSourceStatisticsBean getDataSourceStatistics(String code) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DataSourceStatisticsBean getDataSourceStatisticsByPosition(Integer position) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<DataSourceStatisticsBean> getDataSourceStatisticsByPosition(Integer position, Integer limit) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
