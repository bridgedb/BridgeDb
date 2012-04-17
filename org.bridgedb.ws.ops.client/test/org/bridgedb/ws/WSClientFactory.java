/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import org.bridgedb.IDMapperException;
import org.bridgedb.url.URLMapperTestBase;

/**
 *
 * @author Christian
 */
public class WSClientFactory extends URLMapperTestBase{
    
    public static WSInterface createTestWSClient() throws IDMapperException{
        setupURLs();
        WSInterface webService = new WSClient("http://localhost:8080/OPS-IMS");
        try { 
            webService.isFreeSearchSupported();
        } catch (Exception ex) {
            System.err.println(ex);
            System.out.println ("***** SKIPPING WSClientTest ******");
            System.out.println ("Please make sure the server is running");
            org.junit.Assume.assumeTrue(false);        
        }
        System.out.println("map1URL1=" + map1URL1);
        if (!webService.urlExists(map1URL1).exists()){
            System.out.println ("***** SKIPPING WSClientTest ******");
            System.out.println ("It appears the Test data is not loaded");
            System.out.println ("remove ignore in TestDataToMainServerTest (org.bridgedb.ws.sqlserver) ");            
            org.junit.Assume.assumeTrue(false);        
        }
        return webService;
    }
}
