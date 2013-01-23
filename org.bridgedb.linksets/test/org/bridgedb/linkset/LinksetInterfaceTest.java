// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.linkset;

import javax.xml.datatype.DatatypeConfigurationException;
import org.bridgedb.linkset.rdf.RdfReader;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;
import static org.hamcrest.Matchers.*;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Christian
 */
public abstract class LinksetInterfaceTest extends LinksetInterfaceMinimalTest{
    
    LinksetInterface linksetInterface;
    private static String cwCsFileName = "../org.bridgedb.linksets/test-data/cw-cs.ttl";
    
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
        String result = linksetInterface.validateFile(cwCsFileName, StoreType.TEST, ValidationType.LINKSMINIMAL, false);
        assertThat(result, not(containsString("ERROR")));
        assertThat(result, containsString("Found 3 links"));
    }

    /**
     * Test of validateFileAsDatasetVoid method, of class LinksetInterface.
     */
    @Test
    public void testValidateFileAsDatasetVoid() throws Exception {
        report("validateFileAsDatasetVoid");
        String fileName = "../org.bridgedb.tools.metadata/test-data/chemspider-void.ttl";
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
        linksetInterface.loadFile(cwCsFileName, StoreType.TEST, ValidationType.LINKSMINIMAL);
    }

    /**
     * Test of validate method, of class LinksetInterface.
     */
    @Test
    public void testCheckFileValid() throws Exception {
        report("CheckFileValid");
        linksetInterface.checkFileValid(cwCsFileName, StoreType.TEST, ValidationType.LINKSMINIMAL);
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
