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

import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;
import org.bridgedb.Xref;

@XmlRootElement(name="Xrefs")
public class XrefsBean {

    static final Logger logger = Logger.getLogger(XrefsBean.class);
    
    private Set<XrefBean> Xref;
    
    public XrefsBean(){
        Xref = new HashSet<XrefBean>();
    }

    public XrefsBean(Set<Xref> xrefs){
        Xref = new HashSet<XrefBean>();
        for (Xref xref:xrefs){
            logger.info(xref);
            if (xref != null){
                Xref.add(XrefBean.asBean(xref));
            }
        }
    }

    public Set<Xref> asXrefs() {
        HashSet<Xref> results = new HashSet<Xref>();
        for (XrefBean bean:getXref()){
            results.add(bean.asXref());
        }
        return results;
    }

    /**
     * @return the Xref
     */
    public Set<XrefBean> getXref() {
        return Xref;
    }

    /**
     * @param Xref the Xref to set
     */
    public void setXref(Set<XrefBean> Xref) {
        this.Xref = Xref;
    }

    public boolean isEmpty() {
        return Xref.isEmpty();
    }
        
}
