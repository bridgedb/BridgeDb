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
import static org.bridgedb.uri.UriListenerTest.mapBadUri1;
import org.bridgedb.uri.api.Mapping;
import org.bridgedb.uri.api.MappingsBySet;
import org.bridgedb.uri.lens.Lens;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

/**
 *
 * @author Christian
 */
public abstract class UriMapperSimpleTest extends UriListenerTest{

    private static  final String NULL_GRAPH = null;
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
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(DataSource2);
        targets.add(DataSource3);
        Set<Xref> results = uriMapper.mapID(sourceXref, lensId, targets);
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
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(DataSource3);
        Set results = uriMapper.mapID(sourceXref, lensId, targets);
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
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensId_tgtUriPatterns() throws Exception {
        report("MapUri_sourceUri_lensId_tgtUriPatterns");
        String sourceUri = map3Uri3;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern2);
        targets.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceUri, lensId, NULL_GRAPH, targets);
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
    public void testMapBySet_sourceUri_lensId_tgtUriPatterns() throws Exception {
        report("MapUri_sourceUri_lensId_tgtUriPatterns");
        String sourceUri = map3Uri3;
        Set<String> sourceUris = new HashSet<String>();
        sourceUris.add(sourceUri);
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern2);
        targets.add(stringPattern3);
        MappingsBySet lensMapping = uriMapper.mapBySet(sourceUris, lensId, NULL_GRAPH, targets);
        Set<String> results = lensMapping.getTargetUris();
        assertFalse(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        checkForNoOtherlensId(results);
    }

    @Test
    public void testMapBySet_sourceUris_lensId_tgtUriPatterns() throws Exception {
        report("MapBySet_sourceUris_lensId_tgtUriPatterns");
        Set<String> sourceUris = new HashSet<String>();
        sourceUris.add(map3Uri3);
        sourceUris.add(map1Uri1);
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern2);
        targets.add(stringPattern3);
        MappingsBySet lensMapping = uriMapper.mapBySet(sourceUris, lensId, NULL_GRAPH, targets);
        Set<String> results = lensMapping.getTargetUris();
        assertFalse(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertTrue(results.contains(map1Uri3));
        checkForNoOtherlensId(results);
    }
    
    @Test
    public void testMapBySet_sourceUrisB_lensId_tgtUriPatterns1() throws Exception {
        report("MapBySet_sourceUrisB_lensId_tgtUriPatterns1");
        Set<String> sourceUris = new HashSet<String>();
        sourceUris.add(map3Uri3);
        sourceUris.add(map3Uri1);
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern2);
        targets.add(stringPattern3);
        MappingsBySet lensMapping = uriMapper.mapBySet(sourceUris, lensId, NULL_GRAPH, targets);
        Set<String> results = lensMapping.getTargetUris();
        assertFalse(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        checkForNoOtherlensId(results);
    }

    @Test
    public void testMapBySet_sourceUrisB_lensId_tgtUriPatterns2() throws Exception {
        report("MapBySet_sourceUrisB_lensId_tgtUriPatterns2");
        Set<String> sourceUris = new HashSet<String>();
        sourceUris.add(map3Uri3);
        sourceUris.add(map3Uri1);
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern2);
        targets.add(badUriPrefix+"$id");
        targets.add(stringPattern3);
        targets.add(badUriPrefix+"$id" + ".html");
        MappingsBySet lensMapping = uriMapper.mapBySet(sourceUris, lensId, NULL_GRAPH, targets);
        targets.add(stringPattern2);
        Set<String> results = lensMapping.getTargetUris();
        assertFalse(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        checkForNoOtherlensId(results);
    }

    @Test
    public void testMapBySet_badUriMapNoPattern() throws Exception {
        report("MapBySet_badUriMapNoPattern");
        Set<String> sourceUris = new HashSet<String>();
        sourceUris.add(mapBadUri1);
        String lensId = Lens.DEFAULT_LENS_NAME;
        MappingsBySet lensMapping = uriMapper.mapBySet(sourceUris, lensId, NULL_GRAPH, NO_PATTERNS);
        Set<String> results = lensMapping.getTargetUris();
        assertThat(results, hasItem(mapBadUri1));
        assertEquals(1, results.size());
    }
    
    @Test
    public void testMapBySet_badUriMapWithMatchingPattern1() throws Exception {
        report("MapBySet_badUriMapWithMatchingPattern1");
        Set<String> sourceUris = new HashSet<String>();
        sourceUris.add(mapBadUri1);
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(badUriPrefix+"$id");
        MappingsBySet lensMapping = uriMapper.mapBySet(sourceUris, lensId, NULL_GRAPH, targets);
        Set<String> results = lensMapping.getTargetUris();
        assertThat(results, hasItem(mapBadUri1));
        assertEquals(1, results.size());
    }
    
    @Test
    public void testMapBySet_badUriMapWithMatchingPattern2() throws Exception {
        report("MapBySet_badUriMapWithMatchingPattern2");
        Set<String> sourceUris = new HashSet<String>();
        sourceUris.add(mapBadUri1);
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(badUriPrefix+"$id");
        targets.add(stringPattern3);
        MappingsBySet lensMapping = uriMapper.mapBySet(sourceUris, lensId, NULL_GRAPH, targets);
        Set<String> results = lensMapping.getTargetUris();
        assertEquals(1, results.size());
    }

    @Test
    public void testMapBySet_badUriMapNoMatchPattern() throws Exception {
        report("MapBySet_badUriMapNoMatchPattern");
        Set<String> sourceUris = new HashSet<String>();
        sourceUris.add(mapBadUri1);
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern2);
        targets.add(stringPattern2);
        MappingsBySet lensMapping = uriMapper.mapBySet(sourceUris, lensId, NULL_GRAPH, targets);
        Set<String> results = lensMapping.getTargetUris();
        assertEquals(0, results.size());
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
        targets.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceXref, lensId, NULL_GRAPH, targets);
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
    public void testMapUri_sourceXref_lensId_parttgtUriPattern() throws Exception {
        report("MapUri_sourceXref_lensId_parttgtUriPattern");
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern3.substring(0, stringPattern3.length()-5));
        Set results = uriMapper.mapUri(sourceXref, lensId, NULL_GRAPH, targets);
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
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set results = uriMapper.mapUri(sourceXref, lensId, NULL_GRAPH, NO_PATTERNS);
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
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern2);
        targets.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceXref, lensId, NULL_GRAPH, targets);
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
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceUri, lensId, NULL_GRAPH, targets);
        assertFalse(results.contains(map3Uri1));
        assertFalse(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        checkForNoOtherlensId(results);
    }

    @Test
    public void testMapBySet_sourceUri_lensId_tgtUriPattern() throws Exception {
        report("MapBySet_sourceUri_lensId_tgtUriPattern");
        String sourceUri = map3Uri2;
        Set<String> sourceUris = new HashSet<String>();
        sourceUris.add(sourceUri);
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern3);
        MappingsBySet lensMapping = uriMapper.mapBySet(sourceUris, lensId, NULL_GRAPH, targets);
        Set<String> results = lensMapping.getTargetUris();
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
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set results = uriMapper.mapUri(sourceUri, lensId, NULL_GRAPH, NO_PATTERNS);
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
    public void testMapBySet_sourceUri_lensId() throws Exception {
        report("MapBySeti_sourceUri_lensId");
        String sourceUri = map3Uri2;
        Set<String> sourceUris = new HashSet<String>();
        sourceUris.add(sourceUri);
        String lensId = Lens.DEFAULT_LENS_NAME;
        MappingsBySet lensMapping = uriMapper.mapBySet(sourceUris, lensId, NULL_GRAPH, NO_PATTERNS);
        Set<String> results = lensMapping.getTargetUris();
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
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(DataSource2);
        targets.add(DataSource3);
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
        String lensId = Lens.DEFAULT_LENS_NAME;
        DataSource tgtDataSource = DataSource3;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(tgtDataSource);
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
            assertEquals(tgtDataSource, mapping.getTarget().getDataSource());
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
        report("MapFull_sourceXref_lensId");
        Xref sourceXref = map3xref2;
        String lensId = Lens.DEFAULT_LENS_NAME;
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
        String lensId = Lens.DEFAULT_LENS_NAME;
        UriPattern[] tgtUriPatterns = null;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern2);
        targets.add(stringPattern3);
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
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern3);
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
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(DataSource2);
        targets.add(DataSource3);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            assertTrue(mapping.getSourceUri().contains(sourceUri));
            assertEquals(1, mapping.getSourceUri().size());
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
        String lensId = Lens.DEFAULT_LENS_NAME;
        DataSource tgtDataSource = DataSource3;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(tgtDataSource);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            assertTrue(mapping.getSourceUri().contains(sourceUri));
            assertEquals(1, mapping.getSourceUri().size());
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
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, true, NULL_GRAPH, NO_PATTERNS);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            assertTrue(mapping.getSourceUri().contains(sourceUri));
            assertEquals(1, mapping.getSourceUri().size());
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
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, true, NULL_GRAPH, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            assertTrue(mapping.getSourceUri().contains(sourceUri));
            assertEquals(1, mapping.getSourceUri().size());
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
        String lensId = Lens.DEFAULT_LENS_NAME;
        UriPattern[] tgtUriPatterns = null;
        Set<String> targets = new HashSet<String>();
        targets.add(stringPattern2);
        targets.add(stringPattern3);
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
