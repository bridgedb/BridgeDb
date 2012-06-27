/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.statistics;

import org.bridgedb.DataSource;

/**
 *
 * @author Christian
 */
public class DataSourceStatistics {
    private DataSource dataSource;
    private int numberOfVersions;
    private int numberOfSourceLinkSets;
    private int numberOfTargetLinkSets;
    private int numberOfSourceMappings;
    private int numberOfTargetMappings;
    
    public DataSourceStatistics (DataSource dataSource, int numberOfSourceLinkSets, 
            int numberOfTargetLinkSets, int numberOfSourceMappings, int numberOfTargetMappings){
        this.dataSource = dataSource;
        this.numberOfVersions = 1; //TODO add versions
        this.numberOfSourceLinkSets = numberOfSourceLinkSets;
        this.numberOfTargetLinkSets = numberOfTargetLinkSets;
        this.numberOfSourceMappings = numberOfSourceMappings;
        this.numberOfTargetMappings = numberOfTargetMappings;
    }

    /**
     * @return the dataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * @return the numberOfVersions
     */
    public int getNumberOfVersions() {
        return numberOfVersions;
    }

    /**
     * @return the numberOfSourceLinkSets
     */
    public int getNumberOfSourceLinkSets() {
        return numberOfSourceLinkSets;
    }

    /**
     * @return the numberOfTargetLinkSets
     */
    public int getNumberOfTargetLinkSets() {
        return numberOfTargetLinkSets;
    }

    /**
     * @return the numberOfSourceMappings
     */
    public int getNumberOfSourceMappings() {
        return numberOfSourceMappings;
    }

    /**
     * @return the numberOfTargetMappings
     */
    public int getNumberOfTargetMappings() {
        return numberOfTargetMappings;
    }
}
