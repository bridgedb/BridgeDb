/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import org.bridgedb.ops.Triple;
import org.openrdf.model.Statement;

/**
 *
 * @author Christian
 */
public class OpenRdfTriple extends Triple{
    
    public OpenRdfTriple(Statement statement){
        super(statement.getSubject().stringValue(), 
              statement.getPredicate().stringValue(), 
              statement.getObject().stringValue());
    }
}
