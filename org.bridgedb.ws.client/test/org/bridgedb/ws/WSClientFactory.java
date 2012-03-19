/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

/**
 *
 * @author Christian
 */
public class WSClientFactory {
    
    public static WSInterface createTestWSClient(){
        WSInterface webService = new WSClient("http://localhost:8080/OPS-IMS");
        try { 
            webService.isFreeSearchSupported();
        } catch (Exception ex) {
            System.err.println(ex);
            System.out.println ("***** SKIPPING WSClientTest ******");
            System.out.println ("Please make sure the server is running");
            org.junit.Assume.assumeTrue(false);        
        }
        return webService;
    }
}
