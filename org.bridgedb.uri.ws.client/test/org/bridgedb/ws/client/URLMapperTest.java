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
package org.bridgedb.ws.client;

import com.sun.jersey.api.client.UniformInterfaceException;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.ws.WSOpsClientFactory;
import org.bridgedb.ws.WSCoreInterface;
import org.bridgedb.ws.WSCoreMapper;
import org.bridgedb.ws.WSOpsInterface;
import org.bridgedb.ws.WSOpsMapper;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Christian
 */
public class URLMapperTest  extends org.bridgedb.url.URLMapperTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException {
        WSOpsInterface webService = WSOpsClientFactory.createTestWSClient();
        mappingSet2_3 = 3;
        urlMapper = new WSOpsMapper(webService);
    }
    
    @Test
    @Override //TOO slow
    public void testGetOverallStatistics() throws IDMapperException {
    }

}
