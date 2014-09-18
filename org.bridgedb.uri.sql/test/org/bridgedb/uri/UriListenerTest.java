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

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.bridgedb.DataSourcePatterns;
import org.bridgedb.Xref;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.rdf.UriPatternType;
import org.bridgedb.uri.api.UriMapper;
import org.bridgedb.uri.lens.Lens;
import org.bridgedb.uri.tools.RegexUriPattern;
import org.bridgedb.uri.tools.UriListener;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.IDMapperTestBase;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;

/**
 * Base class of all Test using Uris
 *
 * Adds a method for loading the test data.
 * @author Christian
 */
public abstract class UriListenerTest extends IDMapperTestBase{
        
    protected static UriListener listener;
    protected static final String TEST_PREDICATE = "http://www.w3.org/2004/02/skos/core#exactMatch";
        
    public static final boolean SYMETRIC = true;

    protected static final int mappingSet2_3 = 3;
 
    protected static UriPattern uriPattern1;
    protected static UriPattern uriPattern2;
    protected static UriPattern uriPattern3;
    protected static RegexUriPattern regexUriPattern1;
    protected static RegexUriPattern regexUriPattern2;
    protected static RegexUriPattern regexUriPattern3;
    
    protected static String stringPattern1;
    protected static String stringPattern2;
    protected static String stringPattern3;
    
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
    //Second version of third Set of Uris which again should map to each in one Lens and above in allLens
    protected static String map1AUri1;
    protected static String map1AUri2;
    protected static String map1AUri3;

    //Second set of Uris that are expected to map together.
    protected static String map2Uri1;
    protected static String map2Uri2;
    protected static String map2Uri3;
    //Second version of third Set of Uris which again should map to each in one Lens and above in allLens
    protected static String map2AUri1;
    protected static String map2AUri2;
    protected static String map2AUri3;

    //Third Set of Uris which again should map to each other but not the above
    protected static String map3Uri1;
    protected static String map3Uri2;
    protected static String map3Uri2a;
    protected static String map3Uri3;
    //Second version of third Set of Uris which again should map to each in one Lens and above in allLens
    protected static String map3AUri1;
    protected static String map3AUri2;
    protected static String map3AUri2a;
    protected static String map3AUri3;
    //Add an id that does not exist and can not be used in freesearch
    //Or null if all Strings can be used.
    protected static String badID;
    //And a few Uris also not used
    protected static String badUriPrefix;
    protected static String mapBadUri1;
    protected static String mapBadUri2;
    protected static String mapBadUri3;
        
    @BeforeClass
    public static void setupUris() throws BridgeDBException{
        map1Axref1 = new Xref(ds1Id1+"000001", DataSource1);
        map1Axref2 = new Xref(ds2Id1+"000001", DataSource2);
        map1Axref3 = new Xref(ds3Id1+"000001", DataSource3);
        map2Axref1 = new Xref("bd42675d-9966-48f5-b42e-f6a0c5ec6794000001", DataSource1);
        map2Axref2 = new Xref(ds2Id2+"000001", DataSource2);
        map2Axref3 = new Xref("370186000001", DataSource3);
        map3Axref1 = new Xref("9d4a6a08-6757-4ff2-98c1-e3c8b3e095cc000001", DataSource1);
        map3Axref2 = new Xref(ds2Id3+"000001" , DataSource2);
        map3Axref3 = new Xref("520018000001", DataSource3);

        uriSpace1 = "http://www.conceptwiki.org/concept/";
        uriSpace2 = "http://www.chemspider.com/";
        uriSpace2a = "http://rdf.chemspider.com/";
        uriSpace3 = "http://data.kasabi.com/dataset/chembl-rdf/molecule/m";
        uriSpace3a = "http://linkedchemistry.info/chembl/molecule/m";
         
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
        map1AUri2 = uriSpace2 + ds2Id1+"000001";
        map1AUri3 = map1Axref3.getUrl();
        //Second set of URLs that are expected to map together.
        map2Uri1 = map2xref1.getUrl();
        map2Uri2 = uriSpace2 + ds2Id2;
        map2Uri3 = map2xref3.getUrl();
        map2AUri1 = map2Axref1.getUrl();
        map2AUri2 = uriSpace2 + ds2Id2+"000001";
        map2AUri3 = map2Axref3.getUrl();
        //Third Set of URLs which again should map to each other but not the above
        map3Uri1 = map3xref1.getUrl();
        map3Uri2 = uriSpace2 + ds2Id3;
        map3Uri2a = uriSpace2a + ds2Id3;
        map3Uri3 = map3xref3.getUrl();
        map3AUri1 = map3Axref1.getUrl();
        map3AUri2 = uriSpace2 + ds2Id3+"000001";
        map3AUri2a = uriSpace2a + ds2Id3+"000001";
        map3AUri3 = map3Axref3.getUrl();
         //And a few Uris also not used
        badUriPrefix = "www.notInUriMapper.com#";
        mapBadUri1 = badUriPrefix + ds1Id1;
        mapBadUri2 = uriSpace2 + badID;
        mapBadUri3 = badUriPrefix + "#789";
        
    }
    
