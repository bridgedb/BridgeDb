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
package org.bridgedb.uri;

import org.bridgedb.utils.BridgeDBException;
import org.junit.BeforeClass;
import org.bridgedb.utils.IDMapperTestBase;

/**
 * Extends the Test base to include Uri variables.
 * 
 * @author Christian
 */
public abstract class UriMapperTestBase extends IDMapperTestBase {
            
    //Must be instantiated by implementation of these tests.
    protected static UriMapper uriMapper;

    //Used by subsets during settup to check connection is ok.
    //If set to false will skip testClose(). 
    protected static boolean connectionOk = true;
    
    protected static String uriSpace1;
    protected static String uriSpace2;
    protected static String uriSpace2a;
    protected static String uriSpace3;
    protected static String uriSpace3a;
    
    protected static String link1to2;
    protected static String link1to3;
    protected static String link2to1; 
    protected static String link2to3;
    protected static String link3to1;
    protected static String link3to2; 
    
    protected static String map1Uri1;
    protected static String map1Uri2;
    protected static String map1Uri3;
    //Second set of Uris that are expected to map together.
    protected static String map2Uri1;
    protected static String map2Uri2;
    protected static String map2Uri3;
    //Third Set of Uris which again should map to each other but not the above
    protected static String map3Uri1;
    protected static String map3Uri2;
    protected static String map3Uri2a;
    protected static String map3Uri3;
    //Add an id that does not exist and can not be used in freesearch
    //Or null if all Strings can be used.
    protected static String badID;
    //And a few Uris also not used
    protected static String mapBadUri1;
    protected static String mapBadUri2;
    protected static String mapBadUri3;

    protected static final String TEST_PREDICATE = "http://www.bridgedb.org/test#testPredicate";
    
    @BeforeClass
    public static void setupUris() throws BridgeDBException{
      
        uriSpace1 = "http://www.conceptwiki.org/concept/";
        uriSpace2 = "http://www.chemspider.com/";
        uriSpace2a = "http://rdf.chemspider.com/";
        uriSpace3 = "http://data.kasabi.com/dataset/chembl-rdf/molecule/";
        uriSpace3a = "http://linkedchemistry.info/chembl/molecule/";
         
        link1to2 = uriSpace1 + "->" + uriSpace2;
        link1to3 = uriSpace1 + "->" + uriSpace3;
        link2to1 = uriSpace2 + "->" + uriSpace1;
        link2to3 = uriSpace2 + "->" + uriSpace3;
        link3to1 = uriSpace3 + "->" + uriSpace1;
        link3to2 = uriSpace3 + "->" + uriSpace3;

        map1Uri1 = map1xref1.getUrl();
        map1Uri2 = uriSpace2 + ds2Id1;
        map1Uri3 = map1xref3.getUrl();
        //Second set of URLs that are expected to map together.
        map2Uri1 = map2xref1.getUrl();
        map2Uri2 = uriSpace2 + ds2Id2;
        map2Uri3 = map2xref3.getUrl();
        //Third Set of URLs which again should map to each other but not the above
        map3Uri1 = map3xref1.getUrl();
        map3Uri2 = uriSpace2 + ds2Id3;
        map3Uri2a = uriSpace2a + ds2Id3;
        map3Uri3 = map3xref3.getUrl();
         //And a few Uris also not used
        mapBadUri1 = "www.notInUriMapper.com#" + ds1Id1;
        mapBadUri2 = uriSpace2 + badID;
        mapBadUri3 = "www.notInUriMapper.com#789";
    }
    
}
