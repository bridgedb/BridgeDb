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
package org.bridgedb.mysql;

import java.util.Set;
import org.apache.log4j.Logger;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.sql.SQLIdMapper;
import org.bridgedb.sql.SQLListener;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Loads the test data in and then runs the IDmapper and IDCapabiliies tests
 *
 * @author Christian
 */
//@Ignore 
public class MappingListenerTest extends org.bridgedb.mapping.MappingListenerTest {
    
    static final Logger logger = Logger.getLogger(MappingListenerTest.class);
    static SQLIdMapper sqlIdMapper;
    
    @BeforeClass
    public static void setupIDMapper() throws BridgeDBException{
        connectionOk = false;
        TestSqlFactory.checkMySQLAccess();
        listener = new SQLListener(true, StoreType.TEST);
        loadData();
        sqlIdMapper = new SQLIdMapper(false, StoreType.TEST);
        idMapper  = sqlIdMapper;
        connectionOk = true;
        capabilities = idMapper.getCapabilities(); 
        logger.info("MySQL Setup successfull");
    }
    
    @Test
    public void testMapIDOneToOne() throws IDMapperException{
        report("MapIDOneToOne");
        Set<Xref> results = sqlIdMapper.mapID(map1xref1, DataSource2);
        assertTrue(results.contains(map1xref2));
        assertFalse(results.contains(map1xref3));
        assertFalse(results.contains(map2xref1));
        assertFalse(results.contains(map2xref2));
        assertFalse(results.contains(map2xref2));
    }
 
      
}
