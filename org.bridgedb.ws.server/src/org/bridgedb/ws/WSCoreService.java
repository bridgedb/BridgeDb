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
package org.bridgedb.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.pairs.SyscodeBasedCodeMapper;
import org.bridgedb.sql.SQLIdMapper;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.ws.bean.CapabilitiesBean;
import org.bridgedb.ws.bean.DataSourcesBean;
import org.bridgedb.ws.bean.FreeSearchSupportedBean;
import org.bridgedb.ws.bean.MappingSupportedBean;
import org.bridgedb.ws.bean.PropertiesBean;
import org.bridgedb.ws.bean.PropertyBean;
import org.bridgedb.ws.bean.XrefExistsBean;
import org.bridgedb.ws.bean.XrefMapsBean;
import org.bridgedb.ws.bean.XrefsBean;

/**
 * Webservice server code, that uses the ws.core
 * functionality to expose BridgeDB data
 * @author Christian Y. A. Brenninkmeijer
 *
 */
@Path("/")
public class WSCoreService implements WSCoreInterface {

    static final String NO_CONTENT_ON_EMPTY = "no.content.on.empty";
    protected final boolean noConentOnEmpty;
            
    static final Logger logger = Logger.getLogger(WSCoreService.class);
    
    protected IDMapper idMapper;

    /**
     * Default constructor for super classes.
     * 
     * Super classes will have the responsibilities of setting up the idMapper.
     */
    private WSCoreService() throws BridgeDBException{
        this(new SQLIdMapper(false, new SyscodeBasedCodeMapper()));
    }
    
    public WSCoreService(IDMapper idMapper) throws BridgeDBException {
        this.idMapper = idMapper;
        String property = ConfigReader.getProperty(NO_CONTENT_ON_EMPTY);
        noConentOnEmpty = Boolean.valueOf(property);
        logger.info("WS Service running using supplied idMapper");
    }
        
