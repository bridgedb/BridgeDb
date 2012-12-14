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
package org.bridgedb.mysql;

import java.util.Date;
import java.util.Set;
import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.url.URLMapping;
import org.bridgedb.utils.StoreType;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;


/**
 * Runs the URLMapper interface tests over SQLURLMapper class
 * 
 * Creates the mapper, loads in the test data and then runs the tests.
 *
 * @author Christian
 */
//@Ignore
public class SpeedTest extends org.bridgedb.url.URLMapperTest {
    
    private static final String CREATOR1 = "testCreator";
    private static final String PREDICATE1 = "testMapping";
    private static final long CREATION1 = new Date().getTime();
    private static SQLUrlMapper SQLUrlMapper;

    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{

        connectionOk = false;
        TestSqlFactory.checkSQLAccess();
        connectionOk = true;
        listener = new SQLUrlMapper(true, StoreType.TEST);
        loadData();
        SQLUrlMapper =new SQLUrlMapper(false, StoreType.TEST);
        urlMapper = SQLUrlMapper;
    }
  
    @Test
    public void testMapFullOneToManyNoDataSources() throws IDMapperException{
        report("MapFullOneToManyNoDataSources");
        Date start = new Date();
        for (int i = 1; i < 100; i++){
            urlMapper.mapURLFull(map3URL3);
        }
        Set<URLMapping> results = urlMapper.mapURLFull(map3URL3);
        System.out.println("That took " + ((new Date().getTime())- start.getTime()));
        for (URLMapping URLMapping:results){
            if (URLMapping.getTargetURLs().contains(map3URL3)){
                assertNull(URLMapping.getId());
                assertNull(URLMapping.getMappingSetId());        
                assertNull(URLMapping.getPredicate() );
            } else {
                String[] expectedMatches = {map3URL1, map3URL2, map3URL2a};
                assertThat(URLMapping.getTargetURLs().iterator().next(), isOneOf( expectedMatches ) );
                assertEquals(TEST_PREDICATE, URLMapping.getPredicate() );
                assertNotNull(URLMapping.getId());
                assertNotNull(URLMapping.getMappingSetId());
            }
            assertTrue(URLMapping.getSourceURLs().contains(map3URL3));
        }
    }

   @Test
    public void testMapFullOneToManyNoDataSources2() throws IDMapperException{
        report("MapFullOneToManyNoDataSources");
        Date start = new Date();
        for (int i = 1; i < 100; i++){
            SQLUrlMapper.mapURLFull2(map3URL3);
        }
        Set<URLMapping> results = SQLUrlMapper.mapURLFull2(map3URL3);
        System.out.println("That took " + ((new Date().getTime())- start.getTime()));
        for (URLMapping URLMapping:results){
            if (URLMapping.getTargetURLs().contains(map3URL3)){
                assertNull(URLMapping.getId());
                assertNull(URLMapping.getMappingSetId());        
                assertNull(URLMapping.getPredicate() );
            } else {
                String[] expectedMatches = {map3URL1, map3URL2, map3URL2a};
                assertThat(URLMapping.getTargetURLs().iterator().next(), isOneOf( expectedMatches ) );
                assertEquals(TEST_PREDICATE, URLMapping.getPredicate() );
                assertNotNull(URLMapping.getId());
                assertNotNull(URLMapping.getMappingSetId());
            }
            assertTrue(URLMapping.getSourceURLs().contains(map3URL3));
        }
    }
    @Test
    public void testMapFullOneToOneDataSources() throws IDMapperException{
        report("MapFullOneToOneDataSources");
        Date start = new Date();
        for (int i = 1; i < 100; i++){
            urlMapper.mapURLFull(map3URL3, URISpace2);
        }
        Set<URLMapping> results = urlMapper.mapURLFull(map3URL3, URISpace2);
        System.out.println("That took " + ((new Date().getTime())- start.getTime()));
        for (URLMapping URLMapping:results){            
            if (URLMapping.getTargetURLs().contains(map3URL3)){
                assertNull(URLMapping.getId());
                assertNull(URLMapping.getMappingSetId());        
                assertNull(URLMapping.getPredicate() );
            } else {
                String[] expectedMatches = {map3URL2, map3URL2a};
                assertThat(URLMapping.getTargetURLs().iterator().next(), isOneOf( expectedMatches ) );
                assertEquals(TEST_PREDICATE, URLMapping.getPredicate() );
                assertNotNull(URLMapping.getId());
                assertNotNull(URLMapping.getMappingSetId());
            }
            assertTrue(URLMapping.getSourceURLs().contains(map3URL3));
        }
    }

   @Test
    public void testMapFullOneToOneDataSources2() throws IDMapperException{
        report("MapFullOneToOneDataSources");
        Date start = new Date();
        for (int i = 1; i < 100; i++){
            SQLUrlMapper.mapURLFull2(map3URL3, URISpace2);
        }
        Set<URLMapping> results = SQLUrlMapper.mapURLFull2(map3URL3, URISpace2);
        System.out.println("That took " + ((new Date().getTime())- start.getTime()));
        for (URLMapping URLMapping:results){
            if (URLMapping.getTargetURLs().contains(map3URL3)){
                assertNull(URLMapping.getId());
                assertNull(URLMapping.getMappingSetId());        
                assertNull(URLMapping.getPredicate() );
            } else {
                String[] expectedMatches = {map3URL2, map3URL2a};
                assertThat(URLMapping.getTargetURLs().iterator().next(), isOneOf( expectedMatches ) );
                assertEquals(TEST_PREDICATE, URLMapping.getPredicate() );
                assertNotNull(URLMapping.getId());
                assertNotNull(URLMapping.getMappingSetId());
            }
            assertTrue(URLMapping.getSourceURLs().contains(map3URL3));
        }
    }
}
