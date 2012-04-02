/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.junit.BeforeClass;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

/**
 *
 * @author Christian
 */
public class URLMapperLinksetHandlerTest extends LinksetHandlerTest {
    
    @BeforeClass
    public static void loadMappings() throws IDMapperException, IOException, RDFParseException, RDFHandlerException{
        URLMapperLinkset mapper = new URLMapperLinkset(); 
        listener = mapper;
        idMapper = mapper;
        LinksetHandlerTest.loadMappings();
        mapper.printStats();
    }
}
