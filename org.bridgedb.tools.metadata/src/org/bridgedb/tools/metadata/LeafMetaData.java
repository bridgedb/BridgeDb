/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.tools.metadata;

import org.openrdf.model.URI;

/**
 *
 * @author Christian
 */
public interface LeafMetaData {

    public URI getPredicate();

    public void addParent(LeafMetaData parentLeaf);
    
}
