package org.bridgedb.ws.client;

import org.bridgedb.IDMapperException;
import org.bridgedb.ws.WSOpsClientFactory;
import org.bridgedb.ws.WSOpsInterface;
import org.bridgedb.ws.WSOpsMapper;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class IndirectIDMapperCapabilitiesTest  extends org.bridgedb.IDMapperWithCapabilitiesTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException {
        WSOpsInterface webService = WSOpsClientFactory.createTestWSClient();
        capabilities = new WSOpsMapper(webService);
    }

}
