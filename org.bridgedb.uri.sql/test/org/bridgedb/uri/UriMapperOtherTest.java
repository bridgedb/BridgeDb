/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.uri;

import java.util.List;
import org.bridgedb.uri.tools.Lens;
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
        List<Lens> results = Lens.getLens();
        assertThat(results.size(), greaterThanOrEqualTo(2));
     }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testGetDefaultLens() throws Exception {
        report("GetDefaultLens");
        Lens result = Lens.byId(Lens.getDefaultLens());
        assertEquals(Lens.getDefaultLens(), result.getId());
        assertNotNull(result.getCreatedBy());
        assertNotNull(result.getCreatedOn());
        assertNotNull(result.getName());
        assertThat(result.getJustifications().size(), greaterThanOrEqualTo(1));
     }

    /**
     * Test of mapFull method, of class UriMapper.
     */
    @Test
    public void testGetAllLens() throws Exception {
        report("GetAllLens");
        Lens result = Lens.byId(Lens.getAllLens());
        assertEquals(Lens.getAllLens(), result.getId());
        //assertNotNull(result.getCreatedBy());
        assertNotNull(result.getDescription());
        assertNotNull(result.getName());
        assertThat(result.getJustifications().size(), greaterThanOrEqualTo(1));
     }
}
