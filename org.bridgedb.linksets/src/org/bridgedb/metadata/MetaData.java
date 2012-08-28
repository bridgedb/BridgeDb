/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bridgedb.linkset.constants.RdfConstants;
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
    
    static final String CLEAR_REPORT = "No issues found";

    public MetaData(Resource id, RDFData input){
        rawStatements = new HashSet<Statement>();
        values = new ArrayList<ValueBase> ();
        setupValues();
        this.id = id;
        readFromInput(input);
    }
       
    public MetaData(RDFData input){
        rawStatements = new HashSet<Statement>();
        values = new ArrayList<ValueBase> ();
        setupValues();
        id = getByPredicateObject(input, RdfConstants.TYPE_URI, getResourceType());
        readFromInput(input);
    }

    abstract void setupValues();
    
    abstract URI getResourceType();
    
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
    
    public boolean hasRequiredValues(RequirementLevel forceLevel, boolean exceptAlternatives){
        for (ValueBase valueBase:values){
            if ((valueBase.level.compareTo(forceLevel) <= 0) && !valueBase.hasValue(exceptAlternatives)){
                return false;
            }
        }
        return true;
    }
    
    public boolean hasCorrectTypes(){
        for (ValueBase valueBase:values){
            if (valueBase.hasValue()){
                if (!valueBase.correctType()){
                    return false;
                }
            }
        }
        return true;
    }
    
    public String validityReport(RequirementLevel forceLevel, boolean exceptAlternatives, boolean includeWarnings){
        StringBuilder builder = new StringBuilder();
        for (ValueBase valueBase:values){
            valueBase.appendValidityReport(builder, forceLevel, exceptAlternatives, includeWarnings);
        }
        if (builder.length() == 0){
            return CLEAR_REPORT;
        }
        return builder.toString();
    }

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
    
    Resource getByPredicateObject(URI predicate, Value object){
        return getByPredicateObject(this, predicate, object);
    }
    
    Resource getByPredicateObject(RDFData input, URI predicate, Value object){
        for (Statement statement: input.otherStatements){
            if (statement.getPredicate().equals(predicate)){
                if (statement.getObject().equals(object)){
                    input.otherStatements.remove(statement);
                    rawStatements.add(statement);
                    return statement.getSubject();
                }
            }
        }  
        return null;
    }

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
        for (Iterator<Statement> iterator = input.otherStatements.iterator(); iterator.hasNext();) {
            Statement statement = iterator.next();
            if (statement.getSubject().equals(id)){
                if (statement.getPredicate().equals(predicate)){
                    iterator.remove();
                    rawStatements.add(statement);
                    results.add (statement.getObject());
                }
            }
        }  
        return results;
    }

    void copyById(RDFData input){
        for (Iterator<Statement> iterator = input.otherStatements.iterator(); iterator.hasNext();) {
            Statement statement = iterator.next();
            if (statement.getSubject().equals(id)){
                iterator.remove();
                rawStatements.add(statement);
            }
        }  
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        for (Statement statement: otherStatements){
            addStatement(builder, statement);
        } 
        for (Statement statement: rawStatements){
            addStatement(builder, statement);
        } 
        return builder.toString();
    }
}
