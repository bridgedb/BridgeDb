package org.bridgedb.ws.bean;

import org.bridgedb.ops.LinkSetInfo;
import org.bridgedb.statistics.OverallStatistics;

/**
 *
 * @author Christian
 */
public class LinkSetFactory {
    
    public static LinkSetBean asBean(LinkSetInfo linkSetInfo){
        LinkSetBean bean = new LinkSetBean();
        bean.setId(linkSetInfo.getId());
        bean.setSourceNameSpace(linkSetInfo.getSourceNameSpace());
        bean.setPredicate(linkSetInfo.getPredicate());
        bean.setTargetNameSpace(linkSetInfo.getTargetNameSpace());
        bean.setLinkCount(linkSetInfo.getNumberOfLinks());
        return bean;
    }
    
    public static LinkSetInfo asLinkSetInfo(LinkSetBean bean){
        return new LinkSetInfo(bean.getId(), bean.getSourceNameSpace(), bean.getPredicate(), 
                bean.getTargetNameSpace(), bean.getLinkCount());
    }
}
