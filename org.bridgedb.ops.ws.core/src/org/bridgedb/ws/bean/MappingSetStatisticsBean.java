package org.bridgedb.ws.bean;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="MappingSetStatistics")
public class MappingSetStatisticsBean {
    Integer numberOfMappings;
    Integer numberOfMappingSets;
    Integer numberOfSourceDataSources;
    Integer numberOfPredicates;
    Integer numberOfTargetDataSources;
    
    public MappingSetStatisticsBean(Integer numberOfMappings, Integer numberOfMappingSets, 
            Integer numberOfSourceDataSources, Integer numberOfPredicates, Integer numberOfTargetDataSources){
        this.numberOfMappings = numberOfMappings;
        this.numberOfMappingSets = numberOfMappingSets;
        this.numberOfSourceDataSources = numberOfSourceDataSources;
        this.numberOfPredicates = numberOfPredicates;
        this.numberOfTargetDataSources = numberOfTargetDataSources;
    }

    //Webservice constructor
    public MappingSetStatisticsBean(){
    }
    
    public String toString(){
           return  "MappingSetStatistics: \n\tnumberOfMappings: " + numberOfMappings  
                   + " \n\tnumberOfMappingSets: " + numberOfMappingSets
                   + "\n\tnumberOfSourceDataSources: " + numberOfSourceDataSources
                   + "\n\tnumberOfPredicates: " + numberOfPredicates
                   + "\n\tnumberOfTargetDataSources: " + numberOfTargetDataSources;
    }

    /**
     * @return the numberOfMappings
     */
    public Integer getNumberOfMappings() {
        return numberOfMappings;
    }

    /**
     * @param numberOfMappings the numberOfMappings to set
     */
    public void setNumberOfMappings(Integer numberOfMappings) {
        this.numberOfMappings = numberOfMappings;
    }

    /**
     * @return the numberOfMappingSets
     */
    public Integer getNumberOfMappingSets() {
        return numberOfMappingSets;
    }

    /**
     * @param numberOfMappingSets the numberOfMappingSets to set
     */
    public void setNumberOfMappingSets(Integer numberOfMappingSets) {
        this.numberOfMappingSets = numberOfMappingSets;
    }

    /**
     * @return the numberOfSourceDataSources
     */
    public Integer getNumberOfSourceDataSources() {
        return numberOfSourceDataSources;
    }

    /**
     * @param numberOfSourceDataSources the numberOfSourceDataSources to set
     */
    public void setNumberOfSourceDataSources(Integer numberOfSourceDataSources) {
        this.numberOfSourceDataSources = numberOfSourceDataSources;
    }

    /**
     * @return the numberOfPredicates
     */
    public Integer getNumberOfPredicates() {
        return numberOfPredicates;
    }

    /**
     * @param numberOfPredicates the numberOfPredicates to set
     */
    public void setNumberOfPredicates(Integer numberOfPredicates) {
        this.numberOfPredicates = numberOfPredicates;
    }

    /**
     * @return the numberOfTargetDataSources
     */
    public Integer getNumberOfTargetDataSources() {
        return numberOfTargetDataSources;
    }

    /**
     * @param numberOfTargetDataSources the numberOfTargetDataSources to set
     */
    public void setNumberOfTargetDataSources(Integer numberOfTargetDataSources) {
        this.numberOfTargetDataSources = numberOfTargetDataSources;
    }

    
}
