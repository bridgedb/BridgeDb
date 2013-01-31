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

import java.util.Date;
import org.bridgedb.Xref;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;
import org.bridgedb.utils.TestUtils;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Runs the URLMapper interface tests over SQLURLMapper class
 * 
 * Creates the mapper, loads in the test data and then runs the tests.
 *
 * @author Christian
 */
public class UriPatternSaveTest extends TestUtils {
    
    private static SQLUrlMapper mapper;
    
    @BeforeClass
    public static void setupIDMapper() throws BridgeDBException{
        TestSqlFactory.checkSQLAccess();
        mapper = new SQLUrlMapper(true, StoreType.TEST);
    }

    @Test
    public void testCheckUriPatterns() throws Exception {
        Date start = new Date();
        report("getCheckUriPatterns");
        for (UriPattern pattern:UriPattern.getUriPatterns()){
            String uri = pattern.getPrefix() + "1234" + pattern.getPostfix();
            Xref xref = mapper.toXref(uri);
            assertEquals(pattern.getDataSource(), xref.getDataSource());
        }
        Date end = new Date();
        System.out.println("That took " + (end.getTime()- start.getTime()));
     }

            
}
