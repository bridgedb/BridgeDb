/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.uri;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bridgedb.uri.tools.GraphResolver;
import org.bridgedb.uri.tools.RegexUriPattern;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.Reporter;

import org.junit.jupiter.api.*;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Christian
 */
@Tag("mysql")
public class GraphResolverTest {
    
    public GraphResolverTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of knownGraphs method, of class GraphResolver.
     */
    @org.junit.jupiter.api.Test
    public void testKnownGraphs() throws Exception {
        Reporter.println("knownGraphs");
        Set result = GraphResolver.knownGraphs();
    }

    /**
     * Test of getUriPatternsForGraph method, of class GraphResolver.
     */
    @org.junit.jupiter.api.Test
    public void testGetUriPatternsForGraph() throws Exception {
        Reporter.println("getUriPatternsForGraph");
        GraphResolver.addTestMappings();
        String graph = "http://www.conceptwiki.org";
        Set<RegexUriPattern> expResult = new HashSet<RegexUriPattern>();
        expResult.addAll(RegexUriPattern.existingByPattern("http://www.conceptwiki.org/concept/$id"));
        Set result = GraphResolver.getUriPatternsForGraph(graph);
        assertEquals(expResult, result);
    }

    /**
     * Test of getAllowedUriPatterns method, of class GraphResolver.
     */
    @org.junit.jupiter.api.Test
    public void testGetAllowedUriPatterns() throws BridgeDBException {
        Reporter.println("getAllowedUriPatterns");
        GraphResolver instance = GraphResolver.getInstance();
        Map result = instance.getAllowedUriPatterns();
    }
}
