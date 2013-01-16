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
package org.bridgedb.rdf;

import java.util.Set;
import org.bridgedb.tools.metadata.rdf.LinksetStatementReader;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.TestUtils;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Christian
 */
public class LinksetStatementReaderTest extends TestUtils{
    
     public static final String INFO1 = "@prefix : <#> ."
                + "@prefix void: <http://rdfs.org/ns/void#> ."
                + "@prefix dcterms: <http://purl.org/dc/terms/> ."
                + "@prefix skos: <http://www.w3.org/2004/02/skos/core#> ."
                + "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> ."
                + "@prefix foo: <http://www.foo.com/>."
                + "@prefix pav: <http://purl.org/pav/> ."
                + "@prefix dcterms: <http://purl.org/dc/terms/> ."
                + "@prefix foaf: <http://xmlns.com/foaf/0.1/> ."
                + ":TestDS1 a void:Dataset  ;"
                + "    void:uriSpace <http://www.foo.com/>."
                + ":TestDS2 a void:Dataset  ;"
                + "    void:uriSpace <http://www.example.com/>."
                + ":Test1_2 a void:Linkset  ;"
                + "    void:subjectsTarget :TestDS1 ;"
                + "    void:objectsTarget :TestDS2 ;"
                + "    void:linkPredicate <http://www.bridgedb.org/test#testPredicate> ."
                + "foo:T123 <http://www.bridgedb.org/test#testPredicate> <http://www.example.com/123> ."
                + "foo:T456 <http://www.bridgedb.org/test#testPredicate> <http://www.example.com/456> ."
                + "foo:T789 <http://www.bridgedb.org/test#testPredicate> <http://www.example.com/789> .";

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
    public void testGetVoidStatements() throws BridgeDBException {
        report("getVoidStatements");
        LinksetStatementReader instance = new LinksetStatementReader(TEST_FILE_NAME);
        Set result = instance.getVoidStatements();
        assertEquals(8, result.size());
    }

    /**
     * Test of getLinkStatements method, of class LinksetStatementReader.
     */
    @Test
    public void testGetLinkStatements() throws BridgeDBException {
        report("getLinkStatements");
        LinksetStatementReader instance = new LinksetStatementReader(TEST_FILE_NAME);
        Set result = instance.getLinkStatements();
        assertEquals(3, result.size());
    }

    /**
     * Test of getVoidStatements method, of class LinksetStatementReader.
     */
    @Test
    public void testReadString() throws BridgeDBException {
        report("getVoidStatements");
        RDFFormat format = LinksetStatementReader.getRDFFormatByMimeType("text/turtle");
        LinksetStatementReader instance = new LinksetStatementReader(INFO1, format);
        Set result = instance.getVoidStatements();
        assertEquals(8, result.size());
    }

    /**
     * Test of resetBaseURI method, of class LinksetStatementReader.
     */
    @Test
    public void testResetBaseURI() throws BridgeDBException {
        report("resetBaseURI");
        LinksetStatementReader instance = new LinksetStatementReader(TEST_FILE_NAME);
        String newBaseURI = "http://example.com/";
        instance.resetBaseURI(newBaseURI);
        Set<Statement> result = instance.getVoidStatements();
        for (Statement statement:result){
            assertTrue(statement.getSubject().stringValue().startsWith(newBaseURI));
            assertFalse(statement.getObject().stringValue().startsWith(LinksetStatementReader.DEFAULT_BASE_URI));
        }
    }
    
    @Test
    public void testResetBaseURIForResource1() throws BridgeDBException {
        report("ResetBaseURIForResource1");
        Resource old = new URIImpl (LinksetStatementReader.DEFAULT_BASE_URI + "1234");
        Resource result = LinksetStatementReader.resetBaseURI("http://example.com/", old);
        assertEquals ("http://example.com/1234", result.stringValue());     
    }

    @Test
    public void testResetBaseURIForResource2() throws BridgeDBException {
        report("ResetBaseURIForResource2");
        Resource old = new URIImpl (LinksetStatementReader.DEFAULT_BASE_URI + "#1234");
        Resource result = LinksetStatementReader.resetBaseURI("http://example.org/", old);
        assertEquals ("http://example.org/1234", result.stringValue());     
    }
}
