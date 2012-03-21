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
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class WsSqlXRefByPossitionTest extends XrefByPossitionTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException, MalformedURLException{
        SQLAccess sqlAccess = TestSqlFactory.createTestIDSQLAccess();
        IDMapper inner = new IDMapperSQL(sqlAccess);
        WSService webService = new WSService(inner);
        xrefByPossition = new WSMapper(webService);
    }

}
