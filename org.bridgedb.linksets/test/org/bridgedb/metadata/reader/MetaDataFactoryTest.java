/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.reader;

import org.junit.Ignore;
import org.bridgedb.IDMapperException;
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
    
    //Flags for easy reading of tests
    static final boolean ALLOW_ALTERATIVES = true;
    static final boolean NO_ALTERATIVES = false;;
    static final boolean INCLUDE_WARNINGS = true;
    static final boolean NO_WARNINGS = false;;

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

    private void checkAFile(String fileName) throws IDMapperException{
        File file = new File (fileName);
        MetaData result = MetaDataFactory.readVoid(file);
        String showAll = result.showAll(RequirementLevel.SHOULD);
        String report = result.validityReport(RequirementLevel.SHOULD, ALLOW_ALTERATIVES, NO_WARNINGS);
        System.out.println(report);
        assertTrue(result.hasCorrectTypes());
        assertTrue(result.hasRequiredValues(RequirementLevel.MUST, true));        
    }
    
    /**
     * Test of readVoid method, of class MetaDataFactory.
     */
    @Test
    @Ignore
    public void testChemSpiderVoIDDescriptor() throws Exception {
        Reporter.report("ChemSpider VoID Descriptor");
        checkAFile("test-data/ChemSpider.rdf");
    }

    /**
     * Test of readVoid method, of class MetaDataFactory.
     */
    @Test
    @Ignore
    public void testChEBMLRDFVoIDDescriptor() throws Exception {
        Reporter.report("ChEBML-RDF VoID Descriptor");
        checkAFile("test-data/ChEMBL.ttl");
    }

    /**
     * Test of readVoid method, of class MetaDataFactory.
     */
    @Test
    @Ignore
    public void testchebiHasPartsLinkset() throws Exception {
        Reporter.report("chebiHasPartsLinkset");
        checkAFile("test-data/chebiHasPartsLinkset.ttl");
    }
}
