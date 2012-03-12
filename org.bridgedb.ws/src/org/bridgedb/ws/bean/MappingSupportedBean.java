/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.bean;

import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.DataSource;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="FreeSearchSupported")
public class MappingSupportedBean {
    
    private Boolean isMappingSupported;
    private DataSourceBean source;
    private DataSourceBean target;

    public MappingSupportedBean(){
    }
    
    public MappingSupportedBean(DataSource sourceDataSource, DataSource targetDataSource, boolean supported){
        source = new DataSourceBean(sourceDataSource);
        target = new DataSourceBean(targetDataSource);
        isMappingSupported = supported;
    }
    
    /**
     * @return the isMappingSupported
     */
    public Boolean getisMappingSupported() {
        return isMappingSupported;
    }

    /**
     * @return the isMappingSupported
     */
    public boolean isMappingSupported() {
        return isMappingSupported;
    }
    /**
     * @param isMappingSupported the isMappingSupported to set
     */
    public void setisMappingSupported(Boolean isMappingSupported) {
        this.isMappingSupported = isMappingSupported;
    }

    /**
     * @return the source
     */
    public DataSourceBean getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(DataSourceBean source) {
        this.source = source;
    }

    /**
     * @return the target
     */
    public DataSourceBean getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(DataSourceBean target) {
        this.target = target;
    }
    
}
