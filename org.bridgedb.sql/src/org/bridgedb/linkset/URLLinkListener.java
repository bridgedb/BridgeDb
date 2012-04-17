/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import org.bridgedb.IDMapperException;
import org.bridgedb.provenance.ProvenanceLink;

/**
 *
 * @author Christian
 */
public interface URLLinkListener {
    
    public void registerProvenanceLink(ProvenanceLink provenaceLink) throws IDMapperException;
    
    public void insertLink(String source, String target, ProvenanceLink provenaceLink) throws IDMapperException;

    public void openInput() throws IDMapperException;

    public void closeInput()throws IDMapperException;

}
