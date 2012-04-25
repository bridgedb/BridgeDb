/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ops;

/**
 *
 * @author Christian
 */
public class ProvenanceInfo {
    private final String id;
    private final String sourceNameSpace;
    private final String predicate;
    private final String targetNameSpace;

    public ProvenanceInfo(String id, String sourceNameSpace, String predicate, String targetNameSpace){
        this.id = id;
        this.predicate = predicate;
        this.sourceNameSpace = sourceNameSpace;
        this.targetNameSpace = targetNameSpace;
    }
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
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
}
