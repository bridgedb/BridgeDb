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
    private final ArrayDeque<AbstractMapping> toCheck = new ArrayDeque<AbstractMapping>();
    private final Set<IdSysCodePair> checkedPairs = new HashSet<IdSysCodePair>();
    private final Set<AbstractMapping> mappings = new HashSet<AbstractMapping>();
            
    public TransitiveMappings(IdSysCodePair sourceRef){
        this.sourceRef = sourceRef;
        checkedPairs.add(sourceRef);
    }

    private void addMapping(AbstractMapping mapping){
        toCheck.push(mapping);
        mappings.add(mapping);
        checkedPairs.add(mapping.getTarget());
    }
    
    void addMappings(Set<DirectMapping> newMappings) {
        for (DirectMapping mapping: newMappings){
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

    AbstractMapping nextToCheck() {
        return toCheck.pop();
    }

    void addMappings(AbstractMapping previous, Set<DirectMapping> transitives) {
        for (DirectMapping transitive: transitives){
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

    Set<AbstractMapping> getMappings() {
        return mappings;
    }
}
