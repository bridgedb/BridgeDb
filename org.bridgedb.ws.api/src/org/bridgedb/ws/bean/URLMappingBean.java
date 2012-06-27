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
    String linkSetId;
    String predicate;
    String Error; 
    
    //Webservice constructor
    public URLMappingBean(){
    }
    
    public String toString(){
        if (getError() != null){
           return "URLMappingBean: Error " +  getError();
        } else { 
           return  "URLMapping: id: " + this.getId() + this.getSourceURL() + " " + this.getPredicate() + 
                   " " + this.getTargetURL() + " id: " + this.getId() + " linkSet: " + this.getLinkSetId();
        } 
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
     * @return the linkSetId
     */
    public String getLinkSetId() {
        return linkSetId;
    }

    /**
     * @param linkSetId the linkSetId to set
     */
    public void setLinkSetId(String linkSetId) {
        this.linkSetId = linkSetId;
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
     * @return the Error
     */
    public String getError() {
        return Error;
    }

    /**
     * @param Error the Error to set
     */
    public void setError(String Error) {
        this.Error = Error;
    }
    
}
