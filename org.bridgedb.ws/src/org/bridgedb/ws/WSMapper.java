/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import org.bridgedb.ws.bean.XrefBean;
import org.bridgedb.ws.bean.XRefMapBean;
import org.bridgedb.ws.bean.DataSourceBean;
import org.bridgedb.ws.bean.CapabilitiesBean;
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
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.ws.bean.PropertyBean;

/**
 *
 * @author Christian
 */
public class WSMapper implements IDMapper, IDMapperCapabilities{

    WSInterface webService;
    
    public WSMapper(WSInterface webService){
        this.webService = webService;
    }
    
    @Override
    public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources) throws IDMapperException {
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> codes = new ArrayList<String>();
        ArrayList<String> tgtCodes = new ArrayList<String>();
        for (Xref srcXref:srcXrefs){
            ids.add(srcXref.getId());
            codes.add(srcXref.getDataSource().getSystemCode());
        }
        for (int i = 0 ; i < tgtDataSources.length; i++){
            tgtCodes.add(tgtDataSources[i].getSystemCode());
        }
        List<XRefMapBean>  beans = webService.mapByXrefs(ids, codes, tgtCodes);
        HashMap<Xref, Set<Xref>> results = new HashMap<Xref, Set<Xref>>();
        for (XRefMapBean bean:beans){
            Xref key = bean.getKey();
            Set<Xref> mapSet = bean.getMappedSet();
            results.put(key, mapSet);
        }
        return results;
    }

    @Override
    public Set<Xref> mapID(Xref ref, DataSource... tgtDataSources) throws IDMapperException {
        String id = ref.getId();
        String code = ref.getDataSource().getSystemCode();
        ArrayList<String> tgtCodes = new ArrayList<String>();
        for (int i = 0 ; i < tgtDataSources.length; i++){
            tgtCodes.add(tgtDataSources[i].getSystemCode());
        }
        List<XrefBean>  beans = webService.mapByXref(id, code, tgtCodes);
        HashSet<Xref> results = new HashSet<Xref>();
        for (XrefBean bean:beans){
            results.add(bean.asXref());
        }
        return results;
    }

    @Override
    public boolean xrefExists(Xref xref) throws IDMapperException {
        String id = xref.getId();
        String code = xref.getDataSource().getSystemCode();
        return webService.xrefExists(id,code).exists();
    }

    @Override
    public Set<Xref> freeSearch(String text, int limit) throws IDMapperException {
        List<XrefBean>  beans = webService.freeSearch(text, limit);
        HashSet<Xref> results = new HashSet<Xref>();
        for (XrefBean bean:beans){
            results.add(bean.asXref());
        }
        return results;
    }

    @Override
    public IDMapperCapabilities getCapabilities() {
        CapabilitiesBean bean = webService.getCapabilities();
        return bean.asIDMapperCapabilities();
    }

    @Override
    public void close() throws IDMapperException {
        //Do nothing;
    }

    @Override
    public boolean isConnected() {
        try{
            webService.freeSearch(null, Integer.SIZE);
           return true; 
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean isFreeSearchSupported() {
        return webService.isFreeSearchSupported().isFreeSearchSupported();
    }

    @Override
    public Set<DataSource> getSupportedSrcDataSources() throws IDMapperException {
        List<DataSourceBean> beans = webService.getSupportedSrcDataSources();
        HashSet<DataSource> results = new HashSet<DataSource>();
        for (DataSourceBean bean:beans){
            results.add(bean.asDataSource());
        }
        return results;
    }

    @Override
    public Set<DataSource> getSupportedTgtDataSources() throws IDMapperException {
        List<DataSourceBean> beans = webService.getSupportedTgtDataSources();
        HashSet<DataSource> results = new HashSet<DataSource>();
        for (DataSourceBean bean:beans){
            results.add(bean.asDataSource());
        }
        return results;
    }

    @Override
    public boolean isMappingSupported(DataSource src, DataSource tgt) throws IDMapperException {
        return webService.isMappingSupported(src.getSystemCode(), tgt.getSystemCode()).isMappingSupported();
    }

    @Override
    public String getProperty(String key) {
        return webService.getProperty(key).getValue();
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
