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

import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.uri.Mapping;
import org.bridgedb.ws.bean.XrefBean;

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
@XmlRootElement(name="mapping")
public class MappingBean {
 
    private XrefBean source;
    private XrefBean target;
    
    // Singleton names look better in the xml Bean 
    private Set<String> sourceUri;
    private Set<String> targetUri;
    private Integer mappingSetId;
    private String predicate;
    
    /**
     * Default constructor for webService
     */
    public MappingBean(){
    }
    
    public static MappingBean asBean(Mapping mapping){
        MappingBean bean = new MappingBean();
        bean.setSourceUri(mapping.getSourceUri());
        bean.setSource(new XrefBean(mapping.getSource()));
        bean.setTargetUri(mapping.getTargetUri());
        bean.setTarget(new XrefBean(mapping.getTarget()));
        bean.setMappingSetId(mapping.getMappingSetId());
        bean.setPredicate(mapping.getPredicate());
        return bean;
    }

    public static Mapping asMapping (MappingBean bean){
        Mapping result = new Mapping (bean.getSource().asXref(), bean.getPredicate(),
                bean.getTarget().asXref(), bean.getMappingSetId());
        result.setSourceUri(bean.getSourceUri());
        result.setTargetUri(bean.getTargetUri());
        return result;
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

    /**
     * @return the sourceUri
     */
    public Set<String> getSourceUri() {
        return sourceUri;
    }

    /**
     * @param sourceUri the sourceUri to set
     */
    public void setSourceUri(Set<String> sourceUri) {
        this.sourceUri = sourceUri;
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


 
 }
