/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.uri;

import java.util.List;
import org.bridgedb.statistics.LensInfo;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public abstract class UriMapperOtherTest extends UriListenerTest{


    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testGetProfiles() throws Exception {
        report("getProfiles");
        List<LensInfo> results = uriMapper.getProfiles();
        assertThat(results.size(), greaterThanOrEqualTo(2));
     }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testGetDefaultProfile() throws Exception {
        report("GetDefaultProfile");
        LensInfo result = uriMapper.getProfile(Profile.getDefaultProfile());
        assertEquals(Profile.getDefaultProfile(), result.getUri());
        assertNotNull(result.getCreatedBy());
        assertNotNull(result.getCreatedOn());
        assertNotNull(result.getName());
        assertThat(result.getJustification().size(), greaterThanOrEqualTo(1));
     }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testGetAllProfile() throws Exception {
        report("GetAllProfile");
        LensInfo result = uriMapper.getProfile(Profile.getAllProfile());
        assertEquals(Profile.getAllProfile(), result.getUri());
        assertNotNull(result.getCreatedBy());
        assertNotNull(result.getCreatedOn());
        assertNotNull(result.getName());
        assertThat(result.getJustification().size(), greaterThanOrEqualTo(1));
     }
}
