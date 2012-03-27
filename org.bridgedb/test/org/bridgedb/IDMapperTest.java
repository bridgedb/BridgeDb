package org.bridgedb;

import org.junit.AfterClass;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public abstract class IDMapperTest extends IDMapperCapabilitiesTest{
            
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
        System.out.println("MapIDManyToManyNoDataSources");
        HashSet<Xref> srcXrefs = new HashSet<Xref>();
        srcXrefs.add(map1xref1);
        srcXrefs.add(map2xref2);
        srcXrefs.add(mapBadxref1);
        Map<Xref, Set<Xref>> results = idMapper.mapID(srcXrefs);
        Set<Xref> resultSet = results.get(map1xref1);
        System.out.println(map1xref1);
        assertNotNull(resultSet);
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
        //Assuming either theer 
        assertTrue(resultSet == null || resultSet.isEmpty());
    }
    
    @Test
    public void testMapIDOneToManyNoDataSources() throws IDMapperException{
        System.out.println("MapIDOneToManyNoDataSources");
        Set<Xref> results = idMapper.mapID(map1xref1);
        assertTrue(results.contains(map1xref2));
        assertTrue(results.contains(map1xref3));
        assertFalse(results.contains(map2xref1));
        assertFalse(results.contains(map2xref2));
        assertFalse(results.contains(map2xref2));
    }
    
    @Test
    public void testMapIDOneBad() throws IDMapperException{
        System.out.println("MapIDOneToManyNoDataSources");
        Set<Xref> results = idMapper.mapID(mapBadxref1);
        assertEquals(0, results.size());
    }

    @Test
    public void testMapIDOneToManyWithOneDataSource() throws IDMapperException{
        System.out.println("MapIDOneToManyWithOneDataSource");
        Set<Xref> results = idMapper.mapID(map1xref1, DataSource2);
        assertTrue(results.contains(map1xref2));
        assertFalse(results.contains(map1xref3));
        assertFalse(results.contains(map2xref1));
        assertFalse(results.contains(map2xref2));
        assertFalse(results.contains(map2xref2));
    }
 
    @Test
    public void testMapIDOneToManyWithTwoDataSources() throws IDMapperException{
        System.out.println("MapIDOneToManyWithTwoDataSources");
        Set<Xref> results = idMapper.mapID(map1xref1, DataSource2, DataSource3);
        assertTrue(results.contains(map1xref2));
        assertTrue(results.contains(map1xref3));
        assertFalse(results.contains(map2xref1));
        assertFalse(results.contains(map2xref2));
        assertFalse(results.contains(map2xref2));
    }
 
    @Test
    public void testMapIDOneToManyNoDataSources2() throws IDMapperException{
        System.out.println("MapIDOneToManyNoDataSources");
        Set<Xref> results = idMapper.mapID(map2xref1);
        assertTrue(results.contains(map2xref2));
        assertTrue(results.contains(map2xref3));
        assertFalse(results.contains(map1xref2));
        assertFalse(results.contains(map3xref2));
     }

    @Test
    public void testXrefSupported() throws Exception {
        System.out.println("XrefSupported");
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
        System.out.println("FreeSearchBad");
        Set<Xref> results = idMapper.freeSearch(badID, 10);
        assertTrue (results == null || results.isEmpty());
    }
    
    @Test
    public void testFreeSearchGood() throws IDMapperException{
        org.junit.Assume.assumeTrue(idMapper.getCapabilities().isFreeSearchSupported());       
        System.out.println("FreeSearchGood");
        Set<Xref> results = idMapper.freeSearch(goodId1, 10);
        assertTrue (results.contains(map1xref1));
        assertTrue (results.contains(map1xref2));
        assertTrue (results.contains(map1xref3));
        assertFalse (results.contains(map2xref1));
    }
}
