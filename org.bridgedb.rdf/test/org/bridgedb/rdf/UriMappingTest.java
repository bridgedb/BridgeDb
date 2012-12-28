/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.BufferedWriter;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.TestUtils;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

/**
 *
 * @author Christian
 */
public class UriMappingTest extends TestUtils{
    
    public UriMappingTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws BridgeDBException {
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
     * Test of addMapping method, of class UriMapping.
     */
    @Test
    public void testAddMapping() throws Exception {
        report("addMapping");
        DataSource dataSource = DataSource.getByFullName("UriMappingTest_testAddMapping");
        UriPattern uriPattern = UriPattern.byNameSpace("http://UriMapping.example.com/testAddMapping/");
        UriMappingRelationship uriMappingRelationship = UriMappingRelationship.DATA_SOURCE_URL_PATTERN;
        UriMapping expResult = UriMapping.addMapping(dataSource, uriPattern, uriMappingRelationship);
        UriMapping result = UriMapping.addMapping(dataSource, uriPattern, uriMappingRelationship);
        assertEquals(expResult, result);
        Set setResult = UriMapping.getAllUriMappings();
        assertThat(setResult,(Matcher) hasItem(expResult));
    }

    /**
     * Test of init method, of class UriMapping.
     */
    @Test
    public void testInit() throws Exception {
        report("init");
        String nameSpace = "http://UriMapping.example.com/testInit/";
        DataSource dataSource = DataSource.register("", "UriMappingTest_testInit").
                urlPattern(nameSpace + "$id").asDataSource();
        UriMapping.init();
        UriPattern uriPattern = UriPattern.byNameSpace(nameSpace);
        UriMappingRelationship uriMappingRelationship = UriMappingRelationship.DATA_SOURCE_URL_PATTERN;
        Set setResult = UriMapping.getAllUriMappings();
        UriMapping expResult = UriMapping.addMapping(dataSource, uriPattern, uriMappingRelationship);
        assertThat(setResult,(Matcher) hasItem(expResult));
    }

    /**
     * Test of showSharedUriPatterns method, of class UriMapping.
     */
    @Test
    public void testShowSharedUriPatterns() {
        report("showSharedUriPatterns");
        UriMapping.showSharedUriPatterns();
    }

    /**
     * Test of getRdfId method, of class UriMapping.
     */
    @Test
    public void testGetRdfId() throws BridgeDBException {
        report("getRdfId");
        DataSource dataSource = DataSource.getByFullName("UriMappingTest_testGetRdfId");
        UriPattern uriPattern = UriPattern.byNameSpace("http://UriMapping.example.com/testGetRdfId/");
        UriMappingRelationship uriMappingRelationship = UriMappingRelationship.DATA_SOURCE_URL_PATTERN;
        UriMapping instance = UriMapping.addMapping(dataSource, uriPattern, uriMappingRelationship);
        String expResult = ":uriMapping_UriMappingTest_testGetRdfId_http_UriMapping_example_com_testGetRdfId";
        String result = instance.getRdfId();
        assertEquals(expResult, result);
    }

    /**
     * Test of writeAllAsRDF method, of class UriMapping.
     * /
    @Test
    public void testWriteAllAsRDF() throws Exception {
        System.out.println("writeAllAsRDF");
        BufferedWriter writer = null;
        UriMapping.writeAllAsRDF(writer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of writeAsRDF method, of class UriMapping.
     * /
    @Test
    public void testWriteAsRDF() throws Exception {
        System.out.println("writeAsRDF");
        BufferedWriter writer = null;
        UriMapping instance = null;
        instance.writeAsRDF(writer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of readRdf method, of class UriMapping.
     * /
    @Test
    public void testReadRdf() throws Exception {
        System.out.println("readRdf");
        Resource mappingId = null;
        Set<Statement> uriMappingStatements = null;
        UriMapping expResult = null;
        UriMapping result = UriMapping.readRdf(mappingId, uriMappingStatements);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDataSource method, of class UriMapping.
     * /
    @Test
    public void testGetDataSource() {
        System.out.println("getDataSource");
        UriMapping instance = null;
        DataSource expResult = null;
        DataSource result = instance.getDataSource();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUriPattern method, of class UriMapping.
     * /
    @Test
    public void testGetUriPattern() {
        System.out.println("getUriPattern");
        UriMapping instance = null;
        UriPattern expResult = null;
        UriPattern result = instance.getUriPattern();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    /**/
}
