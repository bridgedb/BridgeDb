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
public abstract class ProvenanceFactoryTest extends IDMapperTestBase {
    
    public static ProvenanceFactory factory;
    

    /**
     * Test of createProvenance method, of class ProvenanceFactory.
     */
    @Test
    public void testCreateProvenance_3args() throws Exception {
        System.out.println("CreateProvenance_3args");
        String createdBy = "createdB";
        String predicate = "predicate";
        long creation = 1000;
        Provenance expResult = factory.createProvenance(DataSource1, predicate, DataSource2, createdBy, creation);
        Provenance result = factory.createProvenance(DataSource1, predicate, DataSource2, createdBy, creation);
        assertEquals(expResult, result);
        //assertTrue(false);
    }

    /**
     * Test of createProvenance method, of class ProvenanceFactory.
     */
    @Test
    public void testCreateProvenance_3argsDifferentSource() throws Exception {
        System.out.println("CreateProvenance_3args");
        String createdBy = "createdB";
        String predicate = "predicate";
        long creation = 1000;
        Provenance expResult = factory.createProvenance(DataSource1, predicate, DataSource2, createdBy, creation);
        Provenance result = factory.createProvenance(DataSource2, predicate, DataSource2, createdBy, creation);
        assertThat(expResult, not(result));
        //assertTrue(false);
    }

    /**
     * Test of createProvenance method, of class ProvenanceFactory.
     */
    @Test
    public void testCreateProvenance_3argsDifferentTarget() throws Exception {
        System.out.println("CreateProvenance_3args");
        String createdBy = "createdB";
        String predicate = "predicate";
        long creation = 1000;
        Provenance expResult = factory.createProvenance(DataSource1, predicate, DataSource2, createdBy, creation);
        Provenance result = factory.createProvenance(DataSource3, predicate, DataSource2, createdBy, creation);
        assertThat(expResult, not(result));
        //assertTrue(false);
    }
    
    /**
     * Test of createProvenance method, of class ProvenanceFactory.
     */
    @Test
    public void testCreateProvenance_3argsSourceEqualsTarget() throws Exception {
        System.out.println("CreateProvenance_3args");
        String createdBy = "createdB";
        String predicate = "predicate";
        long creation = 1000;
        Provenance expResult = factory.createProvenance(DataSource1, predicate, DataSource1, createdBy, creation);
        Provenance result = factory.createProvenance(DataSource1, predicate, DataSource1, createdBy, creation);
        assertEquals(expResult, result);
        //assertTrue(false);
    }

    /**
     * Test of createProvenance method, of class ProvenanceFactory.
     */
    @Test
    public void testCreateProvenance_3argsDifferentCreatedBy() throws Exception {
        System.out.println("CreateProvenance_3argsDifferentCreatedBy");
        String createdBy = "createdB";
        String predicate = "predicate";
        long creation = 1000;
        Provenance expResult = factory.createProvenance(DataSource1, predicate, DataSource2, createdBy, creation);
        Provenance result = factory.createProvenance(DataSource1, predicate, DataSource2, createdBy + 2, creation);
        assertThat(expResult, not(result));
        //assertTrue(false);
    }

    /**
     * Test of createProvenance method, of class ProvenanceFactory.
     */
    @Test
    public void testCreateProvenance_3argsDifferentPredicate() throws Exception {
        System.out.println("CreateProvenance_3argsDifferentPredicate");
        String createdBy = "createdB";
        String predicate = "predicate";
        long creation = 1000;
        Provenance expResult = factory.createProvenance(DataSource1, predicate, DataSource2, createdBy, creation);
        Provenance result = factory.createProvenance(DataSource1, predicate + 2, DataSource2, createdBy, creation);
        assertThat(expResult, not(result));
        //assertTrue(false);
    }

    /**
     * Test of createProvenance method, of class ProvenanceFactory.
     */
    @Test
    public void testCreateProvenance_3argsDifferentCreation() throws Exception {
        System.out.println("CreateProvenance_3argsDifferentCreation");
        String createdBy = "createdB";
        String predicate = "predicate";
        long creation = 1000;
        Provenance expResult = factory.createProvenance(DataSource1, predicate, DataSource2, createdBy, creation);
        Provenance result = factory.createProvenance(DataSource1, predicate, DataSource2, createdBy, creation + 2);
        assertThat(expResult, not(result));
        //assertTrue(false);
    }

 }
