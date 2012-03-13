package org.bridgedb.ws;

import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperCapabilitiesTest;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public abstract class WSClientAsCapabilitiesTest  extends IDMapperCapabilitiesTest{
    
    @BeforeClass
    public static void setupIDMapper() {
        connectionOk = false;
        WSInterface webService = new WSClient("http://localhost:8080/OPS-IMS");
        try { 
            webService.isFreeSearchSupported();
            connectionOk = true;
        } catch (Exception ex) {
            System.err.println(ex);
            System.out.println ("***** SKIPPING WSClientTest ******");
            System.out.println ("Please make sure the server is running");
        }
        idMapper = new WSMapper(webService){
            public IDMapperCapabilities getCapabilities() {
                return this;
            }
        };
        org.junit.Assume.assumeTrue(connectionOk);        
    }

}
