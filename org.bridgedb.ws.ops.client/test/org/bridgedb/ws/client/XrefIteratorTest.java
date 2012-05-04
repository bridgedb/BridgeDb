/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.client;

import org.bridgedb.IDMapperException;
import org.bridgedb.ws.WSClientFactory;
import org.bridgedb.ws.WSInterface;
import org.bridgedb.ws.WSMapper;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class XrefIteratorTest  extends org.bridgedb.XrefIteratorTest{
    
    /* removed due to scale issues
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException {
        WSInterface webService = WSClientFactory.createTestWSClient();
        XrefIterator = new WSMapper(webService);
    }
     */
}
