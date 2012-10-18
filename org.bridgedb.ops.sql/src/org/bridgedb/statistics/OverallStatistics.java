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
 * Holder class for all the gloabl MetaData held by the SQL
 * @author Christian
 */
public class OverallStatistics {
    private int numberOfMappings;
    private int numberOfMappingSets;
    private int numberOfSourceDataSources;
    private int numberOfPredicates;
    private int numberOfTargetDataSources;
    private int numberOfProfiles;
    
    public OverallStatistics(int numberOfMappings, int numberOfMappingSets, 
            int numberOfSourceDataSources, int numberOfPredicates, 
            int numberOfTargetDataSources, int numberOfProfiles) {
        this.numberOfMappings = numberOfMappings;
        this.numberOfMappingSets = numberOfMappingSets;
        this.numberOfSourceDataSources = numberOfSourceDataSources;
        this.numberOfPredicates = numberOfPredicates;
        this.numberOfTargetDataSources = numberOfTargetDataSources;
        this.numberOfProfiles = numberOfProfiles;
    }

    /**
     * @return the numberOfMappings
     */
    public int getNumberOfMappings() {
        return numberOfMappings;
    }

    /**
     * @return the numberOfMappingSets
     */
    public int getNumberOfMappingSets() {
        return numberOfMappingSets;
    }

    /**
     * @return the numberOfSourceDataSources
     */
    public int getNumberOfSourceDataSources() {
        return numberOfSourceDataSources;
    }

    /**
     * @return the numberOfTargetDataSources
     */
    public int getNumberOfTargetDataSources() {
        return numberOfTargetDataSources;
    }

    /**
     * @return the numberOfPredicates
     */
    public int getNumberOfPredicates() {
        return numberOfPredicates;
    }

    /**
     * Returns the number of profiles that have been registered
     * @return the number of profiles
     */
    public int getNumberOfProfiles() {
    	return numberOfProfiles;
    }
    
    /**
     * @param numberOfMappings the numberOfMappings to set
     */
    public void setNumberOfMappings(int numberOfMappings) {
        this.numberOfMappings = numberOfMappings;
    }

    /**
     * @param numberOfMappingSets the numberOfMappingSets to set
     */
    public void setNumberOfMappingSets(int numberOfMappingSets) {
        this.numberOfMappingSets = numberOfMappingSets;
    }

    /**
     * @param numberOfSourceDataSources the numberOfSourceDataSources to set
     */
    public void setNumberOfSourceDataSources(int numberOfSourceDataSources) {
        this.numberOfSourceDataSources = numberOfSourceDataSources;
    }

    /**
     * @param numberOfPredicates the numberOfPredicates to set
     */
    public void setNumberOfPredicates(int numberOfPredicates) {
        this.numberOfPredicates = numberOfPredicates;
    }

    /**
     * @param numberOfTargetDataSources the numberOfTargetDataSources to set
     */
    public void setNumberOfTargetDataSources(int numberOfTargetDataSources) {
        this.numberOfTargetDataSources = numberOfTargetDataSources;
    }
    
    /**
     * @param numberOfProfiles the number of profiles to set
     */
    public void setNumberOfProfiles(int numberOfProfiles) {
    	this.numberOfProfiles = numberOfProfiles;
    }
    
}
