/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.util.Set;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.utils.Reporter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openrdf.model.Statement;

/**
 *
 * @author Christian
 */
public class LinksetStatementReaderTest {
    
    public LinksetStatementReaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
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

    private String TEST_FILE_NAME = "test-data/testMin.ttl";
    
    /**
     * Test of getVoidStatements method, of class LinksetStatementReader.
     */
    @Test
    public void testGetVoidStatements() throws MetaDataException {
        Reporter.report("getVoidStatements");
        LinksetStatementReader instance = new LinksetStatementReader(TEST_FILE_NAME);
        Set result = instance.getVoidStatements();
        assertEquals(8, result.size());
    }

    /**
     * Test of getLinkStatements method, of class LinksetStatementReader.
     */
    @Test
    public void testGetLinkStatements() throws MetaDataException {
        Reporter.report("getLinkStatements");
        LinksetStatementReader instance = new LinksetStatementReader(TEST_FILE_NAME);
        Set result = instance.getLinkStatements();
        assertEquals(3, result.size());
    }

    /**
     * Test of resetBaseURI method, of class LinksetStatementReader.
     */
    @Test
    public void testResetBaseURI() throws MetaDataException {
        Reporter.report("resetBaseURI");
        LinksetStatementReader instance = new LinksetStatementReader(TEST_FILE_NAME);
        String newBaseURI = "http://example.com/";
        instance.resetBaseURI(newBaseURI);
        Set<Statement> result = instance.getVoidStatements();
        for (Statement statement:result){
            assertTrue(statement.getSubject().stringValue().startsWith(newBaseURI));
            assertFalse(statement.getObject().stringValue().startsWith(StatementReader.DEFAULT_BASE_URI));
        }
    }
}
