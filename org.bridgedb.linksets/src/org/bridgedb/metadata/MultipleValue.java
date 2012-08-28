/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.HashSet;
import java.util.Set;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public class MultipleValue extends ValueBase{
    Set<Value> values;

    public MultipleValue(String name, URI predicate, Class type, RequirementLevel level){
        super(name, predicate, type, level);
        values = null;
    }
    
    @Override
    public boolean multipleValuesAllowed() {
        return true;
    }

    @Override
    void addValue(Value value) {
        if (values == null){
            values = new HashSet<Value>();
        }
        values.add(value);
    }

    @Override
    boolean hasValue() {
        return values != null;
    }
    
    String[] getValuesAsString(){
        String[] results;
        if (values == null || values.isEmpty()){
            results = new String[1];
            results[0] = "null";
        } else {
            results = new String[values.size()]; 
            int i = 0;
            for (Value value: values){
               results[i] = value.stringValue();
               i++;
           }
        }
        return results;
    }

    @Override
    boolean correctType() {
        if (values == null){ return true; } //no type issue if there is nothing to type
        for (Value value:values){
            if (!correctType(value)) { return false; }
        }
        return true;
    }

    @Override
    void appendFormatReport(StringBuilder builder) {
        builder.append("ERROR: " + name + " has an expected type of " + type + "\n");
        builder.append("\tThe following Values where found to be incorrect\n");
        for (Value value:values){
            if (!correctType(value)) { 
                 builder.append("\t" + value + " of class " + value.getClass() + "\n");
            }
        }
    }

    @Override
    void show(StringBuilder builder) {
        MetaData.tab(builder);
        builder.append(name);
        builder.append(": ");
        String[] values = getValuesAsString();
        builder.append(values[0]);
        MetaData.newLine(builder);        
        for (int i = 1; i< values.length; i++){
            MetaData.tab(builder);
            MetaData.tab(builder);
            builder.append(values[i]);
            MetaData.newLine(builder);        
        }
    }

}
