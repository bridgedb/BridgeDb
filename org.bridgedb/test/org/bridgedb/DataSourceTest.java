/*
 *BridgeDb,
 *An abstraction layer for identifier mapping services, both local and online.
 *Copyright (c) 2012 Egon Willighagen <egonw@users.sf.net>
 *Copyright (c) 2013 Christian Brenninkmeijer
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and limitations under the License.
 */
package org.bridgedb;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.*;


public class DataSourceTest {

	@org.junit.jupiter.api.Test
	public void testAsDataSource() {
		DataSource source = DataSource.register("X", "Affymetrix")
		    .asDataSource();
		assertNotNull(source);
	}

	@org.junit.jupiter.api.Test
	public void testBuilding() {
		DataSource source = DataSource.register("X", "Affymetrix").asDataSource();
		assertEquals("X", source.getSystemCode());
		assertEquals("Affymetrix", source.getFullName());
	}

	@org.junit.jupiter.api.Test
	public void testBuildingMainUrl() {
		DataSource source = DataSource.register("X", "Affymetrix")
		    .mainUrl("http://www.affymetrix.com")
		    .asDataSource();
		assertEquals("http://www.affymetrix.com", source.getMainUrl());
	}

	@org.junit.jupiter.api.Test
	public void testChangeMainUrl() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DataSource source = DataSource.register("X", "Affymetrix")
					.mainUrl("http://www.affymetrix.com")
					.asDataSource();
			source = DataSource.register("X", "Affymetrix")
					.mainUrl("http://www.affymetrix.com/A")
					.asDataSource();
		});
	}

	@org.junit.jupiter.api.Test
	public void testBuildingType() {
		DataSource source = DataSource.register("X", "Affymetrix")
		    .type("probe")
		    .asDataSource();
		assertEquals("probe", source.getType());
		assertFalse(source.isMetabolite());
	}

	@Disabled
	@org.junit.jupiter.api.Test
	public void testBuildingType1() {
		DataSource source = DataSource.register("X", "Affymetrix")
		    .type("metabolite")
		    .asDataSource();
		assertEquals("metabolite", source.getType());
		assertTrue(source.isMetabolite());
	}

    //TODO check if changing primary is a needed functionality   
	@org.junit.jupiter.api.Test
	public void testBuildingPrimary() {
		DataSource source = DataSource.register("X", "Affymetrix")
		    .primary(false)
		    .asDataSource();
		assertFalse(source.isPrimary());
		source = DataSource.register("X", "Affymetrix")
			.primary(true)
			.asDataSource();
		assertTrue(source.isPrimary());
	}

	@org.junit.jupiter.api.Test
	public void testBuildingMetabolite() {
		DataSource source = DataSource.register("F", "MetaboLoci")
		    .type("metabolite")
		    .asDataSource();
		assertEquals("metabolite", source.getType());
		assertTrue(source.isMetabolite());
	}

	@org.junit.jupiter.api.Test
	public void testDeprecated() {
		DataSource source = DataSource.register("EnAg", "Ensembl Mosquito")
		    .deprecated(true).asDataSource();
		assertTrue(source.isDeprecated());
	}

	@org.junit.jupiter.api.Test
	public void testDeprecated_False() {
		DataSource source = DataSource.register("EnAg", "Ensembl Mosquito")
		    .deprecated(true).deprecated(false).asDataSource();
		assertFalse(source.isDeprecated());
	}

	/**
	 * By default, all new data sources are not deprecated.
	 */
	@org.junit.jupiter.api.Test
	public void testDefaultNotDeprecated() {
		DataSource source = DataSource.register("F", "MetaboLoci")
		    .asDataSource();
		assertFalse(source.isDeprecated());
	}

	@org.junit.jupiter.api.Test
	public void testDeprecatedBy() {
		DataSource source = DataSource.register("EnAg", "Ensembl Mosquito")
		    .deprecatedBy(
		    	DataSource.register("En", "Ensembl").asDataSource()
		    ).asDataSource();
		assertTrue(source.isDeprecated());
		assertNotNull(source.isDeprecatedBy());
		assertEquals("En", source.isDeprecatedBy().getSystemCode());
	}

	@org.junit.jupiter.api.Test
	public void testDeprecatedByUndoneByDeprecatedFalse() {
		DataSource source = DataSource.register("EnAg", "Ensembl Mosquito")
		    .deprecatedBy(
		    	DataSource.register("En", "Ensembl").asDataSource()
		    ).deprecated(false).asDataSource();
		assertFalse(source.isDeprecated());
		assertNull(source.isDeprecatedBy());
	}

	@org.junit.jupiter.api.Test
	public void testDeprecatedByNullCausesException() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DataSource.register("EnAg", "Ensembl Mosquito")
					.deprecatedBy(null).asDataSource();
			throw new IllegalArgumentException();
		});
	}

	@org.junit.jupiter.api.Test
	public void testDefaultNoDeprecatedBy() {
		DataSource source = DataSource.register("Cps", "PubChem-substance")
			.asDataSource();
		assertNull(source.isDeprecatedBy());
	}

	@org.junit.jupiter.api.Test
	public void testEquals() {
		DataSource source = DataSource.register("Cps", "PubChem-substance")
			.asDataSource();
		DataSource source2 = DataSource.register("Cps", "PubChem-substance")
			.asDataSource();
		assertEquals(source, source2);
	}

    //Since Version 2.0.09 this is no longer allowed!
	@org.junit.jupiter.api.Test
	public void testEqualsToo() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DataSource source = DataSource.register("Cpc", "PubChem-compound")
					.asDataSource();
			DataSource source2 = DataSource.register("Cpc", "PubChem compound")
					.asDataSource();
			assertEquals(source, source2);
			throw new IllegalArgumentException();
		});
	}
    
    @org.junit.jupiter.api.Test
	public void testChangeType() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DataSource source = DataSource.register("X", "Affymetrix")
					.type("metabolite")
					.asDataSource();
			assertEquals("metabolite", source.getType());
			assertTrue(source.isMetabolite());
			source = DataSource.register("X", "Affymetrix")
					.type("probe")
					.asDataSource();
			assertEquals("probe", source.getType());
			assertFalse(source.isMetabolite());
			throw new IllegalArgumentException();
		});
	}

	@org.junit.jupiter.api.Test
	public void testBuildingAlternative() {
		DataSource source = DataSource.register("F", "MetaboLoci")
		    .alternative("MetaboLoci Alternative")
		    .asDataSource();
		assertEquals("MetaboLoci Alternative", source.getAlternative());
	}

	@org.junit.jupiter.api.Test
	public void testBuildingDescription() {
		DataSource source = DataSource.register("F", "MetaboLoci")
		    .description("MetaboLoci description")
		    .asDataSource();
		assertEquals("MetaboLoci description", source.getDescription());
	}
}
