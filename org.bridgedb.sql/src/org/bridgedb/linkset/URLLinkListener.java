/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;

/**
 *
 * @author Christian
 */
public interface URLLinkListener {
    
    public void registerProvenanceLink(String provenanceId, DataSource source, String predicate, DataSource target) 
            throws IDMapperException;
    
    public void insertLink(String source, String target, String provenanceId) throws IDMapperException;

    public void openInput() throws IDMapperException;

    public void closeInput()throws IDMapperException;

}
