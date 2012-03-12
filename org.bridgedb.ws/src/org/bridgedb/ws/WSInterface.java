/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
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
public interface WSInterface {

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path(value = "/freeSearch")
    List<XrefBean> freeSearch(
            @QueryParam(value = "text") String text, 
            @QueryParam(value = "limit") Integer limit) throws IDMapperException;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path(value = "/getDataSource/{code}")
    DataSourceBean getDataSoucre(@PathParam(value = "code") String code) throws IDMapperException;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path(value = "/getKeys")
    List<PropertyBean> getKeys();

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path(value = "/getProperty/{key}")
    PropertyBean getProperty(@PathParam(value = "key") String key);

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path(value = "/getSupportedSrcDataSources")
    List<DataSourceBean> getSupportedSrcDataSources() throws IDMapperException;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path(value = "/getTargets")
    List<DataSourceBean> getSupportedTgtDataSources() throws IDMapperException;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path(value = "/isFreeSearchSupported")
    FreeSearchSupportedBean isFreeSearchSupported();

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path(value = "/isMappingSupported")
    MappingSupportedBean isMappingSupported(
            @QueryParam(value = "source") String srcCode, 
            @QueryParam(value = "target") String tgtCode) throws IDMapperException;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path(value = "/mapByXRef")
    List<XrefBean> mapByXref(
            @QueryParam(value = "id") String id, 
            @QueryParam(value = "code") String scrCode, 
            @QueryParam(value = "tgtCode") List<String> targetCodes) throws IDMapperException;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path(value = "/mapByXRefs")
    List<XRefMapBean> mapByXrefs(
            @QueryParam(value = "id") List<String> id, 
            @QueryParam(value = "code") List<String> scrCode, 
            @QueryParam(value = "tgtCode") List<String> targetCodes) throws IDMapperException;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path(value = "/xrefExists")
    XrefExistsBean xrefExists(
            @QueryParam(value = "id") String id, 
            @QueryParam(value = "code") String scrCode) throws IDMapperException;
 
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getCapabilities")
    CapabilitiesBean getCapabilities();

}
