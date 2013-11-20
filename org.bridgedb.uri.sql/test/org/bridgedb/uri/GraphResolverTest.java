/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.uri;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.utils.BridgeDBException;
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
public class GraphResolverTest {
    
    public GraphResolverTest() {
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
     * Test of knownGraphs method, of class GraphResolver.
     */
    @Test
    public void testKnownGraphs() throws Exception {
        Reporter.println("knownGraphs");
        Set result = GraphResolver.knownGraphs();
    }

    /**
     * Test of getUriPatternsForGraph method, of class GraphResolver.
     */
    @Test
    public void testGetUriPatternsForGraph() throws Exception {
        Reporter.println("getUriPatternsForGraph");
        GraphResolver.addTestMappings();
        String graph = "http://www.conceptwiki.org";
        Set<RegexUriPattern> expResult = new HashSet<RegexUriPattern>();
        expResult.addAll(RegexUriPattern.byPattern("http://www.conceptwiki.org/concept/$id"));
        Set result = GraphResolver.getUriPatternsForGraph(graph);
        assertEquals(expResult, result);
    }

    /**
     * Test of getAllowedUriPatterns method, of class GraphResolver.
     */
    @Test
    public void testGetAllowedUriPatterns() throws BridgeDBException {
        Reporter.println("getAllowedUriPatterns");
        GraphResolver instance = GraphResolver.getInstance();
        Map result = instance.getAllowedUriPatterns();
    }
}
