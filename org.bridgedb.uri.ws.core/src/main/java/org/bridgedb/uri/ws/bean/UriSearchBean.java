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

@XmlRootElement(name="UriSearch")
public class UriSearchBean {
    private List<String> Uri;

    private String term;

    public UriSearchBean(){
    }
    
    public UriSearchBean(String term, Set<String> Uris){
        if (Uris == null || Uris.isEmpty()){
            Uri = new ArrayList<String>();
        } else {
            Uri = new ArrayList(Uris);
        }
        this.term = term;
    }

    /**
     * @return the Uri
     */
    public List<String> getUri() {
        return Uri;
    }

    /**
     * @param Uri the Uri to set
     */
    public void setUri(List<String> Uri) {
        this.Uri = Uri;
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

    public Set<String> getUriSet() {
        if (Uri == null || Uri.isEmpty()){
            return new HashSet<String>();
        } else {
            return new HashSet(Uri);
        }
    }

    public boolean isEmpty() {
        return Uri.isEmpty();
    }
 
    
}
