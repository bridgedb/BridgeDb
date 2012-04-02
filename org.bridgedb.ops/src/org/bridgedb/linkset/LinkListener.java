/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/**
 *
 * @author Christian
 */
public interface LinkListener {
    
    public void insertLink(Xref source, Xref target) throws IDMapperException;

    public void openInput() throws IDMapperException;

    public void closeInput()throws IDMapperException;
}
