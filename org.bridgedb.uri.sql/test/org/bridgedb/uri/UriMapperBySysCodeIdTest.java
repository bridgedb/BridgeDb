/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.uri;

import java.util.HashSet;
import java.util.Set;
import org.bridgedb.rdf.UriPattern;
import org.hamcrest.Matcher;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import static org.junit.matchers.JUnitMatchers.hasItem;

/**
 *
 * @author Christian
 */
public abstract class UriMapperBySysCodeIdTest extends UriListenerTest{

    private static  final String EMPTY_GRAPH = "";

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_lensId_tgtUriPatterns() throws Exception {
        report("MapUri_sourceUri_lensId_tgtUriPatterns");
        String sourceUri = map3Uri3;
        String lensId = null;
        UriPattern[] tgtUriPatterns = null;
        MappingsBySysCodeId results = uriMapper.mapUriBySysCodeId(sourceUri, lensId, EMPTY_GRAPH, regexUriPattern2, regexUriPattern3);

        Set<String> sysCodes = results.getSysCodes();
        assertThat(sysCodes, (Matcher) hasItem(DataSource1.getSystemCode()));
        assertThat(sysCodes, (Matcher) hasItem(DataSource2.getSystemCode()));
        assertThat(sysCodes, (Matcher) hasItem(DataSource3.getSystemCode()));
        
        Set<String> ids = results.getIds(DataSource2.getSystemCode());
        assertThat(ids, (Matcher) hasItem(ds2Id3));
        
        Set<String> uris = results.getUris(DataSource2.getSystemCode(), ds2Id3);
        assertThat(ids, (Matcher) hasItem(map3Uri2));
        
        //assertFalse(results.contains(map3Uri1));
        //assertTrue(results.contains(map3Uri2));
        //assertFalse(results.contains(map3Uri2a));
        //assertTrue(results.contains(map3Uri3));
        //assertFalse(results.contains(map2Uri2));
        //assertFalse(results.contains(map1Uri3));
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUris_lensId_tgtUriPatterns() throws Exception {
        report("MapUri_sourceUri_lensId_tgtUriPatterns");
        Set<String> sourceUris = new HashSet<String>();
        sourceUris.add(map3Uri3);
        sourceUris.add(map1Uri3);
        String lensId = null;
        UriPattern[] tgtUriPatterns = null;
        MappingsBySysCodeId results = uriMapper.mapUriBySysCodeId(sourceUris, lensId, EMPTY_GRAPH, regexUriPattern3);

        Set<String> sysCodes = results.getSysCodes();
        assertThat(sysCodes, (Matcher) hasItem(DataSource1.getSystemCode()));
        assertThat(sysCodes, (Matcher) hasItem(DataSource2.getSystemCode()));
        assertThat(sysCodes, (Matcher) hasItem(DataSource3.getSystemCode()));
        
        Set<String> ids = results.getIds(DataSource2.getSystemCode());
        assertThat(ids, (Matcher) hasItem(ds2Id3));
        Set<String> uris = results.getUris(DataSource2.getSystemCode(), ds2Id3);
        assertThat(ids, (Matcher) hasItem(map3Uri2));
        
        ids = results.getIds(DataSource1.getSystemCode());
        assertThat(ids, (Matcher) hasItem(ds1Id1));
        uris = results.getUris(DataSource1.getSystemCode(), ds1Id1);
        assertThat(ids, (Matcher) hasItem(map3Uri1));
                
        //assertFalse(results.contains(map3Uri1));
        //assertTrue(results.contains(map3Uri2));
        //assertFalse(results.contains(map3Uri2a));
        //assertTrue(results.contains(map3Uri3));
        //assertFalse(results.contains(map2Uri2));
        //assertFalse(results.contains(map1Uri3));
    }
 
}
