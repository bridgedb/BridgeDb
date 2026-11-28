// Copyright 2026 Egon Willighagen <egonw@users.sf.net>
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

import java.io.File;

import org.bridgedb.BridgeDb;
import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.rdb.DbFormatDetector;
import org.bridgedb.rdb.ExampleDatabases;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the DuckDB plugin with the DuckDB example database from org.bridgedb.rdb.
 */
public class ExampleDatabaseTest {

	@TempDir
	static File dir;

	private static File db;

	@BeforeAll
	public static void copyDatabase() throws Exception {
		DataSourceTxt.init();
		Class.forName("org.bridgedb.rdb.IDMapperRdb");
		db = ExampleDatabases.copyTo(ExampleDatabases.DUCKDB, dir);
	}

	@Test
	public void testFormat() throws Exception {
		assertEquals(DbFormatDetector.Format.DUCKDB, DbFormatDetector.detect(db));
	}

	@Test
	public void testDuckDBProtocol() throws Exception {
		ExampleDatabases.assertContent(BridgeDb.connect("idmapper-duckdb:" + db));
	}

	@Test
	public void testJdbcProtocol() throws Exception {
		ExampleDatabases.assertContent(BridgeDb.connect("idmapper-jdbc:duckdb:" + db));
	}
}
