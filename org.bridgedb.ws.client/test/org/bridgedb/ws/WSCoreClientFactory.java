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
        System.out.println("in WSCoreInterface 1");
        WSCoreInterface webService = new WSCoreClient("http://localhost:8080/OPS-IMS");
        System.out.println("in WSCoreInterface 2");
        try { 
            webService.isFreeSearchSupported();
            System.out.println("in WSCoreInterface 3");
        } catch (Exception ex) {
            System.err.println(ex);
            System.out.println ("***** SKIPPING WSClientTest ******");
            System.out.println ("Please make sure the server is running");
            org.junit.Assume.assumeTrue(false);        
        }
        System.out.println("in WSCoreInterface 4");
        if (!webService.urlExists(map1URL1).exists()){
        System.out.println("in WSCoreInterface 5a");
            System.out.println ("***** SKIPPING WSClientTest ******");
            System.out.println ("It appears the Test data is not loaded");
            System.out.println ("remove ignore in TestDataToMainServerTest (org.bridgedb.ws.sqlserver) ");            
            org.junit.Assume.assumeTrue(false);        
        }
        System.out.println("in WSCoreInterface 5b");
        return webService;
    }
}
