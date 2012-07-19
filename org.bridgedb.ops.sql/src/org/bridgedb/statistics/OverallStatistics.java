/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.statistics;

/**
 *
 * @author Christian
 */
public class OverallStatistics {
    private int numberOfMappings;
    private int numberOfMappingSets;
    private int numberOfSourceDataSources;
    private int numberOfPredicates;
    private int numberOfTargetDataSources;
    
    public OverallStatistics(int numberOfMappings, int numberOfMappingSets, 
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
}
