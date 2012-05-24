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
import org.bridgedb.ops.ProvenanceInfo;

/**
 *
 * @author Christian
 */
public class SourceTargetCounter implements Comparator<ProvenanceInfo>{
    
    private ArrayList<ProvenanceInfo> collectedInfos;
    
    public SourceTargetCounter(Collection<ProvenanceInfo> provenanceInfos){
        collectedInfos = new ArrayList<ProvenanceInfo>();
        for (ProvenanceInfo provenanceInfo:provenanceInfos){
            addin(provenanceInfo);
        }
        Collections.sort(collectedInfos, this);
    }

    private void addin(ProvenanceInfo provenanceInfo) {
        for (ProvenanceInfo collectedInfo:collectedInfos){
            boolean equals = provenanceInfo.getSourceNameSpace().equals(collectedInfo.getSourceNameSpace());
            if (equals) {
                equals = provenanceInfo.getTargetNameSpace().equals(collectedInfo.getTargetNameSpace());
            }
            if (equals){
                collectedInfo.setNumberOfLinks(collectedInfo.getNumberOfLinks() + provenanceInfo.getNumberOfLinks());
                return;
            }
        }
        ProvenanceInfo summary = new ProvenanceInfo("various", provenanceInfo.getSourceNameSpace(), "various", 
                provenanceInfo.getTargetNameSpace(), provenanceInfo.getNumberOfLinks());
        collectedInfos.add(summary);
    }
    
    public List<ProvenanceInfo> getSummaryInfos(){
        return collectedInfos;
    }

    @Override
    public int compare(ProvenanceInfo o1, ProvenanceInfo o2) {
        int test = o1.getSourceNameSpace().compareTo(o2.getSourceNameSpace());
        if (test != 0) return test;
        return o1.getTargetNameSpace().compareTo(o2.getTargetNameSpace());
    }
}
    
