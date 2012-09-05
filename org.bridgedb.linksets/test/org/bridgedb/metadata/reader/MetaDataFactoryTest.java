/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.reader;

import org.bridgedb.utils.Reporter;
import java.io.File;
import org.bridgedb.metadata.MetaData;
import org.bridgedb.metadata.RDFData;
import org.bridgedb.metadata.RequirementLevel;
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
public class MetaDataFactoryTest {
    
    public MetaDataFactoryTest() {
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
     * Test of readVoid method, of class MetaDataFactory.
     */
    @Test
    public void testReadVoid() throws Exception {
        Reporter.report("readVoid");
        File file = new File ("test-data/ChemSpider.rdf");
        MetaData result = MetaDataFactory.readVoid(file);
        System.out.println("done readVoid");
        System.out.println(result.showAll(RequirementLevel.SHOULD));
        System.out.println("done show all");
        assertTrue(result.hasCorrectTypes());
        assertTrue(result.hasRequiredValues(RequirementLevel.MUST, true));
    }
}
