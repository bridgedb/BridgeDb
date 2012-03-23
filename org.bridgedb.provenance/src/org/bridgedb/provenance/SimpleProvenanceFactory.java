package org.bridgedb.provenance;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.bridgedb.DataSource;

/**
 * The purpose of this class is to provide a single entry point for creating provenance.
 * 
 * Implementations can then just keep this simple version which just creates and returns Provenance.
 * Or add DataBase or registry support to save the Provenances of reuse and providing ids.
 * @author Christian
 */
public class SimpleProvenanceFactory implements ProvenanceFactory{
    
    @Override
    public Provenance createProvenance(DataSource source, String predicate, DataSource target, 
            String createdBy, long creation, long upload){
        return new SimpleProvenance(source, predicate, target, createdBy, creation, upload);
    }
    
    @Override
    public Provenance createProvenance(DataSource source, String predicate, DataSource target, 
            String createdBy, long creation){
        return createProvenance(source, predicate, target, createdBy, 
                creation, new GregorianCalendar().getTimeInMillis());
    }
    
    @Override
    public Provenance createProvenace(Provenance first, Provenance second) throws ProvenanceException{
        return new TransativeProvenace(first, second);
    }
}
