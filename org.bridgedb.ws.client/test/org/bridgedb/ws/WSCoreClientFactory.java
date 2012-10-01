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
package org.bridgedb.ws;

import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTestBase;

/**
 *
 * @author Christian
 */
public class WSCoreClientFactory extends IDMapperTestBase{
    
    public static WSCoreInterface createTestWSClient() throws IDMapperException{
        //ystem.out.println("in WSCoreInterface 1");
        WSCoreInterface webService = new WSCoreClient("http://localhost:8080/BridgeDb");
        //ystem.out.println("in WSCoreInterface 2");
        try { 
            webService.isFreeSearchSupported();
            //ystem.out.println("in WSCoreInterface 3");
        } catch (Exception ex) {
            ex.printStackTrace();
            report("***** SKIPPING BridgeDB WSClientTest ******");
            report("These test are repeated in the OPS client so normally not needed here.");
            report("Please make sure the specicifc bridgeDB.war based server is running");
            org.junit.Assume.assumeTrue(false);        
        }
        //ystem.out.println("in WSCoreInterface 4");
        if (!webService.isMappingSupported(DataSource1.getSystemCode(), DataSource2.getSystemCode()).isMappingSupported()){
            //ystem.out.println("in WSCoreInterface 5a");
            report ("***** SKIPPING WSClientTest ******");
            report ("It appears the Test data is not loaded");
            report("remove ignore in TestDataToMainServerTest (org.bridgedb.ws.sqlserver) ");            
            org.junit.Assume.assumeTrue(false);        
        }
        //ystem.out.println("in WSCoreInterface 5b");
        return webService;
    }
}