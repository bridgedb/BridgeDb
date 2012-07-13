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
import org.junit.Ignore;

/**
 *
 * @author Christian
 */
@Ignore //repeated in OPS client and depends on the specific BridgeBD.war
public class IndirectIDMapperCapabilitiesTest  extends org.bridgedb.IDMapperCapabilitiesTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException {
        WSCoreInterface webService = WSCoreClientFactory.createTestWSClient();
        capabilities = new WSCoreMapper(webService);
    }

}
