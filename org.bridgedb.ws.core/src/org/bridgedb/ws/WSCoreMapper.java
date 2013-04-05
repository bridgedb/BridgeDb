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
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.Xref;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.ws.bean.CapabilitiesBean;
import org.bridgedb.ws.bean.DataSourceBean;
import org.bridgedb.ws.bean.PropertyBean;
import org.bridgedb.ws.bean.XrefBean;
import org.bridgedb.ws.bean.XrefMapBean;

/**
 *
 * @author Christian
 */
public class WSCoreMapper implements IDMapper, IDMapperCapabilities {

    WSCoreInterface webService;
    
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
        HashMap<Xref, Set<Xref>> results = new HashMap<Xref, Set<Xref>>();
        if (codes.isEmpty()) return results; //No valid srcrefs so return empty set
        List<XrefMapBean>  beans = webService.mapID(ids, codes, targetCodes);
        for (XrefMapBean bean:beans){
            Xref source = null;
            Set<Xref> targets = null;
            if (bean.getSource() != null){
                source = XrefBean.asXref(bean.getSource());
                targets = results.get(source);
            }
            if (targets == null){
                targets = new HashSet<Xref>(); 
            }
            targets.add(XrefBean.asXref(bean.getTarget()));
            results.put(source, targets);
        }
        return results;
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
        List<XrefMapBean>  beans = webService.mapID(ids, codes, targetCodes);
        HashSet<Xref> results = new HashSet<Xref>();
        for (XrefMapBean bean:beans){
            if (bean.getTarget() != null){
                results.add(XrefBean.asXref(bean.getTarget()));
            }
        }
        return results;
    }

    @Override
    public boolean xrefExists(Xref xref) throws BridgeDBException {
        if (xref.getId() == null) return false;
        if (xref.getDataSource() == null) return false;
        String id = xref.getId();
        String code = xref.getDataSource().getSystemCode();
        return webService.xrefExists(id,code).exists();
    }

    @Override
    public Set<Xref> freeSearch(String text, int limit) throws BridgeDBException {
        List<XrefBean>  beans = webService.freeSearch(text, "" + limit);
        HashSet<Xref> results = new HashSet<Xref>();
        for (XrefBean bean:beans){
            results.add(XrefBean.asXref(bean));
        }
        return results;
    }

    @Override
    public IDMapperCapabilities getCapabilities() {
        CapabilitiesBean bean = webService.getCapabilities();
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
        return webService.isFreeSearchSupported().isFreeSearchSupported();
    }

    @Override
    public Set<DataSource> getSupportedSrcDataSources() throws BridgeDBException {
        List<DataSourceBean> beans = webService.getSupportedSrcDataSources();
        HashSet<DataSource> results = new HashSet<DataSource>();
        for (DataSourceBean bean:beans){
            results.add(DataSourceBean.asDataSource(bean));
        }
        return results;
    }

    @Override
    public Set<DataSource> getSupportedTgtDataSources() throws BridgeDBException {
        List<DataSourceBean> beans = webService.getSupportedTgtDataSources();
        HashSet<DataSource> results = new HashSet<DataSource>();
        for (DataSourceBean bean:beans){
            results.add(DataSourceBean.asDataSource(bean));
        }
        return results;
    }

    @Override
    public boolean isMappingSupported(DataSource src, DataSource tgt) throws BridgeDBException {
        return webService.isMappingSupported(src.getSystemCode(), tgt.getSystemCode()).isMappingSupported();
    }

    @Override
    public String getProperty(String key) {
        PropertyBean bean = webService.getProperty(key);
        if (bean == null) return null;
        return bean.getValue();
    }

    @Override
    public Set<String> getKeys() {
        List<PropertyBean> beans = webService.getKeys();
        HashSet<String> results = new HashSet<String>();
        for (PropertyBean bean: beans){
            results.add(bean.getKey());
        }
        return results;
    }

}
