package org.bridgedb.ws;

import org.bridgedb.ws.bean.ProvenanceBean;
import org.bridgedb.ws.bean.URLExistsBean;
import org.bridgedb.ws.bean.XrefBean;
import org.bridgedb.ws.bean.XrefMapBean;
import org.bridgedb.ws.bean.CapabilitiesBean;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.provenance.ProvenanceLink;
import org.bridgedb.provenance.ProvenanceMapper;
import org.bridgedb.provenance.WrappedProvenanceMapper;
import org.bridgedb.provenance.XrefProvenance;
import org.bridgedb.result.URLMapping;
import org.bridgedb.url.URLMapper;
import org.bridgedb.url.WrapperURLMapper;
import org.bridgedb.ws.bean.DataSourceBean;
import org.bridgedb.ws.bean.DataSourceBeanFactory;
import org.bridgedb.ws.bean.FreeSearchSupportedBean;
import org.bridgedb.ws.bean.MappingSupportedBean;
import org.bridgedb.ws.bean.MappingSupportedBeanFactory;
import org.bridgedb.ws.bean.PropertyBean;
import org.bridgedb.ws.bean.URLExistsBeanFactory;
import org.bridgedb.ws.bean.URLMappingBean;
import org.bridgedb.ws.bean.URLMappingBeanFactory;
import org.bridgedb.ws.bean.URLSearchBean;
import org.bridgedb.ws.bean.XrefMapBeanFactory;
import org.bridgedb.ws.bean.XrefBeanFactory;
import org.bridgedb.ws.bean.XrefExistsBean;
import org.bridgedb.ws.bean.XrefExistsBeanFactory;
import org.bridgedb.ws.bean.XrefProvenanceBean;
import org.bridgedb.ws.bean.XrefProvenanceBeanFactory;

@Path("/")
public class WSCoreService implements WSCoreInterface {

    protected IDMapper idMapper;
    protected URLMapper urlMapper;
    protected ProvenanceMapper provenanceMapper;

    /**
     * Defuault constuctor for super classes.
     * 
     * Super classes will have the responsibilites of setting up the idMapper.
     */
    protected WSCoreService(){
    }
    
    public WSCoreService(IDMapper idMapper) {
        this.idMapper = idMapper;
        if (idMapper instanceof URLMapper){
            urlMapper = (URLMapper)idMapper;
        } else {
            urlMapper = new WrapperURLMapper(idMapper);
        }
        if (idMapper instanceof ProvenanceMapper){
            provenanceMapper = (ProvenanceMapper)idMapper;
        } else {
            provenanceMapper = new WrappedProvenanceMapper(idMapper);
        }
    }
    
    public WSCoreService(IDMapper idMapper, String predicate) {
        this.idMapper = idMapper;
        if (idMapper instanceof URLMapper){
            urlMapper = (URLMapper)idMapper;
        } else {
            urlMapper = new WrapperURLMapper(idMapper);
        }
        if (idMapper instanceof ProvenanceMapper){
            provenanceMapper = (ProvenanceMapper)idMapper;
        } else {
            provenanceMapper = new WrappedProvenanceMapper(idMapper, predicate);
        }
    }
    
    @Context 
    public UriInfo uriInfo;

