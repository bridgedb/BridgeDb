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
    private final String sourceNameSpace;
    private final String predicate;
    private final String targetNameSpace;
    private Integer numberOfLinks;
    private boolean isTransitive;

    public LinkSetInfo(String id, String sourceNameSpace, String predicate, String targetNameSpace, 
            Integer numberOfLinks, boolean isTransitive){
        this.id = id;
        this.predicate = predicate;
        this.sourceNameSpace = sourceNameSpace;
        this.targetNameSpace = targetNameSpace;
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
     * @return the sourceNameSpace
     */
    public String getSourceNameSpace() {
        return sourceNameSpace;
    }

    /**
     * @return the targetNameSpace
     */
    public String getTargetNameSpace() {
        return targetNameSpace;
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
        return this.id + "\n\tsourceNameSpace:" + this.sourceNameSpace + "\n\tpredicate:" + this.predicate + 
                "\n\ttargetNameSpace:" +this.targetNameSpace + "\n\tnumberOfLinks:" + this.numberOfLinks + "\n";
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
