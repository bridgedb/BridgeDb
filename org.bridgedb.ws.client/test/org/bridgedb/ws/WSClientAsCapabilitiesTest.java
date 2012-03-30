package org.bridgedb.ws;

import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperCapabilitiesTest;
import org.bridgedb.IDMapperException;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 *
 * @author Christian
 */
public class WSClientAsCapabilitiesTest  extends IDMapperCapabilitiesTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException {
        WSInterface webService = WSClientFactory.createTestWSClient();
        idMapper = new WSMapper(webService){
            @Override
            public IDMapperCapabilities getCapabilities() {
                return this;
            }
        };
    }

}
