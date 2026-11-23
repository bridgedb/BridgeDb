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
package org.bridgedb.rdb.construct;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the creation of DuckDB databases.
 */
public class DataDuckDBTest {

	@TempDir
	File dir;

	private static Xref xref;

	@BeforeAll
	public static void init() {
		DataSourceTxt.init();
		xref = new Xref("3643", DataSource.getExistingBySystemCode("L"));
	}

	private String name() {
		return new File(dir, "test").getPath();
	}

	private void assertDatabase(String... expectedInfo) throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:duckdb:" + name() + ".bridge")) {
			try (ResultSet info = con.createStatement().executeQuery("SELECT * FROM info")) {
				info.next();
				for (int i = 0; i < expectedInfo.length; i += 2) {
					assertEquals(expectedInfo[i + 1], info.getString(expectedInfo[i]));
				}
			}
			try (ResultSet count = con.createStatement().executeQuery("SELECT COUNT(*) FROM datanode")) {
				count.next();
				assertEquals(1, count.getInt(1));
			}
		}
	}

	@Test
	public void testSetInfoAfterPreInsert() throws Exception {
		GdbConstruct gdb = GdbConstructImpl4.createInstance(name(), new DataDuckDB(), DBConnector.PROP_RECREATE);
		gdb.createGdbTables();
		gdb.preInsert();
		gdb.setInfo("BUILDDATE", "20261122");
		gdb.setInfo("SERIES", "standard_metabolite");
		gdb.addGene(xref);
		gdb.setInfo("DATATYPE", "Metabolite");
		gdb.commit();
		gdb.finalize();
		assertDatabase("schemaversion", "4", "BUILDDATE", "20261122", "SERIES", "standard_metabolite",
			"DATATYPE", "Metabolite");
	}

	@Test
	public void testSetInfoAfterPreInsertSchema3() throws Exception {
		GdbConstruct gdb = GdbConstructImpl3.createInstance(name(), new DataDuckDB(), DBConnector.PROP_RECREATE);
		gdb.createGdbTables();
		gdb.preInsert();
		gdb.setInfo("BUILDDATE", "20261122");
		gdb.setInfo("SERIES", "standard_metabolite");
		gdb.addGene(xref);
		gdb.commit();
		gdb.finalize();
		assertDatabase("schemaversion", "3", "BUILDDATE", "20261122", "SERIES", "standard_metabolite");
	}

	@Test
	public void testSetInfoQuoting() throws Exception {
		String value = "O'Brien'); DROP TABLE info; --";
		GdbConstruct gdb = GdbConstructImpl4.createInstance(name(), new DataDuckDB(), DBConnector.PROP_RECREATE);
		gdb.createGdbTables();
		gdb.preInsert();
		gdb.setInfo("SERIES", value);
		gdb.setInfo("EMPTY", "");
		gdb.setInfo("MISSING", null);
		gdb.addGene(xref);
		gdb.commit();
		gdb.finalize();
		assertDatabase("SERIES", value, "EMPTY", "", "MISSING", null);
	}

	private void checkDuplicates(GdbConstruct gdb) throws Exception {
		gdb.createGdbTables();
		gdb.preInsert();
		assertEquals(0, gdb.addGene(xref));
		assertEquals(1, gdb.addGene(xref));
		assertNotNull(gdb.recentException());
		assertEquals(0, gdb.addLink(xref, xref));
		assertNull(gdb.recentException());
		assertEquals(1, gdb.addLink(xref, xref));
		assertNotNull(gdb.recentException());
		gdb.commit();
		// also a duplicate of committed data
		assertEquals(1, gdb.addGene(xref));
		gdb.commit();
		gdb.finalize();
		assertDatabase();
	}

	@Test
	public void testDuplicates() throws Exception {
		checkDuplicates(GdbConstructImpl4.createInstance(name(), new DataDuckDB(), DBConnector.PROP_RECREATE));
	}

	@Test
	public void testDuplicatesSchema3() throws Exception {
		checkDuplicates(GdbConstructImpl3.createInstance(name(), new DataDuckDB(), DBConnector.PROP_RECREATE));
	}

	@Test
	public void testNullId() throws Exception {
		GdbConstruct gdb = GdbConstructImpl4.createInstance(name(), new DataDuckDB(), DBConnector.PROP_RECREATE);
		gdb.createGdbTables();
		gdb.preInsert();
		assertEquals(0, gdb.addGene(xref));
		Xref noId = new Xref(null, xref.getDataSource());
		assertEquals(1, gdb.addGene(noId));
		assertEquals(1, gdb.addLink(xref, noId));
		assertEquals(0, gdb.addLink(xref, xref));
		gdb.commit();
		gdb.finalize();
		assertDatabase();
	}

	@Test
	public void testDuplicateOfExistingDatabase() throws Exception {
		GdbConstruct gdb = GdbConstructImpl4.createInstance(name(), new DataDuckDB(), DBConnector.PROP_RECREATE);
		gdb.createGdbTables();
		gdb.preInsert();
		gdb.addGene(xref);
		gdb.addLink(xref, xref);
		gdb.commit();
		gdb.finalize();

		DataDuckDB connector = new DataDuckDB();
		try (Connection con = connector.createConnection(name() + ".bridge", DBConnector.PROP_NONE)) {
			DBConnector.UniqueKeys keys = connector.getUniqueKeys(con, "datanode", "id", "code");
			try (PreparedStatement pst = con.prepareStatement("INSERT INTO datanode (id, code) VALUES (?, ?)")) {
				assertThrows(SQLException.class,
					() -> keys.insert(pst, xref.getId(), xref.getDataSource().getSystemCode()));
			}
		}
		assertDatabase();
	}

	private static void abortTransaction(Connection con) throws SQLException {
		try (Statement st = con.createStatement()) {
			st.execute("CREATE TABLE number (i INTEGER)");
		}
		con.setAutoCommit(false);
		try (Statement st = con.createStatement()) {
			assertThrows(SQLException.class, () -> st.execute("INSERT INTO number VALUES ('none')"));
		}
	}

	@Test
	public void testCommitOfAbortedTransactionFails() throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:duckdb:")) {
			abortTransaction(con);
			assertThrows(SQLException.class, () -> new DataDuckDB().commit(con));
		}
	}

	@Test
	public void testFinalizingAbortedTransactionFails() throws Exception {
		DataDuckDB connector = new DataDuckDB();
		try (Connection con = connector.createConnection(name(), DBConnector.PROP_RECREATE)) {
			abortTransaction(con);
			assertThrows(IDMapperException.class,
				() -> connector.closeConnection(con, DBConnector.PROP_FINALIZE));
			assertFalse(new File(name() + ".bridge").exists());
		}
	}
}
