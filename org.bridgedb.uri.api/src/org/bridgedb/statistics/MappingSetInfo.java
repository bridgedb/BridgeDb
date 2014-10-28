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
    private final String mappingSource;
    private final int symmetric;
    private Integer numberOfLinks;
    private final Integer numberOfSources;
    private final Integer numberOfTargets;

    public MappingSetInfo(int id, DataSetInfo source, String predicate, DataSetInfo target, String justification,
            String mappingSource, int symmetric, Integer numberOfLinks, Integer numberOfSources, 
            Integer numberOfTargets){
        intId = id;
        stringId = null;
        this.predicate = predicate;
        this.source = source;
        this.target = target;
        this.justification = justification;
        this.mappingSource = mappingSource;
        this.symmetric = symmetric;
        this.numberOfLinks = numberOfLinks;
        this.numberOfSources = numberOfSources;
        this.numberOfTargets = numberOfTargets;
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
     * @return the local name of the predicate
     */
    public String predicateLocalName() {
        return localName(predicate);
    }

    /**
     * @return true if this is the symmetric. Will return false if this was the original linkset
     */
    public boolean isSymmetric() {
        return getSymmetric() > 0;
    }

    /**
     * @return true is this has a symmetric even if this was the original
     */
    public boolean hasOrIsSymmetric() {
        return getSymmetric() != 0;
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
     * @return the local name of the predicate
     */
    public String justificationLocalName() {
        return localName(justification);
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

    public boolean isTransitive() {
        return false;
    }

    private String localName(String uri) {
        if (uri.contains("#")){
            return uri.substring(uri.indexOf("#")+1);
        }
        if (uri.contains("/")){
            return uri.substring(uri.lastIndexOf("/")+1);
        }
        return uri.substring(uri.lastIndexOf(".")+1);
    }

    /**
     * @return the mappingUri
     */
    public String getMappingSource() {
        return mappingSource;
    }

    /**
     * @return the mappingUri
     */
    public String sourceLocalName() {
        return localName(mappingSource);
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

}
