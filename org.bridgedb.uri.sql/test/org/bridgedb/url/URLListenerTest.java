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

import org.bridgedb.rdf.UriPattern;
import org.bridgedb.utils.BridgeDBException;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * Base class of all Test using URLs
 *
 * Adds a method for loading the test data.
 * @author Christian
 */
public abstract class URLListenerTest extends URLMapperTestBase{
        
    protected static URLListener listener;
    protected static final String TEST_JUSTIFICATION1 = "http://www.bridgedb.org/test#testJustification1";
    protected static final String TEST_JUSTIFICATION2 = "http://www.bridgedb.org/test#testJustification2";
    protected static final String TEST_PREDICATE = "http://www.w3.org/2004/02/skos/core#exactMatch";
    private static final boolean SYMETRIC = true;
    private static final boolean ORIGINAL = false;
    private static final boolean TRANSATIVE = true;
    protected static int mappingSet2_3;
 
    protected static UriPattern uriPattern1;
    protected static UriPattern uriPattern2;
    protected static UriPattern uriPattern3;
    protected static UriPattern uriPatternBad;
    
    @BeforeClass
    public static void setupUriPatterns() throws BridgeDBException{
        setupURLs();
        connectionOk = true;
        uriPattern1 = UriPattern.byNameSpace(URISpace1);
        uriPattern2 = UriPattern.byNameSpace(URISpace2);
        uriPattern3 = UriPattern.byNameSpace(URISpace3);
        uriPatternBad = UriPattern.byNameSpace("http://www.example.com/URLMapperTest/Bad");
    }
        
    /**
     * Method for loading the Test data
     * Should be called in a @beforeClass method after setting listener
     * 
     * @throws BridgeDBException
     */
    public static void loadData() throws BridgeDBException{
        listener.registerUriPattern(DataSource1, URISpace1 + "$id");
        listener.registerUriPattern(DataSource2, URISpace2 + "$id");
        listener.registerUriPattern(DataSource2, URISpace2a + "$id");
        listener.registerUriPattern(DataSource3, URISpace3 + "$id");
        listener.registerUriPattern(DataSource3, URISpace3a + "$id");

        int mappingSet = listener.registerMappingSet(uriPattern1, TEST_PREDICATE, 
        		TEST_JUSTIFICATION1, uriPattern2, SYMETRIC, ORIGINAL);
        listener.insertURLMapping(map1URL1, map1URL2, mappingSet, SYMETRIC);
        listener.insertURLMapping(map2URL1, map2URL2, mappingSet, SYMETRIC);
        listener.insertURLMapping(map3URL1, map3URL2, mappingSet, SYMETRIC);
        
        mappingSet2_3 = listener.registerMappingSet(uriPattern2, TEST_PREDICATE, 
        		TEST_JUSTIFICATION2, uriPattern3, SYMETRIC, ORIGINAL);
        listener.insertURLMapping(map1URL2, map1URL3, mappingSet2_3, SYMETRIC);
        listener.insertURLMapping(map2URL2, map2URL3, mappingSet2_3, SYMETRIC);
        listener.insertURLMapping(map3URL2, map3URL3, mappingSet2_3, SYMETRIC);

        mappingSet = listener.registerMappingSet(uriPattern1, TEST_PREDICATE, 
        		TEST_JUSTIFICATION2, uriPattern3, SYMETRIC, TRANSATIVE);
        listener.insertURLMapping(map1URL1, map1URL3, mappingSet, SYMETRIC);
        listener.insertURLMapping(map2URL1, map2URL3, mappingSet, SYMETRIC);
        listener.insertURLMapping(map3URL1, map3URL3, mappingSet, SYMETRIC);

        listener.closeInput();
    }


}
