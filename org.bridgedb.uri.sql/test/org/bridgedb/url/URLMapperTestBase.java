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

import org.bridgedb.utils.BridgeDBException;
import org.junit.BeforeClass;
import org.bridgedb.utils.IDMapperTestBase;

/**
 * Extends the Test base to include URL variables.
 * 
 * @author Christian
 */
public abstract class URLMapperTestBase extends IDMapperTestBase {
            
    //Must be instantiated by implementation of these tests.
    protected static URLMapper urlMapper;

    //Used by subsets during settup to check connection is ok.
    //If set to false will skip testClose(). 
    protected static boolean connectionOk = true;
    
    protected static String URISpace1;
    protected static String URISpace2;
    protected static String URISpace2a;
    protected static String URISpace3;
    protected static String URISpace3a;
    
    protected static String link1to2;
    protected static String link1to3;
    protected static String link2to1; 
    protected static String link2to3;
    protected static String link3to1;
    protected static String link3to2; 
    
    protected static String map1URL1;
    protected static String map1URL2;
    protected static String map1URL3;
    //Second set of URLs that are expected to map together.
    protected static String map2URL1;
    protected static String map2URL2;
    protected static String map2URL3;
    //Third Set of URLs which again should map to each other but not the above
    protected static String map3URL1;
    protected static String map3URL2;
    protected static String map3URL2a;
    protected static String map3URL3;
    //Add an id that does not exist and can not be used in freesearch
    //Or null if all Strings can be used.
    protected static String badID;
    //And a few URLs also not used
    protected static String mapBadURL1;
    protected static String mapBadURL2;
    protected static String mapBadURL3;

    protected static final String TEST_PREDICATE = "http://www.bridgedb.org/test#testPredicate";
    
    @BeforeClass
    public static void setupURLs() throws BridgeDBException{
      
        URISpace1 = "http://www.conceptwiki.org/concept/";
        URISpace2 = "http://www.chemspider.com/";
        URISpace2a = "http://rdf.chemspider.com/";
        URISpace3 = "http://data.kasabi.com/dataset/chembl-rdf/molecule/";
        URISpace3a = "http://linkedchemistry.info/chembl/molecule/";
         
        link1to2 = URISpace1 + "->" + URISpace2;
        link1to3 = URISpace1 + "->" + URISpace3;
        link2to1 = URISpace2 + "->" + URISpace1;
        link2to3 = URISpace2 + "->" + URISpace3;
        link3to1 = URISpace3 + "->" + URISpace1;
        link3to2 = URISpace3 + "->" + URISpace3;

        map1URL1 = map1xref1.getUrl();
        map1URL2 = URISpace2 + ds2Id1;
        map1URL3 = map1xref3.getUrl();
        //Second set of URLs that are expected to map together.
        map2URL1 = map2xref1.getUrl();
        map2URL2 = URISpace2 + ds2Id2;
        map2URL3 = map2xref3.getUrl();
        //Third Set of URLs which again should map to each other but not the above
        map3URL1 = map3xref1.getUrl();
        map3URL2 = URISpace2 + ds2Id3;
        map3URL2a = URISpace2a + ds2Id3;
        map3URL3 = map3xref3.getUrl();
         //And a few URLs also not used
        mapBadURL1 = "www.notInURLMapper.com#" + ds1Id1;
        mapBadURL2 = URISpace2 + badID;
        mapBadURL3 = "www.notInURLMapper.com#789";
    }
    
}
