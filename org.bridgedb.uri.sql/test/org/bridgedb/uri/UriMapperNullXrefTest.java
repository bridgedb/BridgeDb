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
public abstract class UriMapperNullXrefTest extends UriListenerTest{

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_profileUri_tgtDataSources() throws Exception {
        report("MapID_sourceXref_profileUri_tgtDataSources");
        Xref sourceXref = null;
        String profileUri = Lens.getDefaultLens();
        Set<Xref> results = uriMapper.mapID(sourceXref, profileUri, DataSource2, DataSource3);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_profileUri_tgtDataSource() throws Exception {
        report("MapID_sourceXref_profileUri_tgtDataSource");
        Xref sourceXref = null;
        String profileUri = Lens.getDefaultLens();
        DataSource tgtDataSource = DataSource3;
        Set results = uriMapper.mapID(sourceXref, profileUri, tgtDataSource);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_profileUri() throws Exception {
        report("MapID_sourceXref_profileUri");
        Xref sourceXref = null;
        String profileUri = Lens.getDefaultLens();
        Set results = uriMapper.mapID(sourceXref, profileUri);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_profileUri_tgtUriPatterns() throws Exception {
        report("MapUri_sourceUri_profileUri_tgtUriPatterns");
        String sourceUri = null;
        String profileUri = Lens.getDefaultLens();
        UriPattern[] tgtUriPatterns = null;
        Set results = uriMapper.mapUri(sourceUri, profileUri, uriPattern2, uriPattern3);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_profileUri_tgtUriPattern() throws Exception {
        report("MapUri_sourceXref_profileUri_tgtUriPattern");
        Xref sourceXref = null;
        String profileUri = Lens.getDefaultLens();
        UriPattern tgtUriPattern = uriPattern3;
        Set results = uriMapper.mapUri(sourceXref, profileUri, tgtUriPattern);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_profileUri() throws Exception {
        report("MapUri_sourceXref_profileUri");
        Xref sourceXref = null;
        String profileUri = Lens.getDefaultLens();
        Set results = uriMapper.mapUri(sourceXref, profileUri);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_profileUri_tgtUriPatterns() throws Exception {
        report("MapUri_sourceXref_profileUri_tgtUriPatterns");
        Xref sourceXref = null;
        String profileUri = Lens.getDefaultLens();
        UriPattern[] tgtUriPatterns = null;
        Set results = uriMapper.mapUri(sourceXref, profileUri, uriPattern2, uriPattern3);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_profileUri_tgtUriPattern() throws Exception {
        report("MapUri_sourceUri_profileUri_tgtUriPattern");
        String sourceUri = null;
        String profileUri = Lens.getDefaultLens();
        UriPattern tgtUriPattern = uriPattern3;
        Set results = uriMapper.mapUri(sourceUri, profileUri, tgtUriPattern);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_profileUri() throws Exception {
        report("MapUri_sourceUri_profileUri");
        String sourceUri = null;
        String profileUri = Lens.getDefaultLens();
        Set results = uriMapper.mapUri(sourceUri, profileUri);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_profileUri_tgtDataSources() throws Exception {
        report("MapFull_sourceXref_profileUri_tgtDataSources");
        Xref sourceXref = null;
        String profileUri = Lens.getDefaultLens();
        DataSource[] tgtDataSources = null;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, profileUri, DataSource2, DataSource3);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_profileUri_tgtDataSource() throws Exception {
        report("MapFull_sourceXref_profileUri_tgtDataSources");
        Xref sourceXref = null;
        String profileUri = Lens.getDefaultLens();
        DataSource tgtDataSource = DataSource3;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, profileUri, tgtDataSource);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_profileUri() throws Exception {
        report("MapFull_sourceXref_profileUri_tgtDataSources");
        Xref sourceXref = null;
        String profileUri = Lens.getDefaultLens();
        Set<Mapping> results = uriMapper.mapFull(sourceXref, profileUri);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_profileUri_tgtUriPatterns() throws Exception {
        report("MapFull_sourceXref_profileUri_tgtUriPatterns");
        Xref sourceXref = null;
        String profileUri = Lens.getDefaultLens();
        UriPattern[] tgtUriPatterns = null;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, profileUri, uriPattern2, uriPattern3);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_profileUri_tgtUriPattern() throws Exception {
        report("MapFull_sourceXref_profileUri_tgtUriPattern");
        Xref sourceXref = null;
        String profileUri = Lens.getDefaultLens();
        UriPattern tgtUriPattern = uriPattern3;
        Set<Mapping> results = uriMapper.mapFull(sourceXref, profileUri, tgtUriPattern);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_profileUri_tgtDataSources() throws Exception {
        report("MapFull_sourceUri_profileUri_tgtDataSources");
        String sourceUri = null;
        Xref sourceXref = null;
        String profileUri = Lens.getDefaultLens();
        DataSource[] tgtDataSources = null;
        Set expResult = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, profileUri, DataSource2, DataSource3);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_profileUri_tgtDataSource() throws Exception {
        report("MapFull_sourceUri_profileUri_tgtDataSource");
        String sourceUri = null;
        Xref sourceXref = null;
        String profileUri = Lens.getDefaultLens();
        DataSource tgtDataSource = DataSource3;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, profileUri, tgtDataSource);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_MapFull_sourceUri_profileUri() throws Exception {
        report("MapFull_sourceUri_profileUri");
        String sourceUri = null;
        Xref sourceXref = null;
        String profileUri = Lens.getDefaultLens();
        Set<Mapping> results = uriMapper.mapFull(sourceUri, profileUri);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_profileUri_tgtUriPattern() throws Exception {
        report("MapFull_sourceUri_profileUri_tgtUriPattern");
        String sourceUri = null;
        Xref sourceXref = null;
        String profileUri = Lens.getDefaultLens();
        UriPattern tgtUriPattern = uriPattern3;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, profileUri, tgtUriPattern);
        assertTrue(results.isEmpty());
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceUri_profileUri_tgtUriPatterns() throws Exception {
        report("MapFull_sourceUri_profileUri_tgtUriPatterns");
        String sourceUri = null;
        Xref sourceXref = null;
        String profileUri = Lens.getDefaultLens();
        UriPattern[] tgtUriPatterns = null;
        Set<Mapping> results = uriMapper.mapFull(sourceUri, profileUri, uriPattern2, uriPattern3);
        assertTrue(results.isEmpty());
    }

}
