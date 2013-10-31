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
    private final String mappingResource;
    private final String mappingSource;
    private final int symmetric;
    private Set<DataSetInfo> viaDataSets;
    private Set<Integer> chainIds;
    private Integer numberOfLinks;
    private final Integer numberOfSources;
    private final Integer numberOfTargets;
    private final Integer frequencyMedium;
    private final Integer frequency75;
    private final Integer frequency90;
    private final Integer frequency99;
    private final Integer frequencyMax;

    public MappingSetInfo(int id, DataSetInfo source, String predicate, DataSetInfo target, String justification,
            String mappingResource, String mappingSource, int symmetric, Set<DataSetInfo> viaDataSets,  Set<Integer> chainIds, 
            Integer numberOfLinks, Integer numberOfSources, Integer numberOfTargets, Integer frequencyMedium,
            Integer frequency75, Integer frequency90, Integer frequency99, Integer frequencyMax){
        intId = id;
        stringId = null;
        this.predicate = predicate;
        this.source = source;
        this.target = target;
        this.justification = justification;
        this.mappingResource = mappingResource;
        this.mappingSource = mappingSource;
        this.symmetric = symmetric;
        setViaDataSets(viaDataSets);
        setChainIds(chainIds);
        this.numberOfLinks = numberOfLinks;
        this.numberOfSources = numberOfSources;
        this.numberOfTargets = numberOfTargets;
        this.frequencyMedium = frequencyMedium;
        this.frequency75 = frequency75;
        this.frequency90 = frequency90;
        this.frequency99 = frequency99;
        this.frequencyMax = frequencyMax;
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
                + "\n\tnumberOfLinks: " + this.numberOfLinks
                //+ "\n\tmappingName: " + this.mappingName 
                //+ "\n\tmappingUri: " + this.mappingUri           
                + "\n";
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
    public final void setChainIds(Set<Integer> chainIds) {
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
    public final void setViaDataSets(Set<DataSetInfo> viaDataSets) {
        if (viaDataSets != null){
            this.viaDataSets = viaDataSets;
        } else {
            this.viaDataSets = new HashSet<DataSetInfo>();
        }
    }

    public boolean isTransitive() {
        return (viaDataSets != null && !viaDataSets.isEmpty());
    }

    /**
     * @return the mappingSource
     */
    public String getMappingResource() {
        return mappingResource;
    }

    /**
     * @return the mappingUri
     */
    public String getMappingSource() {
        return mappingSource;
    }

    /**
     * @return the numberOfSources
     */
    public Integer getNumberOfSources() {
        return numberOfSources;
    }

    /**
     * @return the numberOfTargets
     */
    public Integer getNumberOfTargets() {
        return numberOfTargets;
    }

    /**
     * @return the frequencyMedium
     */
    public Integer getFrequencyMedium() {
        return frequencyMedium;
    }

    /**
     * @return the frequency75
     */
    public Integer getFrequency75() {
        return frequency75;
    }

    /**
     * @return the frequency90
     */
    public Integer getFrequency90() {
        return frequency90;
    }

    /**
     * @return the frequency99
     */
    public Integer getFrequency99() {
        return frequency99;
    }

    /**
     * @return the frequencyMax
     */
    public Integer getFrequencyMax() {
        return frequencyMax;
    }

  }
