/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.provenance.url;

import org.bridgedb.provenance.Provenance;

/**
 *
 * @author Christian
 */
public class ProvenanceStatistics {
    private Provenance provenance;
    private int numberOfMappings;

    public ProvenanceStatistics(Provenance provenance, int numberOfMappings){
        this.provenance = provenance;
        this.numberOfMappings = numberOfMappings;
    }
    
    public Provenance getProvenance() {
        return provenance;
    }
    
    public int getNumberOfMappings(){
        return numberOfMappings;
    }
}
