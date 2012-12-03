// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
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

import org.bridgedb.ws.bean.XrefBean;
import org.bridgedb.ws.bean.XrefMapBean;
import org.bridgedb.ws.bean.CapabilitiesBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.ws.bean.DataSourceBean;
import org.bridgedb.ws.bean.DataSourceBeanFactory;
import org.bridgedb.ws.bean.FreeSearchSupportedBean;
import org.bridgedb.ws.bean.MappingSupportedBean;
import org.bridgedb.ws.bean.MappingSupportedBeanFactory;
import org.bridgedb.ws.bean.PropertyBean;
import org.bridgedb.ws.bean.XrefBeanFactory;
import org.bridgedb.ws.bean.XrefExistsBean;
import org.bridgedb.ws.bean.XrefExistsBeanFactory;
import org.bridgedb.ws.bean.XrefMapBeanFactory;

@Path("/")
public class WSCoreService implements WSCoreInterface {

    static final Logger logger = Logger.getLogger(WSCoreService.class);

    protected IDMapper idMapper;

    /**
     * Defuault constuctor for super classes.
     * 
     * Super classes will have the responsibilites of setting up the idMapper.
     */
    protected WSCoreService() throws IDMapperException{
        ConfigReader.configureLogger();
    }
    
    public WSCoreService(IDMapper idMapper) throws IDMapperException {
        this.idMapper = idMapper;
        ConfigReader.configureLogger();
        logger.info("WS Service running using supplied urlMapper");
    }
        
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/" + WsConstants.GET_SUPPORTED_SOURCE_DATA_SOURCES)
    @Override
    public List<DataSourceBean> getSupportedSrcDataSources() throws IDMapperException {
        ArrayList<DataSourceBean> sources = new ArrayList<DataSourceBean>();
        System.err.println(idMapper);
        IDMapperCapabilities capabilities = idMapper.getCapabilities();
        Set<DataSource> dataSources = capabilities.getSupportedSrcDataSources();
        for (DataSource dataSource:dataSources){
            DataSourceBean bean = DataSourceBeanFactory.asBean(dataSource);
            sources.add(bean);
        }
        return sources;
    } 

    
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/" + WsConstants.FREE_SEARCH)
    @Override
    public List<XrefBean> freeSearch(
            @QueryParam(WsConstants.TEXT) String text,
            @QueryParam(WsConstants.LIMIT) String limitString) throws IDMapperException {
        if (text == null) throw new BridgeDBException(WsConstants.TEXT + " parameter missing");
        if (limitString == null || limitString.isEmpty()){
            Set<Xref> mappings = idMapper.freeSearch(text, Integer.MAX_VALUE);
            return setXrefToListXrefBeans(mappings);
        } else {
            int limit = Integer.parseInt(limitString);
            Set<Xref> mappings = idMapper.freeSearch(text,limit);
            return setXrefToListXrefBeans(mappings);
        }
    } 

