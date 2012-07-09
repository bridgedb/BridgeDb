/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.server;

import java.io.File;
import java.net.MalformedURLException;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.file.IDMapperText;
import org.bridgedb.ws.WSCoreMapper;
import org.bridgedb.ws.WSCoreService;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class LinkSetMapperTest extends org.bridgedb.linkset.LinkSetMapperTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException, MalformedURLException{
        File INTERFACE_TEST_FILE = new File ("../org.bridgedb/test-data/interfaceTest.txt");
        IDMapper inner = new IDMapperText(INTERFACE_TEST_FILE.toURL());
        WSCoreService webService = new WSCoreService(inner, TEST_PREDICATE);
        linkSetMapper = new WSCoreMapper(webService);
    }

}
