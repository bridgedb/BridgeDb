/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.type;

import org.bridgedb.utils.Reporter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;

/**
 *
 * @author Christian
 */
public class IntegerTypeTest {
    
    public IntegerTypeTest() {
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
     * Test of correctType method, of class IntegerType.
     */
    @Test
    public void testCorrectType() {
        Reporter.report("correctType");
        IntegerType instance = new IntegerType();
        Value value = new LiteralImpl("10");
        assertTrue(instance.correctType(value));
        value = new LiteralImpl("ten");
        assertFalse(instance.correctType(value));
    }

    /**
     * Test of getCorrectType method, of class IntegerType.
     */
    @Test
    public void testGetCorrectType() {
        Reporter.report("getCorrectType");
        IntegerType instance = new IntegerType();
        String expResult = " An Integer";
        String result = instance.getCorrectType();
        assertEquals(expResult, result);
    }
}
