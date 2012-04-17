package org.bridgedb.ws.client;

import org.bridgedb.IDMapperException;
import org.bridgedb.ws.WSClientFactory;
import org.bridgedb.ws.WSInterface;
import org.bridgedb.ws.WSMapper;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class URLByPositionTest  extends org.bridgedb.iterator.URLByPositionTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException {
        WSInterface webService = WSClientFactory.createTestWSClient();
        urlByPosition = new WSMapper(webService);
    }

}
