/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import org.bridgedb.IDMapperTestBase;
import java.util.Set;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Christian
 */
public abstract class XrefByPossitionTest extends IDMapperTestBase {
    
    protected static XrefByPossition xrefByPossition; 

    @Test
    public void testbyPossition() throws IDMapperException{
        System.out.println("testbyPossition");
        Xref result0 = xrefByPossition.getXrefByPossition(0);
        assertNotNull(result0);
        Xref result1 = xrefByPossition.getXrefByPossition(1);
        assertFalse(result1.equals(result0));
        Xref result2 = xrefByPossition.getXrefByPossition(8);
        assertFalse(result2.equals(result0));
        assertFalse(result2.equals(result1));
    }

    @Test
    public void testbyPossitionAndLimit() throws IDMapperException{
        System.out.println("testbyPossitionAndLimit");
        Set<Xref> results1 = xrefByPossition.getXrefByPossition(0, 5);
        assertEquals(5, results1.size());
        //Only 4 in the seocnd ones as there may be only 9 mappings
        Set<Xref> results2 = xrefByPossition.getXrefByPossition(5, 4);
        assertEquals(4, results2.size());
        for (Xref xref: results2){
            assertFalse(results1.contains(xref));
        }
    }

    @Test
    public void testbyPossitionAndDataSource() throws IDMapperException{
        System.out.println("testbyPossitionAndDataSource");
        Xref result0 = xrefByPossition.getXrefByPossition(DataSource1, 0);
        assertNotNull(result0);
        assertEquals(result0.getDataSource(), DataSource1);
        Xref result1 = xrefByPossition.getXrefByPossition(DataSource2, 0);
        assertEquals(result1.getDataSource(), DataSource2);
        assertFalse(result1.equals(result0));
        Xref result2 = xrefByPossition.getXrefByPossition(DataSource1, 1);
        assertFalse(result2.equals(result0));
        assertFalse(result2.equals(result1));
        assertEquals(result2.getDataSource(), DataSource1);
    }
        
    @Test
    public void testbyPossitionLimitAndDataSource() throws IDMapperException{
        System.out.println("testbyPossitionLimitAndDataSource");
        //There may be only three for 
        Set<Xref> results = xrefByPossition.getXrefByPossition(DataSource1, 0, 3);
        assertEquals(3, results.size());
        for (Xref xref: results){
            assertEquals(xref.getDataSource(), DataSource1);
        }
    }
}
