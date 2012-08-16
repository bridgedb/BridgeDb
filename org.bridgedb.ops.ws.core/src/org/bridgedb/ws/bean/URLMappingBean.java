// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
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
package org.bridgedb.ws.bean;

import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="URLMapping")
public class URLMappingBean {
    Integer id;
    //Name is single as XML looks better this way
    Set<String> sourceURL;
    //Name is single as XML looks better this way
    Set<String> targetURL;
    private Integer mappingSetId;
    String predicate;
    
    //Webservice constructor
    public URLMappingBean(){
    }

    URLMappingBean(Integer id, Set<String> sourceURLs, String predicate, Set<String> targetURLs, Integer mappingSetId) {
        this.id = id;
        this.sourceURL = sourceURLs;
        this.predicate = predicate;
        this.targetURL = targetURLs;
        this.mappingSetId = mappingSetId;
    }
    
    public String toString(){
           return  "URLMapping: id: " + this.getId() + this.getSourceURL() + " " + this.getPredicate() + 
                   " " + this.getTargetURL() + " id: " + this.getId() + " mappingSet: " + this.getMappingSetId();
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the sourceURL
     */
    public Set<String> getSourceURL() {
        return sourceURL;
    }

    /**
     * @param sourceURL the sourceURL to set
     */
    public void setSourceURL(Set<String> sourceURLs) {
        this.sourceURL = sourceURLs;
    }

    /**
     * @return the targetURL
     */
    public Set<String> getTargetURL() {
        return targetURL;
    }

    /**
     * @param targetURL the targetURL to set
     */
    public void setTargetURL(Set<String> targetURLs) {
        this.targetURL = targetURLs;
    }

    /**
     * @return the predicate
     */
    public String getPredicate() {
        return predicate;
    }

    /**
     * @param predicate the predicate to set
     */
    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    /**
     * @return the mappingSetId
     */
    public Integer getMappingSetId() {
        return mappingSetId;
    }

    /**
     * @param mappingSetId the mappingSetId to set
     */
    public void setMappingSetId(Integer mappingSetId) {
        this.mappingSetId = mappingSetId;
    }
    
}
