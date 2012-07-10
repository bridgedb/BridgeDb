/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.bean;

import org.bridgedb.statistics.MappingSetInfo;

/**
 *
 * @author Christian
 */
public class MappingSetInfoBeanFactory {
    
    public static MappingSetInfo asMappingSetInfo(MappingSetInfoBean bean){
        return new MappingSetInfo(bean.getId(), bean.getSourceSysCode(), bean.getPredicate(), bean.getTargetSysCode(), 
            bean.getNumberOfLinks(), bean.isIsTransitive());
    }

    public static MappingSetInfoBean asBean(MappingSetInfo info) {
        return new MappingSetInfoBean(info.getId(), info.getSourceSysCode(), info.getPredicate(), info.getTargetSysCode(), 
            info.getNumberOfLinks(), info.isTransitive());
    }
}
