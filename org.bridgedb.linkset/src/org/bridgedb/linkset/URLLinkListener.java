/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import org.bridgedb.IDMapperException;
import org.bridgedb.provenance.Provenance;
import org.bridgedb.provenance.ProvenanceFactory;

/**
 *
 * @author Christian
 */
public interface URLLinkListener extends ProvenanceFactory {
    
    public void insertLink(String source, String target, Provenance provenace) throws IDMapperException;

    public void openInput() throws IDMapperException;

    public void closeInput()throws IDMapperException;

}
