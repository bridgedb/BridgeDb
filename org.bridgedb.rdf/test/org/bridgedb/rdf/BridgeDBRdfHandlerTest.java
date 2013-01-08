/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.File;
import java.util.Date;
import org.bridgedb.IDMapperException;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.utils.TestUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author Christian
 */
public class BridgeDBRdfHandlerTest extends TestUtils{
    
    private static File file = new File("test-data/CreatedByTest.ttl");

    public BridgeDBRdfHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws IDMapperException {
        BioDataSource.init();
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
     * Test of writeRdfToFile method, of class BridgeDBRdfHandler.
     */
    @Test
    public void testWriteRdfToFile() throws Exception {
        report("writeRdfToFile");
        BridgeDBRdfHandler.writeRdfToFile(file);
    }

    /**
     * Test of parseRdfFile method, of class BridgeDBRdfHandler.
     */
    @Test
    public void testParseRdfFile() throws Exception {
        report("parseRdfFile ");
        BridgeDBRdfHandler.parseRdfFile(file);
    }

}
