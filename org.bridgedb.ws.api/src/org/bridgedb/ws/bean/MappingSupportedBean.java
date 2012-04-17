/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.bean;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="FreeSearchSupported")
public class MappingSupportedBean {
    
    Boolean isMappingSupported;
    DataSourceBean source;
    DataSourceBean target;

    public MappingSupportedBean(){
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
