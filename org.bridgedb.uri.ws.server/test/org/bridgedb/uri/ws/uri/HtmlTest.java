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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Christian
 */
public class HtmlTest {
    
    static WSUriServer server;
    
    @BeforeAll
    @Tag("mysql")
    public static void setupIDMapper() throws BridgeDBException{
        ConfigReader.useTest();
        TestSqlFactory.checkSQLAccess();
        server = new WSUriServer();
    }

    @Test
    @Tag("mysql")
    public void testWelcomeMessage() throws BridgeDBException, UnsupportedEncodingException{
        Reporter.println("WelcomeMessage");
        Response result = server.welcomeMessage(new DummyHttpServletRequest());
        Assertions.assertEquals(200, result.getStatus());
    }

    @Test
    @Tag("mysql")   
    public void testApi() throws BridgeDBException, UnsupportedEncodingException{
        Reporter.println("API");
        Response result = server.imsApiPage(new DummyHttpServletRequest());
        Assertions.assertEquals(200, result.getStatus());
    }
 }
