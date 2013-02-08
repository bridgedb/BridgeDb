// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.tools.metadata;

import java.util.Set;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.rdf.constants.VoidConstants;
import org.bridgedb.tools.metadata.constants.DctermsConstants;
import org.bridgedb.tools.metadata.rdf.LinksetStatementReader;
import org.bridgedb.tools.metadata.rdf.LinksetStatements;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.TestUtils;
import static org.hamcrest.Matchers.*;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class LinksetVoidInformationTest extends TestUtils{
    
    private static LinksetVoidInformation instance;
    
    public LinksetVoidInformationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        LinksetStatements statements = new LinksetStatementReader(FileTest.LINK_FILE);
        instance = new LinksetVoidInformation(FileTest.LINK_FILE, statements, ValidationType.LINKS);
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
    public void testGetSubjectUriPattern() throws Exception {
        report("getSubjectUriSpace");
        UriPattern result = instance.getSubjectUriPattern();
        assertEquals("http://data.kasabi.com/dataset/chembl-rdf/$id", result.toString());
    }

    /**
     * Test of getTargetUriSpace method, of class LinksetVoidInformation.
     */
    @Test
    public void testGetTargetUriSpace() throws Exception {
        report("getTargetUriSpace");
        UriPattern result = instance.getTargetUriPattern();
        assertEquals("http://rdf.chemspider.com/$id", result.toString());
    }

    /**
     * Test of getPredicate method, of class LinksetVoidInformation.
     */
    @Test
    public void testGetPredicate() throws Exception {
        report("getPredicate");
        String result = instance.getPredicate();
        assertEquals("http://www.w3.org/2004/02/skos/core#exactMatch", result);
    }

    /**
     * Test of getLinksetResource method, of class LinksetVoidInformation.
     */
    @Test
    public void testGetLinksetResource() throws Exception {
        report("getLinksetResource");
        Resource result = instance.getLinksetResource();
        Resource expected = new URIImpl("http://data.kasabi.com/dataset/chembl-rdf/void.ttl/chembl-rdf-compounds_cs_linkset");
        assertEquals(expected, result);
    }

    /**
     * Test of isTransative method, of class LinksetVoidInformation.
     */
    @Test
    public void testIsTransative() {
        report("isTransative");
        boolean result = instance.isTransative();
        assertFalse(result);
    }

    /**
     * Test of Schema method, of class LinksetVoidInformation.
     */
    @Test
    public void testSchema() {
        report("Schema");
        String result = instance.Schema();
        assertNotNull(result);
    }

    /**
     * Test of hasRequiredValues method, of class LinksetVoidInformation.
     */
    @Test
    public void testHasRequiredValues() {
        report("hasRequiredValues");
        boolean result = instance.hasRequiredValues();
        assertTrue(result);
    }

    /**
     * Test of hasCorrectTypes method, of class LinksetVoidInformation.
     */
    @Test
    public void testHasCorrectTypes() throws BridgeDBException {
        report("hasCorrectTypes");
        boolean result = instance.hasCorrectTypes();
        assertTrue(result);
    }

    /**
     * Test of validityReport method, of class LinksetVoidInformation.
     */
    @Test
    public void testValidityReport() throws BridgeDBException {
        report("validityReport");
        boolean includeWarnings = false;
        String result = instance.validityReport(includeWarnings);
        assertThat(result, not(containsString("ERROR")));
    }

     /**
     * Test of getValuesByPredicate method, of class LinksetVoidInformation.
     */
    @Test
    public void testGetValuesByPredicate() {
        report("getValuesByPredicate");
        Set results = instance.getValuesByPredicate(DctermsConstants.TITLE);
        assertThat (results.size(), greaterThanOrEqualTo(3));
    }

    /**
     * Test of getResoucresByPredicate method, of class LinksetVoidInformation.
     */
    @Test
    public void testGetResoucresByPredicate() {
        report("getResoucresByPredicate");
        Set results = instance.getResoucresByPredicate(VoidConstants.SUBJECTSTARGET);
        assertThat (results.size(), greaterThanOrEqualTo(1));
    }

    /**
     * Test of getRDF method, of class LinksetVoidInformation.
     */
    @Test
    public void testGetRDF() {
        report("getRDF");
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
