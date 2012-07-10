/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.bean;

import org.bridgedb.statistics.MappingSetStatistics;
import org.bridgedb.url.URLMapping;

/**
 *
 * @author Christian
 */
public class MappingSetStatisticsBeanFactory {
    
    public static MappingSetStatistics asMappingSetStatistics(MappingSetStatisticsBean bean){
        return new MappingSetStatistics (bean.numberOfMappings, bean.numberOfMappingSets, bean.numberOfSourceDataSources, 
                bean.numberOfPredicates, bean.numberOfTargetDataSources);      
    }

    public static MappingSetStatisticsBean asBean(MappingSetStatistics stats){
        return new MappingSetStatisticsBean (stats.getNumberOfMappings(), stats.getNumberOfMappingSets(), 
                stats.getNumberOfSourceDataSources(), stats.getNumberOfPredicates(), stats.getNumberOfTargetDataSources());      
    }
}
