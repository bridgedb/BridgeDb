/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import org.bridgedb.IDMapperTest;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class URLWSClientTest  extends URLWSTest{
    
    @BeforeClass
    public static void setupIDMapper() {
        connectionOk = false;
        webService = WSClientFactory.createTestWSClient();
        connectionOk = true;
        urlMapper = new WSMapper(webService);
    }

}
