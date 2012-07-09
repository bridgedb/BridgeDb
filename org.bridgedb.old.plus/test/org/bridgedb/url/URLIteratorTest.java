/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.url;

import org.bridgedb.*;
import java.util.HashSet;
import java.util.Iterator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public abstract class URLIteratorTest extends URLMapperTestBase{
    //Must be set by supclasses.
    protected static URISpace urlIterator;
    
    @Test
    public void TestAllDataSourcesIterator() throws IDMapperException{
        report("TestAllDataSourcesIterator");
        Iterable<String> iterable = urlIterator.getURLIterator();
        Iterator<String> iterator = iterable.iterator();
        HashSet<String> asSet = new HashSet<String>();
        while (iterator.hasNext()){
            asSet.add(iterator.next());
        }
        assertTrue(asSet.contains(map1URL1));
        assertTrue(asSet.contains(map1URL3));
        assertTrue(asSet.contains(map2URL2));
        assertTrue(asSet.contains(map3URL3));
        assertFalse(asSet.contains(mapBadURL1));
        assertFalse(asSet.contains(mapBadURL2));
        assertFalse(asSet.contains(mapBadURL3));
    }

    @Test
    public void TestOneDataSourcesIterator() throws IDMapperException{
        report("TestOneDataSourcesIterator");
        Iterable<String> iterable = urlIterator.getURLIterator(URISpace2);
        Iterator<String> iterator = iterable.iterator();
        HashSet<String> asSet = new HashSet<String>();
        while (iterator.hasNext()){
            asSet.add(iterator.next());
        }
        assertFalse(asSet.contains(map1URL1));
        assertFalse(asSet.contains(map1URL3));
        assertTrue(asSet.contains(map1URL2));
        assertTrue(asSet.contains(map2URL2));
        assertTrue(asSet.contains(map3URL2));
        assertFalse(asSet.contains(map3URL1));
        assertFalse(asSet.contains(map3URL3));
        assertFalse(asSet.contains(mapBadURL1));
        assertFalse(asSet.contains(mapBadURL2));
        assertFalse(asSet.contains(mapBadURL3));
    }

}
