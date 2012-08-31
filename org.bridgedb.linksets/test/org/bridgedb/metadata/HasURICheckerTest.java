/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.junit.Ignore;
import org.bridgedb.utils.Reporter;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.Value;
import org.bridgedb.linkset.constants.FrequencyOfChange;
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
public class HasURICheckerTest {
    
    public HasURICheckerTest() {
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
     * Test of legalValue method, of class HasURIChecker.
     */
    @Test
    public void testLegalValue() {
        Reporter.report("legalValue");
        Value value = new URIImpl (FrequencyOfChange.NAME_SPACE + "daily");
        Class hasUriEnumClass = FrequencyOfChange.class;
        assertTrue (HasURIChecker.legalValue(value, hasUriEnumClass));
    }

    /**
     * Test of legalValue method, of class HasURIChecker.
     */
    @Test
    public void testIllegalValue() {
        Reporter.report("illegalValue");
        Value value = new URIImpl (FrequencyOfChange.NAME_SPACE + "nOt_vaLid");
        Class hasUriEnumClass = FrequencyOfChange.class;
        assertFalse (HasURIChecker.legalValue(value, hasUriEnumClass));
    }
}
