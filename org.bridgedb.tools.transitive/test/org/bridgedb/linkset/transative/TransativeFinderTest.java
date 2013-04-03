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
import org.bridgedb.linkset.LinksetLoader;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.uri.Profile;
import org.bridgedb.uri.UriListenerTest;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;
import org.bridgedb.utils.TestUtils;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class TransativeFinderTest extends TestUtils  {
 
    @BeforeClass
    public static void testLoader() throws BridgeDBException, IOException, OpenRDFException, FileNotFoundException {
        //Check database is running and settup correctly or kill the test. 
        TestSqlFactory.checkSQLAccess();
        
        LinksetLoader linksetLoader = new LinksetLoader();
        linksetLoader.clearExistingData( StoreType.TEST);        
        linksetLoader.load("../org.bridgedb.tools.transitive/test-data/sample1To2.ttl", StoreType.TEST, ValidationType.LINKSMINIMAL);
        linksetLoader.load("../org.bridgedb.tools.transitive/test-data/sample1To3.ttl", StoreType.TEST, ValidationType.LINKSMINIMAL);
	}

    @Test
	public void testFinder() throws BridgeDBException, RDFHandlerException, IOException {	
        report("testFinder");
        TransativeFinder transativeFinder = new TransativeFinder(StoreType.TEST);
        transativeFinder.UpdateTransative();
        report("testFinderDone");
	}
	

}
