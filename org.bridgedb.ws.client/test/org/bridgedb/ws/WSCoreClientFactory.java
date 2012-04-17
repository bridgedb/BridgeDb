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
public class WSCoreClientFactory extends URLMapperTestBase{
    
    public static WSCoreInterface createTestWSClient() throws IDMapperException{
        setupURLs();
        WSCoreInterface webService = new WSCoreClient("http://localhost:8080/OPS-IMS");
        try { 
            webService.isFreeSearchSupported();
        } catch (Exception ex) {
            System.err.println(ex);
            System.out.println ("***** SKIPPING WSClientTest ******");
            System.out.println ("Please make sure the server is running");
            org.junit.Assume.assumeTrue(false);        
        }
        if (!webService.urlExists(map1URL1).exists()){
            System.out.println ("***** SKIPPING WSClientTest ******");
            System.out.println ("It appears the Test data is not loaded");
            System.out.println ("remove ignore in TestDataToMainServerTest (org.bridgedb.ws.sqlserver) ");            
            org.junit.Assume.assumeTrue(false);        
        }
        return webService;
    }
}
