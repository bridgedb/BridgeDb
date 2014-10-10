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
 
    private Xref source;
    private Xref target;
    
    private Set<String> sourceUri;
    private Set<String> targetUri;
    private Set<String> mappingSetId;
    private String predicate;
    
    /**
     * Default constructor for webService
     */
    public Mapping(){
        this.sourceUri = new HashSet<String>();
        this.targetUri = new HashSet<String>();
    }
    
    public Mapping (Xref source, String predicate, Xref target, Set<String> mappingSetIds){
        this.sourceUri = new HashSet<String>();
        this.source = source;
        this.targetUri = new HashSet<String>();
        this.target = target;
        this.mappingSetId = new HashSet<String>();
        this.mappingSetId = mappingSetIds;
        this.predicate = predicate;
    }

    /**
     * This is the constructor for a mapping to self.
     * 
     * @param id
     * @param sysCode 
     */
    public Mapping (Xref xref){
        this.sourceUri = new HashSet<String>();
        this.source = xref;
        this.targetUri = new HashSet<String>();
        this.target = xref;
        this.mappingSetId = null;
        this.predicate = null;
    }

    private boolean mapToSelf(){
        return source == target;
    }

    /**
     * @return the sourceUris
     */
    public Set<String> getSourceUri() {
        return sourceUri;
    }
 
    public void addSourceUri(String sourceUri){
        getSourceUri().add(sourceUri);
        if (mapToSelf()){
            getTargetUri().add(sourceUri);
        }
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
        if (getSource() != null){
            output.append("\n\tSource: ");
            output.append(getSource());
        }
        output.append("\n\tPredicate(): ");
        output.append(getPredicate());
        for (String targetUri:getTargetUri()){
            output.append("\n\tTargetUri: ");
            output.append(targetUri);
        }
        if (getTarget() != null){
            output.append("\n\tTarget: ");
            output.append(getTarget()); 
        }
        output.append("\n\tMappingSet(id): ");
        output.append(getMappingSetId());
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

    /**
     * @param predicate the predicate to set
     */
    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

 }
