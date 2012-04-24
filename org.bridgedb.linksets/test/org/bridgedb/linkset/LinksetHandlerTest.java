/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTest;
import org.openrdf.OpenRDFException;

/**
 *
 * @author Christian
 */
public abstract class LinksetHandlerTest extends IDMapperTest {
    
    protected static URLLinkListener listener;
    private static final boolean IS_TEST = true;
    
    private static void parse(){        
    }
    
    public static void loadMappings() throws IDMapperException, IOException, OpenRDFException{
       //ystem.out.println("sample1to2.ttl");
        //The up and back file reference is important for other modules.
        System.out.println("sample1to2.ttl");
        LinksetHandler.testClearAndParse (listener, "../org.bridgedb.linksets/test-data/sample1to2.ttl");
        System.out.println("sample1to3.ttl");
        LinksetHandler.testParse (listener, "../org.bridgedb.linksets/test-data/sample1to3.ttl");
        System.out.println("sample2to1.ttl");
        LinksetHandler.testParse (listener, "../org.bridgedb.linksets/test-data/sample2to1.ttl");
        System.out.println("sample2to3.ttl");
        LinksetHandler.testParse (listener, "../org.bridgedb.linksets/test-data/sample2to3.ttl");
        System.out.println("sample3to1.ttl");
        LinksetHandler.testParse (listener, "../org.bridgedb.linksets/test-data/sample3to1.ttl");
        System.out.println("sample3to2.ttl");
        LinksetHandler.testParse (listener, "../org.bridgedb.linksets/test-data/sample3to2.ttl");
	}

}
