/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bridgedb.metadata.RequirementLevel;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public abstract class ValueBase {
    
    protected final String name;
    protected URI predicate;
    protected final Class type;
    protected final RequirementLevel level;
    protected List<ValueBase> alternatives;
    
    public ValueBase(String name, URI predicate, Class type, RequirementLevel level){
        this.name = name;
        this.type = type;
        this.level = level;
        this.predicate = predicate;
    }
    
    public abstract boolean multipleValuesAllowed();
    
    public void addAlternative(ValueBase alternative){
        if (alternatives == null){
            alternatives = new ArrayList<ValueBase>();
        }
        alternatives.add(alternative);
    }

    abstract void addValue(Value value);
   
    abstract boolean hasValue();
    
    public String toString(){
        return ("ValueBase : " + name + "\n\ttype: " + type + "\n\tlevel: " + level + "\n\tpredicate: " + predicate);
    }
}
