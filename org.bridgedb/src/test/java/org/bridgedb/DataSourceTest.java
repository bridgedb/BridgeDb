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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class DataSourceTest {

	@Test
	public void testAsDataSource() {
		DataSource source = DataSource.register("X", "Affymetrix")
		    .asDataSource();
		assertNotNull(source);
	}
	
	@Test
	public void testDataSourceCategories() {
		String[] categories = { "gene", "disease"};
		DataSource source = DataSource.mock("Me", "MeSH").categories(categories).asDataSource();
		assertNotNull(source);
		assertNotNull(source.getCategories());
		assertEquals(2, source.getCategories().length);
	}
	
	@Test
	public void testAddingCategories() {
		String[] categoriesArray =new String[] {"metabolites", "test", "test2"};
		DataSource source = DataSource.register("F", "MetaboLoci")
		    .categories(categoriesArray)
		    .asDataSource();
		assertEquals("metabolite", source.getType());
		assertTrue(source.isMetabolite());
	}

	@Test
	public void testBuilding() {
		DataSource source = DataSource.register("X", "Affymetrix").asDataSource();
		assertEquals("X", source.getSystemCode());
		assertEquals("Affymetrix", source.getFullName());
		
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> {
					DataSource.register("", "");
				});
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> {
					DataSource.register(null, "");
				});
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> {
					DataSource.register("X", "");
				});
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> {
					DataSource.register("X", null);
				});
		
	}

	@Test
	public void testBuildingMainUrl() {
		DataSource source = DataSource.register("X", "Affymetrix")
		    .mainUrl("http://www.affymetrix.com")
		    .urlPattern(null)
		    .asDataSource();
		assertEquals("http://www.affymetrix.com", source.getMainUrl());
		assertEquals(false, source.urlPatternKnown());
		
		DataSource source2 = DataSource.register("S", "Uniprot-TrEMBL")
				.urlPattern("http://www.uniprot.org/uniprot/$id")
			    .asDataSource();
		
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> {
					DataSource source3 = DataSource.register("S", "Uniprot-TrEMBL")
							.urlPattern("http://www.uniprot.org/uniprot/$id")
						    .asDataSource();
					source3 = DataSource.register("S", "Uniprot-TrEMBL")
							.urlPattern("http://www.uniprot.org/$id")
							.asDataSource();
					});
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> {
					DataSource source6 = DataSource.register("S", "Uniprot-TrEMBL")
							.urlPattern("http://www.uniprot.org/$id")
							.asDataSource();
					});

		assertEquals(true, source2.urlPatternKnown());
		assertEquals("http://identifiers.org/orphanet:$id/1234", source.getIdentifiersOrgUri("1234"));
		assertEquals("orphanet:$id:1234", source.getCompactIdentifier("1234"));
		
	}

	@Test
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

	@Test
	public void testBuildingType() {
		DataSource source = DataSource.register("X", "Affymetrix")
		    .type("probe")
		    .asDataSource();
		assertEquals("probe", source.getType());
		assertFalse(source.isMetabolite());
	}

	@Disabled
	@Test
	public void testBuildingType1() {
		DataSource source = DataSource.register("X", "Affymetrix")
		    .type("metabolite")
		    .asDataSource();
		assertEquals("metabolite", source.getType());
		assertTrue(source.isMetabolite());
	}

    //TODO check if changing primary is a needed functionality   
	@Test
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

	@Test
	public void testBuildingMetabolite() {
		DataSource source = DataSource.register("F", "MetaboLoci")
		    .type("metabolite")
		    .asDataSource();
		assertEquals("metabolite", source.getType());
		assertTrue(source.isMetabolite());
	}


	@Test
	public void testDeprecated() {
		DataSource source = DataSource.register("EnAg", "Ensembl Mosquito")
		    .deprecated(true).asDataSource();
		assertTrue(source.isDeprecated());
	}

	@Test
	public void testDeprecated_False() {
		DataSource source = DataSource.register("EnAg", "Ensembl Mosquito")
		    .deprecated(true).deprecated(false).asDataSource();
		assertFalse(source.isDeprecated());
	}

	/**
	 * By default, all new data sources are not deprecated.
	 */
	@Test
	public void testDefaultNotDeprecated() {
		DataSource source = DataSource.register("F", "MetaboLoci")
		    .asDataSource();
		assertFalse(source.isDeprecated());
	}

	@Test
	public void testDeprecatedBy() {
		DataSource source = DataSource.register("EnAg", "Ensembl Mosquito")
		    .deprecatedBy(
		    	DataSource.register("En", "Ensembl").asDataSource()
		    ).asDataSource();
		assertTrue(source.isDeprecated());
		assertNotNull(source.isDeprecatedBy());
		assertEquals("En", source.isDeprecatedBy().getSystemCode());
	}

	@Test
	public void testDeprecatedByUndoneByDeprecatedFalse() {
		DataSource source = DataSource.register("EnAg", "Ensembl Mosquito")
		    .deprecatedBy(
		    	DataSource.register("En", "Ensembl").asDataSource()
		    ).deprecated(false).asDataSource();
		assertFalse(source.isDeprecated());
		assertNull(source.isDeprecatedBy());
	}

	@Test
	public void testDeprecatedByNullCausesException() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DataSource.register("EnAg", "Ensembl Mosquito")
					.deprecatedBy(null).asDataSource();
		});
	}

	@Test
	public void testDefaultNoDeprecatedBy() {
		DataSource source = DataSource.register("Cps", "PubChem-substance")
			.asDataSource();
		assertNull(source.isDeprecatedBy());
	}

	@Test
	public void testEquals() {
		DataSource source = DataSource.register("Cps", "PubChem-substance")
			.asDataSource();
		DataSource source2 = DataSource.register("Cps", "PubChem-substance")
			.asDataSource();
		assertEquals(source, source2);
	}

    //Since Version 2.0.09 this is no longer allowed!
	@Test
	public void testEqualsToo() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DataSource source = DataSource.register("Cpc", "PubChem-compound")
					.asDataSource();
			DataSource source2 = DataSource.register("Cpc", "PubChem compound")
					.asDataSource();
			assertEquals(source, source2);
		});
	}
    
    @Test
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
		});
	}

	@Test
	public void testBuildingAlternative() {
		DataSource source = DataSource.register("F", "MetaboLoci")
		    .alternative("MetaboLoci Alternative")
		    .asDataSource();
		assertEquals("MetaboLoci Alternative", source.getAlternative());
	}

	@Test
	public void testBuildingDescription() {
		DataSource source = DataSource.register("F", "MetaboLoci")
		    .description("MetaboLoci description")
		    .asDataSource();
		assertEquals("MetaboLoci description", source.getDescription());
	}

	@Test
	public void testGetByPrefix() {
		DataSource chebi = DataSource.register("Ce", "ChEBI")
			.compactIdentifierPrefix("chebi")
			.identifiersOrgBase("")
			.urnBase(null)
			.type(null)
			.idExample("Ce")
			.mainUrl(null)
			.asDataSource();
		assertNotNull(chebi);
		
		DataSource chebi2 = DataSource.register("Ce", "ChEBI")
			.miriamBase("chebi")
			.type("")
			.identifiersOrgBase(null)
			.mainUrl("")
			.asDataSource();
		assertNotNull(chebi2);
		
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> {
					DataSource.register("Ce", "ChEBI")
					.miriamBase(null)
					.asDataSource();
				});
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> {
					DataSource.register("Ce", "ChEBI")
					.compactIdentifierPrefix(null)
					.asDataSource();
				});
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> {
					DataSource.register("Ce", "ChEBI")
					.urnBase("test")
					.asDataSource();
				});
		
		DataSource source = DataSource.getByCompactIdentifierPrefix("chebi");
		
		source = DataSource.register("Gpl", "Guide to Pharmacology")
			    .asDataSource();
		assertEquals(null, source.getMiriamURN(""));
		assertEquals(null, source.getCompactIdentifier("H"));
		assertEquals(null, source.getCompactIdentifier("Gpl"));
		
		assertEquals(null, source.getIdentifiersOrgUri(""));
		assertEquals(false, DataSource.systemCodeExists(""));

		Assertions.assertThrows(IllegalArgumentException.class,
				() -> {
					DataSource.getExistingBySystemCode("");
				});
		
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> {
					DataSource.getExistingByFullName("");
				});
	}
    

	@Test
	public void testBuilders() {
		
		DataSource source = DataSource.register("X", "Affymetrix")
				.type("probe")
				.organism(null)
				.alternative(null)
				.urnBase("")
				.identifiersOrgBase("http://identifiers.org/orphanet:$id")
				.asDataSource();
		assertEquals("probe",source.getType());
		assertEquals(null,source.getOrganism());
		assertEquals("Affymetrix",DataSource.getExistingByFullName("Affymetrix").toString());
		assertEquals(true,DataSource.fullNameExists("Affymetrix"));
		
		DataSource source2 = DataSource.register("Eco", "Ecocyc")
				.alternative("")
				.organism("Escherichia coli")
				.identifiersOrgBase("http://identifiers.org/orphanet/$id")
				.urnBase("Eco")
				.asDataSource();
		assertEquals("Escherichia coli",source2.getOrganism());
		assertNotNull(source2.getOrganism());
			
		DataSource source4 = DataSource.register("Ect", "EPA CompTox")
					.urnBase("urn:miriam:Ect")
					.description("")
					.asDataSource();
		assertEquals("EPA CompTox",DataSource.getByMiriamBase("urn:miriam:Ect").toString());
		assertEquals(null,DataSource.getByMiriamBase(null));
		assertEquals(null,DataSource.getByMiriamBase("test"));
		
		source4.registerAlias("Ect");
		assertEquals("EPA CompTox", DataSource.getByAlias("Ect").toString());
		
		DataSource.getExistingBySystemCode("Ect");
		
		DataSource source6 = DataSource.register("R", "RGD")
				.organism("Rattus norvegicus")
				.asDataSource();
		source6 = DataSource.register("R", "RGD")
				.organism("Rattus norvegicus")
				.asDataSource();
		assertEquals("Rattus norvegicus",source6.getOrganism());
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DataSource source5 = DataSource.register("R", "RGD")
					.organism("Rattus norvegicus")
					.asDataSource();
			source5 = DataSource.register("R", "RGD")
					.organism("Rattus")
					.asDataSource();
		});
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DataSource source5 = DataSource.register("Ect", "EPA CompTox")
					.urnBase("urn:miriam:Ect")
					.asDataSource();
			source5 = DataSource.register("Ect", "EPA CompTox")
					.urnBase("urn:miriam:ecogene")
					.asDataSource();
		});
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DataSource source5 = DataSource.register("Ect", "EPA CompTox")
					.alternative("Metaboloci Alternative")
					.asDataSource();
			source5 = DataSource.register("Ect", "EPA CompTox")
					.alternative("test")
					.asDataSource();
		});
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DataSource source5 = DataSource.register("Ect", "EPA CompTox")
					.alternative("Metaboloci Alternative")
					.asDataSource();
			source5 = DataSource.register("Ect", "EPA CompTox")
					.alternative("test")
					.asDataSource();
		});
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DataSource source5 = DataSource.register("Ect", "EPA CompTox")
					.description("EPA CompTox Dashboard")
					.asDataSource();
			source5 = DataSource.register("Ect", "EPA CompTox")
					.description("test")
					.asDataSource();
		});
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DataSource source5 = DataSource.register("Ect", "EPA CompTox")
					.asDataSource();
			source5 = DataSource.register("L", "EPA CompTox")
					.asDataSource();
		});
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DataSource source5 = DataSource.register("Ect", "EPA CompTox")
					.asDataSource();
			source5 = DataSource.register(null, "EPA CompTox")
					.asDataSource();
		});
	}
	
	@Test
	public void testGetters() {

		Set<DataSource> set = DataSource.getDataSources();
		assertNotNull(set);
		
		DataSource source = DataSource.register("Ect", "EPA CompTox")
				.urnBase("urn:miriam:Ect")
				.asDataSource();

		
		assertEquals(null, source.getKnownUrl("Ect"));
		
		assertNotNull(DataSource.getFilteredSet(false, false, source));
		assertNotNull(DataSource.getFilteredSet(null, null, null));
		assertNotNull(DataSource.getFilteredSet(true, true, source));
		assertNotNull(DataSource.getFilteredSet(null, true, source));
		assertNotNull(DataSource.getFilteredSet(null, false, source));
		assertNotNull(DataSource.getFilteredSet(null, false, "Escherichia coli"));
		
		List<String> result = DataSource.getFullNames();
		assertNotNull(result);
		
		assertEquals(null, DataSource.getByIdentiferOrgBase("base"));
		assertEquals(null, DataSource.getByIdentiferOrgBase(null));
		assertEquals(null, DataSource.getByIdentiferOrgBase("http://identifiers.org/ccds/"));
		assertEquals(null, DataSource.getByIdentiferOrgBase("http://identifiers.org/ccds"));
		assertNotNull(source.getCompactIdentifierPrefix());
	}

	@Test
	public void testBioregistry() {
		DataSource.register("Suniprot", "UniProt")
		    .bioregistryPrefix("uniprot")
		    .asDataSource();
		assertTrue(DataSource.bioregistryPrefixExists("uniprot"));
		assertFalse(DataSource.bioregistryPrefixExists("unifrot"));
	}
	
}
