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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.bio.Organism;
import org.bridgedb.rdb.impl.ConfigFile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests reading a gene database configuration file with ConfigFile and
 * GdbProvider.fromConfigFile(). The mappers are text files, as this module
 * has neither the Derby nor the DuckDB plugin.
 */
public class TestConfigFile {

	@TempDir
	static File dir;

	private static DataSource entrez;
	private static DataSource embl;

	/** the connection string of a small text mapper: three lines of yeast_id_mapping.txt */
	private static String textMapper;

	@BeforeAll
	public static void setup() throws Exception {
		DataSourceTxt.init();
		Class.forName("org.bridgedb.file.IDMapperText");
		entrez = DataSource.getExistingBySystemCode("L");
		embl = DataSource.getExistingBySystemCode("Em");
		File mappings = new File(dir, "yeast.txt");
		Files.writeString(mappings.toPath(),
			entrez.getFullName() + "\t" + embl.getFullName() + "\n" +
			"856452\tU00061\n" +
			"856452\tK02204\n");
		textMapper = "idmapper-text:" + mappings.toURI().toURL();
	}

	private static File writeConfig(String name, String content) throws IOException {
		File config = new File(dir, name);
		Files.writeString(config.toPath(), content);
		return config;
	}

	private static void assertMaps(IDMapper mapper) throws IDMapperException {
		assertEquals(Set.of(new Xref("U00061", embl), new Xref("K02204", embl)),
			mapper.mapID(new Xref("856452", entrez), embl));
	}

	@Test
	public void testParse() throws IOException {
		File config = writeConfig("parse.config",
			"# a comment line\n" +
			"\n" +
			"Homo sapiens\tidmapper-duckdb:/data/Hs_DuckDB_Ensembl_115.bridge\n" +
			"Homo sapiens\tidmapper-pgdb:/data/Hs_Derby_Ensembl_115.bridge\n" +
			"*\t" + textMapper + "\n" +
			"a line without a tab\n" +
			"[drivers]\n" +
			"org.example.Driver\n");
		ConfigFile cf = new ConfigFile(config);
		// connection strings are kept as they are in the file
		assertEquals(Map.of(
			"Homo sapiens", List.of("idmapper-duckdb:/data/Hs_DuckDB_Ensembl_115.bridge",
				"idmapper-pgdb:/data/Hs_Derby_Ensembl_115.bridge"),
			"*", List.of(textMapper)), cf.getMappers());
		assertEquals(List.of("org.example.Driver"), cf.getDrivers());
	}

	@Test
	public void testPlainPaths(@TempDir File dbDir) throws IOException {
		// a plain path gets the protocol that matches the format of the file
		File derby = ExampleDatabases.copyTo(ExampleDatabases.DERBY, dbDir);
		File duckdb = ExampleDatabases.copyTo(ExampleDatabases.DUCKDB, dbDir);
		File config = writeConfig("paths.config",
			"Homo sapiens\t" + derby + "\n" +
			"*\t" + duckdb + "\n");
		assertEquals(Map.of(
			"Homo sapiens", List.of("idmapper-pgdb:" + derby),
			"*", List.of("idmapper-duckdb:" + duckdb)), new ConfigFile(config).getMappers());
	}

	@Test
	public void testPlainPathUndetected() throws IOException {
		// if the format can't be detected, the path is tried as a Derby
		// database, as before BridgeDb 4.0.2, and connecting reports the problem
		File config = writeConfig("undetected.config", "*\t/data/missing.bridge\n");
		assertEquals(Map.of("*", List.of("idmapper-pgdb:/data/missing.bridge")),
			new ConfigFile(config).getMappers());
	}

