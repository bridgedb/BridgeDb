/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

import java.util.Set;
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
        SQLAccess sqlAccess = TestIDSqlFactory.createTestSQLAccess();
        iDMapperSQL = new IDMapperSQL(sqlAccess);
        XrefIterator = iDMapperSQL;
    }
    
    @Test
    public void testbyPosition() throws IDMapperException{
        System.out.println("testbyPosition");
        Xref result0 = iDMapperSQL.getXrefByPosition(0);
        assertNotNull(result0);
        Xref result1 = iDMapperSQL.getXrefByPosition(1);
        assertFalse(result1.equals(result0));
        Xref result2 = iDMapperSQL.getXrefByPosition(8);
        assertFalse(result2.equals(result0));
        assertFalse(result2.equals(result1));
    }

    @Test
    public void testbyPositionAndLimit() throws IDMapperException{
        System.out.println("testbyPositionAndLimit");
        Set<Xref> results1 = iDMapperSQL.getXrefByPosition(0, 5);
        assertEquals(5, results1.size());
        //Only 4 in the seocnd ones as there may be only 9 mappings
        Set<Xref> results2 = iDMapperSQL.getXrefByPosition(5, 4);
        assertEquals(4, results2.size());
        for (Xref xref: results2){
            assertFalse(results1.contains(xref));
        }
    }

    @Test
    public void testbyPositionAndDataSource() throws IDMapperException{
        System.out.println("testbyPositionAndDataSource");
        Xref result0 = iDMapperSQL.getXrefByPosition(DataSource1, 0);
        assertNotNull(result0);
        assertEquals(result0.getDataSource(), DataSource1);
        Xref result1 = iDMapperSQL.getXrefByPosition(DataSource2, 0);
        assertEquals(result1.getDataSource(), DataSource2);
        assertFalse(result1.equals(result0));
        Xref result2 = iDMapperSQL.getXrefByPosition(DataSource1, 1);
        assertFalse(result2.equals(result0));
        assertFalse(result2.equals(result1));
        assertEquals(result2.getDataSource(), DataSource1);
    }
        
    @Test
    public void testbyPositionLimitAndDataSource() throws IDMapperException{
        System.out.println("testbyPositionLimitAndDataSource");
        //There may be only three for 
        Set<Xref> results = iDMapperSQL.getXrefByPosition(DataSource1, 0, 3);
        assertEquals(3, results.size());
        for (Xref xref: results){
            assertEquals(xref.getDataSource(), DataSource1);
        }
    }
}
