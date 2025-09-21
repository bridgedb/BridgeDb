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
import org.bridgedb.uri.tools.GraphResolver;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Christian
 */
@Tag("mysql")
public abstract class UriMapperNullLensTest extends UriListenerTest{

    private static final String EMPTY_GRAPH = "";
    private static final Boolean DEFAULT_IGNORE = null;
    private static final Set<DataSource> NO_TARGET_DATA_SOURCES = null;
    private static final Set<String> NO_PATTERNS = null;
    
    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensId_tgtDataSources() throws Exception {
        report("MapID_sourceXref_lensId_tgtDataSources");
        Xref sourceXref = map2xref2;
        String lensId = null;
        Set<DataSource> tgtDataSources = new HashSet<DataSource>();
        tgtDataSources.add(DataSource2);
        tgtDataSources.add(DataSource3);
        Set results = uriMapper.mapID(sourceXref, lensId, tgtDataSources);
        assertFalse(results.contains(map2xref1));
        assertTrue(results.contains(map2xref2));
        assertTrue(results.contains(map2xref3));
        assertFalse(results.contains(map1xref2));
        assertFalse(results.contains(map1xref1));
        assertFalse(results.contains(map3xref2));
        checkForNoOtherLensXrefs(results);
    }

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensId_tgtDataSource() throws Exception {
        report("MapID_sourceXref_lensId_tgtDataSource");
        Xref sourceXref = map2xref2;
        String lensId = null;
        Set<DataSource> tgtDataSources = new HashSet<DataSource>();
        tgtDataSources.add(DataSource3);
        Set results = uriMapper.mapID(sourceXref, lensId, tgtDataSources);
        assertFalse(results.contains(map2xref1));
        assertFalse(results.contains(map2xref2));
        assertTrue(results.contains(map2xref3));
        assertFalse(results.contains(map1xref2));
        assertFalse(results.contains(map1xref1));
        assertFalse(results.contains(map3xref2));
        checkForNoOtherLensXrefs(results);
    }

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensId() throws Exception {
        report("MapID_sourceXref_lensId");
        Xref sourceXref = map2xref2;
        String lensId = null;
        Set results = uriMapper.mapID(sourceXref, lensId, NO_TARGET_DATA_SOURCES);
        assertTrue(results.contains(map2xref1));
        assertTrue(results.contains(map2xref2));
        assertTrue(results.contains(map2xref3));
        assertFalse(results.contains(map1xref2));
        assertFalse(results.contains(map1xref1));
        assertFalse(results.contains(map3xref2));
        checkForNoOtherLensXrefs(results);
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensId_tgtUriPatterns() throws Exception {
        report("MapUri_sourceUri_lensId_tgtUriPatterns");
        String sourceUri = map3Uri3;
        String lensId = null;
        Set<String> tgtUriPatterns = new HashSet<String>();
        tgtUriPatterns.add(stringPattern2);
        tgtUriPatterns.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceUri, lensId, EMPTY_GRAPH, tgtUriPatterns);
        assertFalse(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        checkForNoOtherlensId(results);
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensId_tgtUriPattern() throws Exception {
        report("MapUri_sourceXref_lensId_tgtUriPattern");
        Xref sourceXref = map3xref2;
        String lensId = null;
        Set<String> tgtUriPatterns = new HashSet<String>();
        tgtUriPatterns.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceXref, lensId, EMPTY_GRAPH, tgtUriPatterns);
        assertFalse(results.contains(map3Uri1));
        assertFalse(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        checkForNoOtherlensId(results);
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensId() throws Exception {
        report("MapUri_sourceXref_lensId");
        Xref sourceXref = map3xref2;
        String lensId = null;
        Set results = uriMapper.mapUri(sourceXref, lensId, EMPTY_GRAPH, NO_PATTERNS);
        assertTrue(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertTrue(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        checkForNoOtherlensId(results);
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensId_tgtUriPatterns() throws Exception {
        report("MapUri_sourceXref_lensId_tgtUriPatterns");
        Xref sourceXref = map3xref2;
        String lensId = null;
        Set<String> tgtUriPatterns = new HashSet<String>();
        tgtUriPatterns.add(stringPattern2);
        tgtUriPatterns.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceXref, lensId, EMPTY_GRAPH, tgtUriPatterns);
        assertFalse(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        checkForNoOtherlensId(results);
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensId_tgtUriPattern() throws Exception {
        report("MapUri_sourceUri_lensId_tgtUriPattern");
        String sourceUri = map3Uri2;
        String lensId = null;
        Set<String> tgtUriPatterns = new HashSet<String>();
        tgtUriPatterns.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceUri, lensId, EMPTY_GRAPH, tgtUriPatterns);
        assertFalse(results.contains(map3Uri1));
        assertFalse(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        checkForNoOtherlensId(results);
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensId() throws Exception {
        report("MapUri_sourceUri_lensId");
        String sourceUri = map3Uri2;
        String lensId = null;
        Set results = uriMapper.mapUri(sourceUri, lensId, EMPTY_GRAPH, NO_PATTERNS);
        assertTrue(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertTrue(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        checkForNoOtherlensId(results);
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_tgtDataSources() throws Exception {
        report("MapFull_sourceXref_lensId_tgtDataSources");
        Xref sourceXref = map3xref2;
        String lensId = null;
        Set<DataSource> tgtDataSources = new HashSet<DataSource>();
        tgtDataSources.add(DataSource2);
        tgtDataSources.add(DataSource3);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, true, tgtDataSources);
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
        checkForNoOtherlensId(targetUris);

        assertThat(targetXrefs, not(hasItem(map3xref1)));
        assertThat(targetXrefs, hasItem(map3xref2));
        assertThat(targetXrefs, hasItem(map3xref3));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_tgtDataSource() throws Exception {
        report("MapFull_sourceXref_lensId_tgtDataSources");
        Xref sourceXref = map3xref2;
        String lensId = null;
        Set<DataSource> tgtDataSources = new HashSet<DataSource>();
        tgtDataSources.add(DataSource3);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, true, tgtDataSources);
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
            assertEquals(DataSource3, mapping.getTarget().getDataSource());
        }
        assertThat(targetUris, not(hasItem(map3Uri1)));
        assertThat(targetUris, not(hasItem(map3Uri2)));
        assertThat(targetUris, not(hasItem(map3Uri2a)));
        assertThat(targetUris, hasItem(map3Uri3));
        assertThat(targetUris, not(hasItem(map2Uri2)));
        assertThat(targetUris, not(hasItem(map1Uri3)));
        checkForNoOtherlensId(targetUris);

        assertThat(targetXrefs, not(hasItem(map3xref1)));
        assertThat(targetXrefs, not(hasItem(map3xref2)));
        assertThat(targetXrefs, hasItem(map3xref3));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId() throws Exception {
        report("MapFull_sourceXref_lensId_tgtDataSources");
        Xref sourceXref = map3xref2;
        String lensId = null;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, true, NO_TARGET_DATA_SOURCES);
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
        assertThat(targetUris, hasItem(map3Uri1));
        assertThat(targetUris, hasItem(map3Uri2));
        assertThat(targetUris, hasItem(map3Uri2a));
        assertThat(targetUris, hasItem(map3Uri3));
        assertThat(targetUris, not(hasItem(map2Uri2)));
        assertThat(targetUris, not(hasItem(map1Uri3)));
        checkForNoOtherlensId(targetUris);

        assertThat(targetXrefs, hasItem(map3xref1));
        assertThat(targetXrefs, hasItem(map3xref2));
        assertThat(targetXrefs, hasItem(map3xref3));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_tgtUriPatterns() throws Exception {
        report("MapFull_sourceXref_lensId_tgtUriPatterns");
        Xref sourceXref = map3xref2;
        String lensId = null;
        Set<String> tgtUriPatterns = new HashSet<String>();
        tgtUriPatterns.add(stringPattern2);
        tgtUriPatterns.add(stringPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, EMPTY_GRAPH, tgtUriPatterns);
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
        checkForNoOtherlensId(targetUris);

        assertThat(targetXrefs, not(hasItem(map3xref1)));
        assertThat(targetXrefs, hasItem(map3xref2));
        assertThat(targetXrefs, hasItem(map3xref3));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_tgtUriPattern() throws Exception {
        report("MapFull_sourceXref_lensId_tgtUriPattern");
        Xref sourceXref = map3xref2;
        String lensId = null;
        Set<String> tgtUriPatterns = new HashSet<String>();
        tgtUriPatterns.add(stringPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, EMPTY_GRAPH, tgtUriPatterns);
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
        assertThat(targetUris, not(hasItem(map3Uri2)));
        assertThat(targetUris, not(hasItem(map3Uri2a)));
        assertThat(targetUris, hasItem(map3Uri3));
        assertThat(targetUris, not(hasItem(map2Uri2)));
        assertThat(targetUris, not(hasItem(map1Uri3)));
        checkForNoOtherlensId(targetUris);

        assertThat(targetXrefs, not(hasItem(map3xref1)));
        assertThat(targetXrefs, not(hasItem(map3xref2)));
        assertThat(targetXrefs, hasItem(map3xref3));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensId_tgtDataSources() throws Exception {
        report("MapFull_sourceUri_lensId_tgtDataSources");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = null;
        Set expResult = null;
        Set<DataSource> tgtDataSources = new HashSet<DataSource>();
        tgtDataSources.add(DataSource2);
        tgtDataSources.add(DataSource3);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, tgtDataSources);
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
        checkForNoOtherlensId(targetUris);

        assertThat(targetXrefs, not(hasItem(map3xref1)));
        assertThat(targetXrefs, hasItem(map3xref2));
        assertThat(targetXrefs, hasItem(map3xref3));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensId_tgtDataSource() throws Exception {
        report("MapFull_sourceUri_lensId_tgtDataSource");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = null;
        DataSource tgtDataSource = DataSource3;
        Set<DataSource> tgtDataSources = new HashSet<DataSource>();
        tgtDataSources.add(tgtDataSource);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, tgtDataSources);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
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
        checkForNoOtherlensId(targetUris);

        assertThat(targetXrefs, not(hasItem(map3xref1)));
        assertThat(targetXrefs, not(hasItem(map3xref2)));
        assertThat(targetXrefs, hasItem(map3xref3));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_MapFull_sourceUri_lensId() throws Exception {
        report("MapFull_sourceUri_lensId");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, NO_TARGET_DATA_SOURCES);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
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
        checkForNoOtherlensId(targetUris);

        assertThat(targetXrefs, hasItem(map3xref1));
        assertThat(targetXrefs, hasItem(map3xref2));
        assertThat(targetXrefs, hasItem(map3xref3));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensId_tgtUriPattern() throws Exception {
        report("MapFull_sourceUri_lensId_tgtUriPattern");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = null;
        Set<String> tgtUriPatterns = new HashSet<String>();
        tgtUriPatterns.add(stringPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, true, EMPTY_GRAPH, tgtUriPatterns);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
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
        checkForNoOtherlensId(targetUris);

        assertThat(targetXrefs, not(hasItem(map3xref1)));
        assertThat(targetXrefs, not(hasItem(map3xref2)));
        assertThat(targetXrefs, hasItem(map3xref3));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensId_graph() throws Exception {
        report("MapFull_sourceUri_lensId_graph");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = null;
        String graph = "MapFull_sourceUri_lensId_graph";
        GraphResolver.addMapping(graph, uriPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, true, graph, NO_PATTERNS);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
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
        checkForNoOtherlensId(targetUris);

        assertThat(targetXrefs, not(hasItem(map3xref1)));
        assertThat(targetXrefs, not(hasItem(map3xref2)));
        assertThat(targetXrefs, hasItem(map3xref3));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensId_tgtUriPatterns() throws Exception {
        report("MapFull_sourceUri_lensId_tgtUriPatterns");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = null;
        Set<String> tgtUriPatterns = new HashSet<String>();
        tgtUriPatterns.add(stringPattern2);
        tgtUriPatterns.add(stringPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, true, EMPTY_GRAPH, tgtUriPatterns);
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
        checkForNoOtherlensId(targetUris);

        assertThat(targetXrefs, not(hasItem(map3xref1)));
        assertThat(targetXrefs, hasItem(map3xref2));
        assertThat(targetXrefs, hasItem(map3xref3));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
    }
}
