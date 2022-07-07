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
package org.bridgedb.loader.transative;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.uri.lens.Lens;
import org.bridgedb.uri.loader.LinksetListener;
import org.bridgedb.uri.loader.transative.TransativeCreator;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.Reporter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class TransativeCreatorTest {
    
    static final URI linkPredicate = new URIImpl("http://www.w3.org/2004/02/skos/core#exactMatch");
    static SQLUriMapper uriListener;
    static LinksetListener instance;

    public TransativeCreatorTest() {
    }
    
    @BeforeAll
    public static void setUpClass() throws BridgeDBException {
        TestSqlFactory.checkSQLAccess();
        ConfigReader.useTest();
        UriPattern.refreshUriPatterns();
        uriListener = SQLUriMapper.createNew();
        instance = new LinksetListener(uriListener);
    }
    
    protected void loadFile(String fileName, String justification) throws Exception{
    	File tempFile = File.createTempFile("TransativeCreatorTest", fileName.replace('/', '-'));
        InputStream in = TransativeCreatorTest.class.getClassLoader().getResourceAsStream(fileName);
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
            out.close();
        }
        loadFile(tempFile, justification);
    }
    
    protected void loadFile(File file, String justification) throws BridgeDBException{
        Reporter.println("parsing " + file.getAbsolutePath());
        int mappingSetId = instance.parse(file, linkPredicate, justification, true);
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
        File transative = TransativeCreator.doTransativeIfPossible(1, 3);
        System.out.println(transative.getAbsolutePath());
        loadFile(transative.getAbsolutePath(), Lens.getDefaultJustifictaionString());
        File transative2 = TransativeCreator.doTransativeIfPossible(4, 2);
        //assertTrue(transative.exists());
        //loadFile(transative, MAIN_JUSTIFCATION);
    }

 }
