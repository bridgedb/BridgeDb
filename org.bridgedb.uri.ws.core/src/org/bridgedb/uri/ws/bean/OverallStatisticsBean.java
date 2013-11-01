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
package org.bridgedb.uri.ws.bean;

import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.statistics.OverallStatistics;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="OverallStatistics")
public class OverallStatisticsBean {
    Integer numberOfMappings;
    Integer numberOfMappingSets;
    Integer numberOfSourceDataSources;
    Integer numberOfPredicates;
    Integer numberOfTargetDataSources;
    Integer numberOfProfiles;
    
    //Webservice constructor
    public OverallStatisticsBean(){
    }
    
    public static OverallStatistics asOverallStatistics(OverallStatisticsBean bean){
        return new OverallStatistics (bean.numberOfMappings, bean.numberOfMappingSets, bean.numberOfSourceDataSources, 
                bean.numberOfPredicates, bean.numberOfTargetDataSources, bean.numberOfProfiles);      
    }

    public static OverallStatisticsBean asBean(OverallStatistics stats){
        OverallStatisticsBean bean = new OverallStatisticsBean();
        bean.numberOfMappings = stats.getNumberOfMappings();
        bean.numberOfMappingSets = stats.getNumberOfMappingSets();
        bean.numberOfSourceDataSources = stats.getNumberOfSourceDataSources();
        bean.numberOfPredicates = stats.getNumberOfPredicates();
        bean.numberOfTargetDataSources = stats.getNumberOfTargetDataSources();
        bean.numberOfProfiles = stats.getNumberOfLenses();      
        return bean;
    }
    
    public String toString(){
           return  "OverallStatistics: \n\tnumberOfMappings: " + numberOfMappings  
                   + " \n\tnumberOfMappingSets: " + numberOfMappingSets
                   + "\n\tnumberOfSourceDataSources: " + numberOfSourceDataSources
                   + "\n\tnumberOfPredicates: " + numberOfPredicates
                   + "\n\tnumberOfTargetDataSources: " + numberOfTargetDataSources
                   + "\n\tnumberOfProfiles: " + numberOfProfiles;
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

	public Integer getNumberOfProfiles() {
		return numberOfProfiles;
	}

	public void setNumberOfProfiles(Integer numberOfProfiles) {
		this.numberOfProfiles = numberOfProfiles;
	}

    
}
