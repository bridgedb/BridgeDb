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

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.bio.DataSourceComparator;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.Reporter;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Christian
 */
public class DataSourceComparatorTest {
    
   
    private static final String DATA_SOURCE_MIN_TTL = "/DataSourceMin.ttl";

   /**
     * Test of getResourceId method, of class DataSourceUris.
     */
    @Test
    public void testGetResourceId() {
        Reporter.println("getResourceId");
        DataSource dataSource = DataSource.register("DataSourceUrisTest_testGetResourceId", "DataSourceUrisTest_testGetResourceId").asDataSource();
        Resource expResult = SimpleValueFactory.getInstance().createIRI("http://vocabularies.bridgedb.org/ops#DataSource_DataSourceUrisTest_testGetResourceId");
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
        DataSource dataSource3 =  
                DataSource.register("dataSourceUrisTest_testCompare3", "dataSourceUrisTest_testCompare3").asDataSource();
        DataSource dataSource4 =  
                DataSource.register("DataSourceUrisTest_testCompare4", "DataSourceUrisTest_testCompare4").asDataSource();
        DataSource dataSource5 = 
                DataSource.register("DataSourceUrisTest_testCompare5", "DataSourceUrisTest_testCompare5").asDataSource();
        DataSourceComparator comparator = new DataSourceComparator();
        assertEquals(0, comparator.compare(dataSource1, dataSource1));
        assertThat(comparator.compare(dataSource1, dataSource2), lessThan(0));
        assertThat(comparator.compare(dataSource2, dataSource1), greaterThan(0));
        assertThat(comparator.compare(dataSource1, dataSource3), lessThan(0));
        assertThat(comparator.compare(dataSource3, dataSource1), greaterThan(0));
        assertThat(comparator.compare(dataSource1, dataSource5), lessThan(0));
        assertThat(comparator.compare(dataSource5, dataSource1), greaterThan(0));
        assertThat(comparator.compare(dataSource2, dataSource3), lessThan(0));
        assertThat(comparator.compare(dataSource3, dataSource2), greaterThan(0));
        assertThat(comparator.compare(dataSource2, dataSource4), lessThan(0));
        assertThat(comparator.compare(dataSource4, dataSource2), greaterThan(0));
        assertThat(comparator.compare(dataSource2, dataSource5), lessThan(0));
        assertThat(comparator.compare(dataSource5, dataSource2), greaterThan(0));
        assertThat(comparator.compare(dataSource3, dataSource4), lessThan(0));
        assertThat(comparator.compare(dataSource4, dataSource3), greaterThan(0));
        assertThat(comparator.compare(dataSource1, dataSource4), lessThan(0));
        assertThat(comparator.compare(dataSource5, dataSource3), greaterThan(0));
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
        UriPattern pattern = UriPattern.existingByPattern("https://www.chemspider.com/Chemical-Structure.$id.html");
        assertThat (result, hasItem(pattern));
//        pattern = UriPattern.existingOrCreateByPattern("http://identifiers.org/chemspider/$id");
//        assertThat (result, hasItem(pattern));
    }
}
