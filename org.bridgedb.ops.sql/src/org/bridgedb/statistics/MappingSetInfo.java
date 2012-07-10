/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.statistics;

/**
 *
 * @author Christian
 */
public class MappingSetInfo {
    private String id;
    private final String sourceSysCode;
    private final String predicate;
    private final String targetSysCode;
    private Integer numberOfLinks;
    private boolean isTransitive;

    public MappingSetInfo(String id, String sourceSysCode, String predicate, String targetSysCode, 
            Integer numberOfLinks, boolean isTransitive){
        this.id = id;
        this.predicate = predicate;
        this.sourceSysCode = sourceSysCode;
        this.targetSysCode = targetSysCode;
        this.numberOfLinks = numberOfLinks;
        this.isTransitive = isTransitive;
    }
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    public void multipleIds(){
        id = "various";
    }
    
    /**
     * @return the predicate
     */
    public String getPredicate() {
        return predicate;
    }

    /**
     * @return the sourceURISpace
     */
    public String getSourceSysCode() {
        return sourceSysCode;
    }

    /**
     * @return the targetURISpace
     */
    public String getTargetSysCode() {
        return targetSysCode;
    }

    /**
     * @return the numberOfLinks
     */
    public Integer getNumberOfLinks() {
        return numberOfLinks;
    }

    /**
     * @param numberOfLinks the numberOfLinks to set
     */
    public void setNumberOfLinks(Integer numberOfLinks) {
        this.numberOfLinks = numberOfLinks;
    }
    
    public String toString(){
        return this.id + "\n\tsourceSysCode:" + this.sourceSysCode + "\n\tpredicate:" + this.predicate + 
                "\n\ttargetSysCode:" +this.targetSysCode + "\n\tnumberOfLinks:" + this.numberOfLinks + "\n";
    }

    /**
     * @return the isTransitive
     */
    public boolean isTransitive() {
        return isTransitive;
    }

    public void setTransitive(boolean newValue) {
        isTransitive = newValue;
    }

 }
