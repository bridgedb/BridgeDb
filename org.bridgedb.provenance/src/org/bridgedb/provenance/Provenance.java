package org.bridgedb.provenance;

import java.util.Date;
import org.bridgedb.IDMapperException;

/**
 * Warning under development. Will change without notice.
 * 
 * @author Christian
 */
public interface Provenance {
    
    //I wonder if something with hierarcy works better here
    //For example why not org.semanticweb.owlapi.model.OWLClass
    public String getPredicate() throws IDMapperException;
    
    public String getCreatedBy();

    public Date getCreationDate();

    public Date getUploadDate();
    
    public boolean isTransative();
    
}
