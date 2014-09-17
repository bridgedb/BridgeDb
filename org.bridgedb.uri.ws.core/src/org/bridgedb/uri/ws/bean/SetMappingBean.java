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
import org.bridgedb.uri.api.SetMappings;
import org.bridgedb.uri.api.UriMapping;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="SetMapping")
public class SetMappingBean {
    private String id;
    private String predicate;
    private String justification;
    private String mappingSource;
    private String mappingResource;
    private Set<UriMappingBean> mapping;

    /**
     * WS Constructor
     */
    public SetMappingBean(){
        mapping = new HashSet<UriMappingBean>();
    }
    
    public final SetMappings asSetMapping(){
       SetMappings setMappings = 
               new SetMappings(getId(), getPredicate(), getJustification(), getMappingSource(), getMappingResource());
       for (UriMappingBean uriMappingBean:getMapping()){
           setMappings.addMapping(uriMappingBean.asUriMapping());
       }
       return setMappings;
    }

    public SetMappingBean(SetMappings setMapping) {
        setId(setMapping.getId());
        setPredicate(setMapping.getPredicate());
        setJustification(setMapping.getJustification());
        setMappingSource(setMapping.getMappingSource());
        setMappingResource(setMapping.getMappingResource());
        mapping = new HashSet<UriMappingBean>();
        for (UriMapping uriMapping:setMapping.getMappings()){
            mapping.add(new UriMappingBean(uriMapping));
        }
    }

    /**
     * @return the id
     */
    public final String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public final void setId(String id) {
        this.id = id;
    }

    /**
     * @return the predicate
     */
    public final String getPredicate() {
        return predicate;
    }

    /**
     * @param predicate the predicate to set
     */
    public final void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    /**
     * @return the justification
     */
    public final String getJustification() {
        return justification;
    }

    /**
     * @param justification the justification to set
     */
    public final void setJustification(String justification) {
        this.justification = justification;
    }

    /**
     * @return the mappingSource
     */
    public final String getMappingSource() {
        return mappingSource;
    }

    /**
     * @param mappingSource the mappingSource to set
     */
    public final void setMappingSource(String mappingSource) {
        this.mappingSource = mappingSource;
    }

    /**
     * @return the mapping
     */
    public final Set<UriMappingBean> getMapping() {
        return mapping;
    }

    /**
     * @param mapping the mapping to set
     */
    public final void setMapping(Set<UriMappingBean> mapping) {
        this.mapping = mapping;
    }

    /**
     * @return the mappingResource
     */
    public String getMappingResource() {
        return mappingResource;
    }

    /**
     * @param mappingResource the mappingResource to set
     */
    public void setMappingResource(String mappingResource) {
        this.mappingResource = mappingResource;
    }
    
  
}
