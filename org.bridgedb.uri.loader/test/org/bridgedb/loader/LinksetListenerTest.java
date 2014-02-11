/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.loader;

import java.io.File;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.uri.loader.LinksetListener;
import org.bridgedb.uri.loader.RdfParser;
import org.bridgedb.uri.lens.Lens;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.Reporter;
import static org.hamcrest.Matchers.*;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class LinksetListenerTest {
    
    static final URI linkPredicate = new URIImpl("http://www.w3.org/2004/02/skos/core#exactMatch");
    static SQLUriMapper uriListener;
    static LinksetListener instance;

    public LinksetListenerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws BridgeDBException {
        TestSqlFactory.checkSQLAccess();
        ConfigReader.useTest();
        uriListener = SQLUriMapper.createNew();
        instance = new LinksetListener(uriListener);
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    private void loadFile(String fileName, String justification) throws BridgeDBException{
        Reporter.println("parsing " + fileName);
        File file = new File(fileName);
        int mappingSetId = instance.parse(file, linkPredicate, justification);
        MappingSetInfo mapping = uriListener.getMappingSetInfo(mappingSetId);
        int numberOfLinks = mapping.getNumberOfLinks();
        assertThat(numberOfLinks, greaterThanOrEqualTo(3));
        
    }
    /**
     * Test of parse method, of class LinksetListener.
     */
    @Test
    public void testLoadTestData() throws Exception {
        Reporter.println("LoadTestData");
        loadFile("../org.bridgedb.uri.loader/test-data/cw-cs.ttl", Lens.getDefaultJustifictaionString());
        loadFile("../org.bridgedb.uri.loader/test-data/cs-cm.ttl", Lens.getDefaultJustifictaionString());
        loadFile("../org.bridgedb.uri.loader/test-data/cw-cm.ttl", Lens.getDefaultJustifictaionString());
        loadFile("../org.bridgedb.uri.loader/test-data/cw-ct.ttl", Lens.getDefaultJustifictaionString());
        loadFile("../org.bridgedb.uri.loader/test-data/cw-dd.ttl", Lens.getDefaultJustifictaionString());
        loadFile("../org.bridgedb.uri.loader/test-data/cw-dt.ttl", Lens.getDefaultJustifictaionString());
        loadFile("../org.bridgedb.uri.loader/test-data/cw-cs_test_lens.ttl", Lens.getTestJustifictaion());
        loadFile("../org.bridgedb.uri.loader/test-data/cs-cm_test_lens.ttl", Lens.getTestJustifictaion());
        loadFile("../org.bridgedb.uri.loader/test-data/cw-cm_test_lens.ttl", Lens.getTestJustifictaion());
    }

 }
//        linksetLoader.load("../org.bridgedb.tools.metadata/test-data/chemspider-void.ttl", StoreType.LOAD, ValidationType.VOID);
//        linksetLoader.load("../org.bridgedb.tools.metadata/test-data/chembl-rdf-void.ttl", StoreType.LOAD, ValidationType.VOID);
