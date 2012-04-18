/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import org.bridgedb.provenance.XrefProvenance;
import org.bridgedb.result.URLMapping;
import org.bridgedb.ws.bean.XrefBean;
import org.bridgedb.ws.bean.XrefMapBean;
import org.bridgedb.ws.bean.CapabilitiesBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.provenance.ProvenanceMapper;
import org.bridgedb.url.URLMapper;
import org.bridgedb.ws.bean.DataSourceBean;
import org.bridgedb.ws.bean.DataSourceBeanFactory;
import org.bridgedb.ws.bean.PropertyBean;
import org.bridgedb.ws.bean.URLMappingBean;
import org.bridgedb.ws.bean.URLMappingBeanFactory;
import org.bridgedb.ws.bean.URLSearchBean;
import org.bridgedb.ws.bean.XrefMapBeanFactory;
import org.bridgedb.ws.bean.XrefBeanFactory;

/**
 *
 * @author Christian
 */
public class WSCoreMapper implements IDMapper, IDMapperCapabilities, URLMapper, ProvenanceMapper {

    WSCoreInterface webService;
    public static final ArrayList<String> ALL_PROVENANCE = new ArrayList<String>();
    
    public WSCoreMapper(WSCoreInterface webService){
        this.webService = webService;
    }
    
    //**** URLMapper functions **** 
    
    @Override
    public Map<String, Set<String>> mapURL(Collection<String> srcURLs, String... tgtNameSpaces) throws IDMapperException {
        HashMap<String, Set<String>> results = new HashMap<String, Set<String>> ();
        if (srcURLs.isEmpty()) return results; //No valid srcrefs so return empty set
        List<URLMappingBean>  beans = 
                webService.mapByURLs(new ArrayList(srcURLs), ALL_PROVENANCE, Arrays.asList(tgtNameSpaces));
        for (URLMappingBean bean:beans){
            Set<String> targets = results.get(bean.getSourceURL());
            if (targets == null){
                targets = new HashSet<String>(); 
            }
            targets.add(bean.getTargetURL());
            results.put(bean.getSourceURL(), targets);
        }
        return results;
    }

    @Override
    public Set<String> mapURL(String srcURL, String... tgtNameSpaces) throws IDMapperException {
        ArrayList<String> srcURLs = new ArrayList<String>();
        srcURLs.add(srcURL);
        List<URLMappingBean> beans = webService.mapByURLs(srcURLs, ALL_PROVENANCE, Arrays.asList(tgtNameSpaces));
        HashSet<String> targetURLS = new HashSet<String>(); 
        for (URLMappingBean bean:beans){
            targetURLS.add(bean.getTargetURL());
        }
        return targetURLS;
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
        List<XrefMapBean>  beans = webService.mapID(ids, codes, ALL_PROVENANCE, tgtCodes);
        for (XrefMapBean bean:beans){
            System.out.println(bean);
            Xref source = XrefBeanFactory.asXref(bean.getSource());
            Set<Xref> targets = results.get(source);
            if (targets == null){
                targets = new HashSet<Xref>(); 
            }
            targets.add(XrefBeanFactory.asXref(bean.getTarget()));
            results.put(source, targets);
        }
        return results;
    }

    @Override
    public Set<Xref> mapID(Xref ref, DataSource... tgtDataSources) throws IDMapperException {
        if (ref.getId() == null || ref.getDataSource() == null) return new HashSet<Xref>();
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> codes = new ArrayList<String>();
        ids.add(ref.getId());
        codes.add(ref.getDataSource().getSystemCode());
        ArrayList<String> tgtCodes = new ArrayList<String>();
        for (int i = 0 ; i < tgtDataSources.length; i++){
            tgtCodes.add(tgtDataSources[i].getSystemCode());
        }
        List<XrefMapBean>  beans = webService.mapID(ids, codes, ALL_PROVENANCE, tgtCodes);
        HashSet<Xref> results = new HashSet<Xref>();
        for (XrefMapBean bean:beans){
            results.add(XrefBeanFactory.asXref(bean.getTarget()));
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
            results.add(XrefBeanFactory.asXref(bean));
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
            results.add(DataSourceBeanFactory.asDataSource(bean));
        }
        return results;
    }

