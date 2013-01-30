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
package org.bridgedb.url;

import org.bridgedb.IDMapperException;

/**
 * Base class of all Test using URLs
 *
 * Adds a method for loading the test data.
 * @author Christian
 */
public abstract class URLListenerTest extends URLMapperTestBase{
        
    protected static URLListener listener;
    protected static final String TEST_PREDICATE = "http://www.w3.org/2004/02/skos/core#exactMatch";
    private static final boolean SYMETRIC = true;
    private static final boolean ORIGINAL = false;
    private static final boolean TRANSATIVE = true;
    protected static int mappingSet2_3;
    
    /**
     * Method for loading the Test data
     * Should eb called in a @beforeClass method after setting listener
     * 
     * @throws IDMapperException
     */
    public static void loadData() throws IDMapperException{
        listener.registerUriPattern(DataSource1, URISpace1 + "$id");
        listener.registerUriPattern(DataSource2, URISpace2 + "$id");
        listener.registerUriPattern(DataSource2, URISpace2a + "$id");
        listener.registerUriPattern(DataSource3, URISpace3 + "$id");
        listener.registerUriPattern(DataSource3, URISpace3a + "$id");

        int mappingSet = listener.registerMappingSet(URISpace1, TEST_PREDICATE, URISpace2, SYMETRIC, ORIGINAL);
        listener.insertURLMapping(map1URL1, map1URL2, mappingSet, SYMETRIC);
        listener.insertURLMapping(map2URL1, map2URL2, mappingSet, SYMETRIC);
        listener.insertURLMapping(map3URL1, map3URL2, mappingSet, SYMETRIC);
        
        mappingSet2_3 = listener.registerMappingSet(URISpace2, TEST_PREDICATE, URISpace3, SYMETRIC, ORIGINAL);
        listener.insertURLMapping(map1URL2, map1URL3, mappingSet2_3, SYMETRIC);
        listener.insertURLMapping(map2URL2, map2URL3, mappingSet2_3, SYMETRIC);
        listener.insertURLMapping(map3URL2, map3URL3, mappingSet2_3, SYMETRIC);

        mappingSet = listener.registerMappingSet(URISpace1, TEST_PREDICATE, URISpace3, SYMETRIC, TRANSATIVE);
        listener.insertURLMapping(map1URL1, map1URL3, mappingSet, SYMETRIC);
        listener.insertURLMapping(map2URL1, map2URL3, mappingSet, SYMETRIC);
        listener.insertURLMapping(map3URL1, map3URL3, mappingSet, SYMETRIC);

        listener.closeInput();
    }


}
