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
    public boolean multipleValuesAllowed() {
        return false;
    }

    @Override
    void addValue(Value value) {
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
    boolean hasValue() {
        return value != null;
    }
    
    
}
