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
            boolean equals = linkSetInfo.getSourceURISpace().equals(collectedInfo.getSourceURISpace());
            if (equals) {
                equals = linkSetInfo.getTargetURISpace().equals(collectedInfo.getTargetURISpace());
            }
            if (equals){
                collectedInfo.multipleIds();
                collectedInfo.setNumberOfLinks(collectedInfo.getNumberOfLinks() + linkSetInfo.getNumberOfLinks());
                if (!linkSetInfo.isTransitive()){
                    collectedInfo.setTransitive(false);
                }
                return;
            }
        }
        collectedInfos.add(linkSetInfo);
    }
    
    public List<LinkSetInfo> getSummaryInfos(){
        return collectedInfos;
    }

    @Override
    public int compare(LinkSetInfo o1, LinkSetInfo o2) {
        int test = o1.getSourceURISpace().compareTo(o2.getSourceURISpace());
        if (test != 0) return test;
        return o1.getTargetURISpace().compareTo(o2.getTargetURISpace());
    }
}
    
