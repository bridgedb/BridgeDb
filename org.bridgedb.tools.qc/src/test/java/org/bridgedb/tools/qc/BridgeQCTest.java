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
package org.bridgedb.tools.qc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;

import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.rdb.ExampleDatabases;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests comparing databases with BridgeQC, with the Derby and DuckDB example
 * databases from org.bridgedb.rdb. Both have the same content.
 */
public class BridgeQCTest {

	@TempDir
	static File dir;

	private static File derby;
	private static File duckdb;

	/** the number of identifiers per data source in the example databases */
	private static final String[] COUNTS = {
		"Wd (Wikidata): 849", "Cs (Chemspider): 510", "Ce (ChEBI): 1272",
		"Ca (CAS): 563", "Ik (InChIKey): 786", "Ch (HMDB): 188"
	};

	@BeforeAll
	public static void copyDatabases() throws Exception {
		DataSourceTxt.init();
		derby = ExampleDatabases.copyTo(ExampleDatabases.DERBY, dir);
		duckdb = ExampleDatabases.copyTo(ExampleDatabases.DUCKDB, dir);
	}

	/**
	 * Compares two databases, as the qc command does, and returns the report.
	 */
	private static String compare(File oldDb, File newDb) throws Exception {
		ByteArrayOutputStream report = new ByteArrayOutputStream();
		BridgeQC qc = new BridgeQC(oldDb, newDb, report);
		try {
			qc.run();
		} finally {
			qc.close();
		}
		return report.toString(StandardCharsets.UTF_8);
	}

	/**
	 * Checks the report of comparing two example databases: the same content,
	 * so nothing changed, and no warnings or errors.
	 */
	private static void assertUnchanged(String report) {
		for (String count : COUNTS) {
			assertTrue(report.contains("INFO: Number of ids in " + count + " (unchanged)"), count + " in:\n" + report);
		}
		for (String db : new String[] {"OLD", "NEW"}) {
			assertTrue(report.contains("INFO: " + db + " database has a total number of identifiers of 4168"), report);
			assertTrue(report.contains("INFO: " + db + " database has a total number of mappings of 4572"), report);
		}
		assertFalse(report.contains("only in old database"), report);
		assertFalse(report.contains("only in new database"), report);
		assertFalse(report.contains("WARNING"), report);
		assertFalse(report.contains("ERROR"), report);
	}

	@Test
	public void testDerbyWithItself() throws Exception {
		String report = compare(derby, derby);
		assertTrue(report.contains("INFO: old database '" + derby + "' detected as a Derby database"), report);
		assertTrue(report.contains("INFO: new database '" + derby + "' detected as a Derby database"), report);
		assertUnchanged(report);
	}

	@Test
	public void testDerbyWithDuckDB() throws Exception {
		String report = compare(derby, duckdb);
		assertTrue(report.contains("INFO: old database '" + derby + "' detected as a Derby database"), report);
		assertTrue(report.contains("INFO: new database '" + duckdb + "' detected as a DuckDB database"), report);
		assertUnchanged(report);
	}
}
