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
        //ystem.out.println("in WSCoreInterface 1");
        WSCoreInterface webService = new WSCoreClient("http://localhost:8080/BridgeDb");
        //ystem.out.println("in WSCoreInterface 2");
        try { 
            webService.isFreeSearchSupported();
            //ystem.out.println("in WSCoreInterface 3");
        } catch (Exception ex) {
            ex.printStackTrace();
            report("***** SKIPPING BridgeDB WSClientTest ******");
            report("These test are repeated in the OPS client so normally not needed here.");
            report("Please make sure the specicifc bridgeDB.war based server is running");
            org.junit.Assume.assumeTrue(false);        
        }
        //ystem.out.println("in WSCoreInterface 4");
        if (!webService.isMappingSupported(DataSource1.getSystemCode(), DataSource2.getSystemCode()).isMappingSupported()){
            //ystem.out.println("in WSCoreInterface 5a");
            report ("***** SKIPPING WSClientTest ******");
            report ("It appears the Test data is not loaded");
            report("remove ignore in TestDataToMainServerTest (org.bridgedb.ws.sqlserver) ");            
            org.junit.Assume.assumeTrue(false);        
        }
        //ystem.out.println("in WSCoreInterface 5b");
        return webService;
    }
}
