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
import org.bridgedb.DataSource;
import org.bridgedb.DataSourceOverwriteLevel;
import org.bridgedb.rdf.BridgeDBRdfHandler;
import org.bridgedb.uri.Mapping;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.ws.WSCoreClient;
import org.bridgedb.ws.WSUriInterface;
import org.bridgedb.ws.WsConstants;
import org.bridgedb.ws.WsUriConstants;
import org.bridgedb.ws.bean.DataSourceUriPatternBean;
import org.bridgedb.ws.bean.MappingBean;
import org.bridgedb.ws.bean.MappingSetInfoBean;
import org.bridgedb.ws.bean.OverallStatisticsBean;
import org.bridgedb.ws.bean.ProfileBean;
import org.bridgedb.ws.bean.UriBean;
import org.bridgedb.ws.bean.UriExistsBean;
import org.bridgedb.ws.bean.UriSearchBean;
import org.bridgedb.ws.bean.XrefBean;

/**
 *
 * @author Christian
 */
public class WSUriClient extends WSCoreClient implements WSUriInterface{

    public final String NO_REPORT = null;
    public final String NO_EXCEPTION = null;
    
    public WSUriClient(String serviceAddress) throws BridgeDBException {
        super(serviceAddress);
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
        BridgeDBRdfHandler.init();
    }
    
    @Override
    public List<MappingBean> map(String id, String scrCode, String uri, String profileUri, List<String> targetCodes, List<String> targetUriPattern) throws BridgeDBException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        if (id != null){
            params.add(WsConstants.ID, id);
        }
        if (scrCode != null){
            params.add(WsConstants.DATASOURCE_SYSTEM_CODE, scrCode);
        }
        if (uri != null){
            params.add(WsUriConstants.URI, uri);            
        }
        params.add(WsUriConstants.PROFILE_URI, profileUri);
        if (targetCodes != null){
            for (String target:targetCodes){
                params.add(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE, target);
            }
        }
        if (targetUriPattern != null){
            for (String target:targetUriPattern){
                params.add(WsUriConstants.TARGET_URI_PATTERN, target);
            }
        }
        //Make service call
        List<MappingBean> result = 
                webResource.path(WsUriConstants.MAP)
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<MappingBean>>() {});
         return result;
    }

    @Override
    public UriExistsBean UriExists(String uri) throws BridgeDBException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add(WsUriConstants.URI, uri);
        //Make service call
        UriExistsBean result = 
                webResource.path(WsUriConstants.URI_EXISTS)
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<UriExistsBean>() {});
         return result;
    }

    @Override
    public UriSearchBean UriSearch(String text, String limitString) throws BridgeDBException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add(WsUriConstants.TEXT, text);
        params.add(WsUriConstants.LIMIT, limitString);
        //Make service call
        UriSearchBean result = 
                webResource.path(WsUriConstants.URI_SEARCH)
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<UriSearchBean>() {});
         return result;
    }

    private String encode (String original){
        return original.replaceAll("%", "%20");
    }
    @Override
    public XrefBean toXref(String uri) throws BridgeDBException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add(WsUriConstants.URI, encode(uri));
        //Make service call
        XrefBean result = 
                webResource.path(WsUriConstants.TO_XREF)
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<XrefBean>() {});
        return result;
    }

    @Override
    public MappingBean getMapping(String id) throws BridgeDBException {
        //Make service call
        MappingBean result = 
                webResource.path(WsUriConstants.MAPPING + "/" + id)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<MappingBean>() {});
         return result;
    }

    /*@Override
    public List<Mapping> getSampleMappings() throws BridgeDBException {
        List<Mapping> result = 
                webResource.path(WsUriConstants.GET_SAMPLE_MAPPINGS)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<Mapping>>() {});
         return result;
    }*/

    @Override
    public OverallStatisticsBean getOverallStatistics() throws BridgeDBException {
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
    public MappingSetInfoBean getMappingSetInfo(String mappingSetId) throws BridgeDBException {
        MappingSetInfoBean result = 
                webResource.path(WsUriConstants.GET_MAPPING_INFO + "/" + mappingSetId)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<MappingSetInfoBean>() {});
         return result;
    }
        
    @Override
    public List<MappingSetInfoBean> getMappingSetInfos(String sourceSysCode, String targetSysCode) throws BridgeDBException {
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
    public DataSourceUriPatternBean getDataSource(String dataSource) throws BridgeDBException{
        //Make service call
        DataSourceUriPatternBean result = 
                webResource.path(WsUriConstants.DATA_SOURCE + "/" + dataSource)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<DataSourceUriPatternBean>() {});
        return result;
    }

   /*TODO FIX this
    @Override
    public ValidationBean validateString(String info, String mimeType, String storeType, String validationType, 
            String includeWarnings) throws BridgeDBException {
        ValidationBean input = new ValidationBean(NO_REPORT, info, mimeType, storeType, validationType, 
                includeWarnings, NO_EXCEPTION);
        ValidationBean result = 
                webResource.path("/validateStringXML")
                .type(MediaType.APPLICATION_XML)
                .post(ValidationBean.class, input);
        return result;
    }*/

	@Override
	public List<ProfileBean> getProfiles() {
		List<ProfileBean> result = 
				webResource.path("profile")
				.accept(MediaType.APPLICATION_XML_TYPE)
				.get(new GenericType<List<ProfileBean>>() {});
		return result;
	}

	@Override
	public ProfileBean getProfile(String id) throws BridgeDBException {
		ProfileBean result = webResource.path("profile/" + id)
		.accept(MediaType.APPLICATION_XML_TYPE)
		.get(new GenericType<ProfileBean>() {});
		return result;
	}
        
    @Override
    public String getSqlCompatVersion() throws BridgeDBException {
        //Make service call
         String result = 
                webResource.path(WsUriConstants.SQL_COMPAT_VERSION)
                .accept(MediaType.TEXT_PLAIN)
                .get(new GenericType<String>() {});
         return result;
    }


}
