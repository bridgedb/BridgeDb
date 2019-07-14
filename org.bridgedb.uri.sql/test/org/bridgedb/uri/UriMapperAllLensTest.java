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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Christian
 */
@Tag("mysql")
public abstract class UriMapperAllLensTest extends UriListenerTest{

    private static  final String NULL_GRAPH = null;
    private static final Boolean DEFAULT_IGNORE = null;
    private static final Set<DataSource> NO_TARGET_DATA_SOURCES = null;
    private static final Set<String> NO_PATTERNS = null;
    
    /**
     * Test of mapID method, of class UriMapper.
     */
    @Tag("mysql")
    @Test
    public void testMapID_sourceXref_lensId_tgtDataSources() throws Exception {
        report("MapID_sourceXref_lensId_tgtDataSources");
        Xref sourceXref = map2xref2;
        String lensId = Lens.ALL_LENS_NAME;
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
        assertFalse(results.contains(map2Axref1));
        assertFalse(results.contains(map1Axref1));
        assertTrue(results.contains(map2Axref2));
        assertTrue(results.contains(map2Axref3)); 
    }

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Tag("mysql")
    @Test
    public void testMapID_sourceXref_lensId_tgtDataSource() throws Exception {
        report("MapID_sourceXref_lensId_tgtDataSource");
        Xref sourceXref = map2xref2;
        String lensId = Lens.ALL_LENS_NAME;
        DataSource tgtDataSource = DataSource3;
        Set<DataSource> tgtDataSources = new HashSet<DataSource>();
        tgtDataSources.add(tgtDataSource);
        Set results = uriMapper.mapID(sourceXref, lensId, tgtDataSources);
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
    @Tag("mysql")
    @Test
    public void testMapID_sourceXref_lensId() throws Exception {
        report("MapID_sourceXref_lensId");
        Xref sourceXref = map2xref2;
        String lensId = Lens.ALL_LENS_NAME;
        Set results = uriMapper.mapID(sourceXref, lensId, NO_TARGET_DATA_SOURCES);
        assertTrue(results.contains(map2xref1));
        assertTrue(results.contains(map2xref2));
        assertTrue(results.contains(map2xref3));
        assertFalse(results.contains(map1xref2));
        assertFalse(results.contains(map1xref1));
        assertFalse(results.contains(map3xref2));
        assertTrue(results.contains(map2Axref1));
        assertTrue(results.contains(map2Axref2));
        assertFalse(results.contains(map1Axref2));
        assertTrue(results.contains(map2Axref3));
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Tag("mysql")
    @Test
    public void testMapUri_sourceUri_lensId_tgtUriPatterns() throws Exception {
        report("MapUri_sourceUri_lensId_tgtUriPatterns");
        String sourceUri = map3Uri3;
        String lensId = Lens.ALL_LENS_NAME;
        Set<String> uriPatterns = new HashSet<String>();
        uriPatterns.add(stringPattern2);
        uriPatterns.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceUri, lensId, NULL_GRAPH, uriPatterns);
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
    @Tag("mysql")
    @Test
    public void testMapUri_sourceXref_lensId_tgtUriPattern() throws Exception {
        report("MapUri_sourceXref_lensId_tgtUriPattern");
        Xref sourceXref = map3xref2;
        String lensId = Lens.ALL_LENS_NAME;
        Set<String> uriPatterns = new HashSet<String>();
        uriPatterns.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceXref, lensId, NULL_GRAPH, uriPatterns);
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
    @Tag("mysql")
    @Test
    public void testMapUri_sourceXref_lensId() throws Exception {
        report("MapUri_sourceXref_lensId");
        Xref sourceXref = map3xref2;
        String lensId = Lens.ALL_LENS_NAME;
        Set results = uriMapper.mapUri(sourceXref, lensId, NULL_GRAPH, NO_PATTERNS);
        assertTrue(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertTrue(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        assertTrue(results.contains(map3AUri1));
        assertTrue(results.contains(map3AUri2));
        assertFalse(results.contains(map2AUri2));
        assertTrue(results.contains(map3AUri2a));
        assertTrue(results.contains(map3AUri3));
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Tag("mysql")
    @Test
    public void testMapUri_sourceXref_lensId_tgtUriPatterns() throws Exception {
        report("MapUri_sourceXref_lensId_tgtUriPatterns");
        Xref sourceXref = map3xref2;
        String lensId = Lens.ALL_LENS_NAME;
        Set<String> uriPatterns = new HashSet<String>();
        uriPatterns.add(stringPattern2);
        uriPatterns.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceXref, lensId, NULL_GRAPH, uriPatterns);
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
    @Tag("mysql")
    @Test
    public void testMapUri_sourceUri_lensId_tgtUriPattern() throws Exception {
        report("MapUri_sourceUri_lensId_tgtUriPattern");
        String sourceUri = map3Uri2;
        String lensId = Lens.ALL_LENS_NAME;
        Set<String> uriPatterns = new HashSet<String>();
        uriPatterns.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceUri, lensId, NULL_GRAPH, uriPatterns);
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
    @Tag("mysql")
    @Test
    public void testMapUri_sourceUri_lensId() throws Exception {
        report("MapUri_sourceUri_lensId");
        String sourceUri = map3Uri2;
        String lensId = Lens.ALL_LENS_NAME;
        Set results = uriMapper.mapUri(sourceUri, lensId, NULL_GRAPH, NO_PATTERNS);
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

    private void checkVia(Mapping mapping){
//        if (mapping.getMappingSetId().size() <= 1){
//            assertEquals(0, mapping.getViaXref().size());
//        } else {
//            if (mapping.getSource() == null){
//                assertEquals(0, mapping.getViaXref().size());            
//            } else {
//                assertEquals(mapping.getMappingSetId().size(), mapping.getViaXref().size() );
//            }
//        }
    }
    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Tag("mysql")
    @Test
    public void testMapFull_sourceXref_lensId_tgtDataSources() throws Exception {
        report("MapFull_sourceXref_lensId_tgtDataSources");
        Xref sourceXref = map3xref2;
        String lensId = Lens.ALL_LENS_NAME;
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
            checkVia(mapping);
        }
        assertFalse(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        assertFalse(targetUris.contains(map3AUri1));
        assertTrue(targetUris.contains(map3AUri2));
        assertTrue(targetUris.contains(map3AUri2a));
        assertTrue(targetUris.contains(map3AUri3));

        assertFalse(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertTrue(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Tag("mysql")
    @Test
    public void testMapFull_sourceXref_lensId_tgtDataSources_noURI() throws Exception {
        report("MapFull_sourceXref_lensId_tgtDataSources_noURI");
        Xref sourceXref = map3xref2;
        String lensId = Lens.ALL_LENS_NAME;
        Set<DataSource> tgtDataSources = new HashSet<DataSource>();
        tgtDataSources.add(DataSource2);
        tgtDataSources.add(DataSource3);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, false, tgtDataSources);
        Set<String> targetUris = new HashSet<String>();
        for (Mapping mapping:results){
            assertTrue(mapping.getSourceUri().isEmpty());
            assertTrue(mapping.getTargetUri().isEmpty());
            checkVia(mapping);
        }
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Tag("mysql")
    @Test
    public void testMapFull_sourceXref_lensId_tgtDataSources_default() throws Exception {
        report("MapFull_sourceXref_lensId_tgtDataSources_default");
        Xref sourceXref = map3xref2;
        String lensId = Lens.ALL_LENS_NAME;
        Set<DataSource> tgtDataSources = new HashSet<DataSource>();
        tgtDataSources.add(DataSource2);
        tgtDataSources.add(DataSource3);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, DEFAULT_IGNORE, tgtDataSources);
        Set<String> targetUris = new HashSet<String>();
        for (Mapping mapping:results){
            assertTrue(mapping.getSourceUri().isEmpty());
            assertTrue(mapping.getTargetUri().isEmpty());
            checkVia(mapping);
        }
    }
    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Tag("mysql")
    @Test
    public void testMapFull_sourceXref_lensId_tgtDataSource() throws Exception {
        report("MapFull_sourceXref_lensId_tgtDataSources");
        Xref sourceXref = map3xref2;
        String lensId = Lens.ALL_LENS_NAME;
        DataSource tgtDataSource = DataSource3;
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
            assertEquals(tgtDataSource, mapping.getTarget().getDataSource());
            checkVia(mapping);
        }
        assertFalse(targetUris.contains(map3Uri1));
        assertFalse(targetUris.contains(map3Uri2));
        assertFalse(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map3AUri1));
        assertFalse(targetUris.contains(map3AUri2));
        assertFalse(targetUris.contains(map3AUri2a));
        assertTrue(targetUris.contains(map3AUri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));

        assertFalse(targetXrefs.contains(map3xref1));
        assertFalse(targetXrefs.contains(map3xref2));
        assertTrue(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map3Axref1));
        assertFalse(targetXrefs.contains(map3Axref2));
        assertTrue(targetXrefs.contains(map3Axref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Tag("mysql")
    @Test
    public void testMapFull_sourceXref_lensId() throws Exception {
        report("MapFull_sourceXref_lensId_tgtDataSources");
        Xref sourceXref = map3xref2;
        String lensId = Lens.ALL_LENS_NAME;
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
            checkVia(mapping);
        }
        assertTrue(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        assertTrue(targetUris.contains(map3AUri1));
        assertTrue(targetUris.contains(map3AUri2));
        assertTrue(targetUris.contains(map3AUri2a));
        assertTrue(targetUris.contains(map3AUri3));

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
    @Tag("mysql")
    @Test
    public void testMapFull_sourceXref_lensId_noUris() throws Exception {
        report("MapFull_sourceXref_lensId_tgtDataSources_noUris");
        Xref sourceXref = map3xref2;
        String lensId = Lens.ALL_LENS_NAME;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, false, NO_TARGET_DATA_SOURCES);
        for (Mapping mapping:results){
            assertTrue(mapping.getSourceUri().isEmpty());
            assertTrue(mapping.getTargetUri().isEmpty());
            checkVia(mapping);
        }
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Tag("mysql")
    @Test
    public void testMapFull_sourceXref_lensId_default() throws Exception {
        report("MapFull_sourceXref_lensId_tgtDataSources_default");
        Xref sourceXref = map3xref2;
        String lensId = Lens.ALL_LENS_NAME;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, false, NO_TARGET_DATA_SOURCES);
        for (Mapping mapping:results){
            assertTrue(mapping.getSourceUri().isEmpty());
            assertTrue(mapping.getTargetUri().isEmpty());
            checkVia(mapping);
        }
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Tag("mysql")
    @Test
    public void testMapFull_sourceXref_lensId_tgtUriPatterns() throws Exception {
        report("MapFull_sourceXref_lensId_tgtUriPatterns");
        Xref sourceXref = map3xref2;
        String lensId = Lens.ALL_LENS_NAME;
        UriPattern[] tgtUriPatterns = null;
        Set<String> uriPatterns = new HashSet<String>();
        uriPatterns.add(stringPattern2);
        uriPatterns.add(stringPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, NULL_GRAPH, uriPatterns);
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
            checkVia(mapping);
        }
        assertFalse(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertFalse(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        assertFalse(targetUris.contains(map3AUri1));
        assertTrue(targetUris.contains(map3AUri2));
        assertFalse(targetUris.contains(map3AUri2a));
        assertTrue(targetUris.contains(map3AUri3));

        assertFalse(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertTrue(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Tag("mysql")
    @Test
    public void testMapFull_sourceXref_lensId_tgtUriPattern() throws Exception {
        report("MapFull_sourceXref_lensId_tgtUriPattern");
        Xref sourceXref = map3xref2;
        String lensId = Lens.ALL_LENS_NAME;
        Set<String> uriPatterns = new HashSet<String>();
        uriPatterns.add(stringPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, NULL_GRAPH, uriPatterns);
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
            checkVia(mapping);
        }
        assertFalse(targetUris.contains(map3Uri1));
        assertFalse(targetUris.contains(map3Uri2));
        assertFalse(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        assertFalse(targetUris.contains(map3AUri1));
        assertFalse(targetUris.contains(map3AUri2));
        assertFalse(targetUris.contains(map3AUri2a));
        assertTrue(targetUris.contains(map3AUri3));

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
    @Tag("mysql")
    @Test
    public void testMapFull_sourceUri_lensId_tgtDataSources() throws Exception {
        report("MapFull_sourceUri_lensId_tgtDataSources");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.ALL_LENS_NAME;
        Set<DataSource> tgtDataSources = new HashSet<DataSource>();
        tgtDataSources.add(DataSource2);
        tgtDataSources.add(DataSource3);
        Set expResult = null;
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
            checkVia(mapping);
        }
        assertFalse(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        assertFalse(targetUris.contains(map3AUri1));
        assertTrue(targetUris.contains(map3AUri2));
        assertTrue(targetUris.contains(map3AUri2a));
        assertTrue(targetUris.contains(map3AUri3));

        assertFalse(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertTrue(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Tag("mysql")
    @Test
    public void testMapFull_sourceUri_lensId_tgtDataSource() throws Exception {
        report("MapFull_sourceUri_lensId_tgtDataSource");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.ALL_LENS_NAME;
        Set<DataSource> tgtDataSources = new HashSet<DataSource>();
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
            checkVia(mapping);
        }
        assertFalse(targetUris.contains(map3Uri1));
        assertFalse(targetUris.contains(map3Uri2));
        assertFalse(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        assertFalse(targetUris.contains(map3AUri1));
        assertFalse(targetUris.contains(map3AUri2));
        assertFalse(targetUris.contains(map3AUri2a));
        assertTrue(targetUris.contains(map3AUri3));

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
    @Tag("mysql")
    @Test
    public void testMapFull_MapFull_sourceUri_lensId() throws Exception {
        report("MapFull_sourceUri_lensId");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.ALL_LENS_NAME;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, NO_TARGET_DATA_SOURCES);
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
            checkVia(mapping);
        }
        assertTrue(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        assertTrue(targetUris.contains(map3AUri1));
        assertTrue(targetUris.contains(map3AUri2));
        assertTrue(targetUris.contains(map3AUri2a));
        assertTrue(targetUris.contains(map3AUri3));

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
    @Tag("mysql")
    @Test
    public void testMapFull_MapFull_sourceUri_lensId_noXref() throws Exception {
        report("MapFull_sourceUri_lensId_noXref");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.ALL_LENS_NAME;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, false, 
                NULL_GRAPH, NO_PATTERNS);
        for (Mapping mapping:results){
            assertTrue(mapping.getSource() == null);
            assertTrue(mapping.getTarget() == null);
            checkVia(mapping);
        }
    }

     /**
     * Test of mapFull method, of class UriMapper.
     */
    @Tag("mysql")
    @Test
    public void testMapFull_sourceUri_lensId_tgtUriPattern() throws Exception {
        report("MapFull_sourceUri_lensId_tgtUriPattern");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.ALL_LENS_NAME;
        Set<String> uriPatterns = new HashSet<String>();
        uriPatterns.add(stringPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, true, NULL_GRAPH, uriPatterns);
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
            checkVia(mapping);
        }
        assertFalse(targetUris.contains(map3Uri1));
        assertFalse(targetUris.contains(map3Uri2));
        assertFalse(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        assertFalse(targetUris.contains(map3AUri1));
        assertFalse(targetUris.contains(map3AUri2));
        assertFalse(targetUris.contains(map3AUri2a));
        assertTrue(targetUris.contains(map3AUri3));

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
    @Tag("mysql")
    @Test
    public void testMapFull_sourceUri_lensId_tgtUriPattern_noUris() throws Exception {
        report("MapFull_sourceUri_lensId_tgtUriPattern_noUris");
        String sourceUri = map3Uri2;
        String lensId = Lens.ALL_LENS_NAME;
        Set<String> uriPatterns = new HashSet<String>();
        uriPatterns.add(stringPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, false, NULL_GRAPH, uriPatterns);
        for (Mapping mapping:results){
            assertNull(mapping.getSource());
            assertNull(mapping.getTarget());
            checkVia(mapping);
        }
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Tag("mysql")
    @Test
    public void testMapFull_sourceUri_lensId_tgtUriPattern_default() throws Exception {
        report("MapFull_sourceUri_lensId_tgtUriPattern_default");
        String sourceUri = map3Uri2;
        String lensId = Lens.ALL_LENS_NAME;
        Set<String> uriPatterns = new HashSet<String>();
        uriPatterns.add(stringPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, false, NULL_GRAPH, uriPatterns);
        for (Mapping mapping:results){
            assertNull(mapping.getSource());
            assertNull(mapping.getTarget());
            checkVia(mapping);
        }
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Tag("mysql")
    @Test
    public void testMapFull_sourceUri_lensId_tgtUriPatterns() throws Exception {
        report("MapFull_sourceUri_lensId_tgtUriPatterns");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensId = Lens.ALL_LENS_NAME;
        Set<String> uriPatterns = new HashSet<String>();
        uriPatterns.add(stringPattern2);
        uriPatterns.add(stringPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, true, NULL_GRAPH, uriPatterns);
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
            checkVia(mapping);
        }
        assertFalse(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertFalse(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        assertFalse(targetUris.contains(map3AUri1));
        assertTrue(targetUris.contains(map3AUri2));
        assertFalse(targetUris.contains(map3AUri2a));
        assertTrue(targetUris.contains(map3AUri3));

        assertFalse(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertTrue(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
    }
}
