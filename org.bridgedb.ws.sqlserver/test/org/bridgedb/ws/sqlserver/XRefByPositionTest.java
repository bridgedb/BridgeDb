/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.sqlserver;

import java.net.MalformedURLException;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.TestURLSqlFactory;
import org.bridgedb.sql.URLMapperSQL;
import org.bridgedb.ws.WSMapper;
import org.bridgedb.ws.WSService;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class XRefByPositionTest extends org.bridgedb.iterator.XrefByPositionTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException, MalformedURLException{
        SQLAccess sqlAccess = TestURLSqlFactory.createTestSQLAccess();
        IDMapper inner = new URLMapperSQL(sqlAccess);
        WSService webService = new WSService(inner);
        xrefByPosition = new WSMapper(webService);
    }

}
