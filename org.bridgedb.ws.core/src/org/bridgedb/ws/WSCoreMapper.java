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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.Response;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.Xref;
import org.bridgedb.utils.BridgeDBException;
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
 *
 * @author Christian
 */
public class WSCoreMapper implements IDMapper, IDMapperCapabilities {

    WSCoreInterface webService;
    static final int NO_CONTEXT = Response.Status.NO_CONTENT.getStatusCode();
    
    public WSCoreMapper(WSCoreInterface webService){
        this.webService = webService;
    }
    
    //**** IDMApper functions *****
    @Override
    public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources) throws BridgeDBException {
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> codes = new ArrayList<String>();
        ArrayList<String> targetCodes = new ArrayList<String>();
        for (Xref srcXref:srcXrefs){
            if (srcXref.getId() != null && srcXref.getDataSource() != null){
                ids.add(srcXref.getId());
                codes.add(srcXref.getDataSource().getSystemCode());
            }
        }
        for (int i = 0 ; i < tgtDataSources.length; i++){
            targetCodes.add(tgtDataSources[i].getSystemCode());
        }
        if (codes.isEmpty()) return new HashMap<Xref, Set<Xref>>(); //No valid srcrefs so return empty set
        Response response = webService.mapID(ids, codes, targetCodes);
        if (response.getStatus() == NO_CONTEXT){
            return new HashMap<Xref, Set<Xref>>();
        }
        XrefMapsBean bean = (XrefMapsBean)response.getEntity();
        return bean.asMappings();
    }

    @Override
    public Set<Xref> mapID(Xref ref, DataSource... tgtDataSources) throws BridgeDBException {
        if (ref.getId() == null || ref.getDataSource() == null) return new HashSet<Xref>();
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> codes = new ArrayList<String>();
        ids.add(ref.getId());
        codes.add(ref.getDataSource().getSystemCode());
        ArrayList<String> targetCodes = new ArrayList<String>();
        for (int i = 0 ; i < tgtDataSources.length; i++){
            targetCodes.add(tgtDataSources[i].getSystemCode());
        }
        Response response = webService.mapID(ids, codes, targetCodes);
        if (response.getStatus() == NO_CONTEXT){
            return new HashSet<Xref>();
        }
        XrefMapsBean bean = (XrefMapsBean)response.getEntity();
        return bean.getTargetXrefs();
    }

    @Override
    public boolean xrefExists(Xref xref) throws BridgeDBException {
        if (xref.getId() == null) return false;
        if (xref.getDataSource() == null) return false;
        String id = xref.getId();
        String code = xref.getDataSource().getSystemCode();
        Response response = webService.xrefExists(id,code);
        XrefExistsBean bean = (XrefExistsBean)response.getEntity();
        return bean.exists();
    }

    @Override
    public Set<Xref> freeSearch(String text, int limit) throws BridgeDBException {
        Response response = webService.freeSearch(text, "" + limit);
        XrefsBean bean =  (XrefsBean)response.getEntity();
        if (response.getStatus() == NO_CONTEXT){
            return new HashSet<Xref>();
        }
        return bean.asXrefs();
    }

    @Override
    public IDMapperCapabilities getCapabilities() {
        Response response = webService.getCapabilities();
        CapabilitiesBean bean = (CapabilitiesBean)response.getEntity(); 
        return bean.asIDMapperCapabilities();
    }

    private boolean isConnected = true;
    // In the case of DataCollection, there is no need to discard associated resources.
    
    @Override
    /** {@inheritDoc} */
    public void close() throws BridgeDBException { 
        isConnected = false; 
    }
 
    @Override
    /** {@inheritDoc} */
    public boolean isConnected() { 
        if (isConnected) {
            try{
                webService.isFreeSearchSupported();
                return true; 
            } catch (Exception ex) {
                return false;
            }
        } 
        return false;
    }

    @Override
    public boolean isFreeSearchSupported() {
        Response response = webService.isFreeSearchSupported();
        FreeSearchSupportedBean bean = (FreeSearchSupportedBean)response.getEntity();
        return bean.isFreeSearchSupported();
    }

    @Override
    public Set<DataSource> getSupportedSrcDataSources() throws BridgeDBException {
        Response response = webService.getSupportedSrcDataSources();
        DataSourcesBean beans = (DataSourcesBean)response.getEntity();
        if (response.getStatus() == NO_CONTEXT){
            return new HashSet<DataSource>();
        }
        return beans.getDataSources();
    }

    @Override
    public Set<DataSource> getSupportedTgtDataSources() throws BridgeDBException {
        Response response = webService.getSupportedTgtDataSources();
        if (response.getStatus() == NO_CONTEXT){
            return new HashSet<DataSource>();
        }
        DataSourcesBean beans = (DataSourcesBean)response.getEntity();
        return beans.getDataSources();
    }

    @Override
    public boolean isMappingSupported(DataSource src, DataSource tgt) throws BridgeDBException {
        Response response = webService.isMappingSupported(src.getSystemCode(), tgt.getSystemCode());
        MappingSupportedBean bean = (MappingSupportedBean)response.getEntity();
        return bean.isMappingSupported();
    }

    @Override
    public String getProperty(String key) {
        Response response = webService.getProperty(key);
        if (response.getStatus() == NO_CONTEXT){
            return null;
        }
        PropertyBean bean = (PropertyBean)response.getEntity();
        if (bean == null) return null;
        return bean.getValue();
    }

    @Override
    public Set<String> getKeys() {
        Response response = webService.getKeys();
        PropertiesBean beans = (PropertiesBean)response.getEntity();
        if (response.getStatus() == NO_CONTEXT){
            return new HashSet<String>();
        }
        return beans.getKeys();
    }

}
