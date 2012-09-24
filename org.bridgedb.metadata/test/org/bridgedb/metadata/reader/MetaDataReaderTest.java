/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.reader;

import org.junit.Ignore;
import org.bridgedb.metadata.utils.Reporter;
import org.bridgedb.rdf.RdfStoreType;
import org.bridgedb.rdf.RepositoryFactory;
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
@Ignore //Slow but working
public class MetaDataReaderTest {
    
    public MetaDataReaderTest() {
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
     * Test of readMetaData method, of class MetaDataReader.
     */
    @Test
    public void testReadMetaData() throws Exception {
        Reporter.report("readMetaData");
        RepositoryFactory.clear(RdfStoreType.TEST);
        String fileName = "test-data/chemspider-void-small.ttl";
        MetaDataReader instance = new MetaDataReader();
        String expResult = "Loaded http://localhost:8080/OPS-IMS/void/1\n"
                + "Dataset void id http://rdf.chemspider.com/void-example.rdf#chemSpiderDataset_drugbank_subset OK!\n"
                + "Dataset void id http://rdf.chemspider.com/void-example.rdf#chemSpiderDataset_chembl_subset OK!\n"
                + "Dataset void id http://rdf.chemspider.com/void-example.rdf#chemSpiderDataset can only be used as a superset.\n"
                + "Decription void id http://rdf.chemspider.com/void-example.rdf OK!\n";
        String result = instance.readMetaData(fileName, RdfStoreType.TEST);
        assertEquals(expResult, result);
    }
}
