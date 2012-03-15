/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import java.io.File;
import java.net.MalformedURLException;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTest;
import org.bridgedb.file.IDMapperText;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class WSIDMapperTest extends WSTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException, MalformedURLException{
        File INTERFACE_TEST_FILE = new File ("../org.bridgedb/test-data/interfaceTest.txt");
        IDMapper inner = new IDMapperText(INTERFACE_TEST_FILE.toURL());
        webService = new WSService(inner);
        idMapper = new WSMapper(webService);
    }

}
