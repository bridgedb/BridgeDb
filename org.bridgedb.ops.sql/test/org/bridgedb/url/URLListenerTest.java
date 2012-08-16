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
package org.bridgedb.url;

import org.bridgedb.IDMapperException;

/**
 *
 * @author Christian
 */
public abstract class URLListenerTest extends URLMapperTestBase{
        
    protected static URLListener listener;
    protected static final String TEST_PREDICATE = "http://www.bridgedb.org/test#testPredicate";
    private static final boolean SYMETRIC = true;
    private static final boolean ORIGINAL = false;
    private static final boolean TRANSATIVE = true;

    public static void loadData() throws IDMapperException{
        listener.registerUriSpace(DataSource1, URISpace1);
        listener.registerUriSpace(DataSource2, URISpace2);
        listener.registerUriSpace(DataSource3, URISpace3);

        int mappingSet = listener.registerMappingSet(URISpace1, TEST_PREDICATE, URISpace2, SYMETRIC, ORIGINAL);
        listener.insertURLMapping(map1URL1, map1URL2, mappingSet, SYMETRIC);
        listener.insertURLMapping(map2URL1, map2URL2, mappingSet, SYMETRIC);
        listener.insertURLMapping(map3URL1, map3URL2, mappingSet, SYMETRIC);
        
        mappingSet = listener.registerMappingSet(URISpace2, TEST_PREDICATE, URISpace3, SYMETRIC, ORIGINAL);
        listener.insertURLMapping(map1URL2, map1URL3, mappingSet, SYMETRIC);
        listener.insertURLMapping(map2URL2, map2URL3, mappingSet, SYMETRIC);
        listener.insertURLMapping(map3URL2, map3URL3, mappingSet, SYMETRIC);

        mappingSet = listener.registerMappingSet(URISpace1, TEST_PREDICATE, URISpace3, SYMETRIC, TRANSATIVE);
        listener.insertURLMapping(map1URL1, map1URL3, mappingSet, SYMETRIC);
        listener.insertURLMapping(map2URL1, map2URL3, mappingSet, SYMETRIC);
        listener.insertURLMapping(map3URL1, map3URL3, mappingSet, SYMETRIC);

        listener.closeInput();
    }


}
