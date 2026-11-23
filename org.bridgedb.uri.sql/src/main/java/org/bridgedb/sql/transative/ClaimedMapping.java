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

import java.util.Set;
import org.bridgedb.pairs.CodeMapper;
import org.bridgedb.pairs.IdSysCodePair;
import org.bridgedb.uri.api.Mapping;
import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author christian
 */
public abstract class ClaimedMapping extends Mapping{

    protected final IdSysCodePair idSysCodePairSource;
    protected final IdSysCodePair idSysCodePairTarget;

    public ClaimedMapping(IdSysCodePair idSysCodePairSource, IdSysCodePair idSysCodePairTarget, 
            String predicate, String justification, int mappingSetId, 
            String mappingResource, String mappingSource, String lens){
        super(predicate, justification, mappingSetId, 
            mappingResource, mappingSource, lens);
        this.idSysCodePairSource = idSysCodePairSource;
        this.idSysCodePairTarget = idSysCodePairTarget;
    }
    
    public ClaimedMapping (ClaimedMapping previous, DirectMapping newMapping, String predicate, 
            String justification) throws BridgeDBException{
        super(previous, newMapping, predicate, justification);
        this.idSysCodePairSource = previous.idSysCodePairSource;
        this.idSysCodePairTarget = newMapping.idSysCodePairTarget;
    }
    
    public ClaimedMapping (String uri, IdSysCodePair pair){
        super(uri, pair);
        this.idSysCodePairSource = pair;
        this.idSysCodePairTarget = pair;
    }

    public ClaimedMapping (String uri, Set<String> targetUris){
        super(uri, targetUris);
        this.idSysCodePairSource = null;
        this.idSysCodePairTarget = null;
    }

    public ClaimedMapping(IdSysCodePair pair){
        super();
        this.idSysCodePairSource = pair;
        this.idSysCodePairTarget = pair;
    }
    
    public abstract boolean createsLoop(IdSysCodePair targetRef);

    public abstract boolean hasMappingToSelf();

    public abstract Set<String> getSysCodesToCheck();

        public final String getSourceSysCode() {
        if (idSysCodePairSource != null){
            return idSysCodePairSource.getSysCode();
        } else if (source != null) {
            return source.getDataSource().getSystemCode();
        } else {
            return sourceUri.iterator().next();
        }
    }

    private final String getSourceId() {
        if (idSysCodePairSource != null){
            return idSysCodePairSource.getId();
        } else if (source != null) {
            return source.getId();
        } else {
            return sourceUri.iterator().next();
        }
    }
    
    public final IdSysCodePair getSourcePair() {
        if (idSysCodePairSource != null){
            return idSysCodePairSource;
        } else if (source != null) {
            return new IdSysCodePair(source.getId(), source.getDataSource().getSystemCode());
        } else {
            return null;
        }
    }
    
    public final String getTargetSysCode() {
        if (idSysCodePairTarget != null){
            return idSysCodePairTarget.getSysCode();
        } else if (target != null) {
            return target.getDataSource().getSystemCode();
        } else {
            return targetUri.iterator().next();            
        }
    }
    
    public final String getTargetName(){
        if (target != null){
            return target.getDataSource().getFullName();
        } else if (idSysCodePairTarget != null) {
            return idSysCodePairTarget.getSysCode();
        } else {
            return targetUri.iterator().next();            
        }
    }
    
    public final String getTargetId() {
        if (idSysCodePairTarget != null){
            return idSysCodePairTarget.getId();
        } else {
            return target.getId();
        }
    }

    public final IdSysCodePair getTargetPair() {
        if (idSysCodePairTarget != null){
            return idSysCodePairTarget;
        } else {
            return new IdSysCodePair(target.getId(), target.getDataSource().getSystemCode());
        }
    }

    @Override
    public int compareTo(Mapping other) {
        if (!(other instanceof ClaimedMapping)){
            return super.compareTo(other);
        }
        ClaimedMapping mapping = (ClaimedMapping)other;
        if (this.sourceUri.size() != 1 || mapping.getSourceUri().size() != 1){
            if (this.getSourceSysCode().compareTo(mapping.getSourceSysCode()) != 0){
                return this.getTargetId().compareTo(mapping.getTargetId());
            }
            if (this.getSourceId().compareTo(mapping.getSourceId()) != 0){
                return this.getTargetId().compareTo(mapping.getTargetId());
            }
        } else {
            String aSourceUri = sourceUri.iterator().next();
            String otherUri = mapping.getSourceUri().iterator().next();
            if (aSourceUri.compareTo(otherUri) != 0){
                return aSourceUri.compareTo(otherUri);
            }
        }
        if (this.getTargetName().compareTo(mapping.getTargetName()) != 0){
            return this.getTargetName().compareTo(mapping.getTargetName());
        }
        if (this.getTargetName().compareTo(mapping.getTargetName()) != 0){
            return this.getTargetName().compareTo(mapping.getTargetName());
        }
        if (this.getTargetId().compareTo(mapping.getTargetId()) != 0){
            return this.getTargetId().compareTo(mapping.getTargetId());
        }
        return 0;
    }
    
    @Override 
    public boolean equals(Object other){
        if (other instanceof Mapping){
            Mapping mapping = (Mapping)other;
            return (compareTo(mapping) == 0);
        }
        return false;
    }

    public final void setTargetXrefs(CodeMapper codeMapper) throws BridgeDBException {
        //if (idSysCodePairTarget != null){
            setTarget(codeMapper.toXref(idSysCodePairTarget));
        //}
        if (getViaMappings() != null){
            for (Mapping via:getViaMappings()){
                if (via instanceof ClaimedMapping){
                    ((ClaimedMapping)via).setTargetXrefs(codeMapper);
                }
            }
        }
    }

    @Override
    public String toString(){
        StringBuilder output = new StringBuilder("mapping ");
        for (String sourceUri:getSourceUri()){
            output.append("\n\tSourceUri: ");
            output.append(sourceUri);
        }
        output.append("\n\tSource: ");
        if (getSource() != null){
            output.append(getSource());
        } else {
            output.append(this.idSysCodePairSource);            
        }
        output.append("\n\tPredicate(): ");
        output.append(getPredicate());
        for (String targetUri:getTargetUri()){
            output.append("\n\tTargetUri: ");
            output.append(targetUri);
        }
        output.append("\n\tTarget: ");
        if (getTarget() != null){
            output.append(getTarget()); 
        } else {
            output.append(this.idSysCodePairTarget); 
            
        }
        output.append("\n\tMappingSet(id): ");
        output.append(this.getMappingSetId());
        output.append("\n");
        return output.toString();
    }



}
