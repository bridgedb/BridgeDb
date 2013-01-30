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
package org.bridgedb.url;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.rdf.RdfConfig;
import org.bridgedb.sql.SQLListener;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.tools.metadata.constants.OwlConstants;
import org.bridgedb.tools.metadata.constants.SkosConstants;
import org.bridgedb.utils.BridgeDBException;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Tests the URLMapper interface (and by loading the URLListener interface)
 *
 * Should be passable by any implementation of URLMapper that has the test data loaded.
 * 
 * @author Christian
 */
public abstract class URLMapperTest extends URLListenerTest{
            
    //Used by subsets during settup to check connection is ok.
    //If set to false will skip testClose(). 
    protected static boolean connectionOk = true;
        
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
        Map<String, Set<String>> results = urlMapper.mapURL(sourceURLs, RdfConfig.getProfileURI(0));
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
    public void testMaptoURLsManyToManyNoDataSources() throws IDMapperException{
        report("MapToURLsManyToManyNoDataSources");
        HashSet<Xref> sourceXrefs = new HashSet<Xref>();
        sourceXrefs.add(map1xref1);
        sourceXrefs.add(map2xref2);
        sourceXrefs.add(mapBadxref2);
        assertNotNull(map1URL1);
        assertNotNull(map2URL2);
        assertNotNull(mapBadURL1);
        Map<Xref, Set<String>> results = urlMapper.mapToURLs(sourceXrefs, RdfConfig.getProfileURI(0));
        Set<String> resultSet = results.get(map1xref1);
        assertNotNull(resultSet);
        assertTrue(resultSet.contains(map1URL2));
        assertTrue(resultSet.contains(map1URL3));
        assertFalse(resultSet.contains(map2URL1));
        assertFalse(resultSet.contains(map2URL3));
        resultSet = results.get(map2xref2);
        assertNotNull(resultSet);
        assertFalse(resultSet.contains(map1URL2));
        assertFalse(resultSet.contains(map1URL3));
        assertTrue(resultSet.contains(map2URL1));
        assertTrue(resultSet.contains(map2URL3));
        resultSet = results.get(map2xref1);
        assertNull(resultSet);
        resultSet = results.get(map3xref1);
        assertNull(resultSet);
        resultSet = results.get(mapBadxref3);
        //According to Martijn and the OPS needs mappers should return the incoming URI where appropiate.
        //Still optional as I am not sure text does.
        assertTrue(resultSet == null || resultSet.size() <= 1);
    }
    
    @Test
    public void testMapIDOneToManyNoDataSources() throws IDMapperException{
        report("MapIDOneToManyNoDataSources");
        Set<String> results = urlMapper.mapURL(map1URL1, RdfConfig.getProfileURI(0));
        assertTrue(results.contains(map1URL2));
        assertTrue(results.contains(map1URL3));
        assertFalse(results.contains(map2URL1));
        assertFalse(results.contains(map2URL2));
        assertFalse(results.contains(map2URL2));
    }
    
    @Test
    public void testToURLsOneToManyNoDataSources() throws IDMapperException{
        report("MapXrefOneToManyNoDataSources");
        Set<String> results = urlMapper.mapToURLs(map1xref1, RdfConfig.getProfileURI(0));
        assertTrue(results.contains(map1URL2));
        assertTrue(results.contains(map1URL3));
        assertFalse(results.contains(map2URL1));
        assertFalse(results.contains(map2URL2));
        assertFalse(results.contains(map2URL2));
    }

    @Test
    public void testMapFullOneToManyNoDataSources() throws IDMapperException{
        report("MapFullOneToManyNoDataSources");
        Set<Mapping> results = urlMapper.mapURLFull(map3URL3, RdfConfig.getProfileURI(0));
        Set<String> mappedTo = new HashSet<String>();
        for (Mapping URLMapping:results){
            if (URLMapping.getTargetURL().contains(map3URL3)){
                assertNull(URLMapping.getId());
                assertNull(URLMapping.getMappingSetId());        
                assertNull(URLMapping.getPredicate() );
            } else {
                mappedTo.addAll(URLMapping.getTargetURL());
                String[] predicates = {TEST_PREDICATE, SkosConstants.EXACT_MATCH.stringValue(), 
                    OwlConstants.EQUIVALENT_CLASS.stringValue()};
                assertThat(URLMapping.getPredicate(), isIn(predicates));
                assertNotNull(URLMapping.getId());
                assertNotNull(URLMapping.getMappingSetId());
            }
            assertTrue(URLMapping.getSourceURL().contains(map3URL3));
        }
        String[] expectedMatches = {map3URL1, map3URL2, map3URL2a};
        assertThat(mappedTo, hasItems(expectedMatches));
        assertThat(mappedTo, not(hasItems(map1URL1)));
        assertThat(mappedTo, not(hasItems(map2URL2)));
    }

