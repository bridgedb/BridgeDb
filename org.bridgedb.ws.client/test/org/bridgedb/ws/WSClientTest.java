/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTest;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class WSClientTest  extends IDWSTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException {
        connectionOk = false;
        webService = WSClientFactory.createTestWSClient();
        connectionOk = true;
        idMapper = new WSMapper(webService);
    }

}
