/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.provenance.url;

import org.bridgedb.DataSource;

/**
 *
 * @author Christian
 */
public class DataSourceStatistics {
    public DataSource dataSource;
    public int numberOfVersions;
    public int numberOfSourceProvenances;
    public int numberOfTargetProvenances;
    public int numberOfSourceMappings;
    public int numberOfTargetMappings;
    
    public DataSourceStatistics (DataSource dataSource, int numberOfSourceProvenances, 
            int numberOfTargetProvenances, int numberOfSourceMappings, int numberOfTargetMappings){
        this.dataSource = dataSource;
        this.numberOfVersions = 1; //TODO add versions
        this.numberOfSourceProvenances = numberOfSourceProvenances;
        this.numberOfTargetProvenances = numberOfTargetProvenances;
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
     * @return the numberOfSourceProvenances
     */
    public int getNumberOfSourceProvenances() {
        return numberOfSourceProvenances;
    }

    /**
     * @return the numberOfTargetProvenances
     */
    public int getNumberOfTargetProvenances() {
        return numberOfTargetProvenances;
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
