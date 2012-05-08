/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.client;

import org.bridgedb.IDMapperException;
import org.bridgedb.ws.WSCoreClientFactory;
import org.bridgedb.ws.WSCoreInterface;
import org.bridgedb.ws.WSCoreMapper;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class IDMapperCapabilitiesTest  extends org.bridgedb.IDMapperCapabilitiesTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException {
        System.out.println("in setup 1");
        WSCoreInterface webService = WSCoreClientFactory.createTestWSClient();
        System.out.println("in setup 2");
        capabilities = new WSCoreMapper(webService).getCapabilities();
        System.out.println("in setup 3");
    }

}
