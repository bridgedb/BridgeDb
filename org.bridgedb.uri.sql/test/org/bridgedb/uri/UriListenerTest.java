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
package org.bridgedb.uri;

import java.util.Set;
import org.bridgedb.Xref;
import org.bridgedb.uri.UriListener;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.IDMapperTestBase;
import org.junit.BeforeClass;
import org.junit.Ignore;
import static org.junit.Assert.*;

/**
 * Base class of all Test using Uris
 *
 * Adds a method for loading the test data.
 * @author Christian
 */
public abstract class UriListenerTest extends IDMapperTestBase{
        
    protected static UriListener listener;
    protected static final String TEST_PREDICATE = "http://www.w3.org/2004/02/skos/core#exactMatch";
        
    private static final boolean SYMETRIC = true;
    private static final boolean ORIGINAL = false;
    private static final boolean TRANSATIVE = true;
    protected static final int mappingSet2_3 = 3;
 
    protected static UriPattern uriPattern1;
    protected static UriPattern uriPattern2;
    protected static UriPattern uriPattern3;
    protected static UriPattern uriPatternBad;
    
    //Must be instantiated by implementation of these tests.
    protected static UriMapper uriMapper;

    //Used by subsets during settup to check connection is ok.
    //If set to false will skip testClose(). 
    protected static boolean connectionOk = true;
    
    protected static Xref map1Axref1;
    protected static Xref map1Axref2;
    protected static Xref map1Axref3;
    protected static Xref map2Axref1;
    protected static Xref map2Axref2;
    protected static Xref map2Axref3;
    protected static Xref map3Axref1;
    protected static Xref map3Axref2;
    protected static Xref map3Axref3;

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
    //Second version of third Set of Uris which again should map to each in one profile and above in allProfile
    protected static String map1AUri1;
    protected static String map1AUri2;
    protected static String map1AUri3;

    //Second set of Uris that are expected to map together.
    protected static String map2Uri1;
    protected static String map2Uri2;
    protected static String map2Uri3;
    //Second version of third Set of Uris which again should map to each in one profile and above in allProfile
    protected static String map2AUri1;
    protected static String map2AUri2;
    protected static String map2AUri3;

    //Third Set of Uris which again should map to each other but not the above
    protected static String map3Uri1;
    protected static String map3Uri2;
    protected static String map3Uri2a;
    protected static String map3Uri3;
    //Second version of third Set of Uris which again should map to each in one profile and above in allProfile
    protected static String map3AUri1;
    protected static String map3AUri2;
    protected static String map3AUri2a;
    protected static String map3AUri3;
    //Add an id that does not exist and can not be used in freesearch
    //Or null if all Strings can be used.
    protected static String badID;
    //And a few Uris also not used
    protected static String mapBadUri1;
    protected static String mapBadUri2;
    protected static String mapBadUri3;
   
    @BeforeClass
    public static void setupUris() throws BridgeDBException{
        map1Axref1 = new Xref(ds1Id1+"_A", DataSource1);
        map1Axref2 = new Xref(ds2Id1+"_A", DataSource2);
        map1Axref3 = new Xref(ds3Id1+"_A", DataSource3);
        map2Axref1 = new Xref("bd42675d-9966-48f5-b42e-f6a0c5ec6794_A", DataSource1);
        map2Axref2 = new Xref(ds2Id2+"_A", DataSource2);
        map2Axref3 = new Xref("m370186_A", DataSource3);
        map3Axref1 = new Xref("9d4a6a08-6757-4ff2-98c1-e3c8b3e095cc_A", DataSource1);
        map3Axref2 = new Xref(ds2Id3+"_A" , DataSource2);
        map3Axref3 = new Xref("m520018_A", DataSource3);

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
        map1AUri1 = map1Axref1.getUrl();
        map1AUri2 = uriSpace2 + ds2Id1+"_A";
        map1AUri3 = map1Axref3.getUrl();
        //Second set of URLs that are expected to map together.
        map2Uri1 = map2xref1.getUrl();
        map2Uri2 = uriSpace2 + ds2Id2;
        map2Uri3 = map2xref3.getUrl();
        map2AUri1 = map2Axref1.getUrl();
        map2AUri2 = uriSpace2 + ds2Id2+"_A";
        map2AUri3 = map2Axref3.getUrl();
        //Third Set of URLs which again should map to each other but not the above
        map3Uri1 = map3xref1.getUrl();
        map3Uri2 = uriSpace2 + ds2Id3;
        map3Uri2a = uriSpace2a + ds2Id3;
        map3Uri3 = map3xref3.getUrl();
        map3AUri1 = map3Axref1.getUrl();
        map3AUri2 = uriSpace2 + ds2Id3+"_A";
        map3AUri2a = uriSpace2a + ds2Id3+"_A";
        map3AUri3 = map3Axref3.getUrl();
         //And a few Uris also not used
        mapBadUri1 = "www.notInUriMapper.com#" + ds1Id1;
        mapBadUri2 = uriSpace2 + badID;
        mapBadUri3 = "www.notInUriMapper.com#789";
        
    }
    
    @BeforeClass
    public static void setupUriPatterns() throws BridgeDBException{
        setupUris();
        connectionOk = true;
        uriPattern1 = UriPattern.byNameSpace(uriSpace1);
        uriPattern2 = UriPattern.byNameSpace(uriSpace2);
        uriPattern3 = UriPattern.byNameSpace(uriSpace3);
        uriPatternBad = UriPattern.byNameSpace("http://www.example.com/UriMapperTest/Bad");
    }
        
