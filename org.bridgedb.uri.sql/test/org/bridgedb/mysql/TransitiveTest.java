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
            
    private static final DataSource dsA = DataSource.register(sysCodeA, sysCodeA).asDataSource();
    private static final DataSource dsB = DataSource.register(sysCodeB, sysCodeB).asDataSource();
    private static final DataSource dsC = DataSource.register(sysCodeC, sysCodeC).asDataSource();
    private static final DataSource dsD = DataSource.register(sysCodeD, sysCodeD).asDataSource();
    private static final DataSource dsE = DataSource.register(sysCodeE, sysCodeE).asDataSource();

    private static RegexUriPattern regexUriPatternA;
    private static RegexUriPattern regexUriPatternB;
    private static RegexUriPattern regexUriPatternC;
    private static RegexUriPattern regexUriPatternD;
    private static RegexUriPattern regexUriPatternE;

    private static final String prefixA = "http://example.com/dsA#";
    private static final String prefixB = "http://example.com/dsB#";
    private static final String prefixC = "http://example.com/dsC#";
    private static final String prefixD = "http://example.com/dsD#";
    private static final String prefixE = "http://example.com/dsE#";
    
    private static int mappingSetAB;
    private static int mappingSetAC;
    private static int mappingSetAD;
    private static int mappingSetAE;
    private static int mappingSetBC;
    private static int mappingSetBD;
    private static int mappingSetBE;
    private static int mappingSetCD;
    private static int mappingSetCE;
    private static int mappingSetDE;
    
    
    @BeforeClass
    public static void setupIDMapper() throws BridgeDBException{
        TestSqlFactory.checkSQLAccess();
        ConfigReader.useTest();
        UriPattern pattern = UriPattern.register(prefixA + "$id", sysCodeA, UriPatternType.dataSourceUriPattern);
        pattern = UriPattern.register(prefixB + "$id", sysCodeB, UriPatternType.dataSourceUriPattern);
        pattern = UriPattern.register(prefixC + "$id", sysCodeC, UriPatternType.dataSourceUriPattern);
        pattern = UriPattern.register(prefixD + "$id", sysCodeD, UriPatternType.dataSourceUriPattern);
        pattern = UriPattern.register(prefixE + "$id", sysCodeE, UriPatternType.dataSourceUriPattern);
        
        sqlUriMapper = SQLUriMapper.createNew();

        regexUriPatternA = RegexUriPattern.factory(prefixA, "", sysCodeA);
        regexUriPatternB = RegexUriPattern.factory(prefixB, "", sysCodeB);
        regexUriPatternC = RegexUriPattern.factory(prefixC, "", sysCodeC);
        regexUriPatternD = RegexUriPattern.factory(prefixD, "", sysCodeD);
        regexUriPatternE = RegexUriPattern.factory(prefixE, "", sysCodeE);
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
        
        resource = new URIImpl("http://example.com/TransitiveTest/BtoC");
        mappingSetBC = sqlUriMapper.registerMappingSet(regexUriPatternB, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternC, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);
        
        resource = new URIImpl("http://example.com/TransitiveTest/BtoD");
        mappingSetBD = sqlUriMapper.registerMappingSet(regexUriPatternB, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternD, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);

        resource = new URIImpl("http://example.com/TransitiveTest/BtoE");
        mappingSetBE = sqlUriMapper.registerMappingSet(regexUriPatternB, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternE, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);
        
        resource = new URIImpl("http://example.com/TransitiveTest/CtoD");
        mappingSetCD = sqlUriMapper.registerMappingSet(regexUriPatternC, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternD, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);

        resource = new URIImpl("http://example.com/TransitiveTest/CtoE");
        mappingSetCE = sqlUriMapper.registerMappingSet(regexUriPatternC, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternE, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);

        resource = new URIImpl("http://example.com/TransitiveTest/DtoE");
        mappingSetDE = sqlUriMapper.registerMappingSet(regexUriPatternD, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPatternE, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);

        //A1 -> B1 -> C1 -> D1
        sqlUriMapper.insertUriMapping(prefixA+"1", prefixB+"1", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"1", prefixC+"1", mappingSetBC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixC+"1", prefixD+"1", mappingSetCD, SYMETRIC);

        //A2 -> B2 -> C2 -> D2 -> A2
        sqlUriMapper.insertUriMapping(prefixA+"2", prefixB+"2", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"2", prefixC+"2", mappingSetBC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixC+"2", prefixD+"2", mappingSetCD, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"2", prefixD+"2", mappingSetAD, SYMETRIC);

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
        for (AbstractMapping mapping:mappings){
            System.out.println(mapping);
        }
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
        IdSysCodePair source = new IdSysCodePair("1", sysCodeA);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(3, mappings.size());
    }

    @Test
    public void testTransitiveMappings2C() throws Exception{
        Reporter.println("TransitiveMappings2C");
        IdSysCodePair source = new IdSysCodePair("1", sysCodeC);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        assertEquals(3, mappings.size());
    }

}
