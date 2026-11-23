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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Christian
 */
@Tag("mysql")
public abstract class UriMapperNullXrefTest extends UriListenerTest{

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
        Xref sourceXref = null;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(DataSource2);
        targets.add(DataSource3);
        Set<Xref> results = uriMapper.mapID(sourceXref, lensId, targets);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensId_tgtDataSource() throws Exception {
        report("MapID_sourceXref_lensId_tgtDataSource");
        Xref sourceXref = null;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(DataSource3);
        Set results = uriMapper.mapID(sourceXref, lensId, targets);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensId() throws Exception {
        report("MapID_sourceXref_lensId");
        Xref sourceXref = null;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set results = uriMapper.mapID(sourceXref, lensId, NO_TARGET_DATA_SOURCES);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensId_tgtUriPatterns() throws Exception {
        report("MapUri_sourceUri_lensId_tgtUriPatterns");
        String sourceUri = null;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> patterns = new HashSet<String>();
        patterns.add(stringPattern2);
        patterns.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceUri, lensId, NULL_GRAPH, patterns);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensId_tgtUriPattern() throws Exception {
        report("MapUri_sourceXref_lensId_tgtUriPattern");
        Xref sourceXref = null;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> patterns = new HashSet<String>();
        patterns.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceXref, lensId, NULL_GRAPH, patterns);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensId() throws Exception {
        report("MapUri_sourceXref_lensId");
        Xref sourceXref = null;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set results = uriMapper.mapUri(sourceXref, lensId, NULL_GRAPH, NO_PATTERNS);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensId_tgtUriPatterns() throws Exception {
        report("MapUri_sourceXref_lensId_tgtUriPatterns");
        Xref sourceXref = null;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> patterns = new HashSet<String>();
        patterns.add(stringPattern2);
        patterns.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceXref, lensId, NULL_GRAPH, patterns);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensId_tgtUriPattern() throws Exception {
        report("MapUri_sourceUri_lensId_tgtUriPattern");
        String sourceUri = null;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> patterns = new HashSet<String>();
        patterns.add(stringPattern3);
        Set results = uriMapper.mapUri(sourceUri, lensId, NULL_GRAPH, patterns);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensId() throws Exception {
        report("MapUri_sourceUri_lensId");
        String sourceUri = null;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set results = uriMapper.mapUri(sourceUri, lensId, NULL_GRAPH, NO_PATTERNS);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_tgtDataSources() throws Exception {
        report("MapFull_sourceXref_lensId_tgtDataSources");
        Xref sourceXref = null;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(DataSource2);
        targets.add(DataSource3);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, DEFAULT_IGNORE, targets);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_tgtDataSource() throws Exception {
        report("MapFull_sourceXref_lensId_tgtDataSources");
        Xref sourceXref = null;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(DataSource3);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, DEFAULT_IGNORE, targets);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId() throws Exception {
        report("MapFull_sourceXref_lensId_tgtDataSources");
        Xref sourceXref = null;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, DEFAULT_IGNORE, NO_TARGET_DATA_SOURCES);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_tgtUriPatterns() throws Exception {
        report("MapFull_sourceXref_lensId_tgtUriPatterns");
        Xref sourceXref = null;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> patterns = new HashSet<String>();
        patterns.add(stringPattern2);
        patterns.add(stringPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, NULL_GRAPH, patterns);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_tgtUriPattern() throws Exception {
        report("MapFull_sourceXref_lensId_tgtUriPattern");
        Xref sourceXref = null;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> patterns = new HashSet<String>();
        patterns.add(stringPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, NULL_GRAPH, patterns);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensId_tgtDataSources() throws Exception {
        report("MapFull_sourceUri_lensId_tgtDataSources");
        String sourceUri = null;
        Xref sourceXref = null;
        String lensId = Lens.DEFAULT_LENS_NAME;
        DataSource[] tgtDataSources = null;
        Set expResult = null;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(DataSource2);
        targets.add(DataSource3);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, targets);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensId_tgtDataSource() throws Exception {
        report("MapFull_sourceUri_lensId_tgtDataSource");
        String sourceUri = null;
        Xref sourceXref = null;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<DataSource> targets = new HashSet<DataSource>();
        targets.add(DataSource3);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, targets);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_MapFull_sourceUri_lensId() throws Exception {
        report("MapFull_sourceUri_lensId");
        String sourceUri = null;
        Xref sourceXref = null;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, NO_TARGET_DATA_SOURCES);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensId_tgtUriPattern() throws Exception {
        report("MapFull_sourceUri_lensId_tgtUriPattern");
        String sourceUri = null;
        Xref sourceXref = null;
        String lensId = Lens.DEFAULT_LENS_NAME;
        Set<String> patterns = new HashSet<String>();
        patterns.add(stringPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, DEFAULT_IGNORE, NULL_GRAPH, patterns);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensId_tgtUriPatterns() throws Exception {
        report("MapFull_sourceUri_lensId_tgtUriPatterns");
        String sourceUri = null;
        Xref sourceXref = null;
        String lensId = Lens.DEFAULT_LENS_NAME;
        UriPattern[] tgtUriPatterns = null;
        Set<String> patterns = new HashSet<String>();
        patterns.add(stringPattern2);
        patterns.add(stringPattern3);
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, DEFAULT_IGNORE, NULL_GRAPH, patterns);
        assertTrue(results.isEmpty());
    }
}
