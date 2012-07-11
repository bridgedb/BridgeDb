/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.bridgedb.statistics.MappingSetInfo;

/**
 *
 * @author Christian
 */
public class SourceTargetCounter implements Comparator<MappingSetInfo>{
    
    private ArrayList<MappingSetInfo> collectedInfos;
    
    public SourceTargetCounter(Collection<MappingSetInfo> mappingSetInfos){
        collectedInfos = new ArrayList<MappingSetInfo>();
        for (MappingSetInfo linkSetInfo:mappingSetInfos){
            addin(linkSetInfo);
        }
        Collections.sort(collectedInfos, this);
    }

    private void addin(MappingSetInfo mappingSetInfo) {
        for (MappingSetInfo collectedInfo:collectedInfos){
            boolean equals = mappingSetInfo.getSourceSysCode().equals(collectedInfo.getSourceSysCode());
            if (equals) {
                equals = mappingSetInfo.getTargetSysCode().equals(collectedInfo.getTargetSysCode());
            }
            if (equals){
                collectedInfo.multipleIds();
                collectedInfo.setNumberOfLinks(collectedInfo.getNumberOfLinks() + mappingSetInfo.getNumberOfLinks());
                if (!mappingSetInfo.isTransitive()){
                    collectedInfo.setTransitive(false);
                }
                return;
            }
        }
        collectedInfos.add(mappingSetInfo);
    }
    
    public List<MappingSetInfo> getSummaryInfos(){
        return collectedInfos;
    }

    @Override
    public int compare(MappingSetInfo o1, MappingSetInfo o2) {
        int test = o1.getSourceSysCode().compareTo(o2.getSourceSysCode());
        if (test != 0) return test;
        return o1.getTargetSysCode().compareTo(o2.getTargetSysCode());
    }
}
    
