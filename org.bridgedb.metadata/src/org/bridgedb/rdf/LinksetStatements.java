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
public interface LinksetStatements{

    public Set<Statement> getLinkStatements();
    
    public Set<Statement> getVoidStatements();
    
    public void resetBaseURI(String newBaseURI);
}
