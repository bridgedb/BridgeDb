/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ops;

/**
 *
 * @author Christian
 */
public class LinkSetInfo {
    private String id;
    private final String sourceURISpace;
    private final String predicate;
    private final String targetURISpace;
    private Integer numberOfLinks;
    private boolean isTransitive;

    public LinkSetInfo(String id, String sourceURISpace, String predicate, String targetURISpace, 
            Integer numberOfLinks, boolean isTransitive){
        this.id = id;
        this.predicate = predicate;
        this.sourceURISpace = sourceURISpace;
        this.targetURISpace = targetURISpace;
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
    public String getSourceURISpace() {
        return sourceURISpace;
    }

    /**
     * @return the targetURISpace
     */
    public String getTargetURISpace() {
        return targetURISpace;
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
        return this.id + "\n\tsourceURISpace:" + this.sourceURISpace + "\n\tpredicate:" + this.predicate + 
                "\n\ttargetURISpace:" +this.targetURISpace + "\n\tnumberOfLinks:" + this.numberOfLinks + "\n";
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
