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
package org.bridgedb.utils;

import java.io.File;
import org.junit.Test;

/**
 *
 * @author Christian
 */
public class DirectoriesConfigTest extends TestUtils {
    
    public DirectoriesConfigTest() {
    }

   /**
     * Test of getVoidDirectory method, of class DirectoriesConfig.
     */
    @Test
    public void testGetVoidDirectory() throws Exception {
        report("getVoidDirectory");
        File result = DirectoriesConfig.getVoidDirectory();
    }

    /**
     * Test of getLinksetDirectory method, of class DirectoriesConfig.
     */
    @Test
    public void testGetLinksetDirectory() throws Exception {
        report("getLinksetDirectory");
        File result = DirectoriesConfig.getLinksetDirectory();
    }

    /**
     * Test of getTransativeDirectory method, of class DirectoriesConfig.
     */
    @Test
    public void testGetTransativeDirectory() throws Exception {
        report("getTransativeDirectory");
        File result = DirectoriesConfig.getTransativeDirectory();
    }

    /**
     * Test of getExportDirectory method, of class DirectoriesConfig.
     */
    @Test
    public void testGetExportDirectory() throws Exception {
        report("getExportDirectory");
        File result = DirectoriesConfig.getExportDirectory();
    }
}
