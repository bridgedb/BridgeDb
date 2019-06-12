/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.uri;

import java.util.HashSet;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.uri.api.Mapping;
import org.bridgedb.uri.lens.Lens;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

/**
 *
 * @author Christian
 */
public abstract class UriMapperTestLensTest extends UriListenerTest{

    private static  final String NULL_GRAPH = null;
    public static final Set<String> NO_PATTERNS = null;
    public static final Set<DataSource> NO_TARGET_DATASOURCE = null;
    public static final Boolean DEFAULT_IGNORE_XREF = null;

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensUri_tgtDataSources() throws Exception {
        report("MapID_sourceXref_lensUri_tgtDataSources");
        Xref sourceXref = map2xref1;
        String lensUri = Lens.TEST_LENS_NAME;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(DataSource2);
        targets.add(DataSource3);
        Set results = uriMapper.mapID(sourceXref, lensUri, targets);
        assertFalse(results.contains(map2xref1));
        assertTrue(results.contains(map2xref2));
        assertTrue(results.contains(map2xref3)); 
        assertFalse(results.contains(map1xref2));
        assertFalse(results.contains(map1xref1));
        assertFalse(results.contains(map3xref2));
        assertFalse(results.contains(map2Axref1));
        assertTrue(results.contains(map2Axref2));
        assertTrue(results.contains(map2Axref3)); 
    }

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensUri_tgtDataSource() throws Exception {
        report("MapID_sourceXref_lensUri_tgtDataSource");
        Xref sourceXref = map2xref1;
        String lensUri = Lens.TEST_LENS_NAME;
        DataSource tgtDataSource = DataSource3;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(tgtDataSource);
        Set results = uriMapper.mapID(sourceXref, lensUri, targets);
        assertFalse(results.contains(map2xref1));
        assertFalse(results.contains(map2xref2));
        assertTrue(results.contains(map2xref3));
        assertFalse(results.contains(map1xref2));
        assertFalse(results.contains(map1xref1));
        assertFalse(results.contains(map3xref2));
        assertFalse(results.contains(map2Axref1));
        assertFalse(results.contains(map2Axref2));
        assertTrue(results.contains(map2Axref3));
    }

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensUri() throws Exception {
        report("MapID_sourceXref_lensUri");
        Xref sourceXref = map2xref1;
        String lensUri = Lens.TEST_LENS_NAME;
        Set results = uriMapper.mapID(sourceXref, lensUri, NO_TARGET_DATASOURCE);
        assertTrue(results.contains(map2xref1));
        assertTrue(results.contains(map2xref2));
        assertTrue(results.contains(map2xref3));
        assertFalse(results.contains(map1xref2));
        assertFalse(results.contains(map1xref1));
        assertFalse(results.contains(map3xref2));
        assertTrue(results.contains(map2Axref1));
        assertTrue(results.contains(map2Axref2));
        assertTrue(results.contains(map2Axref3));
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensUri_tgtUriPatterns() throws Exception {
        report("MapUri_sourceUri_lensUri_tgtUriPatterns");
        String sourceUri = map3Uri3;
        String lensUri = Lens.TEST_LENS_NAME;
        UriPattern[] tgtUriPatterns = null;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern2);
        targets.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceUri, lensUri, NULL_GRAPH, targets);
        assertFalse(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        assertFalse(results.contains(map3AUri1));
        assertTrue(results.contains(map3AUri2));
        assertFalse(results.contains(map3AUri2a));
        assertTrue(results.contains(map3AUri3));
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensUri_tgtUriPattern() throws Exception {
        report("MapUri_sourceXref_lensUri_tgtUriPattern");
        Xref sourceXref = map3xref1;
        String lensUri = Lens.TEST_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceXref, lensUri, NULL_GRAPH, targets);
        System.out.println(results);
        assertFalse(results.contains(map3Uri1));
        assertFalse(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        assertFalse(results.contains(map3AUri1));
        assertFalse(results.contains(map3AUri2));
        assertFalse(results.contains(map3AUri2a));
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensUri() throws Exception {
        report("MapUri_sourceXref_lensUri");
        Xref sourceXref = map3xref1;
        String lensUri = Lens.TEST_LENS_NAME;
        Set results = uriMapper.mapUri(sourceXref, lensUri, NULL_GRAPH, NO_PATTERNS);
        assertTrue(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertTrue(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        assertTrue(results.contains(map3AUri1));
        assertTrue(results.contains(map3AUri2));
        assertTrue(results.contains(map3AUri2a));
        assertTrue(results.contains(map3AUri3));
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensUri_tgtUriPatterns() throws Exception {
        report("MapUri_sourceXref_lensUri_tgtUriPatterns");
        Xref sourceXref = map3xref1;
        String lensUri = Lens.TEST_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern2);
        targets.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceXref, lensUri, NULL_GRAPH, targets);
        assertFalse(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        assertFalse(results.contains(map3AUri1));
        assertTrue(results.contains(map3AUri2));
        assertFalse(results.contains(map3AUri2a));
        assertTrue(results.contains(map3AUri3));
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensUri_tgtUriPattern() throws Exception {
        report("MapUri_sourceUri_lensUri_tgtUriPattern");
        String sourceUri = map3Uri1;
        String lensUri = Lens.TEST_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceUri, lensUri, NULL_GRAPH, targets);
        assertFalse(results.contains(map3Uri1));
        assertFalse(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        assertFalse(results.contains(map3AUri1));
        assertFalse(results.contains(map3AUri2));
        assertFalse(results.contains(map3AUri2a));
        assertTrue(results.contains(map3AUri3));
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensUri() throws Exception {
        report("MapUri_sourceUri_lensUri");
        String sourceUri = map3Uri1;
        String lensUri = Lens.TEST_LENS_NAME;
        Set results = uriMapper.mapUri(sourceUri, lensUri, NULL_GRAPH, NO_PATTERNS);
        assertTrue(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertTrue(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        assertTrue(results.contains(map3AUri1));
        assertTrue(results.contains(map3AUri2));
        assertTrue(results.contains(map3AUri2a));
        assertTrue(results.contains(map3AUri3));
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensUri_tgtDataSources() throws Exception {
        report("MapFull_sourceXref_lensUri_tgtDataSources");
        Xref sourceXref = map3xref1;
        String lensUri = Lens.TEST_LENS_NAME;
        DataSource[] tgtDataSources = null;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(DataSource2);
        targets.add(DataSource3);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, true, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
        }
        assertThat(targetUris, not(hasItem(map3Uri1)));
        assertThat(targetUris, hasItem(map3Uri2));
        assertThat(targetUris, hasItem(map3Uri2a));
        assertThat(targetUris, hasItem(map3Uri3));
        assertThat(targetUris, not(hasItem(map2Uri2)));
        assertThat(targetUris, not(hasItem(map1Uri3)));
        assertThat(targetUris, not(hasItem(map3AUri1)));
        assertThat(targetUris, hasItem(map3AUri2));
        assertThat(targetUris, hasItem(map3AUri2a));
        assertThat(targetUris, hasItem(map3AUri3));

        assertThat(targetXrefs, not(hasItem(map3xref1)));
        assertThat(targetXrefs, hasItem(map3xref2));
        assertThat(targetXrefs, hasItem(map3xref3));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
        assertThat(targetXrefs, not(hasItem(map3Axref1)));
        assertThat(targetXrefs, hasItem(map3Axref2));
        assertThat(targetXrefs, hasItem(map3Axref3));
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensUri_tgtDataSource() throws Exception {
        report("MapFull_sourceXref_lensUri_tgtDataSources");
        Xref sourceXref = map3xref1;
        String lensUri = Lens.TEST_LENS_NAME;
        DataSource tgtDataSource = DataSource3;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(tgtDataSource);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, true, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
            assertEquals(tgtDataSource, mapping.getTarget().getDataSource());
        }
        assertThat(targetUris, not(hasItem(map3Uri1)));
        assertThat(targetUris, not(hasItem(map3Uri2)));
        assertThat(targetUris, not(hasItem(map3Uri2a)));
        assertThat(targetUris, hasItem(map3Uri3));
        assertThat(targetUris, not(hasItem(map3AUri1)));
        assertThat(targetUris, not(hasItem(map3AUri2)));
        assertThat(targetUris, not(hasItem(map3AUri2a)));
        assertThat(targetUris, hasItem(map3AUri3));
        assertThat(targetUris, not(hasItem(map2Uri2)));
        assertThat(targetUris, not(hasItem(map1Uri3)));

        assertThat(targetXrefs, not(hasItem(map3xref1)));
        assertThat(targetXrefs, not(hasItem(map3xref2)));
        assertThat(targetXrefs, hasItem(map3xref3));
        assertThat(targetXrefs, not(hasItem(map3Axref1)));
        assertThat(targetXrefs, not(hasItem(map3Axref2)));
        assertThat(targetXrefs, hasItem(map3Axref3));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensUri() throws Exception {
        report("MapFull_sourceXref_lensUri");
        Xref sourceXref = map3xref1;
        String lensUri = Lens.TEST_LENS_NAME;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, true, NO_TARGET_DATASOURCE);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
        }
        assertThat(targetUris, hasItem(map3Uri1));
        assertThat(targetUris, hasItem(map3Uri2));
        assertThat(targetUris, hasItem(map3Uri2a));
        assertThat(targetUris, hasItem(map3Uri3));
        assertThat(targetUris, not(hasItem(map2Uri2)));
        assertThat(targetUris, not(hasItem(map1Uri3)));
        assertThat(targetUris, hasItem(map3AUri1));
        assertThat(targetUris, hasItem(map3AUri2));
        assertThat(targetUris, hasItem(map3AUri2a));
        assertThat(targetUris, hasItem(map3AUri3));

        assertThat(targetXrefs, hasItem(map3xref1));
        assertThat(targetXrefs, hasItem(map3xref2));
        assertThat(targetXrefs, hasItem(map3xref3));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
        assertThat(targetXrefs, hasItem(map3Axref1));
        assertThat(targetXrefs, hasItem(map3Axref2));
        assertThat(targetXrefs, hasItem(map3Axref3));
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensUri_tgtUriPatterns() throws Exception {
        report("MapFull_sourceXref_lensUri_tgtUriPatterns");
        Xref sourceXref = map3xref1;
        String lensUri = Lens.TEST_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern2);
        targets.add(stringPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, NULL_GRAPH, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
        }
        assertThat(targetUris, not(hasItem(map3Uri1)));
        assertThat(targetUris, hasItem(map3Uri2));
        assertThat(targetUris, not(hasItem(map3Uri2a)));
        assertThat(targetUris, hasItem(map3Uri3));
        assertThat(targetUris, not(hasItem(map2Uri2)));
        assertThat(targetUris, not(hasItem(map1Uri3)));
        assertThat(targetUris, not(hasItem(map3AUri1)));
        assertThat(targetUris, hasItem(map3AUri2));
        assertThat(targetUris, not(hasItem(map3AUri2a)));
        assertThat(targetUris, hasItem(map3AUri3));

        assertThat(targetXrefs, not(hasItem(map3xref1)));
        assertThat(targetXrefs, hasItem(map3xref2));
        assertThat(targetXrefs, hasItem(map3xref3));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
        assertThat(targetXrefs, not(hasItem(map3Axref1)));
        assertThat(targetXrefs, hasItem(map3Axref2));
        assertThat(targetXrefs, hasItem(map3Axref3));
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensUri_tgtUriPattern() throws Exception {
        report("MapFull_sourceXref_lensUri_tgtUriPattern");
        Xref sourceXref = map3xref1;
        String lensUri = Lens.TEST_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, NULL_GRAPH, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
        }
        assertThat(targetUris, not(hasItem(map3Uri1)));
        assertThat(targetUris, not(hasItem(map3Uri2)));
        assertThat(targetUris, not(hasItem(map3Uri2a)));
        assertThat(targetUris, hasItem(map3Uri3));
        assertThat(targetUris, not(hasItem(map2Uri2)));
        assertThat(targetUris, not(hasItem(map1Uri3)));
        assertThat(targetUris, not(hasItem(map3AUri1)));
        assertThat(targetUris, not(hasItem(map3AUri2)));
        assertThat(targetUris, not(hasItem(map3AUri2a)));
        assertThat(targetUris, hasItem(map3AUri3));

        assertThat(targetXrefs, not(hasItem(map3xref1)));
        assertThat(targetXrefs, not(hasItem(map3xref2)));
        assertThat(targetXrefs, hasItem(map3xref3));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
        assertThat(targetXrefs, not(hasItem(map3Axref1)));
        assertThat(targetXrefs, not(hasItem(map3Axref2)));
        assertThat(targetXrefs, hasItem(map3Axref3));
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensUri_tgtDataSources() throws Exception {
        report("MapFull_sourceUri_lensUri_tgtDataSources");
        String sourceUri = map3Uri1;
        Xref sourceXref = map3xref1;
        String lensUri = Lens.TEST_LENS_NAME;
        DataSource[] tgtDataSources = null;
        Set expResult = null;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(DataSource2);
        targets.add(DataSource3);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            assertTrue(mapping.getSourceUri().contains(sourceUri));
            assertTrue(mapping.getSourceUri().size() == 1);
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
        }
        assertThat(targetUris, not(hasItem(map3Uri1)));
        assertThat(targetUris, hasItem(map3Uri2));
        assertThat(targetUris, hasItem(map3Uri2a));
        assertThat(targetUris, hasItem(map3Uri3));
        assertThat(targetUris, not(hasItem(map2Uri2)));
        assertThat(targetUris, not(hasItem(map1Uri3)));
        assertThat(targetUris, not(hasItem(map3AUri1)));
        assertThat(targetUris, hasItem(map3AUri2));
        assertThat(targetUris, hasItem(map3AUri2a));
        assertThat(targetUris, hasItem(map3AUri3));

        assertThat(targetXrefs, not(hasItem(map3xref1)));
        assertThat(targetXrefs, hasItem(map3xref2));
        assertThat(targetXrefs, hasItem(map3xref3));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
        assertThat(targetXrefs, not(hasItem(map3Axref1)));
        assertThat(targetXrefs, hasItem(map3Axref2));
        assertThat(targetXrefs, hasItem(map3Axref3));
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensUri_tgtDataSource() throws Exception {
        report("MapFull_sourceUri_lensUri_tgtDataSource");
        String sourceUri = map3Uri1;
        Xref sourceXref = map3xref1;
        String lensUri = Lens.TEST_LENS_NAME;
        DataSource tgtDataSource = DataSource3;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(tgtDataSource);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            assertTrue(mapping.getSourceUri().contains(sourceUri));
            assertTrue(mapping.getSourceUri().size() == 1);
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
        }
        assertThat(targetUris, not(hasItem(map3Uri1)));
        assertThat(targetUris, not(hasItem(map3Uri2)));
        assertThat(targetUris, not(hasItem(map3Uri2a)));
        assertThat(targetUris, hasItem(map3Uri3));
        assertThat(targetUris, not(hasItem(map2Uri2)));
        assertThat(targetUris, not(hasItem(map1Uri3)));
        assertThat(targetUris, not(hasItem(map3AUri1)));
        assertThat(targetUris, not(hasItem(map3AUri2)));
        assertThat(targetUris, not(hasItem(map3AUri2a)));
        assertThat(targetUris, hasItem(map3AUri3));

        assertThat(targetXrefs, not(hasItem(map3xref1)));
        assertThat(targetXrefs, not(hasItem(map3xref2)));
        assertThat(targetXrefs, hasItem(map3xref3));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
        assertThat(targetXrefs, not(hasItem(map3Axref1)));
        assertThat(targetXrefs, not(hasItem(map3Axref2)));
        assertThat(targetXrefs, hasItem(map3Axref3));
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_MapFull_sourceUri_lensUri() throws Exception {
        report("MapFull_sourceUri_lensUri");
        String sourceUri = map3Uri1;
        Xref sourceXref = map3xref1;
        String lensUri = Lens.TEST_LENS_NAME;
        //To set includeXrefUri we have to use the call which would included graph 
        //therefor the extra null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, true, NULL_GRAPH, NO_PATTERNS);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            assertTrue(mapping.getSourceUri().contains(sourceUri));
            assertTrue(mapping.getSourceUri().size() == 1);
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
        }
        assertThat(targetUris, hasItem(map3Uri1));
        assertThat(targetUris, hasItem(map3Uri2));
        assertThat(targetUris, hasItem(map3Uri2a));
        assertThat(targetUris, hasItem(map3Uri3));
        assertThat(targetUris, not(hasItem(map2Uri2)));
        assertThat(targetUris, not(hasItem(map1Uri3)));
        assertThat(targetUris, hasItem(map3AUri1));
        assertThat(targetUris, hasItem(map3AUri2));
        assertThat(targetUris, hasItem(map3AUri2a));
        assertThat(targetUris, hasItem(map3AUri3));

        assertThat(targetXrefs, hasItem(map3xref1));
        assertThat(targetXrefs, hasItem(map3xref2));
        assertThat(targetXrefs, hasItem(map3xref3));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
        assertThat(targetXrefs, hasItem(map3Axref1));
        assertThat(targetXrefs, hasItem(map3Axref2));
        assertThat(targetXrefs, hasItem(map3Axref3));
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensUri_tgtUriPattern() throws Exception {
        report("MapFull_sourceUri_lensUri_tgtUriPattern");
        String sourceUri = map3Uri1;
        Xref sourceXref = map3xref1;
        String lensUri = Lens.TEST_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, true, NULL_GRAPH, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            assertTrue(mapping.getSourceUri().contains(sourceUri));
            assertTrue(mapping.getSourceUri().size() == 1);
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
        }
        assertThat(targetUris, not(hasItem(map3Uri1)));
        assertThat(targetUris, not(hasItem(map3Uri2)));
        assertThat(targetUris, not(hasItem(map3Uri2a)));
        assertThat(targetUris, hasItem(map3Uri3));
        assertThat(targetUris, not(hasItem(map2Uri2)));
        assertThat(targetUris, not(hasItem(map1Uri3)));
        assertThat(targetUris, not(hasItem(map3AUri1)));
        assertThat(targetUris, not(hasItem(map3AUri2)));
        assertThat(targetUris, not(hasItem(map3AUri2a)));
        assertThat(targetUris, hasItem(map3AUri3));

        assertThat(targetXrefs, not(hasItem(map3xref1)));
        assertThat(targetXrefs, not(hasItem(map3xref2)));
        assertThat(targetXrefs, hasItem(map3xref3));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
        assertThat(targetXrefs, not(hasItem(map3Axref1)));
        assertThat(targetXrefs, not(hasItem(map3Axref2)));
        assertThat(targetXrefs, hasItem(map3Axref3));
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensUri_tgtUriPatterns() throws Exception {
        report("MapFull_sourceUri_lensUri_tgtUriPatterns");
        String sourceUri = map3Uri1;
        Xref sourceXref = map3xref1;
        String lensUri = Lens.TEST_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern2);
        targets.add(stringPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, true, NULL_GRAPH, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            assertTrue(mapping.getSourceUri().contains(sourceUri));
            assertTrue(mapping.getSourceUri().size() == 1);
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
        }
        assertThat(targetUris, not(hasItem(map3Uri1)));
        assertThat(targetUris, hasItem(map3Uri2));
        assertThat(targetUris, not(hasItem(map3Uri2a)));
        assertThat(targetUris, hasItem(map3Uri3));
        assertThat(targetUris, not(hasItem(map2Uri2)));
        assertThat(targetUris, not(hasItem(map1Uri3)));
        assertThat(targetUris, not(hasItem(map3AUri1)));
        assertThat(targetUris, hasItem(map3AUri2));
        assertThat(targetUris, not(hasItem(map3AUri2a)));
        assertThat(targetUris, hasItem(map3AUri3));

        assertThat(targetXrefs, not(hasItem(map3xref1)));
        assertThat(targetXrefs, hasItem(map3xref2));
        assertThat(targetXrefs, hasItem(map3xref3));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
        assertThat(targetXrefs, not(hasItem(map3Axref1)));
        assertThat(targetXrefs, hasItem(map3Axref2));
        assertThat(targetXrefs, hasItem(map3Axref3));
    }

}
