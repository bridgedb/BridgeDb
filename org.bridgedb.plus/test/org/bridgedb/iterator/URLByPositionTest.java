package org.bridgedb.iterator;

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
public abstract class URLByPositionTest extends URLMapperTestBase {
    
    protected static URLByPosition urlByPosition; 

    @Test
    public void testbyPosition() throws IDMapperException{
        System.out.println("testbyPosition");
        String result0 = urlByPosition.getURLByPosition(0);
        assertNotNull(result0);
        String result1 = urlByPosition.getURLByPosition(1);
        assertThat(result1, not(result0));
        String result2 = urlByPosition.getURLByPosition(2);
        assertThat(result2, not(result0));
        assertThat(result2, not(result1));
    }

    @Test
    public void testbyPositionAndLimit() throws IDMapperException{
        System.out.println("testbyPositionAndLimit");
        Set<String> results1 = urlByPosition.getURLByPosition(0, 5);
        assertEquals(5, results1.size());
        //Only 4 in the seocnd ones as there may be only 9 mappings
        Set<String> results2 = urlByPosition.getURLByPosition(5, 4);
        assertEquals(4, results2.size());
        for (String url: results2){
            assertFalse(results1.contains(url));
        }
    }

    @Test
    public void testbyPositionAndDataSource() throws IDMapperException{
        System.out.println("testbyPositionAndDataSource");
        String result0 = urlByPosition.getURLByPosition(nameSpace2, 0);
        assertNotNull(result0);
        assertThat(result0, startsWith(nameSpace2));
        String result1 = urlByPosition.getURLByPosition(nameSpace3, 0);
        assertThat(result1, startsWith(nameSpace3));
        assertThat(result1, not(result0));
        String result2 = urlByPosition.getURLByPosition(nameSpace2, 1);
        assertThat(result2, not(result0));
        assertThat(result2, not(result1));
        assertThat(result2, startsWith(nameSpace2));
    }
        
    @Test
    public void testbyPositionLimitAndDataSource() throws IDMapperException{
        System.out.println("testbyPositionLimitAndDataSource");
        assertNotNull(nameSpace2);
        //There may be only three for 
        Set<String> results = urlByPosition.getURLByPosition(nameSpace2, 0, 3);
        assertEquals(3, results.size());
        for (String url: results){
            assertThat(url, startsWith(nameSpace2));
        }
    }
}
