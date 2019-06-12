/* BridgeDb,
 * An abstraction layer for identifier mapping services, both local and online.
 *
 * Copyright 2006-2009  BridgeDb developers
 * Copyright 2012-2013  Christian Y. A. Brenninkmeijer
 * Copyright 2012-2013  OpenPhacts
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bridgedb.mysql;

import java.util.Date;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;

import org.junit.jupiter.api.BeforeAll;


/**
 * Runs the UriMapper interface tests over SQLUriMapper class
 * 
 * Creates the mapper, loads in the test data and then runs the tests.
 *
 * @author Christian
 */
public class UriMapperBySysCodeIdTest extends org.bridgedb.uri.UriMapperBySysCodeIdTest {
    
    private static final String CREATOR1 = "testCreator";
    private static final String PREDICATE1 = "testMapping";
    private static final long CREATION1 = new Date().getTime();

    @BeforeAll
    public static void setupIDMapper() throws BridgeDBException{

        connectionOk = false;
        TestSqlFactory.checkSQLAccess();
        connectionOk = true;
        ConfigReader.useTest();
        listener = SQLUriMapper.createNew();
        loadData();
        uriMapper = SQLUriMapper.getExisting();
    }
            
}
