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
package org.bridgedb.uri.ws.client;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.bridgedb.uri.api.SetMappings;
import org.bridgedb.uri.lens.Lens;
import org.bridgedb.uri.ws.WSUriInterface;
import org.bridgedb.uri.ws.WsUriConstants;
import org.bridgedb.uri.ws.bean.DataSourceUriPatternBean;
import org.bridgedb.uri.ws.bean.LensBean;
import org.bridgedb.uri.ws.bean.LensesBean;
import org.bridgedb.uri.ws.bean.MappingSetInfoBean;
import org.bridgedb.uri.ws.bean.MappingSetInfosBean;
import org.bridgedb.uri.ws.bean.MappingsBean;
import org.bridgedb.uri.ws.bean.MappingsBySetBean;
import org.bridgedb.uri.ws.bean.OverallStatisticsBean;
import org.bridgedb.uri.ws.bean.SourceInfoBean;
import org.bridgedb.uri.ws.bean.SourceInfosBean;
import org.bridgedb.uri.ws.bean.SourceTargetInfoBean;
import org.bridgedb.uri.ws.bean.SourceTargetInfosBean;
import org.bridgedb.uri.ws.bean.UriExistsBean;
import org.bridgedb.uri.ws.bean.UriMappings;
import org.bridgedb.uri.ws.bean.UriSearchBean;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.ws.WSCoreClient;
import org.bridgedb.ws.WsConstants;
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
    }
    
    @Override
    public Response map(String id, String scrCode, String uri, String lensUri, 
            Boolean includeXrefResults, Boolean includeUriResults,
            List<String> targetCodes, String graph, List<String> targetUriPattern) throws BridgeDBException {
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
        if (lensUri != null){
            params.add(WsUriConstants.LENS_URI, lensUri);        
        }
        if (includeXrefResults != null){
            params.add(WsUriConstants.INCLUDE_XREF_RESULTS, includeXrefResults.toString());        
        }
        if (includeUriResults != null){
            params.add(WsUriConstants.INCLUDE_URI_RESULTS, includeUriResults.toString());        
        }
        if (targetCodes != null){
            for (String target:targetCodes){
                params.add(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE, target);
            }
        }
        if (graph != null){
            params.add(WsUriConstants.GRAPH, graph); 
        }
        if (targetUriPattern != null){
            for (String target:targetUriPattern){
                params.add(WsUriConstants.TARGET_URI_PATTERN, target);
            }
        }
        try{
            //Make service call
            MappingsBean result = 
                    webResource.path(WsUriConstants.MAP)
                    .queryParams(params)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(new GenericType<MappingsBean>() {});
            return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
        } catch (UniformInterfaceException ex){
            //throw new BridgeDBException ("Error", ex); 
            return Response.noContent().build();
        }
    }

    @Override
    public Response mapBySet(List<String> uris, String lensUri, String graph, List<String> targetUriPattern) throws BridgeDBException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        for (String uri:uris){
            if (uri != null){
                params.add(WsUriConstants.URI, uri);            
            }
        }
        if (lensUri != null){
            params.add(WsUriConstants.LENS_URI, lensUri);        
        }
        if (graph != null){
            params.add(WsUriConstants.GRAPH, graph); 
        }
        if (targetUriPattern != null){
            for (String target:targetUriPattern){
                params.add(WsUriConstants.TARGET_URI_PATTERN, target);
            }
        }
        try {
            MappingsBySetBean result = 
                webResource.path(WsUriConstants.MAP_BY_SET)
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<MappingsBySetBean>() {});
            return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
        } catch (UniformInterfaceException ex){
            return Response.noContent().build();
        }
    }


    @Override
    public Response UriExists(String uri) throws BridgeDBException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add(WsUriConstants.URI, uri);
        try {
            //Make service call
            UriExistsBean result = 
                    webResource.path(WsUriConstants.URI_EXISTS)
                    .queryParams(params)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(new GenericType<UriExistsBean>() {});
            return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
        } catch (UniformInterfaceException ex){
            return Response.noContent().build();
        }
    }

    @Override
    public Response UriSearch(String text, String limitString) throws BridgeDBException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add(WsUriConstants.TEXT, text);
        params.add(WsUriConstants.LIMIT, limitString);
        try {
            //Make service call
            UriSearchBean result = 
                    webResource.path(WsUriConstants.URI_SEARCH)
                    .queryParams(params)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(new GenericType<UriSearchBean>() {});
            return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
        } catch (UniformInterfaceException ex){
            return Response.noContent().build();
        }
    }

    private String encode (String original){
        return original.replaceAll("%", "%20");
    }
    
    @Override
    public Response toXref(String uri) throws BridgeDBException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add(WsUriConstants.URI, encode(uri));
        try {
            //Make service call
            XrefBean result = 
                    webResource.path(WsUriConstants.TO_XREF)
                    .queryParams(params)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(new GenericType<XrefBean>() {});
            return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
        } catch (UniformInterfaceException ex){
            return Response.noContent().build();
        }
    }

    @Override
    public Response toUris(String id, String scrCode) throws BridgeDBException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add(WsUriConstants.ID, encode(id));
        params.add(WsUriConstants.DATASOURCE_SYSTEM_CODE, encode(scrCode));
        try {
            //Make service call
            UriMappings result = 
                    webResource.path(WsUriConstants.TO_XREF)
                    .queryParams(params)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(new GenericType<UriMappings>() {});
            return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
        } catch (UniformInterfaceException ex){
            return Response.noContent().build();
        }
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
    public Response getOverallStatistics(String lensUri) throws BridgeDBException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add(WsUriConstants.LENS_URI, encode(lensUri));
        try {
             //Make service call
             OverallStatisticsBean result = 
                    webResource.path(WsUriConstants.GET_OVERALL_STATISTICS)
                    .queryParams(params)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(new GenericType<OverallStatisticsBean>() {});
            return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
        } catch (UniformInterfaceException ex){
            return Response.noContent().build();
        }
    }

    @Override
    public Response getSourceInfos(String lensUri) throws BridgeDBException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add(WsUriConstants.LENS_URI, encode(lensUri));
        try {
            SourceInfosBean result = 
                    webResource.path(WsUriConstants.SOURCE_INFOS)
                    .queryParams(params)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(new GenericType<SourceInfosBean>() {});
            return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
        } catch (UniformInterfaceException ex){
            return Response.noContent().build();
        }
    }

    @Override
    public Response getSourceTargetInfos(String sourceSysCode, String lensUri) throws BridgeDBException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add(WsUriConstants.SOURCE_DATASOURCE_SYSTEM_CODE, encode(sourceSysCode));
        params.add(WsUriConstants.LENS_URI, encode(lensUri));
        try {
             SourceTargetInfosBean result = 
                    webResource.path(WsUriConstants.SOURCE_TARGET_INFOS)
                    .queryParams(params)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(new GenericType<SourceTargetInfosBean>() {});
            return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
        } catch (UniformInterfaceException ex){
            ex.printStackTrace();
            return Response.noContent().build();
        }
    }

   @Override
    public Response getMappingSetInfo(String mappingSetId) throws BridgeDBException {
        try {
            MappingSetInfoBean result = 
                    webResource.path(WsUriConstants.MAPPING_SET + "/" + mappingSetId)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(new GenericType<MappingSetInfoBean>() {});
            return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
        } catch (UniformInterfaceException ex){
            return Response.noContent().build();
        }
    }
        
    @Override
    public Response getMappingSetInfos(String sourceSysCode, String targetSysCode, String lensUri) throws BridgeDBException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add(WsUriConstants.SOURCE_DATASOURCE_SYSTEM_CODE, sourceSysCode);
        params.add(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE, targetSysCode);
        params.add(WsUriConstants.LENS_URI, encode(lensUri));
        try {
            //Make service call
            MappingSetInfosBean result = 
                    webResource.path(WsUriConstants.MAPPING_SET)
                    .queryParams(params)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(new GenericType<MappingSetInfosBean>() {});
                return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
        } catch (UniformInterfaceException ex){
            return Response.noContent().build();
        }
    }

    @Override
    public Response getDataSource(String dataSource) throws BridgeDBException{
        try {
            //Make service call
            DataSourceUriPatternBean result = 
                    webResource.path(WsUriConstants.DATA_SOURCE + "/" + dataSource)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(new GenericType<DataSourceUriPatternBean>() {});
            return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
        } catch (UniformInterfaceException ex){
            return Response.noContent().build();
        }
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

	public Response getLenses() {
        try {
            List<LensBean> result = 
                	webResource.path(Lens.METHOD_NAME)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(new GenericType<List<LensBean>>() {});
                return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
        } catch (UniformInterfaceException ex){
            return Response.noContent().build();
        }
	}

    @Override
	public Response getLens(String id) throws BridgeDBException {
        try {
            LensBean result = webResource.path(Lens.METHOD_NAME + "/" + id)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(new GenericType<LensBean>() {});
            return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
        } catch (UniformInterfaceException ex){
            return Response.noContent().build();
        }
	}
        
    @Override
    public Response getSqlCompatVersion() throws BridgeDBException {
        try {
            //Make service call
            String result = 
                    webResource.path(WsUriConstants.SQL_COMPAT_VERSION)
                    .accept(MediaType.TEXT_PLAIN)
                    .get(new GenericType<String>() {});
                return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
        } catch (UniformInterfaceException ex){
            return Response.noContent().build();
        }
    }

    @Override
    public Response mapUri(List<String> uris, String lensUri, String graph, List<String> targetUriPatterns) throws BridgeDBException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        for (String uri:uris){
            if (uri != null){
                params.add(WsUriConstants.URI, uri);            
            }
        }
        if (lensUri != null){
            params.add(WsUriConstants.LENS_URI, lensUri);        
        }
         if (graph != null){
            params.add(WsUriConstants.GRAPH, graph); 
        }
        if (targetUriPatterns != null){
            for (String target:targetUriPatterns){
                params.add(WsUriConstants.TARGET_URI_PATTERN, target);
            }
        }
        try{
            //Make service call
            UriMappings result = 
                    webResource.path(WsUriConstants.MAP_URI)
                    .queryParams(params)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(new GenericType<UriMappings>() {});
             return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
        } catch (UniformInterfaceException ex){
            return Response.noContent().build();
        }
   }

    @Override
    public Response getLenses(String lensUri, String lensGroup) throws BridgeDBException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        if (lensUri != null){
            params.add(WsUriConstants.LENS_URI, lensUri);        
        }
        if (lensGroup != null){
            params.add(WsUriConstants.LENS_GROUP, lensGroup);        
        }
        try{
            //Make service call
            LensesBean result = 
                    webResource.path(WsUriConstants.MAP_URI)
                    .queryParams(params)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(new GenericType<LensesBean>() {});
             return Response.ok(result, MediaType.APPLICATION_XML_TYPE).build();
        } catch (UniformInterfaceException ex){
            return Response.noContent().build();
        }
    }

 }
