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
package org.bridgedb.rdb.mysql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.DriverManager;

import org.bridgedb.BridgeDb;
import org.bridgedb.IDMapperException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests the MySQL plugin without a MySQL server.
 */
public class MySQLPluginTest {

	@BeforeAll
	public static void loadDriver() throws ClassNotFoundException {
		Class.forName("org.bridgedb.rdb.IDMapperRdb");
	}

	@Test
	public void testPluginLoads() throws ClassNotFoundException {
		Class.forName("org.bridgedb.rdb.mysql.IDMapperMySQL");
	}

	@Test
	public void testMySQLDriverIsUsed() throws Exception {
		assertEquals("com.mysql.cj.jdbc.Driver",
			DriverManager.getDriver("jdbc:mysql://localhost/bridgedb").getClass().getName());
	}

	@Test
	public void testJdbcProtocolReachesTheDriver() {
		// nothing listens on port 1, so this fails in the MySQL driver, not for lack of a driver
		IDMapperException e = assertThrows(IDMapperException.class,
			() -> BridgeDb.connect("idmapper-jdbc:mysql://127.0.0.1:1/bridgedb?connectTimeout=2000"));
		assertTrue(e.getMessage().startsWith("Could not connect to database"), e.getMessage());
		assertFalse(e.getMessage().contains("No suitable driver"), e.getMessage());
	}

	@Test
	public void testOtherDatabasesAreNotOnClasspath() {
		for (String name : new String[] {"org.apache.derby.jdbc.EmbeddedDriver", "org.duckdb.DuckDBDriver"}) {
			assertThrows(ClassNotFoundException.class, () -> Class.forName(name), name);
		}
	}
}
