// BridgeDb,
// An abstraction layer for identifer mapping services, both local and online.
// Copyright 2006-2009 BridgeDb developers
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

package org.bridgedb.webservice.biomart.util;

/**
 * Attribute, corresponding to attribute in BioMart.
 * @author gjj
 */
public class Attribute {
    private String name;
    private String displayName;

    /**
     *
     * @param name attribute name
     * @param displayName attribute display name
     */
    public Attribute(String name, String displayName) {
            this.name = name;
    this.displayName = displayName;
    }

    /**
     *
     * @return attribute name
     */
    public String getName() {
            return name;
    }

    /**
     * 
     * @return attribute display name
     */
    public String getDisplayName() {
        return displayName;
    }

}
