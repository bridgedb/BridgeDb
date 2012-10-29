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

import java.util.Date;
import javax.xml.datatype.DatatypeConfigurationException;
import org.bridgedb.IDMapperException;
import org.bridgedb.linkset.LinksetLoader;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.utils.StoreType;
import org.bridgedb.ws.WSOpsClientFactory;
import org.bridgedb.ws.WSOpsInterface;
import org.bridgedb.ws.WSOpsMapper;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This class depends on URLListenerTest having loaded the data.
 * 
 * @author Christian
 */
@Ignore
public class LinksetInterfaceMinimalTest extends org.bridgedb.linkset.LinksetInterfaceMinimalTest {
    
    static WSOpsMapper wsOpsMapper;
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException {
        WSOpsInterface webService = WSOpsClientFactory.createTestWSClient();
        wsOpsMapper = new WSOpsMapper(webService);
    }
      
    public LinksetInterfaceMinimalTest() 
            throws DatatypeConfigurationException, MetaDataException {
        super(wsOpsMapper);
    }
    
    @Test
    public void testValidateStringAsDatasetVoid() throws Exception {
    }

    /**
     * Test of validateStringAsLinksetVoid method, of class LinksetInterface.
     */
    @Test
    public void testValidateStringAsLinksetVoid() throws Exception {
    }

    /**
     * Test of validateStringAsLinkset method, of class LinksetInterface.
     */
    @Test
    public void testValidateStringAsLinks() throws Exception {
    }

     /**
     * Test of load method, of class LinksetInterface.
     */
    @Test
    public void testLoadStrings() throws Exception {
    }

   /**
     * Test of validate method, of class LinksetInterface.
     */
    @Test
    public void testCheckStringValid() throws Exception {
    }

}
