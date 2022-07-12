/*
*BridgeDb,
*An abstraction layer for identifier mapping services, both local and online.
*Copyright (c) 2006-2009 BridgeDb Developers
*Copyright (c) 2012-2013 Christian Brenninkmeijer
*
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and limitations under the License.
*/
package org.bridgedb.bio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.bridgedb.DataSource;
import org.bridgedb.DataSourcePatterns;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 * @author Christian
 */
public class DataSourceTxtTest {

	@BeforeAll
	public static void setUpSources() {
		DataSourceTxt.init();
	}
	
	@org.junit.jupiter.api.Test
	public void testUniqueSystemCodes() {
		Set<String> codes = new HashSet<String>();
		Set<DataSource> sources = DataSource.getDataSources();
		assertNotSame(0, sources.size());
		for (DataSource source : sources) {
			codes.add(source.getSystemCode());
		}
		assertEquals(sources.size(), codes.size());
	}

	@org.junit.jupiter.api.Test
	public void systemCodesDoNotHaveWhitespace() {
		Set<DataSource> sources = DataSource.getDataSources();
		assertNotSame(0, sources.size());
		for (DataSource source : sources) {
			String sysCode = source.getSystemCode();
			if (sysCode != null) {
				assertEquals(sysCode.length(), sysCode.trim().length());
				assertFalse(sysCode.contains(" "));
			}
		}
	}
    
    /**
     * Test of init and writer test method, of class DataSourceTxt.
     */
	@org.junit.jupiter.api.Test
    	public void testWriteRead() throws IOException {
        System.out.println("WriteRead");
        File generated = File.createTempFile("UnitTest", "testWriteRead");
        BufferedWriter writer = new BufferedWriter(new FileWriter(generated));
        DataSourceTxt.writeToBuffer(writer);
        writer.close();
        InputStream is = new FileInputStream(generated);
        DataSourceTxt.loadInputStream(is);
    }

	@org.junit.jupiter.api.Test
    public void testCategories() throws Exception {
		DataSource source = DataSource.getExistingByFullName("MeSH");
		assertNotNull(source);
		assertNotNull(source.getCategories());
		assertEquals(4, source.getCategories().length);
	}

	@org.junit.jupiter.api.Test
	public void testWikidata() throws Exception {
	DataSource wikidata = DataSource.getExistingByFullName("Wikidata");
	assertNotNull(wikidata);
	assertTrue(wikidata.urlPatternKnown());
	assertEquals("Wd", wikidata.getSystemCode());
}

	@org.junit.jupiter.api.Test
    public void testWikidataBySystemCode() throws Exception {
    	DataSource wikidata = DataSource.getExistingByBioregistryPrefix("wikidata");
    	assertNotNull(wikidata);
    	assertTrue(wikidata.urlPatternKnown());
    	assertEquals("Wikidata", wikidata.getFullName());
    }

	@org.junit.jupiter.api.Test
    	public void testChEMBL() throws Exception {
    	DataSource wikidata = DataSource.getExistingByFullName("ChEMBL compound");
    	assertNotNull(wikidata);
    	assertTrue(wikidata.urlPatternKnown());
    	assertEquals("Cl", wikidata.getSystemCode());
    }

	@org.junit.jupiter.api.Test
    	public void testKNApSAcK() throws Exception {
    	DataSource wikidata = DataSource.getExistingByFullName("KNApSAcK");
    	assertNotNull(wikidata);
    	assertTrue(wikidata.urlPatternKnown());
    	assertEquals("Cks", wikidata.getSystemCode());
    }

	@org.junit.jupiter.api.Test
	public void testMIRIAMFeatures() throws Exception {
		DataSource chebi = DataSource.getExistingByFullName("ChEBI");
		assertNotNull(chebi);
		assertEquals("urn:miriam:chebi:1234", chebi.getMiriamURN("1234"));
		assertEquals("chebi", chebi.getCompactIdentifierPrefix());
	}

	@org.junit.jupiter.api.Test
	public void testPrefix() throws Exception {
		DataSource ds = DataSource.getExistingBySystemCode("L");
		String prefix = ds.getCompactIdentifierPrefix();
		assertEquals("ncbigene", prefix);
	}
	
	@org.junit.jupiter.api.Test
	public void testCatchIllegalArgumentException() throws Exception {
		DataSourceTxt dataSourceTxt = new DataSourceTxt();
		// use "[" as input for fields[9] in order to throw an IllegalArgumentException.
		String[] fields = {"Affy", "X", "", "", "", "", "", "", "","["};		
		assertThrows(IllegalArgumentException.class, () -> dataSourceTxt.loadLine(fields));
	}
	
	private DataSourceComparator dsc = new DataSourceComparator();

	@org.junit.jupiter.api.Test
	public void testSoftCompare() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method softCompare = DataSourceComparator.class.getDeclaredMethod("softCompare", String.class, String.class);
		Method compare = DataSourceComparator.class.getDeclaredMethod("compare", DataSource.class, DataSource.class);
		softCompare.setAccessible(true);
		int returnSoftCompare;
		compare.setAccessible(true);
		int returnCompare;
		
		DataSource dataSource1 = DataSource.register("X", "Affy")
			    .asDataSource();
		DataSource dataSource2 = DataSource.register("En", "Ensembl")
			    .asDataSource();
		
		String value1 = null;
		String value2 = null;
		
		returnSoftCompare = (int) softCompare.invoke(dsc, value1, value2);
		assertEquals(0, returnSoftCompare);
		
		value2 = "test";
		returnSoftCompare = (int) softCompare.invoke(dsc, value1, value2);
		assertEquals(-1, returnSoftCompare);		
		value1 = "test";
		value2 = null;
		returnSoftCompare = (int) softCompare.invoke(dsc, value1, value2);
		assertEquals(1, returnSoftCompare);
		
		returnCompare = (int) compare.invoke(dsc,dataSource1, dataSource2);
		returnSoftCompare = (int) softCompare.invoke(dsc,dataSource1.getFullName(), dataSource2.getFullName());
		assertEquals(returnSoftCompare, returnCompare);
		
	}
	

}
