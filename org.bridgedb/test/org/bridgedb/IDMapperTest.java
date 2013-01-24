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
package org.bridgedb;

import org.junit.AfterClass;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests to be run on any class that implements IDMapper interface.
 * Also tests the IDMapperCapabilities.
 *
 * Implementing tests have to set idMapper and capabilities
 *
 * Should be passed by any class loaded with the test data.
 *
 * @author Christian
 */
@Ignore
public abstract class IDMapperTest extends IDMapperCapabilitiesTest{
            
    static protected IDMapper idMapper;
    
    //Used by subsets during settup to check connection is ok.
    //If set to false will skip testClose(). 
    protected static boolean connectionOk = true;
    
    @AfterClass //Setup as an afterclass so it is run last.
    public static void testClose() throws IDMapperException{
        if (connectionOk){        
            assertTrue (idMapper.isConnected());
            idMapper.close();
            assertFalse (idMapper.isConnected());
        }
    }
    
    @Test
    public void testMapIDManyToManyNoDataSources() throws IDMapperException{
        report("MapIDManyToManyNoDataSources");
        HashSet<Xref> srcXrefs = new HashSet<Xref>();
        srcXrefs.add(map1xref1);
        srcXrefs.add(map2xref2);
        srcXrefs.add(mapBadxref1);
        Map<Xref, Set<Xref>> results = idMapper.mapID(srcXrefs);
        Set<Xref> resultSet = results.get(map1xref1);
        assertNotNull(resultSet);
        System.out.println(map1xref1);        
        System.out.println(resultSet);
        System.out.println(map1xref2);
        System.out.println(DataSource.getDataSources());
        assertTrue(resultSet.contains(map1xref2));
        assertTrue(resultSet.contains(map1xref3));
        assertFalse(resultSet.contains(map2xref1));
        assertFalse(resultSet.contains(map2xref3));
        resultSet = results.get(map2xref2);
        assertNotNull(resultSet);
        assertFalse(resultSet.contains(map1xref2));
        assertFalse(resultSet.contains(map1xref3));
        assertTrue(resultSet.contains(map2xref1));
        assertTrue(resultSet.contains(map2xref3));
        resultSet = results.get(map2xref1);
        assertNull(resultSet);
        resultSet = results.get(map3xref1);
        assertNull(resultSet);
        resultSet = results.get(mapBadxref1);
        //Could be a result with just self. 
        if (resultSet != null && !resultSet.isEmpty()){
            assertEquals(1, resultSet.size());
            assertTrue(resultSet.contains(mapBadxref1));
        }
    }
    
    @Test
    public void testMapIDOneToManyNoDataSources() throws IDMapperException{
        report("MapIDOneToManyNoDataSources");
        Set<Xref> results = idMapper.mapID(map1xref1);
        assertTrue(results.contains(map1xref2));
        assertTrue(results.contains(map1xref3));
        assertFalse(results.contains(map2xref1));
        assertFalse(results.contains(map2xref2));
        assertFalse(results.contains(map2xref2));
    }
    
    @Test
    public void testMapIDOneBad() throws IDMapperException{
        report("MapIDOneToManyNoDataSources");
        Set<Xref> results = idMapper.mapID(mapBadxref1);
        if (results != null && !results.isEmpty()){
            assertEquals(1, results.size());
            assertTrue(results.contains(mapBadxref1));
        }
    }

    @Test
    public void testMapIDOneBadWithTarget() throws IDMapperException{
        report("MapIDOneToManyNoDataSourcesWithTarget");
        Set<Xref> results = idMapper.mapID(mapBadxref1, DataSource2);
        if (results != null && !results.isEmpty()){
            assertEquals(1, results.size());
            assertTrue(results.contains(mapBadxref1));
        }
    }

    @Test
    public void testMapIDOneToManyWithOneDataSource() throws IDMapperException{
        report("MapIDOneToManyWithOneDataSource");
        Set<Xref> results = idMapper.mapID(map1xref1, DataSource2);
        assertTrue(results.contains(map1xref2));
        assertFalse(results.contains(map1xref3));
        assertFalse(results.contains(map2xref1));
        assertFalse(results.contains(map2xref2));
        assertFalse(results.contains(map2xref2));
    }
 
    @Test
    public void testMapIDOneToManyWithTwoDataSources() throws IDMapperException{
        report("MapIDOneToManyWithTwoDataSources");
        Set<Xref> results = idMapper.mapID(map1xref1, DataSource2, DataSource3);
        assertTrue(results.contains(map1xref2));
        assertTrue(results.contains(map1xref3));
        assertFalse(results.contains(map2xref1));
        assertFalse(results.contains(map2xref2));
        assertFalse(results.contains(map2xref2));
    }
 
