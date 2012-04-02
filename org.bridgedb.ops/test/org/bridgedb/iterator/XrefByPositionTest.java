/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.iterator;

import org.bridgedb.IDMapperTestBase;
import java.util.Set;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Christian
 */
public abstract class XrefByPositionTest extends IDMapperTestBase {
    
    protected static XrefByPosition xrefByPosition; 

    @Test
    public void testbyPosition() throws IDMapperException{
        System.out.println("testbyPosition");
        Xref result0 = xrefByPosition.getXrefByPosition(0);
        assertNotNull(result0);
        Xref result1 = xrefByPosition.getXrefByPosition(1);
        assertFalse(result1.equals(result0));
        Xref result2 = xrefByPosition.getXrefByPosition(8);
        assertFalse(result2.equals(result0));
        assertFalse(result2.equals(result1));
    }

    @Test
    public void testbyPositionAndLimit() throws IDMapperException{
        System.out.println("testbyPositionAndLimit");
        Set<Xref> results1 = xrefByPosition.getXrefByPosition(0, 5);
        assertEquals(5, results1.size());
        //Only 4 in the seocnd ones as there may be only 9 mappings
        Set<Xref> results2 = xrefByPosition.getXrefByPosition(5, 4);
        assertEquals(4, results2.size());
        for (Xref xref: results2){
            assertFalse(results1.contains(xref));
        }
    }

    @Test
    public void testbyPositionAndDataSource() throws IDMapperException{
        System.out.println("testbyPositionAndDataSource");
        Xref result0 = xrefByPosition.getXrefByPosition(DataSource1, 0);
        assertNotNull(result0);
        assertEquals(result0.getDataSource(), DataSource1);
        Xref result1 = xrefByPosition.getXrefByPosition(DataSource2, 0);
        assertEquals(result1.getDataSource(), DataSource2);
        assertFalse(result1.equals(result0));
        Xref result2 = xrefByPosition.getXrefByPosition(DataSource1, 1);
        assertFalse(result2.equals(result0));
        assertFalse(result2.equals(result1));
        assertEquals(result2.getDataSource(), DataSource1);
    }
        
    @Test
    public void testbyPositionLimitAndDataSource() throws IDMapperException{
        System.out.println("testbyPositionLimitAndDataSource");
        //There may be only three for 
        Set<Xref> results = xrefByPosition.getXrefByPosition(DataSource1, 0, 3);
        assertEquals(3, results.size());
        for (Xref xref: results){
            assertEquals(xref.getDataSource(), DataSource1);
        }
    }
}
