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
package org.bridgedb.uri.ws.bean;

import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.uri.api.Mapping;

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
@XmlRootElement(name="Mapping")
public class UriMappings {
 
    private Set<String> targetUri;
    
    /**
     * Default constructor for webService
     */
    public UriMappings(){
        targetUri = new HashSet<String>();
    }
    
    public UriMappings(Set<String> targetUri){
        this.targetUri = targetUri;
    }
    
    public static UriMappings asBean(Set<String> targets){
        UriMappings bean = new UriMappings();
        bean.setTargetUri(targets);
        return bean;
    }

    public static UriMappings toBean(Set<Mapping> mappings){
        UriMappings bean = new UriMappings();
        bean.targetUri = new HashSet<String>();
        for (Mapping mapping:mappings){
            if (mapping.getTargetUri() != null){
                bean.targetUri.addAll(mapping.getTargetUri());
            }
        }
        return bean;
    }

    /**
     * @return the targetUri
     */
    public Set<String> getTargetUri() {
        return targetUri;
    }

    /**
     * @param targetUri the targetUri to set
     */
    public void setTargetUri(Set<String> targetUri) {
        this.targetUri = targetUri;
    }
 
    public String toString(){
        return targetUri.toString();
    }

    public boolean isEmpty() {
        return targetUri.isEmpty();
    }
 }
