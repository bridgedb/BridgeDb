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
public class FreeSearchSupportedBean {
    
    private Boolean isFreeSearchSupported;

    public FreeSearchSupportedBean(){
    }
    
    public FreeSearchSupportedBean(boolean supported){
        isFreeSearchSupported = supported;
    }
    
    /**
     * @return the isFreeSearchSupported
     */
    public Boolean getIsFreeSearchSupported() {
        return isFreeSearchSupported;
    }

    /**
     * @return the isFreeSearchSupported
     */
    public boolean isFreeSearchSupported() {
        return isFreeSearchSupported;
    }
    /**
     * @param isFreeSearchSupported the isFreeSearchSupported to set
     */
    public void setIsFreeSearchSupported(Boolean isFreeSearchSupported) {
        this.isFreeSearchSupported = isFreeSearchSupported;
    }
    
}
