package org.bridgedb.ws;

import org.bridgedb.iterator.XrefByPossitionTest;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class WSClientXrefByPossitionTest  extends XrefByPossitionTest{
    
    @BeforeClass
    public static void setupIDMapper() {
        WSInterface webService = WSClientFactory.createTestWSClient();
        xrefByPossition = new WSMapper(webService);
    }

}
