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
import java.util.regex.Pattern;
import org.bridgedb.DataSourcePatterns;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.rdf.UriPatternType;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.uri.api.UriMapper;
import org.bridgedb.uri.tools.Lens;
import org.bridgedb.uri.tools.RegexUriPattern;
import org.bridgedb.uri.tools.UriListener;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.IDMapperTestBase;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;

/**
 * Base class of all Test using Uris
 *
 * Adds a method for loading the test data.
 * @author Christian
 */
public abstract class FrequencyTest extends IDMapperTestBase{
        
    //Must be instantiated by implementation of these tests.
    protected static UriMapper uriMapper;
    protected static UriListener listener;

    protected static final String TEST_PREDICATE = "http://www.w3.org/2004/02/skos/core#exactMatch";
        
    public static final boolean SYMETRIC = true;
    public static final Set<String> NO_VIA = null;
    public static final Set<Integer> NO_CHAIN = null;
 
    protected static UriPattern uriPattern1;
    protected static UriPattern uriPattern2;
    protected static RegexUriPattern regexUriPattern1;
    protected static RegexUriPattern regexUriPattern2;
    

    protected static String uriSpace1;
    protected static String uriSpace2;
    
    @BeforeClass
    public static void setupUriPatterns() throws BridgeDBException{
        uriSpace1 = "http://www.example.com/left_";
        uriSpace2 = "http://www.example.com/right_";
        DataSourcePatterns.registerPattern(DataSource2, Pattern.compile("^\\d+$"));
        uriPattern1 = UriPattern.register(uriSpace1 + "$id", dataSource1Code, UriPatternType.dataSourceUriPattern);
        uriPattern2 = UriPattern.register(uriSpace2 + "$id", dataSource2Code, UriPatternType.dataSourceUriPattern);
        
        regexUriPattern1 = RegexUriPattern.factory(uriPattern1, dataSource1Code);
        regexUriPattern2 = RegexUriPattern.factory(uriPattern2, dataSource2Code);
    }
        
    /**
     * Method for loading the Test data
     * Should be called in a @beforeClass method after setting listener
     * 
     * @throws BridgeDBException
     */
    public static int loadData(int[] counts) throws BridgeDBException{

        Resource resource = new URIImpl("http://example.com/1to2");
        int mappingSet = listener.registerMappingSet(regexUriPattern1, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPattern2, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);
        int source = 0;
        for (int i = 0; i < counts.length; i++){
            for (int j = 0; j < counts[i]; j++){
                source++;
                String left = uriSpace1 + source;
                for (int k = 0; k <= i; k++){
                    String right = uriSpace2 + k;
                    listener.insertUriMapping(left, right, mappingSet, SYMETRIC);
                }
            }
        }
        
        listener.closeInput();
        return mappingSet;
    }

    private Integer countLinks(int[] counts){
        int total = 0;
        for (int i = 0; i < counts.length; i++){
            total+= ((i+1)* counts[i]);
        }     
        return total;
    }
    
    @Test
    public void testFrequency1() throws Exception {
        //Date start = new Date();
        report("Frequency1");
        int[] counts = {3,2,2,1,1,1};
        int mappingSet = loadData(counts);
        MappingSetInfo info = uriMapper.getMappingSetInfo(mappingSet);
        assertEquals(countLinks(counts),info.getNumberOfLinks());
        assertEquals((Integer)10,info.getNumberOfSources());
        assertEquals((Integer)6,info.getNumberOfTargets());
        assertEquals((Integer)2,info.getFrequencyMedium());
        assertEquals((Integer)4,info.getFrequency75());
        assertEquals((Integer)5,info.getFrequency90());
        assertEquals((Integer)6,info.getFrequency99());
        assertEquals((Integer)6,info.getFrequencyMax());
    }

    @Test
    public void testFrequency2() throws Exception {
        //Date start = new Date();
        report("Frequency2");
        int[] counts = {30,20,20,10,10,10};
        int mappingSet = loadData(counts);
        MappingSetInfo info = uriMapper.getMappingSetInfo(mappingSet);
        assertEquals(countLinks(counts),info.getNumberOfLinks());
        assertEquals((Integer)100,info.getNumberOfSources());
        assertEquals((Integer)6,info.getNumberOfTargets());
        assertEquals((Integer)2,info.getFrequencyMedium());
        assertEquals((Integer)4,info.getFrequency75());
        assertEquals((Integer)5,info.getFrequency90());
        assertEquals((Integer)6,info.getFrequency99());
        assertEquals((Integer)6,info.getFrequencyMax());
    }

    @Test
    public void testFrequency3() throws Exception {
        //Date start = new Date();
        report("Frequency3");
        int[] counts = {30,25,10,10,5,6,4,5,4,1};
        int mappingSet = loadData(counts);
        MappingSetInfo info = uriMapper.getMappingSetInfo(mappingSet);
        assertEquals(countLinks(counts),info.getNumberOfLinks());
        assertEquals((Integer)100,info.getNumberOfSources());
        assertEquals((Integer)10,info.getNumberOfTargets());
        assertEquals((Integer)2,info.getFrequencyMedium());
        assertEquals((Integer)4,info.getFrequency75());
        assertEquals((Integer)7,info.getFrequency90());
        assertEquals((Integer)9,info.getFrequency99());
        assertEquals((Integer)10,info.getFrequencyMax());
    }

}
