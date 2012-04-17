/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.provenance;

import org.bridgedb.DataSource;

/**
 *
 * @author Christian
 */
public class ProvenanceLink {
    
    private String id;
    private DataSource source;
    private String predicate;
    private DataSource target;
    
    public ProvenanceLink(String id, DataSource source, String predicate, DataSource target){
        this.id = id;
        this.source = source;
        this.predicate = predicate;
        this.target = target;
    }

    public ProvenanceLink(String id, String sourceNameSpace, String predicate, String targetNameSpace){
        this.id = id;
        this.source = DataSource.getByNameSpace(sourceNameSpace);
        this.predicate = predicate;
        this.target = DataSource.getByNameSpace(targetNameSpace);
    }

    public DataSource getSource() {
        return source;
    }

    public String getSourceNameSpace() {
        return source.getNameSpace();
    }

    public String getId() {
        return id;
    }

    public String getPredicate() {
        return this.predicate;
    }
    
    public DataSource getTarget() {
        return target;
    }

    public String getTargetNameSpace() {
        return target.getNameSpace();
    }
    
    @Override
    public boolean equals(Object other){
        if (other instanceof ProvenanceLink){
            ProvenanceLink otherLink = (ProvenanceLink)other;
            return (this.id.equals(otherLink.id) && 
                    this.source == otherLink.source && 
                    this.predicate.equals(otherLink.predicate) &&
                    this.target == otherLink.target);
        } else {
            return false;
        }
    }

    @Override
    public String toString(){
        return this.id + ": " + this.source + " " + this.predicate + " " + this.target;
    }
}
