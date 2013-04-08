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
package org.bridgedb.statistics;

import java.util.HashSet;
import java.util.Set;

/**
 * Holder class for the main Meta Data of MappingSet.
 *
 * Does not include everything in the void header but only what is captured in the SQL.
 * @author Christian
 */
public class MappingSetInfo {
    private String stringId;
    private final int intId;
    private final String sourceSysCode;
    private final String predicate;
    private final String targetSysCode;
    private final String justification;
    private final int symmetric;
    private Set<String> viaSystemCode;
    private Set<Integer> chainIds;
    private Integer numberOfLinks;

    public MappingSetInfo(int id, String sourceSysCode, String predicate, String targetSysCode, String justification,
            int symmetric, Set<String> viaSystemCodes,  Set<Integer> chainIds, Integer numberOfLinks){
        intId = id;
        stringId = null;
        this.predicate = predicate;
        this.sourceSysCode = sourceSysCode;
        this.targetSysCode = targetSysCode;
        this.justification = justification;
        this.symmetric = symmetric;
        setViaSystemCode(viaSystemCodes);
        this.chainIds = chainIds;
        this.numberOfLinks = numberOfLinks;
    }
    
    /**
     * @return the id
     */
    public String getStringId() {
        if (stringId == null){
            return "" + intId;
        }
        return stringId;
    }

    /**
     * @return the id
     */
    public int getIntId() {
        return intId;
    }

    public void multipleIds(){
        stringId = "various";
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
     * @return the symmetric
     */
    public boolean isSymmetric() {
        return getSymmetric() > 0;
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
        return this.getStringId() + "\n\tsourceSysCode:" + this.sourceSysCode + "\n\tpredicate:" + this.predicate 
                + "\n\ttargetSysCode: " + this.targetSysCode 
                + "\n\tsymetric: " + this.symmetric
                + "\n\tviaSystemCodes: " + this.viaSystemCode 
                + "\n\tchainIds: " + this.chainIds
                + "\n\tnumberOfLinks: " + this.numberOfLinks + "\n";
    }

    /**
     * @return the isTransitive
     */
    public boolean isTransitive() {
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
        if (viaSystemCode == null){
            this.viaSystemCode = new HashSet<String>();
        } else {
            this.viaSystemCode = viaSystemCode;
        }
    }

    /**
     * @return the justification
     */
    public String getJustification() {
        return justification;
    }

    /**
     * @return the chainIds
     */
    public Set<Integer> getChainIds() {
        return chainIds;
    }

    /**
     * @param chainIds the chainIds to set
     */
    public void setChainIds(Set<Integer> chainIds) {
        this.chainIds = chainIds;
    }

    /**
     * @return the symmetric
     */
    public int getSymmetric() {
        return symmetric;
    }


 }
