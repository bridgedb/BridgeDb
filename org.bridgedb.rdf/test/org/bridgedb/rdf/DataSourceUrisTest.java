/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import org.bridgedb.DataSource;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.TestUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;

/**
 *
 * @author Christian
 */
public class DataSourceUrisTest extends TestUtils{
    
    public DataSourceUrisTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

   /**
     * Test of getResourceId method, of class DataSourceUris.
     */
    @Test
    public void testGetResourceId() {
        report("getResourceId");
        DataSource dataSource = DataSource.getByFullName("DataSourceUrisTest_testGetResourceId");
        URI expResult = new URIImpl("http://openphacts.cs.man.ac.uk:9090//ontology/DataSource.owl#DataSource_DataSourceUrisTest_testGetResourceId");
        URI result = DataSourceUris.getResourceId(dataSource);
        assertEquals(expResult, result);
    }

    /**
     * Test of byDataSource method, of class DataSourcePlus.
     */
    @Test
    public void testByDataSource() throws BridgeDBException {
        report("byDataSource");
        DataSource dataSource = DataSource.getByFullName("DataSourceUrisTest_testByDataSource");
        DataSourceUris expResult = DataSourceUris.byDataSource(dataSource);
        DataSourceUris result = DataSourceUris.byDataSource(dataSource);
        assertEquals(expResult, result);
    }

}
