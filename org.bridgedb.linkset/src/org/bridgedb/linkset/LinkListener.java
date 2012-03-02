/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import org.openrdf.model.URI;

/**
 *
 * @author Christian
 */
public interface LinkListener {
    
    public void insertLink(URI source, String predicate, URI target) throws IDMapperLinksetException;
}
