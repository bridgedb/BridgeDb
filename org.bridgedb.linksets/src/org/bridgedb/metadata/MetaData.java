/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.algebra.StatementPattern;

/**
 *
 * @author Christian
 */
public abstract class MetaData extends RDFData {
    
    Resource id; 

    Set<Statement> rawStatements;
    
    List<ValueBase> values;

    public MetaData(Resource id, RDFData input){
        rawStatements = new HashSet<Statement>();
        values = new ArrayList<ValueBase> ();
        setupValues();
        this.id = id;
        readFromInput(input);
    }
       
    abstract void setupValues();
    
    private void readFromInput(RDFData input) {
        for (ValueBase valueBase:values){
            if (valueBase.multipleValuesAllowed()){
               Set<Value> values = getAllByIdPredicate(input, valueBase.predicate);
               for (Value value:values){
                    valueBase.addValue(value);
               }
            } else {
                Value value = getByIdPredicate(input, valueBase.predicate);
                valueBase.addValue(value);
            }
        }
        copyById(input);
    }

    public void addStatement(Statement statement){
        otherStatements.add(statement);
    }
    
    //Check if the minimal information to load the linkset is provided.
    public abstract boolean isMinimallyValid();

    //Check if all the MUST information is provided.
    public abstract boolean isValid();
    
    //Check if all the SHOULD information is provided.
    public abstract boolean isFullValid();
    
    public abstract String validityReport(boolean full);

    public String showAllAsRDF(){
        StringBuilder builder = new StringBuilder();
        for (Statement statememt: rawStatements){
            builder.append(statememt);
            builder.append("\n");
        }
        addOthers(builder);
        return builder.toString();
    }
    
    public String showAll(RequirementLevel forceLevel){
        StringBuilder builder = new StringBuilder();
        builder.append("ID: ");
        builder.append(id);
        newLine(builder);
        for (ValueBase valueBase:values){
            if ((valueBase.level.compareTo(forceLevel) <= 0) || valueBase.hasValue()){
                if (valueBase instanceof SingletonValue){
                    builder.append(valueBase.name);
                    builder.append(": ");
                    builder.append(((SingletonValue)valueBase).getValueAsString());
                    newLine(builder);        
                } else if (valueBase instanceof  MultipleValue){
                    
                } else {
                    throw new UnsupportedOperationException("Unexpected ValueBase Class " + valueBase.getClass());
                }
            }
        }
        addOthers(builder);
        return builder.toString();
    }
    
    /*Resource getByPredicateObject(URI predicate, Value object){
        for (Statement statement: otherStatements){
            if (statement.getPredicate().equals(predicate)){
                if (statement.getObject().equals(object)){
                    otherStatements.remove(statement);
                    rawStatements.add(statement);
                    return statement.getSubject();
                }
            }
        }  
        return null;
    }*/
    
    Value getByIdPredicate(RDFData input, URI predicate){
        for (Statement statement: input.otherStatements){
            if (statement.getSubject().equals(id)){
                if (statement.getPredicate().equals(predicate)){
                    input.otherStatements.remove(statement);
                    rawStatements.add(statement);
                    return statement.getObject();
                }
            }
        }  
        return null;
    }

    /*Set<Value> getAllBySubjectPredicate(Resource subject, URI predicate){
        HashSet<Value> results = new HashSet<Value>();
        for (Statement statement: otherStatements){
            if (statement.getSubject().equals(subject)){
                if (statement.getPredicate().equals(predicate)){
                    otherStatements.remove(statement);
                    rawStatements.add(statement);
                    results.add (statement.getObject());
                }
            }
        }  
        return results;
    }*/

    Set<Value> getAllByIdPredicate(RDFData input, URI predicate){
        HashSet<Value> results = new HashSet<Value>();
        for (Statement statement: input.otherStatements){
            if (statement.getSubject().equals(id)){
                if (statement.getPredicate().equals(predicate)){
                    input.otherStatements.remove(statement);
                    rawStatements.add(statement);
                    results.add (statement.getObject());
                }
            }
        }  
        return results;
    }

    void copyById(RDFData input){
        for (Statement statement: input.otherStatements){
            if (statement.getSubject().equals(id)){
                input.otherStatements.remove(statement);
                rawStatements.add(statement);
            }
        }  
    }

}
