/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.uri;

import java.util.List;
import org.bridgedb.statistics.LensInfo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Christian
 */
public abstract class UriMapperOtherTest extends UriListenerTest{


    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testGetLenss() throws Exception {
        report("getLenss");
        List<LensInfo> results = uriMapper.getLens();
        assertThat(results.size(), greaterThanOrEqualTo(2));
     }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testGetDefaultLens() throws Exception {
        report("GetDefaultLens");
        LensInfo result = uriMapper.getLens(Lens.getDefaultLens());
        assertEquals(Lens.getDefaultLens(), result.getUri());
        assertNotNull(result.getCreatedBy());
        assertNotNull(result.getCreatedOn());
        assertNotNull(result.getName());
        assertThat(result.getJustification().size(), greaterThanOrEqualTo(1));
     }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testGetAllLens() throws Exception {
        report("GetAllLens");
        LensInfo result = uriMapper.getLens(Lens.getAllLens());
        assertEquals(Lens.getAllLens(), result.getUri());
        assertNotNull(result.getCreatedBy());
        assertNotNull(result.getCreatedOn());
        assertNotNull(result.getName());
        assertThat(result.getJustification().size(), greaterThanOrEqualTo(1));
     }
}
