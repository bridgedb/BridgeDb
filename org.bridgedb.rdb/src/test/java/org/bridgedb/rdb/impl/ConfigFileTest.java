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
package org.bridgedb.rdb.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.bridgedb.rdb.ExampleDatabases;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests ConfigFile.protocolFor(), which picks the protocol for a plain path
 * in a gene database configuration file.
 */
public class ConfigFileTest {

	@TempDir
	File dir;

	@Test
	public void testDerby() throws Exception {
		File db = ExampleDatabases.copyTo(ExampleDatabases.DERBY, dir);
		assertEquals("idmapper-pgdb:", ConfigFile.protocolFor(db.getPath()));
	}

	@Test
	public void testDuckDB() throws Exception {
		File db = ExampleDatabases.copyTo(ExampleDatabases.DUCKDB, dir);
		assertEquals("idmapper-duckdb:", ConfigFile.protocolFor(db.getPath()));
	}

	@Test
	public void testMissingFile() {
		File missing = new File(dir, "missing.bridge");
		assertEquals("idmapper-pgdb:", ConfigFile.protocolFor(missing.getPath()));
	}

	@Test
	public void testUnknownFormat() throws Exception {
		File text = new File(dir, "text.bridge");
		Files.write(text.toPath(), "This is not a database.".getBytes(StandardCharsets.US_ASCII));
		assertEquals("idmapper-pgdb:", ConfigFile.protocolFor(text.getPath()));
	}
}
