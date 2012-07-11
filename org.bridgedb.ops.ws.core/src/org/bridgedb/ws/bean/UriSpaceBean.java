package org.bridgedb.ws.bean;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="URL")
public class UriSpaceBean {
    private String uriSpace;

    /**
     * WebServer constructor
     */
    public UriSpaceBean(){
        
    }

    public UriSpaceBean(String uriSpace){
        this.uriSpace = uriSpace;
    }
    
    /**
     * @return the UriSpace
     */
    public String getUriSpace() {
        return uriSpace;
    }

    /**
     * @param UriSpace the UriSpace to set
     */
    public void setUriSpace(String uriSpace) {
        this.uriSpace = uriSpace;
    }

}
