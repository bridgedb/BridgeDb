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
package org.bridgedb.uri.api;

public class UriMapping {
 
    private final String sourceUri;
    private final String targetUri;
    
    /**
     * Default constructor for webService
     */
    public UriMapping(String sourceUri, String targetUri){
        this.sourceUri = sourceUri;
        this.targetUri = targetUri;
    }

    /**
     * @return the sourceUri
     */
    public String getSourceUri() {
        return sourceUri;
    }

    /**
     * @return the targetUri
     */
    public String getTargetUri() {
        return targetUri;
    }

    void append(StringBuilder sb) {
        sb.append("\n\t\tsourceUri: ");
        sb.append(sourceUri);
        sb.append("\n\t\ttargetUri: ");
        sb.append(targetUri);
    }

  
 }
