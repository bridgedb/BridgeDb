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
package org.bridgedb.ws.server;

import java.io.File;
import java.net.MalformedURLException;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.file.IDMapperText;
import org.bridgedb.ws.WSCoreInterface;
import org.bridgedb.ws.WSCoreMapper;
import org.bridgedb.ws.WSCoreService;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class IDMapperTest extends org.bridgedb.utils.IDMapperTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException, MalformedURLException{
        File INTERFACE_TEST_FILE = new File ("../org.bridgedb/test-data/interfaceTest.txt");
        IDMapper inner = new IDMapperText(INTERFACE_TEST_FILE.toURL());
        WSCoreInterface  webService = new WSCoreService(inner);
        idMapper = new WSCoreMapper(webService);
        capabilities = idMapper.getCapabilities();        
    }

}
