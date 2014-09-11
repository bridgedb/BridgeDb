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
package org.bridgedb.mysql;

import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.pairs.IdSysCodePair;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.rdf.UriPatternType;
import org.bridgedb.sql.AbstractMapping;
import org.bridgedb.sql.DirectMapping;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.sql.TransitiveMapping;
import org.bridgedb.uri.UriListenerTest;
import static org.bridgedb.uri.UriListenerTest.NO_CHAIN;
import static org.bridgedb.uri.UriListenerTest.NO_VIA;
import static org.bridgedb.uri.UriListenerTest.SYMETRIC;
import org.bridgedb.uri.lens.Lens;
import org.bridgedb.uri.tools.RegexUriPattern;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.IDMapperTestBase;
import org.bridgedb.utils.Reporter;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;

/**
 * Tests the UriMapper interface (and by loading the UriListener interface)
 *
 * Should be passable by any implementation of UriMapper that has the test data loaded.
 * 
 * @author Christian
 */
public class TransitiveTest {
   
    private static SQLUriMapper sqlUriMapper;
    private static final String TEST_PREDICATE = "http://www.w3.org/2004/02/skos/core#exactMatch";

    private static final String sysCodeA = "TransitiveTestA";
    private static final String sysCodeB = "TransitiveTestB";
    private static final String sysCodeC = "TransitiveTestC";
    private static final String sysCodeD = "TransitiveTestD";
    private static final String sysCodeE = "TransitiveTestE";
    private static final String sysCodeF = "TransitiveTestF";
           
    private static final DataSource dsA = DataSource.register(sysCodeA, sysCodeA).asDataSource();
    private static final DataSource dsB = DataSource.register(sysCodeB, sysCodeB).asDataSource();
    private static final DataSource dsC = DataSource.register(sysCodeC, sysCodeC).asDataSource();
    private static final DataSource dsD = DataSource.register(sysCodeD, sysCodeD).asDataSource();
    private static final DataSource dsE = DataSource.register(sysCodeE, sysCodeE).asDataSource();
    private static final DataSource dsF = DataSource.register(sysCodeF, sysCodeF).asDataSource();
   
    private static RegexUriPattern regexUriPatternA;
    private static RegexUriPattern regexUriPatternB;
    private static RegexUriPattern regexUriPatternC;
    private static RegexUriPattern regexUriPatternD;
    private static RegexUriPattern regexUriPatternE;
    private static RegexUriPattern regexUriPatternF;

    private static final String prefixA = "http://example.com/dsA#";
    private static final String prefixB = "http://example.com/dsB#";
    private static final String prefixC = "http://example.com/dsC#";
    private static final String prefixD = "http://example.com/dsD#";
    private static final String prefixE = "http://example.com/dsE#";
    private static final String prefixF = "http://example.com/dsF#";
    
    private static int mappingSetAB;
    private static int mappingSetAC;
    private static int mappingSetAD;
    private static int mappingSetAE;
    private static int mappingSetAF;
    private static int mappingSetBC;
    private static int mappingSetBD;
    private static int mappingSetBE;
    private static int mappingSetBF;
    private static int mappingSetCD;
    private static int mappingSetCE;
    private static int mappingSetCF;
    private static int mappingSetDE;
    private static int mappingSetDF;
    private static int mappingSetEE;
    private static int mappingSetEF;
    private static int mappingSetFF;
    
    
    @BeforeClass
    public static void setupIDMapper() throws BridgeDBException{
        TestSqlFactory.checkSQLAccess();
        ConfigReader.useTest();
        UriPattern pattern = UriPattern.register(prefixA + "$id", sysCodeA, UriPatternType.dataSourceUriPattern);
        pattern = UriPattern.register(prefixB + "$id", sysCodeB, UriPatternType.dataSourceUriPattern);
        pattern = UriPattern.register(prefixC + "$id", sysCodeC, UriPatternType.dataSourceUriPattern);
        pattern = UriPattern.register(prefixD + "$id", sysCodeD, UriPatternType.dataSourceUriPattern);
        pattern = UriPattern.register(prefixE + "$id", sysCodeE, UriPatternType.dataSourceUriPattern);
        pattern = UriPattern.register(prefixF + "$id", sysCodeF, UriPatternType.dataSourceUriPattern);
        
        sqlUriMapper = SQLUriMapper.createNew();

        regexUriPatternA = RegexUriPattern.factory(prefixA, "", sysCodeA);
        regexUriPatternB = RegexUriPattern.factory(prefixB, "", sysCodeB);
        regexUriPatternC = RegexUriPattern.factory(prefixC, "", sysCodeC);
        regexUriPatternD = RegexUriPattern.factory(prefixD, "", sysCodeD);
        regexUriPatternE = RegexUriPattern.factory(prefixE, "", sysCodeE);
        regexUriPatternF = RegexUriPattern.factory(prefixF, "", sysCodeF);
        loadData();
        
        sqlUriMapper = SQLUriMapper.getExisting();
    }
    
