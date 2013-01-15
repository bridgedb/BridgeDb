/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.type;

import org.bridgedb.tools.metadata.type.AllowedValueType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.bridgedb.utils.TestUtils;
import org.bridgedb.utils.Reporter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Christian
 */
public class AllowedValueTypeTest extends TestUtils{
    
    static Element property;
    
    public AllowedValueTypeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        //Create instance of DocumentBuilderFactory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //Get the DocumentBuilder
        DocumentBuilder parser = factory.newDocumentBuilder();
        //Create blank DOM Document 
        Document doc = parser.newDocument();
        property = doc.createElement("property");
        Element child1 = doc.createElement("AllowedValue");
        child1.appendChild(doc.createTextNode("first"));
        property.appendChild(child1);
        Element child2 = doc.createElement("AllowedValue");
        child2.appendChild(doc.createTextNode("second"));
        property.appendChild(child2);
        Element child3 = doc.createElement("AllowedValue");
        child3.appendChild(doc.createTextNode("third"));
        property.appendChild(child3);
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
        AllowedValueType instance = new AllowedValueType(property);
        Value value = new LiteralImpl("first");
        assertTrue(instance.correctType(value));
        value = new LiteralImpl("1st");
        assertFalse(instance.correctType(value));
    }

    /**
     * Test of getCorrectType method, of class IntegerType.
     */
    @Test
    public void testGetCorrectType() {
        report("getCorrectType");
        AllowedValueType instance = new AllowedValueType(property);
        String expResult = " One of [first, second, third]";
        String result = instance.getCorrectType();
        assertEquals(expResult, result);
    }
}
