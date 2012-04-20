/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import java.util.ArrayList;
import java.util.List;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.bridgedb.IDMapperException;
import org.bridgedb.ws.bean.OverallStatisticsBean;
import org.bridgedb.ws.bean.URLBean;
import org.bridgedb.ws.bean.URLMappingBean;
import org.bridgedb.ws.bean.XrefBean;

/**
 *
 * @author Christian
 */
public class WSClient extends WSCoreClient implements WSInterface{

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
    }
/*    @Override
    public URLMappingBeanImpl getMapping(Integer id) throws IDMapperException {
       //Make service call
        URLMappingBeanImpl result = 
                webResource.path("getMapping/" + id)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<URLMappingBean>() {});
        return result;
    }

    @Override
    public List<URLMappingBean> getURLMappings(String URL, List<String> targetNameSpace) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("URL",  URL);
        for (String target:targetNameSpace){
            params.add("targetNameSpace", target);
        }
       //Make service call
        List<URLMappingBean> result = 
                webResource.path("getURLMappings")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<URLMappingBean>>() {});
        return result;
    }

    @Override
    public ProvenanceStatisticsBean getProvenance(Integer id) throws IDMapperException {
       //Make service call
        ProvenanceStatisticsBean result = 
                webResource.path("getProvenance/" + id)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<ProvenanceStatisticsBean>() {});
        return result;
    }

    @Override
    public ProvenanceStatisticsBean getProvenanceByPosition(Integer position) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("position",  position.toString());
       //Make service call
        ProvenanceStatisticsBean result = 
                webResource.path("getProvenanceByPosition")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<ProvenanceStatisticsBean>() {});
        return result;
    }

    @Override
    public List<ProvenanceStatisticsBean> getProvenanceByPosition(Integer position, Integer limit) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("position",  position.toString());
        if (limit != null){
            params.add("limit",  limit.toString());
        }
        List<ProvenanceStatisticsBean> result = 
                webResource.path("getProvenancesByPosition")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<ProvenanceStatisticsBean>>() {});
        return result;
    }

    @Override
    public List<ProvenanceStatisticsBean> getSourceProvenanceByNameSpace(String nameSpace) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("nameSpace",  nameSpace);
        List<ProvenanceStatisticsBean> result = 
                webResource.path("getSourceProvenanceByNameSpace")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<ProvenanceStatisticsBean>>() {});
        return result;
    }

    @Override
    public List<ProvenanceStatisticsBean> getTargetProvenanceByNameSpace(String nameSpace) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("nameSpace",  nameSpace);;
        List<ProvenanceStatisticsBean> result = 
                webResource.path("getTargetProvenanceByNameSpace")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<ProvenanceStatisticsBean>>() {});
        return result;
    }
*/
   /* @Override
    public DataSourceStatisticsBean getDataSourceStatistics(String code) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("code",  code);
        DataSourceStatisticsBean result = 
                webResource.path("getDataSourceStatistics")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<DataSourceStatisticsBean>() {});
        return result;
    }

    @Override
    public DataSourceStatisticsBean getDataSourceStatisticsByPosition(Integer position) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("position",  position.toString());
        DataSourceStatisticsBean result = 
                webResource.path("getDataSourceStatisticsByAPosition")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<DataSourceStatisticsBean>() {});
        return result;
    }

    @Override
    public List<DataSourceStatisticsBean> getDataSourceStatisticsByPosition(Integer position, Integer limit) throws IDMapperException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("position",  position.toString());
        params.add("limit",  limit.toString());
        List<DataSourceStatisticsBean> result = 
                webResource.path("getDataSourceStatisticsByPosition")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<DataSourceStatisticsBean>>() {});
        return result;
    }
*/

}