    @Test
    public void testMapXrefFullOneToManyNoDataSources() throws IDMapperException{
        report("MapXrefFullOneToManyNoDataSources");
        Set<Mapping> results = urlMapper.mapToURLsFull(map3xref3, RdfConfig.getProfileURI(0));
        Set<String> mappedTo = new HashSet<String>();
        for (Mapping URLMapping:results){
            if (URLMapping.getTargetURL().contains(map3URL3)){
                assertNull(URLMapping.getId());
                assertNull(URLMapping.getMappingSetId());        
                assertNull(URLMapping.getPredicate() );
            } else {
                mappedTo.addAll(URLMapping.getTargetURL());
                String[] predicates = {TEST_PREDICATE, SkosConstants.EXACT_MATCH.stringValue(), 
                    OwlConstants.EQUIVALENT_CLASS.stringValue()};
                assertThat(URLMapping.getPredicate(), isIn(predicates));
                assertNotNull(URLMapping.getId());
                assertNotNull(URLMapping.getMappingSetId());
            }
            assertEquals(map3xref3, URLMapping.getSource());
        }
        String[] expectedMatches = {map3URL1, map3URL2, map3URL2a};
        assertThat(mappedTo, hasItems(expectedMatches));
        assertThat(mappedTo, not(hasItems(map1URL1)));
        assertThat(mappedTo, not(hasItems(map2URL2)));
    }

    @Test
    public void testMapIDOneBad() throws IDMapperException{
        report("MapIDOneBad");
        Set<String> results = urlMapper.mapURL(mapBadURL1, RdfConfig.getProfileURI(0));
        //According to Martijn and the OPS needs mappers should return the incoming URI where appropiate.
        //Still optional as I am not sure text does.
        //Not all mappers will have the pattern matching to notice this is an invalid URI
        assertTrue(results.size() <= 1);
    }

    @Test
    public void testMapFullOneBad() throws IDMapperException{
        report("MapFullOneBad");
        Set<Mapping> results = urlMapper.mapURLFull(mapBadURL1, RdfConfig.getProfileURI(0));
        assertTrue(results.size() <= 1);
    }

    @Test
    public void testMapFullOneBadOneNameSpace() throws IDMapperException{
        report("MapFullOneBadOneNameSpace");
        Set<Mapping> results = urlMapper.mapURLFull(mapBadURL1, RdfConfig.getProfileURI(0), URISpace2);
        assertTrue(results.size() <= 1);
    }

    @Test
    public void testMapIDOneToManyWithOneDataSource() throws IDMapperException{
        report("MapIDOneToManyWithOneDataSource");
        Set<String> results = urlMapper.mapURL(map1URL1, RdfConfig.getProfileURI(0), URISpace2);
        assertTrue(results.contains(map1URL2));
        assertFalse(results.contains(map1URL3));
        assertFalse(results.contains(map2URL1));
        assertFalse(results.contains(map2URL2));
        assertFalse(results.contains(map2URL2));
    }
 
    @Test
    public void testMapToURLsOneToManyWithOneDataSource() throws IDMapperException{
        report("MapIDOneToManyWithOneDataSource");
        Set<String> results = urlMapper.mapToURLs(map1xref1, RdfConfig.getProfileURI(0), URISpace2);
        assertTrue(results.contains(map1URL2));
        assertFalse(results.contains(map1URL3));
        assertFalse(results.contains(map2URL1));
        assertFalse(results.contains(map2URL2));
        assertFalse(results.contains(map2URL2));
    }
 
