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
    private final DataSetInfo source;
    private final String predicate;
    private final DataSetInfo target;
    private final String justification;
    private final int symmetric;
    private Set<DataSetInfo> viaDataSets;
    private Set<Integer> chainIds;
    private Integer numberOfLinks;

    public MappingSetInfo(int id, DataSetInfo source, String predicate, DataSetInfo target, String justification,
            int symmetric, Set<DataSetInfo> viaDataSets,  Set<Integer> chainIds, Integer numberOfLinks){
        intId = id;
        stringId = null;
        this.predicate = predicate;
        this.source = source;
        this.target = target;
        this.justification = justification;
        this.symmetric = symmetric;
        setViaDataSets(viaDataSets);
        setChainIds(chainIds);
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

    public void combineIds(MappingSetInfo other){
        stringId = this.getStringId() + "," + other.getStringId();
    }
    
    /**
     * @return the predicate
     */
    public String getPredicate() {
        return predicate;
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
    
    @Override
    public String toString(){
        return this.getStringId()  
                + "\n\tsource:" + this.getSource()
                + "\n\tpredicate:" + this.predicate 
                + "\n\ttarget: " + this.getTarget() 
                + "\n\tsymetric: " + this.symmetric
                + "\n\tviaDataSets: " + this.getViaDataSets()
                + "\n\tchainIds: " + this.chainIds
                + "\n\tnumberOfLinks: " + this.numberOfLinks + "\n";
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
        if (chainIds != null){
            this.chainIds = chainIds;
        } else {
            chainIds = new HashSet<Integer>();
        }
    }

    /**
     * @return the symmetric
     */
    public int getSymmetric() {
        return symmetric;
    }

    /**
     * @return the source
     */
    public DataSetInfo getSource() {
        return source;
    }

    /**
     * @return the target
     */
    public DataSetInfo getTarget() {
        return target;
    }

    /**
     * @return the viaDataSets
     */
    public Set<DataSetInfo> getViaDataSets() {
        return viaDataSets;
    }

    /**
     * @param viaDataSets the viaDataSets to set
     */
    public void setViaDataSets(Set<DataSetInfo> viaDataSets) {
        if (viaDataSets != null){
            this.viaDataSets = viaDataSets;
        } else {
            this.viaDataSets = new HashSet<DataSetInfo>();
        }
    }

    public boolean isTransitive() {
        return (viaDataSets != null && !viaDataSets.isEmpty());
    }

  }