    public static void loadData() throws BridgeDBException{
        
        Resource resource = new URIImpl("http://example.com/TransitiveTest/AtoB");
        mappingSetAB = sqlUriMapper.registerMappingSet(regexUriPatternA, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternB, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);
        
        resource = new URIImpl("http://example.com/TransitiveTest/AtoC");
        mappingSetAC = sqlUriMapper.registerMappingSet(regexUriPatternA, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternC, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);
        
        resource = new URIImpl("http://example.com/TransitiveTest/AtoD");
        mappingSetAD = sqlUriMapper.registerMappingSet(regexUriPatternA, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternD, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);

        resource = new URIImpl("http://example.com/TransitiveTest/AtoE");
        mappingSetAE = sqlUriMapper.registerMappingSet(regexUriPatternA, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternE, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);
        
        resource = new URIImpl("http://example.com/TransitiveTest/AtoF");
        mappingSetAF = sqlUriMapper.registerMappingSet(regexUriPatternA, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternF, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);

        resource = new URIImpl("http://example.com/TransitiveTest/BtoC");
        mappingSetBC = sqlUriMapper.registerMappingSet(regexUriPatternB, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternC, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);
        
        resource = new URIImpl("http://example.com/TransitiveTest/BtoD");
        mappingSetBD = sqlUriMapper.registerMappingSet(regexUriPatternB, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternD, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);

        resource = new URIImpl("http://example.com/TransitiveTest/BtoE");
        mappingSetBE = sqlUriMapper.registerMappingSet(regexUriPatternB, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternE, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);
        
        resource = new URIImpl("http://example.com/TransitiveTest/BtoF");
        mappingSetBF = sqlUriMapper.registerMappingSet(regexUriPatternB, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternF, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);

        resource = new URIImpl("http://example.com/TransitiveTest/CtoD");
        mappingSetCD = sqlUriMapper.registerMappingSet(regexUriPatternC, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternD, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);

        resource = new URIImpl("http://example.com/TransitiveTest/CtoE");
        mappingSetCE = sqlUriMapper.registerMappingSet(regexUriPatternC, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternE, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);

        resource = new URIImpl("http://example.com/TransitiveTest/CtoF");
        mappingSetCF = sqlUriMapper.registerMappingSet(regexUriPatternC, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternF, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);

        resource = new URIImpl("http://example.com/TransitiveTest/DtoE");
        mappingSetDE = sqlUriMapper.registerMappingSet(regexUriPatternD, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternE, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);

        resource = new URIImpl("http://example.com/TransitiveTest/DtoF");
        mappingSetDF = sqlUriMapper.registerMappingSet(regexUriPatternD, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternF, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);

        resource = new URIImpl("http://example.com/TransitiveTest/EtoE");
        mappingSetEE = sqlUriMapper.registerMappingSet(regexUriPatternE, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternE, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);

        resource = new URIImpl("http://example.com/TransitiveTest/EtoF");
        mappingSetEF = sqlUriMapper.registerMappingSet(regexUriPatternE, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternF, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);

        resource = new URIImpl("http://example.com/TransitiveTest/FtoF");
        mappingSetFF = sqlUriMapper.registerMappingSet(regexUriPatternF, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternF, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);

        //A1 -> B1 -> C1 -> D1
        sqlUriMapper.insertUriMapping(prefixA+"1", prefixB+"1", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"1", prefixC+"1", mappingSetBC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixC+"1", prefixD+"1", mappingSetCD, SYMETRIC);

        //A2 -> B2 -> C2 -> D2 -> A2
        sqlUriMapper.insertUriMapping(prefixA+"2", prefixB+"2", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"2", prefixC+"2", mappingSetBC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixC+"2", prefixD+"2", mappingSetCD, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"2", prefixD+"2", mappingSetAD, SYMETRIC);

        //A3a -> B3a -> C3 -> D3 -> A3b
        sqlUriMapper.insertUriMapping(prefixA+"3a", prefixB+"3", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"3", prefixC+"3", mappingSetBC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixC+"3", prefixD+"3", mappingSetCD, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"3b", prefixD+"3", mappingSetAD, SYMETRIC);

        //A4 -> B4  A4 -> C4  A4 -> D4
        sqlUriMapper.insertUriMapping(prefixA+"4", prefixB+"4", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"4", prefixC+"4", mappingSetAC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"4", prefixD+"4", mappingSetAD, SYMETRIC);

        //A5 -> B5a,B5b, C5a, c5b, D5a, D5b
        sqlUriMapper.insertUriMapping(prefixA+"5", prefixB+"5a", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"5", prefixB+"5b", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"5", prefixC+"5a", mappingSetAC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"5", prefixC+"5b", mappingSetAC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"5", prefixD+"5a", mappingSetAD, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"5", prefixD+"5b", mappingSetAD, SYMETRIC);

        //A6a -> B6a -> C6 -> B6b -> A6b
        sqlUriMapper.insertUriMapping(prefixA+"6a", prefixB+"6a", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"6b", prefixB+"6b", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"6a", prefixC+"6", mappingSetBC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"6b", prefixC+"6", mappingSetBC, SYMETRIC);
        
        //A7a -> B7 -> A7b
        sqlUriMapper.insertUriMapping(prefixA+"7a", prefixB+"7", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"7b", prefixB+"7", mappingSetAB, SYMETRIC);

        //A8 -> E8a -> E8b
        sqlUriMapper.insertUriMapping(prefixA+"8", prefixE+"8a", mappingSetAE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"8a", prefixE+"8b", mappingSetEE, SYMETRIC);
        
        //A9a -> E9a -> E9b -A9b
        sqlUriMapper.insertUriMapping(prefixA+"9a", prefixE+"9a", mappingSetAE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"9a", prefixE+"9b", mappingSetEE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"9b", prefixE+"9b", mappingSetAE, SYMETRIC);
        
        //A10 -> E10a -> E10b -A10
        sqlUriMapper.insertUriMapping(prefixA+"10", prefixE+"10a", mappingSetAE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"10a", prefixE+"10b", mappingSetEE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"10", prefixE+"10b", mappingSetAE, SYMETRIC);
        
         //A11 -> E11a -> E11b - B11
        sqlUriMapper.insertUriMapping(prefixA+"11", prefixE+"11a", mappingSetAE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"11a", prefixE+"11b", mappingSetEE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"11", prefixE+"11b", mappingSetBE, SYMETRIC);
        
         // E12a -> E12b - A12a   E12b - A12b
        sqlUriMapper.insertUriMapping(prefixA+"12a", prefixE+"12a", mappingSetAE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"12a", prefixE+"12b", mappingSetEE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"12b", prefixE+"12b", mappingSetAE, SYMETRIC);

        sqlUriMapper.closeInput();
    }

