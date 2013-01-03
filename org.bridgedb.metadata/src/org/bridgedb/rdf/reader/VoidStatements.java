/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf.reader;

import java.util.Set;
import org.openrdf.model.Statement;

/**
 *
 * @author Christian
 */
public interface VoidStatements{

    public Set<Statement> getVoidStatements();
    
    public void resetBaseURI(String newBaseURI);
}
