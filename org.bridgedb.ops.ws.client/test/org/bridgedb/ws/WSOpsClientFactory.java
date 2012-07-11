/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTestBase;

/**
 *
 * @author Christian
 */
public class WSOpsClientFactory extends IDMapperTestBase{
    
    public static WSOpsInterface createTestWSClient() throws IDMapperException{
        System.out.println("in WSCoreInterface 1");
        WSOpsInterface webService = new WSOpsClient("http://localhost:8080/OPS-IMS");
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
        if (!webService.isMappingSupported(DataSource1.getSystemCode(), DataSource2.getSystemCode()).isMappingSupported()){
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
