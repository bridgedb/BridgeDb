package org.bridgedb.ws;

import org.bridgedb.url.URLMapperTestBase;
import java.util.Set;
import org.bridgedb.IDMapperException;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.*;

/**
 * @author Christian
 */
public abstract class URLByPossitionTest extends URLMapperTestBase {
    
    protected static URLByPossition urlByPossition; 

    @Test
    public void testbyPossition() throws IDMapperException{
        System.out.println("testbyPossition");
        String result0 = urlByPossition.getURLByPossition(0);
        assertNotNull(result0);
        String result1 = urlByPossition.getURLByPossition(1);
        assertThat(result1, not(result0));
        String result2 = urlByPossition.getURLByPossition(2);
        assertThat(result2, not(result0));
        assertThat(result2, not(result1));
    }

    @Test
    public void testbyPossitionAndLimit() throws IDMapperException{
        System.out.println("testbyPossitionAndLimit");
        Set<String> results1 = urlByPossition.getURLByPossition(0, 5);
        assertEquals(5, results1.size());
        //Only 4 in the seocnd ones as there may be only 9 mappings
        Set<String> results2 = urlByPossition.getURLByPossition(5, 4);
        assertEquals(4, results2.size());
        for (String url: results2){
            assertFalse(results1.contains(url));
        }
    }

    @Test
    public void testbyPossitionAndDataSource() throws IDMapperException{
        System.out.println("testbyPossitionAndDataSource");
        String result0 = urlByPossition.getURLByPossition(nameSpace2, 0);
        assertNotNull(result0);
        assertThat(result0, startsWith(nameSpace2));
        String result1 = urlByPossition.getURLByPossition(nameSpace3, 0);
        assertThat(result1, startsWith(nameSpace3));
        assertThat(result1, not(result0));
        String result2 = urlByPossition.getURLByPossition(nameSpace2, 1);
        assertThat(result2, not(result0));
        assertThat(result2, not(result1));
        assertThat(result2, startsWith(nameSpace2));
    }
        
    @Test
    public void testbyPossitionLimitAndDataSource() throws IDMapperException{
        System.out.println("testbyPossitionLimitAndDataSource");
        assertNotNull(nameSpace2);
        //There may be only three for 
        Set<String> results = urlByPossition.getURLByPossition(nameSpace2, 0, 3);
        assertEquals(3, results.size());
        for (String url: results){
            assertThat(url, startsWith(nameSpace2));
        }
    }
}
