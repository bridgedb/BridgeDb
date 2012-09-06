/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public class RDFData {
    
    private Set<Statement> unusedStatements;
    private Set<Statement> usedStatements;
    Resource id; 

    public RDFData(){
        unusedStatements = new HashSet<Statement>();
        usedStatements = new HashSet<Statement>();
    }
       
    public void addStatement(Statement statement){
        if (statement != null) {
            //ystem.out.println(statement);
            unusedStatements.add(statement);
        }
    }
    
    final void addUnusedStatements(StringBuilder builder){
        for (Statement statememt: unusedStatements){
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

    public boolean hasPredicateObject(URI predicate, Value object){
        for (Statement statement: unusedStatements){
            if (statement.getPredicate().equals(predicate)){
                if (statement.getObject().equals(object)){
                    return true;
                }
            }
        }  
        for (Statement statement: usedStatements){
            if (statement.getPredicate().equals(predicate)){
                if (statement.getObject().equals(object)){
                    return true;
                }
            }
        }  
        return false;
    }
    
    final Resource getUnusedByPredicateObject(RDFData input, URI predicate, Value object){
        for (Statement statement: input.unusedStatements){
            if (statement.getPredicate().equals(predicate)){
                if (statement.getObject().equals(object)){
                    input.unusedStatements.remove(statement);
                    input.usedStatements.add(statement);
                    usedStatements.add(statement);
                    return statement.getSubject();
                }
            }
        }  
        return null;
    }

    final Resource getAnyByPredicateObject(RDFData input, URI predicate, Value object){
        Resource reply = getUnusedByPredicateObject(input, predicate, object);
        if (reply != null) { return reply; }
        for (Statement statement: input.usedStatements){
            if (statement.getPredicate().equals(predicate)){
                if (statement.getObject().equals(object)){
                    usedStatements.add(statement);
                    return statement.getSubject();
                }
            }
        }  
        return null;
    }

    Value getByIdPredicate(RDFData input, URI predicate){
        for (Statement statement: input.unusedStatements){
            if (statement.getSubject().equals(id)){
                if (statement.getPredicate().equals(predicate)){
                    input.unusedStatements.remove(statement);
                    input.usedStatements.add(statement);
                    usedStatements.add(statement);
                    return statement.getObject();
                }
            }
        }  
        for (Statement statement: input.usedStatements){
            if (statement.getSubject().equals(id)){
                if (statement.getPredicate().equals(predicate)){
                    usedStatements.add(statement);
                    return statement.getObject();
                }
            }
        }  
        return null;
    }
    
    Set<Value> getAllByIdPredicate(RDFData input, URI predicate){
        HashSet<Value> results = new HashSet<Value>();
        for (Iterator<Statement> iterator = input.unusedStatements.iterator(); iterator.hasNext();) {
            Statement statement = iterator.next();
            if (statement.getSubject().equals(id)){
                if (statement.getPredicate().equals(predicate)){
                    iterator.remove();
                    input.usedStatements.add(statement);
                    usedStatements.add(statement);
                    results.add (statement.getObject());
                }
            }
        }  
        for (Statement statement: input.usedStatements){
            if (statement.getSubject().equals(id)){
                if (statement.getPredicate().equals(predicate)){
                    usedStatements.add(statement);
                    results.add (statement.getObject());
                }
            }
        }  
        return results;
    }

    void copyUnusedStatements(RDFData input){
        for (Iterator<Statement> iterator = input.unusedStatements.iterator(); iterator.hasNext();) {
            Statement statement = iterator.next();
            if (statement.getSubject().equals(id)){
                iterator.remove();
                unusedStatements.add(statement);
            }
        }  
    }
    
    public String showAllAsRDF(){
        StringBuilder builder = new StringBuilder();
        for (Statement statememt: usedStatements){
            builder.append(statememt);
            builder.append("\n");
        }
        addUnusedStatements(builder);
        return builder.toString();
    }
    
    public String toString(){
        StringBuilder builder = new StringBuilder();
        for (Statement statement: usedStatements){
            addStatement(builder, statement);
        } 
        for (Statement statement: unusedStatements){
            addStatement(builder, statement);
        } 
        return builder.toString();
    }

}
