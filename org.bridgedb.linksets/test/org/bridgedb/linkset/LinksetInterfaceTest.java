/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import org.junit.Ignore;
import org.bridgedb.linkset.rdf.RdfReader;
import java.util.Set;
import javax.xml.datatype.DatatypeConfigurationException;
import org.bridgedb.IDMapperException;
import org.bridgedb.metadata.AppendBase;
import org.bridgedb.metadata.MetaDataTestBase;
import org.bridgedb.metadata.validator.ValidationType;
import org.bridgedb.rdf.LinksetStatementReaderTest;
import org.bridgedb.metadata.rdf.StringOutputStream;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.Reporter;
import org.bridgedb.utils.StoreType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Christian
 */
public abstract class LinksetInterfaceTest extends LinksetInterfaceMinimalTest{
    
    LinksetInterface linksetInterface;
    
    public LinksetInterfaceTest(LinksetInterface instance) throws DatatypeConfigurationException, BridgeDBException{
        super(instance);
        linksetInterface = new LinksetLoader();
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
     * Test of validateFile method, of class LinksetInterface.
     */
    @Test
    public void testValidateFile() throws Exception {
        report("validateFile");
        String fileName = "../org.bridgedb.linksets/test-data/sample1to2.ttl";
        String result = linksetInterface.validateFile(fileName, StoreType.TEST, ValidationType.LINKSMINIMAL, false);
        assertThat(result, not(containsString("ERROR")));
        assertThat(result, containsString("Found 3 links"));
    }

    /**
     * Test of validateFileAsDatasetVoid method, of class LinksetInterface.
     */
    @Test
    public void testValidateFileAsDatasetVoid() throws Exception {
        report("validateFileAsDatasetVoid");
        String fileName = "../org.bridgedb.metadata/test-data/chemspider-void.ttl";
        String result = linksetInterface.validateFile(fileName, StoreType.TEST, ValidationType.VOID, true);
        assertThat(result, not(containsString("ERROR"))); 
        assertThat(result, containsString("INFO")); 
    }

    /**
     * Test of validateFileAsLinksetVoid method, of class LinksetInterface.
     * /
    @Test
    public void testValidateFileAsLinksetVoid() throws Exception {
        Reporter.report("validateFileAsLinksetVoid");
        String fileName = "../org.bridgedb.linksets/test-data/loadLinkSetwithLinks.xml";
        String result = linksetInterface.validateFileAsLinksetVoid(fileName);
        assertThat(result, not(containsString("ERROR")));
    }*/

    /**
     * Test of validateFileAsLinkset method, of class LinksetInterface.
     */
    @Test
    public void testValidateFileAsLinkset() throws Exception {
        report("validateFileAsLinkset");
        String fileName = "../org.bridgedb.linksets/test-data/loadLinkSetwithLinks.xml";
        String result = linksetInterface.validateFile(fileName, StoreType.TEST, ValidationType.LINKS, false);
        assertThat(result, not(containsString("ERROR")));
        assertThat(result, containsString("Found 2 links"));
   }

     /**
     * Test of load method, of class LinksetInterface.
     */
    @Test
    public void testLoadFile() throws Exception {
        report("load File");
        String fileName = "../org.bridgedb.linksets/test-data/sample1to2.ttl";
        linksetInterface.loadFile(fileName, StoreType.TEST, ValidationType.LINKSMINIMAL);
    }

    /**
     * Test of validate method, of class LinksetInterface.
     */
    @Test
    public void testCheckFileValid() throws Exception {
        report("CheckFileValid");
        String fileName = "../org.bridgedb.linksets/test-data/sample1to2.ttl";
        linksetInterface.checkFileValid(fileName, StoreType.TEST, ValidationType.LINKSMINIMAL);
    }

    /**
     * Test of clearExistingData method, of class LinksetInterface.
     */
    @Test
    public void testClearExistingData() throws Exception {
        report("clearExistingData");
        StoreType storeType = null;
        linksetInterface.clearExistingData(StoreType.TEST);
        RdfReader reader = new RdfReader(StoreType.TEST);
        String result = reader.getVoidRDF(1);
        //117 is length of empty rdf
        assertEquals(117, result.length());
    }

}
