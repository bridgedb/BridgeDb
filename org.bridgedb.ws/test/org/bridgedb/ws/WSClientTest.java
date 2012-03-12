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
public class WSClientTest  extends IDMapperTest{
    
    @BeforeClass
    public static void setupIDMapper() {
        WSInterface webService = new WSClient();
        idMapper = new WSMapper(webService);
    }

}
