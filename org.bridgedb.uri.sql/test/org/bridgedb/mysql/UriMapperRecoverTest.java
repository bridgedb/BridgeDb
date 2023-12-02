/* 
 * BridgeDb,
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.uri.UriListenerTest;
import org.bridgedb.uri.lens.Lens;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


/**
 * Runs the UriMapper interface tests over SQLUriMapper class
 * 
 * Creates the mapper, loads in the test data and then runs the tests.
 *
 * @author Christian
 */
@Tag("mysql")
public class UriMapperRecoverTest extends UriListenerTest {
    
    private static final String CREATOR1 = "testCreator";
    private static final String PREDICATE1 = "testMapping";
    private static final long CREATION1 = new Date().getTime();

    @Test
    @Tag("mysql")
    public void testRecover()throws BridgeDBException{
        TestSqlFactory.checkSQLAccess();
        ConfigReader.useTest();
        listener = SQLUriMapper.createNew();
        loadDataPart1();
        uriMapper = SQLUriMapper.getExisting();
        OverallStatistics stats = uriMapper.getOverallStatistics(Lens.ALL_LENS_NAME);
        stats.getNumberOfMappings();
        assertEquals(10, stats.getNumberOfMappingSets());
        assertEquals(12, stats.getNumberOfMappings());
        listener.recover();
        stats = uriMapper.getOverallStatistics(Lens.ALL_LENS_NAME);
        assertEquals(12, stats.getNumberOfMappings());
        assertEquals(4, stats.getNumberOfMappingSets());
        Resource resource = SimpleValueFactory.getInstance().createIRI("http://example.com/1to2Another");
        int mappingSet = listener.registerMappingSet(regexUriPattern1, TEST_PREDICATE, 
        		Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPattern2, resource);
        assertEquals(5, mappingSet);
        stats = uriMapper.getOverallStatistics(Lens.ALL_LENS_NAME);
        assertEquals(12, stats.getNumberOfMappings());
    }
}
