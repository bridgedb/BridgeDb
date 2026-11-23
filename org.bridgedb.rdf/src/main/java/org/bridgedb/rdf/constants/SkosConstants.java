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
package org.bridgedb.rdf.constants;

/**
 *
 */
public class SkosConstants {

    private static final String PREFIX = "http://www.w3.org/2004/02/skos/core#";
    
    public static final String BROAD_MATCH = PREFIX + "broadMatch";
    public static final String BROADER = PREFIX + "broader";
    public static final String CLOSE_MATCH = PREFIX + "closeMatch";
    public static final String EXACT_MATCH = PREFIX + "exactMatch";
    public static final String MAPPING_RELATION = PREFIX + "mappingRelation";
    public static final String NARROW_MATCH = PREFIX + "narrowMatch";
    public static final String NARROWER = PREFIX + "narrower";
    public static final String RELATED_MATCH = PREFIX + "relatedMatch";
    public static final String RELATED = PREFIX + "related"; //Ugly but used.

}
