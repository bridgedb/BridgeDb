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
        //ystem.out.println("in WSCoreInterface 1");
        WSOpsInterface webService = new WSOpsClient("http://localhost:8080/OPS-IMS");
        //ystem.out.println("in WSCoreInterface 2");
        try { 
            webService.isFreeSearchSupported();
            //ystem.out.println("in WSCoreInterface 3");
        } catch (Exception ex) {
            System.err.println(ex);
            System.err.println ("***** SKIPPING WSClientTest ******");
            System.err.println ("Please make sure the server is running");
            org.junit.Assume.assumeTrue(false);        
        }
        //ystem.out.println("in WSCoreInterface 4");
        if (!webService.isMappingSupported(DataSource1.getSystemCode(), DataSource2.getSystemCode()).isMappingSupported()){
        //ystem.out.println("in WSCoreInterface 5a");
            System.err.println ("***** SKIPPING WSClientTest ******");
            System.err.println ("It appears the Test data is not loaded");
            System.err.println ("remove ignore in TestDataToMainServerTest (org.bridgedb.ws.sqlserver) ");            
            org.junit.Assume.assumeTrue(false);        
        }
        //ystem.out.println("in WSCoreInterface 5b");
        return webService;
    }
}