	@Test
	public void testMissingDerbyPlugin(@TempDir File dbDir) throws IOException {
		// this module's tests have neither the Derby nor the DuckDB plugin
		File derby = ExampleDatabases.copyTo(ExampleDatabases.DERBY, dbDir);
		File config = writeConfig("derby.config", "*\t" + derby + "\n");
		IDMapperException e = assertThrows(IDMapperException.class, () -> GdbProvider.fromConfigFile(config));
		assertTrue(e.getMessage().contains("module org.bridgedb.rdb.derby"), e.getMessage());
	}

	@Test
	public void testMissingDuckDBPlugin(@TempDir File dbDir) throws IOException {
		File duckdb = ExampleDatabases.copyTo(ExampleDatabases.DUCKDB, dbDir);
		File config = writeConfig("duckdb.config", "*\t" + duckdb + "\n");
		IDMapperException e = assertThrows(IDMapperException.class, () -> GdbProvider.fromConfigFile(config));
		assertTrue(e.getMessage().contains("module org.bridgedb.rdb.duckdb"), e.getMessage());
	}

	@Test
	public void testCommentAfterValue() throws IOException {
		File config = writeConfig("comment.config",
			"*\tidmapper-duckdb:/data/metabolites.bridge # a comment after a value\n");
		assertEquals(Map.of("*", List.of("idmapper-duckdb:/data/metabolites.bridge")),
			new ConfigFile(config).getMappers());
	}

	@Test
	public void testOrganism() throws Exception {
		File config = writeConfig("organism.config", "Anopheles gambiae\t" + textMapper + "\n");
		GdbProvider gdbs = GdbProvider.fromConfigFile(config);
		assertEquals(Set.of(Organism.AnophelesGambiae), gdbs.getOrganisms());
		assertTrue(gdbs.globalGdbs.isEmpty());
		assertMaps(gdbs.getStack(Organism.AnophelesGambiae));
		// another organism has no mappers
		assertTrue(gdbs.getStack(Organism.HomoSapiens).getMappers().isEmpty());
	}

	@Test
	public void testGlobal() throws Exception {
		File config = writeConfig("global.config", "*\t" + textMapper + "\n");
		GdbProvider gdbs = GdbProvider.fromConfigFile(config);
		assertTrue(gdbs.getOrganisms().isEmpty());
		assertEquals(1, gdbs.globalGdbs.size());
		// a global mapper is used for every organism
		assertMaps(gdbs.getStack(Organism.HomoSapiens));
		assertMaps(gdbs.getStack(Organism.SaccharomycesCerevisiae));
	}

	@Test
	public void testInvalidOrganism() throws Exception {
		File config = writeConfig("invalid.config", "Invalid\t" + textMapper + "\n");
		GdbProvider gdbs = GdbProvider.fromConfigFile(config);
		assertTrue(gdbs.getOrganisms().isEmpty());
		assertTrue(gdbs.globalGdbs.isEmpty());
	}

	@Test
	public void testAddGdbs() throws Exception {
		File config = writeConfig("add.config", "Anopheles gambiae\t" + textMapper + "\n");
		GdbProvider gdbs = GdbProvider.fromConfigFile(config);
		IDMapper mapper = BridgeDb.connect(textMapper);

		gdbs.addOrganismGdb(Organism.SaccharomycesCerevisiae, mapper);
		assertEquals(Set.of(Organism.AnophelesGambiae, Organism.SaccharomycesCerevisiae), gdbs.getOrganisms());
		assertMaps(gdbs.getStack(Organism.SaccharomycesCerevisiae));

		// adding the same global mapper twice adds it once, also to each organism
		gdbs.addGlobalGdb(mapper);
		gdbs.addGlobalGdb(mapper);
		assertEquals(List.of(mapper), gdbs.globalGdbs);
		assertEquals(2, gdbs.getStack(Organism.AnophelesGambiae).getMappers().size());

		gdbs.removeGlobalGdb(mapper);
		assertTrue(gdbs.globalGdbs.isEmpty());
		assertEquals(1, gdbs.getStack(Organism.AnophelesGambiae).getMappers().size());
	}
}
