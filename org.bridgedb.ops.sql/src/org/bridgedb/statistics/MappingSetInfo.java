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
package org.bridgedb.statistics;

/**
 *
 * @author Christian
 */
public class MappingSetInfo {
    private String id;
    private final String sourceSysCode;
    private final String predicate;
    private final String targetSysCode;
    private Integer numberOfLinks;
    private boolean isTransitive;

    public MappingSetInfo(String id, String sourceSysCode, String predicate, String targetSysCode, 
            Integer numberOfLinks, boolean isTransitive){
        this.id = id;
        this.predicate = predicate;
        this.sourceSysCode = sourceSysCode;
        this.targetSysCode = targetSysCode;
        this.numberOfLinks = numberOfLinks;
        this.isTransitive = isTransitive;
    }
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    public void multipleIds(){
        id = "various";
    }
    
    /**
     * @return the predicate
     */
    public String getPredicate() {
        return predicate;
    }

    /**
     * @return the sourceURISpace
     */
    public String getSourceSysCode() {
        return sourceSysCode;
    }

    /**
     * @return the targetURISpace
     */
    public String getTargetSysCode() {
        return targetSysCode;
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
    
    public String toString(){
        return this.id + "\n\tsourceSysCode:" + this.sourceSysCode + "\n\tpredicate:" + this.predicate + 
                "\n\ttargetSysCode:" +this.targetSysCode + "\n\tnumberOfLinks:" + this.numberOfLinks + "\n";
    }

    /**
     * @return the isTransitive
     */
    public boolean isTransitive() {
        return isTransitive;
    }

    public void setTransitive(boolean newValue) {
        isTransitive = newValue;
    }

 }
