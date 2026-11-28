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
package org.bridgedb.rdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.bridgedb.IDMapperException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests that DbFormatDetector tells Derby and DuckDB database files apart.
 */
public class DbFormatDetectorTest {

	@TempDir
	File tempDir;

	/**
	 * Returns a database file from the classpath.
	 */
	private static File resource(String name) throws URISyntaxException {
		URL url = DbFormatDetectorTest.class.getClassLoader().getResource(name);
		assertNotNull(url, "test database not found on the classpath: " + name);
		return new File(url.toURI());
	}

	@Test
	public void testDerby() throws URISyntaxException, IDMapperException {
		File f = resource("aminoacids_derby_db3.bridge");
		assertEquals(DbFormatDetector.Format.DERBY, DbFormatDetector.detect(f));
	}

	@Test
	public void testDuckDB() throws URISyntaxException, IDMapperException {
		File f = resource("aminoacids_duckdb_db3.bridge");
		assertEquals(DbFormatDetector.Format.DUCKDB, DbFormatDetector.detect(f));
	}

	@Test
	public void testNeither() throws IOException {
		File f = new File(tempDir, "text.bridge");
		Files.write(f.toPath(), "This is not a database.".getBytes(StandardCharsets.US_ASCII));
		assertThrows(IDMapperException.class, () -> DbFormatDetector.detect(f));
	}

	@Test
	public void testTooShort() throws IOException {
		File f = new File(tempDir, "short.bridge");
		Files.write(f.toPath(), new byte[] { 0x50, 0x4B, 0x03, 0x04 });
		assertThrows(IDMapperException.class, () -> DbFormatDetector.detect(f));
	}

	@Test
	public void testMissingFile() {
		File f = new File(tempDir, "missing.bridge");
		assertThrows(IDMapperException.class, () -> DbFormatDetector.detect(f));
	}
}
