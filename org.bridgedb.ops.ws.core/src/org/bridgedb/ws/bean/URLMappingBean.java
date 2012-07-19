/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.bean;

import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="URLMapping")
public class URLMappingBean {
    Integer id;
    //Name is single as XML looks better this way
    Set<String> sourceURL;
    //Name is single as XML looks better this way
    Set<String> targetURL;
    private Integer mappingSetId;
    String predicate;
    
    //Webservice constructor
    public URLMappingBean(){
    }

    URLMappingBean(Integer id, Set<String> sourceURLs, String predicate, Set<String> targetURLs, Integer mappingSetId) {
        this.id = id;
        this.sourceURL = sourceURLs;
        this.predicate = predicate;
        this.targetURL = targetURLs;
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
    public Set<String> getSourceURL() {
        return sourceURL;
    }

    /**
     * @param sourceURL the sourceURL to set
     */
    public void setSourceURL(Set<String> sourceURLs) {
        this.sourceURL = sourceURLs;
    }

    /**
     * @return the targetURL
     */
    public Set<String> getTargetURL() {
        return targetURL;
    }

    /**
     * @param targetURL the targetURL to set
     */
    public void setTargetURL(Set<String> targetURLs) {
        this.targetURL = targetURLs;
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
