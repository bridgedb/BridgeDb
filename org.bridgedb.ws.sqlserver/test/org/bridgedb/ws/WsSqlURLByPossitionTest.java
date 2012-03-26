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
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.sql.URLMapperSQL;
import org.bridgedb.url.URLMapper;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class WsSqlURLByPossitionTest extends URLByPossitionTest{
    
    @BeforeClass
    public static void setupURLMapper() throws IDMapperException, MalformedURLException{
        SQLAccess sqlAccess = TestSqlFactory.createTestURLSQLAccess();
        IDMapper inner = new URLMapperSQL(sqlAccess);
        WSService webService = new WSService(inner);
        urlByPossition = new WSMapper(webService);
    }

}
