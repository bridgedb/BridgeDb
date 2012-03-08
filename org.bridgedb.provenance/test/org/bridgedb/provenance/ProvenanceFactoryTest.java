/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.provenance;

import org.bridgedb.provenance.Provenance;
import org.bridgedb.provenance.Provenance;
import org.bridgedb.provenance.ProvenanceFactory;
import org.bridgedb.provenance.ProvenanceFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public abstract class ProvenanceFactoryTest {
    
    public static ProvenanceFactory factory;
    
    /**
     * Test of createProvenance method, of class ProvenanceFactory.
     * /
    @Test
    public void testCreateProvenance_4args() throws Exception {
        System.out.println("createProvenance");
        String createdBy = "A";
        String predicate = "";
        long creation = 0L;
        long upload = 0L;
        ProvenanceFactory instance = new ProvenanceFactoryImpl();
        Provenance expResult = null;
        Provenance result = instance.createProvenance(createdBy, predicate, creation, upload);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of createProvenance method, of class ProvenanceFactory.
     */
    @Test
    public void testCreateProvenance_3args() throws Exception {
        System.out.println("CreateProvenance_3args");
        String createdBy = "createdB";
        String predicate = "predicate";
        long creation = 1000;
        Provenance expResult = factory.createProvenance(createdBy, predicate, creation);
        Provenance result = factory.createProvenance(createdBy, predicate, creation);
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
        Provenance expResult = factory.createProvenance(createdBy, predicate, creation);
        Provenance result = factory.createProvenance(createdBy+"2", predicate, creation);
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
        Provenance expResult = factory.createProvenance(createdBy, predicate, creation);
        Provenance result = factory.createProvenance(createdBy, predicate + "2", creation);
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
        Provenance expResult = factory.createProvenance(createdBy, predicate, creation);
        Provenance result = factory.createProvenance(createdBy, predicate, creation + 2);
        assertThat(expResult, not(result));
        //assertTrue(false);
    }

    /**
     * Test of createProvenace method, of class ProvenanceFactory.
     * /
    @Test
    public void testCreateProvenace() throws Exception {
        System.out.println("createProvenace");
        Provenance first = null;
        Provenance second = null;
        ProvenanceFactory instance = new ProvenanceFactoryImpl();
        Provenance expResult = null;
        Provenance result = instance.createProvenace(first, second);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
     * */
 }
