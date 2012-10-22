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

import java.util.ArrayList;
import org.bridgedb.ws.bean.MappingSetInfoBean;
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
import org.bridgedb.mysql.MySQLSpecific;
import org.bridgedb.rdf.RdfWrapper;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.url.URLMapper;
import org.bridgedb.url.URLMapping;
import org.bridgedb.utils.Reporter;
import org.bridgedb.ws.bean.DataSourceUriSpacesBean;
import org.bridgedb.ws.bean.DataSourceUriSpacesBeanFactory;
import org.bridgedb.ws.bean.MappingSetInfoBeanFactory;
import org.bridgedb.ws.bean.OverallStatisticsBean;
import org.bridgedb.ws.bean.OverallStatisticsBeanFactory;
import org.bridgedb.ws.bean.URLBean;
import org.bridgedb.ws.bean.URLMappingBeanFactory;
import org.bridgedb.ws.bean.URLSearchBean;
import org.bridgedb.ws.bean.XrefBean;
import org.bridgedb.ws.bean.XrefBeanFactory;

@Path("/")
public class WSOpsService extends WSCoreService implements WSOpsInterface {

    protected URLMapper urlMapper;
    
    public WSOpsService() {
    	try {
			SQLAccess sqlAccess = SqlFactory.createSQLAccess();
			urlMapper = new SQLUrlMapper(false, sqlAccess, new MySQLSpecific());
		} catch (IDMapperException e) {
			Reporter.report(e.getLocalizedMessage());
			throw new RuntimeException(e);
		}   	
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
    		@QueryParam("profileURL") String profileURL,
            @QueryParam("targetURISpace") List<String> targetURISpace) throws IDMapperException {
        if (URL == null) throw new IDMapperException("URL parameter missing.");        
        if (URL.isEmpty()) throw new IDMapperException("URL parameter may not be null.");        
        if (profileURL == null || profileURL.isEmpty())  profileURL = RdfWrapper.getProfileURI(0);
        if (targetURISpace == null) {
        	targetURISpace = new ArrayList<String>();
        }
        String[] targetURISpaces = new String[targetURISpace.size()];
        for (int i = 0; i < targetURISpace.size(); i++){
            targetURISpaces[i] = targetURISpace.get(i);
        }
        Set<URLMapping> urlMappings = urlMapper.mapURLFull(URL, profileURL, targetURISpaces);
        List<URLMappingBean> results = new ArrayList<URLMappingBean>(); 
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
    @Path("/mapping")
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
    @Path("/getOverallStatistics") 
    public OverallStatisticsBean getOverallStatistics() throws IDMapperException {
        OverallStatistics overallStatistics = urlMapper.getOverallStatistics();
        OverallStatisticsBean bean = OverallStatisticsBeanFactory.asBean(overallStatistics);
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
