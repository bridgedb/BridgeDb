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
import org.bridgedb.DataSource;
import org.bridgedb.linkset.LinksetLoader;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;
import org.bridgedb.utils.TestUtils;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
@Ignore
public class TransativeFinder1Test extends TestUtils  {
 
    SQLUriMapper mapper;
    LinksetLoader linksetLoader;
 
    @Before
    public void testLoader() throws BridgeDBException, IOException, OpenRDFException, FileNotFoundException {
        //Check database is running and settup correctly or kill the test. 
        TestSqlFactory.checkSQLAccess();
        mapper = SQLUriMapper.factory(false, StoreType.TEST);
        linksetLoader = new LinksetLoader();
        linksetLoader.clearExistingData( StoreType.TEST);  
        DataSource transativeTestA = DataSource.register("TransativeTestA", "TransativeTestA").asDataSource();
        mapper.registerUriPattern(transativeTestA, "http://www.example.com/DS_A/$id");
        DataSource transativeTestB = DataSource.register("TransativeTestB", "TransativeTestB").asDataSource();
        mapper.registerUriPattern(transativeTestB, "http://www.example.com/DS_B/$id");
        DataSource transativeTestC = DataSource.register("TransativeTest_C", "TransativeTest_C").asDataSource();
        mapper.registerUriPattern(transativeTestC, "http://www.example.com/DS_C/$id");
        DataSource transativeTestD = DataSource.register("TransativeTestD", "TransativeTestD").asDataSource();
        mapper.registerUriPattern(transativeTestD, "http://www.example.com/DS_D/$id");
        DataSource transativeTestE = DataSource.register("TransativeTestE", "TransativeTestE").asDataSource();
        mapper.registerUriPattern(transativeTestE, "http://www.example.com/DS_E/$id");
        DataSource transativeTestF = DataSource.register("TransativeTestF", "TransativeTestF").asDataSource();
        mapper.registerUriPattern(transativeTestE, "http://www.example.com/DS_F/$id");
	}

    @Test
	public void testEmpty() throws BridgeDBException, RDFHandlerException, IOException {	
    }
    
 }
