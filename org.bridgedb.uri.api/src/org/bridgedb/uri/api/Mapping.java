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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.bridgedb.Xref;
import org.bridgedb.pairs.IdSysCodePair;

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
public class Mapping {
 
    private final IdSysCodePair idSysCodePairSource;
    private final IdSysCodePair idSysCodePairTarget;
    private final String justification;
    private final String lens;
    private final Set<String> mappingSetId;
    private final String predicate;
    private final String mappingSource;
    private final String mappingResource;
        
    
    //These are set later if required        
    private Xref source = null;
    private Xref target = null;
    private Set<String> sourceUri = new HashSet<String>();
    private Set<String> targetUri = new HashSet<String>();
    
    public Mapping(IdSysCodePair idSysCodePairSource, IdSysCodePair idSysCodePairTarget, 
            String predicate, String justification, int mappingSetId, 
            String mappingSource, String mappingResource, String lens){
        this.idSysCodePairSource = idSysCodePairSource;
        this.idSysCodePairTarget = idSysCodePairTarget;
        this.predicate = predicate;
        this.justification = justification;
        this.mappingSetId = new HashSet<String>();
        this.mappingSetId.add(""+mappingSetId);
        this.lens = lens;     
        this.mappingSource = mappingSource;
        this.mappingResource = mappingResource;
    }
    
    public Mapping(IdSysCodePair idSysCodePairSource, IdSysCodePair idSysCodePairTarget, 
            String predicate, String justification, Set<String> mappingSetIds, 
            String mappingSource, String mappingResource, String lens){
        this.idSysCodePairSource = idSysCodePairSource;
        this.idSysCodePairTarget = idSysCodePairTarget;
        this.predicate = predicate;
        this.justification = justification;
        this.mappingSetId = mappingSetIds;
        this.lens = lens;
        this.mappingSource = mappingSource;
        this.mappingResource = mappingResource;
    }
    
    public Mapping(IdSysCodePair pair){
        this.idSysCodePairSource = pair;
        this.idSysCodePairTarget = pair;
        this.predicate = null;
        this.justification = null;
        this.mappingSetId = new HashSet<String>();
        this.lens = null;     
        this.mappingSource = null;
        this.mappingResource = null;
    }

    public Mapping (IdSysCodePair idSysCodePairSource, Xref source, String predicate,
             IdSysCodePair idSysCodePairTarget, Xref target, 
            Set<String> mappingSetIds, String lens){
        this.idSysCodePairSource = idSysCodePairSource;
        this.idSysCodePairTarget = idSysCodePairTarget;
        this.justification = null;
        this.sourceUri = new HashSet<String>();
        this.source = source;
        this.targetUri = new HashSet<String>();
        this.target = target;
        this.mappingSetId = mappingSetIds;
        this.predicate = predicate;
        this.lens = lens;
        this.mappingSource = null;
        this.mappingResource = null;
    }

    public Mapping (String sourceUri, String predicate, Set<String> mappingSetIds, String lens){
        this.idSysCodePairSource = null;
        this.idSysCodePairTarget = null;
        this.justification = null;
        this.sourceUri = new HashSet<String>();
        this.sourceUri.add(sourceUri);
        this.source = null;
        this.targetUri = new HashSet<String>();
        this.target = null;
        this.mappingSetId = mappingSetIds;
        this.predicate = predicate;
        this.lens = lens;
        this.mappingSource = null;
        this.mappingResource = null;
    }

    /**
     * This is the constructor for a mapping to self.
     * 
     * @param xref
     */
    public Mapping (Xref xref, String lens){
        this.idSysCodePairSource = null;
        this.idSysCodePairTarget = null;
        this.justification = null;
        this.sourceUri = new HashSet<String>();
        this.source = xref;
        this.targetUri = new HashSet<String>();
        this.target = xref;
        this.mappingSetId = null;
        this.predicate = null;
        this.lens = lens;
        this.mappingSource = null;
        this.mappingResource = null;
    }

    public Mapping (String uri, IdSysCodePair pair){
        this.idSysCodePairSource = pair;
        this.idSysCodePairTarget = pair;
        this.justification = null;
        this.sourceUri = new HashSet<String>();
        this.sourceUri.add(uri);
        this.source = null;
        this.targetUri = new HashSet<String>();
        this.targetUri.add(uri);
        this.target = null;
        this.mappingSetId = null;
        this.predicate = null;
        this.lens = null;
        this.mappingSource = null;
        this.mappingResource = null;
    }
    
    /**
     * @return the sourceUris
     */
    public Set<String> getSourceUri() {
        return sourceUri;
    }
 
    public void addSourceUri(String sourceUri){
        getSourceUri().add(sourceUri);
    }
    
    public void addSourceUris(Collection<String> sourceUris){
        getSourceUri().addAll(sourceUris);
    }
    
    /**
     * @return the target Uris
     */
    public Set<String> getTargetUri() {
        return targetUri;
    }

    public void addTargetUri(String targetUri){
        getTargetUri().add(targetUri);
    }

    public void addTargetUris(Collection<String> extraTargetUris){
        getTargetUri().addAll(extraTargetUris);
    }

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
        output.append(getMappingSetId());
        output.append("\n");
        return output.toString();
    }
   
    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other instanceof Mapping){
            Mapping otherMapping = (Mapping)other;
           if (!otherMapping.sourceUri.equals(sourceUri)) return false;
            if (!otherMapping.targetUri.equals(targetUri)) return false;
            if (!otherMapping.getMappingSetId().equals(getMappingSetId())) return false;
            //No need to check predicate as by defintion one id has one predicate
            return true;
         } else {
            return false;
        }
    }

    /**
     * @return the mappingSetId
     */
    public Set<String> getMappingSetId() {
        return mappingSetId;
    }

    /**
     * @return the predicate
     */
    public String getPredicate() {
        return predicate;
    }

    /**
     * @return the source
     */
    public Xref getSource() {
        return source;
    }

    /**
     * @return the target
     */
    public Xref getTarget() {
        return target;
    }
    
    /**
     * @param sourceUris the sourceUris to set
     */
    public void setSourceUri(Set<String> sourceUris) {
        this.sourceUri = sourceUris;
    }

    /**
     * @param targetUris the targetUris to set
     */
    public void setTargetUri(Set<String> targetUris) {
        this.targetUri = targetUris;
    }

    public String getLens() {
        return lens;
    }

    /**
     * @return the idSysCodePairSource
     */
    public IdSysCodePair getIdSysCodePairSource() {
        return idSysCodePairSource;
    }

    /**
     * @return the idSysCodePairTarget
     */
    public IdSysCodePair getIdSysCodePairTarget() {
        return idSysCodePairTarget;
    }

    /**
     * @return the justification
     */
    public String getJustification() {
        return justification;
    }

    /**
     * @param source the source to set
     */
    public void setSource(Xref source) {
        this.source = source;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(Xref target) {
        this.target = target;
    }

    /**
     * @return the mappingSource
     */
    public String getMappingSource() {
        return mappingSource;
    }

    /**
     * @return the mappingResource
     */
    public String getMappingResource() {
        return mappingResource;
    }

    /**
     * @param predicate the predicate to set
     * /
    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }
    /**/
 }
