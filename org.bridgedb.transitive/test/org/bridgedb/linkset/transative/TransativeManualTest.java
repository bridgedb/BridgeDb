/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset.transative;

import org.bridgedb.linkset.LinksetLoader;
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
public class TransativeManualTest {
    
    public TransativeManualTest() {
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
     * Test of main method, of class TransativeCreator.
     */
    @Test
    public void testMain() throws Exception {
        System.out.println("main");
        String[] args = new String[4];
        args[0] = "21";
        args[1] = "17";
        args[2] = "load";
        String fileName = "D:/OpenPhacts/ondex2linksets/JulyTransitive/cw-drugbankDrugs-transitive.ttl";
//        String fileName = "test-data/linkset2To3.ttl";
        args[3] = fileName;
        TransativeCreator.main(args);
        args = new String[2];
        args[0] = fileName;
        args[1] = "validate";
        LinksetLoader.main (args);
    }
}
