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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
@XmlRootElement(name="mappings")
public class MappingsBean {
    
    private Set<MappingBean> Mapping;
    
    /**
     * Default constructor for webService
     */
    public MappingsBean(){
        Mapping = new HashSet<MappingBean>();
    }
    
    public MappingsBean (Set<Mapping> mappings){
        Mapping = new HashSet<MappingBean>();
        for (Mapping aMapping:mappings){
            Mapping.add(MappingBean.asBean(aMapping));;
        }
     }

    public Set<Mapping> asMappings (){
        HashSet<Mapping> result = new HashSet<Mapping>();
        for (MappingBean bean:Mapping){
            result.add(MappingBean.asMapping(bean));
        }
        return result;
    }

    public List<MappingBean> asMappingBeanList (){
        return new ArrayList<MappingBean> (Mapping);
    }

   /**
     * @return the mapping
     */
    public Set<MappingBean> getMapping() {
        return Mapping;
    }

    /**
     * @param mapping the mapping to set
     */
    public void setMapping(Set<MappingBean> mapping) {
        this.Mapping = mapping;
    }
    
  
 }
