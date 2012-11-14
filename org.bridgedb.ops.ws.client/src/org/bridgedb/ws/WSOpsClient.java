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

import com.sun.jersey.api.client.ClientResponse;
import java.io.InputStream;
import java.util.List;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.bridgedb.IDMapperException;
import org.bridgedb.ws.bean.DataSourceUriSpacesBean;
import org.bridgedb.ws.bean.MappingSetInfoBean;
import org.bridgedb.ws.bean.OverallStatisticsBean;
import org.bridgedb.ws.bean.URLBean;
import org.bridgedb.ws.bean.URLExistsBean;
import org.bridgedb.ws.bean.URLMappingBean;
import org.bridgedb.ws.bean.URLSearchBean;
import org.bridgedb.ws.bean.ValidationBean;
import org.bridgedb.ws.bean.XrefBean;

/**
 *
 * @author Christian
 */
public class WSOpsClient extends WSCoreClient implements WSOpsInterface{

    public final String NO_REPORT = null;
    public final String NO_EXCEPTION = null;
    
    public WSOpsClient(String serviceAddress) {
        super(serviceAddress);
    }

    @Override
    public List<URLMappingBean> mapURL(String URL, List<String> targetUriSpace) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("URL", URL);
        for (String target:targetUriSpace){
            params.add("targetURISpace", target);
        }
        //Make service call
        List<URLMappingBean> result = 
                webResource.path("mapURL")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<URLMappingBean>>() {});
         return result;
    }

    @Override
    public URLExistsBean URLExists(String URL) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("URL", URL);
        //Make service call
        URLExistsBean result = 
                webResource.path("URLExists")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<URLExistsBean>() {});
         return result;
    }

    @Override
    public URLSearchBean URLSearch(String text, String limitString) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("text", text);
        params.add("limit", limitString);
        //Make service call
        URLSearchBean result = 
                webResource.path("URLSearch")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<URLSearchBean>() {});
         return result;
    }

    @Override
    public XrefBean toXref(String URL) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("URL", URL);
        //Make service call
        XrefBean result = 
                webResource.path("toXref")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<XrefBean>() {});
         return result;
    }

    @Override
    public URLMappingBean getMapping(String id) throws IDMapperException {
        //Make service call
        URLMappingBean result = 
                webResource.path("mapping/" + id)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<URLMappingBean>() {});
         return result;
    }

    @Override
    public List<URLBean> getSampleSourceURLs() throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        //Make service call
        List<URLBean> result = 
                webResource.path("getSampleSourceURLs")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<URLBean>>() {});
         return result;
    }

    @Override
    public OverallStatisticsBean getOverallStatistics() throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        //Make service call
        OverallStatisticsBean result = 
                webResource.path("getOverallStatistics")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<OverallStatisticsBean>() {});
         return result;
    }

    @Override
    public MappingSetInfoBean getMappingSetInfo(String mappingSetId) throws IDMapperException {
        MappingSetInfoBean result = 
                webResource.path("getMappingSetInfo/" + mappingSetId)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<MappingSetInfoBean>() {});
         return result;
    }
        
    @Override
    public List<MappingSetInfoBean> getMappingSetInfos() throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        //Make service call
        List<MappingSetInfoBean> result = 
                webResource.path("getMappingSetInfos")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<MappingSetInfoBean>>() {});
         return result;
    }

    @Override
    public DataSourceUriSpacesBean getDataSource(String dataSource) throws IDMapperException{
        //Make service call
        DataSourceUriSpacesBean result = 
                webResource.path("dataSource/" + dataSource)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<DataSourceUriSpacesBean>() {});
         return result;
    }

    @Override
    public String loadString(String info, String mineType, String storeType, String validationType) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String checkStringValid(String info, String mimeType, String storeType, String validationType) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //TODO FIX this
    @Override
    public ValidationBean validateString(String info, String mimeType, String storeType, String validationType, 
            String includeWarnings) throws IDMapperException {
        ValidationBean input = new ValidationBean(NO_REPORT, info, mimeType, storeType, validationType, 
                includeWarnings, NO_EXCEPTION);
        ValidationBean result = 
                webResource.path("/validateStringXML")
                .type(MediaType.APPLICATION_XML)
                .post(ValidationBean.class, input);
//        System.out.println(response.getStatus());  
//        System.out.println(response);  
//        System.out.println(response.hasEntity());  
//        System.out.println(response.getClass());  
//        System.out.println(response.getType());  
//         = response.getEntity(ValidationBean.class);
        System.out.println(result);
        return result;
    }

    @Override
    public ValidationBean validateInputStream(InputStream inputStream, String mimeTypee, String storeType, String validationType, String includeWarnings) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
