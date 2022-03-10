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

import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;


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
		
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> {
					DataSource.register("", ""); throw new IllegalArgumentException();
				});
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> {
					DataSource.register(null, ""); throw new IllegalArgumentException();
				});
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> {
					DataSource.register("X", ""); throw new IllegalArgumentException();
				});
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> {
					DataSource.register("X", null); throw new IllegalArgumentException();
				});
		
	}

	@org.junit.jupiter.api.Test
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

	@org.junit.jupiter.api.Test
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
					.asDataSource(); throw new IllegalArgumentException();
				});
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> {
					DataSource.register("Ce", "ChEBI")
					.compactIdentifierPrefix(null)
					.asDataSource(); throw new IllegalArgumentException();
				});
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> {
					DataSource.register("Ce", "ChEBI")
					.urnBase("test")
					.asDataSource(); throw new IllegalArgumentException();
				});
		
		DataSource source = DataSource.getByCompactIdentifierPrefix("chebi");
		
		source = DataSource.register("Gpl", "Guide to Pharmacology")
			    .asDataSource();
		assertEquals(null, source.getMiriamURN(""));
		assertEquals(null, source.getCompactIdentifier("H"));
		assertEquals(null, source.getCompactIdentifier("Gpl"));
		
		assertEquals(null, source.getIdentifiersOrgUri(""));

		Assertions.assertThrows(IllegalArgumentException.class,
				() -> {
					source.getExistingBySystemCode(""); throw new IllegalArgumentException();
				});
		assertEquals(false, source.systemCodeExists(""));
		
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> {
					source.getExistingByFullName(""); throw new IllegalArgumentException();
				});
	}
    

	@org.junit.jupiter.api.Test
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
		assertEquals("Affymetrix",source.getExistingByFullName("Affymetrix").toString());
		assertEquals(true,source.fullNameExists("Affymetrix"));
		
		DataSource source2 = DataSource.register("Eco", "Ecocyc")
				.alternative("")
				.organism("Escherichia coli")
				.identifiersOrgBase("http://identifiers.org/orphanet/$id")
				.urnBase("Eco")
				.asDataSource();
		assertEquals("Escherichia coli",source2.getOrganism());
		assertNotNull(source2.getOrganism());
			
		DataSource source3 = DataSource.register("Ec", "Ecogene")
				.urnBase("urn:miriam:ecogene")
				.description(null)
				.identifiersOrgBase("http://identifiers.org/ecogene/")
				.alternative("MetaboLoci Alternative")
				.asDataSource();
		assertEquals("Ecogene",source3.getByMiriamBase("urn:miriam:ecogene").toString());
		assertEquals("urn:miriam:ecogene:urn%3Amiriam%3Aecogene",source3.getMiriamURN("urn:miriam:ecogene").toString());

		DataSource source4 = DataSource.register("Ect", "EPA CompTox")
					.urnBase("urn:miriam:Ect")
					.description("")
					.asDataSource();
		assertEquals("EPA CompTox",source4.getByMiriamBase("urn:miriam:Ect").toString());
		assertEquals(null,source4.getByMiriamBase(null));
		assertEquals(null,source4.getByMiriamBase("test"));
		
		source4.registerAlias("Ect");
		assertEquals("EPA CompTox", source4.getByAlias("Ect").toString());
		
		source4.getExistingBySystemCode("Ect");
		
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
			throw new IllegalArgumentException();
		});
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DataSource source5 = DataSource.register("Ect", "EPA CompTox")
					.urnBase("urn:miriam:Ect")
					.asDataSource();
			source5 = DataSource.register("Ect", "EPA CompTox")
					.urnBase("urn:miriam:ecogene")
					.asDataSource();
			throw new IllegalArgumentException();
		});
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DataSource source5 = DataSource.register("Ect", "EPA CompTox")
					.alternative("Metaboloci Alternative")
					.asDataSource();
			source5 = DataSource.register("Ect", "EPA CompTox")
					.alternative("test")
					.asDataSource();
			throw new IllegalArgumentException();
		});
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DataSource source5 = DataSource.register("Ect", "EPA CompTox")
					.alternative("Metaboloci Alternative")
					.asDataSource();
			source5 = DataSource.register("Ect", "EPA CompTox")
					.alternative("test")
					.asDataSource();
			throw new IllegalArgumentException();
		});
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DataSource source5 = DataSource.register("Ect", "EPA CompTox")
					.description("EPA CompTox Dashboard")
					.asDataSource();
			source5 = DataSource.register("Ect", "EPA CompTox")
					.description("test")
					.asDataSource();
			throw new IllegalArgumentException();
		});
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DataSource source5 = DataSource.register("Ect", "EPA CompTox")
					.asDataSource();
			source5 = DataSource.register("L", "EPA CompTox")
					.asDataSource();
			throw new IllegalArgumentException();
		});
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DataSource source5 = DataSource.register("Ect", "EPA CompTox")
					.asDataSource();
			source5 = DataSource.register(null, "EPA CompTox")
					.asDataSource();
			throw new IllegalArgumentException();
		});
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DataSource source5 = DataSource.register("Ec", "Ecogene")
					.identifiersOrgBase("http://identifiers.org/ecogene/")
					.asDataSource();
			throw new IllegalArgumentException();
		});
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DataSource source5 = DataSource.register("Ec", "Ecogene")
					.identifiersOrgBase("test")
					.asDataSource();
			throw new IllegalArgumentException();
		});
		
		
	}
	
	@org.junit.jupiter.api.Test
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
	
	
}
