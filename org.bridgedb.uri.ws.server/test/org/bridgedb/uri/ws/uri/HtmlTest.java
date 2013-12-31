/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.uri.ws.uri;

import java.io.UnsupportedEncodingException;
import javax.ws.rs.core.Response;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.Reporter;
import org.bridgedb.ws.uri.WSUriServer;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Christian
 */
public class HtmlTest {
    
    static WSUriServer server;
    
    @BeforeClass
    public static void setupIDMapper() throws BridgeDBException{
        ConfigReader.useTest();
        TestSqlFactory.checkSQLAccess();
        server = new WSUriServer();
    }

    @Test 
    public void testWelcomeMessage() throws BridgeDBException, UnsupportedEncodingException{
        Reporter.println("WelcomeMessage");
        Response result = server.welcomeMessage(new DummyHttpServletRequest());
        assertEquals(200, result.getStatus());
    }

    @Test 
    public void testGetMappingInfo() throws BridgeDBException, UnsupportedEncodingException{
        Reporter.println("GetMappingInfo");
        Response result = server.getSetMapping(null, null, null, new DummyHttpServletRequest());
        assertEquals(200, result.getStatus());
    }
}
