/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ops;

import java.util.List;
import org.bridgedb.IDMapperException;
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
public class LinkSetStoreTest {
    
    protected static LinkSetStore instance;
    
    public LinkSetStoreTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        instance = new StubLinkSetStore();
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
     * Test of getLinksetNames method, of class LinkSetStore.
     */
    @Test
    public void testGetLinksetNames() throws Exception {
        List result = instance.getLinksetNames();
        assertTrue(result.size() >= 3);
    }

    /**
     * Test of getRDF method, of class LinkSetStore.
     */
    @Test
    public void testGetRDF() throws Exception {
        int id = 0;
        String result = instance.getRDF(id);
        assertNotNull(result);
    }

 }