    @Test
    public void testDirectMappings1AtoB() throws Exception{
        Reporter.println("DirectMappings1AtoB");
        IdSysCodePair source = new IdSysCodePair("1", sysCodeA);
        Set<DirectMapping> mappings = sqlUriMapper.getDirectMappings(source);
        assertEquals(1, mappings.size());
    }

    @Test
    public void testDirectMappings1BtoAC() throws Exception{
        Reporter.println("DirectMappings1BtoAC");
        IdSysCodePair source = new IdSysCodePair( "1", sysCodeB);
        Set<DirectMapping> mappings = sqlUriMapper.getDirectMappings(source);
        assertEquals(2, mappings.size());
    }

    @Test
    public void testTransitiveMappings1A() throws Exception{
        Reporter.println("TransitiveMappings1A");
        IdSysCodePair source = new IdSysCodePair("1", sysCodeA);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(3, mappings.size());
    }

    @Test
    public void testTransitiveMappings1C() throws Exception{
        Reporter.println("TransitiveMappings1C");
        IdSysCodePair source = new IdSysCodePair("1", sysCodeC);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(3, mappings.size());
    }

    @Test
    public void testTransitiveMappings2A() throws Exception{
        Reporter.println("TransitiveMappings2A");
        IdSysCodePair source = new IdSysCodePair("2", sysCodeA);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(3, mappings.size());
    }

    @Test
    public void testTransitiveMappings2C() throws Exception{
        Reporter.println("TransitiveMappings2C");
        IdSysCodePair source = new IdSysCodePair("2", sysCodeC);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(3, mappings.size());
    }

    @Test
    public void testTransitiveMappings3A() throws Exception{
        Reporter.println("TransitiveMappings3A");
        IdSysCodePair source = new IdSysCodePair("3a", sysCodeA);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(3, mappings.size()); //B3, C3, D3
    }

    @Test
    public void testTransitiveMappings3C() throws Exception{
        Reporter.println("TransitiveMappings3C");
        IdSysCodePair source = new IdSysCodePair("3", sysCodeC);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(4, mappings.size()); //A3a, A3b, B3, C3, D3
    }

    @Test
    public void testTransitiveMappings4A() throws Exception{
        Reporter.println("TransitiveMappings4A");
        IdSysCodePair source = new IdSysCodePair("4", sysCodeA);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(3, mappings.size()); //B4, C4, D4
    }

