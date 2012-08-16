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

import java.util.List;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.Xref;
import org.junit.Ignore;
import java.util.Date;
import org.junit.BeforeClass;
import org.bridgedb.IDMapperException;
import org.junit.AfterClass;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Christian
 */
public abstract class URLMapperTest extends URLListenerTest{
            
    //Used by subsets during settup to check connection is ok.
    //If set to false will skip testClose(). 
    protected static boolean connectionOk = true;
        
    static private Date start;
    
    @BeforeClass
    public static void startTime() throws IDMapperException{
        start = new Date();
    }

    @AfterClass
    public static void stopTime() throws IDMapperException{
        report("Test took " + ((new Date()).getTime() - start.getTime()));
    }
    
    @Test
    public void testMapIDManyToManyNoDataSources() throws IDMapperException{
        report("MapIDManyToManyNoDataSources");
        HashSet<String> sourceURLs = new HashSet<String>();
        sourceURLs.add(map1URL1);
        sourceURLs.add(map2URL2);
        sourceURLs.add(mapBadURL1);
        assertNotNull(map1URL1);
        assertNotNull(map2URL2);
        assertNotNull(mapBadURL1);
        Map<String, Set<String>> results = urlMapper.mapURL(sourceURLs);
        Set<String> resultSet = results.get(map1URL1);
        assertNotNull(resultSet);
        assertTrue(resultSet.contains(map1URL2));
        assertTrue(resultSet.contains(map1URL3));
        assertFalse(resultSet.contains(map2URL1));
        assertFalse(resultSet.contains(map2URL3));
        resultSet = results.get(map2URL2);
        assertNotNull(resultSet);
        assertFalse(resultSet.contains(map1URL2));
        assertFalse(resultSet.contains(map1URL3));
        assertTrue(resultSet.contains(map2URL1));
        assertTrue(resultSet.contains(map2URL3));
        resultSet = results.get(map2URL1);
        assertNull(resultSet);
        resultSet = results.get(map3URL1);
        assertNull(resultSet);
        resultSet = results.get(mapBadURL1);
        //According to Martijn and the OPS needs mappers should return the incoming URI where appropiate.
        //Still optional as I am not sure text does.
        assertTrue(resultSet == null || resultSet.size() <= 1);
    }
    
    @Test
    public void testMapIDOneToManyNoDataSources() throws IDMapperException{
        report("MapIDOneToManyNoDataSources");
        Set<String> results = urlMapper.mapURL(map1URL1);
        assertTrue(results.contains(map1URL2));
        assertTrue(results.contains(map1URL3));
        assertFalse(results.contains(map2URL1));
        assertFalse(results.contains(map2URL2));
        assertFalse(results.contains(map2URL2));
    }
    
    @Test
    public void testMapFullOneToManyNoDataSources() throws IDMapperException{
        report("MapFullOneToManyNoDataSources");
        Set<URLMapping> results = urlMapper.mapURLFull(map3URL3);
        for (URLMapping URLMapping:results){
            if (URLMapping.getTargetURLs().contains(map3URL3)){
                assertNull(URLMapping.getId());
                assertNull(URLMapping.getMappingSetId());        
                assertNull(URLMapping.getPredicate() );
            } else {
                String[] expectedMatches = {map3URL1, map3URL2};
                assertThat(URLMapping.getTargetURLs().iterator().next(), isOneOf( expectedMatches ) );
                assertEquals(TEST_PREDICATE, URLMapping.getPredicate() );
                assertNotNull(URLMapping.getId());
                assertNotNull(URLMapping.getMappingSetId());
            }
            assertTrue(URLMapping.getSourceURLs().contains(map3URL3));
        }
    }

    @Test
    public void testMapIDOneBad() throws IDMapperException{
        report("MapIDOneBad");
        Set<String> results = urlMapper.mapURL(mapBadURL1);
        //According to Martijn and the OPS needs mappers should return the incoming URI where appropiate.
        //Still optional as I am not sure text does.
        //Not all mappers will have the pattern matching to notice this is an invalid URI
        assertTrue(results.size() <= 1);
    }

    @Test
    public void testMapIDOneToManyWithOneDataSource() throws IDMapperException{
        report("MapIDOneToManyWithOneDataSource");
        Set<String> results = urlMapper.mapURL(map1URL1, URISpace2);
        assertTrue(results.contains(map1URL2));
        assertFalse(results.contains(map1URL3));
        assertFalse(results.contains(map2URL1));
        assertFalse(results.contains(map2URL2));
        assertFalse(results.contains(map2URL2));
    }
 
    @Test
    public void testMapToSelfWithOneDataSource() throws IDMapperException{
        report("MapToSelfWithOneDataSource");
        Set<String> results = urlMapper.mapURL(map1URL2, URISpace2);
        assertTrue(results.contains(map1URL2));
        assertFalse(results.contains(map1URL3));
        assertFalse(results.contains(map2URL1));
        assertFalse(results.contains(map2URL2));
        assertFalse(results.contains(map2URL2));
    }

    @Test
    public void testMapIDOneToManyWithTwoDataSources() throws IDMapperException{
        report("MapIDOneToManyWithTwoDataSources");
        Set<String> results = urlMapper.mapURL(map1URL1, URISpace2, URISpace3);
        assertTrue(results.contains(map1URL2));
        assertTrue(results.contains(map1URL3));
        assertFalse(results.contains(map2URL1));
        assertFalse(results.contains(map2URL2));
        assertFalse(results.contains(map2URL2));
    }
 
