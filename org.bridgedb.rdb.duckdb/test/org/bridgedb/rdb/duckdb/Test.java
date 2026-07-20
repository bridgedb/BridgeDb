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
package org.bridgedb.rdb.duckdb;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import org.bridgedb.BridgeDb;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.bio.Organism;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;

/**
 * Tests for the DuckDB-backed IDMapper stack.
 */
public class Test {

	public static final String FILENAMES [] = { "yeast_id_mapping"};
	public static final Organism hS = Organism.HomoSapiens;
	public static final Organism aG = Organism.AnophelesGambiae;
	public static final Organism sC = Organism.SaccharomycesCerevisiae;
    public static final int DEFAULT_BUFFER_SIZE = 8192;
	private SimpleGdb newGdb;
	private SimpleGdb newGdbNull;

	@BeforeAll
	public static void setup() {
		DataSourceTxt.init();
	}

	/**
	 * GdbProvider.fromConfigFile() opens every mapper it is pointed at, which
	 * requires a real gene database file to connect to. Unlike the Derby-based
	 * org.bridgedb.rdb module, there is no bundled DuckDB fixture database to
	 * exercise this with, so this is disabled until such a fixture is added.
	 */
	@Disabled("requires a DuckDB gene database fixture file, which is not bundled with this module")
	@org.junit.jupiter.api.Test
	public void testGdbProvider() throws ClassNotFoundException, IDMapperException, IOException {
	}

	@org.junit.jupiter.api.Test
	public void testSimpleGdb() throws ClassNotFoundException, IDMapperException, IOException, SQLException{
		// Load drivers
		Class.forName("org.bridgedb.file.IDMapperText");
		Class.forName("org.bridgedb.rdb.duckdb.IDMapperRdb");

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
}
