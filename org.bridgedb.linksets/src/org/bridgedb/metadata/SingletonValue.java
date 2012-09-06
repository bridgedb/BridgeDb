/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.Set;
import org.bridgedb.metadata.ValueBase;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public class SingletonValue extends ValueBase{
    Value value;

    public SingletonValue(String name, URI predicate, Class type, RequirementLevel level){
        super(name, predicate, type, level);
        value = null;
    }
    
    @Override
    public void loadFromInput(MetaData metaData, RDFData input) {
        Value value = metaData.getByIdPredicate(input, predicate);
        if (this.value != null){
            throw new IllegalStateException("Value has already been set to " + this.value);
        }
        this.value = value;
    }
    
    Value getValue(){
        return value;
    }
            
    String getValueAsString(){
        if (value == null){
            return "null";
        }
        return value.stringValue();
    }

    @Override
    public boolean hasRequiredValues(RequirementLevel forceLevel, boolean exceptAlternatives) {
        if (level.compareTo(forceLevel) > 0) { return true; }
        return hasValue(exceptAlternatives);
    }

    @Override
    boolean hasValue(){
        return value != null;
    }

    @Override
    public boolean hasCorrectTypes() {
        if (value == null) { return true; }
        return correctType(value);
    }

    @Override
    public String toString(){
        return super.toString() + "\n\tValue: " + value;
    }

    @Override
    void appendFormatReport(StringBuilder builder) {
        builder.append("ERROR: " + name + " has a Value: " + value + "\n"
                + "\tValue has the class " + Value.class + "\n"
                + "\tThe Expected Type was " + type + ".\n");
    }

    @Override
    public void addInfo(StringBuilder builder, RequirementLevel forceLevel) {
        if ((level.compareTo(forceLevel) <= 0) || hasValue()){
            MetaData.tab(builder);
            builder.append(name);
            builder.append(": ");
            builder.append(getValueAsString());
            MetaData.newLine(builder);        
        }
    }


}
