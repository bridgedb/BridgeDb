package org.bridgedb;

import org.junit.Ignore;
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
public abstract class IDMapperCapabilitiesTest extends IDMapperTestBase{
    
    //Must be instantiated by implementation of these tests.
    protected static IDMapper idMapper;
        
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
    @Ignore
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
