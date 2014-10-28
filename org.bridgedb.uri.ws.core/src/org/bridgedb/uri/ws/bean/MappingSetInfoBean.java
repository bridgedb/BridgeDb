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

import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.statistics.MappingSetInfo;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="MappingSetInfo")
public class MappingSetInfoBean {
    private Integer id = null;
    private DataSetInfoBean source;
    private String predicate;
    private DataSetInfoBean target;
    private String justification;
    private Integer symmetric;
    private Integer numberOfLinks;
    private String mappingSource;    
    private Integer numberOfSources;
    private Integer numberOfTargets;

    /**
     * WS Constructor
     */
    public MappingSetInfoBean(){
    }
    

    public MappingSetInfoBean (MappingSetInfo info) {
        id = info.getIntId();
        source = DataSetInfoBean.asBean(info.getSource());
        predicate = info.getPredicate();
        target = DataSetInfoBean.asBean(info.getTarget());
        justification = info.getJustification();
        mappingSource = info.getMappingSource();
        symmetric = info.getSymmetric();
        numberOfLinks = info.getNumberOfLinks();
        numberOfSources= info.getNumberOfSources();
        numberOfTargets = info.getNumberOfTargets();
    }

    public MappingSetInfo asMappingSetInfo(){
       return new MappingSetInfo(getId(), 
               DataSetInfoBean.asDataSetInfo(getSource()), 
               getPredicate(), 
               DataSetInfoBean.asDataSetInfo(getTarget()), 
               getJustification(), 
               getMappingSource(),
               getSymmetric(), 
               getNumberOfLinks(),
               getNumberOfSources(),
               getNumberOfTargets());
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
     * @return the numberOfLinks
     */
    public Integer getNumberOfLinks() {
        return numberOfLinks;
    }

    /**
     * @param numberOfLinks the numberOfLinks to set
     */
    public void setNumberOfLinks(Integer numberOfLinks) {
        this.numberOfLinks = numberOfLinks;
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
     * @return the symmetric
     */
    public Integer getSymmetric() {
        return symmetric;
    }

    /**
     * @param symmetric the symmetric to set
     */
    public void setSymmetric(Integer symmetric) {
        this.symmetric = symmetric;
    }

    /**
     * @return the source
     */
    public DataSetInfoBean getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(DataSetInfoBean source) {
        this.source = source;
    }

    /**
     * @return the target
     */
    public DataSetInfoBean getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(DataSetInfoBean target) {
        this.target = target;
    }

    /**
     * @return the numberOfSources
     */
    public Integer getNumberOfSources() {
        return numberOfSources;
    }

    /**
     * @param numberOfSources the numberOfSources to set
     */
    public void setNumberOfSources(Integer numberOfSources) {
        this.numberOfSources = numberOfSources;
    }

    /**
     * @return the numberOfTargets
     */
    public Integer getNumberOfTargets() {
        return numberOfTargets;
    }

    /**
     * @param numberOfTargets the numberOfTargets to set
     */
    public void setNumberOfTargets(Integer numberOfTargets) {
        this.numberOfTargets = numberOfTargets;
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
    
    public boolean isEmpty() {
        return id == null;
    }

}
