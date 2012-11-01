/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.util.Set;
import org.openrdf.model.Statement;

/**
 *
 * @author Christian
 */
public interface LinksetStatements extends VoidStatements{

    public Set<Statement> getLinkStatements();
    
    @Override
    public Set<Statement> getVoidStatements();
    
    @Override
    public void resetBaseURI(String newBaseURI);
}
