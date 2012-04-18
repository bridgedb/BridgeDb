package org.bridgedb.ws.bean;

import org.bridgedb.statistics.OverallStatistics;

/**
 *
 * @author Christian
 */
public class OverallStatisticsBeanFactory {
    
    public static OverallStatisticsBean asBean(OverallStatistics overallStatistics){
        OverallStatisticsBean bean = new OverallStatisticsBean();
        bean.setNumberOfMappings(overallStatistics.getNumberOfMappings());
        bean.setNumberOfProvenances(overallStatistics.getNumberOfProvenances());
        bean.setNumberOfSourceDataSources(overallStatistics.getNumberOfSourceDataSources());
        bean.setNumberOfTargetDataSources(overallStatistics.getNumberOfTargetDataSources());
        return bean;
    }
    
    public static OverallStatistics asOverallStatistics(OverallStatisticsBean bean){
        return new OverallStatistics(bean.getNumberOfMappings(), bean.getNumberOfProvenances(), 
                bean.getNumberOfSourceDataSources(), bean.getNumberOfPredicates(), bean.getNumberOfTargetDataSources());
    }
}
