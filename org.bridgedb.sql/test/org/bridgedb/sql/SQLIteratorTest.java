/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

import org.bridgedb.XrefIteratorTest;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Warning: These tests depend on the data loaded in the IDMapperSQLTest.
 * So the first time this is run (or run after base tests change) this these test may cause errors.
 * Once IDMapperSQLTest is run once these should be fine until the test data changes again.
 * @author Christian
 */
public class SQLIteratorTest extends XrefIteratorTest {
    
    private static IDMapperSQL iDMapperSQL;

    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{
        SQLAccess sqlAccess = MySQLAccess.getTestMySQLAccess();
        checkConnection(sqlAccess);
        iDMapperSQL = new IDMapperSQL(sqlAccess);
        XrefIterator = iDMapperSQL;
    }
    
    private static void checkConnection(SQLAccess sqlAccess){
        connectionOk = false;
        try {
            sqlAccess.getConnection();
            connectionOk = true;
        } catch (BridgeDbSqlException ex) {
            System.err.println(ex);
            System.err.println("**** SKIPPPING IDMapperSQLTest tests due to Connection error.");
            System.err.println("To run these test you must have the following:");
            System.err.println("1. A MYSQL server running on port 3306");
            System.err.println("2. A database \"imstest\" setup on that server");
            System.err.println("3. A user \"imstest\" with password \"imstest\"");
            System.err.println("4. Full rights for user \"imstest\" on the database \"imstest\".");
            System.err.println("      DO NOT GRANT \"imstest\" RIGHTS TO OTHER DATABASES.");
         }
        org.junit.Assume.assumeTrue(connectionOk);        
    }
    
    @Test
    public void testbyPossition() throws IDMapperException{
        System.out.println("testbyPossition");
        Xref result0 = iDMapperSQL.getXrefByPossition(0);
        assertNotNull(result0);
        Xref result1 = iDMapperSQL.getXrefByPossition(1);
        assertFalse(result1.equals(result0));
        Xref result2 = iDMapperSQL.getXrefByPossition(8);
        assertFalse(result2.equals(result0));
        assertFalse(result2.equals(result1));
    }

    @Test
    public void testbyPossitionAndDataSource() throws IDMapperException{
        System.out.println("testbyPossitionAndDataSource");
        Xref result0 = iDMapperSQL.getXrefByPossitionAndDataSource(DataSource1, 0);
        assertNotNull(result0);
        assertEquals(result0.getDataSource(), DataSource1);
        Xref result1 = iDMapperSQL.getXrefByPossitionAndDataSource(DataSource2, 0);
        assertEquals(result1.getDataSource(), DataSource2);
        assertFalse(result1.equals(result0));
        Xref result2 = iDMapperSQL.getXrefByPossitionAndDataSource(DataSource1, 1);
        assertFalse(result2.equals(result0));
        assertFalse(result2.equals(result1));
        assertEquals(result2.getDataSource(), DataSource1);
        System.out.println("testbyPossitionAndDataSource Done");
    }
        
}
