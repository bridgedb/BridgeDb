package org.bridgedb;

import org.junit.AfterClass;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public abstract class IDMapperCapabilitiesTest {
    
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
    //Add some half null xrefs
    protected static Xref HALFNULL1 = new Xref("123", null);
	protected static Xref HALFNULL2 = new Xref(null, DataSource1);
    //Add a property key that will not be found
    protected static String badKey = "NoT A ProPertY keY";
    
    //Used by subsets during settup to check connection is ok.
    //If set to false will skip testClose(). 
    protected static boolean connectionOk = true;
    
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
        //Add some half null xrefs
        HALFNULL1 = new Xref("123", null);
        HALFNULL2 = new Xref(null, DataSource1);
        //Add a property key that will not be found
        badKey = "NoT A ProPertY keY";
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
    
    // each property should be a non-null value.
    // note that there may be zero properties, in which this 
    // test is irrelevant
    @Test
    public void testKeysHaveProperties(){
        IDMapperCapabilities capabilities = idMapper.getCapabilities();
        for (String key : idMapper.getCapabilities().getKeys())
		{	
			assertNotNull (capabilities.getProperty(key));
		}
    }
    
    @Test
    public void testBadKeyNoProperties(){
        IDMapperCapabilities capabilities = idMapper.getCapabilities();
        assertNull(capabilities.getProperty(badKey));
    }

    @Test
    public void testFreeSearch() throws IDMapperException{
        org.junit.Assume.assumeTrue(idMapper.getCapabilities().isFreeSearchSupported());       
        org.junit.Assume.assumeTrue(badID != null);
        System.out.println("FreeSearchBad");
        Set<Xref> results = idMapper.freeSearch(badID, 10);
        assertTrue (results == null || results.isEmpty());
    }

    //** Tests where half of Xref is null **
    @Test
    public void testXrefWithHalfNullXrefs() throws IDMapperException{
        System.out.println("XrefWithHalfNullXrefs");
        assertFalse (idMapper.xrefExists(HALFNULL1));
		assertFalse (idMapper.xrefExists(HALFNULL2));
    }
    
    @Test
    public void testIDMapperHalfNullXrefs() throws IDMapperException{
        System.out.println("IDMapperHalfNullXrefs");
        Set<Xref> result = idMapper.mapID(HALFNULL1);
        assertTrue(result == null || result.isEmpty());
    }
    
    @Test
    public void testIDMapperHalfNullXrefs2() throws IDMapperException{
        System.out.println("IDMapperHalfNullXrefs2");
        Set<Xref> result = idMapper.mapID(HALFNULL2);
        assertTrue(result == null || result.isEmpty());
    }

    @Test
    public void testIDMapperHalfNullXrefs3() throws IDMapperException{
        System.out.println("IDMapperHalfNullXrefs3");
        Set<Xref> result = idMapper.mapID(HALFNULL1, DataSource2, DataSource3);
        assertTrue(result == null || result.isEmpty());
    }
    
    @Test
    public void testIDMapperHalfNullXrefs4() throws IDMapperException{
        System.out.println("IDMapperHalfNullXrefs4");
        Set<Xref> result = idMapper.mapID(HALFNULL2, DataSource2, DataSource3);
        assertTrue(result == null || result.isEmpty());
    }

    @Test
    public void testIDMapperSeveralHalfNullXrefs() throws IDMapperException{
        System.out.println("IDMapperSeveralHalfNullXrefs");
        HashSet<Xref> src = new HashSet<Xref>();
        src.add(HALFNULL1);
        src.add(HALFNULL2);
        Map<Xref, Set<Xref>> result = idMapper.mapID(src, DataSource2, DataSource3);
        assertTrue(result == null || result.isEmpty());
    }
}
