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
package org.bridgedb.sql;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bridgedb.pairs.IdSysCodePair;

/**
 *
 * @author christian
 */
public class MappingsHandlers {
    
    private final IdSysCodePair sourceRef;
    private final ArrayDeque<AbstractMapping> toCheck = new ArrayDeque<AbstractMapping>();
    private final Set<IdSysCodePair> checkedPairs = new HashSet<IdSysCodePair>();
    private final Set<AbstractMapping> mappings = new HashSet<AbstractMapping>();
            
    public MappingsHandlers(IdSysCodePair sourceRef){
        this.sourceRef = sourceRef;
        checkedPairs.add(sourceRef);
    }

    private void addMapping(AbstractMapping mapping){
        System.out.println("+ " + mapping);
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

    void addMappings(AbstractMapping previous, Set<DirectMapping> newMappings) {
        for (DirectMapping newMapping: newMappings){
            IdSysCodePair targetRef = newMapping.getTarget();
            if (checkedPairs.contains(targetRef)){
                System.out.println("Duplicate " + targetRef);
            } else {
                System.out.println(targetRef);
                System.out.println(checkedPairs);
                List<DirectMapping> via;
                if (previous instanceof DirectMapping ){
                    via = new ArrayList<DirectMapping>();
                    via.add((DirectMapping)previous);
                } else {
                    TransitiveMapping previousT = (TransitiveMapping)previous;  
                    via = new ArrayList<DirectMapping>(previousT.getVia());
                }
                via.add(newMapping);
                TransitiveMapping transitiveMapping = new TransitiveMapping(previous.getSource(), targetRef, via);
                addMapping(transitiveMapping);
            }
        }
    }

    Set<AbstractMapping> getMappings() {
        return mappings;
    }
}
