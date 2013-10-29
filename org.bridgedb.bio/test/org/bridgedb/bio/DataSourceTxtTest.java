/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
