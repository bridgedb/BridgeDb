/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.mysql;

import org.bridgedb.mysql.URLMapperSQL;
import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.sql.TestSqlFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * This class depends on URLasIDMapperSQLTest having loaded the data.
 * 
 * @author Christian
 */
@Ignore
public class SourceTargetCounterTest extends org.bridgedb.statistics.SourceTargetCounterTest {
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{

        connectionOk = false;
        SQLAccess sqlAccess = TestSqlFactory.createTestSQLAccess();
        connectionOk = true;
        URLMapperSQL urlMapperSQL = new URLMapperSQL(sqlAccess);
        opsMapper = urlMapperSQL;
    }
            
}
