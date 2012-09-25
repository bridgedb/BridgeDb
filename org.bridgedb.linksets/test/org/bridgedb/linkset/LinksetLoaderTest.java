// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
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

import java.io.FileNotFoundException;
import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.rdf.RdfStoreType;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.utils.Reporter;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.OpenRDFException;

/**
 * @author Christian
 */
public class LinksetLoaderTest {
        
    @BeforeClass
    public static void testLoader() throws IDMapperException, IOException, OpenRDFException  {
        //Check database is running and settup correctly or kill the test. 
        TestSqlFactory.createTestSQLAccess();
        
        LinksetLoader loader = new LinksetLoader();
        loader.clearLinksets(RdfStoreType.TEST);
        
        Reporter.report("sample2to1.ttl");
        loader.parse("../org.bridgedb.linksets/test-data/sample1to2.ttl", "test");
        
        Reporter.report("sample1to3.ttl");
        loader.parse("../org.bridgedb.linksets/test-data/sample1to3.ttl", "test");

        Reporter.report("sample2to3.ttl");
        loader.parse("../org.bridgedb.linksets/test-data/sample2to3.ttl", "test");
        
        Reporter.report("cw-cs.ttl");
        loader.parse("../org.bridgedb.linksets/test-data/cw-cs.ttl", "test");
        
        Reporter.report("cw-cm.ttl");
        loader.parse("../org.bridgedb.linksets/test-data/cw-cm.ttl", "test");
        
        Reporter.report("cw-dd.ttl");
        loader.parse("../org.bridgedb.linksets/test-data/cw-dd.ttl", "test");
        
        Reporter.report("cw-ct.ttl");
        loader.parse("../org.bridgedb.linksets/test-data/cw-ct.ttl", "test");
        
        Reporter.report("cw-dt.ttl");
        loader.parse("../org.bridgedb.linksets/test-data/cw-dt.ttl", "test");
	}

    @Test(expected=FileNotFoundException.class)
    public void testFileNotFound() throws IDMapperException, FileNotFoundException {
    	LinksetLoader loader = new LinksetLoader();
    	loader.parse("noFile.xyz", "validate");
    }

    @Test
    public void testFileExists() throws IDMapperException, FileNotFoundException {
    	LinksetLoader loader = new LinksetLoader();
    	loader.parse("test-data/cw-dd.ttl", "validate");
    }

}
