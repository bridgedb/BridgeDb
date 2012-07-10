/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.statistics;

/**
 *
 * @author Christian
 */
public class MappingSetStatistics {
    private final int numberOfMappings;
    private final int numberOfMappingSets;
    private final int numberOfSourceDataSources;
    private final int numberOfPredicates;
    private final int numberOfTargetDataSources;
    
    public MappingSetStatistics(int numberOfMappings, int numberOfMappingSets, 
            int numberOfSourceDataSources, int numberOfPredicates, int numberOfTargetDataSources){
        this.numberOfMappings = numberOfMappings;
        this.numberOfMappingSets = numberOfMappingSets;
        this.numberOfSourceDataSources = numberOfSourceDataSources;
        this.numberOfPredicates = numberOfPredicates;
        this.numberOfTargetDataSources = numberOfTargetDataSources;
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
}
