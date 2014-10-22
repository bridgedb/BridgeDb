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

import org.bridgedb.sql.transative.DirectMapping;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bridgedb.Xref;
import org.bridgedb.pairs.CodeMapper;
import org.bridgedb.pairs.IdSysCodePair;
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.uri.api.Mapping;
import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author christian
 */
public class TransitiveMapping extends IDSysCodePairMapping {
    private final List<DirectMapping> via;
    private static final String NEW_LINE = System.getProperty("line.separator");
    
    private final Set<String> sysCodesToCheck;
    private boolean includesMappingToSelf = false;

    public TransitiveMapping (IDSysCodePairMapping previous, DirectMapping newMapping, String predicate, String justification) throws BridgeDBException{
        super(previous.getSourcePair(), newMapping.getTargetPair(), 
                predicate, justification, mergeIds(previous, newMapping), 
                BridgeDBConstants.TRANSITIVE, BridgeDBConstants.TRANSITIVE, newMapping.getLens());
        //Never expected but just in case
        if (!previous.getTargetPair().equals(newMapping.getSourcePair())){
            throw new BridgeDBException ("Unexpected broken mapping chain");
        }
        via = createVia(previous, newMapping);
        sysCodesToCheck = recordSysCodes(previous, newMapping);
    }
    
    private static List<String> mergeIds(IDSysCodePairMapping previous, DirectMapping newMapping){
        ArrayList<String> results = new ArrayList<String>(previous.getMappingSetId());
        results.add(newMapping.getId());
        return results;
    } 
            
    private List<DirectMapping> createVia(IDSysCodePairMapping previous, DirectMapping newMapping){
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

    private Set<String> recordSysCodes(IDSysCodePairMapping previous, DirectMapping newMapping) throws BridgeDBException {
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


    List<DirectMapping> getVia() {
        return via;
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

    @Override
    public void setTargetXrefs(CodeMapper codeMapper) throws BridgeDBException {
        setTarget(codeMapper.toXref(getTargetPair()));
        List<Xref> vaiXref = new ArrayList<Xref>();
        for (DirectMapping aVia:via){
            aVia.setTargetXrefs(codeMapper);
            vaiXref.add(aVia.getTarget());
        }
        this.setViaXref(vaiXref);
    }

}
