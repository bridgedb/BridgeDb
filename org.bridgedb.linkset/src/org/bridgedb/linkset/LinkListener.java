/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.provenance.Provenance;
import org.openrdf.model.URI;

/**
 *
 * @author Christian
 */
public interface LinkListener {
    
    public void insertLink(Xref source, Xref target) throws IDMapperException;

    //public void insertLink(URI source, String predicate, URI target) throws IDMapperException;
    
    //public void init() throws IDMapperException;
    
    public void init(Provenance provenance) throws IDMapperException;

    public void closeInput()throws IDMapperException;
}
