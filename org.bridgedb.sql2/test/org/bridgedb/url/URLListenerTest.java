package org.bridgedb.url;

import org.bridgedb.mapping.MappingListener;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTestBase;
import org.junit.Test;

/**
 *
 * @author Christian
 */
public abstract class URLListenerTest extends URLMapperTestBase{
        
    protected static URLListener listener;

    @Test
    public void testLinkListener() throws IDMapperException{
        listener.registerUriSpace(DataSource1, URISpace1);
        listener.registerUriSpace(DataSource2, URISpace2);
        listener.registerUriSpace(DataSource3, URISpace3);
    }

}
