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
package org.bridgedb.ws.bean;

import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.statistics.MappingSetInfo;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="MappingSetInfo")
public class MappingSetInfoBean {
    private Integer id;
    private DataSetInfoBean source;
    private String predicate;
    private DataSetInfoBean target;
    private String justification;
    private Integer symmetric;
    private Integer numberOfLinks;
    private Set<DataSetInfoBean> viaDataSets;
    private Set<Integer> chainId;

    /**
     * WS Constructor
     */
    public MappingSetInfoBean(){
    }
    
    public static MappingSetInfo asMappingSetInfo(MappingSetInfoBean bean){
       return new MappingSetInfo(bean.getId(), DataSetInfoBean.asDataSetInfo(bean.getSource()), bean.getPredicate(), 
               DataSetInfoBean.asDataSetInfo(bean.getTarget()), bean.getJustification(), bean.getSymmetric(), 
               DataSetInfoBean.asDataSetInfos(bean.getViaDataSets()), bean.getChainId(), bean.getNumberOfLinks());
    }

    public static MappingSetInfoBean asBean(MappingSetInfo info) {
        MappingSetInfoBean bean = new MappingSetInfoBean();
        bean.id = info.getIntId();
        bean.source = DataSetInfoBean.asBean(info.getSource());
        bean.predicate = info.getPredicate();
        bean.target = DataSetInfoBean.asBean(info.getTarget());
        bean.justification = info.getJustification();
        bean.symmetric = info.getSymmetric();
        bean.numberOfLinks = info.getNumberOfLinks();
        bean.viaDataSets = DataSetInfoBean.asBeans(info.getViaDataSets());
        bean.chainId = info.getChainIds();
        return bean;
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
     * @return the chainId
     */
    public Set<Integer> getChainId() {
        return chainId;
    }

    /**
     * @param chainId the chainId to set
     */
    public void setChainId(Set<Integer> chainId) {
        this.chainId = chainId;
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
     * @return the viaDataSets
     */
    public Set<DataSetInfoBean> getViaDataSets() {
        return viaDataSets;
    }

    /**
     * @param viaDataSets the viaDataSets to set
     */
    public void setViaDataSets(Set<DataSetInfoBean> viaDataSets) {
        this.viaDataSets = viaDataSets;
    }
    
}
