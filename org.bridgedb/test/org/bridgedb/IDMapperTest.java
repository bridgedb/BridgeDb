package org.bridgedb;

import java.util.Map;
import java.util.HashSet;
import java.util.Collection;
import java.util.Set;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public abstract class IDMapperTest {
    
    //Must be instantiated by implementation of these tests.
    protected static IDMapper idMapper;
    
    //DataSource that MUST be supported.
    protected static DataSource DataSource1;
    protected static DataSource DataSource2;
    protected static DataSource DataSource3;
    //This DataSource MUST not be supported
    protected static DataSource DataSourceBad;
    
    //The id for map1xref1
    String goodId1;
    //Set of Xrefs that are expected to map together.
    protected static Xref map1xref1;
    protected static Xref map1xref2;
    protected static Xref map1xref3;
    //Second set of Xrefs that are expected to map together.
    protected static Xref map2xref1;
    protected static Xref map2xref2;
    protected static Xref map2xref3;
    //Third Set of Xref which again should map to each other but not the above
    protected static Xref map3xref1;
    protected static Xref map3xref2;
    protected static Xref map3xref3;
    //Add an id that does not exist and can not be used in freesearch
    //Or null if all Strings can be used.
    protected static String badID;
    //And a few Xrefs also not used
    protected static Xref mapBadxref1;
    protected static Xref mapBadxref2;
    protected static Xref mapBadxref3;
        
    
    @BeforeClass
    /**
     * Class to set up the variables.
     * 
     * Should be overrided to change all of the variables.
     * To change some over write it. Call super.setupVariables() and then change the few that need fixing.
     * <p>
     * Note: According to the Junit api 
     * "The @BeforeClass methods of superclasses will be run before those the current class."
     */
    public static void setupVariables(){
        //If the actual source to be tested does not contain these please overwrite with ones that do exist.
        DataSource1 = DataSource.register("TestDS1", "TestDS1").asDataSource();
        DataSource2 = DataSource.register("TestDS2", "TestDS2").asDataSource();
        DataSource3 = DataSource.register("TestDS3", "TestDS3").asDataSource();
        //This DataSource MUST not be supported
        DataSourceBad = DataSource.register("TestDSBad", "TestDSBad").asDataSource();
    
        //Set of Xrefs that are expected to map together.
        //Note: Ids intentionally equals for testing of DataCollection
        map1xref1 = new Xref("123", DataSource1);
        map1xref2 = new Xref("123", DataSource2);
        map1xref3 = new Xref("123", DataSource3);
        //Second set of Xrefs that are expected to map together.
        //But these are not expected NOT to map to the first set
        map2xref1 = new Xref("456", DataSource1);
        map2xref2 = new Xref("456", DataSource2);
        map2xref3 = new Xref("456", DataSource3);
        //Third Set of Xref which again should map to eachothe but not the above
        map3xref1 = new Xref("789", DataSource1);
        map3xref2 = new Xref("789", DataSource2);
        map3xref3 = new Xref("789", DataSource3);
        //Add an id that does not exist and can not be used in freesearch
        //Or null if all Strings can be used.
        badID = "abc";
        //And a few Xrefs also not used
        mapBadxref1 = new Xref("123", DataSourceBad);
        mapBadxref2 = new Xref(badID, DataSource2);
        mapBadxref3 = new Xref("789", DataSourceBad);        
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
    public void testDataSourceSupported() throws Exception {
        System.out.println("DataSourceSupported");
        Set<DataSource> dataSources = idMapper.getCapabilities().getSupportedSrcDataSources();
        DataSource expected = dataSources.iterator().next();
        
        assertTrue(dataSources.contains(DataSource1));
        assertTrue(dataSources.contains(DataSource2));
        assertTrue(dataSources.contains(DataSource3));
        assertFalse(dataSources.contains(DataSourceBad));
    }
   
    @Test
    public void testDataTargetSupported() throws Exception {
        System.out.println("DataTagerSupported");
        Set<DataSource> dataSources = idMapper.getCapabilities().getSupportedTgtDataSources();
        DataSource expected = dataSources.iterator().next();
        
        assertTrue(dataSources.contains(DataSource1));
        assertTrue(dataSources.contains(DataSource2));
        assertTrue(dataSources.contains(DataSource3));
        assertFalse(dataSources.contains(DataSourceBad));
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
    public void testIsMappingSupported() throws IDMapperException{
        System.out.println("isMappingSupported");
        IDMapperCapabilities capabilities = idMapper.getCapabilities();
        assertTrue(capabilities.isMappingSupported(DataSource1, DataSource2));
        assertFalse(capabilities.isMappingSupported(DataSource1, DataSourceBad));
    }

    @Test
    public void testGetKeys() throws IDMapperException{
        System.out.println("GetKeys");
        IDMapperCapabilities capabilities = idMapper.getCapabilities();
        assertNotNull(capabilities.getKeys());
    }
    
    @Test
    public void testFreeSearchGood() throws IDMapperException{
        org.junit.Assume.assumeTrue(idMapper.getCapabilities().isFreeSearchSupported());       
        System.out.println("FreeSearchGood");
        Set<Xref> results = idMapper.freeSearch(map1xref1.getId(), 10);
        assertTrue (results.contains(map1xref1));
    }

    @Test
    public void testFreeSearch() throws IDMapperException{
        org.junit.Assume.assumeTrue(idMapper.getCapabilities().isFreeSearchSupported());       
        org.junit.Assume.assumeTrue(badID != null);
        System.out.println("FreeSearchBad");
        Set<Xref> results = idMapper.freeSearch(badID, 10);
        assertTrue (results == null || results.isEmpty());
    }
}
