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
@XmlRootElement(name="Property")
public class PropertyBean {
    
    private String key;
    private String value;

    public PropertyBean(){
    }

    public PropertyBean(String key, String value){
        this.key = key;
        this.value = value;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
}
