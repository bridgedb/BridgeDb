/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="IDMapperCapabilities")
public class CapabilitiesBean implements IDMapperCapabilities{

    private Boolean isFreeSearchSupported;
    private List<DataSourceBean> supportedSrcDataSources;
    private List<DataSourceBean> supportedTgtDataSources;
    private List<DataSourceMapBean> supportedMappings;
    private List<PropertyBean> properties;
    
    public CapabilitiesBean(){
        isFreeSearchSupported = null;
        supportedSrcDataSources = null;
        supportedTgtDataSources = null;
        supportedMappings = null;
        properties = new ArrayList<PropertyBean>();
    }
    
    public CapabilitiesBean(IDMapperCapabilities capabilities) {
        isFreeSearchSupported = capabilities.isFreeSearchSupported();
        supportedSrcDataSources = new ArrayList<DataSourceBean>();
        supportedTgtDataSources = new ArrayList<DataSourceBean>();
        supportedMappings = new ArrayList<DataSourceMapBean>();
        try {
            Set<DataSource> sources = capabilities.getSupportedSrcDataSources();
            Set<DataSource> targets = capabilities.getSupportedTgtDataSources();
            for (DataSource dataSource:targets){
                supportedTgtDataSources.add(new DataSourceBean(dataSource));
            }
            for (DataSource source:sources){
                supportedSrcDataSources.add(new DataSourceBean(source));
                HashSet<DataSource> mappedTargets = new HashSet<DataSource>();
                for (DataSource target:targets){
                    if (capabilities.isMappingSupported(source, target)){
                        mappedTargets.add(target);
                    }
                }
                supportedMappings.add(new DataSourceMapBean(source, mappedTargets));
            } 
        } catch (IDMapperException ex){
            ex.printStackTrace();
            //Nothing else we can do so leave it as empty as it its.
        }
        properties = new ArrayList<PropertyBean>();
        Set<String> keys = capabilities.getKeys();
        for (String key:keys){
            properties.add(new PropertyBean(key, capabilities.getProperty(key)));
        }
    }
    
    @Override
    public boolean isFreeSearchSupported() {
        return isFreeSearchSupported;
    }

    @Override
    public Set<DataSource> getSupportedSrcDataSources() throws IDMapperException {
        HashSet<DataSource> results = new HashSet<DataSource>();       
        for (DataSourceBean bean:supportedSrcDataSources){
            results.add(bean.asDataSource());
        }
        return results;
    }

    @Override
    public Set<DataSource> getSupportedTgtDataSources() throws IDMapperException {
        HashSet<DataSource> results = new HashSet<DataSource>();       
        for (DataSourceBean bean:supportedTgtDataSources){
            results.add(bean.asDataSource());
        }
        return results;
    }

    @Override
    public boolean isMappingSupported(DataSource src, DataSource tgt) throws IDMapperException {
        for (DataSourceMapBean bean:supportedMappings){
            if (bean.getKey() == src ){
                Set<DataSource> targets = bean.getMappedSet();
                return targets.contains(tgt);
            }
        }
        return false;
    }

    @Override
    public String getProperty(String key) {
        for (PropertyBean bean:properties){
            if (bean.getKey().equals(key)){
                return bean.getValue();
            }
        }
        return null;
    }

    @Override
    public Set<String> getKeys() {
        HashSet<String> keys = new HashSet<String>();
        for (PropertyBean bean:properties){
            keys.add(bean.getKey());
        }
        return keys;
    }

    public IDMapperCapabilities asIDMapperCapabilities() {
        return this;
    }
}
