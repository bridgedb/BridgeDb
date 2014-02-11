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
import org.bridgedb.uri.api.MappingsBySet;
import org.bridgedb.uri.api.SetMappings;
import org.bridgedb.uri.api.UriMapping;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="MappingBySet")
public class MappingsBySetBean {
    
    private String lens;
    private Set<SetMappingBean> mappingSet;
    private Set<UriMappingBean> mapping;

    /**
     * WS Constructor
     */
    public MappingsBySetBean(){
        mappingSet = new HashSet<SetMappingBean>();
        mapping = new HashSet<UriMappingBean>();
    }
    
    public MappingsBySetBean(MappingsBySet mappingsBySet) {
        setLens(mappingsBySet.getLens());
        mappingSet = new HashSet<SetMappingBean>();
        for (SetMappings setMapping:mappingsBySet.getSetMappings()){
            mappingSet.add(new SetMappingBean(setMapping));
        }
        mapping = new HashSet<UriMappingBean>();
        for (UriMapping uriMapping:mappingsBySet.getMappings()){
            mapping.add(new UriMappingBean(uriMapping));
        }

    }
    
    public final MappingsBySet asMappingsBySet(){
        MappingsBySet mappingsBySet = new MappingsBySet(getLens());
        for (SetMappingBean setMappingBean:getMappingSet()){
           mappingsBySet.addSetMapping(setMappingBean.asSetMapping());
       }
       for (UriMappingBean uriMappingBean:getMapping()){
           mappingsBySet.addMapping(uriMappingBean.asUriMapping());
       }
       return mappingsBySet;
    }

    /**
     * @return the lens
     */
    public String getLens() {
        return lens;
    }

    /**
     * @param lens the lens to set
     */
    public void setLens(String lens) {
        this.lens = lens;
    }

    /**
     * @return the mappingSet
     */
    public Set<SetMappingBean> getMappingSet() {
        return mappingSet;
    }

    /**
     * @param mappingSet the mappingSet to set
     */
    public void setMappingSet(Set<SetMappingBean> mappingSet) {
        this.mappingSet = mappingSet;
    }

    /**
     * @return the mapping
     */
    public Set<UriMappingBean> getMapping() {
        return mapping;
    }

    /**
     * @param mapping the mapping to set
     */
    public void setMapping(Set<UriMappingBean> mapping) {
        this.mapping = mapping;
    }

 
}
