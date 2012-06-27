/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.bridgedb.ops.LinkSetInfo;

/**
 *
 * @author Christian
 */
public class SourceTargetCounter implements Comparator<LinkSetInfo>{
    
    private ArrayList<LinkSetInfo> collectedInfos;
    
    public SourceTargetCounter(Collection<LinkSetInfo> linkSetInfos){
        collectedInfos = new ArrayList<LinkSetInfo>();
        for (LinkSetInfo linkSetInfo:linkSetInfos){
            addin(linkSetInfo);
        }
        Collections.sort(collectedInfos, this);
    }

    private void addin(LinkSetInfo linkSetInfo) {
        for (LinkSetInfo collectedInfo:collectedInfos){
            boolean equals = linkSetInfo.getSourceNameSpace().equals(collectedInfo.getSourceNameSpace());
            if (equals) {
                equals = linkSetInfo.getTargetNameSpace().equals(collectedInfo.getTargetNameSpace());
            }
            if (equals){
                collectedInfo.setNumberOfLinks(collectedInfo.getNumberOfLinks() + linkSetInfo.getNumberOfLinks());
                return;
            }
        }
        LinkSetInfo summary = new LinkSetInfo("various", linkSetInfo.getSourceNameSpace(), "various", 
                linkSetInfo.getTargetNameSpace(), linkSetInfo.getNumberOfLinks());
        collectedInfos.add(summary);
    }
    
    public List<LinkSetInfo> getSummaryInfos(){
        return collectedInfos;
    }

    @Override
    public int compare(LinkSetInfo o1, LinkSetInfo o2) {
        int test = o1.getSourceNameSpace().compareTo(o2.getSourceNameSpace());
        if (test != 0) return test;
        return o1.getTargetNameSpace().compareTo(o2.getTargetNameSpace());
    }
}
    
