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

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensUri_tgtDataSources() throws Exception {
        report("MapID_sourceXref_lensUri_tgtDataSources");
        Xref sourceXref = null;
        String lensUri = Lens.getDefaultLens();
        Set<Xref> results = uriMapper.mapID(sourceXref, lensUri, DataSource2, DataSource3);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensUri_tgtDataSource() throws Exception {
        report("MapID_sourceXref_lensUri_tgtDataSource");
        Xref sourceXref = null;
        String lensUri = Lens.getDefaultLens();
        DataSource tgtDataSource = DataSource3;
        Set results = uriMapper.mapID(sourceXref, lensUri, tgtDataSource);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_lensUri() throws Exception {
        report("MapID_sourceXref_lensUri");
        Xref sourceXref = null;
        String lensUri = Lens.getDefaultLens();
        Set results = uriMapper.mapID(sourceXref, lensUri);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensUri_tgtUriPatterns() throws Exception {
        report("MapUri_sourceUri_lensUri_tgtUriPatterns");
        String sourceUri = null;
        String lensUri = Lens.getDefaultLens();
        UriPattern[] tgtUriPatterns = null;
        Set results = uriMapper.mapUri(sourceUri, lensUri, uriPattern2, uriPattern3);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensUri_tgtUriPattern() throws Exception {
        report("MapUri_sourceXref_lensUri_tgtUriPattern");
        Xref sourceXref = null;
        String lensUri = Lens.getDefaultLens();
        UriPattern tgtUriPattern = uriPattern3;
        Set results = uriMapper.mapUri(sourceXref, lensUri, tgtUriPattern);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensUri() throws Exception {
        report("MapUri_sourceXref_lensUri");
        Xref sourceXref = null;
        String lensUri = Lens.getDefaultLens();
        Set results = uriMapper.mapUri(sourceXref, lensUri);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_lensUri_tgtUriPatterns() throws Exception {
        report("MapUri_sourceXref_lensUri_tgtUriPatterns");
        Xref sourceXref = null;
        String lensUri = Lens.getDefaultLens();
        UriPattern[] tgtUriPatterns = null;
        Set results = uriMapper.mapUri(sourceXref, lensUri, uriPattern2, uriPattern3);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensUri_tgtUriPattern() throws Exception {
        report("MapUri_sourceUri_lensUri_tgtUriPattern");
        String sourceUri = null;
        String lensUri = Lens.getDefaultLens();
        UriPattern tgtUriPattern = uriPattern3;
        Set results = uriMapper.mapUri(sourceUri, lensUri, tgtUriPattern);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensUri() throws Exception {
        report("MapUri_sourceUri_lensUri");
        String sourceUri = null;
        String lensUri = Lens.getDefaultLens();
        Set results = uriMapper.mapUri(sourceUri, lensUri);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensUri_tgtDataSources() throws Exception {
        report("MapFull_sourceXref_lensUri_tgtDataSources");
        Xref sourceXref = null;
        String lensUri = Lens.getDefaultLens();
        DataSource[] tgtDataSources = null;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, DataSource2, DataSource3);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensUri_tgtDataSource() throws Exception {
        report("MapFull_sourceXref_lensUri_tgtDataSources");
        Xref sourceXref = null;
        String lensUri = Lens.getDefaultLens();
        DataSource tgtDataSource = DataSource3;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, tgtDataSource);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensUri() throws Exception {
        report("MapFull_sourceXref_lensUri_tgtDataSources");
        Xref sourceXref = null;
        String lensUri = Lens.getDefaultLens();
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensUri_tgtUriPatterns() throws Exception {
        report("MapFull_sourceXref_lensUri_tgtUriPatterns");
        Xref sourceXref = null;
        String lensUri = Lens.getDefaultLens();
        UriPattern[] tgtUriPatterns = null;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, uriPattern2, uriPattern3);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_lensUri_tgtUriPattern() throws Exception {
        report("MapFull_sourceXref_lensUri_tgtUriPattern");
        Xref sourceXref = null;
        String lensUri = Lens.getDefaultLens();
        UriPattern tgtUriPattern = uriPattern3;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, lensUri, tgtUriPattern);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensUri_tgtDataSources() throws Exception {
        report("MapFull_sourceUri_lensUri_tgtDataSources");
        String sourceUri = null;
        Xref sourceXref = null;
        String lensUri = Lens.getDefaultLens();
        DataSource[] tgtDataSources = null;
        Set expResult = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, DataSource2, DataSource3);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensUri_tgtDataSource() throws Exception {
        report("MapFull_sourceUri_lensUri_tgtDataSource");
        String sourceUri = null;
        Xref sourceXref = null;
        String lensUri = Lens.getDefaultLens();
        DataSource tgtDataSource = DataSource3;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, tgtDataSource);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_MapFull_sourceUri_lensUri() throws Exception {
        report("MapFull_sourceUri_lensUri");
        String sourceUri = null;
        Xref sourceXref = null;
        String lensUri = Lens.getDefaultLens();
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensUri_tgtUriPattern() throws Exception {
        report("MapFull_sourceUri_lensUri_tgtUriPattern");
        String sourceUri = null;
        Xref sourceXref = null;
        String lensUri = Lens.getDefaultLens();
        UriPattern tgtUriPattern = uriPattern3;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, tgtUriPattern);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_lensUri_tgtUriPatterns() throws Exception {
        report("MapFull_sourceUri_lensUri_tgtUriPatterns");
        String sourceUri = null;
        Xref sourceXref = null;
        String lensUri = Lens.getDefaultLens();
        UriPattern[] tgtUriPatterns = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, lensUri, uriPattern2, uriPattern3);
        assertTrue(results.isEmpty());
    }

}
