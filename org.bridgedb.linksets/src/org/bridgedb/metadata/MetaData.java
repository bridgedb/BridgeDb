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
import org.bridgedb.linkset.constants.DulConstants;
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
    
    List<ValueBase> values;
    
    static final String CLEAR_REPORT = "No issues found";

    public MetaData(Resource id, RDFData input){
        values = new ArrayList<ValueBase> ();
        setupValues();
        this.id = id;
        readFromInput(input);
    }
       
    public MetaData(RDFData input){
        values = new ArrayList<ValueBase> ();
        setupValues();
        id = getAnyByPredicateObject(input, RdfConstants.TYPE_URI, getResourceType());
        readFromInput(input);
    }

    abstract void setupValues();
    
    abstract URI getResourceType();
    
    void readFromInput(RDFData input) {
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
        copyUnusedStatements(input);
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
    
    void validityReport(StringBuilder builder, RequirementLevel forceLevel, boolean exceptAlternatives, 
            boolean includeWarnings){
        for (ValueBase valueBase:values){
            valueBase.appendValidityReport(builder, this, forceLevel, exceptAlternatives, includeWarnings);
        }        
    }
    
    public String validityReport(RequirementLevel forceLevel, boolean exceptAlternatives, boolean includeWarnings){
        StringBuilder builder = new StringBuilder();
        validityReport(builder, forceLevel, exceptAlternatives, includeWarnings);
        if (builder.length() == 0){
            return CLEAR_REPORT;
        }
        return builder.toString();
    }

    void addInfo(StringBuilder builder, RequirementLevel forceLevel){
        builder.append("ID: ");
        builder.append(id);
        newLine(builder);
        for (ValueBase valueBase:values){
            if ((valueBase.level.compareTo(forceLevel) <= 0) || valueBase.hasValue()){
                valueBase.show(builder);
            }
        }
        addNamedChildren(builder);
        addUnusedStatements(builder);        
        addChildren(builder, forceLevel);
    }
    
    void addNamedChildren(StringBuilder builder) {
        //No named children here
    }

    void addChildren(StringBuilder builder,  RequirementLevel forceLevel) {
        //No children here
    }

    public String showAll(RequirementLevel forceLevel){
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().toString());
        newLine(builder);
        addInfo(builder, forceLevel);
        return builder.toString();
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


 

}
