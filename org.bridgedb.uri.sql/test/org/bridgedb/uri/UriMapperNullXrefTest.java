/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.uri;

import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.bridgedb.rdf.UriPattern;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Christian
 */
public abstract class UriMapperNullXrefTest extends UriListenerTest{

    private static  final String NULL_GRAPH = null;

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensId_tgtDataSources() throws Exception {
        report("MapID_sourceXref_lensId_tgtDataSources");
        Xref sourceXref = null;
        String lensId = Lens.getDefaultLens();
        Set<Xref> results = uriMapper.mapID(sourceXref, lensId, DataSource2, DataSource3);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensId_tgtDataSource() throws Exception {
        report("MapID_sourceXref_lensId_tgtDataSource");
        Xref sourceXref = null;
        String lensId = Lens.getDefaultLens();
        DataSource tgtDataSource = DataSource3;
        Set results = uriMapper.mapID(sourceXref, lensId, tgtDataSource);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensId() throws Exception {
        report("MapID_sourceXref_lensId");
        Xref sourceXref = null;
        String lensId = Lens.getDefaultLens();
        Set results = uriMapper.mapID(sourceXref, lensId);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensId_tgtUriPatterns() throws Exception {
        report("MapUri_sourceUri_lensId_tgtUriPatterns");
        String sourceUri = null;
        String lensId = Lens.getDefaultLens();
        Set results = uriMapper.mapUri(sourceUri, lensId, NULL_GRAPH, regexUriPattern2, regexUriPattern3);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensId_tgtUriPattern() throws Exception {
        report("MapUri_sourceXref_lensId_tgtUriPattern");
        Xref sourceXref = null;
        String lensId = Lens.getDefaultLens();
        Set results = uriMapper.mapUri(sourceXref, lensId, NULL_GRAPH, regexUriPattern3);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensId() throws Exception {
        report("MapUri_sourceXref_lensId");
        Xref sourceXref = null;
        String lensId = Lens.getDefaultLens();
        Set results = uriMapper.mapUri(sourceXref, lensId, NULL_GRAPH);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensId_tgtUriPatterns() throws Exception {
        report("MapUri_sourceXref_lensId_tgtUriPatterns");
        Xref sourceXref = null;
        String lensId = Lens.getDefaultLens();
        Set results = uriMapper.mapUri(sourceXref, lensId, NULL_GRAPH, regexUriPattern2, regexUriPattern3);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensId_tgtUriPattern() throws Exception {
        report("MapUri_sourceUri_lensId_tgtUriPattern");
        String sourceUri = null;
        String lensId = Lens.getDefaultLens();
        Set results = uriMapper.mapUri(sourceUri, lensId, NULL_GRAPH, regexUriPattern3);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensId() throws Exception {
        report("MapUri_sourceUri_lensId");
        String sourceUri = null;
        String lensId = Lens.getDefaultLens();
        Set results = uriMapper.mapUri(sourceUri, lensId, NULL_GRAPH);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_tgtDataSources() throws Exception {
        report("MapFull_sourceXref_lensId_tgtDataSources");
        Xref sourceXref = null;
        String lensId = Lens.getDefaultLens();
        DataSource[] tgtDataSources = null;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, DataSource2, DataSource3);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_tgtDataSource() throws Exception {
        report("MapFull_sourceXref_lensId_tgtDataSources");
        Xref sourceXref = null;
        String lensId = Lens.getDefaultLens();
        DataSource tgtDataSource = DataSource3;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, tgtDataSource);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId() throws Exception {
        report("MapFull_sourceXref_lensId_tgtDataSources");
        Xref sourceXref = null;
        String lensId = Lens.getDefaultLens();
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_tgtUriPatterns() throws Exception {
        report("MapFull_sourceXref_lensId_tgtUriPatterns");
        Xref sourceXref = null;
        String lensId = Lens.getDefaultLens();
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, NULL_GRAPH, regexUriPattern2, regexUriPattern3);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensId_tgtUriPattern() throws Exception {
        report("MapFull_sourceXref_lensId_tgtUriPattern");
        Xref sourceXref = null;
        String lensId = Lens.getDefaultLens();
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensId, NULL_GRAPH, regexUriPattern3);
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
        String lensId = Lens.getDefaultLens();
        DataSource[] tgtDataSources = null;
        Set expResult = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, DataSource2, DataSource3);
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
        String lensId = Lens.getDefaultLens();
        DataSource tgtDataSource = DataSource3;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, tgtDataSource);
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
        String lensId = Lens.getDefaultLens();
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId);
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
        String lensId = Lens.getDefaultLens();
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, NULL_GRAPH, regexUriPattern3);
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
        String lensId = Lens.getDefaultLens();
        UriPattern[] tgtUriPatterns = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensId, NULL_GRAPH, regexUriPattern2, regexUriPattern3);
        assertTrue(results.isEmpty());
    }

}
