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
package org.bridgedb.linkset.constants;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 */
public class PavConstants {
    
    private static final String pavTermns = "http://purl.org/pav/2.0/";
    
    public static final URI ACCESSED_BY = new URIImpl(pavTermns + "sourceAccessedBy");
    public static final URI ACCESSED_FROM = new URIImpl(pavTermns + "sourceAccessedAt");
    public static final URI ACCESSED_ON = new URIImpl(pavTermns + "sourceAccessedOn");
    public static final URI AUTHORED_BY = new URIImpl(pavTermns + "authoredBy");
    public static final URI AUTHORED_ON = new URIImpl(pavTermns + "authorOn");
    public static final URI CREATED_BY = new URIImpl(pavTermns + "createdBy");
    public static final URI CREATED_ON = new URIImpl(pavTermns + "created");
    public static final URI DERIVED_BY = new URIImpl(pavTermns + "derivedBy");
    public static final URI DERIVED_FROM = new URIImpl(pavTermns + "derivedFrom");
    public static final URI DERIVED_ON = new URIImpl(pavTermns + "derivedOn");
    public static final URI IMPORTED_BY = new URIImpl(pavTermns + "importedBy");
    public static final URI IMPORTED_FROM = new URIImpl(pavTermns + "importedFrom");
    public static final URI IMPORTED_ON = new URIImpl(pavTermns + "importedOn");
    public static final URI MODIFIED_ON = new URIImpl(pavTermns + "modified");
    public static final URI RETRIEVED_BY = new URIImpl(pavTermns + "retrievedBy");
    public static final URI RETRIEVED_ON = new URIImpl(pavTermns + "retrievedOn");
    public static final URI RETRIEVED_FROM = new URIImpl(pavTermns + "retrievedFrom");
    public static final URI VERSION = new URIImpl(pavTermns + "version");
    
    
}