    protected List<XrefBean> setXrefToListXrefBeans(Set<Xref> xrefs){
       ArrayList<XrefBean> results = new ArrayList<XrefBean>();
        for (Xref xref:xrefs){
           results.add(XrefBeanFactory.asBean(xref));
        }
        return results;        
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/" + WsConstants.MAP_ID)
    @Override
    public List<XrefMapBean> mapID(
            @QueryParam(WsConstants.ID) List<String> id,
            @QueryParam(WsConstants.SOURCE_DATASOURCE_SYSTEM_CODE) List<String> scrCode,
            @QueryParam(WsConstants.TARGET_DATASOURCE_SYSTEM_CODE) List<String> targetCodes) throws IDMapperException {
        if (id == null) throw new BridgeDBException(WsConstants.ID + " parameter missing");
        if (id.isEmpty()) throw new BridgeDBException(WsConstants.ID + " parameter missing");
        if (scrCode == null) throw new BridgeDBException(WsConstants.SOURCE_DATASOURCE_SYSTEM_CODE + " parameter missing");
        if (scrCode.isEmpty()) throw new BridgeDBException(WsConstants.SOURCE_DATASOURCE_SYSTEM_CODE + " parameter missing");
        if (id.size() != scrCode.size()) throw new BridgeDBException("Must have same number of " + WsConstants.ID + 
                " and " + WsConstants.SOURCE_DATASOURCE_SYSTEM_CODE + " parameters");
        ArrayList<Xref> srcXrefs = new ArrayList<Xref>();
        for (int i = 0; i < id.size() ;i++){
            DataSource dataSource = DataSource.getBySystemCode(scrCode.get(i));
            Xref source = new Xref(id.get(i), dataSource);
            srcXrefs.add(source);
        }
        DataSource[] targetDataSources = new DataSource[targetCodes.size()];
        for (int i=0; i< targetCodes.size(); i++){
             targetDataSources[i] = DataSource.getBySystemCode(targetCodes.get(i));
        }
        
        Map<Xref, Set<Xref>>  mappings = idMapper.mapID(srcXrefs, targetDataSources);
        ArrayList<XrefMapBean> results = new ArrayList<XrefMapBean>();
        for (Xref source:mappings.keySet()){
            for (Xref target:mappings.get(source)){
                results.add(XrefMapBeanFactory.asBean(source, target));
            }
        }
        return results;
    } 

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/" + WsConstants.XREF_EXISTS)
    @Override
    public XrefExistsBean xrefExists( 
            @QueryParam(WsConstants.ID) String id,
            @QueryParam(WsConstants.DATASOURCE_SYSTEM_CODE) String scrCode) throws IDMapperException {
        if (id == null) throw new BridgeDBException (WsConstants.ID + " parameter can not be null");
        if (scrCode == null) throw new BridgeDBException (WsConstants.DATASOURCE_SYSTEM_CODE + " parameter can not be null");            
        DataSource dataSource = DataSource.getBySystemCode(scrCode);
        Xref source = new Xref(id, dataSource);
        return XrefExistsBeanFactory.asBean(source, idMapper.xrefExists(source));
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/" + WsConstants.GET_SUPPORTED_TARGET_DATA_SOURCES)
    @Override
    public List<DataSourceBean> getSupportedTgtDataSources() throws IDMapperException {
        ArrayList<DataSourceBean> targets = new ArrayList<DataSourceBean>();
        Set<DataSource> dataSources = idMapper.getCapabilities().getSupportedSrcDataSources();
        for (DataSource dataSource:dataSources){
            DataSourceBean bean = DataSourceBeanFactory.asBean(dataSource);
            targets.add(bean);
        }
        return targets;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path(WsConstants.IS_FREE_SEARCH_SUPPORTED)
    @Override
    public FreeSearchSupportedBean isFreeSearchSupported() {
        return new FreeSearchSupportedBean(idMapper.getCapabilities().isFreeSearchSupported());
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/" + WsConstants.IS_MAPPING_SUPPORTED)
    @Override
    public MappingSupportedBean isMappingSupported(
            @QueryParam(WsConstants.SOURCE_DATASOURCE_SYSTEM_CODE) String sourceCode, 
            @QueryParam(WsConstants.TARGET_DATASOURCE_SYSTEM_CODE) String targetCode) throws IDMapperException {
        if (sourceCode == null) throw new BridgeDBException (WsConstants.SOURCE_DATASOURCE_SYSTEM_CODE + " parameter can not be null");
        if (targetCode == null) throw new BridgeDBException (WsConstants.TARGET_DATASOURCE_SYSTEM_CODE + " parameter can not be null");
        DataSource src = DataSource.getBySystemCode(sourceCode);
        DataSource tgt = DataSource.getBySystemCode(targetCode);
        return MappingSupportedBeanFactory.asBean(src, tgt, idMapper.getCapabilities().isMappingSupported(src, tgt));
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/" + WsConstants.PROPERTY + "/{key}")
    @Override
    public PropertyBean getProperty(@PathParam("key")String key) {
        String property = idMapper.getCapabilities().getProperty(key);
        return new PropertyBean(key, property);
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/" + WsConstants.GET_KEYS)
    @Override
    public List<PropertyBean> getKeys() {
        Set<String> keys = idMapper.getCapabilities().getKeys();
        ArrayList<PropertyBean> results = new ArrayList<PropertyBean>();
        IDMapperCapabilities idMapperCapabilities = idMapper.getCapabilities();
        for (String key:keys){
            results.add(new PropertyBean(key, idMapperCapabilities.getProperty(key)));
        }
        return results;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/" + WsConstants.GET_CAPABILITIES)
    @Override
    public CapabilitiesBean getCapabilities()  {
        return new CapabilitiesBean(idMapper.getCapabilities());
    }


}
