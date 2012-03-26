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
@XmlRootElement(name="URLExist")
public class URLExistsBean {
    
    private Boolean exists;
    private String URL;
    
    public URLExistsBean(){
    }
    
    public URLExistsBean(String URL, boolean exists){
        this.exists = exists;
        URL = URL;
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
     * @return the URL
     */
    public String getURL() {
        return URL;
    }

    /**
     * @param URL the URL to set
     */
    public void setURL(String URL) {
        this.URL = URL;
    }
    
  
}
