// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
// Copyright 2006-2009 BridgeDb developers
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
package org.bridgedb.rdb.duckdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.bio.Organism;
import org.bridgedb.rdb.GdbProvider;
import org.bridgedb.rdb.SimpleGdb;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the DuckDB plugin with a small database.
 */
public class DuckDBPluginTest {

	@TempDir
	static File dir;

	private static File db;

	@BeforeAll
	public static void createDatabase() throws Exception {
		DataSourceTxt.init();
		Class.forName("org.bridgedb.rdb.IDMapperRdb");
		db = new File(dir, "test.bridge");
		try (Connection con = DriverManager.getConnection("jdbc:duckdb:" + db);
				Statement st = con.createStatement()) {
			st.execute("CREATE TABLE info (schemaversion INTEGER, BuildDate VARCHAR)");
			st.execute("INSERT INTO info VALUES (4, '20261122')");
			st.execute("CREATE TABLE datanode (id VARCHAR, code VARCHAR, isPrimary SMALLINT)");
			st.execute("INSERT INTO datanode VALUES ('3643', 'L', 1), ('ENSG00000171105', 'En', 1)");
			st.execute("CREATE TABLE link (idLeft VARCHAR, codeLeft VARCHAR, idRight VARCHAR, codeRight VARCHAR)");
			st.execute("INSERT INTO link VALUES ('3643', 'L', '3643', 'L'), ('3643', 'L', 'ENSG00000171105', 'En')");
			st.execute("CREATE TABLE attribute (id VARCHAR, code VARCHAR, attrname VARCHAR, attrvalue VARCHAR)");
		}
	}

	private static void assertMaps(IDMapper mapper) throws IDMapperException {
		DataSource ensembl = DataSource.getExistingBySystemCode("En");
		Set<Xref> result = mapper.mapID(new Xref("3643", DataSource.getExistingBySystemCode("L")), ensembl);
		assertEquals(1, result.size());
		assertEquals("ENSG00000171105", result.iterator().next().getId());
	}

	@Test
	public void testDuckDBProtocol() throws IDMapperException {
		assertMaps(BridgeDb.connect("idmapper-duckdb:" + db));
	}

	@Test
	public void testPropertyKeysAreUpperCase() throws IDMapperException {
		for (String protocol : new String[] {"idmapper-duckdb:", "idmapper-jdbc:duckdb:"}) {
			IDMapperCapabilities caps = BridgeDb.connect(protocol + db).getCapabilities();
			assertEquals(Set.of("SCHEMAVERSION", "BUILDDATE"), caps.getKeys());
			assertEquals("4", caps.getProperty("SCHEMAVERSION"));
			assertEquals("20261122", caps.getProperty("BUILDDATE"));
		}
	}

	@Test
	public void testJdbcProtocol() throws IDMapperException {
		assertMaps(BridgeDb.connect("idmapper-jdbc:duckdb:" + db));
	}

	@Test
	public void testSameFileTwice() throws IDMapperException {
		IDMapper first = BridgeDb.connect("idmapper-duckdb:" + db);
		assertMaps(first);
		assertMaps(BridgeDb.connect("idmapper-duckdb:" + db));
		assertMaps(first);
	}

	@Test
	public void testReadOnly() throws Exception {
		SimpleGdb gdb = (SimpleGdb) BridgeDb.connect("idmapper-duckdb:" + db);
		try (Statement st = gdb.getConnection().createStatement()) {
			assertThrows(SQLException.class, () -> st.execute("CREATE TABLE other (i INTEGER)"));
		}
	}

	@Test
	public void testMissingFileIsNotCreated() {
		File missing = new File(dir, "missing.bridge");
		assertThrows(IDMapperException.class, () -> BridgeDb.connect("idmapper-duckdb:" + missing));
		assertFalse(missing.exists());
	}

	@Test
	public void testGdbProvider() throws Exception {
		File config = new File(dir, "gdb.config");
		Files.writeString(config.toPath(), "Homo sapiens\tidmapper-duckdb:" + db + "\n");
		assertMaps(GdbProvider.fromConfigFile(config).getStack(Organism.HomoSapiens));
	}

	@Test
	public void testDerbyProtocolIsNotClaimed() {
		IDMapperException e = assertThrows(IDMapperException.class,
			() -> BridgeDb.connect("idmapper-pgdb:" + db));
		assertTrue(e.getMessage().startsWith("Unknown protocol"), e.getMessage());
	}

	@Test
	public void testOtherDatabasesAreNotOnClasspath() {
		for (String name : new String[] {"org.apache.derby.jdbc.EmbeddedDriver", "com.mysql.cj.jdbc.Driver"}) {
			assertThrows(ClassNotFoundException.class, () -> Class.forName(name), name);
		}
	}
}
