/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.bean;

import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.statistics.DataSourceStatistics;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="DataSourceStatistics")
public class DataSourceStatisticsBean {
    private DataSourceBean dataSource;
    private Integer numberOfVersions;
    private Integer numberOfSourceProvenances;
    private Integer numberOfTargetProvenances;
    private Integer numberOfSourceMappings;
    private Integer numberOfTargetMappings;

    public DataSourceStatisticsBean(){  
    }

    public DataSourceStatisticsBean(DataSourceStatistics statistics){  
        this.dataSource = DataSourceBeanFactory.asBean(statistics.getDataSource());
        this.numberOfSourceMappings = statistics.getNumberOfSourceMappings();
        this.numberOfTargetMappings = statistics.getNumberOfTargetMappings();
        this.numberOfSourceProvenances = statistics.getNumberOfSourceProvenances();
        this.numberOfTargetProvenances = statistics.getNumberOfTargetProvenances();
    }

    public DataSourceStatistics asDataSourceStatistics() {
        return new DataSourceStatistics(DataSourceBeanFactory.asDataSource(dataSource), 
                numberOfSourceProvenances, numberOfTargetProvenances, 
                numberOfSourceMappings, numberOfTargetMappings);   
    }

    /**
     * @return the dataSource
     */
    public DataSourceBean getDataSource() {
        return dataSource;
    }

    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSource(DataSourceBean dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return the numberOfVersions
     */
    public Integer getNumberOfVersions() {
        return numberOfVersions;
    }

    /**
     * @param numberOfVersions the numberOfVersions to set
     */
    public void setNumberOfVersions(Integer numberOfVersions) {
        this.numberOfVersions = numberOfVersions;
    }

    /**
     * @return the numberOfSourceProvenances
     */
    public Integer getNumberOfSourceProvenances() {
        return numberOfSourceProvenances;
    }

    /**
     * @param numberOfSourceProvenances the numberOfSourceProvenances to set
     */
    public void setNumberOfSourceProvenances(Integer numberOfSourceProvenances) {
        this.numberOfSourceProvenances = numberOfSourceProvenances;
    }

    /**
     * @return the numberOfTargetProvenances
     */
    public Integer getNumberOfTargetProvenances() {
        return numberOfTargetProvenances;
    }

    /**
     * @param numberOfTargetProvenances the numberOfTargetProvenances to set
     */
    public void setNumberOfTargetProvenances(Integer numberOfTargetProvenances) {
        this.numberOfTargetProvenances = numberOfTargetProvenances;
    }

    /**
     * @return the numberOfSourceMappings
     */
    public Integer getNumberOfSourceMappings() {
        return numberOfSourceMappings;
    }

    /**
     * @param numberOfSourceMappings the numberOfSourceMappings to set
     */
    public void setNumberOfSourceMappings(Integer numberOfSourceMappings) {
        this.numberOfSourceMappings = numberOfSourceMappings;
    }

    /**
     * @return the numberOfTargetMappings
     */
    public Integer getNumberOfTargetMappings() {
        return numberOfTargetMappings;
    }

    /**
     * @param numberOfTargetMappings the numberOfTargetMappings to set
     */
    public void setNumberOfTargetMappings(Integer numberOfTargetMappings) {
        this.numberOfTargetMappings = numberOfTargetMappings;
    }

 }
