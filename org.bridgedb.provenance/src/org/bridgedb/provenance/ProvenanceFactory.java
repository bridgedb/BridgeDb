package org.bridgedb.provenance;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * The purpose of this class is to provide a single entry point for creating provenance.
 * 
 * Implementations can then just keep this simple version which just creates and returns Provenance.
 * Or add DataBase or registry support to save the Provenances of reuse and providing ids.
 * @author Christian
 */
public interface ProvenanceFactory {
    
    public Provenance createProvenance(String createdBy, String predicate, long creation, long upload) throws ProvenanceException;
    
    public Provenance createProvenance(String createdBy, String predicate, long creation) throws ProvenanceException;
    
    public Provenance createProvenace(Provenance first, Provenance second) throws ProvenanceException;
}
