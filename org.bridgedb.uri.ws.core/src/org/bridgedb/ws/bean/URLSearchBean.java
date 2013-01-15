// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="URLSearch")
public class URLSearchBean {
    private List<String> URL;

    private String term;

    public URLSearchBean(){
    }
    
    public URLSearchBean(String term, Set<String> URLs){
        if (URLs == null || URLs.isEmpty()){
            URL = new ArrayList<String>();
        } else {
            URL = new ArrayList(URLs);
        }
        this.term = term;
    }

    /**
     * @return the URL
     */
    public List<String> getURL() {
        return URL;
    }

    /**
     * @param URL the URL to set
     */
    public void setURL(List<String> URL) {
        this.URL = URL;
    }

    /**
     * @return the term
     */
    public String getTerm() {
        return term;
    }

    /**
     * @param term the term to set
     */
    public void setTerm(String term) {
        this.term = term;
    }

    public Set<String> getURLSet() {
        if (URL == null || URL.isEmpty()){
            return new HashSet<String>();
        } else {
            return new HashSet(URL);
        }
    }
 
    
}
