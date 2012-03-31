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
        String createdBy = "testCreateProvenance_3args";
        String predicate = "predicate";
        long creation = 1000;
        Provenance result = factory.createProvenance(DataSource1, predicate, DataSource2, createdBy, creation);
        assertEquals(DataSource1, result.getSource());
        assertEquals(predicate, result.getPredicate());
        assertEquals(DataSource2.getNameSpace(), result.getTarget().getNameSpace());
        assertEquals(DataSource2, result.getTarget());
        assertEquals(createdBy, result.getCreatedBy());
        assertEquals(creation, result.getCreation());
        //assertTrue(false);
    }

    /**
     * Test of createProvenance method, of class ProvenanceFactory.
     */
    @Test
    public void testCreateProvenance_3argsSourceEqualsTarget() throws Exception {
        System.out.println("CreateProvenance_3args");
        String createdBy = "testCreateProvenance_3argsSourceEqualsTarget";
        String predicate = "predicate";
        long creation = 1000;
        Provenance result = factory.createProvenance(DataSource1, predicate, DataSource1, createdBy, creation);
        assertEquals(DataSource1, result.getSource());
        assertEquals(predicate, result.getPredicate());
        assertEquals(DataSource1, result.getTarget());
        assertEquals(createdBy, result.getCreatedBy());
        assertEquals(creation, result.getCreation());
    }

 }
