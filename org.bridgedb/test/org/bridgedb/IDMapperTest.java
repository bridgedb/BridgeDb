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
    
    //If the actual source to be tested does not contain these please overwrite with ones that do exist.
    public DataSource DataSource1 = DataSource.register("TestDS1", "TestDS1").asDataSource();
    public DataSource DataSource2 = DataSource.register("TestDS2", "TestDS2").asDataSource();
    public DataSource DataSource3 = DataSource.register("TestDS3", "TestDS3").asDataSource();
    //This DataSource MUST not be supported
    public DataSource DataSourceBad = DataSource.register("TestDSBad", "TestDSBad").asDataSource();
    
    //Set of Xrefs that are expected to map together.
    //Note: Ids intentionally equals for testing of DataCollection
    public Xref map1xref1 = new Xref("123", DataSource1);
    public Xref map1xref2 = new Xref("123", DataSource2);
    public Xref map1xref3 = new Xref("123", DataSource3);
    //Second set of Xrefs that are expected to map together.
    //But these are not expected NOT to map to the first set
    public Xref map2xref1 = new Xref("456", DataSource1);
    public Xref map2xref2 = new Xref("456", DataSource2);
    public Xref map2xref3 = new Xref("456", DataSource3);
    //Third Set of Xref which again should map to eachothe but not the above
    public Xref map3xref1 = new Xref("789", DataSource1);
    public Xref map3xref2 = new Xref("789", DataSource2);
    public Xref map3xref3 = new Xref("789", DataSource3);
    //And a few Xrefs also not used
    public Xref mapBadxref1 = new Xref("123", DataSourceBad);
    public Xref mapBadxref2 = new Xref("abc", DataSource2);
    public Xref mapBadxref3 = new Xref("789", DataSourceBad);
        
    //Must be instantiated bu sup tests.
    public static IDMapper idMapper;
    
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
        System.out.println(dataSources);
        DataSource expected = dataSources.iterator().next();
        System.out.println(expected);
        
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
    
    
}
