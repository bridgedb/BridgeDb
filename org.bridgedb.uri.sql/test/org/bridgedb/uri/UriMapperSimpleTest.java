/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.uri;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.Xref;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.statistics.ProfileInfo;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Christian
 */
public class UriMapperSimpleTest extends UriListenerTest{

    static UriMapper instance;
    
    @BeforeClass
    public static void setUp() throws BridgeDBException {
        instance = new SQLUriMapper(false, StoreType.TEST);
        listener = (UriListener)instance;
        loadData();
    }
    
    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_profileUri_tgtDataSources() throws Exception {
        report("MapID_sourceXref_profileUri_tgtDataSources");
        Xref sourceXref = map2xref2;
        String profileUri = Profile.getDefaultProfile();
        Set results = instance.mapID(sourceXref, profileUri, DataSource2, DataSource3);
        assertFalse(results.contains(map2xref1));
        assertTrue(results.contains(map2xref2));
        assertTrue(results.contains(map2xref3));
        assertFalse(results.contains(map1xref2));
        assertFalse(results.contains(map1xref1));
        assertFalse(results.contains(map3xref2));
    }

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_profileUri_tgtDataSource() throws Exception {
        report("MapID_sourceXref_profileUri_tgtDataSource");
        Xref sourceXref = map2xref2;
        String profileUri = Profile.getDefaultProfile();
        DataSource tgtDataSource = DataSource3;
        Set results = instance.mapID(sourceXref, profileUri, tgtDataSource);
        assertFalse(results.contains(map2xref1));
        assertFalse(results.contains(map2xref2));
        assertTrue(results.contains(map2xref3));
        assertFalse(results.contains(map1xref2));
        assertFalse(results.contains(map1xref1));
        assertFalse(results.contains(map3xref2));
    }

