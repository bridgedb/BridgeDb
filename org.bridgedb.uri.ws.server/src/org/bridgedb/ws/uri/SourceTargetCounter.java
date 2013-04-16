// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.ws.uri;

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
            boolean equals = mappingSetInfo.getSource().equals(collectedInfo.getSource());
            if (equals) {
                equals = mappingSetInfo.getTarget().equals(collectedInfo.getTarget());
            }
            if (equals){
                collectedInfo.combineIds(mappingSetInfo);
                collectedInfo.setNumberOfLinks(collectedInfo.getNumberOfLinks() + mappingSetInfo.getNumberOfLinks());
                if (mappingSetInfo.getViaDataSets() == null || mappingSetInfo.getViaDataSets().isEmpty()){
                    collectedInfo.setViaDataSets(null);
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
        int test = o1.getSource().compareTo(o2.getSource());
        if (test != 0) return test;
        return o1.getTarget().compareTo(o2.getTarget());
    }
}
    
