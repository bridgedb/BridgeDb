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
package org.bridgedb.file;

//import buildsystem.Measure;

import java.io.File;
import java.net.MalformedURLException;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author Christian
 */
public class TestIDMapperText extends IDMapperTest {
	
	private static final File INTERFACE_TEST_FILE = new File ("test-data/interfaceTest.txt");
	
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException, MalformedURLException{
		idMapper = new IDMapperText(INTERFACE_TEST_FILE.toURL());
        capabilities = idMapper.getCapabilities();
	}
	
	@Test 
    public void testFileExists()
	{
        report("FileExists");
		Assert.assertTrue (INTERFACE_TEST_FILE.exists());
	}	
	
}