    /**
     * Test of mapID method, of class UriMapper.
     */
    @Test
    public void testMapID_sourceXref_profileUri() throws Exception {
        report("MapID_sourceXref_profileUri");
        Xref sourceXref = map2xref2;
        String profileUri = Profile.getDefaultProfile();
        Set results = instance.mapID(sourceXref, profileUri);
        assertTrue(results.contains(map2xref1));
        assertTrue(results.contains(map2xref2));
        assertTrue(results.contains(map2xref3));
        assertFalse(results.contains(map1xref2));
        assertFalse(results.contains(map1xref1));
        assertFalse(results.contains(map3xref2));
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_profileUri_tgtUriPatterns() throws Exception {
        report("MapUri_sourceUri_profileUri_tgtUriPatterns");
        String sourceUri = map3Uri3;
        String profileUri = Profile.getDefaultProfile();
        UriPattern[] tgtUriPatterns = null;
        Set results = instance.mapUri(sourceUri, profileUri, uriPattern2, uriPattern3);
        assertFalse(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_profileUri_tgtUriPattern() throws Exception {
        report("MapUri_sourceXref_profileUri_tgtUriPattern");
        Xref sourceXref = map3xref2;
        String profileUri = Profile.getDefaultProfile();
        UriPattern tgtUriPattern = uriPattern3;
        Set results = instance.mapUri(sourceXref, profileUri, tgtUriPattern);
        assertFalse(results.contains(map3Uri1));
        assertFalse(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_profileUri() throws Exception {
        report("MapUri_sourceXref_profileUri");
        Xref sourceXref = map3xref2;
        String profileUri = Profile.getDefaultProfile();
        Set results = instance.mapUri(sourceXref, profileUri);
        assertTrue(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertTrue(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceXref_profileUri_tgtUriPatterns() throws Exception {
        report("MapUri_sourceXref_profileUri_tgtUriPatterns");
        Xref sourceXref = map3xref2;
        String profileUri = Profile.getDefaultProfile();
        UriPattern[] tgtUriPatterns = null;
        Set results = instance.mapUri(sourceXref, profileUri, uriPattern2, uriPattern3);
        assertFalse(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_profileUri_tgtUriPattern() throws Exception {
        report("MapUri_sourceUri_profileUri_tgtUriPattern");
        String sourceUri = map3Uri2;
        String profileUri = Profile.getDefaultProfile();
        UriPattern tgtUriPattern = uriPattern3;
        Set results = instance.mapUri(sourceUri, profileUri, tgtUriPattern);
        assertFalse(results.contains(map3Uri1));
        assertFalse(results.contains(map3Uri2));
        assertFalse(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
    }

    /**
     * Test of mapUri method, of class UriMapper.
     */
    @Test
    public void testMapUri_sourceUri_profileUri() throws Exception {
        report("MapUri_sourceUri_profileUri");
        String sourceUri = map3Uri2;
        String profileUri = Profile.getDefaultProfile();
        Set results = instance.mapUri(sourceUri, profileUri);
        assertTrue(results.contains(map3Uri1));
        assertTrue(results.contains(map3Uri2));
        assertTrue(results.contains(map3Uri2a));
        assertTrue(results.contains(map3Uri3));
        assertFalse(results.contains(map2Uri2));
        assertFalse(results.contains(map1Uri3));
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testMapFull_sourceXref_profileUri_tgtDataSources() throws Exception {
        report("MapFull_sourceXref_profileUri_tgtDataSources");
        Xref sourceXref = map3xref2;
        String profileUri = Profile.getDefaultProfile();
        DataSource[] tgtDataSources = null;
        Set<Mapping> results = instance.mapFull(sourceXref, profileUri, DataSource2, DataSource3);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSourceXref());
            if (!mapping.getTargetXref().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getId(), not(equalTo(null)));
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTargetXref());
            assertFalse(ids.contains(mapping.getId()));
            ids.add(mapping.getId());
        }
        assertFalse(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
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
    @Test
    public void testMapFull_sourceXref_profileUri_tgtDataSource() throws Exception {
        report("MapFull_sourceXref_profileUri_tgtDataSources");
        Xref sourceXref = map3xref2;
        String profileUri = Profile.getDefaultProfile();
        DataSource tgtDataSource = DataSource3;
        Set<Mapping> results = instance.mapFull(sourceXref, profileUri, tgtDataSource);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSourceXref());
            if (!mapping.getTargetXref().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getId(), not(equalTo(null)));
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTargetXref());
            assertEquals(tgtDataSource, mapping.getTargetXref().getDataSource());
            assertFalse(ids.contains(mapping.getId()));
            ids.add(mapping.getId());
        }
        assertFalse(targetUris.contains(map3Uri1));
        assertFalse(targetUris.contains(map3Uri2));
        assertFalse(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
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
    public void testMapFull_sourceXref_profileUri() throws Exception {
        report("MapFull_sourceXref_profileUri_tgtDataSources");
        Xref sourceXref = map3xref2;
        String profileUri = Profile.getDefaultProfile();
        Set<Mapping> results = instance.mapFull(sourceXref, profileUri);
        Set<String> targetUris = new HashSet<String>();
        Set<Xref> targetXrefs = new HashSet<Xref>();
        Set<Integer> ids = new HashSet<Integer>(); 
        for (Mapping mapping:results){
            assertEquals(sourceXref, mapping.getSourceXref());
            if (!mapping.getTargetXref().equals(sourceXref)){
                assertThat(mapping.getPredicate(), not(equalTo(null)));            
                assertThat(mapping.getId(), not(equalTo(null)));
                assertThat(mapping.getMappingSetId(), not(equalTo(null)));
            }
            targetUris.addAll(mapping.getTargetUri());
            targetXrefs.add(mapping.getTargetXref());
            assertFalse(ids.contains(mapping.getId()));
            ids.add(mapping.getId());
        }
        assertTrue(targetUris.contains(map3Uri1));
        assertTrue(targetUris.contains(map3Uri2));
        assertTrue(targetUris.contains(map3Uri2a));
        assertTrue(targetUris.contains(map3Uri3));
        assertFalse(targetUris.contains(map2Uri2));
        assertFalse(targetUris.contains(map1Uri3));
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
    @Ignore
    public void testMapFull_sourceXref_profileUri_tgtUriPatterns() throws Exception {
        report("MapFull_sourceXref_profileUri_tgtUriPatterns");
        Xref sourceXref = map3xref2;
        String profileUri = Profile.getDefaultProfile();
        UriPattern[] tgtUriPatterns = null;
        Set expResult = null;
        Set result = instance.mapFull(sourceXref, profileUri, tgtUriPatterns);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    @Ignore
    public void testMapFull_sourceXref_profileUri_tgtUriPattern() throws Exception {
        report("MapFull_sourceXref_profileUri_tgtUriPattern");
        Xref sourceXref = map3xref2;
        String profileUri = Profile.getDefaultProfile();
        UriPattern tgtUriPattern = null;
        Set expResult = null;
        Set result = instance.mapFull(sourceXref, profileUri, tgtUriPattern);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    @Ignore
    public void testMapFull_sourceUri_profileUri_tgtDataSources() throws Exception {
        report("MapFull_sourceUri_profileUri_tgtDataSources");
        String sourceUri = map3Uri2;
        String profileUri = Profile.getDefaultProfile();
        DataSource[] tgtDataSources = null;
        Set expResult = null;
        Set result = instance.mapFull(sourceUri, profileUri, tgtDataSources);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    @Ignore
    public void testMapFull_sourceUri_profileUri_tgtDataSource() throws Exception {
        report("MapFull_sourceUri_profileUri_tgtDataSource");
        String sourceUri = map3Uri2;
        String profileUri = Profile.getDefaultProfile();
        DataSource tgtDataSource = null;
        Set expResult = null;
        Set result = instance.mapFull(sourceUri, profileUri, tgtDataSource);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    @Ignore
    public void testMapFull_MapFull_sourceUri_profileUri() throws Exception {
        report("MapFull_sourceUri_profileUri");
        String sourceUri = map3Uri2;
        String profileUri = Profile.getDefaultProfile();
        Set expResult = null;
        Set result = instance.mapFull(sourceUri, profileUri);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    @Ignore
    public void testMapFull_sourceUri_profileUri_tgtUriPattern() throws Exception {
        report("MapFull_sourceUri_profileUri_tgtUriPattern");
        String sourceUri = map3Uri2;
        String profileUri = Profile.getDefaultProfile();
        UriPattern tgtUriPattern = null;
        Set expResult = null;
        Set result = instance.mapFull(sourceUri, profileUri, tgtUriPattern);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    @Ignore
    public void testMapFull_sourceUri_profileUri_tgtUriPatterns() throws Exception {
        report("MapFull_sourceUri_profileUri_tgtUriPatterns");
        String sourceUri = map3Uri2;
        String profileUri = Profile.getDefaultProfile();
        UriPattern[] tgtUriPatterns = null;
        Set expResult = null;
        Set result = instance.mapFull(sourceUri, profileUri, tgtUriPatterns);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of uriExists method, of class UriMapper.
     */
    @Test
    @Ignore
    public void testUriExists() throws Exception {
        report("uriExists");
        String uri = "";
        boolean expResult = false;
        boolean result = instance.uriExists(uri);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of uriSearch method, of class UriMapper.
     */
    @Test
    @Ignore
    public void testUriSearch() throws Exception {
        report("uriSearch");
        String text = "";
        int limit = 0;
        Set expResult = null;
        Set result = instance.uriSearch(text, limit);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCapabilities method, of class UriMapper.
     */
    @Test
    @Ignore
    public void testGetCapabilities() {
        report("getCapabilities");
        IDMapperCapabilities expResult = null;
        IDMapperCapabilities result = instance.getCapabilities();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of close method, of class UriMapper.
     */
    @Test
    @Ignore
    public void testClose() throws Exception {
        report("close");
        instance.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isConnected method, of class UriMapper.
     */
    @Test
    @Ignore
    public void testIsConnected() {
        report("isConnected");
        boolean expResult = false;
        boolean result = instance.isConnected();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toXref method, of class UriMapper.
     */
    @Test
    @Ignore
    public void testToXref() throws Exception {
        report("toXref");
        String uri = "";
        Xref expResult = null;
        Xref result = instance.toXref(uri);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMapping method, of class UriMapper.
     */
    @Test
    @Ignore
    public void testGetMapping() throws Exception {
        report("getMapping");
        int id = 0;
        Mapping expResult = null;
        Mapping result = instance.getMapping(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSampleMapping method, of class UriMapper.
     */
    @Test
    @Ignore
    public void testGetSampleMapping() throws Exception {
        report("getSampleMapping");
        List expResult = null;
        List result = instance.getSampleMapping();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOverallStatistics method, of class UriMapper.
     */
    @Test
    @Ignore
    public void testGetOverallStatistics() throws Exception {
        report("getOverallStatistics");
        OverallStatistics expResult = null;
        OverallStatistics result = instance.getOverallStatistics();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMappingSetInfo method, of class UriMapper.
     */
    @Test
    @Ignore
    public void testGetMappingSetInfo() throws Exception {
        report("getMappingSetInfo");
        int mappingSetId = 0;
        MappingSetInfo expResult = null;
        MappingSetInfo result = instance.getMappingSetInfo(mappingSetId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMappingSetInfos method, of class UriMapper.
     */
    @Test
    @Ignore
    public void testGetMappingSetInfos() throws Exception {
        report("getMappingSetInfos");
        String sourceSysCode = "";
        String targetSysCode = "";
        List expResult = null;
        List result = instance.getMappingSetInfos(sourceSysCode, targetSysCode);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUriPatterns method, of class UriMapper.
     */
    @Test
    @Ignore
    public void testGetUriPatterns() throws Exception {
        report("getUriPatterns");
        String dataSource = "";
        Set expResult = null;
        Set result = instance.getUriPatterns(dataSource);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getProfiles method, of class UriMapper.
     */
    @Test
    @Ignore
    public void testGetProfiles() throws Exception {
        report("getProfiles");
        List expResult = null;
        List result = instance.getProfiles();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getProfile method, of class UriMapper.
     */
    @Test
    @Ignore
    public void testGetProfile() throws Exception {
        report("getProfile");
        String profileUri = Profile.getDefaultProfile();
        ProfileInfo expResult = null;
        ProfileInfo result = instance.getProfile(profileUri);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSqlCompatVersion method, of class UriMapper.
     */
    @Test
    @Ignore
    public void testGetSqlCompatVersion() throws Exception {
        report("getSqlCompatVersion");
        int expResult = 0;
        int result = instance.getSqlCompatVersion();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
