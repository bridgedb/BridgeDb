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
package org.bridgedb.linkset.transative;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.linkset.LinksetLoader;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.utils.StoreType;
import org.bridgedb.utils.TestUtils;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class TransativeCreatorTest extends TestUtils {
    
    private static StoreType VALIDATE_ONLY = null;
    private static URI GENERATE_PREDICATE = null;
    private static URI USE_EXISTING_LICENSES = null;
    private static URI NO_DERIVED_BY = null;
    private static boolean LOAD = true;
    private static boolean DO_NOT_LOAD = false;
    
    @BeforeClass
    public static void testLoader() throws IDMapperException, IOException, OpenRDFException, FileNotFoundException {
        //Check database is running and settup correctly or kill the test. 
        TestSqlFactory.checkSQLAccess();
        
        LinksetLoader linksetLoader = new LinksetLoader();
        linksetLoader.clearExistingData( StoreType.TEST);        
        linksetLoader.loadFile("../org.bridgedb.tools.transitive/test-data/sample1to2.ttl", StoreType.TEST, ValidationType.LINKSMINIMAL);
        linksetLoader.loadFile("../org.bridgedb.tools.transitive/test-data/sample1to3.ttl", StoreType.TEST, ValidationType.LINKSMINIMAL);
	}

    @Test
    @Ignore
    public void testNoLink() {
        report("NoLink");
        String fileName = null;
        try {
            TransativeCreator.createTransative(1, 3, fileName, StoreType.TEST, GENERATE_PREDICATE, USE_EXISTING_LICENSES, NO_DERIVED_BY);
            assertFalse(true);
        } catch (Exception e){
            String error = "Target of mappingSet 1 is TestDS2 Which is not the same as the Source of 3 which is TestDS1";
            assertEquals(error, e.getMessage());
        }
    }

    //TODO cleanup this test!
    @Test
    public void testCreateTransative() throws RDFHandlerException, IOException, IDMapperException {
        report("CreateTransative");
        String fileName = "../org.bridgedb.tools.transitive/test-data/linkset2To3.ttl";
        TransativeCreator.createTransative(2, 3, fileName, StoreType.TEST, GENERATE_PREDICATE, USE_EXISTING_LICENSES, NO_DERIVED_BY);
        new LinksetLoader().checkFileValid(fileName, VALIDATE_ONLY, ValidationType.LINKSMINIMAL);
    }

}
