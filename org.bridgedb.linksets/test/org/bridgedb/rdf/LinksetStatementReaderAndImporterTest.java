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
package org.bridgedb.rdf;

import java.util.Set;
import org.bridgedb.linkset.LinksetLoader;
import org.bridgedb.linkset.rdf.LinksetStatementReaderAndImporter;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.utils.StoreType;
import org.bridgedb.utils.TestUtils;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Christian
 */
public class LinksetStatementReaderAndImporterTest extends TestUtils{
    
    public LinksetStatementReaderAndImporterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        //Check database is running and settup correctly or kill the test. 
        TestSqlFactory.checkSQLAccess();
        
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
     * Test of endRDF method, of class LinksetStatementReaderAndImporter.
     */
    @Test
    public void testLoadFromRDFNoOther() throws Exception {
        report("LoadFromRDFNoOther");
        new LinksetLoader().clearExistingData(StoreType.TEST);
        ValidationType validationType = ValidationType.LINKSMINIMAL;
        LinksetStatementReaderAndImporter instance = new LinksetStatementReaderAndImporter("test-data/testPart2.ttl", StoreType.TEST);
        Set result = instance.getLinkStatements();
        assertEquals(3, result.size());
        result = instance.getVoidStatements();
        assertEquals(4, result.size());
    }

    /**
     * Test of endRDF method, of class LinksetStatementReaderAndImporter.
     */
    @Test
    public void testLoadFromRDFWithOtherTest() throws Exception {
        report("LoadFromRDFWithOtherTest");
        LinksetLoader linksetLoader = new LinksetLoader();
        linksetLoader.clearExistingData(StoreType.TEST);
        ValidationType validationType = ValidationType.LINKSMINIMAL;
        linksetLoader.loadFile("../org.bridgedb.linksets/test-data/testPart1.ttl", StoreType.TEST, validationType);
        LinksetStatementReaderAndImporter instance = new LinksetStatementReaderAndImporter("test-data/testPart2.ttl", StoreType.TEST);
        Set result = instance.getLinkStatements();
        assertEquals(3, result.size());
        result = instance.getVoidStatements();
        assertEquals(8, result.size());
    }

    /**
     * Test of endRDF method, of class LinksetStatementReaderAndImporter.
     */
    @Test
    public void testLoadFromRDFWithOtherLive() throws Exception {
        report("LoadFromRDFWithOtherLive");
        new LinksetLoader().clearExistingData(StoreType.TEST);
        ValidationType validationType = ValidationType.LINKSMINIMAL;
        LinksetStatementReaderAndImporter instance = new LinksetStatementReaderAndImporter("test-data/testPart2.ttl", StoreType.LIVE);
        Set result = instance.getLinkStatements();
        assertEquals(3, result.size());
        result = instance.getVoidStatements();
        assertEquals(4, result.size());
    }
}
