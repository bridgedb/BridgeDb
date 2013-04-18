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

import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class LensTest extends org.bridgedb.uri.UriListenerTest {
    
    static SQLUriMapper sqlUriMapper;
    
    @BeforeClass
    public static void setupIDMapper() throws BridgeDBException{

        connectionOk = false;
        TestSqlFactory.checkSQLAccess();
        connectionOk = true;
        sqlUriMapper = SQLUriMapper.factory(true, StoreType.TEST);
        listener = sqlUriMapper;
        loadData();
        uriMapper = sqlUriMapper;;
    }
            
    @Test
    public void testRegisterLens() throws Exception {
        report("RegisterLens");
        String name = "LensTest1";
        URI createdBy = new URIImpl("http://example.com/LensTest");
        URI[] justificationUris = new URI[2];
        justificationUris[0] = new URIImpl("http://example.com/Justifictaion1"); 
        justificationUris[1] = new URIImpl("http://example.com/Justifictaion2"); 
        String uri = sqlUriMapper.registerLens(name, createdBy,justificationUris);
    }

}
