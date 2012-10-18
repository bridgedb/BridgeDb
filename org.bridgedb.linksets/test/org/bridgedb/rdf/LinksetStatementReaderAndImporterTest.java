/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.util.Set;
import org.bridgedb.metadata.validator.ValidationType;
import org.bridgedb.utils.StoreType;
import org.bridgedb.linkset.LinksetLoader;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.utils.Reporter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public class LinksetStatementReaderAndImporterTest {
    
    public LinksetStatementReaderAndImporterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        //Check database is running and settup correctly or kill the test. 
        TestSqlFactory.createTestSQLAccess();
        
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of endRDF method, of class LinksetStatementReaderAndImporter.
     */
    @Test
    public void testLoadFromRDF() throws Exception {
        Reporter.report("EndRDF");
        LinksetLoader.clearExistingData(StoreType.TEST);
        ValidationType validationType = ValidationType.LINKSMINIMAL;
        LinksetStatementReaderAndImporter instance = new LinksetStatementReaderAndImporter("test-data/testPart2.ttl", StoreType.TEST);
        Set result = instance.getLinkStatements();
        System.out.println(result);
        assertEquals(3, result.size());
    }
}
