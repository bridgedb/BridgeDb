/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import java.util.List;

import com.sun.jersey.api.client.Client;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.bridgedb.IDMapperException;
import org.bridgedb.ws.bean.CapabilitiesBean;
import org.bridgedb.ws.bean.DataSourceBean;
import org.bridgedb.ws.bean.FreeSearchSupportedBean;
import org.bridgedb.ws.bean.MappingSetInfoBean;
import org.bridgedb.ws.bean.MappingSetStatisticsBean;
import org.bridgedb.ws.bean.MappingSupportedBean;
import org.bridgedb.ws.bean.PropertyBean;
import org.bridgedb.ws.bean.URLBean;
import org.bridgedb.ws.bean.URLExistsBean;
import org.bridgedb.ws.bean.URLMappingBean;
import org.bridgedb.ws.bean.URLSearchBean;
import org.bridgedb.ws.bean.UriSpacesBean;
import org.bridgedb.ws.bean.XrefMapBean;
import org.bridgedb.ws.bean.XrefBean;
import org.bridgedb.ws.bean.XrefExistsBean;

/**
 *
 * @author Christian
 */
public class WSOpsClient extends WSCoreClient implements WSOpsInterface{

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
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        //Make service call
        URLMappingBean result = 
                webResource.path("getMapping/" + id)
                .queryParams(params)
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
    public MappingSetStatisticsBean getMappingSetStatistics() throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        //Make service call
        MappingSetStatisticsBean result = 
                webResource.path("getMappingStatistics")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<MappingSetStatisticsBean>() {});
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
    public UriSpacesBean getUriSpaces(String code) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("code", code);
        //Make service call
        UriSpacesBean result = 
                webResource.path("getUriSpaces")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<UriSpacesBean>() {});
         return result;
    }
        

}
