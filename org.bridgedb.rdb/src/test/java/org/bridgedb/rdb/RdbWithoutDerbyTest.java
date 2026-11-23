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
package org.bridgedb.rdb;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bridgedb.BridgeDb;
import org.bridgedb.IDMapperException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests that org.bridgedb.rdb works without Derby.
 */
public class RdbWithoutDerbyTest {

	@BeforeAll
	public static void loadDriver() throws ClassNotFoundException {
		Class.forName("org.bridgedb.rdb.IDMapperRdb");
	}

	@Test
	public void testDerbyIsNotOnClasspath() {
		for (String name : new String[] {"org.apache.derby.jdbc.EmbeddedDriver",
				"org.apache.derby.jdbc.ClientDriver", "org.bridgedb.rdb.derby.IDMapperDerby"}) {
			assertThrows(ClassNotFoundException.class, () -> Class.forName(name), name);
		}
	}

	@Test
	public void testJdbcProtocolIsRegistered() {
		IDMapperException e = assertThrows(IDMapperException.class,
			() -> BridgeDb.connect("idmapper-jdbc:nosuchdatabase:test"));
		assertTrue(e.getMessage().startsWith("Could not connect to database"), e.getMessage());
	}

	@Test
	public void testDerbyProtocolsAreNotRegistered() {
		for (String protocol : new String[] {"idmapper-pgdb", "idmapper-derbyclient"}) {
			IDMapperException e = assertThrows(IDMapperException.class,
				() -> BridgeDb.connect(protocol + ":test"));
			assertTrue(e.getMessage().startsWith("Unknown protocol"), e.getMessage());
		}
	}
}
