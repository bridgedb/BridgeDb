/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.client;

import com.sun.jersey.api.client.UniformInterfaceException;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.ws.WSOpsClientFactory;
import org.bridgedb.ws.WSCoreInterface;
import org.bridgedb.ws.WSCoreMapper;
import org.bridgedb.ws.WSOpsInterface;
import org.bridgedb.ws.WSOpsMapper;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Christian
 */
public class URLMapperTest  extends org.bridgedb.url.URLMapperTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException {
        WSOpsInterface webService = WSOpsClientFactory.createTestWSClient();
        urlMapper = new WSOpsMapper(webService);
    }
    
    /**
     * Overwrite as different exception
     * @throws IDMapperException 
     */
    @Test
    (expected=UniformInterfaceException.class)
    public void testGetXrefBad() throws IDMapperException {
        report("GetXrefBad");
        Xref result = urlMapper.toXref(mapBadURL1);
    }

    @Test
    @Override //TOO slow
    public void testGetOverallStatistics() throws IDMapperException {
    }

}
