/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.type;

import org.bridgedb.utils.TestUtils;
import org.bridgedb.metadata.constants.XMLSchemaConstants;
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
public class XsdTypeTest extends TestUtils {
    
    public XsdTypeTest() {
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
     * Test of correctType method, of class XsdType.
     */
    @Test
    public void testIntegerTypes() {
        report("IntegerTypes");
        XsdType instance = new XsdType(XMLSchemaConstants.INTEGER.stringValue());
        Value value = new LiteralImpl("10",XMLSchemaConstants.INTEGER);;
        assertTrue(instance.correctType(value));
        value = new LiteralImpl("10",XMLSchemaConstants.NON_NEGATIVE_INTEGER);
        assertTrue(instance.correctType(value));
        value = new LiteralImpl("10");
        assertFalse(instance.correctType(value));
     }

    /**
     * Test of getCorrectType method, of class XsdType.
     */
    @Test
    public void testGetCorrectType() {
        report("getCorrectType");
        XsdType instance = new XsdType(XMLSchemaConstants.INTEGER.stringValue());
        String expResult = XMLSchemaConstants.INTEGER.stringValue();
        String result = instance.getCorrectType();
        assertEquals(expResult, result);
     }
}
