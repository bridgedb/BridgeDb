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

import java.io.FileNotFoundException;
import org.bridgedb.sql.TestSqlFactory;
import org.openrdf.OpenRDFException;
import org.bridgedb.IDMapperException;
import java.io.IOException;
import org.bridgedb.utils.Reporter;
import org.bridgedb.linkset.LinksetLoader;
import org.bridgedb.metadata.validator.ValidationType;
import org.bridgedb.utils.StoreType;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class TransativeCreatorTest {
    
    private static StoreType VALIDATE_ONLY = null;
    private static URI GENERATE_PREDICATE = null;
    private static URI USE_EXISTING_LICENSES = null;
    private static URI NO_DERIVED_BY = null;
    private static boolean LOAD = true;
    private static boolean DO_NOT_LOAD = false;
    
    @BeforeClass
    public static void testLoader() throws IDMapperException, IOException, OpenRDFException, FileNotFoundException {
        //Check database is running and settup correctly or kill the test. 
        TestSqlFactory.createTestSQLAccess();
        LinksetLoader.clearExistingData( StoreType.TEST);        
        LinksetLoader.parse("../org.bridgedb.transitive/test-data/sample1to2.ttl", StoreType.TEST, ValidationType.LINKSMINIMAL, LOAD);
        LinksetLoader.parse("../org.bridgedb.transitive/test-data/sample1to3.ttl", StoreType.TEST, ValidationType.LINKSMINIMAL, LOAD);
	}
    
    @Test
    @Ignore
    public void testNoLinkToSelf() throws RDFHandlerException, IOException, IDMapperException {
        Reporter.report("NoLinkToSelf");
        String fileName = null;
        try {
            TransativeCreator.createTransative(1, 2, fileName, StoreType.TEST, GENERATE_PREDICATE, USE_EXISTING_LICENSES, NO_DERIVED_BY);
            assertFalse(true);
        } catch (Exception e){
            String error = "Source of mappingSet 1(TestDS2) is the same as the Target of 2. No need for a transative mapping";
            assertEquals(error, e.getMessage());
        }
    }

    @Test
    @Ignore
    public void testNoLink() {
        Reporter.report("NoLink");
        String fileName = null;
        try {
            TransativeCreator.createTransative(1, 3, fileName, StoreType.TEST, GENERATE_PREDICATE, USE_EXISTING_LICENSES, NO_DERIVED_BY);
            assertFalse(true);
        } catch (Exception e){
            String error = "Target of mappingSet 1 is TestDS2 Which is not the same as the Source of 3 which is TestDS1";
            assertEquals(error, e.getMessage());
        }
    }

    @Test
    public void testCreateTransative() throws RDFHandlerException, IOException, IDMapperException {
        Reporter.report("CreateTransative");
        String fileName = "../org.bridgedb.transitive/test-data/linkset2To3.ttl";
        TransativeCreator.createTransative(2, 3, fileName, StoreType.TEST, GENERATE_PREDICATE, USE_EXISTING_LICENSES, NO_DERIVED_BY);
        System.out.println("Ok");
        LinksetLoader.parse(fileName, VALIDATE_ONLY, ValidationType.LINKSMINIMAL, DO_NOT_LOAD);
    }

}