    @Override
    public Set<DataSource> getSupportedTgtDataSources() throws IDMapperException {
        List<DataSourceBean> beans = webService.getSupportedTgtDataSources();
        HashSet<DataSource> results = new HashSet<DataSource>();
        for (DataSourceBean bean:beans){
            results.add(DataSourceBeanFactory.asDataSource(bean));
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

    // ****** ProvenanceMapper Functions ***** 
    @Override
    public Map<Xref, Set<XrefProvenance>> mapIDProvenance(Collection<Xref> srcXrefs, 
            Collection<String> provenanceIds, Collection<DataSource> targetDataSources) throws IDMapperException {
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> codes = new ArrayList<String>();
        for (Xref srcXref:srcXrefs){
            if (srcXref.getId() != null && srcXref.getDataSource() != null){
                ids.add(srcXref.getId());
                codes.add(srcXref.getDataSource().getSystemCode());
            }
        }
        ArrayList<String> tgtCodes = new ArrayList<String>();
        for (DataSource targetDataSource:targetDataSources){
            tgtCodes.add(targetDataSource.getSystemCode());
        }
        HashMap<Xref, Set<XrefProvenance>> results = new HashMap<Xref, Set<XrefProvenance>>();
        if (codes.isEmpty()) return results; //No valid srcrefs so return empty set
        List<XrefMapBean>  beans = webService.mapID(ids, codes, new ArrayList(provenanceIds), tgtCodes);
        for (XrefMapBean bean:beans){
            Xref source = XrefBeanFactory.asXref(bean.getSource());
            Set<XrefProvenance> targets = results.get(source);
            if (targets == null){
                targets = new HashSet<XrefProvenance>();
            }
            targets.add(XrefMapBeanFactory.asXrefProvenance(bean));
            results.put(source, targets);
        }
        return results;
    }

    @Override
    public Set<XrefProvenance> mapIDProvenance(Xref ref, Collection<String> provenanceIds, 
            Collection<DataSource> targetDataSources) throws IDMapperException {
        if (ref.getId() == null || ref.getDataSource() == null) return new HashSet<XrefProvenance>();
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> codes = new ArrayList<String>();
        ids.add(ref.getId());
        codes.add(ref.getDataSource().getSystemCode());
        ArrayList<String> tgtCodes = new ArrayList<String>();    
        Iterator<DataSource> targetDataSourcesIterator = targetDataSources.iterator();
        while (targetDataSourcesIterator.hasNext())
            tgtCodes.add(targetDataSourcesIterator.next().getSystemCode());
        List<XrefMapBean>  beans = webService.mapID(ids, codes, new ArrayList(provenanceIds), tgtCodes);
        HashSet<XrefProvenance> results = new HashSet<XrefProvenance>();
        for (XrefMapBean bean:beans){
            results.add(XrefMapBeanFactory.asXrefProvenance(bean));
        }
        return results;
    }

    @Override
    public Set<URLMapping> mapURL(Collection<String> srcURLs, Collection<String> provenanceIds, 
            Collection<String> targetNameSpaces) throws IDMapperException {
        HashSet<URLMapping> results = new HashSet<URLMapping>();
        if (srcURLs.isEmpty()) return results; //No valid srcrefs so return empty set
        List<URLMappingBean>  beans = 
                webService.mapByURLs(new ArrayList(srcURLs), new ArrayList(provenanceIds), new ArrayList(targetNameSpaces));
        for (URLMappingBean bean:beans){
            results.add(URLMappingBeanFactory.asURLMapping(bean));
        }
        return results;
    }

    //@Override
    //public ProvenanceLink getProvenance(String provenanceId) throws IDMapperException {
    //    ProvenanceBean  bean = webService.getProvenance(provenanceId);
    //    return ProvenanceBeanFactory.asProvenance(bean);
    //}
   
}
