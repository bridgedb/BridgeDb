/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import java.net.MalformedURLException;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.TestURLSqlFactory;
import org.bridgedb.sql.URLMapperSQL;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class WsSqlIDMapperTest extends IDWSTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException, MalformedURLException{
        connectionOk = false;
        SQLAccess sqlAccess = TestURLSqlFactory.createTestSQLAccess();
        connectionOk = true;
        IDMapper inner = new URLMapperSQL(sqlAccess);
        webService = new WSService(inner);
        idMapper = new WSMapper(webService);
    }

}
