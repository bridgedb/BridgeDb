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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * Holder class for the main Meta Data of MappingSet.
 *
 * Does not include everything in the void header but only what is captured in the SQL.
 * @author Christian
 * @deprecated 
 */
public class MappingsBySet {
    private final String lens;
    private final Set<SetMappings> setMappings;
    
    static final Logger logger = Logger.getLogger(MappingsBySet.class);
    
    /*
     * These are the direct mappings based on namespace substitution
     */
    private final Set<UriMapping> mappings;
    
    /**
     * @deprecated 
     * @param lens
     * @param mappings 
     */
    public MappingsBySet(String lens, Set<Mapping> mappings){
        this.lens = lens;
        this.setMappings = new HashSet<SetMappings>();
        this.mappings = new HashSet<UriMapping>();
        for (Mapping mapping:mappings){
            if (mapping.getJustification() == null){
                addUriMapping(mapping);
            } else {
                addSetMapping(mapping);
            }
        }
    }
    
    private void addUriMapping(Mapping mapping) {
        for (String source:mapping.getSourceUri()){
            for (String target: mapping.getTargetUri()){
                mappings.add(new UriMapping(source, target));
            }
        }
    }

    private void addSetMapping(Mapping mapping) {   
        String mappingId = toString(mapping.getMappingSetId());
        SetMappings setMapping = new SetMappings(mappingId, mapping.getPredicate(), 
                mapping.getJustification(), mapping.getMappingSource());
        for (String source:mapping.getSourceUri()){
            for (String target: mapping.getTargetUri()){
                setMapping.addMapping(new UriMapping(source, target));
            }
        }
        setMappings.add(setMapping);
    }

    
    /**
    public void addMappings (Set<String> mappingSetIds, String predicate, String justification, String mappingSource, 
            String mappingResource, String sourceUri, Set<String> targetUris){
        String mappingId = sortAndString(mappingSetIds);
        SetMappings setMapping = setMappingById(mappingId);
        if (setMapping == null){
            setMapping = new SetMappings(mappingId, predicate, justification, mappingSource, mappingResource);
            setMappings.add(setMapping);
        }
        for (String targetUri: targetUris){
            setMapping.addMapping(new UriMapping(sourceUri, targetUri));
        }
    }

    public void addSetMapping(SetMappings setMapping){
        setMappings.add(setMapping);
    }
    
    public void addMapping (Set<String> mappingSetIds, String predicate, String justification, String mappingSource, 
            String mappingResource, String sourceUri, String targetUri){
        String mappingSetId = sortAndString(mappingSetIds);
        SetMappings setMapping = setMappingById(mappingSetId);
        if (setMapping == null){
            setMapping = new SetMappings(mappingSetId, predicate, justification, mappingSource, mappingResource);
            getSetMappings().add(setMapping);
        }
        setMapping.addMapping(new UriMapping(sourceUri, targetUri));
    }
    
    public final void addMapping (String sourceUri, String targetUri){
        mappings.add(new UriMapping(sourceUri, targetUri));
    }
    
    public final void addMapping (UriMapping uriMapping){
        mappings.add(uriMapping);
    }
    
    public void addMappings (String sourceUri, Set<String> targetUris){
       for (String targetUri:targetUris){
           addMapping(sourceUri, targetUri);
       }
    }

    public void addMappings (MappingsBySet other){
        for (UriMapping uriMapping: other.getMappings()){
            addMapping(uriMapping);
        }
        for (SetMappings setMapping: other.getSetMappings()){
            addSetMapping(setMapping);
        }
    }
    */
    private SetMappings setMappingById(String id) {
        for (SetMappings setMapping: getSetMappings()){
            if (setMapping.getId().equals(id)){
                return setMapping;
            }
        }
        return null;
    }
    
    /**
     * @deprecated 
     * @return 
     */
    public Set<String> getTargetUris(){
        HashSet<String> targetUris = new HashSet<String>();
        for (SetMappings setMapping: getSetMappings()){
            targetUris.addAll(setMapping.getTargetUris());           
        }
        for (UriMapping mapping:getMappings()){
            targetUris.add(mapping.getTargetUri());
        }

        return targetUris;
    }

    /**
     * @deprecated 
     * @return 
     */
    public String toString(){
        StringBuilder sb = new StringBuilder("Lens: ");
        sb.append(getLens());
        for (SetMappings setMapping: getSetMappings()){
            setMapping.append(sb);           
        }
        sb.append("\n\tUriSpace based mappings");
        for (UriMapping mapping:getMappings()){
            mapping.append(sb);
        }
        return sb.toString();
    }

    /**
     * @deprecated 
     * @return the lens
     */
    public String getLens() {
        return lens;
    }

    /**
     * @deprecated 
     * @return the setMappings
     */
    public Set<SetMappings> getSetMappings() {
        return setMappings;
    }

    /**
     * @deprecated 
     * @return the mappings
     */
    public Set<UriMapping> getMappings() {
        return mappings;
    }
    
    /**
     * @deprecated 
     * @return 
     */
    public boolean isEmpty(){
        return mappings.isEmpty() && setMappings.isEmpty();
    }

    private String toString(List<String> mappingSetIds) {
        if (mappingSetIds == null || mappingSetIds.isEmpty()){
            return "";
        }
        if (mappingSetIds.size() == 1){
            return mappingSetIds.get(0);
        }
        StringBuilder sb = new StringBuilder(mappingSetIds.get(0));
        for (int i = 1; i < mappingSetIds.size(); i++){
            sb.append("_").append(mappingSetIds.get(i));
        }
        return sb.toString();
    }

 }
