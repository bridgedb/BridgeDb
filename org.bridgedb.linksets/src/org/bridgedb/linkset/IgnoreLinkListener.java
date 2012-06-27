/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.util.HashSet;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/**
 *
 * @author Christian
 */
public class IgnoreLinkListener implements URLLinkListener{

    @Override
    public void openInput() throws IDMapperException {
        //Do nothing
    }

    @Override
    public void closeInput() throws IDMapperException {
        //Do nothing
    }

    @Override
    public void registerLinkSet(String linkSetId, DataSource source, String predicate, DataSource target,
            boolean isTransitive) throws IDMapperException {
        //Do nothing
    }

    @Override
    public void insertLink(String source, String target, String forwardlinkSetId, String inverselinkSetId) throws IDMapperException {
        //Do nothing
    }

    @Override
    public Set<String> getLinkSetIds() throws IDMapperException {
        return new HashSet<String>();
    }
    
}
