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
public class URLWSClientTest  extends org.bridgedb.url.URLMapperTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException {
        connectionOk = false;
        WSCoreInterface webService = WSCoreClientFactory.createTestWSClient();
        connectionOk = true;
        urlMapper = new WSCoreMapper(webService);
    }

}
