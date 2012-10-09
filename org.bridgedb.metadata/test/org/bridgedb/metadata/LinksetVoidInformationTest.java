/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

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
        instance = new LinksetVoidInformation(FileTest.LINK_FILE, ValidationType.LINKSETVOID);
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
        assertNotNull(result);
    }

    /**
     * Test of getTargetUriSpace method, of class LinksetVoidInformation.
     */
    @Test
    public void testGetTargetUriSpace() throws Exception {
        Reporter.report("getTargetUriSpace");
        String result = instance.getTargetUriSpace();
        assertNotNull(result);
    }

    /**
     * Test of getPredicate method, of class LinksetVoidInformation.
     */
    @Test
    public void testGetPredicate() throws Exception {
        Reporter.report("getPredicate");
        String result = instance.getPredicate();
        assertNotNull(result);
    }

    /**
     * Test of getLinksetResource method, of class LinksetVoidInformation.
     */
    @Test
    public void testGetLinksetResource() throws Exception {
        Reporter.report("getLinksetResource");
        Resource result = instance.getLinksetResource();
        assertNotNull(result);
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
        assertNotNull(result);
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
        Set results = instance.getRDF();
        assertThat (results.size(), greaterThanOrEqualTo(20));
    }
}
