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

import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.Xref;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="XrefExist")
public class XrefExistsBean {
    
    Boolean exists;
    XrefBean Xref;
    
    public XrefExistsBean(){
    }
        
    public XrefExistsBean(Xref xref, boolean exists){
        this.exists = exists;
        Xref = new XrefBean(xref);
    }
    
    public XrefExistsBean(String id, String scrCode, boolean exists){
        this.exists = exists;
        Xref = new XrefBean(id, scrCode);
    }

   /**
     * @return the isSupported
     */
    public Boolean getExists() {
        return exists;
    }

    /**
     * @return the isSupported
     */
    public boolean exists() {
        return exists;
    }
    /**
     * @param isSupported the isSupported to set
     */
    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    /**
     * @return the Xref
     */
    public XrefBean getXref() {
        return Xref;
    }

    /**
     * @param Xref the Xref to set
     */
    public void setXref(XrefBean Xref) {
        this.Xref = Xref;
    }
    
  
}
