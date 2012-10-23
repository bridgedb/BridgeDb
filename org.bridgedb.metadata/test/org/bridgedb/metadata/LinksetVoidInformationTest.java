/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.bridgedb.rdf.LinksetStatements;
import org.bridgedb.rdf.LinksetStatementReader;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.bridgedb.utils.Reporter;
import java.util.Set;
import org.bridgedb.metadata.constants.DctermsConstants;
import org.bridgedb.metadata.constants.VoidConstants;
import org.bridgedb.metadata.validator.ValidationType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openrdf.model.Resource;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Christian
 */
public class LinksetVoidInformationTest {
    
    private static LinksetVoidInformation instance;
    
    public LinksetVoidInformationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        LinksetStatements statements = new LinksetStatementReader(FileTest.LINK_FILE);
        instance = new LinksetVoidInformation(statements, ValidationType.LINKSETVOID);
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
     * Test of getSubjectUriSpace method, of class LinksetVoidInformation.
     */
    @Test
    public void testGetSubjectUriSpace() throws Exception {
        Reporter.report("getSubjectUriSpace");
        String result = instance.getSubjectUriSpace();
        assertEquals("http://data.kasabi.com/dataset/chembl-rdf/", result);
    }

    /**
     * Test of getTargetUriSpace method, of class LinksetVoidInformation.
     */
    @Test
    public void testGetTargetUriSpace() throws Exception {
        Reporter.report("getTargetUriSpace");
        String result = instance.getTargetUriSpace();
        assertEquals("http://rdf.chemspider.com/", result);
    }

    /**
     * Test of getPredicate method, of class LinksetVoidInformation.
     */
    @Test
    public void testGetPredicate() throws Exception {
        Reporter.report("getPredicate");
        String result = instance.getPredicate();
        assertEquals("http://www.w3.org/2004/02/skos/core#exactMatch", result);
    }

    /**
     * Test of getLinksetResource method, of class LinksetVoidInformation.
     */
    @Test
    public void testGetLinksetResource() throws Exception {
        Reporter.report("getLinksetResource");
        Resource result = instance.getLinksetResource();
        Resource expected = new URIImpl("http://data.kasabi.com/dataset/chembl-rdf/void.ttl/chembl-rdf-compounds_cs_linkset");
        assertEquals(expected, result);
    }

    /**
     * Test of isTransative method, of class LinksetVoidInformation.
     */
    @Test
    public void testIsTransative() {
        Reporter.report("isTransative");
        boolean result = instance.isTransative();
        assertFalse(result);
    }

    /**
     * Test of Schema method, of class LinksetVoidInformation.
     */
    @Test
    public void testSchema() {
        Reporter.report("Schema");
        String result = instance.Schema();
        assertNotNull(result);
    }

    /**
     * Test of hasRequiredValues method, of class LinksetVoidInformation.
     */
    @Test
    public void testHasRequiredValues() {
        Reporter.report("hasRequiredValues");
        boolean result = instance.hasRequiredValues();
        assertTrue(result);
    }

    /**
     * Test of hasCorrectTypes method, of class LinksetVoidInformation.
     */
    @Test
    public void testHasCorrectTypes() {
        Reporter.report("hasCorrectTypes");
        boolean result = instance.hasCorrectTypes();
        assertTrue(result);
    }

    /**
     * Test of validityReport method, of class LinksetVoidInformation.
     */
    @Test
    public void testValidityReport() {
        Reporter.report("validityReport");
        boolean includeWarnings = false;
        String result = instance.validityReport(includeWarnings);
        assertEquals("No issues found\nFound 2 links", result);
    }

     /**
     * Test of getValuesByPredicate method, of class LinksetVoidInformation.
     */
    @Test
    public void testGetValuesByPredicate() {
        Reporter.report("getValuesByPredicate");
        Set results = instance.getValuesByPredicate(DctermsConstants.TITLE);
        assertThat (results.size(), greaterThanOrEqualTo(3));
    }

    /**
     * Test of getResoucresByPredicate method, of class LinksetVoidInformation.
     */
    @Test
    public void testGetResoucresByPredicate() {
        Reporter.report("getResoucresByPredicate");
        Set results = instance.getResoucresByPredicate(VoidConstants.SUBJECTSTARGET);
        assertThat (results.size(), greaterThanOrEqualTo(1));
    }

    /**
     * Test of getRDF method, of class LinksetVoidInformation.
     */
    @Test
    public void testGetRDF() {
        Reporter.report("getRDF");
        Set<Statement> results = instance.getRDF();
        boolean found = false;
        for (Statement statement:results){
            if (statement.getObject().equals(VoidConstants.DATASET)){
                found = true;
            }
        }
        assertTrue(found);
        assertThat (results.size(), greaterThanOrEqualTo(20));
    }
}