    // ***** URLMapper Supporting Methods ****
    
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/mapByURLs")
    @Override
    public List<URLMappingBean> mapByURLs(
            @QueryParam("srcURL") List<String> srcURL,
            @QueryParam("provenaceId") List<String> provenaceId, 
            @QueryParam("tgtNameSpace") List<String> targetNameSpace) throws IDMapperException {
        if (srcURL == null) throw new IDMapperException("srcURL parameter missig");
        if (srcURL.isEmpty()) throw new IDMapperException("srcURL parameter missig");
        Set<URLMapping> mappings = provenanceMapper.mapURL(srcURL, provenaceId, targetNameSpace);
        System.out.println(provenaceId);
        ArrayList<URLMappingBean> results = new ArrayList<URLMappingBean>();
        for (URLMapping mapping: mappings){
            results.add(URLMappingBeanFactory.asBean(mapping, false));
        }
        return results;
    }

/*    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/mapByURL")
    @Override
    public URLMappingBean mapByURL(
            @QueryParam("srcURL") String srcURL, 
            @QueryParam("provenaceId") List<String> provenaceId, 
            @QueryParam("tgtNameSpace") List<String> tgtNameSpace) throws IDMapperException {
        if (srcURL == null) throw new IDMapperException("srcURL parameter missig");
        if (srcURL.isEmpty()) throw new IDMapperException("srcURL parameter missig");
        Set<String> mappings;
        if ( tgtNameSpace!=null){
            mappings = urlMapper.mapURL(srcURL, tgtNameSpace.toArray(new String[0]));
        } else {
            mappings = urlMapper.mapURL(srcURL);
        }
        return new URLMapBean(srcURL, mappings);
    }
*/
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/URLExists")
    @Override
    public URLExistsBean urlExists( 
            @QueryParam("URL") String URL) throws IDMapperException {
        if (URL == null) throw new IDMapperException ("\"URL\" parameter missing");
        if (URL.isEmpty()) throw new IDMapperException ("\"URL\" parameter missing");            
        return URLExistsBeanFactory.asBean(URL, urlMapper.uriExists(URL));
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/URLSearch")
    @Override
    public URLSearchBean URLSearch(
            @QueryParam("text") String text, 
            @QueryParam("limit") Integer limit) throws IDMapperException {
        if (text == null) throw new IDMapperException("text parameter missig");
        Set<String> mappings;
        if (limit == null){
            mappings = urlMapper.urlSearch(text, Integer.MAX_VALUE);
        } else {
            mappings = urlMapper.urlSearch(text,limit);
        }
        return new URLSearchBean(text, mappings);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getSupportedSrcDataSources")
    @Override
    public List<DataSourceBean> getSupportedSrcDataSources() throws IDMapperException {
        ArrayList<DataSourceBean> sources = new ArrayList<DataSourceBean>();
        System.err.println(idMapper);
        IDMapperCapabilities capabilities = idMapper.getCapabilities();
        Set<DataSource> dataSources = capabilities.getSupportedSrcDataSources();
        for (DataSource dataSource:dataSources){
            DataSourceBean bean = DataSourceBeanFactory.asBean(dataSource);
            sources.add(bean);
        }
        return sources;
    } 

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getDataSource/{code}")
    @Override
    public DataSourceBean getDataSoucre(@PathParam("code") String code) throws IDMapperException {
        DataSource dataSource = DataSource.getBySystemCode(code);
        DataSourceBean bean = DataSourceBeanFactory.asBean(dataSource);
        return bean;
    } 

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/freeSearch")
    @Override
    public List<XrefBean> freeSearch(
            @QueryParam("text") String text,
            @QueryParam("limit") Integer limit) throws IDMapperException {
        if (text == null) throw new IDMapperException("text parameter missig");
        if (limit == null){
            Set<Xref> mappings = idMapper.freeSearch(text, Integer.MAX_VALUE);
            return setXrefToListXrefBeans(mappings);
        } else {
            Set<Xref> mappings = idMapper.freeSearch(text,limit);
            return setXrefToListXrefBeans(mappings);
        }
    } 

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/mapByXRef")
    @Override
    public List<XrefProvenanceBean> mapByXref(
            @QueryParam("id") String id,
            @QueryParam("code") String scrCode,
            @QueryParam("provenance") List<String> provenaceIds,
            @QueryParam("tgtCode") List<String> targetCodes
            ) throws IDMapperException {
        if (id == null) throw new IDMapperException("id parameter missig");
        if (scrCode == null) throw new IDMapperException("code parameter missig");
        DataSource dataSource = DataSource.getBySystemCode(scrCode);
        Xref source = new Xref(id, dataSource);
        ArrayList<DataSource> targetDataSources = new ArrayList<DataSource>();
        for (String targetCode: targetCodes){
             targetDataSources.add(DataSource.getBySystemCode(targetCode));
        }
        Set<XrefProvenance> mappings = provenanceMapper.mapIDProvenance(source, provenaceIds, targetDataSources);
        return setXrefProvenanceToListXrefProvenanceBeans(mappings);
    } 

    protected List<XrefProvenanceBean> setXrefProvenanceToListXrefProvenanceBeans(Set<XrefProvenance> xrefProvenances){
       ArrayList<XrefProvenanceBean> results = new ArrayList<XrefProvenanceBean>();
        for (XrefProvenance xrefProvenance:xrefProvenances){
           results.add(XrefProvenanceBeanFactory.asBean(xrefProvenance));
        }
        return results;        
    }
    
    protected List<XrefBean> setXrefToListXrefBeans(Set<Xref> xrefs){
       ArrayList<XrefBean> results = new ArrayList<XrefBean>();
        for (Xref xref:xrefs){
           results.add(XrefBeanFactory.asBean(xref));
        }
        return results;        
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/mapByXRefs")
    @Override
    public List<XrefMapBean> mapByXrefs(
            @QueryParam("id") List<String> id,
            @QueryParam("code") List<String> scrCode,
            @QueryParam("provenance") List<String> provenaceIds,
            @QueryParam("tgtCode") List<String> targetCodes) throws IDMapperException {
        if (id == null) throw new IDMapperException("id parameter missig");
        if (id.isEmpty()) throw new IDMapperException("id parameter missig");
        if (scrCode == null) throw new IDMapperException("code parameter missig");
        if (scrCode.isEmpty()) throw new IDMapperException("code parameter missig");
        if (id.size() != scrCode.size()) throw new IDMapperException("Must have same number of id and code parameters");
        HashSet<Xref> srcXrefs = new HashSet<Xref>();
        for (int i = 0; i < id.size() ;i++){
            DataSource dataSource = DataSource.getBySystemCode(scrCode.get(i));
            Xref source = new Xref(id.get(i), dataSource);
            srcXrefs.add(source);
        }
        ArrayList<DataSource> targetDataSources = new ArrayList<DataSource>();
        for (String targetCode: targetCodes){
             targetDataSources.add(DataSource.getBySystemCode(targetCode));
        }
        Map<Xref, Set<XrefProvenance>>  mappings = provenanceMapper.mapIDProvenance(srcXrefs,  provenaceIds, targetDataSources);
        ArrayList<XrefMapBean> results = new ArrayList<XrefMapBean>();
        for (Xref source:mappings.keySet()){
           results.add(XrefMapBeanFactory.asBean(source, mappings.get(source)));
        }
        return results;
    } 

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/xrefExists")
    @Override
    public XrefExistsBean xrefExists( 
            @QueryParam("id") String id,
            @QueryParam("code") String scrCode) throws IDMapperException {
        if (id == null) throw new IDMapperException ("\"id\" parameter can not be null");
        if (scrCode == null) throw new IDMapperException ("\"code\" parameter can not be null");            
        DataSource dataSource = DataSource.getBySystemCode(scrCode);
        Xref source = new Xref(id, dataSource);
        return XrefExistsBeanFactory.asBean(source, idMapper.xrefExists(source));
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getSupportedTgtDataSources")
    @Override
    public List<DataSourceBean> getSupportedTgtDataSources() throws IDMapperException {
        ArrayList<DataSourceBean> targets = new ArrayList<DataSourceBean>();
        Set<DataSource> dataSources = idMapper.getCapabilities().getSupportedSrcDataSources();
        for (DataSource dataSource:dataSources){
            DataSourceBean bean = DataSourceBeanFactory.asBean(dataSource);
            targets.add(bean);
        }
        return targets;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/isFreeSearchSupported")
    @Override
    public FreeSearchSupportedBean isFreeSearchSupported() {
        return new FreeSearchSupportedBean(idMapper.getCapabilities().isFreeSearchSupported());
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/isMappingSupported")
    @Override
    public MappingSupportedBean isMappingSupported(
            @QueryParam("source") String srcCode, 
            @QueryParam("target") String tgtCode) throws IDMapperException {
        if (srcCode == null) throw new IDMapperException ("\"source\" parameter can not be null");
        if (tgtCode == null) throw new IDMapperException ("\"target\" parameter can not be null");
        DataSource src = DataSource.getBySystemCode(srcCode);
        DataSource tgt = DataSource.getBySystemCode(tgtCode);
        return MappingSupportedBeanFactory.asBean(src, tgt, idMapper.getCapabilities().isMappingSupported(src, tgt));
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getProperty")
    @Override
    public PropertyBean getProperty(@PathParam("key")String key) {
        String property = idMapper.getCapabilities().getProperty(key);
        return new PropertyBean(key, property);
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getKeys")
    @Override
    public List<PropertyBean> getKeys() {
        Set<String> keys = idMapper.getCapabilities().getKeys();
        ArrayList<PropertyBean> results = new ArrayList<PropertyBean>();
        IDMapperCapabilities idMapperCapabilities = idMapper.getCapabilities();
        for (String key:keys){
            results.add(new PropertyBean(key, idMapperCapabilities.getProperty(key)));
        }
        return results;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getCapabilities")
    public CapabilitiesBean getCapabilities()  {
        return new CapabilitiesBean(idMapper.getCapabilities());
    }


}
