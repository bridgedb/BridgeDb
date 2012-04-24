/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.junit.BeforeClass;
import org.openrdf.OpenRDFException;

/**
 *
 * @author Christian
 */
public class URLMapperLinksetHandlerTest extends LinksetHandlerTest {
    
    @BeforeClass
    public static void loadMappings() throws IDMapperException, IOException, OpenRDFException{
        URLMapperLinkset mapper = new URLMapperLinkset(); 
        listener = mapper;
        idMapper = mapper;
        LinksetHandlerTest.loadMappings();
        mapper.printStats();
    }
}
