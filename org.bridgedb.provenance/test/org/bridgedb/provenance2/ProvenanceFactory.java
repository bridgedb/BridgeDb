package org.bridgedb.provenance;

import org.bridgedb.DataSource;

/**
 * The purpose of this class is to provide a single entry point for creating provenance.
 * 
 * Implementations can then just keep this simple version which just creates and returns Provenance.
 * Or add DataBase or registry support to save the Provenances of reuse and providing ids.
 * @author Christian
 */
public interface ProvenanceFactory {
    
    public Provenance createProvenance(DataSource source, String predicate, DataSource target, 
            String createdBy, long creation, long upload) throws ProvenanceException;
    
    public Provenance createProvenance(DataSource source, String predicate, DataSource target,
            String createdBy, long creation) throws ProvenanceException;
    
    public Provenance createProvenace(Provenance first, Provenance second) throws ProvenanceException;
}
