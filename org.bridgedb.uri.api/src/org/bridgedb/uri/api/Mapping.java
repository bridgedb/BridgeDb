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
package org.bridgedb.uri.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bridgedb.Xref;
import org.bridgedb.pairs.CodeMapper;
import org.bridgedb.pairs.IdSysCodePair;
import org.bridgedb.utils.BridgeDBException;

/**
 * Contains the information held for a particular mapping.
 * <p>
 * @See getMethods for what is returned.
 * <p>
 * A few things that are not returned and why included:
 * <ul>
 * <li>UriSpace: 
 * @author Christian
 */
public class Mapping implements Comparable<Mapping>{

    //USed During Mapping Construction and filling out and transitive calculations
    private final IdSysCodePair idSysCodePairSource;
    private final IdSysCodePair idSysCodePairTarget;
    
    //Both ASSERTED and TRANSITIVE mappings have these
    private final String justification;
    private final String lens;
    private final String predicate;

    //Only ASSERTED mappings have these
    private final String mappingResource;
    private final String mappingSource;
    private final String id;
    
    //Only TRANSITIVE mappings have these
    private final List<Mapping> viaMappings;
    
    //All three types can have these
    //These are set later if required        
    private Xref source = null;
    private Xref target = null;
    private Set<String> sourceUri = new HashSet<String>();
    private Set<String> targetUri = new HashSet<String>();
    
    /**
     * 
     * @param idSysCodePairSource
     * @param idSysCodePairTarget
     * @param predicate
     * @param justification
     * @param mappingSetId
     * @param mappingResource
     * @param mappingSource
     * @param lens 
     */
    public Mapping(IdSysCodePair idSysCodePairSource, IdSysCodePair idSysCodePairTarget, 
            String predicate, String justification, int mappingSetId, String mappingResource, String mappingSource, String lens){
        this.idSysCodePairSource = idSysCodePairSource;
        this.idSysCodePairTarget = idSysCodePairTarget;
        this.predicate = predicate;
        this.justification = justification;
        this.id = "" + mappingSetId;
        this.lens = lens;     
        this.mappingResource = mappingResource;
        this.mappingSource = mappingSource;
        viaMappings = new ArrayList<Mapping>();
    }
    
    public Mapping(Mapping previous, Mapping newMapping, String predicate, String justification){
        this.idSysCodePairSource = previous.idSysCodePairSource;
        this.idSysCodePairTarget = newMapping.idSysCodePairTarget;
        this.predicate = predicate;
        this.justification = justification;
        this.lens = previous.lens;
 
        this.id = previous.id + "_" + newMapping.id;
        
        this.mappingResource = null;
        this.mappingSource = null;
        
        viaMappings = new ArrayList<Mapping>();
        if (previous.isTransitive()){
            this.viaMappings.addAll(previous.getViaMappings());
        } else {
            this.viaMappings.add(previous);
        }

        if (newMapping.isTransitive()){
            this.viaMappings.addAll(newMapping.viaMappings);
        } else {
            this.viaMappings.add(newMapping);
        }
    }
    
    public Mapping(IdSysCodePair pair){
        this.idSysCodePairSource = pair;
        this.idSysCodePairTarget = pair;
        this.predicate = null;
        this.justification = null;
        this.id = "self";
        this.lens = null;     
        this.mappingResource = null;
        this.mappingSource = null;
        viaMappings = new ArrayList<Mapping>();
    }

    //From Bean
    public Mapping(Xref source, Xref target, Set<String> sourceUri, Set<String> targetUri, 
            String justification, String predicate, String lens, String mappingResource, String mappingSource, 
            String mappingSetId, List<Mapping> vias){
        this.idSysCodePairSource = null;
        this.idSysCodePairTarget = null;
        this.source = source;
        this.target = target;
        this.sourceUri = sourceUri;
        this.targetUri = targetUri;
        this.predicate = predicate;
        this.justification = justification;
        this.id = mappingSetId;
        this.lens = lens;
        this.mappingResource = mappingResource;
        this.mappingSource = mappingSource;
        this.viaMappings = vias;
    }

     public Mapping (String uri, Set<String> targetUris){
        this.idSysCodePairSource = null;
        this.idSysCodePairTarget = null;
        this.sourceUri.add(uri);
        this.targetUri.addAll(targetUris);
        this.predicate = null;
        this.justification = null;
        this.id = "self";
        this.lens = null;     
        this.mappingResource = null;
        this.mappingSource = null;
        viaMappings = new ArrayList<Mapping>();
    }
    
    public Mapping (String uri, IdSysCodePair pair){
        this.idSysCodePairSource = pair;
        this.idSysCodePairTarget = pair;
        this.sourceUri.add(uri);
        this.targetUri.add(uri);
        this.predicate = null;
        this.justification = null;
        this.id = null;
        this.lens = null;     
        this.mappingResource = null;
        this.mappingSource = null;
        viaMappings = new ArrayList<Mapping>();
    }
    
    /**
     * @return the sourceUris
     */
    public final Set<String> getSourceUri() {
        return sourceUri;
    }
 
    public final void addSourceUri(String sourceUri){
        getSourceUri().add(sourceUri);
    }
    
    public final void addSourceUris(Collection<String> sourceUris){
        getSourceUri().addAll(sourceUris);
    }
    
    /**
     * @return the target Uris
     */
    public final Set<String> getTargetUri() {
        return targetUri;
    }

    public final void addTargetUri(String targetUri){
        getTargetUri().add(targetUri);
    }

    public final void addTargetUris(Collection<String> extraTargetUris){
        getTargetUri().addAll(extraTargetUris);
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
        output.append(id);
        output.append("\n");
        return output.toString();
    }

    public final String getMappingSetId(){
        return id;
    }
    /**
     * @return the predicate
     */
    public final String getPredicate() {
        return predicate;
    }

    /**
     * @return the source
     */
    public final Xref getSource() {
        return source;
    }

    /**
     * @return the target
     */
    public final Xref getTarget() {
        return target;
    }
    
    /**
     * @param sourceUris the sourceUris to set
     */
    public final void setSourceUri(Set<String> sourceUris) {
        this.sourceUri = sourceUris;
    }

    /**
     * @param targetUris the targetUris to set
     */
    public final void setTargetUri(Set<String> targetUris) {
        this.targetUri = targetUris;
    }

    public final String getLens() {
        return lens;
    }

    /**
     * @return the justification
     */
    public final String getJustification() {
        return justification;
    }

    /**
     * @param source the source to set
     */
    public final void setSource(Xref source) {
        this.source = source;
    }

    /**
     * @param target the target to set
     */
    public final void setTarget(Xref target) {
        this.target = target;
    }

    /**
     * @return the mappingResource
     */
    public String getMappingResource() {
        return mappingResource;
    }

    /**
     * @return the mappingSource
     */
    public final String getMappingSource() {
        return mappingSource;
    }

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
    public int compareTo(Mapping mapping) {
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
                via.setTargetXrefs(codeMapper);
            }
        }
    }

    /**
     * @return the viaMappings
     */
    public List<Mapping> getViaMappings() {
        return viaMappings;
    }

    public final boolean isMappingToSelf() {
        return predicate == null;
    }
    
    public final boolean isTransitive() {
         return !viaMappings.isEmpty();
    }
    
}