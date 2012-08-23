/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.HashSet;
import java.util.Set;
import org.openrdf.model.Statement;

/**
 *
 * @author Christian
 */
public class RDFData {
    
    Set<Statement> otherStatements;
    
    public RDFData(){
        otherStatements = new HashSet<Statement>();
    }
       
    public void addStatement(Statement statement){
        if (statement != null) {
            otherStatements.add(statement);
        }
    }
    
    final void addOthers(StringBuilder builder){
        for (Statement statememt: otherStatements){
            builder.append(statememt);
            newLine(builder);
        }        
    }
       
    final void newLine(StringBuilder builder){
        builder.append("\n");
    }

}
