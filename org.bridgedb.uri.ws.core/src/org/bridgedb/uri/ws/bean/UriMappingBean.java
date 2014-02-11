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

import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.uri.api.UriMapping;

/**
 * Contains the information held for a particular mapping.
 * <p>
 * @See getMethods for what is returned.
 * <p>
 * A few things that are not returned and why included:
 * <ul>
 * <li>UriSpace: 
 * @author Christian
 */
@XmlRootElement(name="UriMapping")
public class UriMappingBean {
 
    private String sourceUri;
    private String targetUri;
    
    /**
     * Default constructor for webService
     */
    public UriMappingBean(){
    }
    
    public UriMappingBean(UriMapping mapping){
        setSourceUri(mapping.getSourceUri());
        setTargetUri(mapping.getTargetUri());
    }

    public final UriMapping asUriMapping (){
        return new UriMapping (getSourceUri(), getTargetUri());
    }

    /**
     * @return the sourceUri
     */
    public final String getSourceUri() {
        return sourceUri;
    }

    /**
     * @param sourceUri the sourceUri to set
     */
    public final void setSourceUri(String sourceUri) {
        this.sourceUri = sourceUri;
    }

    /**
     * @return the targetUri
     */
    public final String getTargetUri() {
        return targetUri;
    }

    /**
     * @param targetUri the targetUri to set
     */
    public final void setTargetUri(String targetUri) {
        this.targetUri = targetUri;
    }
    
 
 }
