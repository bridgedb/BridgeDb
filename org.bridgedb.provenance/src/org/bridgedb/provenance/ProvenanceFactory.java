package org.bridgedb.provenance;

import java.util.Date;

/**
 * The purpose of this class is to provide a single entry point for creating provenance.
 * 
 * Implementations can then just keep this simple version which just creates and returns Provenance.
 * Or add DataBase or registry support to save the Provenances of reuse and providing ids.
 * @author Christian
 */
public abstract class ProvenanceFactory {
    
    public static Provenance createProvenance(String createdBy, String predicate, Date creationDate, Date uploadDate){
        return new SimpleProvenance(createdBy, predicate, creationDate, uploadDate);
    }
    
    public static Provenance createProvenace(Provenance first, Provenance second){
        return new TransativeProvenace(first, second);
    }
}
