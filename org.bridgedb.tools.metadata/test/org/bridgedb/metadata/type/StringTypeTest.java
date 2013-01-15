/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.type;

import org.bridgedb.tools.metadata.type.StringType;
import org.bridgedb.utils.TestUtils;
import org.bridgedb.rdf.constants.XMLSchemaConstants;
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
public class StringTypeTest extends TestUtils{
    
    public StringTypeTest() {
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
        report("correctType");
        StringType instance = new StringType();
        Value value = new LiteralImpl("10");
        assertTrue(instance.correctType(value));
        value = new LiteralImpl("10",XMLSchemaConstants.INTEGER);
        assertFalse(instance.correctType(value));
        value = new LiteralImpl("ten");
        assertTrue(instance.correctType(value));
        value = new LiteralImpl("ten",XMLSchemaConstants.STRING);
        assertTrue(instance.correctType(value));
    }

    /**
     * Test of getCorrectType method, of class IntegerType.
     */
    @Test
    public void testGetCorrectType() {
        report("getCorrectType");
        StringType instance = new StringType();
        String expResult = " A String";
        String result = instance.getCorrectType();
        assertEquals(expResult, result);
    }
}