    /**
     * Method for loading the Test data
     * Should be called in a @beforeClass method after setting listener
     * 
     * @throws BridgeDBException
     */
    public static void loadData() throws BridgeDBException{
        listener.registerUriPattern(DataSource1, uriSpace1 + "$id");
        listener.registerUriPattern(DataSource2, uriSpace2 + "$id");
        listener.registerUriPattern(DataSource2, uriSpace2a + "$id");
        listener.registerUriPattern(DataSource3, uriSpace3 + "$id");
        listener.registerUriPattern(DataSource3, uriSpace3a + "$id");

        int mappingSet = listener.registerMappingSet(uriPattern1, TEST_PREDICATE, 
        		Profile.getDefaultJustifictaionString(), uriPattern2, SYMETRIC, ORIGINAL);
        listener.insertUriMapping(map1Uri1, map1Uri2, mappingSet, SYMETRIC);
        listener.insertUriMapping(map2Uri1, map2Uri2, mappingSet, SYMETRIC);
        listener.insertUriMapping(map3Uri1, map3Uri2, mappingSet, SYMETRIC);
        
        mappingSet = listener.registerMappingSet(uriPattern2, TEST_PREDICATE, 
        		Profile.getDefaultJustifictaionString(), uriPattern3, SYMETRIC, ORIGINAL);
        assertEquals(mappingSet2_3, mappingSet);
        listener.insertUriMapping(map1Uri2, map1Uri3, mappingSet2_3, SYMETRIC);
        listener.insertUriMapping(map2Uri2, map2Uri3, mappingSet2_3, SYMETRIC);
        listener.insertUriMapping(map3Uri2, map3Uri3, mappingSet2_3, SYMETRIC);

        mappingSet = listener.registerMappingSet(uriPattern1, TEST_PREDICATE, 
        		Profile.getDefaultJustifictaionString(), uriPattern3, SYMETRIC, TRANSATIVE);
        listener.insertUriMapping(map1Uri1, map1Uri3, mappingSet, SYMETRIC);
        listener.insertUriMapping(map2Uri1, map2Uri3, mappingSet, SYMETRIC);
        listener.insertUriMapping(map3Uri1, map3Uri3, mappingSet, SYMETRIC);

        mappingSet = listener.registerMappingSet(uriPattern1, TEST_PREDICATE, 
        		Profile.getTestJustifictaion(), uriPattern2, SYMETRIC, ORIGINAL);
        listener.insertUriMapping(map1Uri1, map1AUri2, mappingSet, SYMETRIC);
        listener.insertUriMapping(map2Uri1, map2AUri2, mappingSet, SYMETRIC);
        listener.insertUriMapping(map3Uri1, map3AUri2, mappingSet, SYMETRIC);
        listener.insertUriMapping(map1AUri1, map1Uri2, mappingSet, SYMETRIC);
        listener.insertUriMapping(map2AUri1, map2Uri2, mappingSet, SYMETRIC);
        listener.insertUriMapping(map3AUri1, map3Uri2, mappingSet, SYMETRIC);

        mappingSet = listener.registerMappingSet(uriPattern2, TEST_PREDICATE, 
        		Profile.getTestJustifictaion(), uriPattern3, SYMETRIC, ORIGINAL);
        listener.insertUriMapping(map1Uri2, map1AUri3, mappingSet, SYMETRIC);
        listener.insertUriMapping(map2Uri2, map2AUri3, mappingSet, SYMETRIC);
        listener.insertUriMapping(map3Uri2, map3AUri3, mappingSet, SYMETRIC);
        listener.insertUriMapping(map1AUri2, map1Uri3, mappingSet, SYMETRIC);
        listener.insertUriMapping(map2AUri2, map2Uri3, mappingSet, SYMETRIC);
        listener.insertUriMapping(map3AUri2, map3Uri3, mappingSet, SYMETRIC);

        mappingSet = listener.registerMappingSet(uriPattern1, TEST_PREDICATE, 
        		Profile.getTestProfile(), uriPattern3, SYMETRIC, TRANSATIVE);
        listener.insertUriMapping(map1Uri1, map1AUri3, mappingSet, SYMETRIC);
        listener.insertUriMapping(map2Uri1, map2AUri3, mappingSet, SYMETRIC);
        listener.insertUriMapping(map3Uri1, map3AUri3, mappingSet, SYMETRIC);
        listener.insertUriMapping(map1AUri1, map1Uri3, mappingSet, SYMETRIC);
        listener.insertUriMapping(map2AUri1, map2Uri3, mappingSet, SYMETRIC);
        listener.insertUriMapping(map3AUri1, map3Uri3, mappingSet, SYMETRIC);

        listener.closeInput();
    }

    protected void checkForNoOtherProfileXrefs(Set results){
        assertFalse(results.contains(map2Axref1));
        assertFalse(results.contains(map2Axref2));
        assertFalse(results.contains(map2Axref3));        
    }

    protected void checkForNoOtherProfileUri(Set results){
        assertFalse(results.contains(map3AUri1));
        assertFalse(results.contains(map3AUri2));
        assertFalse(results.contains(map3AUri2a));
        assertFalse(results.contains(map3AUri3));
    }

}
