/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

import org.bridgedb.IDMapperException;
import org.bridgedb.iterator.URLByPositionTest;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * Warning: These tests depend on the data loaded in the IDMapperSQLTest.
 * So the first time this is run (or run after base tests change) this these test may cause errors.
 * Once IDMapperSQLTest is run once these should be fine until the test data changes again.
 * @author Christian
 */
public class URLsqlURLByPositionTest extends URLByPositionTest {
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{
        SQLAccess sqlAccess = TestURLSqlFactory.createTestSQLAccess();
        URLMapperSQL urlMapperSQL = new URLMapperSQL(sqlAccess);
        urlByPosition = urlMapperSQL;
    }
     
}
