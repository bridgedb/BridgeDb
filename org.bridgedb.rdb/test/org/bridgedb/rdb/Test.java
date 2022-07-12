/*
 *BridgeDb,
 *An abstraction layer for identifier mapping services, both local and online.
 *Copyright (c) 2006 - 2009  BridgeDb Developers
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and limitations under the License.
 */
package org.bridgedb.rdb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.AttributeMapper;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.bio.Organism;
import org.bridgedb.impl.InternalUtils;
import org.bridgedb.rdb.SimpleGdb.QueryLifeCycle;
import buildsystem.Measure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Test access to the derby client running on the webservice.
 */
public class Test {
	
	private Measure measure;
	public static final String FILENAMES [] = { "yeast_id_mapping"};
	public static final Organism hS = Organism.HomoSapiens;
	public static final Organism aG = Organism.AnophelesGambiae;
	public static final Organism sC = Organism.SaccharomycesCerevisiae;
    public static final int DEFAULT_BUFFER_SIZE = 8192;	
	private SimpleGdb newGdb;
	private SimpleGdb newGdbNull;
	
	@BeforeEach
	public void setUp() {
		measure = new Measure("bridgedb_timing.txt");
	}

	@BeforeAll
	public static void setup() {
		DataSourceTxt.init();
	}
	
	@org.junit.jupiter.api.Test
	public void testGdbProvider() throws ClassNotFoundException, IDMapperException, IOException{
		Class.forName("org.bridgedb.file.IDMapperText");
		ClassLoader classLoader = this.getClass().getClassLoader();

		File gdbFile = new File("gdbTest.config");
		File gdbFileAg = new File("gdbTestAg.config");
		File gdbFileInvalid = new File("gdbTestInvalid.config");
		File gdbFileTestWrite = new File("gdbTestWrite.config");
		
		Set<Organism> orgSet = new HashSet<Organism>();
		Set<Organism> orgSetAg = new HashSet<Organism>();
		GdbProvider organismGdbProvider = new GdbProvider();
		GdbProvider gdbFromConfig = new GdbProvider();
		GdbProvider gdbFromConfigInvalid = new GdbProvider();
		GdbProvider gdbFromConfigAg = new GdbProvider();
		String filePath = "";
		String fileName = "yeast_id_mapping";
		String fullName = fileName + ".txt";
		URL url = classLoader.getResource(fullName);
		IDMapper m = BridgeDb.connect("idmapper-text:" + url);

		organismGdbProvider.addOrganismGdb(sC, m);
		organismGdbProvider.addGlobalGdb(m);
		organismGdbProvider.removeGlobalGdb(m);
		orgSet = organismGdbProvider.getOrganisms();
		assertTrue(orgSet.contains(sC));
		
		// Use a temp file to point to the unknownDataSource.bridge file in each .config file
		String dataFile = "unknownDataSource.bridge";
		File tmpFile = File.createTempFile(dataFile, ".bridge");
		System.out.println(tmpFile.getAbsolutePath());
		InputStream stream = classLoader.getResourceAsStream("unknownDataSource.bridge");
		System.out.println("stream: " + stream);
		System.out.println("path: " + tmpFile.toPath());
		tmpFile.delete();
		Files.copy(stream, tmpFile.toPath());
		filePath = tmpFile.getAbsolutePath();
		System.out.println("filePath: " + filePath);
		tmpFile.deleteOnExit();
		
		PrintWriter outputTest = new PrintWriter("gdbTest.config");
		outputTest.printf("*\t"+ filePath);
		outputTest.close();

		PrintWriter outputTestAg = new PrintWriter("gdbTestAg.config");
		outputTestAg.printf("Anopheles gambiae\t"+ filePath);
		outputTestAg.close();

		PrintWriter outputTestInvalid = new PrintWriter("gdbTestInvalid.config");
		outputTestInvalid.printf("Invalid\t"+ filePath);
		outputTestInvalid.close();

		gdbFromConfig = GdbProvider.fromConfigFile(gdbFile);
		assertNotNull(gdbFromConfig);
		assertFalse(gdbFromConfig.globalGdbs.isEmpty()&&gdbFromConfig.organism2gdb.isEmpty());
		// Test fromConfigFile() method with Anopheles gambiae as Organism in .config file
		gdbFromConfigAg = GdbProvider.fromConfigFile(gdbFileAg);
		assertNotNull(gdbFromConfigAg);
		assertFalse(gdbFromConfigAg.globalGdbs.isEmpty()&&gdbFromConfigAg.organism2gdb.isEmpty());
		// Test fromConfigFile() method with invalid Organism in .config file
		gdbFromConfigInvalid = GdbProvider.fromConfigFile(gdbFileInvalid);
		assertTrue(gdbFromConfigInvalid.globalGdbs.isEmpty()&&gdbFromConfigInvalid.organism2gdb.isEmpty());

		gdbFromConfig.addOrganismGdb(sC, m);
		orgSet = gdbFromConfig.getOrganisms();
		System.out.println(orgSet);
        assertTrue(orgSet.contains(sC));

        gdbFromConfigAg.addGlobalGdb(m);
		orgSetAg = gdbFromConfigAg.getOrganisms();
		System.out.println(orgSetAg);
        assertTrue(orgSetAg.contains(aG));
        // Test adding the same global gdb twice, ensure size remains = 1.
        gdbFromConfigAg.addGlobalGdb(m);
        assertTrue(orgSetAg.size() == 1);
	}
	
