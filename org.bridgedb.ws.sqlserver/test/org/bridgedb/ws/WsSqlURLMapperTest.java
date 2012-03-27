/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import java.net.MalformedURLException;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.sql.IDMapperSQL;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.TestURLSqlFactory;
import org.bridgedb.sql.URLMapperSQL;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class WsSqlURLMapperTest extends URLWSTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException, MalformedURLException{
        connectionOk = false;
        SQLAccess sqlAccess = TestURLSqlFactory.createTestSQLAccess();
        connectionOk = true;
        IDMapper inner = new URLMapperSQL(sqlAccess);
        webService = new WSService(inner);
        urlMapper = new WSMapper(webService);
    }

}
