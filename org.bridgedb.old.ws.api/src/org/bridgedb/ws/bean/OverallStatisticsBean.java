/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.bean;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="Statistics")
public class OverallStatisticsBean {
    private int numberOfMappings;
    private int numberOfLinkSets;
    private int numberOfSourceDataSources;
    private int numberOfPredicates;
    private int numberOfTargetDataSources;

    /**
     * @return the numberOfMappings
     */
    public int getNumberOfMappings() {
        return numberOfMappings;
    }

    /**
     * @param numberOfMappings the numberOfMappings to set
     */
    public void setNumberOfMappings(int numberOfMappings) {
        this.numberOfMappings = numberOfMappings;
    }

    /**
     * @return the numberOfLinkSets
     */
    public int getNumberOfLinkSets() {
        return numberOfLinkSets;
    }

    /**
     * @param numberOfLinkSets the numberOfLinkSets to set
     */
    public void setNumberOfLinkSets(int numberOfLinkSets) {
        this.numberOfLinkSets = numberOfLinkSets;
    }

    /**
     * @return the numberOfSourceDataSources
     */
    public int getNumberOfSourceDataSources() {
        return numberOfSourceDataSources;
    }

    /**
     * @param numberOfSourceDataSources the numberOfSourceDataSources to set
     */
    public void setNumberOfSourceDataSources(int numberOfSourceDataSources) {
        this.numberOfSourceDataSources = numberOfSourceDataSources;
    }

    /**
     * @return the numberOfTargetDataSources
     */
    public int getNumberOfTargetDataSources() {
        return numberOfTargetDataSources;
    }

    /**
     * @param numberOfTargetDataSources the numberOfTargetDataSources to set
     */
    public void setNumberOfTargetDataSources(int numberOfTargetDataSources) {
        this.numberOfTargetDataSources = numberOfTargetDataSources;
    }

    /**
     * @return the numberOfPredicates
     */
    public int getNumberOfPredicates() {
        return numberOfPredicates;
    }

    /**
     * @param numberOfPredicates the numberOfPredicates to set
     */
    public void setNumberOfPredicates(int numberOfPredicates) {
        this.numberOfPredicates = numberOfPredicates;
    }

}
