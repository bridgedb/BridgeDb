package org.bridgedb.ws.client;

import org.bridgedb.IDMapperException;
import org.bridgedb.ws.WSOpsClientFactory;
import org.bridgedb.ws.WSOpsInterface;
import org.bridgedb.ws.WSOpsMapper;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class IDMapperTest  extends org.bridgedb.IDMapperTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException {
        connectionOk = false;
        WSOpsInterface webService = WSOpsClientFactory.createTestWSClient();
        connectionOk = true;
        idMapper = new WSOpsMapper(webService);
    }

}
