// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.sql.transative;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bridgedb.pairs.IdSysCodePair;
import org.bridgedb.sql.justification.JustificationMaker;
import org.bridgedb.sql.predicate.PredicateMaker;
import org.bridgedb.sql.transative.DirectMapping;
import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author christian
 */
public class MappingsHandlers {
    
    private final IdSysCodePair sourceRef;
    private final ArrayDeque<AbstractMapping> toCheck = new ArrayDeque<AbstractMapping>();
    private final Set<IdSysCodePair> checkedPairs = new HashSet<IdSysCodePair>();
    private final Set<AbstractMapping> mappings = new HashSet<AbstractMapping>();
    private final TransitiveChecker transitiveChecker;  
    private final PredicateMaker predicateMaker;
    private final JustificationMaker justificationMaker;
    
    
    public MappingsHandlers(IdSysCodePair sourceRef, TransitiveChecker transitiveChecker, 
            PredicateMaker predicateMaker, JustificationMaker justificationMaker){
        this.sourceRef = sourceRef;
        checkedPairs.add(sourceRef);
        this.transitiveChecker = transitiveChecker;
        this.predicateMaker = predicateMaker;
        this.justificationMaker = justificationMaker;
    }

    private void addMapping(AbstractMapping mapping){
        //ystem.out.println("+ " + mapping);
        toCheck.push(mapping);
        mappings.add(mapping);
        checkedPairs.add(mapping.getTarget());
    }
    
    public final void addMappings(Set<DirectMapping> newMappings) {
        for (DirectMapping mapping: newMappings){
            if (checkedPairs.contains(mapping.getTarget())){
                //ystem.out.println("Duplicate " + mapping.getTarget());
            } else {
                addMapping(mapping);
            }
        }
    }

    public final boolean moreToCheck() {
        return !toCheck.isEmpty();
    }

    public final AbstractMapping nextToCheck() {
        return toCheck.pop();
    }

    public final void addMapping(AbstractMapping previous, DirectMapping newMapping) throws BridgeDBException {   
        IdSysCodePair targetRef = newMapping.getTarget();
        if (!transitiveChecker.allowTransitive(previous, newMapping)){
            System.out.println("Ignoring transative via " + newMapping.getSource().getSysCode());
            return;
        //}  else {
            //ystem.out.println("OK transative via " + newMapping.getSource().getSysCode());                
        }
        if (checkedPairs.contains(targetRef)){
            //ystem.out.println("Duplicate " + targetRef);
            return;
        } 
        if (previous.createsLoop(targetRef)){
            //ystem.out.println("Loop " + targetRef);        
            return;
        }
        String predicate = predicateMaker.possibleCombine(previous.getPredicate(), newMapping.getPredicate());
        if (predicate == null){
            //ystem.out.println("unable to combine " + previous.getPredicate() + " and " +  newMapping.getPredicate());
            return;
        }
        String justification = justificationMaker.possibleCombine(previous.getJustification(), newMapping.getJustification());
        if (justification == null){
            //ystem.out.println("unable to combine " + previous.getJustification() + " and " +  newMapping.getJustification());
            return;
        }            
        TransitiveMapping transitiveMapping = new TransitiveMapping(previous, newMapping, predicate, justification);
        //ystem.out.println("Adding " + transitiveMapping);
        addMapping(transitiveMapping);
    }
 
    public final void addMappings(AbstractMapping previous, Set<DirectMapping> newMappings) throws BridgeDBException {   
        for (DirectMapping newMapping: newMappings){
            this.addMapping(previous, newMapping);
        }
    }
    
    public final Set<AbstractMapping> getMappings() {
        return mappings;
    }
}