    @Test
    public void testMapToSelfWithOneDataSource() throws IDMapperException{
        report("MapToSelfWithOneDataSource");
        Set<String> results = urlMapper.mapURL(map1URL2, RdfConfig.getProfileURI(0), URISpace2);
        assertTrue(results.contains(map1URL2));
        assertFalse(results.contains(map1URL3));
        assertFalse(results.contains(map2URL1));
        assertFalse(results.contains(map2URL2));
        assertFalse(results.contains(map2URL2));
    }

    @Test
    public void testMapIDOneToManyWithTwoDataSources() throws IDMapperException{
        report("MapIDOneToManyWithTwoDataSources");
        Set<String> results = urlMapper.mapURL(map1URL1, RdfConfig.getProfileURI(0), URISpace2, URISpace3);
        assertTrue(results.contains(map1URL2));
        assertTrue(results.contains(map1URL3));
        assertFalse(results.contains(map2URL1));
        assertFalse(results.contains(map2URL2));
        assertFalse(results.contains(map2URL2));
    }
 
    @Test
    public void testMapIDOneToManyNoDataSources2() throws IDMapperException{
        report("MapIDOneToManyNoDataSources");
        Set<String> results = urlMapper.mapURL(map2URL1, RdfConfig.getProfileURI(0));
        assertTrue(results.contains(map2URL2));
        assertTrue(results.contains(map2URL3));
        assertFalse(results.contains(map1URL2));
        assertFalse(results.contains(map3URL2));
     }

    @Test
    public void testMapNoneExistingDataSource() throws IDMapperException{
        report("MapNoneExistingDataSource");
        Set<String> results = urlMapper.mapURL(map1URL2, RdfConfig.getProfileURI(0), "http://wwww.THIS.should.NOT.Be.InThe.Data.zzz");
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
        Set<String> results = urlMapper.urlSearch(ds2Id3, 10);
        //Skip these if there are 10 or more possible ones. No Gurantee whiuch come back
        System.out.println(ds2Id3);
        System.out.println(results);
        if (results.size() < 10){
            assertTrue (results.contains(map3URL2));
            assertTrue (results.contains(map3URL2a));
        }
        assertFalse (results.contains(map3URL1));
        assertFalse (results.contains(map1URL2));
    }
    
    @Test
    public void testFreeSearchGoodJust2() throws IDMapperException{
        org.junit.Assume.assumeTrue(urlMapper.getCapabilities().isFreeSearchSupported());       
        report("FreeSearchGoodJust2");
        Set<String> results = urlMapper.urlSearch(ds2Id2, 2);
        assertEquals (2, results.size());
     }

    @Test
    public void testGetXrefGood() throws IDMapperException {
        report("GetXrefGood");
        Xref result = urlMapper.toXref(map2URL2);
        assertEquals(map2xref2, result);
    }

    @Test (expected = BridgeDBException.class)
    public void testGetXrefBad() throws IDMapperException {
        report("GetXrefBad");
        Xref xref = urlMapper.toXref(mapBadURL1);
    }
    
    @Test
    public void testGetMapping() throws IDMapperException {
        report("GetMapping");
        Set<Mapping> results = urlMapper.mapURLFull(map3URL3, RdfConfig.getProfileURI(0));
        Integer mappingId = null;
        Integer setId = null;
        for (Mapping URLMapping:results){
            if (URLMapping.getTargetURL().contains(map3URL2)){
                mappingId = URLMapping.getId();
                setId = URLMapping.getMappingSetId();        
            }
        }
        Mapping result = urlMapper.getMapping(mappingId);
        assertEquals(mappingId, result.getId());
        assertTrue(result.getSourceURL().contains(map3URL3));
        assertEquals(TEST_PREDICATE, result.getPredicate());
        assertTrue(result.getTargetURL().contains(map3URL2));
        assertEquals(setId, result.getMappingSetId());
        assertEquals(map3xref3.getId(), result.getSourceId());
        assertEquals(map3xref3.getDataSource().getSystemCode(), result.getSourceSysCode());
        assertEquals(map3xref2.getId(), result.getTargetId());
        assertEquals(map3xref2.getDataSource().getSystemCode(), result.getTargetSysCode());
    }
    
