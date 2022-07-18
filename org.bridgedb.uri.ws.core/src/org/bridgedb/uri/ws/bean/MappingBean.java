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
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.Xref;
import org.bridgedb.uri.api.Mapping;
import org.bridgedb.ws.bean.XrefBean;

/**
 * Contains the information held for a particular mapping.
 * <p>
 * See getMethods for what is returned.
 * <p>
 * @author Christian
 */
@XmlRootElement(name="mapping")
public class MappingBean {
 
    private XrefBean source;
    private XrefBean target;

    // Singleton names look better in the xml Bean 
    private Set<String> sourceUri;
    private Set<String> targetUri;

    private String justification;
    private String predicate;
    private String lens;

    private String mappingResource;
    private String mappingSource;
    private String mappingSetId;
    
    private List<MappingBean> via; 
        
    /**
     * Default constructor for webService
     */
    public MappingBean(){
        via = new ArrayList<MappingBean>();
    }
    
    public static MappingBean asBean(Mapping mapping){
        MappingBean bean = new MappingBean();
        bean.setSource(XrefBean.asBean(mapping.getSource()));
        bean.setTarget(XrefBean.asBean(mapping.getTarget()));
        
        //ystem.out.println ("to bean " + mapping.getTarget());
        //ystem.out.println ("in bean " + bean.getTarget());
        
        bean.setSourceUri(mapping.getSourceUri());
        bean.setTargetUri(mapping.getTargetUri());
        
        bean.setJustification(mapping.getJustification());
        bean.setPredicate(mapping.getPredicate());
        bean.setLens(mapping.getLens());
 
        bean.setMappingResource(mapping.getMappingResource());
        bean.setMappingSource(mapping.getMappingSource());
        bean.setMappingSetId(mapping.getMappingSetId());
        
        List<MappingBean> vias = new ArrayList<MappingBean>();
        for (Mapping via:mapping.getViaMappings()){
            vias.add(asBean(via));
        }
        bean.setVia(vias);

        return bean;
    }

    public static Mapping asMapping (MappingBean bean){
        Xref source = null;
        if (bean.getSource() != null){
            source = bean.getSource().asXref();
        }
        Xref target = null;
        if (bean.getTarget() != null){
            target = bean.getTarget().asXref();
        }
        //ystem.out.println("from bean " + target);

        List<Mapping> vias = new ArrayList<Mapping>();
        for (MappingBean via:bean.getVia()){
            vias.add(asMapping(via));
        }
        
        Mapping mapping = new Mapping(source, target, bean.sourceUri, bean.targetUri, bean.justification, bean.predicate, bean.lens, 
            bean.mappingResource, bean.mappingSource, bean.mappingSetId, vias); 
        //ystem.out.println("in mapping " + target);
        return mapping;
        
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
     * @return the justification
     */
    public String getJustification() {
        return justification;
    }

    /**
     * @param justification the justification to set
     */
    public void setJustification(String justification) {
        this.justification = justification;
    }

    /**
     * @return the mappingSource
     */
    public String getMappingSource() {
        return mappingSource;
    }

    /**
     * @param mappingSource the mappingSource to set
     */
    public void setMappingSource(String mappingSource) {
        this.mappingSource = mappingSource;
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

    /**
     * @return the mappingSetId
     */
    public String getMappingSetId() {
        return mappingSetId;
    }

    /**
     * @param mappingSetId the mappingSetId to set
     */
    public void setMappingSetId(String mappingSetId) {
        this.mappingSetId = mappingSetId;
    }

    /**
     * @return the via
     */
    public List<MappingBean> getVia() {
        return via;
    }

    /**
     * @param via the via to set
     */
    public void setVia(List<MappingBean> via) {
        this.via = via;
    }

 }
