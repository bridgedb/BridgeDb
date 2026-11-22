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
package org.bridgedb.uri.ws.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.statistics.MappingSetInfo;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="MappingSetInfos")
public class MappingSetInfosBean {

    private Set<MappingSetInfoBean> MappingSetInfo;
    
    /**
     * WS Constructor
     */
    public MappingSetInfosBean(){
        MappingSetInfo = new HashSet<MappingSetInfoBean>();
    }

    /**
     * @return the MappingSetInfo
     */
    public Set<MappingSetInfoBean> getMappingSetInfo() {
        return MappingSetInfo;
    }

    /**
     * @param MappingSetInfo the MappingSetInfo to set
     */
    public void setMappingSetInfo(Set<MappingSetInfoBean> MappingSetInfo) {
        this.MappingSetInfo = MappingSetInfo;
    }

    public List<MappingSetInfo> getMappingSetInfos() {
        ArrayList<MappingSetInfo> results = new ArrayList<MappingSetInfo>();
        for (MappingSetInfoBean bean:MappingSetInfo){
            results.add(bean.asMappingSetInfo());
        }
        return results;
    }

    public void addMappingSetInfo(MappingSetInfo info) {
        MappingSetInfo.add(new MappingSetInfoBean(info));
    }

    public boolean isEmpty() {
        return MappingSetInfo.isEmpty();
    }
    

}
