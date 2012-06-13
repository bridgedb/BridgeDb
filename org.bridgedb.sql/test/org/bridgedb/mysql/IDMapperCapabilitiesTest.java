/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.mysql;

import org.bridgedb.mysql.URLMapperSQL;
import java.util.Date;
import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.TestSqlFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * 
 * @author Christian
 */
public class IDMapperCapabilitiesTest extends org.bridgedb.IDMapperCapabilitiesTest {
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{
        SQLAccess sqlAccess = TestSqlFactory.createTestSQLAccess();
        URLMapperSQL urlMapperSQL = new URLMapperSQL(sqlAccess);
        capabilities = urlMapperSQL;
    }
            
}
