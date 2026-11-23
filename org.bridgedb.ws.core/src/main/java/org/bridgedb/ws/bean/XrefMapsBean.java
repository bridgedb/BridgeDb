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
package org.bridgedb.ws.bean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.Xref;

@XmlRootElement(name="XrefMappings")
public class XrefMapsBean {

    private Set<XrefMapBean> XrefMapping;
    
    public XrefMapsBean(){
        XrefMapping = new HashSet<XrefMapBean>();
    }

    public XrefMapsBean(Map<Xref, Set<Xref>>  mappings){
        this.XrefMapping = new HashSet<XrefMapBean>();
        for (Xref source:mappings.keySet()){
            for (Xref target:mappings.get(source)){
                this.XrefMapping.add(org.bridgedb.ws.bean.XrefMapBean.asBean(source, target));
            }
        }
    }
    
    public  Map<Xref, Set<Xref>> asMappings(){
        HashMap<Xref, Set<Xref>> results = new HashMap<Xref, Set<Xref>>();
        for (XrefMapBean bean:XrefMapping){
            Xref source = bean.getSource().asXref();
            Set<Xref>targets = results.get(source);
            if (targets == null){
                targets = new HashSet<Xref>();
            }
            Xref target = bean.getTarget().asXref();
            targets.add(target);
            results.put(source, targets);
        }
        return results;
   }
    
    public String toString(){
        StringBuffer buffer = new StringBuffer("XrefMapsBean: \n\t");
        for (XrefMapBean bean:XrefMapping){
            buffer.append(bean.toString());
            buffer.append("\n\t");
        }
        buffer.append("number of mappings: ");
        buffer.append(XrefMapping.size());
        return buffer.toString();
    }

    public Set<Xref> getTargetXrefs() {
        HashSet<Xref> results = new HashSet<Xref>();
        for (XrefMapBean bean:getXrefMapping()){
            results.add(bean.getTarget().asXref());
        }
        return results;
    }

    /**
     * @return the XrefMapping
     */
    public Set<XrefMapBean> getXrefMapping() {
        return XrefMapping;
    }

    /**
     * @param XrefMapping the XrefMapping to set
     */
    public void setXrefMapping(Set<XrefMapBean> XrefMapping) {
        this.XrefMapping = XrefMapping;
    }

    public boolean isEmpty() {
        return XrefMapping.isEmpty();
    }
}