    @BeforeClass
    public static void setupUriPatterns() throws BridgeDBException{
        setupUris();
        connectionOk = true;
        DataSourcePatterns.registerPattern(DataSource2, Pattern.compile("^\\d+$"));
        uriPattern1 = UriPattern.register(uriSpace1 + "$id", dataSource1Code, UriPatternType.dataSourceUriPattern);
        uriPattern2 = UriPattern.register(uriSpace2 + "$id", dataSource2Code, UriPatternType.dataSourceUriPattern);
        UriPattern.register(uriSpace2a + "$id", dataSource2Code, UriPatternType.dataSourceUriPattern);
        uriPattern3 = UriPattern.register(uriSpace3 + "$id", dataSource3Code, UriPatternType.dataSourceUriPattern);
        UriPattern.register(uriSpace3a + "$id", dataSource3Code, UriPatternType.dataSourceUriPattern);
        
        regexUriPattern1 = RegexUriPattern.factory(uriPattern1, dataSource1Code);
        regexUriPattern2 = RegexUriPattern.factory(uriPattern2, dataSource2Code);
        regexUriPattern3 = RegexUriPattern.factory(uriPattern3, dataSource3Code);
        
        stringPattern1 = uriSpace1 + "$id";
        stringPattern2 = uriSpace2 + "$id";
        stringPattern3 = uriSpace3 + "$id";
        
    }
        
    /**
     * Method for loading the Test data
     * Should be called in a @beforeClass method after setting listener
     * 
     * @throws BridgeDBException
     */
    public static void loadDataPart1() throws BridgeDBException{

        Resource resource = new URIImpl("http://example.com/1to2");
        int mappingSet = listener.registerMappingSet(regexUriPattern1, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), Lens.getDefaultJustifictaionString(), regexUriPattern2, resource, resource);
        listener.insertUriMapping(map1Uri1, map1Uri2, mappingSet, SYMETRIC);
        listener.insertUriMapping(map2Uri1, map2Uri2, mappingSet, SYMETRIC);
        listener.insertUriMapping(map3Uri1, map3Uri2, mappingSet, SYMETRIC);
        
        resource = new URIImpl("http://example.com/2to3");
        mappingSet = listener.registerMappingSet(regexUriPattern2, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), Lens.getDefaultJustifictaionString(), regexUriPattern3, resource, resource);
        assertEquals(mappingSet2_3, mappingSet);
        listener.insertUriMapping(map1Uri2, map1Uri3, mappingSet2_3, SYMETRIC);
        listener.insertUriMapping(map2Uri2, map2Uri3, mappingSet2_3, SYMETRIC);
        listener.insertUriMapping(map3Uri2, map3Uri3, mappingSet2_3, SYMETRIC);

        //Close here to test recover
        listener.closeInput();
        
        resource = new URIImpl("http://example.com/1to2Lensed");
        mappingSet = listener.registerMappingSet(regexUriPattern1, TEST_PREDICATE, 
        		Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPattern2, resource, resource);
        listener.insertUriMapping(map1Uri1, map1AUri2, mappingSet, SYMETRIC);
        listener.insertUriMapping(map2Uri1, map2AUri2, mappingSet, SYMETRIC);
        listener.insertUriMapping(map3Uri1, map3AUri2, mappingSet, SYMETRIC);
        listener.insertUriMapping(map1AUri1, map1Uri2, mappingSet, SYMETRIC);
        listener.insertUriMapping(map2AUri1, map2Uri2, mappingSet, SYMETRIC);
        listener.insertUriMapping(map3AUri1, map3Uri2, mappingSet, SYMETRIC);

        resource = new URIImpl("http://example.com/2to3Lensed");
        mappingSet = listener.registerMappingSet(regexUriPattern2, TEST_PREDICATE, 
        		Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPattern3, resource, resource);
        listener.insertUriMapping(map1Uri2, map1AUri3, mappingSet, SYMETRIC);
        listener.insertUriMapping(map2Uri2, map2AUri3, mappingSet, SYMETRIC);
        listener.insertUriMapping(map3Uri2, map3AUri3, mappingSet, SYMETRIC);
        listener.insertUriMapping(map1AUri2, map1Uri3, mappingSet, SYMETRIC);
        listener.insertUriMapping(map2AUri2, map2Uri3, mappingSet, SYMETRIC);
        listener.insertUriMapping(map3AUri2, map3Uri3, mappingSet, SYMETRIC);

    }
    
    public static void loadData() throws BridgeDBException{
        loadDataPart1();
        //Close here if not testing recover
        listener.closeInput();
    }

    /**
     * Method for loading the Test data
     * Should be called in a @beforeClass method after setting listener
     * 
     * @throws BridgeDBException
     */
    public static void loadData2Way() throws BridgeDBException{
        Resource resource = new URIImpl("http://example.com/2to3Lensed");
        int mappingSet = listener.registerMappingSet(regexUriPattern2, TEST_PREDICATE, 
        		Lens.getTestJustifictaion() +"Forward", Lens.getTestJustifictaion() +"BackWard", regexUriPattern3, resource, resource);
        listener.insertUriMapping(map1Uri2, map1AUri3, mappingSet, SYMETRIC);
        listener.insertUriMapping(map2Uri2, map2AUri3, mappingSet, SYMETRIC);
        listener.insertUriMapping(map3Uri2, map3AUri3, mappingSet, SYMETRIC);
        listener.closeInput();
    }
    

    protected void checkForNoOtherLensXrefs(Set results){
        assertFalse(results.contains(map2Axref1));
        assertFalse(results.contains(map2Axref2));
        assertFalse(results.contains(map2Axref3));        
    }

    protected void checkForNoOtherlensId(Set results){
        assertFalse(results.contains(map3AUri1));
        assertFalse(results.contains(map3AUri2));
        assertFalse(results.contains(map3AUri2a));
        assertFalse(results.contains(map3AUri3));
    }

}
