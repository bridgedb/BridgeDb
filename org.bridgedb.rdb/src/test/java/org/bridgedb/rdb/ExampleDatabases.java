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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/**
 * The example databases in src/test/resources, for the tests of this module
 * and, through the test-jar, of the Derby and DuckDB plugins.
 * <p>
 * Both are schema 3 databases with the amino acids in Wikidata, made by
 * createAminoAcidDbs.py on 2026-11-26.
 */
public final class ExampleDatabases {

	/** The Derby example database. */
	public static final String DERBY = "aminoacids_derby_db3.bridge";

	/** The DuckDB example database, with the same content as the Derby one. */
	public static final String DUCKDB = "aminoacids_duckdb_db3.bridge";

	private ExampleDatabases() {}

	/**
	 * Copies an example database from the classpath into a directory. The
	 * database may be inside the test-jar, but the plugins need a file.
	 * @param name {@link #DERBY} or {@link #DUCKDB}
	 * @param dir the directory to copy it to, for example a JUnit TempDir
	 * @return the copy
	 * @throws IOException if the database is not on the classpath, or could not be copied
	 */
	public static File copyTo(String name, File dir) throws IOException {
		File copy = new File(dir, name);
		try (InputStream in = ExampleDatabases.class.getClassLoader().getResourceAsStream(name)) {
			if (in == null) throw new IOException("Example database not found on the classpath: " + name);
			Files.copy(in, copy.toPath());
		}
		return copy;
	}

	/**
	 * Checks the properties and a few mappings of an opened example database.
	 * The data sources must be initialized, for example with DataSourceTxt.init().
	 * @param mapper the opened Derby or DuckDB example database
	 * @throws IDMapperException if the database could not be queried
	 */
	public static void assertContent(IDMapper mapper) throws IDMapperException {
		assertProperties(mapper.getCapabilities());
		assertMappings(mapper);
	}

	/**
	 * Checks the properties of an opened example database.
	 * @param caps the capabilities of the opened Derby or DuckDB example database
	 */
	public static void assertProperties(IDMapperCapabilities caps) {
		assertEquals("3", caps.getProperty("SCHEMAVERSION"));
		assertEquals("WIKIDATA-AMINOACIDS", caps.getProperty("DATASOURCENAME"));
		assertEquals("WIKIDATA20261126", caps.getProperty("DATASOURCEVERSION"));
		assertEquals("Metabolite", caps.getProperty("DATATYPE"));
		assertEquals("test_aminoacids", caps.getProperty("SERIES"));
	}

	/**
	 * Checks a few mappings of an example database. This also works for an
	 * IDMapperStack with the database, which has no properties of its own.
	 * The data sources must be initialized, for example with DataSourceTxt.init().
	 * @param mapper the opened Derby or DuckDB example database, or a stack with it
	 * @throws IDMapperException if the database could not be queried
	 */
	public static void assertMappings(IDMapper mapper) throws IDMapperException {
		DataSource wikidata = DataSource.getExistingBySystemCode("Wd");
		DataSource hmdb = DataSource.getExistingBySystemCode("Ch");
		DataSource cas = DataSource.getExistingBySystemCode("Ca");

		// L-alanine
		Xref alanine = new Xref("Q218642", wikidata);
		assertTrue(mapper.xrefExists(alanine));
		assertEquals(Set.of(new Xref("HMDB0000161", hmdb)), mapper.mapID(alanine, hmdb));
		assertEquals(Set.of(new Xref("56-41-7", cas)), mapper.mapID(alanine, cas));

		// and back, from HMDB to Wikidata: Wikidata has L-alanine zwitterion
		// with the same HMDB identifier
		Xref zwitterion = new Xref("Q106345485", wikidata);
		assertEquals(Set.of(alanine, zwitterion), mapper.mapID(new Xref("HMDB0000161", hmdb), wikidata));

		// an identifier that is not in the database
		Xref unknown = new Xref("Q1", wikidata);
		assertFalse(mapper.xrefExists(unknown));
		assertTrue(mapper.mapID(unknown, hmdb).isEmpty());
	}
}
