/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.server;

import java.net.MalformedURLException;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.sql.URLMapperSQL;
import org.bridgedb.ws.WSInterface;
import org.bridgedb.ws.WSMapper;
import org.bridgedb.ws.WSService;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class XrefIteratorTest extends org.bridgedb.XrefIteratorTest{
    
    /* Removed due to scale issues
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException, MalformedURLException{
        SQLAccess sqlAccess = TestSqlFactory.createTestSQLAccess();
        IDMapper inner = new URLMapperSQL(sqlAccess);
        WSInterface webService = new WSService(inner);
        XrefIterator = new WSMapper(webService);
    }
     */
}
