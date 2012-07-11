package org.bridgedb.ws;

import java.util.ArrayList;
import org.bridgedb.ws.bean.MappingSetInfoBean;
import org.bridgedb.ws.bean.MappingSetStatisticsBean;
import org.bridgedb.ws.bean.URLExistsBean;
import org.bridgedb.ws.bean.URLMappingBean;
import java.util.List;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.MappingSetStatistics;
import org.bridgedb.url.URLMapper;
import org.bridgedb.url.URLMapping;
import org.bridgedb.ws.bean.DataSourceUriSpacesBean;
import org.bridgedb.ws.bean.DataSourceUriSpacesBeanFactory;
import org.bridgedb.ws.bean.MappingSetInfoBeanFactory;
import org.bridgedb.ws.bean.MappingSetStatisticsBeanFactory;
import org.bridgedb.ws.bean.URLBean;
import org.bridgedb.ws.bean.URLMappingBeanFactory;
import org.bridgedb.ws.bean.URLSearchBean;
import org.bridgedb.ws.bean.XrefBean;
import org.bridgedb.ws.bean.XrefBeanFactory;

@Path("/")
public class WSOpsService extends WSCoreService implements WSOpsInterface {

    protected URLMapper urlMapper;
    
    public WSOpsService() {
    }

    public WSOpsService(URLMapper urlMapper) {
        super(urlMapper);
        this.urlMapper = urlMapper;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/mapURL")
    @Override
    public List<URLMappingBean> mapURL(@QueryParam("URL") String URL,
            @QueryParam("targetURISpace") List<String> targetURISpace) throws IDMapperException {
        if (URL == null) throw new IDMapperException("URL parameter missing.");
        if (URL.isEmpty()) throw new IDMapperException("URL parameter may not be null.");
        String[] targetURISpaces = new String[targetURISpace.size()];
        for (int i = 0; i < targetURISpace.size(); i++){
            targetURISpaces[i] = targetURISpace.get(i);
        }
        Set<URLMapping> urlMappings = urlMapper.mapURLFull(URL, targetURISpaces);
        ArrayList<URLMappingBean> results = new ArrayList<URLMappingBean>(); 
        for (URLMapping urlMapping:urlMappings){
            results.add(URLMappingBeanFactory.asBean(urlMapping));
        }
        return results;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/URLExists")
    @Override
    public URLExistsBean URLExists(@QueryParam("URL") String URL) throws IDMapperException {
        if (URL == null) throw new IDMapperException("URL parameter missing.");
        if (URL.isEmpty()) throw new IDMapperException("URL parameter may not be null.");
        boolean exists = urlMapper.uriExists(URL);
        return new URLExistsBean(URL, exists);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/URLSearch")
    @Override
    public URLSearchBean URLSearch(@QueryParam("text") String text,
            @QueryParam("limit") String limitString) throws IDMapperException {
        if (text == null) throw new IDMapperException("text parameter missing.");
        if (text.isEmpty()) throw new IDMapperException("text parameter may not be null.");
        if (limitString == null || limitString.isEmpty()){
            Set<String> urls = urlMapper.urlSearch(text, Integer.MAX_VALUE);
            return new URLSearchBean(text, urls);
        } else {
            int limit = Integer.parseInt(limitString);
            Set<String> urls = urlMapper.urlSearch(text, limit);
            return new URLSearchBean(text, urls);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/toXref")
    @Override
    public XrefBean toXref(@QueryParam("URL") String URL) throws IDMapperException {
        if (URL == null) throw new IDMapperException("URL parameter missing.");
        if (URL.isEmpty()) throw new IDMapperException("URL parameter may not be null.");
        Xref xref = urlMapper.toXref(URL);
        return XrefBeanFactory.asBean(xref);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/Amapping")
    public URLMappingBean getMapping() throws IDMapperException {
        throw new IDMapperException("id path parameter missing.");
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/mapping/{id}")
    public URLMappingBean getMapping(@PathParam("id") String idString) throws IDMapperException {
        if (idString == null) throw new IDMapperException("id path parameter missing.");
        if (idString.isEmpty()) throw new IDMapperException("id path parameter may not be null.");
        int id = Integer.parseInt(idString);
        URLMapping mapping = urlMapper.getMapping(id);
        return URLMappingBeanFactory.asBean(mapping);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/Amapping/{id}")
    public DataSourceUriSpacesBean getDataSourceX(@PathParam("id") String id) throws IDMapperException {
        if (id == null) throw new IDMapperException("id path parameter missing.");
        if (id.isEmpty()) throw new IDMapperException("id path parameter may not be null.");
        Set<String> urls = urlMapper.getUriSpaces(id);
        DataSource ds = DataSource.getBySystemCode(id);
        DataSourceUriSpacesBean bean = DataSourceUriSpacesBeanFactory.asBean(ds, urls);
        return bean;
    }
 
    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getSampleSourceURLs") 
    public List<URLBean> getSampleSourceURLs() throws IDMapperException {
        Set<String> URLs = urlMapper.getSampleSourceURLs();
        List<URLBean> beans = new ArrayList<URLBean>();
        for (String URL:URLs){
            URLBean bean = new URLBean();
            bean.setURL(URL);
            beans.add(bean);
        }
        return beans;
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getMappingStatistics") 
    public MappingSetStatisticsBean getMappingSetStatistics() throws IDMapperException {
        MappingSetStatistics overallStatistics = urlMapper.getMappingSetStatistics();
        MappingSetStatisticsBean bean = MappingSetStatisticsBeanFactory.asBean(overallStatistics);
        return bean;
    }


    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getMappingSetInfos") 
    public List<MappingSetInfoBean> getMappingSetInfos() throws IDMapperException {
        List<MappingSetInfo> infos = urlMapper.getMappingSetInfos();
        ArrayList<MappingSetInfoBean> results = new ArrayList<MappingSetInfoBean>();
        for (MappingSetInfo info:infos){
            results.add(MappingSetInfoBeanFactory.asBean(info));
        }
        return results;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/dataSource")
    public DataSourceUriSpacesBean getDataSource() throws IDMapperException {
        throw new IDMapperException("id path parameter missing.");
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Override
    @Path("/dataSource/{id}")
    public DataSourceUriSpacesBean getDataSource(@PathParam("id") String id) throws IDMapperException {
        if (id == null) throw new IDMapperException("id path parameter missing.");
        if (id.isEmpty()) throw new IDMapperException("id path parameter may not be null.");
        Set<String> urls = urlMapper.getUriSpaces(id);
        DataSource ds = DataSource.getBySystemCode(id);
        DataSourceUriSpacesBean bean = DataSourceUriSpacesBeanFactory.asBean(ds, urls);
        return bean;
    }
 
}
