/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.util.List;
import org.bridgedb.IDMapperException;

/**
 *
 * @author Christian
 */
public interface LinkSetStore {
 //   public List<Triple> getTriples (String graphId) throws IDMapperException;

    public List<String> getLinksetNames() throws IDMapperException;

    public String getRDF (int id) throws IDMapperException;

}
