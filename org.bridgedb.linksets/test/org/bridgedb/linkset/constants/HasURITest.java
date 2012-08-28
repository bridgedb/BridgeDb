/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset.constants;

import org.bridgedb.utils.Reporter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class HasURITest {
    
    public HasURITest() {
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
     * Test of getURI method, of class HasURI.
     */
    @Test
    public void testGetURI() {
        Reporter.report("getURI");
        HasURI instance = FrequencyOfChange.DAILY;
        URI expResult = new URIImpl (FrequencyOfChange.NAME_SPACE + "daily");
        URI result = instance.getURI();
        assertEquals(expResult, result);
    }

    /**
     * Test of getURI method, of class HasURI.
     */
    @Test
    public void testLegalValueTest() {
        Reporter.report("LegalValueTest");
        Class checkClass = FrequencyOfChange.class;
        URI check = new URIImpl (FrequencyOfChange.NAME_SPACE + "daily");
        Object[] hasUris = checkClass.getEnumConstants();
        boolean found = false;
        for (Object object:hasUris){
           HasURI hasUri = (HasURI)object;
           if (hasUri.getURI().equals(check)){
               found = true;
           }
        }
        assertTrue(found);
    }

    /**
     * Test of getURI method, of class HasURI.
     */
    @Test
    public void testIlegalValueTest() {
        Reporter.report("IlegalValueTest");
        Class checkClass = FrequencyOfChange.class;
        URI check = new URIImpl (FrequencyOfChange.NAME_SPACE + "NOTVALID");
        Object[] hasUris = checkClass.getEnumConstants();
        boolean found = false;
        for (Object object:hasUris){
           HasURI hasUri = (HasURI)object;
           if (hasUri.getURI().equals(check)){
               found = true;
           }
        }
        assertFalse(found);
    }
}
