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
import java.util.Set;

/**
 * Holder class for the main Meta Data of the relevant mappings in a single Set plus some set data.
 *
 * Does not include everything in the void header but only what is captured in the SQL.
 * @author Christian
 */
public class SetMappings {
    private final int id;
    private final String predicate;
    private final String justification;
    private final String mappingSource;
    private final String mappingResource;
    private final Set<UriMapping> mappings;
    
    public static final String METHOD_NAME = "mappingSet";
    public static final String URI_PREFIX = "/" + METHOD_NAME + "/";
    public static final String HAS_LENS = "http://www.bridgedb.org/rdf/fulfillsLens" ;
    
    public SetMappings(int id, String predicate, String justification, String mappingSource, String mappingResource){
        this.id = id;
        this.predicate = predicate;
        this.justification = justification;
        this.mappingSource = mappingSource;
        this.mappingResource = mappingResource;
        this.mappings = new HashSet<UriMapping>();
    }
    
    public void addMapping(UriMapping mapping){
        getMappings().add(mapping);
    }

    /**
     * @return the intId
     */
    public int getId() {
        return id;
    }

    /**
     * @return the predicate
     */
    public String getPredicate() {
        return predicate;
    }

    /**
     * @return the justification
     */
    public String getJustification() {
        return justification;
    }

    /**
     * @return the mappingSource
     */
    public String getMappingSource() {
        return mappingSource;
    }

    /**
     * @return the mappings
     */
    public Set<UriMapping> getMappings() {
        return mappings;
    }

    HashSet<String> getTargetUris() {
        HashSet<String> targetUris = new HashSet<String>();
        for (UriMapping mapping:mappings){
            targetUris.add(mapping.getTargetUri());
        }
        return targetUris;
    }

    void append(StringBuilder sb) {
        sb.append("\n\tid: ");
        sb.append(id);
        sb.append("\n\tpredicate: ");
        sb.append(predicate);
        sb.append("\n\tjustification: ");
        sb.append(justification);
        sb.append("\n\tmappingSource: ");
        sb.append(mappingSource);
        for (UriMapping mapping:mappings){
            mapping.append(sb);
        }
    }

    /**
     * @return the mappingResource
     */
    public String getMappingResource() {
        return mappingResource;
    }


  }
