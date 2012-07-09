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
import org.bridgedb.mysql.MysqlMapper;
import org.bridgedb.url.WrappedIDMapper;
import org.bridgedb.ws.WSCoreInterface;
import org.bridgedb.ws.WSCoreMapper;
import org.bridgedb.ws.WSCoreService;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class URLMapperTest extends org.bridgedb.url.URLMapperTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException, MalformedURLException{
        connectionOk = false;
        SQLAccess sqlAccess = TestSqlFactory.createTestSQLAccess();
        connectionOk = true;
        IDMapper inner = new WrappedIDMapper(new MysqlMapper(sqlAccess));
        WSCoreInterface webService = new WSCoreService(inner);
        urlMapper = new WSCoreMapper(webService);
    }

}
