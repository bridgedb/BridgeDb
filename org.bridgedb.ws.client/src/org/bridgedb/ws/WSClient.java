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
import org.bridgedb.ws.bean.MappingSupportedBean;
import org.bridgedb.ws.bean.PropertyBean;
import org.bridgedb.ws.bean.URLExistsBean;
import org.bridgedb.ws.bean.URLMapBean;
import org.bridgedb.ws.bean.URLSearchBean;
import org.bridgedb.ws.bean.URLsBean;
import org.bridgedb.ws.bean.XRefMapBean;
import org.bridgedb.ws.bean.XrefBean;
import org.bridgedb.ws.bean.XrefExistsBean;

/**
 *
 * @author Christian
 */
public class WSClient implements WSInterface{

    public String serviceAddress;

    private final WebResource webResource;

    public WSClient(String serviceAddress) {
        this.serviceAddress = serviceAddress;
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        webResource = client.resource(serviceAddress);        
    }
    
    //*** URLMapper functions methods *****
    @Override
    public List<URLMapBean> mapByURLs(List<String> srcURL, List<String> tgtNameSpace) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        for (String asrcURL:srcURL){
            params.add("srcURL", asrcURL);
        }
        for (String target:tgtNameSpace){
            params.add("tgtNameSpace", target);
        }
        //Make service call
        List<URLMapBean> result = 
                webResource.path("mapByURLs")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<URLMapBean>>() {});
        return result;
    }

    @Override
    public URLMapBean mapByURL(String srcURL, List<String> tgtNameSpace) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("srcURL", srcURL);
        for (String target:tgtNameSpace){
            params.add("tgtNameSpace", target);
        }
        //Make service call
        URLMapBean result = 
                webResource.path("mapByURL")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<URLMapBean>() {});
        return result;
    }

    @Override
    public URLExistsBean urlExists(String URL) throws IDMapperException {
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
    public URLSearchBean URLSearch(String text, Integer limit) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("text", text);
        params.add("limit", limit.toString());
        //Make service call
        URLSearchBean result = 
                webResource.path("URLSearch")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<URLSearchBean>() {});
        return result;
    }
    
    @Override
    public List<XrefBean> freeSearch(String text, Integer limit) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("text", text);
        params.add("limit", limit.toString());
        //Make service call
        List<XrefBean> result = 
                webResource.path("freeSearch")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<XrefBean>>() {});
        return result;
    }

    @Override
    public DataSourceBean getDataSoucre(String code) throws IDMapperException {
        //Make service call
        DataSourceBean result = 
                webResource.path("getDataSource/" + code)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<DataSourceBean>() {});
        return result;
    }

    @Override
    public List<PropertyBean> getKeys() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        //Make service call
        List<PropertyBean> result = 
                webResource.path("getKeys")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<PropertyBean>>() {});
        return result;
    }

    @Override
    public PropertyBean getProperty(String key) {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("key", key);
        //Make service call
        PropertyBean result = 
                webResource.path("getProperty")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<PropertyBean>() {});
        return result;
    }

    @Override
    public List<DataSourceBean> getSupportedSrcDataSources() throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        //Make service call
        List<DataSourceBean> result = 
                webResource.path("getSupportedSrcDataSources")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<DataSourceBean>>() {});
        return result;
    }

    @Override
    public List<DataSourceBean> getSupportedTgtDataSources() throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        //Make service call
        List<DataSourceBean> result = 
                webResource.path("getSupportedTgtDataSources")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<DataSourceBean>>() {});
        return result;
    }

    @Override
    public FreeSearchSupportedBean isFreeSearchSupported() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        //Make service call
        FreeSearchSupportedBean result = 
                webResource.path("isFreeSearchSupported")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<FreeSearchSupportedBean>() {});
        return result;
    }

    @Override
    public MappingSupportedBean isMappingSupported(String srcCode, String tgtCode) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("source", srcCode);
        params.add("target", tgtCode);
        //Make service call
        MappingSupportedBean result = 
                webResource.path("isMappingSupported")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<MappingSupportedBean>() {});
        return result;
    }

    @Override
    public List<XrefBean> mapByXref(String id, String scrCode, List<String> targetCodes) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("id", id);
        params.add("code", scrCode);
        for (String target:targetCodes){
            params.add("tgtCode", target);
        }
        //Make service call
        List<XrefBean> result = 
                webResource.path("mapByXRef")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<XrefBean>>() {});
        return result;
    }

    @Override
    public List<XRefMapBean> mapByXrefs(List<String> id, List<String> scrCode, List<String> targetCodes) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        for (String one:id){
            params.add("id", one);
        }
        for (String one:scrCode){
            params.add("code", one);
        }
        for (String target:targetCodes){
            params.add("tgtCode", target);
        }
        //Make service call
        List<XRefMapBean> result = 
                webResource.path("mapByXRefs")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<XRefMapBean>>() {});
        return result;
    }

    @Override
    public XrefExistsBean xrefExists(String id, String scrCode) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("id", id);
        params.add("code", scrCode);
        //Make service call
        XrefExistsBean result = 
                webResource.path("xrefExists")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<XrefExistsBean>() {});
        return result;
    }

    @Override
    public CapabilitiesBean getCapabilities() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        //Make service call
        CapabilitiesBean result = 
                webResource.path("getCapabilities")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<CapabilitiesBean>() {});
        return result;
    }

    @Override
    public List<XrefBean> getXrefByPosition(String code, Integer position, Integer limit) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        if (code != null) {
            params.add("code",  code);
        }
        params.add("position",  position.toString());
        if (limit != null){
            params.add("limit",  limit.toString());
        }
       //Make service call
        List<XrefBean> result = 
                webResource.path("getXrefByPosition")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<XrefBean>>() {});
        return result;
    }

    @Override
    public URLsBean getURLByPosition(String nameSpace, Integer position, Integer limit) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        if (nameSpace != null) {
            params.add("nameSpace",  nameSpace);
        }
        params.add("position",  position.toString());
        if (limit != null){
            params.add("limit",  limit.toString());
        }
       //Make service call
        URLsBean result = 
                webResource.path("getURLByPosition")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<URLsBean>() {});
        return result;
    }

}