	@org.junit.jupiter.api.Test
	public void testSimpleGdb() throws ClassNotFoundException, IDMapperException, IOException, SQLException{
		// Load drivers
		Class.forName("org.bridgedb.file.IDMapperText");
		Class.forName("org.bridgedb.rdb.IDMapperRdb");
		Class.forName("com.mysql.jdbc.Driver");
		// no longer needed?? Class.forName("org.apache.derby.jdbc.ClientDriver");

		String fileName = "yeast_id_mapping";
		String fullName = fileName + ".txt";
		ClassLoader classLoader = this.getClass().getClassLoader();
		URL url = classLoader.getResource(fullName);
		assertNotNull(url, "Could not find resource in classpath: " + fullName);
		IDMapper m = BridgeDb.connect("idmapper-text:" + url);

		String connectionString = "idmapper-text:" + url;
		int pos = connectionString.indexOf(":");
		String location = connectionString.substring(pos + 1);
		String url2 = "jdbc:" + location;

        Assertions.assertThrows(IDMapperException.class, () -> {
        	newGdb = SimpleGdbFactory.createInstance(location, url2);
        });

        Assertions.assertThrows(NullPointerException.class, () -> {
        	newGdbNull = SimpleGdbFactory.createInstance("testNull", null);
        });
        
	}

	@Disabled
	@org.junit.jupiter.api.Test
	public void testDerbyClient() throws IDMapperException, ClassNotFoundException {
		long start, end, delta;
		start = System.currentTimeMillis();
		Class.forName ("org.bridgedb.rdb.IDMapperRdb");
		Class.forName ("org.apache.derby.jdbc.ClientDriver");
		
		IDMapper mapper = BridgeDb.connect ("idmapper-derbyclient:Homo sapiens?host=www.wikipathways.org");
		end = System.currentTimeMillis();
		delta = end - start;
		measure.add ("timing::idmapper-derbyclient connect to two databases", "" + delta, "msec");
		
		for (String key : mapper.getCapabilities().getKeys()) {
			System.out.println (key + " -> " + mapper.getCapabilities().getProperty(key));
		}

		System.out.println (mapper.getCapabilities().getSupportedTgtDataSources());
		
		Set <String> symbols = new HashSet<String>();
		AttributeMapper attr = (AttributeMapper)mapper;

		for (String key : attr.getAttributeSet()) {
			System.out.println (key);
		}

		// time the common case of doing a free search and then querying all for symbol
		start = System.currentTimeMillis();
		Map<Xref, String> symbolMap = attr.freeAttributeSearch("p53", "symbol", 100);
		end = System.currentTimeMillis();
		delta = end - start;
		System.out.println (delta);
		measure.add ("timing::idmapper-derbyclient free query for p53", "" + delta, "msec");
		System.out.println (symbols);
		
		// time the case of getting all attributes for backpage info
		start = System.currentTimeMillis();
		Xref insr = new Xref ("ENSG00000171105", DataSource.getExistingBySystemCode("En"));
		for (String x : new String[] {"Description", "Symbol", "Chromosome"})
		//TODO: Synonyms is also available, but not on ENSG.... ids
		{
			Set<String> result = attr.getAttributes(insr , x);
			assertTrue ( result.size() > 0, "No result for " + x);

			System.out.println (result);
		}
		
		end = System.currentTimeMillis();
		delta = end - start;
		measure.add ("timing::idmapper-derbyclient query for backpage attributes", "" + delta, "msec");
		System.out.println (delta);
	}
}
