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
    //Names of list are singular as they appear in the xml individually
    private List<DataSourceBean> sourceDataSource;
    private List<DataSourceBean> targetDataSource;
    private List<DataSourceMapBean> supportedMapping;
    private List<PropertyBean> property;
    
    public CapabilitiesBean(){
        isFreeSearchSupported = null;
        sourceDataSource = null;
        targetDataSource = null;
        supportedMapping = null;
        property = new ArrayList<PropertyBean>();
    }
    
    public CapabilitiesBean(IDMapperCapabilities capabilities) {
        isFreeSearchSupported = capabilities.isFreeSearchSupported();
        sourceDataSource = new ArrayList<DataSourceBean>();
        targetDataSource = new ArrayList<DataSourceBean>();
        supportedMapping = new ArrayList<DataSourceMapBean>();
        try {
            Set<DataSource> sources = capabilities.getSupportedSrcDataSources();
            Set<DataSource> targets = capabilities.getSupportedTgtDataSources();
            for (DataSource dataSource:targets){
                targetDataSource.add(DataSourceBeanFactory.asBean(dataSource));
            }
            for (DataSource source:sources){
                sourceDataSource.add(DataSourceBeanFactory.asBean(source));
                HashSet<DataSource> mappedTargets = new HashSet<DataSource>();
                for (DataSource target:targets){
                    if (capabilities.isMappingSupported(source, target)){
                        mappedTargets.add(target);
                    }
                }
                supportedMapping.add(DataSourceMapBeanFactory.asBean(source, mappedTargets));
            } 
        } catch (IDMapperException ex){
            ex.printStackTrace();
            //Nothing else we can do so leave it as empty as it its.
        }
        property = new ArrayList<PropertyBean>();
        Set<String> keys = capabilities.getKeys();
        for (String key:keys){
            property.add(new PropertyBean(key, capabilities.getProperty(key)));
        }
    }
    
    @Override
    public boolean isFreeSearchSupported() {
        return getIsFreeSearchSupported();
    }

    @Override
    public Set<DataSource> getSupportedSrcDataSources() throws IDMapperException {
        HashSet<DataSource> results = new HashSet<DataSource>();       
        for (DataSourceBean bean:sourceDataSource){
            results.add(DataSourceBeanFactory.asDataSource(bean));
        }
        return results;
    }

    @Override
    public Set<DataSource> getSupportedTgtDataSources() throws IDMapperException {
        HashSet<DataSource> results = new HashSet<DataSource>();       
        for (DataSourceBean bean:targetDataSource){
            results.add(DataSourceBeanFactory.asDataSource(bean));
        }
        return results;
    }

    @Override
    public boolean isMappingSupported(DataSource src, DataSource tgt) throws IDMapperException {
        for (DataSourceMapBean bean:supportedMapping){
            if (DataSourceMapBeanFactory.getKey(bean) == src ){
                Set<DataSource> targets = DataSourceMapBeanFactory.getMappedSet(bean);
                return targets.contains(tgt);
            }
        }
        return false;
    }

    @Override
    public String getProperty(String key) {
        for (PropertyBean bean:property){
            if (bean.getKey().equals(key)){
                return bean.getValue();
            }
        }
        return null;
    }

    @Override
    public Set<String> getKeys() {
        HashSet<String> keys = new HashSet<String>();
        for (PropertyBean bean:property){
            keys.add(bean.getKey());
        }
        return keys;
    }

    public IDMapperCapabilities asIDMapperCapabilities() {
        return this;
    }

    /**
     * @return the isFreeSearchSupported
     */
    public Boolean getIsFreeSearchSupported() {
        return isFreeSearchSupported;
    }

    /**
     * @param isFreeSearchSupported the isFreeSearchSupported to set
     */
    public void setIsFreeSearchSupported(Boolean isFreeSearchSupported) {
        this.isFreeSearchSupported = isFreeSearchSupported;
    }

    /**
     * @param sourceDataSource  the sourceDataSource(s) to set
     */
    public void setSourceDataSource(List<DataSourceBean> sourceDataSource) {
        this.sourceDataSource = sourceDataSource;
    }

    /**
     * @param targetDataSource the targetDataSource(s) to set
     */
    public void setTargetDataSource(List<DataSourceBean> targetDataSource) {
        this.targetDataSource = targetDataSource;
    }

    /**
     * @return the supportedMapping(s)
     */
    public List<DataSourceMapBean> getSupportedMapping() {
        return supportedMapping;
    }

    /**
     * @param supportedMapping the supportedMapping(s) to set
     */
    public void setSupportedMapping(List<DataSourceMapBean> supportedMapping) {
        this.supportedMapping = supportedMapping;
    }

    /**
     * @return the properties
     */
    public List<PropertyBean> getProperty() {
        return property;
    }

    /**
     * @param property the properties to set
     */
    public void setProperty(List<PropertyBean> property) {
        this.property = property;
    }

    /**
     * @return the sourceDataSource(s)
     */
    public List<DataSourceBean> getSourceDataSource() {
        return sourceDataSource;
    }

    /**
     * @return the targetDataSource(s)
     */
    public List<DataSourceBean> getTargetDataSource() {
        return targetDataSource;
    }
}
