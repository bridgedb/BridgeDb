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

}
