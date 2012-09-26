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
package org.bridgedb.linkset.transative;

import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.mysql.MySQLSpecific;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.TestSqlFactory;
import org.openrdf.OpenRDFException;
import org.bridgedb.IDMapperException;
import java.io.IOException;
import org.bridgedb.utils.Reporter;
import org.bridgedb.linkset.LinksetLoader;
import org.bridgedb.linkset.LinksetLoaderTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
@Ignore //broken
public class TransativeCreatorTest {
    
    @BeforeClass
    public static void testLoader() throws IDMapperException, IOException, OpenRDFException  {
        //Check database is running and settup correctly or kill the test. 
        TestSqlFactory.createTestSQLAccess();
        SQLAccess sqlAccess = TestSqlFactory.createTestSQLAccess();
        //Clear the SQL where linkset ids come from
        new SQLUrlMapper(true, sqlAccess, new MySQLSpecific());
        
        Reporter.report("sample1to2.ttl");
        String[] args1 = {"../org.bridgedb.transitive/test-data/sample1to2.ttl", "testnew"};
        LinksetLoader.main (args1);
        Reporter.report("sample1to3.ttl");
        String[] args2 = {"../org.bridgedb.transitive/test-data/sample1to3.ttl", "test"};
        LinksetLoader.main (args2);
	}

    //TODO cleanup this test!
    /**
     * Test of main method, of class TransativeCreator.
     */
    @Test
    public void testMain() {
        Reporter.report("main");
        String[] args = new String[4];
        args[0] = "2";
        args[1] = "3";
        args[2] = "test";
        String fileName = "../org.bridgedb.transitive/test-data/linkset2To3.ttl";
//        String fileName = "test-data/linkset2To3.ttl";
        args[3] = fileName;
        try {
            TransativeCreator.main(args);
        } catch (Exception e){
            e.printStackTrace();
            assertTrue(false);
        }
        args = new String[2];
        args[0] = fileName;
        args[1] = "validate";
        try {
            LinksetLoader.main (args);
        } catch (Exception e){
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
