package org.bridgedb.ws;

import org.bridgedb.iterator.URLByPossitionTest;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class WSClientURLByPossitionTest  extends URLByPossitionTest{
    
    @BeforeClass
    public static void setupIDMapper() {
        WSInterface webService = WSClientFactory.createTestWSClient();
        urlByPossition = new WSMapper(webService);
    }

}
