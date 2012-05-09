package org.bridgedb;

import org.junit.Ignore;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
@Ignore
public abstract class IDMapperCapabilitiesTest extends IDMapperTestBase{
    
    //Must be instantiated by implementation of these tests.
    protected static IDMapperCapabilities capabilities;
        
    @Test
    public void testDataSourceSupported() throws Exception {
        report("DataSourceSupported");
        
        Set<DataSource> dataSources = capabilities.getSupportedSrcDataSources();
        
        DataSource expected = dataSources.iterator().next();
        
        assertTrue(dataSources.contains(DataSource1));
        assertTrue(dataSources.contains(DataSource2));
        assertTrue(dataSources.contains(DataSource3));
        assertFalse(dataSources.contains(DataSourceBad));
    }
   
    @Test
    public void testDataTargetSupported() throws Exception {
        report("DataTagerSupported");
        Set<DataSource> dataSources = capabilities.getSupportedTgtDataSources();
        DataSource expected = dataSources.iterator().next();
        
        assertTrue(dataSources.contains(DataSource1));
        assertTrue(dataSources.contains(DataSource2));
        assertTrue(dataSources.contains(DataSource3));
        assertFalse(dataSources.contains(DataSourceBad));
    }

    @Test
    public void testIsMappingSupported() throws IDMapperException{
        report("isMappingSupported");
        assertTrue(capabilities.isMappingSupported(DataSource1, DataSource2));
        assertFalse(capabilities.isMappingSupported(DataSource1, DataSourceBad));
    }

    @Test
    public void testGetKeys() throws IDMapperException{
        report("GetKeys");
        assertNotNull(capabilities.getKeys());
    }
    
    // each property should be a non-null value.
    // note that there may be zero properties, in which this 
    // test is irrelevant
    @Test
    public void testKeysHaveProperties(){
        report("KeysHaveProperties");
        for (String key : capabilities.getKeys())
		{	
            System.out.println("key: " + key);
            System.out.println("property: " + capabilities.getProperty(key));
			assertNotNull (capabilities.getProperty(key));
		}
    }
    
    @Test
    @Ignore
    public void testBadKeyNoProperties(){
        report ("BadKeyNoProperties");
        assertNull(capabilities.getProperty(badKey));
    }

}
