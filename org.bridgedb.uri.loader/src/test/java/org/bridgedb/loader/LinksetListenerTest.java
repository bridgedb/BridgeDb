/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.loader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.uri.lens.Lens;
import org.bridgedb.uri.loader.LinksetListener;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.Reporter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Christian
 */
public class LinksetListenerTest {
    
    static final IRI linkPredicate = SimpleValueFactory.getInstance().createIRI("http://www.w3.org/2004/02/skos/core#exactMatch");
    static SQLUriMapper uriListener;
    static LinksetListener instance;

    public LinksetListenerTest() {
    }
    
    @BeforeAll
    public static void setUpClass() throws BridgeDBException {
        TestSqlFactory.checkSQLAccess();
        ConfigReader.useTest();
        uriListener = SQLUriMapper.createNew();
        instance = new LinksetListener(uriListener);
    }

    private void loadFile(String fileName, String justification) throws Exception{
        Reporter.println("parsing " + fileName);
    	File tempFile = File.createTempFile("TransativeCreatorTest", fileName.replace('/', '-'));
        InputStream in = LinksetListenerTest.class.getClassLoader().getResourceAsStream(fileName);
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
            out.close();
        }
        int mappingSetId = instance.parse(tempFile, linkPredicate, justification, true);
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
        loadFile("test-data/cw-cs.ttl", Lens.getDefaultJustifictaionString());
        loadFile("test-data/cs-ops.ttl", Lens.getDefaultJustifictaionString());
        loadFile("test-data/ops-ops_lensed.ttl", Lens.getTestJustifictaion());
        loadFile("test-data/cw-cs_lensed.ttl", Lens.getTestJustifictaion());
        loadFile("test-data/cs-ops_lensed.ttl", Lens.getTestJustifictaion());
    }

 }
//        linksetLoader.load("../org.bridgedb.tools.metadata/test-data/chemspider-void.ttl", StoreType.LOAD, ValidationType.VOID);
//        linksetLoader.load("../org.bridgedb.tools.metadata/test-data/chembl-rdf-void.ttl", StoreType.LOAD, ValidationType.VOID);
