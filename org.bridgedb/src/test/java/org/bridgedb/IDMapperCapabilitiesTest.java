/*
 *BridgeDb,
 *An abstraction layer for identifier mapping services, both local and online.
 *Copyright (c) 2012 Christian Y. A. Brenninkmeijer
 *Copyright (c) 2013 OpenPhacts
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and limitations under the License.
 */

package org.bridgedb;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Test to be run on any class that implements IDMapperCapabilities
 * Severs both as a base for IdMapperTest
 * and to test class that implement IDMapperCapabilities such as WSClient
 *
 * Implementing tests have to set capabilities
 *
 * Should be passed by any class loaded with the test data.
 *
 * @author Christian
 */
public abstract class IDMapperCapabilitiesTest extends IDMapperTestBase{
    
    //Must be instantiated by implementation of these tests.
    protected static IDMapperCapabilities capabilities;
    
    @org.junit.jupiter.api.Test
    public void testDataSourceSupported() throws Exception {
        report("DataSourceSupported");
        Set<DataSource> dataSources = capabilities.getSupportedSrcDataSources();
        assertTrue(dataSources.contains(DataSource1));
        assertTrue(dataSources.contains(DataSource2));
        assertTrue(dataSources.contains(DataSource3));
        assertFalse(dataSources.contains(DataSourceBad));
    }

    @org.junit.jupiter.api.Test
    public void testDataTargetSupported() throws Exception {
        report("DataTagerSupported");
        Set<DataSource> dataSources = capabilities.getSupportedTgtDataSources();
        DataSource expected = dataSources.iterator().next();
        assertTrue(dataSources.contains(DataSource1));
        assertTrue(dataSources.contains(DataSource2));
        assertTrue(dataSources.contains(DataSource3));
        assertFalse(dataSources.contains(DataSourceBad));
    }

    @org.junit.jupiter.api.Test
    public void testIsMappingSupported() throws IDMapperException{
        report("isMappingSupported");
        assertTrue(capabilities.isMappingSupported(DataSource1, DataSource2));
        assertFalse(capabilities.isMappingSupported(DataSource1, DataSourceBad));
    }

    @org.junit.jupiter.api.Test
    public void testGetKeys() throws IDMapperException{
        report("GetKeys");
        assertNotNull(capabilities.getKeys());
    }
    
    // each property should be a non-null value.
    // note that there may be zero properties, in which this 
    // test is irrelevant
    @org.junit.jupiter.api.Test
    public void testKeysHaveProperties(){
        report("KeysHaveProperties");
        for (String key : capabilities.getKeys())
		{	
			assertNotNull (capabilities.getProperty(key));
		}
    }

    @org.junit.jupiter.api.Test
    public void testBadKeyNoProperties(){
        report ("BadKeyNoProperties");
        assertNull(capabilities.getProperty(badKey));
    }
}
