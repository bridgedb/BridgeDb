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
@XmlRootElement(name="URLMapping")
public class URLMappingBean {
    Integer id;
    String sourceURL;
    String targetURL;
    private Integer mappingSetId;
    String predicate;
    
    //Webservice constructor
    public URLMappingBean(){
    }

    URLMappingBean(Integer id, String sourceURL, String predicate, String targetURL, Integer mappingSetId) {
        this.id = id;
        this.sourceURL = sourceURL;
        this.predicate = predicate;
        this.targetURL = targetURL;
        this.mappingSetId = mappingSetId;
    }
    
    public String toString(){
           return  "URLMapping: id: " + this.getId() + this.getSourceURL() + " " + this.getPredicate() + 
                   " " + this.getTargetURL() + " id: " + this.getId() + " mappingSet: " + this.getMappingSetId();
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the sourceURL
     */
    public String getSourceURL() {
        return sourceURL;
    }

    /**
     * @param sourceURL the sourceURL to set
     */
    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    /**
     * @return the targetURL
     */
    public String getTargetURL() {
        return targetURL;
    }

    /**
     * @param targetURL the targetURL to set
     */
    public void setTargetURL(String targetURL) {
        this.targetURL = targetURL;
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
     * @return the mappingSetId
     */
    public Integer getMappingSetId() {
        return mappingSetId;
    }

    /**
     * @param mappingSetId the mappingSetId to set
     */
    public void setMappingSetId(Integer mappingSetId) {
        this.mappingSetId = mappingSetId;
    }
    
}