    @Test
    public void testMapIDOneToManyNoDataSources2() throws IDMapperException{
        report("MapIDOneToManyNoDataSources");
        Set<Xref> results = idMapper.mapID(map2xref1);
        assertTrue(results.contains(map2xref2));
        assertTrue(results.contains(map2xref3));
        assertFalse(results.contains(map1xref2));
        assertFalse(results.contains(map3xref2));
     }

    @Test
    public void testXrefSupported() throws Exception {
        report("XrefSupported");
        assertTrue(idMapper.xrefExists(map1xref1));
        assertTrue(idMapper.xrefExists(map1xref2));
        assertTrue(idMapper.xrefExists(map1xref3));
        assertTrue(idMapper.xrefExists(map2xref1));
        assertTrue(idMapper.xrefExists(map2xref2));
        assertTrue(idMapper.xrefExists(map2xref3));
        assertTrue(idMapper.xrefExists(map3xref1));
        assertTrue(idMapper.xrefExists(map3xref2));
        assertTrue(idMapper.xrefExists(map3xref3));
        assertFalse(idMapper.xrefExists(mapBadxref1));
        assertFalse(idMapper.xrefExists(mapBadxref2));
        assertFalse(idMapper.xrefExists(mapBadxref3));
    }
        
    @Test
    public void testFreeSearchBad() throws IDMapperException{
        org.junit.Assume.assumeTrue(idMapper.getCapabilities().isFreeSearchSupported());       
        org.junit.Assume.assumeTrue(badID != null);
        report("FreeSearchBad");
        Set<Xref> results = idMapper.freeSearch(badID, 10);
        assertTrue (results == null || results.isEmpty());
    }
    
    @Test
    public void testFreeSearchGood() throws IDMapperException{
        report("FreeSearchGood");
        org.junit.Assume.assumeTrue(idMapper.getCapabilities().isFreeSearchSupported());       
        report("FreeSearchGood");
        Set<Xref> results = idMapper.freeSearch(ds1Id1, 10);
        //there many be many othe results in which case skip testing for specific ones.
         if (results.size() < 10){
            assertTrue (results.contains(map1xref1));
        }
        assertFalse (results.contains(map2xref1));
    }

    @Test
    @Ignore // There is no longer an id in the test data used more than once.
    public void testFreeSearchGoodJust2() throws IDMapperException{
        org.junit.Assume.assumeTrue(idMapper.getCapabilities().isFreeSearchSupported());       
        report("FreeSearchGoodJust2");
        Set<Xref> results = idMapper.freeSearch("An id used more than twice here", 2);
        assertEquals (2, results.size());
    }
    
    //** Tests where half of Xref is null **
    @Test
    public void testXrefWithHalfNullXrefs() throws IDMapperException{
        report("XrefWithHalfNullXrefs");
        assertFalse (idMapper.xrefExists(HALFNULL1));
		assertFalse (idMapper.xrefExists(HALFNULL2));
    }
    
    @Test
    public void testIDMapperHalfNullXrefs() throws IDMapperException{
        report("IDMapperHalfNullXrefs");
        Set<Xref> result = idMapper.mapID(HALFNULL1);
        assertTrue(result == null || result.isEmpty());
    }
    
    @Test
    public void testIDMapperHalfNullXrefs2() throws IDMapperException{
        report("IDMapperHalfNullXrefs2");
        Set<Xref> result = idMapper.mapID(HALFNULL2);
        assertTrue(result == null || result.isEmpty());
    }

    @Test
    public void testIDMapperHalfNullXrefs3() throws IDMapperException{
        report("IDMapperHalfNullXrefs3");
        Set<Xref> result = idMapper.mapID(HALFNULL1, DataSource2, DataSource3);
        assertTrue(result == null || result.isEmpty());
    }
    
    @Test
    public void testIDMapperHalfNullXrefs4() throws IDMapperException{
        report("IDMapperHalfNullXrefs4");
        Set<Xref> result = idMapper.mapID(HALFNULL2, DataSource2, DataSource3);
        assertTrue(result == null || result.isEmpty());
    }

    @Test
    public void testIDMapperSeveralHalfNullXrefs() throws IDMapperException{
        report("IDMapperSeveralHalfNullXrefs");
        HashSet<Xref> src = new HashSet<Xref>();
        src.add(HALFNULL1);
        src.add(HALFNULL2);
        Map<Xref, Set<Xref>> result = idMapper.mapID(src, DataSource2, DataSource3);
        assertTrue(result == null || result.isEmpty());
    }

}
