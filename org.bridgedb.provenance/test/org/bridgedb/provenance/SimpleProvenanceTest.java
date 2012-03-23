/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.provenance;

import org.bridgedb.IDMapperTestBase;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public class SimpleProvenanceTest  extends IDMapperTestBase{
    
   /**
     * Test of equals method, of class SimpleProvenance.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        long creation = 100;
        long upload = 50;
        SimpleProvenance expected = new SimpleProvenance(DataSource1, "predicate", DataSource2, "createdBy", creation, upload);
        SimpleProvenance result = new SimpleProvenance(DataSource1, "predicate", DataSource2, "createdBy", creation, upload);
        assertEquals(expected, result);
    }

    /**
     * Test of equals method, of class SimpleProvenance.
     */
    @Test
    public void testEqualsIDSame() {
        System.out.println("EqualsIDSame");
        long creation = 100;
        long upload = 50;
        SimpleProvenance expected = new SimpleProvenance(45, DataSource1, "predicate", DataSource2, 
                "createdBy", creation, upload);
        SimpleProvenance result = new SimpleProvenance(45, DataSource1, "predicate", DataSource2, 
                "createdBy", creation, upload);
        assertEquals(expected, result);
    }

    /**
     * Test of equals method, of class SimpleProvenance.
     */
    @Test
    public void testEqualsFailsOnIDDifferent() {
        System.out.println("EqualsFailsOnIDDifferent");
        long creation = 100;
        long upload = 50;
        SimpleProvenance expected = new SimpleProvenance(45, DataSource1, "predicate", DataSource2, 
                "createdBy", creation, upload);
        SimpleProvenance result = new SimpleProvenance(23, DataSource1, "predicate", DataSource2, 
                "createdBy", creation, upload);
        assertThat(expected, not(result));
    }

    /**
     * Test of equals method, of class SimpleProvenance.
     */
    @Test
    public void testEqualsFailsOnSourceDiff() {
        System.out.println("EqualsFailsOnSourceDiff");
        long creation = 100;
        long upload = 50;
        SimpleProvenance expected = new SimpleProvenance(DataSource1, "predicate", DataSource2, "createdBy", creation, upload);
        SimpleProvenance result = new SimpleProvenance(DataSource3, "predicate", DataSource2, "createdBy", creation, upload);
        assertThat(expected, not(result));
    }

    /**
     * Test of equals method, of class SimpleProvenance.
     */
    @Test
    public void testEqualsFailsOnTargetDiff() {
        System.out.println("EqualsFailsOnTargetDiff");
        long creation = 100;
        long upload = 50;
        SimpleProvenance expected = new SimpleProvenance(DataSource1, "predicate", DataSource2, "createdBy", creation, upload);
        SimpleProvenance result = new SimpleProvenance(DataSource1, "predicate", DataSource3, "createdBy", creation, upload);
        assertThat(expected, not(result));
    }

    /**
     * Test of equals method, of class SimpleProvenance.
     */
    @Test
    public void testEqualsFailsOnCreatorDiff() {
        System.out.println("EqualsFailsOnCreatorDiff");
        long creation = 100;
        long upload = 50;
        SimpleProvenance expected = new SimpleProvenance(DataSource1, "predicate", DataSource2, "createdBy", creation, upload);
        SimpleProvenance result = new SimpleProvenance(DataSource1, "predicate", DataSource2, "createdBy2", creation, upload);
        assertThat(expected, not(result));
    }
    /**
     * Test of equals method, of class SimpleProvenance.
     */
    @Test
    public void testEqualsFailsOnPredicateDiff() {
        System.out.println("EqualsFailsOnPredicateDiff");
        long creation = 100;
        long upload = 50;
        SimpleProvenance expected = new SimpleProvenance(DataSource1, "predicate", DataSource2, "createdBy", creation, upload);
        SimpleProvenance result = new SimpleProvenance(DataSource1, "predicate2", DataSource2, "createdBy", creation, upload);
        assertThat(expected, not(result));
    }

    /**
     * Test of equals method, of class SimpleProvenance.
     */
    @Test
    public void testEqualsFailsOnCreationDiff() {
        System.out.println("EqualsFailsOnCreationDiff");
        long creation = 100;
        long upload = 50;
        SimpleProvenance expected = new SimpleProvenance(DataSource1, "predicate", DataSource2, "createdBy", creation, upload);
        SimpleProvenance result = new SimpleProvenance(DataSource1, "predicate", DataSource2, "createdBy", creation + 10, upload);
        assertThat(expected, not(result));
    }

}
