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
public class ProvenanceBean {
    
    private String id;
    private String predicate;
    private String sourceNameSpace;
    private String targetNameSpace;

    public ProvenanceBean(){
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the predicate
     */
    public String getPredicate() {
        return predicate;
    }

    /**
     * @param predicate the predicate to set
     */
    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    /**
     * @return the sourceNameSpace
     */
    public String getSourceNameSpace() {
        return sourceNameSpace;
    }

    /**
     * @param sourceNameSpace the sourceNameSpace to set
     */
    public void setSourceNameSpace(String sourceNameSpace) {
        this.sourceNameSpace = sourceNameSpace;
    }

    /**
     * @return the targetNameSpace
     */
    public String getTargetNameSpace() {
        return targetNameSpace;
    }

    /**
     * @param targetNameSpace the targetNameSpace to set
     */
    public void setTargetNameSpace(String targetNameSpace) {
        this.targetNameSpace = targetNameSpace;
    }

}
