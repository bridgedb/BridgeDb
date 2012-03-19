package org.bridgedb.ws;

import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperCapabilitiesTest;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class WSClientAsCapabilitiesTest  extends IDMapperCapabilitiesTest{
    
    @BeforeClass
    public static void setupIDMapper() {
        WSInterface webService = WSClientFactory.createTestWSClient();
        idMapper = new WSMapper(webService){
            @Override
            public IDMapperCapabilities getCapabilities() {
                return this;
            }
        };
    }

}
