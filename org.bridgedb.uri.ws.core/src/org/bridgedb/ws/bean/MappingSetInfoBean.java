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
    private String sourceSysCode;
    private String predicate;
    private String targetSysCode;
    private String justification;
    private Integer symmetric;
    private Integer numberOfLinks;
    private Set<String> viaSystemCode;
    private Set<Integer> chainId;

    /**
     * WS Constructor
     */
    public MappingSetInfoBean(){
    }
    
    public static MappingSetInfo asMappingSetInfo(MappingSetInfoBean bean){
       return new MappingSetInfo(bean.getId(), bean.getSourceSysCode(), bean.getPredicate(), bean.getTargetSysCode(), 
            bean.getJustification(), bean.getSymmetric(), bean.getViaSystemCode(), bean.getChainId(), bean.getNumberOfLinks());
    }

    public static MappingSetInfoBean asBean(MappingSetInfo info) {
        MappingSetInfoBean bean = new MappingSetInfoBean();
        bean.id = info.getIntId();
        bean.sourceSysCode = info.getSourceSysCode();
        bean.predicate = info.getPredicate();
        bean.targetSysCode = info.getTargetSysCode();
        bean.justification = info.getJustification();
        bean.symmetric = info.getSymmetric();
        bean.numberOfLinks = info.getNumberOfLinks();
        bean.viaSystemCode = info.getViaSystemCode();
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
     * @return the sourceSysCode
     */
    public String getSourceSysCode() {
        return sourceSysCode;
    }

    /**
     * @param sourceSysCode the sourceSysCode to set
     */
    public void setSourceSysCode(String sourceSysCode) {
        this.sourceSysCode = sourceSysCode;
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
     * @return the targetSysCode
     */
    public String getTargetSysCode() {
        return targetSysCode;
    }

    /**
     * @param targetSysCode the targetSysCode to set
     */
    public void setTargetSysCode(String targetSysCode) {
        this.targetSysCode = targetSysCode;
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
     * @return the isTransitive
     */
    public boolean isIsTransitive() {
        return !viaSystemCode.isEmpty();
    }

    /**
     * @return the viaSystemCode
     */
    public Set<String> getViaSystemCode() {
        return viaSystemCode;
    }

    /**
     * @param viaSystemCode the viaSystemCode to set
     */
    public void setViaSystemCode(Set<String> viaSystemCode) {
        this.viaSystemCode = viaSystemCode;
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
    
}
