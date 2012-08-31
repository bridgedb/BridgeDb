/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.HashSet;
import java.util.Set;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

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
            tab(builder);
            builder.append(statememt);
            newLine(builder);
        }        
    }
       
    static final void newLine(StringBuilder builder){
        builder.append("\n");
    }

    static final void tab(StringBuilder builder){
        builder.append("\t");
    }

    void addStatement(StringBuilder builder, Statement statement){
        builder.append(statement.getSubject());
        builder.append(" ");
        builder.append(statement.getPredicate());
        builder.append(" ");
        builder.append(statement.getObject());
        builder.append("\n");        
    }

    public boolean hasPredicate(URI predicate){
        for (Statement statement: otherStatements){
            if (statement.getPredicate().equals(predicate)){
                return true;
            }
        }   
        return false;
    }
    
    public String toString(){
        StringBuilder builder = new StringBuilder();
        for (Statement statement: otherStatements){
            addStatement(builder, statement);
        } 
        return builder.toString();
    }
}
