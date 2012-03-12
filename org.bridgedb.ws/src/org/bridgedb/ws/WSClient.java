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
import org.bridgedb.ws.bean.XRefMapBean;
import org.bridgedb.ws.bean.XrefBean;
import org.bridgedb.ws.bean.XrefExistsBean;

/**
 *
 * @author Christian
 */
public class WSClient implements WSInterface{

    public String serviceAddress = "http://localhost:8080/OPS-IMS";

    private final WebResource webResource;

    public WSClient() {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        webResource = client.resource(serviceAddress);        
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<PropertyBean> getKeys() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PropertyBean getProperty(String key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<DataSourceBean> getSupportedSrcDataSources() throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<DataSourceBean> getSupportedTgtDataSources() throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
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
        throw new UnsupportedOperationException("Not supported yet.");
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
    
}
