package org.bridgedb.linkset;

import java.util.Date;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTest;
import org.bridgedb.linkset.LinkListener;
import org.junit.Ignore;

/**
 *
 * @author Christian
 */
@Ignore
public abstract class IDMapperAndLinkListenerTest extends IDMapperTest{
   
    //The Listener may be but needs not be the same as the IDMapper
    protected static LinkListener listener;
        
    /**
     * Suggested method to load the data.
     * 
     * Must be called or replaced.
     * @throws IDMapperException 
     */
    protected static void defaultLoadData() throws IDMapperException{
        listener.openInput();

        listener.insertLink(map1xref1, map1xref2);
        listener.insertLink(map2xref1, map2xref2);
        listener.insertLink(map3xref1, map3xref2);
        
        listener.insertLink(map1xref1, map1xref3);
        listener.insertLink(map2xref1, map2xref3);
        listener.insertLink(map3xref1, map3xref3);

        listener.insertLink(map1xref2, map1xref1);
        listener.insertLink(map2xref2, map2xref1);
        listener.insertLink(map3xref2, map3xref1);

        listener.insertLink(map1xref2, map1xref3);
        listener.insertLink(map2xref2, map2xref3);
        listener.insertLink(map3xref2, map3xref3);

        listener.insertLink(map1xref3, map1xref1);
        listener.insertLink(map2xref3, map2xref1);
        listener.insertLink(map3xref3, map3xref1);
 
        listener.insertLink(map1xref3, map1xref2);
        listener.insertLink(map2xref3, map2xref2);
        listener.insertLink(map3xref3, map3xref2);
        listener.closeInput();
     }
    
}
