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
import org.bridgedb.pairs.IdSysCodePair;
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
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
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
public class TransitiveTest extends UriListenerTest{
   
    private static SQLUriMapper sqlUriMapper;
    
    @BeforeClass
    public static void setupIDMapper() throws BridgeDBException{
        TestSqlFactory.checkSQLAccess();
        ConfigReader.useTest();
        listener = SQLUriMapper.createNew();
        loadData();
        sqlUriMapper = SQLUriMapper.getExisting();
    }
    
    public static void loadData() throws BridgeDBException{

        Resource resource = new URIImpl("http://example.com/1to2");
        int mappingSet = listener.registerMappingSet(regexUriPattern1, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPattern2, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);
        listener.insertUriMapping(map1Uri1, map1Uri2, mappingSet, SYMETRIC);
        listener.insertUriMapping(map2Uri1, map2Uri2, mappingSet, SYMETRIC);
        listener.insertUriMapping(map3Uri1, map3Uri2, mappingSet, SYMETRIC);
        
        resource = new URIImpl("http://example.com/2to3");
        mappingSet = listener.registerMappingSet(regexUriPattern2, TEST_PREDICATE, 
                Lens.getDefaultJustifictaionString(), regexUriPattern3, resource, resource, SYMETRIC, NO_VIA, NO_CHAIN);
        assertEquals(mappingSet2_3, mappingSet);
        listener.insertUriMapping(map1Uri2, map1Uri3, mappingSet2_3, SYMETRIC);
        listener.insertUriMapping(map2Uri2, map2Uri3, mappingSet2_3, SYMETRIC);
        listener.insertUriMapping(map3Uri2, map3Uri3, mappingSet2_3, SYMETRIC);
        listener.closeInput();
    }

    @Test
    public void testDirectMappings() throws Exception{
        report("DirectMappings");
        IdSysCodePair source = new IdSysCodePair(ds1Id1, dataSource1Code);
        Set<DirectMapping> mappings = sqlUriMapper.getDirectMappings(source);
        assertThat(mappings.size(), greaterThanOrEqualTo(1));
    }

    @Test
    public void testTransitiveMappings() throws Exception{
        report("TransitiveMappings");
        IdSysCodePair source = new IdSysCodePair(ds1Id1, dataSource1Code);
        Set<AbstractMapping> mappings = sqlUriMapper.getTransitiveMappings(source);
        System.out.println(mappings);
        assertThat(mappings.size(), greaterThanOrEqualTo(1));
    }

}
