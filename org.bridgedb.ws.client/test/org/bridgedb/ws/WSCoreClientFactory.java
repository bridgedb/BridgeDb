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
public class WSCoreClientFactory extends IDMapperTestBase{
    
    public static WSCoreInterface createTestWSClient() throws IDMapperException{
        System.out.println("in WSCoreInterface 1");
        WSCoreInterface webService = new WSCoreClient("http://localhost:8080/BridgeDb");
        System.out.println("in WSCoreInterface 2");
        try { 
            webService.isFreeSearchSupported();
            System.out.println("in WSCoreInterface 3");
        } catch (Exception ex) {
            ex.printStackTrace();
            report("***** SKIPPING WSClientTest ******");
            report("Please make sure the server is running");
            org.junit.Assume.assumeTrue(false);        
        }
        System.out.println("in WSCoreInterface 4");
        if (!webService.isMappingSupported(DataSource1.getSystemCode(), DataSource2.getSystemCode()).isMappingSupported()){
            System.out.println("in WSCoreInterface 5a");
            report ("***** SKIPPING WSClientTest ******");
            report ("It appears the Test data is not loaded");
            report("remove ignore in TestDataToMainServerTest (org.bridgedb.ws.sqlserver) ");            
            org.junit.Assume.assumeTrue(false);        
        }
        System.out.println("in WSCoreInterface 5b");
        return webService;
    }
}
