/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridgedb.sql;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import org.bridgedb.pairs.IdSysCodePair;

/**
 *
 * @author christian
 */
public class TransitiveMappings {
    
    private final IdSysCodePair sourceRef;
    private final ArrayDeque<TransitiveMapping> toCheck = new ArrayDeque<TransitiveMapping>();
    private final Set<IdSysCodePair> checkedPairs = new HashSet<IdSysCodePair>();
    private final Set<TransitiveMapping> mappings = new HashSet<TransitiveMapping>();
            
    public TransitiveMappings(IdSysCodePair sourceRef){
        this.sourceRef = sourceRef;
        checkedPairs.add(sourceRef);
    }

    private void addMapping(TransitiveMapping mapping){
        toCheck.push(mapping);
        mappings.add(mapping);
        checkedPairs.add(mapping.getTarget());
    }
    
    void addMappings(Set<TransitiveMapping> newMappings) {
        for (TransitiveMapping mapping: newMappings){
            if (checkedPairs.contains(mapping.getTarget())){
                System.out.println("Duplicate " + mapping.getTarget());
            } else {
                addMapping(mapping);
            }
        }
    }

    boolean moreToCheck() {
        return !toCheck.isEmpty();
    }

    TransitiveMapping nextToCheck() {
        return toCheck.pop();
    }

    void addMappings(TransitiveMapping toCheck, Set<TransitiveMapping> transitives) {
        for (TransitiveMapping transitive: transitives){
            IdSysCodePair targetRef = transitive.getTarget();
            System.out.println(targetRef);
            System.out.println(checkedPairs);
            if (checkedPairs.contains(targetRef)){
                System.out.println("Duplicate " + targetRef);
            } else {
                addMapping(transitive);
            }
        }
    }

    Set<TransitiveMapping> getMappings() {
        return mappings;
    }
}
