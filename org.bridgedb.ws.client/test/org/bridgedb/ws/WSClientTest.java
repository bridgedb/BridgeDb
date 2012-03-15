/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import org.bridgedb.IDMapperTest;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class WSClientTest  extends WSTest{
    
    @BeforeClass
    public static void setupIDMapper() {
        connectionOk = false;
        webService = new WSClient("http://localhost:8080/OPS-IMS");
        try { 
            webService.isFreeSearchSupported();
            connectionOk = true;
        } catch (Exception ex) {
            System.err.println(ex);
            System.out.println ("***** SKIPPING WSClientTest ******");
            System.out.println ("Please make sure the server is running");
        }
        idMapper = new WSMapper(webService);
        org.junit.Assume.assumeTrue(connectionOk);        
    }

}
