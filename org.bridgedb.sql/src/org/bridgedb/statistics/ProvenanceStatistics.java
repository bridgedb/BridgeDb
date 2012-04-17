/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.statistics;

import org.bridgedb.provenance.ProvenanceLink;

/**
 *
 * @author Christian
 */
public class ProvenanceStatistics {
    private ProvenanceLink provenanceLink;
    private int numberOfMappings;

    public ProvenanceStatistics(ProvenanceLink provenanceLink, int numberOfMappings){
        this.provenanceLink = provenanceLink;
        this.numberOfMappings = numberOfMappings;
    }
    
    public ProvenanceLink getProvenanceLink() {
        return provenanceLink;
    }
    
    public int getNumberOfMappings(){
        return numberOfMappings;
    }
}
