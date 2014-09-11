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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bridgedb.pairs.IdSysCodePair;
import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author christian
 */
public class TransitiveMapping extends AbstractMapping {
    private final List<DirectMapping> via;
    private static final String NEW_LINE = System.getProperty("line.separator");
    
    private final Set<String> sysCodesToCheck;
    private boolean includesMappingToSelf = false;
    
    public TransitiveMapping (AbstractMapping previous, DirectMapping newMapping) throws BridgeDBException{
        super(previous.getSource(), newMapping.getTarget());
        //Never expected but just in case
        if (!previous.getTarget().equals(newMapping.getSource())){
            throw new BridgeDBException ("Unexpected broken mapping chain");
        }
        via = createVia(previous, newMapping);
        sysCodesToCheck = recordSysCodes(previous, newMapping);
    }
    
    private List<DirectMapping> createVia(AbstractMapping previous, DirectMapping newMapping){
        List<DirectMapping> newVia;
        if (previous instanceof DirectMapping ){
            newVia = new ArrayList<DirectMapping>();
            newVia.add((DirectMapping)previous);
        } else {
            TransitiveMapping previousT = (TransitiveMapping)previous;  
            newVia = new ArrayList<DirectMapping>(previousT.getVia());
        }
        newVia.add(newMapping);
        return newVia;
    }

    private Set<String> recordSysCodes(AbstractMapping previous, DirectMapping newMapping) throws BridgeDBException {
        //Check if new mapping is mapping to self.
        Set<String> syscodes;
        if (newMapping.getSource().getSysCode().equals(newMapping.getTarget().getSysCode())){
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
        syscodes.add(newMapping.getTarget().getSysCode());
        return syscodes;
    }


    List<DirectMapping> getVia() {
        return via;
    }
    
    boolean createsLoop(IdSysCodePair targetRef){
        return sysCodesToCheck.contains(targetRef.getSysCode());
    }

    public String toString(){
        StringBuilder builder = new StringBuilder(super.toString());
        for (DirectMapping mapping:via){
            builder.append(NEW_LINE).append("\t").append(mapping);
        }
        return builder.toString();
    }

    @Override
    boolean hasMappingToSelf() {
        return includesMappingToSelf;
    }

    @Override
    Set<String> getSysCodesToCheck() {
        return sysCodesToCheck;
    }


}
/*
//Assume it is just adding
    private void addSysCode(DirectMapping newMapping) throws BridgeDBException {
        if (newMapping.getSource().getSysCode().equals(newMapping.getTarget().getSysCode())){
            if (includesMappingToSelf){
                throw new BridgeDBException("Unexpected seond mapping to self");
            }
            includesMappingToSelf = true;
            inboundSyscodes.add(newMapping.getTarget().getSysCode());
        } else if (includesMappingToSelf){
            outboundSyscodes.add(newMapping.getTarget().getSysCode());
        } else {
            inboundSyscodes.add(newMapping.getTarget().getSysCode());
        }
    }


*/