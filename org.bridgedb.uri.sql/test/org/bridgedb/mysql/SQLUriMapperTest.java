/* BridgeDb,
 * An abstraction layer for identifier mapping services, both local and online.
 *
 * Copyright 2006-2009  BridgeDb developers
 * Copyright 2012-2013  Christian Y. A. Brenninkmeijer
 * Copyright 2012-2013  OpenPhacts
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bridgedb.mysql;

import org.bridgedb.pairs.IdSysCodePair;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.uri.UriListenerTest;
import org.bridgedb.uri.tools.RegexUriPattern;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests the UriMapper interface (and by loading the UriListener interface)
 *
 * Should be passable by any implementation of UriMapper that has the test data loaded.
 * 
 * @author Christian
 */
public class SQLUriMapperTest extends UriListenerTest{
   
    private static SQLUriMapper sqlUriMapper;
    
    @BeforeAll
    @Tag("mysql")
    public static void setupIDMapper() throws BridgeDBException{
        TestSqlFactory.checkSQLAccess();
        ConfigReader.useTest();
        sqlUriMapper = SQLUriMapper.createNew();
    }
    
    @Test
    @Tag("mysql")
    public void testToUriPattern() throws BridgeDBException{
        report("ToUriPattern");
        IdSysCodePair result = sqlUriMapper.toIdSysCodePair(map1Uri1);
        IdSysCodePair expected = new IdSysCodePair (ds1Id1, dataSource1Code);
        assertEquals(expected, result);
    }

    @Test
    @Tag("mysql")
    public void testToUriPatternUsingLike() throws BridgeDBException{
        report("ToUriPatternUsingLike");
        IdSysCodePair result = sqlUriMapper.toIdSysCodePair("http://bio2rdf.org/chebi:1234");
        IdSysCodePair expected = new IdSysCodePair ("1234", "Ce"); //will be "Ce"
        assertEquals(expected, result);
    }
      
    @Test
    @Tag("mysql")
    public void testBugInsdc() throws Exception {
        report("LoadBugInsdc");
        RegexUriPattern pattern = sqlUriMapper.toUriPattern("http://identifiers.org/ena.embl/AAG52984");
        assertNotNull(pattern);
    }

    @Test
    @Tag("mysql")
    public void testBugDrugbank() throws Exception {
        report("BugDrugbank");
        RegexUriPattern pattern = sqlUriMapper.toUriPattern("http://drugbank.ca/drugs/DB01269");
        assertNotNull(pattern);
    }
}
