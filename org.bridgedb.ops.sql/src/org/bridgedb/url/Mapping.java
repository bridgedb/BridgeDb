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
package org.bridgedb.url;

import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;

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
@XmlRootElement(name="Mapping")
public class Mapping {
 
    private Integer id;
    private String sourceId;
    private String sourceSysCode;
    private String targetId;
    private String targetSysCode;
    
    // Singleton names look better in the xml Bean 
    private Set<String> sourceURL;
    private Set<String> targetURL;
    private Integer mappingSetId;
    private String predicate;
    
    /**
     * Default constructor for webService
     */
    public Mapping(){
        this.sourceURL = new HashSet<String>();
        this.targetURL = new HashSet<String>();
    }
    
    public Mapping (Integer id, String sourceId, String sourceSysCode, String predicate, 
            String targetId, String targetSysCode, Integer mappingSetId){
        this.id = id;
        this.sourceURL = new HashSet<String>();
        this.sourceId = sourceId;
        this.sourceSysCode = sourceSysCode;
        this.targetURL = new HashSet<String>();
        this.targetId = targetId;
        this.targetSysCode = targetSysCode;
        this.mappingSetId = mappingSetId;
        this.predicate = predicate;
    }

    public Mapping (String id, String sysCode){
        this.id = null;
        this.sourceURL = new HashSet<String>();
        this.sourceId = id;
        this.sourceSysCode = sysCode;
        this.targetURL = new HashSet<String>();
        this.targetId = id;
        this.targetSysCode = sysCode;
        this.mappingSetId = null;
        this.predicate = null;
    }
    
    public Mapping (Integer id, String sourceURL, String predicate, String targetURL, Integer mappingSetId, boolean test){
        this.id = id;
        this.sourceURL = new HashSet<String>();
        this.sourceURL.add(sourceURL);
        this.sourceId = null;
        this.sourceSysCode = null;
        this.targetURL = new HashSet<String>();
        this.targetURL.add(targetURL);
        this.targetId = null;
        this.targetSysCode = null;
        this.mappingSetId = mappingSetId;
        this.predicate = predicate;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return the sourceURLs
     */
    public Set<String> getSourceURL() {
        return sourceURL;
    }

    public void addSourceURL(String sourceURL){
        getSourceURL().add(sourceURL);
    }
    
    /**
     * @return the target URLs
     */
    public Set<String> getTargetURL() {
        return targetURL;
    }

    public void addTargetURL(String targetURL){
        getTargetURL().add(targetURL);
    }

    public String toString(){
        StringBuilder output = new StringBuilder("mapping ");
        output.append(this.getId());
        for (String sourceURL:getSourceURL()){
            output.append("\n\tSourceURL: ");
            output.append(sourceURL);
        }
        if (getSource() != null){
            output.append("\n\tSource: ");
            output.append(getSource());
        }
        output.append("\n\tPredicate(): ");
        output.append(getPredicate());
        for (String targetURL:getTargetURL()){
            output.append("\n\tTargetURL: ");
            output.append(targetURL);
        }
        if (getTarget() != null){
            output.append("\n\tTarget: ");
            output.append(getTarget()); 
        }
        output.append("\n\tMappingSet(id): ");
        output.append(getMappingSetId());
        return output.toString();
    }
   
    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other instanceof Mapping){
            Mapping otherMapping = (Mapping)other;
            if (otherMapping.getId() != getId()) return false;
            if (!otherMapping.sourceURL.equals(sourceURL)) return false;
            if (!otherMapping.targetURL.equals(targetURL)) return false;
            if (!otherMapping.getMappingSetId().equals(getMappingSetId())) return false;
            //No need to check predicate as by defintion one id has one predicate
            return true;
         } else {
            return false;
        }
    }

    /**
     * @return the mappingSetId
     */
    public Integer getMappingSetId() {
        return mappingSetId;
    }

    /**
     * @return the predicate
     */
    public String getPredicate() {
        return predicate;
    }

    /**
     * @return the source
     */
    public Xref getSource() {
        DataSource ds = DataSource.getBySystemCode(getSourceSysCode());
        return new Xref(getSourceId(), ds);
    }

    /**
     * @return the target
     */
    public Xref getTarget() {
        DataSource ds = DataSource.getBySystemCode(getTargetSysCode());
        return new Xref(getTargetId(), ds);
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the sourceId
     */
    public String getSourceId() {
        return sourceId;
    }

    /**
     * @param sourceId the sourceId to set
     */
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * @return the targetId
     */
    public String getTargetId() {
        return targetId;
    }

    /**
     * @param targetId the targetId to set
     */
    public void setTargetId(String targetId) {
        this.targetId = targetId;
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
     * @param sourceURLs the sourceURLs to set
     */
    public void setSourceURL(Set<String> sourceURLs) {
        this.sourceURL = sourceURLs;
    }

    /**
     * @param targetURLs the targetURLs to set
     */
    public void setTargetURL(Set<String> targetURLs) {
        this.targetURL = targetURLs;
    }

    /**
     * @param mappingSetId the mappingSetId to set
     */
    public void setMappingSetId(Integer mappingSetId) {
        this.mappingSetId = mappingSetId;
    }

    /**
     * @param predicate the predicate to set
     */
    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

 }
