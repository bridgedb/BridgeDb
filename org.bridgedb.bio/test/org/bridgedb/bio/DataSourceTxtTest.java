// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
// Copyright 2006-2009 BridgeDb developers
// Copyright 2012-2013 Christian Brenninkmeijer
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
package org.bridgedb.bio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import org.bridgedb.DataSource;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Christian
 */
public class DataSourceTxtTest {
    
	@Test
	public void testUniqueSystemCodes() {
		DataSourceTxt.init();
		Set<String> codes = new HashSet<String>();
		Set<DataSource> sources = DataSource.getDataSources();
		Assert.assertNotSame(0, sources.size());
		for (DataSource source : sources) {
			codes.add(source.getSystemCode());
		}
		Assert.assertEquals(sources.size(), codes.size());
	}

	@Test
	public void systemCodesDoNotHaveWhitespace() {
		DataSourceTxt.init();
		Set<DataSource> sources = DataSource.getDataSources();
		Assert.assertNotSame(0, sources.size());
		for (DataSource source : sources) {
			String sysCode = source.getSystemCode();
			if (sysCode != null) {
				Assert.assertEquals(sysCode.length(), sysCode.trim().length());
				Assert.assertFalse(sysCode.contains(" "));
			}
		}
	}
    
    /**
     * Test of init and writer test method, of class DataSourceTxt.
     */
    @Test
    public void testWriteRead() throws IOException {
        System.out.println("WriteRead");
        DataSourceTxt.init();
        File generated = new File("test-data/generatedDatasources.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(generated));
 //       DataSourceTxt.writeToBuffer(writer);
        InputStream is = new FileInputStream(generated);
        DataSourceTxt.loadInputStream(is);
    }

}
