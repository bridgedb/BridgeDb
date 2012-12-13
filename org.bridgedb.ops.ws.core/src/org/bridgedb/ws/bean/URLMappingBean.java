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
    private XrefBean source;
    //Name is single as XML looks better this way
    Set<String> targetURL;
    private XrefBean target;
    private Integer mappingSetId;
    String predicate;
    
    //Webservice constructor
    public URLMappingBean(){
    }

    URLMappingBean(Integer id, Set<String> sourceURLs, XrefBean source, String predicate, 
            Set<String> targetURLs, XrefBean target, Integer mappingSetId) {
        this.id = id;
        this.sourceURL = sourceURLs;
        this.source = source;
        this.predicate = predicate;
        this.targetURL = targetURLs;
        this.target = target;
        this.mappingSetId = mappingSetId;
    }
    
    public String toString(){
        StringBuilder builder = new StringBuilder ("URLMapping: id: ");
        builder.append(this.getId());
        if (this.getSourceURL() != null){
            builder.append(this.getSourceURL());
            builder.append(" ");
        }
        if (this.getSource() != null){
            builder.append(this.getSourceURL());
            builder.append(" ");
        }
        builder.append(this.getPredicate());
        builder.append(" ");
        if (this.getTargetURL() != null){
            builder.append(this.getTargetURL());
            builder.append(" id: ");
        }
        if (this.getTarget() != null){
            builder.append(this.getTarget());
            builder.append(" id: ");
        }
        builder.append(this.getId());
        builder.append(" mappingSet: ");
        builder.append(this.getMappingSetId());
        return builder.toString();
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

    /**
     * @return the source
     */
    public XrefBean getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(XrefBean source) {
        this.source = source;
    }

    /**
     * @return the target
     */
    public XrefBean getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(XrefBean target) {
        this.target = target;
    }
    
}
