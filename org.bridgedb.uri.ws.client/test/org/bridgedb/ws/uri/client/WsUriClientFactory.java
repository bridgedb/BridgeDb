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
package org.bridgedb.ws.uri.client;

import org.bridgedb.IDMapperException;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.ws.uri.client.WSUriClient;
import org.bridgedb.ws.WSUriInterface;

/**
 *
 * @author Christian
 */
public class WsUriClientFactory extends org.bridgedb.utils.IDMapperTestBase{
    
    public static WSUriInterface createTestWSClient() throws BridgeDBException{
        try {
            setupXref();
        } catch (IDMapperException e){
            throw BridgeDBException.convertToBridgeDB(e);
        }
        //ystem.out.println("in WSCoreInterface 1");
        WSUriInterface webService;
        if (ConfigReader.SANDBOX){
            webService = new WSUriClient("http://localhost:8080/OPS-IMS-TEST");
        } else {
            webService = new WSUriClient("http://localhost:8080/OPS-IMS");
        }
        //ystem.out.println("in WSCoreInterface 2");
        try { 
            webService.isFreeSearchSupported();
            //ystem.out.println("in WSCoreInterface 3");
        } catch (Exception ex) {
            System.err.println(ex);
            System.err.println ("***** SKIPPING WSClientTest ******");
            System.err.println ("Please make sure the server is running");
            org.junit.Assume.assumeTrue(false);        
        }
        if (!webService.isMappingSupported(DataSource1.getSystemCode(), DataSource2.getSystemCode()).isMappingSupported()){
        //ystem.out.println("in WSCoreInterface 5a");
            System.err.println ("***** SKIPPING WSClientTest ******");
            System.err.println ("It appears the Test data is not loaded");
            System.err.println ("remove ignore in TestDataToMainServerTest (org.bridgedb.ws.sqlserver) ");            
            org.junit.Assume.assumeTrue(false);        
        }
        //ystem.out.println("in WSCoreInterface 5b");
        return webService;
    }
}
