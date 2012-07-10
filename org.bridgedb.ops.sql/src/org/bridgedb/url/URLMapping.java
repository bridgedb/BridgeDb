/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.url;

/**
 *
 * @author Christian
 */
public class URLMapping {
 
    private Integer id;
    private String sourceURL;
    private String targetURL;
    private Integer mappingSetId;
    private String predicate;
    
    public URLMapping (Integer id, String sourceURL, String targetURL, Integer mappingSetId, String predicate){
        this.id = id;
        this.sourceURL = sourceURL;
        this.targetURL = targetURL;
        this.mappingSetId = mappingSetId;
        this.predicate = predicate;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return the source
     */
    public String getSourceURL() {
        return sourceURL;
    }

    /**
     * @return the target
     */
    public String getTargetURL() {
        return targetURL;
    }

    public String toString(){
        return this.id  + ": " + this.sourceURL + " " + this.getPredicate() + " " + this.targetURL + 
                " mappingSet: " + this.getMappingSetId();
    }
   
    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other instanceof URLMapping){
            URLMapping otherMapping = (URLMapping)other;
            if (otherMapping.id != id) return false;
            if (!otherMapping.sourceURL.equals(sourceURL)) return false;
            if (!otherMapping.targetURL.equals(targetURL)) return false;
            if (!otherMapping.getMappingSetId().equals(getMappingSetId())) return false;
            //No need to check predicate as by defintion one id has one predicate
            return true;
         } else {
            return false;
        }
    }

    /**
     * @return the mappingSetId
     */
    public Integer getMappingSetId() {
        return mappingSetId;
    }

    /**
     * @return the predicate
     */
    public String getPredicate() {
        return predicate;
    }
}
