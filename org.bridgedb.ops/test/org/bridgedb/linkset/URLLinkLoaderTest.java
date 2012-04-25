package org.bridgedb.linkset;

import java.util.Set;
import java.util.Date;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTest;
import org.bridgedb.IDMapperTestBase;
import org.bridgedb.url.URLMapperTestBase;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public abstract class URLLinkLoaderTest extends URLMapperTestBase {
   
    //The Listener may be but needs not be the same as the IDMapper
    protected static URLLinkListener listener;
        
    /**
     * Suggested method to load the data.
     * 
     * Must be called or replaced.
     * @throws IDMapperException 
     */
    protected static void defaultLoadData() throws IDMapperException{
        listener.openInput();

        listener.registerProvenanceLink(link1to2, DataSource1, TEST_PREDICATE, DataSource2);
        listener.insertLink(map1URL1, map1URL2, link1to2);
        listener.insertLink(map2URL1, map2URL2, link1to2);
        listener.insertLink(map3URL1, map3URL2, link1to2);

        listener.registerProvenanceLink(link1to3, DataSource1, TEST_PREDICATE, DataSource3);
        listener.insertLink(map1URL1, map1URL3, link1to3);
        listener.insertLink(map2URL1, map2URL3, link1to3);
        listener.insertLink(map3URL1, map3URL3, link1to3);
        
        listener.registerProvenanceLink(link2to1, DataSource2, TEST_PREDICATE, DataSource1);
        listener.insertLink(map1URL2, map1URL1, link2to1);
        listener.insertLink(map2URL2, map2URL1, link2to1);
        listener.insertLink(map3URL2, map3URL1, link2to1);

        listener.registerProvenanceLink(link2to3, DataSource2, TEST_PREDICATE, DataSource3);
        listener.insertLink(map1URL2, map1URL3, link2to3);
        listener.insertLink(map2URL2, map2URL3, link2to3);
        listener.insertLink(map3URL2, map3URL3, link2to3);

        listener.registerProvenanceLink(link3to1, DataSource3, TEST_PREDICATE, DataSource1);
        listener.insertLink(map1URL3, map1URL1, link3to1);
        listener.insertLink(map2URL3, map2URL1, link3to1);
        listener.insertLink(map3URL3, map3URL1, link3to1);

        listener.registerProvenanceLink(link3to2, DataSource3, TEST_PREDICATE, DataSource2);
        listener.insertLink(map1URL3, map1URL2, link3to2);
        listener.insertLink(map2URL3, map2URL2, link3to2);
        listener.insertLink(map3URL3, map3URL2, link3to2);
        
        listener.closeInput();
 
        Set<String> results = listener.getProvenanceIds();
        assertTrue(results.contains(link1to2));
        assertTrue(results.contains(link1to3));
        assertTrue(results.contains(link2to1));
        assertTrue(results.contains(link2to3));
        assertTrue(results.contains(link3to1));
        assertTrue(results.contains(link3to2));
    }
    
}
