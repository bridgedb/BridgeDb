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
package org.bridgedb.uri;

import java.util.Collection;
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
    private Set<String> sourceUri;
    private Set<String> targetUri;
    private Integer mappingSetId;
    private String predicate;
    
    /**
     * Default constructor for webService
     */
    public Mapping(){
        this.sourceUri = new HashSet<String>();
        this.targetUri = new HashSet<String>();
    }
    
    public Mapping (Integer id, String sourceId, String sourceSysCode, String predicate, 
            String targetId, String targetSysCode, Integer mappingSetId){
        this.id = id;
        this.sourceUri = new HashSet<String>();
        this.sourceId = sourceId;
        this.sourceSysCode = sourceSysCode;
        this.targetUri = new HashSet<String>();
        this.targetId = targetId;
        this.targetSysCode = targetSysCode;
        this.mappingSetId = mappingSetId;
        this.predicate = predicate;
    }

    /**
     * This is the constructor for a mapping to self.
     * 
     * @param id
     * @param sysCode 
     */
    public Mapping (String id, String sysCode){
        this.id = null;
        this.sourceUri = new HashSet<String>();
        this.sourceId = id;
        this.sourceSysCode = sysCode;
        this.targetUri = new HashSet<String>();
        this.targetId = id;
        this.targetSysCode = sysCode;
        this.mappingSetId = null;
        this.predicate = null;
    }

    private boolean mapToSelf(){
        if (sourceSysCode == null){
            if (targetSysCode != null){
                return false;
            }
        } else {
            if (!sourceSysCode.equals(targetSysCode)){
                return false;
            } 
        }
        if (sourceId == null){
            if (targetId != null){
                return false;
            }
        } else {
            if (!sourceId.equals(targetId)){
                return false;
            }
        }
        return true;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return the sourceUris
     */
    public Set<String> getSourceUri() {
        return sourceUri;
    }

    public void addSourceUri(String sourceUri){
        getSourceUri().add(sourceUri);
        if (mapToSelf()){
            getTargetUri().add(sourceUri);
        }
    }
    
    public void addSourceUris(Collection<String> sourceUris){
        getSourceUri().addAll(sourceUris);
        if (mapToSelf()){
            getTargetUri().addAll(sourceUris);
        }
    }
    
    /**
     * @return the target Uris
     */
    public Set<String> getTargetUri() {
        return targetUri;
    }

    public void addTargetUri(String targetUri){
        getTargetUri().add(targetUri);
    }

    public void addTargetUris(Collection<String> extraTargetUris){
        getTargetUri().addAll(extraTargetUris);
    }

    public String toString(){
        StringBuilder output = new StringBuilder("mapping ");
        output.append(this.getId());
        for (String sourceUri:getSourceUri()){
            output.append("\n\tSourceUri: ");
            output.append(sourceUri);
        }
        if (getSource() != null){
            output.append("\n\tSource: ");
            output.append(getSource());
        }
        output.append("\n\tPredicate(): ");
        output.append(getPredicate());
        for (String targetUri:getTargetUri()){
            output.append("\n\tTargetUri: ");
            output.append(targetUri);
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
            if (!otherMapping.sourceUri.equals(sourceUri)) return false;
            if (!otherMapping.targetUri.equals(targetUri)) return false;
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
     * @param sourceUris the sourceUris to set
     */
    public void setSourceUri(Set<String> sourceUris) {
        this.sourceUri = sourceUris;
    }

    /**
     * @param targetUris the targetUris to set
     */
    public void setTargetUri(Set<String> targetUris) {
        this.targetUri = targetUris;
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
