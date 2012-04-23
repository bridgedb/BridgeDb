/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.sqlserver;

import java.net.MalformedURLException;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.sql.URLMapperSQL;
import org.bridgedb.ws.WSCoreInterface;
import org.bridgedb.ws.WSCoreMapper;
import org.bridgedb.ws.WSCoreService;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class IndirectIDMapperCapabilitiesTest extends org.bridgedb.IDMapperCapabilitiesTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException, MalformedURLException{
        SQLAccess sqlAccess = TestSqlFactory.createTestSQLAccess();
        IDMapper inner = new URLMapperSQL(sqlAccess);
        WSCoreInterface webService = new WSCoreService(inner);
        capabilities = new WSCoreMapper(webService).getCapabilities();
    }

}
