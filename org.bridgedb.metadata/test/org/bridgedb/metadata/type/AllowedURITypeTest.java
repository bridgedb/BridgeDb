/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.type;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.bridgedb.metadata.utils.Reporter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Christian
 */
public class AllowedURITypeTest {
    
    static Element property;
    
    public AllowedURITypeTest() {
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
        child1.appendChild(doc.createTextNode("http://example.com#first"));
        property.appendChild(child1);
        Element child2 = doc.createElement("AllowedValue");
        child2.appendChild(doc.createTextNode("http://example.com#second"));
        property.appendChild(child2);
        Element child3 = doc.createElement("AllowedValue");
        child3.appendChild(doc.createTextNode("http://example.com#third"));
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
        Reporter.report("correctType");
        AllowedUriType instance = new AllowedUriType(property);
        Value value = new URIImpl("http://example.com#first");
        assertTrue(instance.correctType(value));
        value = new LiteralImpl("http://example.com#first");
        assertFalse(instance.correctType(value));
    }

    /**
     * Test of getCorrectType method, of class IntegerType.
     */
    @Test
    public void testGetCorrectType() {
        Reporter.report("getCorrectType");
        AllowedUriType instance = new AllowedUriType(property);
        String expResult = " URI in [http://example.com#first, http://example.com#second, http://example.com#third]";
        String result = instance.getCorrectType();
        assertEquals(expResult, result);
    }
}
