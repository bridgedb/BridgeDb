package org.bridgedb.ws.bean;

import org.bridgedb.ops.ProvenanceInfo;
import org.bridgedb.statistics.OverallStatistics;

/**
 *
 * @author Christian
 */
public class ProvenanceFactory {
    
    public static ProvenanceBean asBean(ProvenanceInfo provenanceInfo){
        ProvenanceBean bean = new ProvenanceBean();
        bean.setId(provenanceInfo.getId());
        bean.setSourceNameSpace(provenanceInfo.getSourceNameSpace());
        bean.setPredicate(provenanceInfo.getPredicate());
        bean.setTargetNameSpace(provenanceInfo.getTargetNameSpace());
        bean.setLinkCount(provenanceInfo.getNumberOfLinks());
        return bean;
    }
    
    public static ProvenanceInfo asProvenanceInfo(ProvenanceBean bean){
        return new ProvenanceInfo(bean.getId(), bean.getSourceNameSpace(), bean.getPredicate(), 
                bean.getTargetNameSpace(), bean.getLinkCount());
    }
}
