/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.bean;

import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.url.URLMapping;

/**
 *
 * @author Christian
 */
public class OverallStatisticsBeanFactory {
    
    public static OverallStatistics asOverallStatistics(OverallStatisticsBean bean){
        return new OverallStatistics (bean.numberOfMappings, bean.numberOfMappingSets, bean.numberOfSourceDataSources, 
                bean.numberOfPredicates, bean.numberOfTargetDataSources);      
    }

    public static OverallStatisticsBean asBean(OverallStatistics stats){
        return new OverallStatisticsBean (stats.getNumberOfMappings(), stats.getNumberOfMappingSets(), 
                stats.getNumberOfSourceDataSources(), stats.getNumberOfPredicates(), stats.getNumberOfTargetDataSources());      
    }
}
