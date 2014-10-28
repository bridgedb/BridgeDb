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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
public class Mapping implements Comparable<Mapping>{

    private final IdSysCodePair idSysCodePairSource;
    private final IdSysCodePair idSysCodePairTarget;
    private final String justification;
    private final String lens;
    private final List<String> mappingSetId;
    private final String predicate;
    private final String mappingSource;
        
    //These are set later if required        
    private Xref source = null;
    private Xref target = null;
    private Set<String> sourceUri = new HashSet<String>();
    private Set<String> targetUri = new HashSet<String>();
    
    private List<Xref> viaXref = new ArrayList<Xref>();
    
    public Mapping(IdSysCodePair idSysCodePairSource, IdSysCodePair idSysCodePairTarget, 
            String predicate, String justification, int mappingSetId, String mappingSource, String lens){
        this.idSysCodePairSource = idSysCodePairSource;
        this.idSysCodePairTarget = idSysCodePairTarget;
        this.predicate = predicate;
        this.justification = justification;
        this.mappingSetId = new ArrayList<String>();
        this.mappingSetId.add(""+mappingSetId);
        this.lens = lens;     
        this.mappingSource = mappingSource;
    }
    
    public Mapping(IdSysCodePair idSysCodePairSource, IdSysCodePair idSysCodePairTarget, 
            String predicate, String justification, List<String> mappingSetIds, 
            String mappingSource, String lens){
        this.idSysCodePairSource = idSysCodePairSource;
        this.idSysCodePairTarget = idSysCodePairTarget;
        this.predicate = predicate;
        this.justification = justification;
        this.mappingSetId = mappingSetIds;
        this.lens = lens;
        this.mappingSource = mappingSource;
    }
    
    public Mapping(IdSysCodePair pair){
        this.idSysCodePairSource = pair;
        this.idSysCodePairTarget = pair;
        this.predicate = null;
        this.justification = null;
        this.mappingSetId = new ArrayList<String>();
        this.lens = null;     
        this.mappingSource = null;
    }

     public Mapping (Xref source, String predicate, Xref target, 
            List<String> mappingSetIds, String lens){
        this.idSysCodePairSource = null;
        this.idSysCodePairTarget = null;
        this.justification = null;
        this.sourceUri = new HashSet<String>();
        this.source = source;
        this.targetUri = new HashSet<String>();
        this.target = target;
        this.mappingSetId = mappingSetIds;
        this.predicate = predicate;
        this.lens = lens;
        this.mappingSource = null;
    }

    public Mapping (IdSysCodePair idSysCodePairSource, Xref source, String predicate,
             IdSysCodePair idSysCodePairTarget, Xref target, 
            List<String> mappingSetIds, String lens){
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
    }

    public Mapping (String sourceUri, String predicate, List<String> mappingSetIds, String lens){
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
    }

     public Mapping (String uri, Set<String> targetUris){
        this.idSysCodePairSource = null;
        this.idSysCodePairTarget = null;
        this.justification = null;
        this.sourceUri = new HashSet<String>();
        this.sourceUri.add(uri);
        this.source = null;
        this.targetUri.add(uri);
        this.target = null;
        this.mappingSetId = new ArrayList<String>();
        this.predicate = null;
        this.lens = null;
        this.mappingSource = null;
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
        this.mappingSetId = new ArrayList<String>();
        this.predicate = null;
        this.lens = null;
        this.mappingSource = null;
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
        output.append(getMappingSetId());
        if (this.getViaXref() != null && !this.getViaXref().isEmpty()){
            output.append("\n\tViaXrefs ");
            for (Xref xref:this.getViaXref()){
                output.append("\n\t\t").append(xref);            
            }
        }
        output.append("\n");
        return output.toString();
    }
   
    /**
     * @return the mappingSetId
     */
    public final List<String> getMappingSetId() {
        return mappingSetId;
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

    public final String getSourceId() {
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
            return new IdSysCodePair(source.getId(), target.getDataSource().getSystemCode());
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
        ArrayList myIds = new ArrayList(getMappingSetId());
        Collections.sort(myIds);
        List otherIds = new ArrayList(mapping.getMappingSetId());
        Collections.sort(otherIds);
        Iterator<String> myIt = myIds.iterator();
        Iterator<String> otherIt = otherIds.iterator();
        while (myIt.hasNext() && otherIt.hasNext()){
            String my = myIt.next();
            String other = otherIt.next();
            if (my.compareTo(other) != 0) {
                return my.compareTo(other);
            }
        }
        if (myIt.hasNext()){
            return +1;
        }
        if (otherIt.hasNext()){
            return -1;
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

    /**
     * @return the viaXref
     */
    public List<Xref> getViaXref() {
        return viaXref;
    }

    /**
     * @param viaXref the viaXref to set
     */
    public void setViaXref(List<Xref> viaXref) {
        this.viaXref = viaXref;
    }
 
}