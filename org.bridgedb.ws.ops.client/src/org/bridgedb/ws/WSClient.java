/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import java.util.ArrayList;
import java.util.List;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.bridgedb.IDMapperException;
import org.bridgedb.ws.bean.OverallStatisticsBean;
import org.bridgedb.ws.bean.ProvenanceBean;
import org.bridgedb.ws.bean.URLBean;
import org.bridgedb.ws.bean.URLMappingBean;
import org.bridgedb.ws.bean.XrefBean;

/**
 *
 * @author Christian
 */
public  class WSClient extends WSCoreClient implements WSInterface{

    public WSClient(String serviceAddress) {
        super(serviceAddress);
    }
    
    @Override
    public List<URLMappingBean> getMappings(
            List<String> URLs, List<String> sourceURLs, List<String> targetURLs, 
            List<String> nameSpaces, List<String> sourceNameSpaces, List<String> targetNameSpaces,
            List<String> provenanceIdStrings, String positionString, String limitString, Boolean full){
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        for (String URL:URLs){
            params.add("URL", URL);
        }
        for (String sourceURL:sourceURLs){
            params.add("sourceURL", sourceURL);
        }
        for (String targetURL:targetURLs){
            params.add("targetURL", targetURL);
        }
        for (String nameSpace:nameSpaces){
            params.add("nameSpace", nameSpace);
        }
        for (String sourceNameSpace:sourceNameSpaces){
            params.add("sourceNameSpace", sourceNameSpace);
        }
        for (String targetNameSpace:targetNameSpaces){
            params.add("targetNameSpace", targetNameSpace);
        }
        for (String provenanceIdString:provenanceIdStrings){
            params.add("provenanceId", provenanceIdString);
        }
        if (positionString != null){
           params.add("position", positionString);            
        }
        if (limitString != null){
           params.add("limit", limitString);            
        }
        if (full != null){
           params.add("full", ""+full);            
        }
        List<URLMappingBean> result = 
                webResource.path("getMappings")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<URLMappingBean>>() {});
        return result;        
    }

    @Override
    public URLMappingBean getMapping(String idString) {
        URLMappingBean result = 
                webResource.path("getMapping/" + idString)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<URLMappingBean>() {});
        return result;        
    }

    @Override
    public OverallStatisticsBean getOverallStatistics() throws IDMapperException {
       OverallStatisticsBean result = 
                webResource.path("getOverallStatistics")
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<OverallStatisticsBean>() {});
        return result;        
 
    }

    /* Removed due to scale issues
    @Override
    public List<XrefBean> getXrefs(ArrayList<String> dataSourceSysCodes, List<String> provenanceIdStrings, 
            String positionString, String limitString) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        for (String dataSourceSysCode:dataSourceSysCodes){
            params.add("dataSourceSysCode", dataSourceSysCode);
        }
        for (String provenanceIdString:provenanceIdStrings){
            params.add("provenanceId", provenanceIdString);
        }
        if (positionString != null){
           params.add("position", positionString);            
        }
        if (limitString != null){
           params.add("limit", limitString);            
        }
        List<XrefBean> result = 
                webResource.path("getXrefs")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<XrefBean>>() {});
        return result;        
    }

    @Override
    public List<URLBean> getURLs(List<String> nameSpaces, List<String> provenanceIdStrings, 
            String positionString, String limitString) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        for (String nameSpace:nameSpaces){
            params.add("nameSpace", nameSpace);
        }
        for (String provenanceIdString:provenanceIdStrings){
            params.add("provenanceId", provenanceIdString);
        }
        if (positionString != null){
           params.add("position", positionString);            
        }
        if (limitString != null){
           params.add("limit", limitString);            
        }
        List<URLBean> result = 
                webResource.path("getURLs")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<URLBean>>() {});
        return result;        
    }*/

    @Override
    public List<ProvenanceBean> getProvenanceInfos() throws IDMapperException {
        List<ProvenanceBean> result = 
                webResource.path("getProvenanceInfos")
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<ProvenanceBean>>() {});
        return result;        
    }

    @Override
    public ProvenanceBean getProvenanceInfo(String id) throws IDMapperException {
        ProvenanceBean result = 
                webResource.path("getProvenanceInfo/" + id)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<ProvenanceBean>() {});
        return result;        
    }

    @Override
    public List<URLBean> getSampleSourceURLs() throws IDMapperException {
        List<URLBean> result = 
                webResource.path("getSampleSourceURLs")
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<URLBean>>() {});
        return result;        
    }

    @Override
    public List<URLBean> getLinksetNames() throws IDMapperException {
        List<URLBean> result = 
                webResource.path("getLinksetNames")
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<URLBean>>() {});
        return result;        
    }

    @Override
    public String linkset(String idString) throws IDMapperException {
        String result = 
                webResource.path("getRDF/" + idString)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<String>() {});
        return result;        
    }

}