    @Test
    public void testMapIDOneToManyNoDataSources2() throws IDMapperException{
        report("MapIDOneToManyNoDataSources");
        Set<String> results = urlMapper.mapURL(map2URL1);
        assertTrue(results.contains(map2URL2));
        assertTrue(results.contains(map2URL3));
        assertFalse(results.contains(map1URL2));
        assertFalse(results.contains(map3URL2));
     }

    @Test
    public void testMapNoneExistingDataSource() throws IDMapperException{
        report("MapNoneExistingDataSource");
        Set<String> results = urlMapper.mapURL(map1URL2, "http://wwww.THIS.should.NOT.Be.InThe.Data.zzz");
        System.out.println(results);
        assertEquals(0,results.size());
    }

    @Test
    public void testURLSupported() throws Exception {
        report("URLSupported");
        assertTrue(urlMapper.uriExists(map1URL1));
        assertTrue(urlMapper.uriExists(map1URL2));
        assertTrue(urlMapper.uriExists(map1URL3));
        assertTrue(urlMapper.uriExists(map2URL1));
        assertTrue(urlMapper.uriExists(map2URL2));
        assertTrue(urlMapper.uriExists(map2URL3));
        assertTrue(urlMapper.uriExists(map3URL1));
        assertTrue(urlMapper.uriExists(map3URL2));
        assertTrue(urlMapper.uriExists(map3URL3));
        assertFalse(urlMapper.uriExists(mapBadURL1));
        assertFalse(urlMapper.uriExists(mapBadURL2));
        assertFalse(urlMapper.uriExists(mapBadURL3));
    }
        
    @Test
    public void testFreeSearchBad() throws IDMapperException{
        org.junit.Assume.assumeTrue(urlMapper.getCapabilities().isFreeSearchSupported());       
        org.junit.Assume.assumeTrue(badID != null);
        report("FreeSearchBad");
        Set<String> results = urlMapper.urlSearch(badID, 10);
        assertTrue (results == null || results.isEmpty());
    }
    
    @Test
    public void testFreeSearchGood() throws IDMapperException{
        org.junit.Assume.assumeTrue(urlMapper.getCapabilities().isFreeSearchSupported());       
        report("FreeSearchGood");
        Set<String> results = urlMapper.urlSearch(goodId1, 10);
        //Skip these if there are 10 or more possible ones. No Gurantee whiuch come back
        if (results.size() < 10){
            assertTrue (results.contains(map1URL1));
            assertTrue (results.contains(map1URL1));
            assertTrue (results.contains(map1URL3));
        }
        assertFalse (results.contains(map2URL1));
    }
    
    @Test
    public void testFreeSearchGoodJust2() throws IDMapperException{
        org.junit.Assume.assumeTrue(urlMapper.getCapabilities().isFreeSearchSupported());       
        report("FreeSearchGoodJust2");
        Set<String> results = urlMapper.urlSearch(goodId1, 2);
        assertEquals (2, results.size());
     }

    @Test
    public void testGetXrefGood() throws IDMapperException {
        report("GetXrefGood");
        Xref result = urlMapper.toXref(map2URL2);
        assertEquals(map2xref2, result);
    }

    @Test
    (expected=BridgeDbSqlException.class)
    public void testGetXrefBad() throws IDMapperException {
        report("GetXrefBad");
        Xref result = urlMapper.toXref(mapBadURL1);
    }
    
    
    @Test
    public void testGetMapping() throws IDMapperException {
        report("GetMapping");
        Set<URLMapping> results = urlMapper.mapURLFull(map3URL3);
        Integer mappingId = null;
        Integer setId = null;
        for (URLMapping URLMapping:results){
            if (URLMapping.getTargetURLs().contains(map3URL2)){
                mappingId = URLMapping.getId();
                setId = URLMapping.getMappingSetId();        
            }
        }
        URLMapping result = urlMapper.getMapping(mappingId);
        assertEquals(mappingId, result.getId());
        assertTrue(result.getSourceURLs().contains(map3URL3));
        assertEquals(TEST_PREDICATE, result.getPredicate());
        assertTrue(result.getTargetURLs().contains(map3URL2));
        assertEquals(setId, result.getMappingSetId());
    }
    
    @Test
    public void testGetSampleSourceURL() throws IDMapperException {
        report("GetSampleSourceURL");
        Set<String> results = urlMapper.getSampleSourceURLs();
        assertEquals(5, results.size());
        for (String url:results){
            assertTrue(urlMapper.uriExists(url));
        }
    }

    @Test
    public void testGetOverallStatistics() throws IDMapperException {
        report("GetOverallStatistics()");
        OverallStatistics results = urlMapper.getOverallStatistics();
        assertThat (results.getNumberOfMappings(), greaterThanOrEqualTo(18));
        assertThat (results.getNumberOfMappingSets(), greaterThanOrEqualTo(6));
        assertThat (results.getNumberOfSourceDataSources(), greaterThanOrEqualTo(3));
        assertThat (results.getNumberOfTargetDataSources(), greaterThanOrEqualTo(3));
        assertThat (results.getNumberOfPredicates(), greaterThanOrEqualTo(1));
    }

    @Test
    public void testGetMappingSetInfos() throws IDMapperException {
        report("GetUriSpaces");
        List<MappingSetInfo> results = urlMapper.getMappingSetInfos();
        assertThat (results.size(), greaterThanOrEqualTo(6));
    }

    @Test
    public void testGetUriSpaces() throws IDMapperException {
        report("GetUriSpaces");
        Set<String> results = urlMapper.getUriSpaces(map2xref3.getDataSource().getSystemCode());
        assertTrue (results.contains(URISpace3));
    }
}
