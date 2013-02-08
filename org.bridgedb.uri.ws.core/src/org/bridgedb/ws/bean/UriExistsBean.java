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

/**
 *
 * @author Christian
 */
@XmlRootElement(name="UriExist")
public class UriExistsBean {
    
    protected Boolean exists;
    protected  String Uri;
    
    public UriExistsBean(){
    }
    
    public UriExistsBean(String Uri, boolean exists){
        this.exists = exists;
        this.Uri = Uri;
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
     * @return the Uri
     */
    public String getUri() {
        return Uri;
    }

    /**
     * @param Uri the Uri to set
     */
    public void setUri(String Uri) {
        this.Uri = Uri;
    }
    
  
}
