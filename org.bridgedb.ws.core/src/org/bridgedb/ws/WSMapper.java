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
import java.util.Arrays;
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
import org.bridgedb.XrefIterator;
import org.bridgedb.url.URLMapper;
import org.bridgedb.ws.bean.PropertyBean;
import org.bridgedb.ws.bean.URLMapBean;
import org.bridgedb.ws.bean.URLSearchBean;

/**
 *
 * @author Christian
 */
public class WSMapper implements URLMapper, IDMapper, IDMapperCapabilities, XrefIterator, XrefByPossition {

    WSInterface webService;
    
    public WSMapper(WSInterface webService){
        this.webService = webService;
    }
    
    //**** URLMapper functions **** 
    
    @Override
    public Map<String, Set<String>> mapURL(Collection<String> srcURLs, String... tgtNameSpaces) throws IDMapperException {
        HashMap<String, Set<String>> results = new HashMap<String, Set<String>> ();
        if (srcURLs.isEmpty()) return results; //No valid srcrefs so return empty set
        List<URLMapBean>  beans = webService.mapByURLs(new ArrayList(srcURLs), Arrays.asList(tgtNameSpaces));
        for (URLMapBean bean:beans){
            results.put(bean.getSource(), bean.getTargetsSet());
        }
        return results;
    }

    @Override
    public Set<String> mapURL(String ref, String... tgtNameSpaces) throws IDMapperException {
        URLMapBean  bean = webService.mapByURL(ref, Arrays.asList(tgtNameSpaces));
        return bean.getTargetsSet();
    }

    @Override
    public boolean uriExists(String URL) throws IDMapperException {
        return webService.urlExists(URL).exists();
    }

    @Override
    public Set<String> urlSearch(String text, int limit) throws IDMapperException {
        URLSearchBean  bean = webService.URLSearch(text, limit);
        return bean.getURLSet();
    }

    //**** IDMApper functions *****
    @Override
    public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources) throws IDMapperException {
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> codes = new ArrayList<String>();
        ArrayList<String> tgtCodes = new ArrayList<String>();
        for (Xref srcXref:srcXrefs){
            if (srcXref.getId() != null && srcXref.getDataSource() != null){
                ids.add(srcXref.getId());
                codes.add(srcXref.getDataSource().getSystemCode());
            }
        }
        for (int i = 0 ; i < tgtDataSources.length; i++){
            tgtCodes.add(tgtDataSources[i].getSystemCode());
        }
        HashMap<Xref, Set<Xref>> results = new HashMap<Xref, Set<Xref>>();
        if (codes.isEmpty()) return results; //No valid srcrefs so return empty set
        List<XRefMapBean>  beans = webService.mapByXrefs(ids, codes, tgtCodes);
        for (XRefMapBean bean:beans){
            Xref key = bean.getKey();
            Set<Xref> mapSet = bean.getMappedSet();
            results.put(key, mapSet);
        }
        return results;
    }

    @Override
    public Set<Xref> mapID(Xref ref, DataSource... tgtDataSources) throws IDMapperException {
        if (ref.getId() == null || ref.getDataSource() == null) return new HashSet<Xref>();
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
        if (xref.getId() == null) return false;
        if (xref.getDataSource() == null) return false;
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

    private boolean isConnected = true;
    // In the case of DataCollection, there is no need to discard associated resources.
    
    @Override
    /** {@inheritDoc} */
    public void close() throws IDMapperException { 
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

    //OPTIONAL
    @Override
    public Iterable<Xref> getIterator(DataSource ds) throws IDMapperException {
        return new WSIterator(webService, ds);
    }

    //OPTIONAL
    @Override
    public Iterable<Xref> getIterator() throws IDMapperException {
        return new WSIterator(webService);
    }

    @Override
    public Set<Xref> getXrefByPossition(int possition, int limit) throws IDMapperException {
        List<XrefBean> beans = webService.getXrefByPossition(null, possition, limit);
        HashSet<Xref> results = new HashSet<Xref>();
        for (XrefBean bean:beans){
            results.add(bean.asXref());
        }
        return results;
    }

    @Override
    public Xref getXrefByPossition(int possition) throws IDMapperException {
        List<XrefBean> beans = webService.getXrefByPossition(null, possition, null);
        HashSet<Xref> results = new HashSet<Xref>();
        if (beans.isEmpty()) {
            return null;
        }
        return beans.get(0).asXref();
    }

    @Override
    public Set<Xref> getXrefByPossition(DataSource ds, int possition, int limit) throws IDMapperException {
        List<XrefBean> beans = webService.getXrefByPossition(ds.getSystemCode(), possition, limit);
        HashSet<Xref> results = new HashSet<Xref>();
        for (XrefBean bean:beans){
            results.add(bean.asXref());
        }
        return results;
    }

    @Override
    public Xref getXrefByPossition(DataSource ds, int possition) throws IDMapperException {
        List<XrefBean> beans = webService.getXrefByPossition(ds.getSystemCode(), possition, null);
        HashSet<Xref> results = new HashSet<Xref>();
        if (beans.isEmpty()) {
            return null;
        }
        return beans.get(0).asXref();
    }
    
}