    private DataSourcesBean getSupportedSrcDataSourcesInner() throws BridgeDBException {
        IDMapperCapabilities capabilities = idMapper.getCapabilities();
        try {
            Set<DataSource> dataSources = capabilities.getSupportedSrcDataSources();
            return new DataSourcesBean (dataSources);
        } catch (IDMapperException e){
            throw BridgeDBException.convertToBridgeDB(e);
        }
    } 

    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsConstants.GET_SUPPORTED_SOURCE_DATA_SOURCES)
    @Override
    public Response getSupportedSrcDataSources() throws BridgeDBException {
        DataSourcesBean bean = getSupportedSrcDataSourcesInner();
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        }
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    } 

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + WsConstants.GET_SUPPORTED_SOURCE_DATA_SOURCES)
    public Response getSupportedSrcDataSourcesJson() throws BridgeDBException {
        DataSourcesBean bean = getSupportedSrcDataSourcesInner();
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        }
        return Response.ok(bean, MediaType.APPLICATION_JSON_TYPE).build();
    } 

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + WsConstants.GET_SUPPORTED_SOURCE_DATA_SOURCES)
    public Response getSupportedSrcDataSources(@Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        DataSourcesBean bean = getSupportedSrcDataSourcesInner();
        if (noConentOnEmpty & bean.isEmpty()){
            noContentWrapper(httpServletRequest);
        }
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    } 
    
    private XrefsBean freeSearchInner(String text, String limitString) throws BridgeDBException {
        if (text == null) {
            throw new BridgeDBException(WsConstants.TEXT + " parameter missing");
        }
        Set<Xref> mappings;
        try {
            if (limitString == null || limitString.isEmpty()){
                mappings = idMapper.freeSearch(text, Integer.MAX_VALUE);
           } else {
                int limit = Integer.parseInt(limitString);
                mappings = idMapper.freeSearch(text,limit);
            }
        } catch (IDMapperException e){
            throw BridgeDBException.convertToBridgeDB(e);
        }
        return new XrefsBean(mappings);
    } 

    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsConstants.FREE_SEARCH)
    @Override
    public Response freeSearch(
            @QueryParam(WsConstants.TEXT) String text,
            @QueryParam(WsConstants.LIMIT) String limitString) throws BridgeDBException {
        XrefsBean bean = freeSearchInner(text, limitString);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        }
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    } 
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + WsConstants.FREE_SEARCH)
    public Response freeSearchJson(
            @QueryParam(WsConstants.TEXT) String text,
            @QueryParam(WsConstants.LIMIT) String limitString) throws BridgeDBException {
        XrefsBean bean = freeSearchInner(text, limitString);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        }
        return Response.ok(bean, MediaType.APPLICATION_JSON_TYPE).build();
    } 

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + WsConstants.FREE_SEARCH)
    public Response freeSearch(
            @QueryParam(WsConstants.TEXT) String text,
            @QueryParam(WsConstants.LIMIT) String limitString,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        XrefsBean bean = freeSearchInner(text, limitString);
        if (noConentOnEmpty & bean.isEmpty()){
            return noContentWrapper(httpServletRequest);
        }
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    } 

    private XrefMapsBean mapIDInner(List<String> id, List<String> scrCode, List<String> targetCodes) throws BridgeDBException {
        if (id == null) {
            throw new BridgeDBException(WsConstants.ID + " parameter missing");
        }
        if (id.isEmpty()) {
            throw new BridgeDBException(WsConstants.ID + " parameter missing");
        }
        if (scrCode == null) {
            throw new BridgeDBException(WsConstants.DATASOURCE_SYSTEM_CODE + " parameter missing");
        }
        if (scrCode.isEmpty()) {
            throw new BridgeDBException(WsConstants.DATASOURCE_SYSTEM_CODE + " parameter missing");
        }
        if (id.size() != scrCode.size()) {
            throw new BridgeDBException("Must have same number of " + WsConstants.ID + 
                " and " + WsConstants.DATASOURCE_SYSTEM_CODE + " parameters");
        }
        ArrayList<Xref> srcXrefs = new ArrayList<Xref>();
        for (int i = 0; i < id.size() ;i++){
            try {
                DataSource dataSource = DataSource.getExistingBySystemCode(scrCode.get(i));
                Xref source = new Xref(id.get(i), dataSource);
                srcXrefs.add(source);
            } catch (IllegalArgumentException ex){
                logger.error(ex.getMessage());
            }
        }
        DataSource[] targetDataSources = new DataSource[targetCodes.size()];
        for (int i=0; i< targetCodes.size(); i++){
             targetDataSources[i] = DataSource.getExistingBySystemCode(targetCodes.get(i));
        }
        
        try {
            Map<Xref, Set<Xref>>  mappings = idMapper.mapID(srcXrefs, targetDataSources);
            return new XrefMapsBean(mappings);
        } catch (IDMapperException e){
            throw BridgeDBException.convertToBridgeDB(e);
        }
    } 
    
    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsConstants.MAP_ID)
    @Override
    public Response mapID(
            @QueryParam(WsConstants.ID) List<String> id,
            @QueryParam(WsConstants.DATASOURCE_SYSTEM_CODE) List<String> scrCode,
            @QueryParam(WsConstants.TARGET_DATASOURCE_SYSTEM_CODE) List<String> targetCodes) throws BridgeDBException {
        XrefMapsBean bean = mapIDInner(id, scrCode, targetCodes);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        }
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    } 

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + WsConstants.MAP_ID)
    public Response mapIDJson(
            @QueryParam(WsConstants.ID) List<String> id,
            @QueryParam(WsConstants.DATASOURCE_SYSTEM_CODE) List<String> scrCode,
            @QueryParam(WsConstants.TARGET_DATASOURCE_SYSTEM_CODE) List<String> targetCodes) throws BridgeDBException {
        XrefMapsBean bean = mapIDInner(id, scrCode, targetCodes);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        }
        return Response.ok(bean, MediaType.APPLICATION_JSON_TYPE).build();
    } 

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + WsConstants.MAP_ID)
    public Response mapID(
            @QueryParam(WsConstants.ID) List<String> id,
            @QueryParam(WsConstants.DATASOURCE_SYSTEM_CODE) List<String> scrCode,
            @QueryParam(WsConstants.TARGET_DATASOURCE_SYSTEM_CODE) List<String> targetCodes,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        XrefMapsBean bean = mapIDInner(id, scrCode, targetCodes);
        if (noConentOnEmpty & bean.isEmpty()){
            return noContentWrapper(httpServletRequest);
        }
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    } 

    private XrefExistsBean xrefExistsInner(String id, String scrCode) throws BridgeDBException {
        if (id == null) {
            throw new BridgeDBException (WsConstants.ID + " parameter can not be null");
        }
        if (scrCode == null) {
            throw new BridgeDBException (WsConstants.DATASOURCE_SYSTEM_CODE + " parameter can not be null");
        }  
        DataSource dataSource;
        try {
            dataSource = DataSource.getExistingBySystemCode(scrCode);
        } catch (IllegalArgumentException ex){
             logger.error(ex.getMessage());
             return new XrefExistsBean(id, scrCode, false);           
        }
        Xref source = new Xref(id, dataSource);
        try {
            return new XrefExistsBean(source, idMapper.xrefExists(source));
        } catch (IDMapperException e){
            throw BridgeDBException.convertToBridgeDB(e);
        }
    }
    
    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsConstants.XREF_EXISTS)
    @Override
    public Response xrefExists( 
            @QueryParam(WsConstants.ID) String id,
            @QueryParam(WsConstants.DATASOURCE_SYSTEM_CODE) String scrCode) throws BridgeDBException {
        XrefExistsBean bean = xrefExistsInner(id, scrCode);
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();            
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + WsConstants.XREF_EXISTS)
    public Response xrefExistsJson( 
            @QueryParam(WsConstants.ID) String id,
            @QueryParam(WsConstants.DATASOURCE_SYSTEM_CODE) String scrCode) throws BridgeDBException {
        XrefExistsBean bean = xrefExistsInner(id, scrCode);
        return Response.ok(bean, MediaType.APPLICATION_JSON_TYPE).build();            
    }

   private DataSourcesBean getSupportedTgtDataSourcesInner() throws BridgeDBException {
        try {
            Set<DataSource> dataSources = idMapper.getCapabilities().getSupportedSrcDataSources();
            return new DataSourcesBean(dataSources);
        } catch (IDMapperException e){
            throw BridgeDBException.convertToBridgeDB(e);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsConstants.GET_SUPPORTED_TARGET_DATA_SOURCES)
    @Override
    public Response getSupportedTgtDataSources() throws BridgeDBException {
        DataSourcesBean bean = getSupportedTgtDataSourcesInner();
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        }
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + WsConstants.GET_SUPPORTED_TARGET_DATA_SOURCES)
    public Response getSupportedTgtDataSourcesJson() throws BridgeDBException {
        DataSourcesBean bean = getSupportedTgtDataSourcesInner();
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        }
        return Response.ok(bean, MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + WsConstants.GET_SUPPORTED_TARGET_DATA_SOURCES)
    public Response getSupportedTgtDataSources(@Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        DataSourcesBean bean = getSupportedTgtDataSourcesInner();
        if (noConentOnEmpty & bean.isEmpty()){
            return noContentWrapper(httpServletRequest);
        }
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path(WsConstants.IS_FREE_SEARCH_SUPPORTED)
    @Override
    public Response isFreeSearchSupported() {
        FreeSearchSupportedBean bean = new FreeSearchSupportedBean(idMapper.getCapabilities().isFreeSearchSupported());
        //FreeSearchSupported is never empty so never no context
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path(WsConstants.IS_FREE_SEARCH_SUPPORTED)
    public Response isFreeSearchSupportedJson() {
        FreeSearchSupportedBean bean = new FreeSearchSupportedBean(idMapper.getCapabilities().isFreeSearchSupported());
        //FreeSearchSupported is never empty so never no context
        return Response.ok(bean, MediaType.APPLICATION_JSON_TYPE).build();
    }

    private MappingSupportedBean isMappingSupportedInner(String sourceCode, String targetCode) throws BridgeDBException {
        if (sourceCode == null) {
            throw new BridgeDBException (WsConstants.SOURCE_DATASOURCE_SYSTEM_CODE + " parameter can not be null");
        }
        if (targetCode == null) {
            throw new BridgeDBException (WsConstants.TARGET_DATASOURCE_SYSTEM_CODE + " parameter can not be null");
        }
        DataSource src = DataSource.getExistingBySystemCode(sourceCode);
        DataSource tgt = DataSource.getExistingBySystemCode(targetCode);
        try {
            return new MappingSupportedBean(src, tgt, idMapper.getCapabilities().isMappingSupported(src, tgt));
        } catch (IDMapperException e){
            throw BridgeDBException.convertToBridgeDB(e);
        }     
    }

    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsConstants.IS_MAPPING_SUPPORTED)
    @Override
    public Response isMappingSupported(
            @QueryParam(WsConstants.SOURCE_DATASOURCE_SYSTEM_CODE) String sourceCode, 
            @QueryParam(WsConstants.TARGET_DATASOURCE_SYSTEM_CODE) String targetCode) throws BridgeDBException {
        MappingSupportedBean bean = isMappingSupportedInner(sourceCode, targetCode); 
        //MappingSupported is never empty so never no content
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsConstants.IS_MAPPING_SUPPORTED)
    public Response isMappingSupportedJson(
            @QueryParam(WsConstants.SOURCE_DATASOURCE_SYSTEM_CODE) String sourceCode, 
            @QueryParam(WsConstants.TARGET_DATASOURCE_SYSTEM_CODE) String targetCode) throws BridgeDBException {
        MappingSupportedBean bean = isMappingSupportedInner(sourceCode, targetCode); 
        //MappingSupported is never empty so never no content
        return Response.ok(bean, MediaType.APPLICATION_JSON_TYPE).build();
    }

    private PropertyBean getPropertyInner(String key) {
        String property = idMapper.getCapabilities().getProperty(key);
        return new PropertyBean(key, property);
    }
    
    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsConstants.PROPERTY + "/{key}")
    @Override
    public Response getProperty(@PathParam("key")String key) {
        PropertyBean bean = getPropertyInner(key);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        }
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + WsConstants.PROPERTY + "/{key}")
    public Response getPropertyJson(@PathParam("key")String key) {
        PropertyBean bean = getPropertyInner(key);
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        }
        return Response.ok(bean, MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsConstants.PROPERTY + "/{key}")
    public Response getProperty(@PathParam("key")String key,
            @Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        PropertyBean bean = getPropertyInner(key);
        if (noConentOnEmpty & bean.isEmpty()){
            return noContentWrapper(httpServletRequest);
        }
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    }

    private PropertiesBean getKeysInner() {
        PropertiesBean bean = new PropertiesBean();
        Set<String> keys = idMapper.getCapabilities().getKeys();
        IDMapperCapabilities idMapperCapabilities = idMapper.getCapabilities();
        for (String key:keys){
            bean.addProperty(key, idMapperCapabilities.getProperty(key));
        }
        return bean;
    }

    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsConstants.GET_KEYS)
    @Override
    public Response getKeys() {
        PropertiesBean bean = getKeysInner();
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        }
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + WsConstants.GET_KEYS)
    public Response getKeysJson() {
        PropertiesBean bean = getKeysInner();
        if (noConentOnEmpty & bean.isEmpty()){
            return Response.noContent().build();
        }
        return Response.ok(bean, MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/" + WsConstants.GET_KEYS)
    public Response getKeys(@Context HttpServletRequest httpServletRequest) throws BridgeDBException {
        PropertiesBean bean = getKeysInner();
        if (noConentOnEmpty & bean.isEmpty()){
            return noContentWrapper(httpServletRequest);
        }
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Path("/" + WsConstants.GET_CAPABILITIES)
    @Override
    public Response getCapabilities()  {
        CapabilitiesBean bean = new CapabilitiesBean(idMapper.getCapabilities());
        return Response.ok(bean, MediaType.APPLICATION_XML_TYPE).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/" + WsConstants.GET_CAPABILITIES)
    public Response getCapabilitiesJson()  {
        CapabilitiesBean bean = new CapabilitiesBean(idMapper.getCapabilities());
        return Response.ok(bean, MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Simple warning that no Context was found.
     * 
     * Can be overwritten with nicer page
     * @param httpServletRequest Used by super classes
     * @return
     * @throws BridgeDBException thrown by super classes
     */
    protected Response noContentWrapper(HttpServletRequest httpServletRequest) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" ");
        sb.append("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\"/>");
        sb.append("<head>\n");
        sb.append("<title>No Results found</title>\n");
        sb.append("</head>\n<body>\n");
        sb.append("<h1>Sorry no results found!</h1>\n");
        sb.append("<h2>The parameters provided resulted in an empty set.</h2>\n");
        sb.append("<h3>No exception was thrown so the parameters where valid just too restrictive.</h3>\n");
        sb.append("<h2>XML and json version return status 204 in this case</h2>\n");
        sb.append("</body></html>");
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }
}
