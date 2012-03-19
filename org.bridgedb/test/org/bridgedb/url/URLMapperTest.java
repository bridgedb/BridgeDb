package org.bridgedb.url;

import org.bridgedb.DataSource;
import org.junit.BeforeClass;
import org.bridgedb.IDMapperException;
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
public abstract class URLMapperTest {
            
    //Must be instantiated by implementation of these tests.
    protected static URLMapper urlMapper;

    //Used by subsets during settup to check connection is ok.
    //If set to false will skip testClose(). 
    protected static boolean connectionOk = true;
    
    protected static String nameSpace2;
    protected static String nameSpace3;
    
    protected static String map1URL1;
    protected static String map1URL2;
    protected static String map1URL3;
    //Second set of URLs that are expected to map together.
    protected static String map2URL1;
    protected static String map2URL2;
    protected static String map2URL3;
    //Third Set of URLs which again should map to each other but not the above
    protected static String map3URL1;
    protected static String map3URL2;
    protected static String map3URL3;
    //Add an id that does not exist and can not be used in freesearch
    //Or null if all Strings can be used.
    protected static String badID;
    //And a few URLs also not used
    protected static String mapBadURL1;
    protected static String mapBadURL2;
    protected static String mapBadURL3;

    @BeforeClass
    public static void loadDataSources() throws IDMapperException{
        DataSource.register("TestDS1", "TestDS1").urlPattern("www.example.com/pizza/$id/topping");
        DataSource.register("TestDS2", "TestDS2").urlPattern("www.example.com/$id");
        DataSource.register("TestDS3", "TestDS3").nameSpace("www.example.org#");
        
        nameSpace2 = "www.example.com/";
        nameSpace3 = "www.example.org#";
        
        map1URL1 = "www.example.com/pizza/123/topping";
        map1URL2 = "www.example.com/123";
        map1URL3 = "www.example.org#123";
        //Second set of URLs that are expected to map together.
        map2URL1 = "www.example.com/pizza/456/topping";
        map2URL2 = "www.example.com/456";
        map2URL3 = "www.example.org#456";
        //Third Set of URLs which again should map to each other but not the above
        map3URL1 = "www.example.com/pizza/789/topping";
        map3URL2 = "www.example.com/789";
        map3URL3 = "www.example.org#789";
        //Add an id that does not exist and can not be used in freesearch
        //Or null if all Strings can be used.
        badID = "abc";
        //And a few URLs also not used
        mapBadURL1 = "www.notInURLMapper.com#123";
        mapBadURL2 = "www.example.com/abc";
        mapBadURL3 = "www.notInURLMapper.com#789";
    }
    
    @AfterClass //Setup as an afterclass so it is run last.
    public static void testClose() throws IDMapperException{
        if (connectionOk){        
            assertTrue (urlMapper.isConnected());
            urlMapper.close();
            assertFalse (urlMapper.isConnected());
        }
    }
    
    @Test
    public void testMapIDManyToManyNoDataSources() throws IDMapperException{
        System.out.println("MapIDManyToManyNoDataSources");
        HashSet<String> srcURLs = new HashSet<String>();
        srcURLs.add(map1URL1);
        srcURLs.add(map2URL2);
        srcURLs.add(mapBadURL1);
        assertNotNull(map1URL1);
        assertNotNull(map2URL2);
        assertNotNull(mapBadURL1);
        Map<String, Set<String>> results = urlMapper.mapURL(srcURLs);
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
        //Assuming either theer 
        assertTrue(resultSet == null || resultSet.isEmpty());
    }
    
    @Test
    public void testMapIDOneToManyNoDataSources() throws IDMapperException{
        System.out.println("MapIDOneToManyNoDataSources");
        Set<String> results = urlMapper.mapURL(map1URL1);
        assertTrue(results.contains(map1URL2));
        assertTrue(results.contains(map1URL3));
        assertFalse(results.contains(map2URL1));
        assertFalse(results.contains(map2URL2));
        assertFalse(results.contains(map2URL2));
    }
    
    @Test
    public void testMapIDOneBad() throws IDMapperException{
        System.out.println("MapIDOneToManyNoDataSources");
        Set<String> results = urlMapper.mapURL(mapBadURL1);
        assertEquals(0, results.size());
    }

    @Test
    public void testMapIDOneToManyWithOneDataSource() throws IDMapperException{
        System.out.println("MapIDOneToManyWithOneDataSource");
        Set<String> results = urlMapper.mapURL(map1URL1, nameSpace2);
        assertTrue(results.contains(map1URL2));
        assertFalse(results.contains(map1URL3));
        assertFalse(results.contains(map2URL1));
        assertFalse(results.contains(map2URL2));
        assertFalse(results.contains(map2URL2));
    }
 
    @Test
    public void testMapIDOneToManyWithTwoDataSources() throws IDMapperException{
        System.out.println("MapIDOneToManyWithTwoDataSources");
        Set<String> results = urlMapper.mapURL(map1URL1, nameSpace2, nameSpace3);
        assertTrue(results.contains(map1URL2));
        assertTrue(results.contains(map1URL3));
        assertFalse(results.contains(map2URL1));
        assertFalse(results.contains(map2URL2));
        assertFalse(results.contains(map2URL2));
    }
 
    @Test
    public void testMapIDOneToManyNoDataSources2() throws IDMapperException{
        System.out.println("MapIDOneToManyNoDataSources");
        Set<String> results = urlMapper.mapURL(map2URL1);
        assertTrue(results.contains(map2URL2));
        assertTrue(results.contains(map2URL3));
        assertFalse(results.contains(map1URL2));
        assertFalse(results.contains(map3URL2));
     }

    @Test
    public void testXrefSupported() throws Exception {
        System.out.println("XrefSupported");
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
    public void testFreeSearch() throws IDMapperException{
        org.junit.Assume.assumeTrue(urlMapper.getCapabilities().isFreeSearchSupported());       
        org.junit.Assume.assumeTrue(badID != null);
        System.out.println("FreeSearchBad");
        Set<String> results = urlMapper.freeSearch(badID, 10);
        assertTrue (results == null || results.isEmpty());
    }
    
}
