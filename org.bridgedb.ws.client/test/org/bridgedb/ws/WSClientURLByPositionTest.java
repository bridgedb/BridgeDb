package org.bridgedb.ws;

import org.bridgedb.IDMapperException;
import org.bridgedb.iterator.URLByPositionTest;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class WSClientURLByPositionTest  extends URLByPositionTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException {
        WSInterface webService = WSClientFactory.createTestWSClient();
        urlByPosition = new WSMapper(webService);
    }

}
