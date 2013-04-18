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
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Christian
 */
public abstract class UriMapperNullTargetTest extends UriListenerTest{

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensUri_tgtDataSources_first_null() throws Exception {
        report("MapID_sourceXref_lensUri_tgtDataSources_first_null");
        Xref sourceXref = map2xref2;
        String lensUri = Lens.getDefaultLens();
        DataSource[] targets = new DataSource[2];
        targets[0] = null;
        targets[1] = DataSource3;
        Set<Xref> results = uriMapper.mapID(sourceXref, lensUri, targets);
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
    public void testMapID_sourceXref_lensUri_tgtDataSources_second_null() throws Exception {
        report("MapID_sourceXref_lensUri_tgtDataSources_second_null");
        Xref sourceXref = map2xref2;
        String lensUri = Lens.getDefaultLens();
        DataSource[] targets = new DataSource[2];
        targets[0] = DataSource2;
        targets[1] = null;
        Set<Xref> results = uriMapper.mapID(sourceXref, lensUri, targets);
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
    public void testMapID_sourceXref_lensUri_tgtDataSource() throws Exception {
        report("MapID_sourceXref_lensUri_tgtDataSource");
        Xref sourceXref = map2xref2;
        String lensUri = Lens.getDefaultLens();
        DataSource tgtDataSource = null;
        Set results = uriMapper.mapID(sourceXref, lensUri, tgtDataSource);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensUri_null_array() throws Exception {
        report("MapID_sourceXref_lensUri_null_array");
        Xref sourceXref = map2xref2;
        String lensUri = Lens.getDefaultLens();
        DataSource[] tragets = null;
        Set results = uriMapper.mapID(sourceXref, lensUri, tragets);
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
    public void testMapID_sourceXref_lensUri_empty_array() throws Exception {
        report("MapID_sourceXref_lensUri_empty_array");
        Xref sourceXref = map2xref2;
        String lensUri = Lens.getDefaultLens();
        DataSource[] tragets = new DataSource[0];
        Set results = uriMapper.mapID(sourceXref, lensUri, tragets);
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
    public void testMapUri_sourceUri_lensUri_tgtUriPatterns_first_null() throws Exception {
        report("MapUri_sourceUri_lensUri_tgtUriPatterns_first_null");
        String sourceUri = map3Uri3;
        String lensUri = Lens.getDefaultLens();
        UriPattern[] tgtUriPatterns = new UriPattern[2];
        tgtUriPatterns[0] = null;
        tgtUriPatterns[1] = uriPattern3;
        Set results = uriMapper.mapUri(sourceUri, lensUri, tgtUriPatterns);
        assertFalse(results.contains(map3Uri1));
        assertFalse(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        checkForNoOtherLensUri(results);
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensUri_tgtUriPatterns_second_null() throws Exception {
        report("MapUri_sourceUri_lensUri_tgtUriPatterns_second_null");
        String sourceUri = map3Uri3;
        String lensUri = Lens.getDefaultLens();
        UriPattern[] tgtUriPatterns = new UriPattern[2];
        tgtUriPatterns[0] = uriPattern2;
        tgtUriPatterns[1] = null;
        Set results = uriMapper.mapUri(sourceUri, lensUri, tgtUriPatterns);
        assertFalse(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertFalse(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        checkForNoOtherLensUri(results);
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensUri_tgtUriPattern() throws Exception {
        report("MapUri_sourceXref_lensUri_tgtUriPattern");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        UriPattern tgtUriPattern = null;
        Set results = uriMapper.mapUri(sourceXref, lensUri, tgtUriPattern);
        assertTrue(results.isEmpty());
     }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensUri_null_pattern() throws Exception {
        report("MapUri_sourceXref_lensUri_null_pattern");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        UriPattern[] targets = null;
        Set results = uriMapper.mapUri(sourceXref, lensUri, targets);
        assertTrue(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertTrue(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        checkForNoOtherLensUri(results);
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensUri_empty_pattern() throws Exception {
        report("MapUri_sourceXref_lensUri");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        UriPattern[] targets = new UriPattern[0];
        Set results = uriMapper.mapUri(sourceXref, lensUri, targets);
        assertTrue(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertTrue(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        checkForNoOtherLensUri(results);
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensUri_tgtUriPatterns_second__null() throws Exception {
        report("MapUri_sourceXref_lensUri_tgtUriPatterns_second_null");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        UriPattern[] tgtUriPatterns = new UriPattern[2];
        tgtUriPatterns[0] = uriPattern2;
        tgtUriPatterns[1] = null;
        Set results = uriMapper.mapUri(sourceXref, lensUri, tgtUriPatterns);
        assertFalse(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertFalse(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        checkForNoOtherLensUri(results);
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensUri_tgtUriPatterns_first_null() throws Exception {
        report("MapUri_sourceXref_lensUri_tgtUriPatterns_first_null");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        UriPattern[] tgtUriPatterns = new UriPattern[2];
        tgtUriPatterns[0] = null;
        tgtUriPatterns[1] = uriPattern3;
        Set results = uriMapper.mapUri(sourceXref, lensUri, tgtUriPatterns);
        assertFalse(results.contains(map3Uri1));
        assertFalse(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        checkForNoOtherLensUri(results);
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensUri_tgtUriPattern() throws Exception {
        report("MapUri_sourceUri_lensUri_tgtUriPattern");
        String sourceUri = map3Uri2;
        String lensUri = Lens.getDefaultLens();
        UriPattern tgtUriPattern = null;
        Set results = uriMapper.mapUri(sourceUri, lensUri, tgtUriPattern);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensUri_null_target() throws Exception {
        report("MapUri_sourceUri_lensUri_null_target");
        String sourceUri = map3Uri2;
        String lensUri = Lens.getDefaultLens();
        UriPattern[] targets = null;
        Set results = uriMapper.mapUri(sourceUri, lensUri, targets);
        assertTrue(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertTrue(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        checkForNoOtherLensUri(results);
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensUri_empty_targets() throws Exception {
        report("MapUri_sourceUri_lensUri_empty_targets");
        String sourceUri = map3Uri2;
        String lensUri = Lens.getDefaultLens();
        UriPattern[] targets = new UriPattern[0];
        Set results = uriMapper.mapUri(sourceUri, lensUri, targets);
        assertTrue(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertTrue(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        checkForNoOtherLensUri(results);
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensUri_tgtDataSources_first_null() throws Exception {
        report("MapFull_sourceXref_lensUri_tgtDataSources_first_null");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        DataSource[] targets = new DataSource[2];
        targets[0] = null;
        targets[1] = DataSource3;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getId(), not(equalTo(null)));
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
            assertFalse(ids.contains(mapping.getId()));
            ids.add(mapping.getId());
        }
        assertFalse(targetUris.contains(map3Uri1));
        assertFalse(targetUris.contains(map3Uri2));
        assertFalse(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherLensUri(targetUris);

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
    public void testMapFull_sourceXref_lensUri_tgtDataSources_second_null() throws Exception {
        report("MapFull_sourceXref_lensUri_tgtDataSources_second_null");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        DataSource[] targets = new DataSource[2];
        targets[0] = DataSource2;
        targets[1] = null;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getId(), not(equalTo(null)));
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
            assertFalse(ids.contains(mapping.getId()));
            ids.add(mapping.getId());
        }
        assertFalse(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertFalse(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherLensUri(targetUris);

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
    public void testMapFull_sourceXref_lensUri_tgtDataSource() throws Exception {
        report("MapFull_sourceXref_lensUri_tgtDataSources");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        DataSource tgtDataSource = null;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, tgtDataSource);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensUri_null_tgtDataSources() throws Exception {
        report("MapFull_sourceXref_lensUri_null_tgtDataSources");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        DataSource[] targets = null;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getId(), not(equalTo(null)));
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
            assertFalse(ids.contains(mapping.getId()));
            ids.add(mapping.getId());
        }
        assertTrue(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherLensUri(targetUris);

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
    public void testMapFull_sourceXref_lensUri_empty_tgtDataSources() throws Exception {
        report("MapFull_sourceXref_lensUri_empty_tgtDataSources");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        DataSource[] targets = new DataSource[0];
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getId(), not(equalTo(null)));
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
            assertFalse(ids.contains(mapping.getId()));
            ids.add(mapping.getId());
        }
        assertTrue(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherLensUri(targetUris);

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
    public void testMapFull_sourceXref_lensUri_null_UriPatterns() throws Exception {
        report("MapFull_sourceXref_lensUri_null_UriPatterns");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        UriPattern[] targets = null;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getId(), not(equalTo(null)));
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
            assertFalse(ids.contains(mapping.getId()));
            ids.add(mapping.getId());
        }
        assertTrue(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherLensUri(targetUris);

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
    public void testMapFull_sourceXref_lensUri_empty_UriPatterns() throws Exception {
        report("MapFull_sourceXref_lensUri_empty_UriPatterns");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        UriPattern[] targets = new UriPattern[0];
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getId(), not(equalTo(null)));
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
            assertFalse(ids.contains(mapping.getId()));
            ids.add(mapping.getId());
        }
        assertTrue(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherLensUri(targetUris);

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
    public void testMapFull_sourceXref_lensUri_tgtUriPatterns_first_null() throws Exception {
        report("MapFull_sourceXref_lensUri_tgtUriPatterns_first_null");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        UriPattern[] tgtUriPatterns = new UriPattern[2];
        tgtUriPatterns[0] = null;
        tgtUriPatterns[1] = uriPattern3;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, tgtUriPatterns);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getId(), not(equalTo(null)));
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
            assertFalse(ids.contains(mapping.getId()));
            ids.add(mapping.getId());
        }
        assertFalse(targetUris.contains(map3Uri1));
        assertFalse(targetUris.contains(map3Uri2));
        assertFalse(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherLensUri(targetUris);

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
    public void testMapFull_sourceXref_lensUri_tgtUriPatterns_second_null() throws Exception {
        report("MapFull_sourceXref_lensUri_tgtUriPatterns_second_null");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        UriPattern[] tgtUriPatterns = new UriPattern[2];
        tgtUriPatterns[0] = uriPattern2;
        tgtUriPatterns[1] = null;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, tgtUriPatterns);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getId(), not(equalTo(null)));
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
            assertFalse(ids.contains(mapping.getId()));
            ids.add(mapping.getId());
        }
        assertFalse(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertFalse(targetUris.contains(map3Uri2a));
        assertFalse(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherLensUri(targetUris);

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
    public void testMapFull_sourceXref_lensUri_tgtUriPattern() throws Exception {
        report("MapFull_sourceXref_lensUri_tgtUriPattern");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        UriPattern tgtUriPattern = null;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, tgtUriPattern);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensUri_tgtDataSources_first_null() throws Exception {
        report("MapFull_sourceUri_lensUri_tgtDataSources_first_null");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        DataSource[] targets = new DataSource[2];
        targets[0] = null;
        targets[1] = DataSource3;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            assertTrue(mapping.getSourceUri().contains(sourceUri));
            assertTrue(mapping.getSourceUri().size() == 1);
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getId(), not(equalTo(null)));
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
            assertFalse(ids.contains(mapping.getId()));
            ids.add(mapping.getId());
        }
        assertFalse(targetUris.contains(map3Uri1));
        assertFalse(targetUris.contains(map3Uri2));
        assertFalse(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherLensUri(targetUris);

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
    public void testMapFull_sourceUri_lensUri_tgtDataSources_second_null() throws Exception {
        report("MapFull_sourceUri_lensUri_tgtDataSources_second_null");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        DataSource[] targets = new DataSource[2];
        targets[0] = DataSource2;
        targets[1] = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            assertTrue(mapping.getSourceUri().contains(sourceUri));
            assertTrue(mapping.getSourceUri().size() == 1);
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getId(), not(equalTo(null)));
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
            assertFalse(ids.contains(mapping.getId()));
            ids.add(mapping.getId());
        }
        assertFalse(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertFalse(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherLensUri(targetUris);

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
    public void testMapFull_sourceUri_lensUri_tgtDataSource() throws Exception {
        report("MapFull_sourceUri_lensUri_tgtDataSource");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        DataSource tgtDataSource = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, tgtDataSource);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_MapFull_sourceUri_lensUri_null_DataSources() throws Exception {
        report("MapFull_sourceUri_lensUri_null_DataSources");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        DataSource[] targets = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            assertTrue(mapping.getSourceUri().contains(sourceUri));
            assertTrue(mapping.getSourceUri().size() == 1);
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getId(), not(equalTo(null)));
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
            assertFalse(ids.contains(mapping.getId()));
            ids.add(mapping.getId());
        }
        assertTrue(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherLensUri(targetUris);

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
    public void testMapFull_MapFull_sourceUri_lensUri_empty_dataSources() throws Exception {
        report("MapFull_sourceUri_lensUri_empty_dataSources");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        DataSource[] targets = new DataSource[0];
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            assertTrue(mapping.getSourceUri().contains(sourceUri));
            assertTrue(mapping.getSourceUri().size() == 1);
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getId(), not(equalTo(null)));
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
            assertFalse(ids.contains(mapping.getId()));
            ids.add(mapping.getId());
        }
        assertTrue(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherLensUri(targetUris);

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
    public void testMapFull_MapFull_sourceUri_lensUri_null_patterns() throws Exception {
        report("MapFull_sourceUri_lensUri_null_patterns");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        UriPattern[] targets = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            assertTrue(mapping.getSourceUri().contains(sourceUri));
            assertTrue(mapping.getSourceUri().size() == 1);
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getId(), not(equalTo(null)));
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
            assertFalse(ids.contains(mapping.getId()));
            ids.add(mapping.getId());
        }
        assertTrue(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherLensUri(targetUris);

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
    public void testMapFull_MapFull_sourceUri_lensUri_empty_patterns() throws Exception {
        report("MapFull_sourceUri_lensUri_empty_patterns");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        UriPattern[] targets = new UriPattern[0];
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, targets);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            assertTrue(mapping.getSourceUri().contains(sourceUri));
            assertTrue(mapping.getSourceUri().size() == 1);
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getId(), not(equalTo(null)));
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
            assertFalse(ids.contains(mapping.getId()));
            ids.add(mapping.getId());
        }
        assertTrue(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherLensUri(targetUris);

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
    public void testMapFull_sourceUri_lensUri_tgtUriPattern() throws Exception {
        report("MapFull_sourceUri_lensUri_tgtUriPattern");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        UriPattern tgtUriPattern = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, tgtUriPattern);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensUri_tgtUriPatterns_first_null() throws Exception {
        report("MapFull_sourceUri_lensUri_tgtUriPatterns_first_null");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        UriPattern[] tgtUriPatterns = new UriPattern[2];
        tgtUriPatterns[0] = null;
        tgtUriPatterns[1] = uriPattern3;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, tgtUriPatterns);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            assertTrue(mapping.getSourceUri().contains(sourceUri));
            assertTrue(mapping.getSourceUri().size() == 1);
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getId(), not(equalTo(null)));
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
            assertFalse(ids.contains(mapping.getId()));
            ids.add(mapping.getId());
        }
        assertFalse(targetUris.contains(map3Uri1));
        assertFalse(targetUris.contains(map3Uri2));
        assertFalse(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherLensUri(targetUris);

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
    public void testMapFull_sourceUri_lensUri_tgtUriPatterns_second_null() throws Exception {
        report("MapFull_sourceUri_lensUri_tgtUriPatterns_second_null");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getDefaultLens();
        UriPattern[] tgtUriPatterns = new UriPattern[2];
        tgtUriPatterns[0] = uriPattern2;
        tgtUriPatterns[1] = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, tgtUriPatterns);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSource());
            assertTrue(mapping.getSourceUri().contains(sourceUri));
            assertTrue(mapping.getSourceUri().size() == 1);
            if (!mapping.getTarget().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getId(), not(equalTo(null)));
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTarget());
            assertFalse(ids.contains(mapping.getId()));
            ids.add(mapping.getId());
        }
        assertFalse(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertFalse(targetUris.contains(map3Uri2a));
        assertFalse(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        checkForNoOtherLensUri(targetUris);

        assertFalse(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertFalse(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }

}