    @Test
    public void testGetSampleMappings() throws IDMapperException {
        report("GetSampleSourceURL");
        List<Mapping> results = urlMapper.getSampleMapping();
        assertEquals(5, results.size());
        for (Mapping mapping:results){
            Set<String> sources = mapping.getSourceURL();
            assertThat(sources.size(), greaterThan(0));
            Set<String> targets = mapping.getTargetURL();
            assertThat(targets.size(), greaterThan(0));
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
    public void testGetMappingSetInfo() throws IDMapperException {
        report("GetMappingSetInfo");
        MappingSetInfo result = urlMapper.getMappingSetInfo(mappingSet2_3);
        assertEquals(DataSource2.getSystemCode(), result.getSourceSysCode());
        assertEquals(DataSource3.getSystemCode(), result.getTargetSysCode());
    }

    @Test
    public void testGetMappingSetInfos() throws IDMapperException {
        report("GetMappingSetInfo All");
        List<MappingSetInfo> results = urlMapper.getMappingSetInfos(null, null);
        assertThat (results.size(), greaterThanOrEqualTo(6));
    }

    @Test
    public void testGetMappingSetInfosBySourceAndTarget() throws IDMapperException {
        report("GetMappingSetInfos source and target");
        List<MappingSetInfo> results = 
                urlMapper.getMappingSetInfos(DataSource2.getSystemCode(), DataSource1.getSystemCode());
        assertThat (results.size(), greaterThanOrEqualTo(1));
        for (MappingSetInfo info:results){
            assertEquals(DataSource2.getSystemCode(), info.getSourceSysCode());
            assertEquals(DataSource1.getSystemCode(), info.getTargetSysCode());
        }
    }

    @Test
    public void testGetMappingSetInfosByTarget() throws IDMapperException {
        report("GetMappingSetInfos target");
        List<MappingSetInfo> results = 
                urlMapper.getMappingSetInfos(null, DataSource3.getSystemCode());
        assertThat (results.size(), greaterThanOrEqualTo(2));
        for (MappingSetInfo info:results){
            assertEquals(DataSource3.getSystemCode(), info.getTargetSysCode());
        }
    }
    @Test
    public void testGetMappingSetInfosBySource() throws IDMapperException {
        report("GetMappingSetInfos source");
        List<MappingSetInfo> results = 
                urlMapper.getMappingSetInfos(DataSource1.getSystemCode(), null);
        assertThat (results.size(), greaterThanOrEqualTo(2));
        for (MappingSetInfo info:results){
            assertEquals(DataSource1.getSystemCode(), info.getSourceSysCode());
        }
    }
    
    @Test
    public void testGetUriSpaces() throws IDMapperException {
        report("GetUriSpaces");
        Set<String> results = urlMapper.getUriSpaces(map2xref3.getDataSource().getSystemCode());
        assertTrue (results.contains(URISpace3));
    }
    
    @Test
    public void testGetSourceUriSpace() throws IDMapperException {
        report("GetSourceUriSpace");
        Set<String> results = urlMapper.getSourceUriSpace(mappingSet2_3);
        assertFalse (results.contains(URISpace1));
        assertTrue (results.contains(URISpace2));
        assertTrue (results.contains(URISpace2a));
        assertFalse (results.contains(URISpace3));
        assertFalse (results.contains(URISpace3a));
    }

    @Test
    public void testGetTargetUriSpace() throws IDMapperException {
        report("GetTargetUriSpace");
        MappingSetInfo result = urlMapper.getMappingSetInfo(mappingSet2_3);
        Set<String> results = urlMapper.getTargetUriSpace(mappingSet2_3);
        assertFalse (results.contains(URISpace1));
        assertFalse (results.contains(URISpace2));
        assertFalse (results.contains(URISpace2a));
        assertTrue (results.contains(URISpace3));
        assertTrue (results.contains(URISpace3a));
    }
    
    @Test
    public void testGetSqlCompatVersion() throws IDMapperException {
        report("GetSqlCompatVersion");
        int result = urlMapper.getSqlCompatVersion();
        assertEquals(SQLListener.SQL_COMPAT_VERSION, result);
    }
}
