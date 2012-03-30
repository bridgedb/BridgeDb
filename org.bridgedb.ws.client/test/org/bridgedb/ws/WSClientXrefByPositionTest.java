package org.bridgedb.ws;

import org.bridgedb.IDMapperException;
import org.bridgedb.iterator.XrefByPositionTest;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class WSClientXrefByPositionTest  extends XrefByPositionTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException {
        WSInterface webService = WSClientFactory.createTestWSClient();
        xrefByPosition = new WSMapper(webService);
    }

}
