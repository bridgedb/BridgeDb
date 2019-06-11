/*
 * BridgeDb,
 * An abstraction layer for identifier mapping services, both local and online.
 * Copyright (c) 2006 - 2009  BridgeDb Developers
 * Copyright (c) 2012-2013 Christian Y. A. Brenninkmeiier
 * Copyright (c) 2012 - 2013 OpenPhacts
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.bridgedb.rdf;

import java.io.InputStream;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.bio.DataSourceComparator;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.Reporter;
import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;
import org.junit.jupiter.api.*;

import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class DataSourceComparatorTest {
    
   
    private static final String DATA_SOURCE_MIN_TTL = "/DataSourceMin.ttl";

	public DataSourceComparatorTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

   /**
     * Test of getResourceId method, of class DataSourceUris.
     */
    @Test
    public void testGetResourceId() {
        Reporter.println("getResourceId");
        DataSource dataSource = DataSource.getByFullName("DataSourceUrisTest_testGetResourceId");
        Resource expResult = new URIImpl("http://vocabularies.bridgedb.org/ops#DataSource_DataSourceUrisTest_testGetResourceId");
        Resource result = BridgeDBRdfHandler.asResource(dataSource);
        assertEquals(expResult, result);
    }

    @Test
    public void testCompare() throws BridgeDBException{
        Reporter.println("compare");
        DataSource dataSource1 = 
                DataSource.register("DataSourceUrisTest_testCompare1", "DataSourceUrisTest_testCompare1").asDataSource();
        DataSource dataSource2 = 
                DataSource.register("DataSourceUrisTest_testCompare2", "DataSourceUrisTest_testCompare2").asDataSource();
        DataSource dataSource3 = DataSource.getByFullName("dataSourceUrisTest_testCompare3");
        DataSource dataSource4 = DataSource.getBySystemCode("DataSourceUrisTest_testCompare4");
        DataSource dataSource5 = DataSource.getBySystemCode("DataSourceUrisTest_testCompare5");
        DataSourceComparator comparator = new DataSourceComparator();
        assertEquals(0, comparator.compare(dataSource1, dataSource1));
        assertThat(comparator.compare(dataSource1, dataSource2), lessThan(0));
        assertThat(comparator.compare(dataSource2, dataSource1), greaterThan(0));
        assertThat(comparator.compare(dataSource1, dataSource3), lessThan(0));
        assertThat(comparator.compare(dataSource3, dataSource1), greaterThan(0));
        assertThat(comparator.compare(dataSource5, dataSource1), lessThan(0));
        assertThat(comparator.compare(dataSource1, dataSource5), greaterThan(0));
        assertThat(comparator.compare(dataSource2, dataSource3), lessThan(0));
        assertThat(comparator.compare(dataSource3, dataSource2), greaterThan(0));
        assertThat(comparator.compare(dataSource4, dataSource2), lessThan(0));
        assertThat(comparator.compare(dataSource2, dataSource4), greaterThan(0));
        assertThat(comparator.compare(dataSource5, dataSource2), lessThan(0));
        assertThat(comparator.compare(dataSource2, dataSource5), greaterThan(0));
        assertThat(comparator.compare(dataSource4, dataSource3), lessThan(0));
        assertThat(comparator.compare(dataSource3, dataSource4), greaterThan(0));
        assertThat(comparator.compare(dataSource4, dataSource1), lessThan(0));
        assertThat(comparator.compare(dataSource3, dataSource5), greaterThan(0));
        assertThat(comparator.compare(dataSource4, dataSource5), lessThan(0));
        assertThat(comparator.compare(dataSource5, dataSource4), greaterThan(0));
    }
    
    @Test
    public void testGetUriPatterns() throws BridgeDBException{
        Reporter.println("GetUriPatterns");
        InputStream dataSourceStream = getClass().getResourceAsStream(DATA_SOURCE_MIN_TTL);
        assertNotNull("Could not find: test-data" + DATA_SOURCE_MIN_TTL);
        
        BridgeDBRdfHandler.parseRdfInputStream(dataSourceStream);
        Set<UriPattern> result = UriPattern.byCodeAndType("Cs", UriPatternType.mainUrlPattern);
        assertFalse("Could not find main URL pattern for Cs", result.isEmpty());
        UriPattern pattern = UriPattern.existingByPattern("http://www.chemspider.com/Chemical-Structure.$id.html");
        assertThat (result, hasItem(pattern));
//        pattern = UriPattern.existingOrCreateByPattern("http://identifiers.org/chemspider/$id");
//        assertThat (result, hasItem(pattern));
    }
}
