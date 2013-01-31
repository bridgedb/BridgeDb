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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.bridgedb.utils.TestUtils;
import org.junit.Test;

/**
 * NOTE: This test will fail if any previous tests have called BioDataSource.init()
 * @author Christian
 */
public class DataSourceLoaderTest extends TestUtils{
    
    private File utilsFile = new File("../org.bridgedb.utils/resources/DataSource.ttl");
    private static File renameFile = new File("test-data/DataSource.ttl");
    private static File primaryFile = new File("test-data/DataSourceWithPrimaries.ttl");

    /**
     * Test of writeRdfToFile method, of class BridgeDBRdfHandler.
     */
    @Test
    public void testRdfFileInputOutput() throws Exception {
        report("RdfFileInputOutput");
        BridgeDBRdfHandler.parseRdfFile(utilsFile);
        BridgeDBRdfHandler.writeRdfToFile(renameFile, false);
        BridgeDBRdfHandler.parseRdfFile(renameFile);
        BridgeDBRdfHandler.writeRdfToFile(primaryFile, true);
        BridgeDBRdfHandler.parseRdfFile(primaryFile);
    }
    
    public void testInputStreamInput() throws Exception {
        report("InputStreamInput");
        InputStream stream = new FileInputStream(utilsFile);
        BridgeDBRdfHandler.parseRdfInputStream(stream);
    }

    public void testInit() throws Exception {
        report("InputStreamInput");
        BridgeDBRdfHandler.init();
    }
}
