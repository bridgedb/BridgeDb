/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.bean;

import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.Xref;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="XrefExist")
public class XrefExistsBean {
    
    private Boolean exists;
    private XrefBean Xref;
    
    public XrefExistsBean(){
    }
    
    public XrefExistsBean(Xref xref, boolean exists){
        this.exists = exists;
        Xref = new XrefBean(xref);
    }
    
    /**
     * @return the isSupported
     */
    public Boolean getExists() {
        return exists;
    }

    /**
     * @return the isSupported
     */
    public boolean exists() {
        return exists;
    }
    /**
     * @param isSupported the isSupported to set
     */
    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    /**
     * @return the Xref
     */
    public XrefBean getXref() {
        return Xref;
    }

    /**
     * @param Xref the Xref to set
     */
    public void setXref(XrefBean Xref) {
        this.Xref = Xref;
    }
    
  
}
