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

import java.util.HashSet;
import java.util.Set;
import org.bridgedb.pairs.IdSysCodePair;
import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author christian
 */
public class TransitiveMapping extends ClaimedMapping {
    private static final String NEW_LINE = System.getProperty("line.separator");
    
    private final Set<String> sysCodesToCheck;
    private boolean includesMappingToSelf = false;

    public TransitiveMapping (ClaimedMapping previous, DirectMapping newMapping, String predicate, 
            String justification) throws BridgeDBException{
        super(previous, newMapping, predicate, justification);
        //Never expected but just in case
        if (!previous.getTargetPair().equals(newMapping.getSourcePair())){
            throw new BridgeDBException ("Unexpected broken mapping chain");
        }
        sysCodesToCheck = recordSysCodes(previous, newMapping);
    }
    
    private Set<String> recordSysCodes(ClaimedMapping previous, DirectMapping newMapping) throws BridgeDBException {
        //Check if new mapping is mapping to self.
        //stem.out.println("recording System codes");
        //ystem.out.println(previous);
        //ystem.out.println(newMapping);
        Set<String> syscodes;
        if (newMapping.getSourceSysCode().equals(newMapping.getTargetSysCode())){
            if (previous.hasMappingToSelf()){
                throw new BridgeDBException("Two mappings to self in same chain.");
            } else {
                //Ony code that matters is the last mapping which is added at the end.
                syscodes = new HashSet<String>();
                includesMappingToSelf = true;
            }
        } else {
            includesMappingToSelf = previous.hasMappingToSelf();
            syscodes = new HashSet<String>(previous.getSysCodesToCheck());  
        }
        syscodes.add(newMapping.getTargetSysCode());
        //ystem.out.println("==" + syscodes + "==");
        return syscodes;
    }
    
    public boolean createsLoop(IdSysCodePair targetRef){
        //ystem.out.println ("c " + this);
        //ystem.out.println (targetRef);
        //Check if incoming is a mapping to self (Same syscode)
        if (getTargetSysCode().equals(targetRef.getSysCode())){
            //Only allow one per transitive
            //ystem.out.println("mapping to self");
            return includesMappingToSelf;
        }
        //ystem.out.println ("!= " + getIdSysCodePairTarget().getSysCode());
        //ystem.out.println(sysCodesToCheck);
        //The target must be a new one
        return sysCodesToCheck.contains(targetRef.getSysCode());
    }

    @Override
    public boolean hasMappingToSelf() {
        return includesMappingToSelf;
    }

    @Override
    public Set<String> getSysCodesToCheck() {
        return sysCodesToCheck;
    }

}
