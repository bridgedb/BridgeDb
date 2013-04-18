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
public abstract class UriMapperTestLensTest extends UriListenerTest{

   /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensUri_tgtDataSources() throws Exception {
        report("MapID_sourceXref_lensUri_tgtDataSources");
        Xref sourceXref = map2xref2;
        String lensUri = Lens.getTestLens();
        Set results = uriMapper.mapID(sourceXref, lensUri, DataSource2, DataSource3);
        assertFalse(results.contains(map2xref1));
        assertTrue(results.contains(map2xref2));
        assertFalse(results.contains(map2xref3)); 
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
    public void testMapID_sourceXref_lensUri_tgtDataSource() throws Exception {
        report("MapID_sourceXref_lensUri_tgtDataSource");
        Xref sourceXref = map2xref2;
        String lensUri = Lens.getTestLens();
        DataSource tgtDataSource = DataSource3;
        Set results = uriMapper.mapID(sourceXref, lensUri, tgtDataSource);
        assertFalse(results.contains(map2xref1));
        assertFalse(results.contains(map2xref2));
        assertFalse(results.contains(map2xref3));
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
        Xref sourceXref = map2xref2;
        String lensUri = Lens.getTestLens();
        Set results = uriMapper.mapID(sourceXref, lensUri);
        assertFalse(results.contains(map2xref1));
        assertTrue(results.contains(map2xref2));
        assertFalse(results.contains(map2xref3));
        assertFalse(results.contains(map1xref2));
        assertFalse(results.contains(map1xref1));
        assertFalse(results.contains(map3xref2));
        assertTrue(results.contains(map2Axref1));
        assertFalse(results.contains(map2Axref2));
        assertTrue(results.contains(map2Axref3));
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensUri_tgtUriPatterns() throws Exception {
        report("MapUri_sourceUri_lensUri_tgtUriPatterns");
        String sourceUri = map3Uri3;
        String lensUri = Lens.getTestLens();
        UriPattern[] tgtUriPatterns = null;
        Set results = uriMapper.mapUri(sourceUri, lensUri, uriPattern2, uriPattern3);
        assertFalse(results.contains(map3Uri1));
        assertFalse(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        assertFalse(results.contains(map3AUri1));
        assertTrue(results.contains(map3AUri2));
        assertFalse(results.contains(map3AUri2a));
        assertFalse(results.contains(map3AUri3));
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensUri_tgtUriPattern() throws Exception {
        report("MapUri_sourceXref_lensUri_tgtUriPattern");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getTestLens();
        UriPattern tgtUriPattern = uriPattern3;
        Set results = uriMapper.mapUri(sourceXref, lensUri, tgtUriPattern);
        assertFalse(results.contains(map3Uri1));
        assertFalse(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertFalse(results.contains(map3Uri3));
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
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getTestLens();
        Set results = uriMapper.mapUri(sourceXref, lensUri);
        assertFalse(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertTrue(results.contains(map3Uri2a));
        assertFalse(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        assertTrue(results.contains(map3AUri1));
        assertFalse(results.contains(map3AUri2));
        assertFalse(results.contains(map3AUri2a));
        assertTrue(results.contains(map3AUri3));
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensUri_tgtUriPatterns() throws Exception {
        report("MapUri_sourceXref_lensUri_tgtUriPatterns");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getTestLens();
        UriPattern[] tgtUriPatterns = null;
        Set results = uriMapper.mapUri(sourceXref, lensUri, uriPattern2, uriPattern3);
        assertFalse(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertFalse(results.contains(map3Uri3));
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
    public void testMapUri_sourceUri_lensUri_tgtUriPattern() throws Exception {
        report("MapUri_sourceUri_lensUri_tgtUriPattern");
        String sourceUri = map3Uri2;
        String lensUri = Lens.getTestLens();
        UriPattern tgtUriPattern = uriPattern3;
        Set results = uriMapper.mapUri(sourceUri, lensUri, tgtUriPattern);
        assertFalse(results.contains(map3Uri1));
        assertFalse(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertFalse(results.contains(map3Uri3));
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
        String sourceUri = map3Uri2;
        String lensUri = Lens.getTestLens();
        Set results = uriMapper.mapUri(sourceUri, lensUri);
        assertFalse(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertTrue(results.contains(map3Uri2a));
        assertFalse(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
        assertTrue(results.contains(map3AUri1));
        assertFalse(results.contains(map3AUri2));
        assertFalse(results.contains(map3AUri2a));
        assertTrue(results.contains(map3AUri3));
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensUri_tgtDataSources() throws Exception {
        report("MapFull_sourceXref_lensUri_tgtDataSources");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getTestLens();
        DataSource[] tgtDataSources = null;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, DataSource2, DataSource3);
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
        assertFalse(targetUris.contains(map3AUri1));
        assertFalse(targetUris.contains(map3AUri2));
        assertFalse(targetUris.contains(map3AUri2a));
        assertTrue(targetUris.contains(map3AUri3));

        assertFalse(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertFalse(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
        assertFalse(targetXrefs.contains(map3Axref1));
        assertFalse(targetXrefs.contains(map3Axref2));
        assertTrue(targetXrefs.contains(map3Axref3));
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensUri_tgtDataSource() throws Exception {
        report("MapFull_sourceXref_lensUri_tgtDataSources");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getTestLens();
        DataSource tgtDataSource = DataSource3;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, tgtDataSource);
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
            assertEquals(tgtDataSource, mapping.getTarget().getDataSource());
            assertFalse(ids.contains(mapping.getId()));
            ids.add(mapping.getId());
        }
        assertFalse(targetUris.contains(map3Uri1));
        assertFalse(targetUris.contains(map3Uri2));
        assertFalse(targetUris.contains(map3Uri2a));
        assertFalse(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map3AUri1));
        assertFalse(targetUris.contains(map3AUri2));
        assertFalse(targetUris.contains(map3AUri2a));
        assertTrue(targetUris.contains(map3AUri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));

        assertFalse(targetXrefs.contains(map3xref1));
        assertFalse(targetXrefs.contains(map3xref2));
        assertFalse(targetXrefs.contains(map3xref3));
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
    @Test
    public void testMapFull_sourceXref_lensUri() throws Exception {
        report("MapFull_sourceXref_lensUri_tgtDataSources");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getTestLens();
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri);
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
        assertTrue(targetUris.contains(map3AUri1));
        assertFalse(targetUris.contains(map3AUri2));
        assertFalse(targetUris.contains(map3AUri2a));
        assertTrue(targetUris.contains(map3AUri3));

        assertFalse(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertFalse(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
        assertTrue(targetXrefs.contains(map3Axref1));
        assertFalse(targetXrefs.contains(map3Axref2));
        assertTrue(targetXrefs.contains(map3Axref3));
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensUri_tgtUriPatterns() throws Exception {
        report("MapFull_sourceXref_lensUri_tgtUriPatterns");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getTestLens();
        UriPattern[] tgtUriPatterns = null;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, uriPattern2, uriPattern3);
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
        assertFalse(targetUris.contains(map3AUri1));
        assertFalse(targetUris.contains(map3AUri2));
        assertFalse(targetUris.contains(map3AUri2a));
        assertTrue(targetUris.contains(map3AUri3));

        assertFalse(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertFalse(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
        assertFalse(targetXrefs.contains(map3Axref1));
        assertFalse(targetXrefs.contains(map3Axref2));
        assertTrue(targetXrefs.contains(map3Axref3));
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensUri_tgtUriPattern() throws Exception {
        report("MapFull_sourceXref_lensUri_tgtUriPattern");
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getTestLens();
        UriPattern tgtUriPattern = uriPattern3;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, tgtUriPattern);
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
        assertFalse(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        assertFalse(targetUris.contains(map3AUri1));
        assertFalse(targetUris.contains(map3AUri2));
        assertFalse(targetUris.contains(map3AUri2a));
        assertTrue(targetUris.contains(map3AUri3));

        assertFalse(targetXrefs.contains(map3xref1));
        assertFalse(targetXrefs.contains(map3xref2));
        assertFalse(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
        assertFalse(targetXrefs.contains(map3Axref1));
        assertFalse(targetXrefs.contains(map3Axref2));
        assertTrue(targetXrefs.contains(map3Axref3));
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensUri_tgtDataSources() throws Exception {
        report("MapFull_sourceUri_lensUri_tgtDataSources");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getTestLens();
        DataSource[] tgtDataSources = null;
        Set expResult = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, DataSource2, DataSource3);
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
        assertFalse(targetUris.contains(map3AUri1));
        assertFalse(targetUris.contains(map3AUri2));
        assertFalse(targetUris.contains(map3AUri2a));
        assertTrue(targetUris.contains(map3AUri3));

        assertFalse(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertFalse(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
        assertFalse(targetXrefs.contains(map3Axref1));
        assertFalse(targetXrefs.contains(map3Axref2));
        assertTrue(targetXrefs.contains(map3Axref3));
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensUri_tgtDataSource() throws Exception {
        report("MapFull_sourceUri_lensUri_tgtDataSource");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getTestLens();
        DataSource tgtDataSource = DataSource3;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, tgtDataSource);
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
        assertFalse(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        assertFalse(targetUris.contains(map3AUri1));
        assertFalse(targetUris.contains(map3AUri2));
        assertFalse(targetUris.contains(map3AUri2a));
        assertTrue(targetUris.contains(map3AUri3));

        assertFalse(targetXrefs.contains(map3xref1));
        assertFalse(targetXrefs.contains(map3xref2));
        assertFalse(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
        assertFalse(targetXrefs.contains(map3Axref1));
        assertFalse(targetXrefs.contains(map3Axref2));
        assertTrue(targetXrefs.contains(map3Axref3));
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_MapFull_sourceUri_lensUri() throws Exception {
        report("MapFull_sourceUri_lensUri");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getTestLens();
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri);
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
        assertTrue(targetUris.contains(map3AUri1));
        assertFalse(targetUris.contains(map3AUri2));
        assertFalse(targetUris.contains(map3AUri2a));
        assertTrue(targetUris.contains(map3AUri3));

        assertFalse(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertFalse(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
        assertTrue(targetXrefs.contains(map3Axref1));
        assertFalse(targetXrefs.contains(map3Axref2));
        assertTrue(targetXrefs.contains(map3Axref3));
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensUri_tgtUriPattern() throws Exception {
        report("MapFull_sourceUri_lensUri_tgtUriPattern");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getTestLens();
        UriPattern tgtUriPattern = uriPattern3;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, tgtUriPattern);
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
        assertFalse(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
        assertFalse(targetUris.contains(map3AUri1));
        assertFalse(targetUris.contains(map3AUri2));
        assertFalse(targetUris.contains(map3AUri2a));
        assertTrue(targetUris.contains(map3AUri3));

        assertFalse(targetXrefs.contains(map3xref1));
        assertFalse(targetXrefs.contains(map3xref2));
        assertFalse(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
        assertFalse(targetXrefs.contains(map3Axref1));
        assertFalse(targetXrefs.contains(map3Axref2));
        assertTrue(targetXrefs.contains(map3Axref3));
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensUri_tgtUriPatterns() throws Exception {
        report("MapFull_sourceUri_lensUri_tgtUriPatterns");
        String sourceUri = map3Uri2;
        Xref sourceXref = map3xref2;
        String lensUri = Lens.getTestLens();
        UriPattern[] tgtUriPatterns = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, uriPattern2, uriPattern3);
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
        assertFalse(targetUris.contains(map3AUri1));
        assertFalse(targetUris.contains(map3AUri2));
        assertFalse(targetUris.contains(map3AUri2a));
        assertTrue(targetUris.contains(map3AUri3));

        assertFalse(targetXrefs.contains(map3xref1));
        assertTrue(targetXrefs.contains(map3xref2));
        assertFalse(targetXrefs.contains(map3xref3));
        assertFalse(targetXrefs.contains(map1xref2));
        assertFalse(targetXrefs.contains(map1xref1));
        assertFalse(targetXrefs.contains(map2xref2));   
        assertFalse(targetXrefs.contains(map3Axref1));
        assertFalse(targetXrefs.contains(map3Axref2));
        assertTrue(targetXrefs.contains(map3Axref3));
    }

}
