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

/**
 *
 * @author Christian
 */
@XmlRootElement(name="Properties")
public class PropertiesBean {

    private Set<PropertyBean> Property;
    
    public PropertiesBean(){
         Property = new HashSet<PropertyBean>();
    }

    /**
     * @return the Property
     */
    public Set<PropertyBean> getProperty() {
        return Property;
    }

    /**
     * @param Property the Property to set
     */
    public void setProperty(Set<PropertyBean> Property) {
        this.Property = Property;
    }

    public Set<String> getKeys() {
        HashSet<String> results = new HashSet<String>();
        for (PropertyBean bean:Property){
            results.add(bean.getKey());
        }
        return results;
    }

    public void addProperty(String key, String property) {
        PropertyBean bean = new PropertyBean(key, property);
        Property.add(bean);
    }

    public boolean isEmpty() {
        return Property.isEmpty();
    }

}
