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
package org.bridgedb.ws.uri.client;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.bridgedb.IDMapperException;
import org.bridgedb.url.Mapping;
import org.bridgedb.ws.WSCoreClient;
import org.bridgedb.ws.WSUriInterface;
import org.bridgedb.ws.WsConstants;
import org.bridgedb.ws.WsUriConstants;
import org.bridgedb.ws.bean.DataSourceUriSpacesBean;
import org.bridgedb.ws.bean.MappingSetInfoBean;
import org.bridgedb.ws.bean.OverallStatisticsBean;
import org.bridgedb.ws.bean.URLBean;
import org.bridgedb.ws.bean.URLExistsBean;
import org.bridgedb.ws.bean.URLSearchBean;
import org.bridgedb.ws.bean.XrefBean;

/**
 *
 * @author Christian
 */
public class WSUriClient extends WSCoreClient implements WSUriInterface{

    public final String NO_REPORT = null;
    public final String NO_EXCEPTION = null;
    
    public WSUriClient(String serviceAddress) {
        super(serviceAddress);
    }

    @Override
    public List<Mapping> mapURL(String URL, List<String> targetUriSpace) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add(WsUriConstants.URL, URL);
        for (String target:targetUriSpace){
            params.add(WsUriConstants.TARGET_URI_SPACE, target);
        }
        //Make service call
        List<Mapping> result = 
                webResource.path(WsUriConstants.MAP_URL)
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<Mapping>>() {});
         return result;
    }

    @Override
    public List<Mapping> mapToURLs(String id, String scrCode, List<String> targetUriSpace) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add(WsConstants.ID, id);
        params.add(WsConstants.DATASOURCE_SYSTEM_CODE, scrCode);
        for (String target:targetUriSpace){
            params.add(WsUriConstants.TARGET_URI_SPACE, target);
        }
        //Make service call
        List<Mapping> result = 
                webResource.path(WsUriConstants.MAP_TO_URLS)
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<Mapping>>() {});
         return result;
    }
   
    @Override
    public URLExistsBean URLExists(String URL) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add(WsUriConstants.URL, URL);
        //Make service call
        URLExistsBean result = 
                webResource.path(WsUriConstants.URL_EXISTS)
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<URLExistsBean>() {});
         return result;
    }

    @Override
    public URLSearchBean URLSearch(String text, String limitString) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add(WsUriConstants.TEXT, text);
        params.add(WsUriConstants.LIMIT, limitString);
        //Make service call
        URLSearchBean result = 
                webResource.path(WsUriConstants.URL_SEARCH)
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<URLSearchBean>() {});
         return result;
    }

    @Override
    public XrefBean toXref(String URL) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add(WsUriConstants.URL, URL);
        //Make service call
        XrefBean result = 
                webResource.path(WsUriConstants.TO_XREF)
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<XrefBean>() {});
         return result;
    }

    @Override
    public Mapping getMapping(String id) throws IDMapperException {
        //Make service call
        Mapping result = 
                webResource.path(WsUriConstants.MAPPING + "/" + id)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<Mapping>() {});
         return result;
    }

    @Override
    public List<Mapping> getSampleMappings() throws IDMapperException {
        List<Mapping> result = 
                webResource.path(WsUriConstants.GET_SAMPLE_MAPPINGS)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<Mapping>>() {});
         return result;
    }

    @Override
    public OverallStatisticsBean getOverallStatistics() throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        //Make service call
        OverallStatisticsBean result = 
                webResource.path(WsUriConstants.GET_OVERALL_STATISTICS)
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<OverallStatisticsBean>() {});
         return result;
    }

    @Override
    public MappingSetInfoBean getMappingSetInfo(String mappingSetId) throws IDMapperException {
        MappingSetInfoBean result = 
                webResource.path(WsUriConstants.GET_MAPPING_INFO + "/" + mappingSetId)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<MappingSetInfoBean>() {});
         return result;
    }
        
    @Override
    public List<MappingSetInfoBean> getMappingSetInfos(String sourceSysCode, String targetSysCode) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add(WsUriConstants.SOURCE_DATASOURCE_SYSTEM_CODE, sourceSysCode);
        params.add(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE, targetSysCode);
        //Make service call
        List<MappingSetInfoBean> result = 
                webResource.path(WsUriConstants.GET_MAPPING_INFO)
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<MappingSetInfoBean>>() {});
         return result;
    }

    @Override
    public DataSourceUriSpacesBean getDataSource(String dataSource) throws IDMapperException{
        //Make service call
        DataSourceUriSpacesBean result = 
                webResource.path(WsUriConstants.DATA_SOURCE + "/" + dataSource)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<DataSourceUriSpacesBean>() {});
         return result;
    }

   /*TODO FIX this
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
    }*/

    @Override
    public String getSqlCompatVersion() throws IDMapperException {
        //Make service call
         String result = 
                webResource.path(WsUriConstants.SQL_COMPAT_VERSION)
                .accept(MediaType.TEXT_PLAIN)
                .get(new GenericType<String>() {});
         return result;
    }

}
