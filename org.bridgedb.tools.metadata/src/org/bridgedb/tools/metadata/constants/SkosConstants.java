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
package org.bridgedb.tools.metadata.constants;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 */
public class SkosConstants {

    private static final String skosns = "http://www.w3.org/2004/02/skos/core#";
    
    public static final URI BROAD_MATCH = new URIImpl(skosns + "broadMatch");
    public static final URI CLOSE_MATCH = new URIImpl(skosns + "closeMatch");
    public static final URI EXACT_MATCH = new URIImpl(skosns + "exactMatch");
    public static final URI MAPPING_RELATION = new URIImpl(skosns + "mappingRelation");
    public static final URI NARROW_MATCH = new URIImpl(skosns + "narrowMatch");
    public static final URI RELATED_MATCH = new URIImpl(skosns + "relatedMatch");

}