    @Test
    public void testTransitiveMappings4C() throws Exception{
        Reporter.println("TransitiveMappings4C");
        IdSysCodePair source = new IdSysCodePair("4", sysCodeC);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(3, mappings.size()); //A4, B4, D4
    }


    @Test
    public void testTransitiveMappings6A() throws Exception{
        Reporter.println("TransitiveMappings6A");
        IdSysCodePair source = new IdSysCodePair("6a", sysCodeA);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(2, mappings.size()); //B6a, C6
    }

    @Test
    public void testTransitiveMappings6B() throws Exception{
        Reporter.println("TransitiveMappings6B");
        IdSysCodePair source = new IdSysCodePair("6a", sysCodeB);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(2, mappings.size()); //A6a, C6
    }

    @Test
    public void testTransitiveMappings6C() throws Exception{
        Reporter.println("TransitiveMappings6C");
        IdSysCodePair source = new IdSysCodePair("6", sysCodeC);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(4, mappings.size()); //A6a, A6b, B6a, B6b
    }

    @Test
    public void testTransitiveMappings7A() throws Exception{
        Reporter.println("TransitiveMappings7A");
        IdSysCodePair source = new IdSysCodePair("7a", sysCodeA);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(1, mappings.size()); //B7
    }

    @Test
    public void testTransitiveMappings7B() throws Exception{
        Reporter.println("TransitiveMappings6B");
        IdSysCodePair source = new IdSysCodePair("7", sysCodeB);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(2, mappings.size()); //A7a, A7b
    }

    @Test
    public void testTransitiveMappings8A() throws Exception{
        Reporter.println("TransitiveMappings8A");
        IdSysCodePair source = new IdSysCodePair("8", sysCodeA);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(2, mappings.size()); //8E1, 8E2
    }

    @Test
    public void testTransitiveMappings8Ea() throws Exception{
        Reporter.println("TransitiveMappings8E1");
        IdSysCodePair source = new IdSysCodePair("8a", sysCodeE);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(2, mappings.size()); //A8, E8b
    }
    
    @Test
    public void testTransitiveMappings8Eb() throws Exception{
        Reporter.println("TransitiveMappings8E1");
        IdSysCodePair source = new IdSysCodePair("8b", sysCodeE);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(2, mappings.size()); //A8, E8a
    }

    @Test
    public void testTransitiveMappings9A() throws Exception{
        Reporter.println("TransitiveMappings9A");
        IdSysCodePair source = new IdSysCodePair("9a", sysCodeA);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(3, mappings.size()); //9Ab, 9E1, 9E2
    }

    @Test
    public void testTransitiveMappings9E() throws Exception{
        Reporter.println("TransitiveMappings9E");
        IdSysCodePair source = new IdSysCodePair("9a", sysCodeE);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(3, mappings.size()); //A9a, A9b,, E8b
    }
    
    @Test
    public void testTransitiveMappings10A() throws Exception{
        Reporter.println("TransitiveMappings10A");
        IdSysCodePair source = new IdSysCodePair("10", sysCodeA);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(2, mappings.size()); //10E1, 10E2
    }

    @Test
    public void testTransitiveMappings10E() throws Exception{
        Reporter.println("TransitiveMappings10E");
        IdSysCodePair source = new IdSysCodePair("10a", sysCodeE);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(2, mappings.size()); //A10, E8b
    }

    //A11 -> E11a -> E11b - B11
    @Test
    public void testTransitiveMappings11A() throws Exception{
        Reporter.println("TransitiveMappings11A");
        IdSysCodePair source = new IdSysCodePair("11", sysCodeA);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        for (AbstractMapping mapping: mappings){
            System.out.println(mapping);
        }        
        assertEquals(3, mappings.size()); //B11 E11a, E11b
    }

    @Test
    public void testTransitiveMappings11E() throws Exception{
        Reporter.println("TransitiveMappings11E");
        IdSysCodePair source = new IdSysCodePair("11a", sysCodeE);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(3, mappings.size()); // A11, B11, E11b
    }

     // E12a -> E12b - A12a   E12b - A12b
    @Test
    public void testTransitiveMappings12A() throws Exception{
        Reporter.println("TransitiveMappings12A");
        IdSysCodePair source = new IdSysCodePair("12a", sysCodeA);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(3, mappings.size()); //A1b E12a, E12b
    }

    @Test
    public void testTransitiveMappings12E() throws Exception{
        Reporter.println("TransitiveMappings12E");
        IdSysCodePair source = new IdSysCodePair("12a", sysCodeE);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(3, mappings.size()); // A12a, A12b, E12b
    }
}
