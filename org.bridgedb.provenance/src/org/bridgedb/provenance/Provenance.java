/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.provenance;

import java.util.Date;

/**
 *
 * @author Christian
 */
public interface Provenance {
    
    //I wonder if something with hierarcy works better here
    //For example why not org.semanticweb.owlapi.model.OWLClass
    public String getPredicate();
    
    public String getCreatedBy();

    public Date getCreationDate();

    public Date getUploadDate();
    
    public boolean isTransative();
    
}
