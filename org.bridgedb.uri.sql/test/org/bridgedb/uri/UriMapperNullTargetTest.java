/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.uri;

import java.util.HashSet;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.bridgedb.uri.api.Mapping;
import org.bridgedb.uri.tools.Lens;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Christian
 */
public abstract class UriMapperNullTargetTest extends UriListenerTest{

    private static  final String NULL_GRAPH = null;

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensId_tgtDataSources_first_null() throws Exception {
        report("MapID_sourceXref_lensId_tgtDataSources_first_null");
        Xref sourceXref = map2xref2;
        String lensId = Lens.getDefaultLens();
        DataSource[] targets = new DataSource[2];
        targets[0] = null;
        targets[1] = DataSource3;
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
        String lensId = Lens.getDefaultLens();
        DataSource[] targets = new DataSource[2];
        targets[0] = DataSource2;
        targets[1] = null;
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
        String lensId = Lens.getDefaultLens();
        DataSource tgtDataSource = null;
        Set results = uriMapper.mapID(sourceXref, lensId, tgtDataSource);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensId_null_array() throws Exception {
        report("MapID_sourceXref_lensId_null_array");
        Xref sourceXref = map2xref2;
        String lensId = Lens.getDefaultLens();
        DataSource[] tragets = null;
        Set results = uriMapper.mapID(sourceXref, lensId, tragets);
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
        String lensId = Lens.getDefaultLens();
        DataSource[] tragets = new DataSource[0];
        Set results = uriMapper.mapID(sourceXref, lensId, tragets);
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
        String lensId = Lens.getDefaultLens();
        String[] tgtUriPatterns = new String[2];
        tgtUriPatterns[0] = null;
        tgtUriPatterns[1] = stringPattern3;
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
        String lensId = Lens.getDefaultLens();
        String[] tgtUriPatterns = new String[2];
        tgtUriPatterns[0] = stringPattern2;
        tgtUriPatterns[1] = null;
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
        String lensId = Lens.getDefaultLens();
        String tgtUriPattern = null;
        Set results = uriMapper.mapUri(sourceXref, lensId, NULL_GRAPH, tgtUriPattern);
        assertTrue(results.isEmpty());
     }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensId_null_pattern() throws Exception {
        report("MapUri_sourceXref_lensId_null_pattern");
        Xref sourceXref = map3xref2;
        String lensId = Lens.getDefaultLens();
        String[] targets = null;
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
        String lensId = Lens.getDefaultLens();
        String[] targets = new String[0];
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
        String lensId = Lens.getDefaultLens();
        String[] tgtUriPatterns = new String[2];
        tgtUriPatterns[0] = stringPattern2;
        tgtUriPatterns[1] = null;
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
        String lensId = Lens.getDefaultLens();
        String[] tgtUriPatterns = new String[2];
        tgtUriPatterns[0] = null;
        tgtUriPatterns[1] = stringPattern3;
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
        String lensId = Lens.getDefaultLens();
        String tgtUriPattern = null;
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
        String lensId = Lens.getDefaultLens();
        String[] targets = null;
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
        String lensId = Lens.getDefaultLens();
        String[] targets = new String[0];
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
        String lensId = Lens.getDefaultLens();
        DataSource[] targets = new DataSource[2];
        targets[0] = null;
        targets[1] = DataSource3;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, targets);
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
        assertFalse(targetUris.contains(map3Uri1));
        assertFalse(targetUris.contains(map3Uri2));
        assertFalse(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherlensId(targetUris);

        assertFalse(targetXrefs.contains(map3xref1));
        assertFalse(targetXrefs.contains(map3xref2));
        assertTrue(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_tgtDataSources_second_null() throws Exception {
        report("MapFull_sourceXref_lensId_tgtDataSources_second_null");
        Xref sourceXref = map3xref2;
        String lensId = Lens.getDefaultLens();
        DataSource[] targets = new DataSource[2];
        targets[0] = DataSource2;
        targets[1] = null;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, targets);
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
        assertFalse(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertFalse(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherlensId(targetUris);

        assertFalse(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertFalse(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_tgtDataSource() throws Exception {
        report("MapFull_sourceXref_lensId_tgtDataSources");
        Xref sourceXref = map3xref2;
        String lensId = Lens.getDefaultLens();
        DataSource tgtDataSource = null;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, tgtDataSource);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_null_tgtDataSources() throws Exception {
        report("MapFull_sourceXref_lensId_null_tgtDataSources");
        Xref sourceXref = map3xref2;
        String lensId = Lens.getDefaultLens();
        DataSource[] targets = null;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, targets);
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
        assertTrue(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherlensId(targetUris);

        assertTrue(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertTrue(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_empty_tgtDataSources() throws Exception {
        report("MapFull_sourceXref_lensId_empty_tgtDataSources");
        Xref sourceXref = map3xref2;
        String lensId = Lens.getDefaultLens();
        DataSource[] targets = new DataSource[0];
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, targets);
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
        assertTrue(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherlensId(targetUris);

        assertTrue(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertTrue(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_null_UriPatterns() throws Exception {
        report("MapFull_sourceXref_lensId_null_UriPatterns");
        Xref sourceXref = map3xref2;
        String lensId = Lens.getDefaultLens();
        String[] targets = null;
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
        assertTrue(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherlensId(targetUris);

        assertTrue(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertTrue(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_empty_UriPatterns() throws Exception {
        report("MapFull_sourceXref_lensId_empty_UriPatterns");
        Xref sourceXref = map3xref2;
        String lensId = Lens.getDefaultLens();
        String[] targets = new String[0];
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
        assertTrue(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherlensId(targetUris);

        assertTrue(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertTrue(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_tgtUriPatterns_first_null() throws Exception {
        report("MapFull_sourceXref_lensId_tgtUriPatterns_first_null");
        Xref sourceXref = map3xref2;
        String lensId = Lens.getDefaultLens();
        String[] tgtUriPatterns = new String[2];
        tgtUriPatterns[0] = null;
        tgtUriPatterns[1] = stringPattern3;
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
        assertFalse(targetUris.contains(map3Uri1));
        assertFalse(targetUris.contains(map3Uri2));
        assertFalse(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherlensId(targetUris);

        assertFalse(targetXrefs.contains(map3xref1));
        assertFalse(targetXrefs.contains(map3xref2));
        assertTrue(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_tgtUriPatterns_second_null() throws Exception {
        report("MapFull_sourceXref_lensId_tgtUriPatterns_second_null");
        Xref sourceXref = map3xref2;
        String lensId = Lens.getDefaultLens();
        String[] tgtUriPatterns = new String[2];
        tgtUriPatterns[0] = stringPattern2;
        tgtUriPatterns[1] = null;
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
        assertFalse(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertFalse(targetUris.contains(map3Uri2a));
        assertFalse(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherlensId(targetUris);

        assertFalse(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertFalse(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_tgtUriPattern() throws Exception {
        report("MapFull_sourceXref_lensId_tgtUriPattern");
        Xref sourceXref = map3xref2;
        String lensId = Lens.getDefaultLens();
        String tgtUriPattern = null;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, NULL_GRAPH, tgtUriPattern);
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
        String lensId = Lens.getDefaultLens();
        DataSource[] targets = new DataSource[2];
        targets[0] = null;
        targets[1] = DataSource3;
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
        assertFalse(targetUris.contains(map3Uri1));
        assertFalse(targetUris.contains(map3Uri2));
        assertFalse(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherlensId(targetUris);

        assertFalse(targetXrefs.contains(map3xref1));
        assertFalse(targetXrefs.contains(map3xref2));
        assertTrue(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensId_tgtDataSources_second_null() throws Exception {
        report("MapFull_sourceUri_lensId_tgtDataSources_second_null");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.getDefaultLens();
        DataSource[] targets = new DataSource[2];
        targets[0] = DataSource2;
        targets[1] = null;
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
        assertFalse(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertFalse(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherlensId(targetUris);

        assertFalse(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertFalse(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensId_tgtDataSource() throws Exception {
        report("MapFull_sourceUri_lensId_tgtDataSource");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.getDefaultLens();
        DataSource tgtDataSource = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, tgtDataSource);
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
        String lensId = Lens.getDefaultLens();
        DataSource[] targets = null;
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
        assertTrue(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherlensId(targetUris);

        assertTrue(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertTrue(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_MapFull_sourceUri_lensId_empty_dataSources() throws Exception {
        report("MapFull_sourceUri_lensId_empty_dataSources");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.getDefaultLens();
        DataSource[] targets = new DataSource[0];
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
        assertTrue(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherlensId(targetUris);

        assertTrue(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertTrue(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_MapFull_sourceUri_lensId_null_patterns() throws Exception {
        report("MapFull_sourceUri_lensId_null_patterns");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.getDefaultLens();
        String[] targets = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, NULL_GRAPH, targets);
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
        assertTrue(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherlensId(targetUris);

        assertTrue(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertTrue(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_MapFull_sourceUri_lensId_empty_patterns() throws Exception {
        report("MapFull_sourceUri_lensId_empty_patterns");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.getDefaultLens();
        String[] targets = new String[0];
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, NULL_GRAPH, targets);
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
        assertTrue(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherlensId(targetUris);

        assertTrue(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertTrue(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensId_tgtUriPattern() throws Exception {
        report("MapFull_sourceUri_lensId_tgtUriPattern");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.getDefaultLens();
        String tgtUriPattern = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, NULL_GRAPH, tgtUriPattern);
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
        String lensId = Lens.getDefaultLens();
        String[] tgtUriPatterns = new String[2];
        tgtUriPatterns[0] = null;
        tgtUriPatterns[1] = stringPattern3;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, NULL_GRAPH, tgtUriPatterns);
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
        assertFalse(targetUris.contains(map3Uri1));
        assertFalse(targetUris.contains(map3Uri2));
        assertFalse(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherlensId(targetUris);

        assertFalse(targetXrefs.contains(map3xref1));
        assertFalse(targetXrefs.contains(map3xref2));
        assertTrue(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensId_tgtUriPatterns_second_null() throws Exception {
        report("MapFull_sourceUri_lensId_tgtUriPatterns_second_null");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.getDefaultLens();
        String[] tgtUriPatterns = new String[2];
        tgtUriPatterns[0] = stringPattern2;
        tgtUriPatterns[1] = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, NULL_GRAPH, tgtUriPatterns);
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
        assertFalse(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertFalse(targetUris.contains(map3Uri2a));
        assertFalse(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherlensId(targetUris);

        assertFalse(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertFalse(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }

}
