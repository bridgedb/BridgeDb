/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.uri;

import java.util.HashSet;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.bridgedb.sql.SQLUriMapper;
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
public abstract class UriMapperNullTargetTest extends UriListenerTest{

    private static  final String NULL_GRAPH = null;
    private static final Boolean DEFAULT_IGNORE = null;
    private static final Set<DataSource> NO_TARGET_DATA_SOURCES = null;
    private static final Set<String> NO_PATTERNS = null;

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensId_tgtDataSources_first_null() throws Exception {
        report("MapID_sourceXref_lensId_tgtDataSources_first_null");
        Xref sourceXref = map2xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(null);
        targets.add(DataSource3);
        Set<Xref> results = uriMapper.mapID(sourceXref, lensId, targets);
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
    public void testMapID_sourceXref_lensId_tgtDataSources_second_null() throws Exception {
        report("MapID_sourceXref_lensId_tgtDataSources_second_null");
        Xref sourceXref = map2xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(DataSource2);
        targets.add(null);
        Set<Xref> results = uriMapper.mapID(sourceXref, lensId, targets);
        assertFalse(results.contains(map2xref1));
        assertTrue(results.contains(map2xref2));
        assertFalse(results.contains(map2xref3));
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
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> tgtDataSources = new HashSet<DataSource>();
        tgtDataSources.add(null);
        Set results = uriMapper.mapID(sourceXref, lensId, tgtDataSources);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensId_null_array() throws Exception {
        report("MapID_sourceXref_lensId_null_array");
        Xref sourceXref = map2xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
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
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensId_empty_array() throws Exception {
        report("MapID_sourceXref_lensId_empty_array");
        Xref sourceXref = map2xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> targets = new HashSet<DataSource>();
        Set results = uriMapper.mapID(sourceXref, lensId, targets );
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
    public void testMapUri_sourceUri_lensId_tgtUriPatterns_first_null() throws Exception {
        report("MapUri_sourceUri_lensId_tgtUriPatterns_first_null");
        String sourceUri = map3Uri3;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> tgtUriPatterns = new HashSet<String>();
        tgtUriPatterns.add(null);
        tgtUriPatterns.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceUri, lensId, NULL_GRAPH, tgtUriPatterns);
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
    public void testMapUri_sourceUri_lensId_tgtUriPatterns_second_null() throws Exception {
        report("MapUri_sourceUri_lensId_tgtUriPatterns_second_null");
        String sourceUri = map3Uri3;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> tgtUriPatterns = new HashSet<String>();
        tgtUriPatterns.add(stringPattern2);
        tgtUriPatterns.add(null);
        Set results = uriMapper.mapUri(sourceUri, lensId, NULL_GRAPH, tgtUriPatterns);
        assertFalse(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertFalse(results.contains(map3Uri3));
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
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(null);
        Set results = uriMapper.mapUri(sourceXref, lensId, NULL_GRAPH, targets);
        assertTrue(results.isEmpty());
     }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensId_null_pattern() throws Exception {
        report("MapUri_sourceXref_lensId_null_pattern");
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = null;
        Set results = uriMapper.mapUri(sourceXref, lensId, NULL_GRAPH, targets);
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
    public void testMapUri_sourceXref_lensId_empty_pattern() throws Exception {
        report("MapUri_sourceXref_lensId");
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        Set results = uriMapper.mapUri(sourceXref, lensId, NULL_GRAPH, targets);
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
    public void testMapUri_sourceXref_lensId_tgtUriPatterns_second__null() throws Exception {
        report("MapUri_sourceXref_lensId_tgtUriPatterns_second_null");
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> tgtUriPatterns = new HashSet<String>();
        tgtUriPatterns.add(stringPattern2);
        tgtUriPatterns.add(null);
        Set results = uriMapper.mapUri(sourceXref, lensId, NULL_GRAPH, tgtUriPatterns);
        assertFalse(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertFalse(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        checkForNoOtherlensId(results);
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensId_tgtUriPatterns_first_null() throws Exception {
        report("MapUri_sourceXref_lensId_tgtUriPatterns_first_null");
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> tgtUriPatterns = new HashSet<String>();
        tgtUriPatterns.add(null);
        tgtUriPatterns.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceXref, lensId, NULL_GRAPH, tgtUriPatterns);
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
    public void testMapUri_sourceUri_lensId_tgtUriPattern() throws Exception {
        report("MapUri_sourceUri_lensId_tgtUriPattern");
        String sourceUri = map3Uri2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> tgtUriPattern = new HashSet<String>();
        tgtUriPattern.add(null);
        Set results = uriMapper.mapUri(sourceUri, lensId, NULL_GRAPH, tgtUriPattern);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensId_null_target() throws Exception {
        report("MapUri_sourceUri_lensId_null_target");
        String sourceUri = map3Uri2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = null;
        Set results = uriMapper.mapUri(sourceUri, lensId, NULL_GRAPH, targets);
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
    public void testMapUri_sourceUri_lensId_empty_targets() throws Exception {
        report("MapUri_sourceUri_lensId_empty_targets");
        String sourceUri = map3Uri2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        Set results = uriMapper.mapUri(sourceUri, lensId, NULL_GRAPH, targets);
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
    public void testMapFull_sourceXref_lensId_tgtDataSources_first_null() throws Exception {
        report("MapFull_sourceXref_lensId_tgtDataSources_first_null");
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(null);
        targets.add(DataSource3);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, true, targets);
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
    public void testMapFull_sourceXref_lensId_tgtDataSources_second_null() throws Exception {
        report("MapFull_sourceXref_lensId_tgtDataSources_second_null");
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(DataSource2);
        targets.add(null);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, true, targets);
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
        assertThat(targetUris, not(hasItem(map3Uri3)));
        assertThat(targetUris, not(hasItem(map2Uri2)));
        assertThat(targetUris, not(hasItem(map1Uri3)));
        checkForNoOtherlensId(targetUris);

        assertThat(targetXrefs, not(hasItem(map3xref1)));
        assertThat(targetXrefs, hasItem(map3xref2));
        assertThat(targetXrefs, not(hasItem(map3xref3)));
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
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> tgtDataSource = new HashSet<DataSource>();
        tgtDataSource.add(null);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, DEFAULT_IGNORE, tgtDataSource);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_null_tgtDataSources() throws Exception {
        report("MapFull_sourceXref_lensId_null_tgtDataSources");
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> targets = null;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, true, targets);
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
    public void testMapFull_sourceXref_lensId_empty_tgtDataSources() throws Exception {
        report("MapFull_sourceXref_lensId_empty_tgtDataSources");
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> targets = new HashSet<DataSource>();
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, true, targets);
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
    public void testMapFull_sourceXref_lensId_null_UriPatterns() throws Exception {
        report("MapFull_sourceXref_lensId_null_UriPatterns");
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = null;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, NULL_GRAPH, targets);
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
    public void testMapFull_sourceXref_lensId_empty_UriPatterns() throws Exception {
        report("MapFull_sourceXref_lensId_empty_UriPatterns");
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, NULL_GRAPH, targets);
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
    public void testMapFull_sourceXref_lensId_tgtUriPatterns_first_null() throws Exception {
        report("MapFull_sourceXref_lensId_tgtUriPatterns_first_null");
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> tgtUriPatterns = new HashSet<String>();
        tgtUriPatterns.add(null);
        tgtUriPatterns.add(stringPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, NULL_GRAPH, tgtUriPatterns);
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
    public void testMapFull_sourceXref_lensId_tgtUriPatterns_second_null() throws Exception {
        report("MapFull_sourceXref_lensId_tgtUriPatterns_second_null");
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> tgtUriPatterns = new HashSet<String>();
        tgtUriPatterns.add(stringPattern2);
        tgtUriPatterns.add(null);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, NULL_GRAPH, tgtUriPatterns);
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
        assertThat(targetUris, not(hasItem(map3Uri3)));
        assertThat(targetUris, not(hasItem(map2Uri2)));
        assertThat(targetUris, not(hasItem(map1Uri3)));
        checkForNoOtherlensId(targetUris);

        assertThat(targetXrefs, not(hasItem(map3xref1)));
        assertThat(targetXrefs, hasItem(map3xref2));
        assertThat(targetXrefs, not(hasItem(map3xref3)));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_tgtUriPattern() throws Exception {
        //No way to pass an empty set via webservices
        org.junit.Assume.assumeTrue(uriMapper instanceof SQLUriMapper); 
        report("MapFull_sourceXref_lensId_tgtUriPattern");
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> tgtUriPatterns = new HashSet<String>();
        tgtUriPatterns.add(null);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, NULL_GRAPH, tgtUriPatterns);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensId_tgtDataSources_first_null() throws Exception {
        report("MapFull_sourceUri_lensId_tgtDataSources_first_null");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(null);
        targets.add(DataSource3);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, targets);
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
    public void testMapFull_sourceUri_lensId_tgtDataSources_second_null() throws Exception {
        report("MapFull_sourceUri_lensId_tgtDataSources_second_null");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(DataSource2);
        targets.add(null);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, targets);
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
        assertThat(targetUris, not(hasItem(map3Uri3)));
        assertThat(targetUris, not(hasItem(map2Uri2)));
        assertThat(targetUris, not(hasItem(map1Uri3)));
        checkForNoOtherlensId(targetUris);

        assertThat(targetXrefs, not(hasItem(map3xref1)));
        assertThat(targetXrefs, hasItem(map3xref2));
        assertThat(targetXrefs, not(hasItem(map3xref3)));
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
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(null);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, targets);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_MapFull_sourceUri_lensId_null_DataSources() throws Exception {
        report("MapFull_sourceUri_lensId_null_DataSources");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> targets = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, targets);
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
    public void testMapFull_MapFull_sourceUri_lensId_empty_dataSources() throws Exception {
        report("MapFull_sourceUri_lensId_empty_dataSources");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> targets = new HashSet<DataSource>();
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        for (Mapping mapping:results){
            if (uriMapper instanceof SQLUriMapper){
                //Skip this tes for Webservice is it would need IncludedXrefResults set to true 
                //Which this call does not allow
                assertEquals(sourceXref, mapping.getSource());
                if (!mapping.getTarget().equals(sourceXref)){
                    assertThat(mapping.getPredicate(), not(equalTo(null)));            
                    assertThat(mapping.getMappingSetId(), not(equalTo(null)));
                }
                targetXrefs.add(mapping.getTarget());
           }
            assertTrue(mapping.getSourceUri().contains(sourceUri));
            assertTrue(mapping.getSourceUri().size() == 1);
            targetUris.addAll(mapping.getTargetUri());
        }
        assertThat(targetUris, hasItem(map3Uri1));
        assertThat(targetUris, hasItem(map3Uri2));
        assertThat(targetUris, hasItem(map3Uri2a));
        assertThat(targetUris, hasItem(map3Uri3));
        assertThat(targetUris, not(hasItem(map2Uri2)));
        assertThat(targetUris, not(hasItem(map1Uri3)));
        checkForNoOtherlensId(targetUris);

        //Skip this tes for Webservice is it would need IncludedXrefResults set to true 
        //Which this call does not allow
        if (uriMapper instanceof SQLUriMapper){
            assertThat(targetXrefs, hasItem(map3xref1));
            assertThat(targetXrefs, hasItem(map3xref2));
            assertThat(targetXrefs, hasItem(map3xref3));
            assertThat(targetXrefs, not(hasItem(map1xref2)));
            assertThat(targetXrefs, not(hasItem(map1xref1)));
            assertThat(targetXrefs, not(hasItem(map2xref2)));   
        }
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_MapFull_sourceUri_lensId_null_patterns() throws Exception {
        report("MapFull_sourceUri_lensId_null_patterns");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, true, NULL_GRAPH, targets);
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
    public void testMapFull_MapFull_sourceUri_lensId_null_patterns_noXrefs() throws Exception {
        report("MapFull_sourceUri_lensId_null_patterns_noXrefs");
        String sourceUri = map3Uri2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, false, NULL_GRAPH, targets);
        for (Mapping mapping:results){
            assertTrue(mapping.getSource() == null);
            assertTrue(mapping.getTarget() == null);
        }
    }
    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_MapFull_sourceUri_lensId_null_patterns_noXrefs_default() throws Exception {
        report("MapFull_sourceUri_lensId_null_patterns_default");
        String sourceUri = map3Uri2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, DEFAULT_IGNORE, NULL_GRAPH, targets);
        for (Mapping mapping:results){
            assertTrue(mapping.getSource() == null);
            assertTrue(mapping.getTarget() == null);
        }
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_MapFull_sourceUri_lensId_empty_patterns() throws Exception {
        report("MapFull_sourceUri_lensId_empty_patterns");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, true, NULL_GRAPH, targets);
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
        //No way to pass an empty set via webservices
        org.junit.Assume.assumeTrue(uriMapper instanceof SQLUriMapper); 
        report("MapFull_sourceUri_lensId_tgtUriPattern");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(null);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, DEFAULT_IGNORE, NULL_GRAPH, targets);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensId_tgtUriPatterns_first_null() throws Exception {
        report("MapFull_sourceUri_lensId_tgtUriPatterns_first_null");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> tgtUriPatterns = new HashSet<String>();
        tgtUriPatterns.add(null);
        tgtUriPatterns.add(stringPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, true, NULL_GRAPH, tgtUriPatterns);
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
    public void testMapFull_sourceUri_lensId_tgtUriPatterns_second_null() throws Exception {
        report("MapFull_sourceUri_lensId_tgtUriPatterns_second_null");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> tgtUriPatterns = new HashSet<String>();
        tgtUriPatterns.add(stringPattern2);
        tgtUriPatterns.add(null);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, true, NULL_GRAPH, tgtUriPatterns);
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
        assertThat(targetUris, not(hasItem(map3Uri3)));
        assertThat(targetUris, not(hasItem(map2Uri2)));
        assertThat(targetUris, not(hasItem(map1Uri3)));
        checkForNoOtherlensId(targetUris);

        assertThat(targetXrefs, not(hasItem(map3xref1)));
        assertThat(targetXrefs, hasItem(map3xref2));
        assertThat(targetXrefs, not(hasItem(map3xref3)));
        assertThat(targetXrefs, not(hasItem(map1xref2)));
        assertThat(targetXrefs, not(hasItem(map1xref1)));
        assertThat(targetXrefs, not(hasItem(map2xref2)));   
    }

}
