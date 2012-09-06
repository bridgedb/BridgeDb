/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bridgedb.linkset.constants.RdfConstants;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public abstract class MetaData extends RDFData {
    
    List<MetaPart> metaParts = new ArrayList<MetaPart> ();
   
    static final String CLEAR_REPORT = "No issues found";

    public MetaData(Resource id, RDFData input){
        setupValues();
        this.id = id;
        readFromInput(input);
    }
       
    public MetaData(RDFData input){
        setupValues();
        id = getAnyByPredicateObject(input, RdfConstants.TYPE_URI, getResourceType());
        readFromInput(input);
    }

    abstract void setupValues();
    
    abstract URI getResourceType();
    
    void readFromInput(RDFData input) {
        for (MetaPart metaPart:metaParts){
            metaPart.loadFromInput(this, input);
        }
        copyUnusedStatements(input);
    }

    public boolean hasRequiredValues(RequirementLevel forceLevel){
        for (MetaPart metaPart:metaParts){
            if (!metaPart.hasRequiredValues(forceLevel)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean hasCorrectTypes(){
        for (MetaPart metaPart:metaParts){
            if (!metaPart.hasCorrectTypes()){
                System.out.println(metaPart);
                return false;
            }
        }
        return true;
    }
    
    void validityReport(StringBuilder builder, RequirementLevel forceLevel, boolean includeWarnings){
        for (MetaPart metaPart:metaParts){
            metaPart.appendValidityReport(builder, this, forceLevel, includeWarnings);
        }        
    }
    
    public String validityReport(RequirementLevel forceLevel, boolean includeWarnings){
        StringBuilder builder = new StringBuilder();
        validityReport(builder, forceLevel, includeWarnings);
        if (builder.length() == 0){
            return CLEAR_REPORT;
        }
        return builder.toString();
    }

    void addInfo(StringBuilder builder, RequirementLevel forceLevel){
        builder.append("ID: ");
        builder.append(id);
        newLine(builder);
        for (MetaPart metaPart:metaParts){
            metaPart.addInfo(builder, forceLevel);
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
