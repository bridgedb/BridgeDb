/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.provenance;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public class SimpleProvenanceTest {
    
    public SimpleProvenanceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of equals method, of class SimpleProvenance.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        long creation = 100;
        long upload = 50;
        Object other = new SimpleProvenance("createdBy", "predicate", creation, upload);
        SimpleProvenance instance = new SimpleProvenance("createdBy", "predicate", creation, upload);
        assertTrue(instance.equals(other));
    }

    /**
     * Test of equals method, of class SimpleProvenance.
     */
    @Test
    public void testEqualsFailsOnCreatorID() {
        System.out.println("EqualsFailsOnCreatorID");
        long creation = 100;
        long upload = 50;
        Object other = new SimpleProvenance(23,"createdBy", "predicate", creation, upload);
        SimpleProvenance instance = new SimpleProvenance(45,"createdBy2", "predicate", creation, upload);
        assertFalse(instance.equals(other));
    }

    /**
     * Test of equals method, of class SimpleProvenance.
     */
    @Test
    public void testEqualsFailsOnCreatorDiff() {
        System.out.println("EqualsFailsOnCreatorDiff");
        long creation = 100;
        long upload = 50;
        Object other = new SimpleProvenance("createdBy", "predicate", creation, upload);
        SimpleProvenance instance = new SimpleProvenance("createdBy2", "predicate", creation, upload);
        assertFalse(instance.equals(other));
    }

    /**
     * Test of equals method, of class SimpleProvenance.
     */
    @Test
    public void testEqualsFailsOnPredicateDiff() {
        System.out.println("EqualsFailsOnPredicateDiff");
        long creation = 100;
        long upload = 50;
        Object other = new SimpleProvenance("createdBy", "predicate", creation, upload);
        SimpleProvenance instance = new SimpleProvenance("createdBy", "predicate2", creation, upload);
        assertFalse(instance.equals(other));
    }

    /**
     * Test of equals method, of class SimpleProvenance.
     */
    @Test
    public void testEqualsFailsOnCreationDiff() {
        System.out.println("EqualsFailsOnCreationDiff");
        long creation = 100;
        long upload = 50;
        Object other = new SimpleProvenance("createdBy", "predicate", creation, upload);
        SimpleProvenance instance = new SimpleProvenance("createdBy", "predicate", creation + 10, upload);
        assertFalse(instance.equals(other));
    }

    /**
     * Test of getPredicate method, of class SimpleProvenance.
     * /
    @Test
    public void testGetPredicate() {
        System.out.println("getPredicate");
        SimpleProvenance instance = null;
        String expResult = "";
        String result = instance.getPredicate();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCreatedBy method, of class SimpleProvenance.
     * /
    @Test
    public void testGetCreatedBy() {
        System.out.println("getCreatedBy");
        SimpleProvenance instance = null;
        String expResult = "";
        String result = instance.getCreatedBy();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCreation method, of class SimpleProvenance.
     * /
    @Test
    public void testGetCreation() {
        System.out.println("getCreation");
        SimpleProvenance instance = null;
        long expResult = 0L;
        long result = instance.getCreation();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUpload method, of class SimpleProvenance.
     * /
    @Test
    public void testGetUpload() {
        System.out.println("getUpload");
        SimpleProvenance instance = null;
        long expResult = 0L;
        long result = instance.getUpload();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isTransative method, of class SimpleProvenance.
     * /
    @Test
    public void testIsTransative() {
        System.out.println("isTransative");
        SimpleProvenance instance = null;
        boolean expResult = false;
        boolean result = instance.isTransative();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getId method, of class SimpleProvenance.
     * /
    @Test
    public void testGetId() {
        System.out.println("getId");
        SimpleProvenance instance = null;
        int expResult = 0;
        int result = instance.getId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/
}
