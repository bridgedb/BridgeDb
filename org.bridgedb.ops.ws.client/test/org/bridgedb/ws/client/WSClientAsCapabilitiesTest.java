package org.bridgedb.ws.client;

import org.bridgedb.IDMapperCapabilitiesTest;
import org.bridgedb.IDMapperException;
import org.bridgedb.ws.WSOpsClientFactory;
import org.bridgedb.ws.WSOpsInterface;
import org.bridgedb.ws.WSOpsMapper;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 *
 * @author Christian
 */
public class WSClientAsCapabilitiesTest  extends IDMapperCapabilitiesTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException {
        WSOpsInterface webService = WSOpsClientFactory.createTestWSClient();
        capabilities = new WSOpsMapper(webService);
    }

}
