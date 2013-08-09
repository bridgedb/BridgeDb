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
package org.bridgedb.bio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.DataSourcePatterns;
import org.bridgedb.Xref;
import org.junit.Assert;
import org.junit.Before;

public class Test
{
	boolean eventReceived = false;

	@Before
	public void setUp()
	{
		// cause static initializer to run.
		BioDataSource.init();
	}

	@org.junit.Test
	public void testInit()
	{
		for (DataSource ds : DataSource.getDataSources())
		{
			assertNotNull(ds);
			assertNotNull(ds.getFullName());
			// test for all but a test case in testFromUrn()
			if (!"blahblahblah".equals(ds.getFullName())) {
				assertNotNull(
					"Unexpected null system code for " + ds.getFullName(),
					ds.getSystemCode()
				);
			}
		}
	}
	
	@org.junit.Test
	public void testURN()
	{
		Xref ref = new Xref ("3643", BioDataSource.ENTREZ_GENE);
		Xref ref2 = new Xref ("GO:00001", BioDataSource.GENE_ONTOLOGY);
		assertEquals ("urn:miriam:ncbigene:3643", ref.getURN());
		assertEquals ("urn:miriam:obo.go:GO%3A00001", ref2.getURN());
		
	}

	@org.junit.Test
	public void testSpeciesSpecificEnsembl()
	{
		assertEquals (BioDataSource.ENSEMBL_COW, BioDataSource.getSpeciesSpecificEnsembl(Organism.BosTaurus));
		assertEquals (BioDataSource.ENSEMBL_MOSQUITO, BioDataSource.getSpeciesSpecificEnsembl(Organism.AnophelesGambiae));
	}

	@org.junit.Test
	public void testBioDataSources()
	{
		assertEquals (BioDataSource.WORMBASE.getOrganism(), Organism.CaenorhabditisElegans);
		assertEquals (BioDataSource.ENSEMBL_CHICKEN.getOrganism(), Organism.GallusGallus);
		assertEquals (BioDataSource.CAS.getType(), "metabolite");
	}
	
	@org.junit.Test
	public void testPatterns()
	{
		assertTrue (DataSourcePatterns.getDataSourceMatches("1.1.1.1").contains(BioDataSource.ENZYME_CODE));
		assertTrue (DataSourcePatterns.getDataSourceMatches("50-99-7").contains(BioDataSource.CAS));
		assertTrue (DataSourcePatterns.getDataSourceMatches("HMDB00122").contains(BioDataSource.HMDB));
		assertTrue (DataSourcePatterns.getDataSourceMatches("C00031").contains(BioDataSource.KEGG_COMPOUND));
		assertTrue (DataSourcePatterns.getDataSourceMatches("CHEBI:17925").contains(BioDataSource.CHEBI));
	}

	@org.junit.Test
	public void testBasCASNumbers()
	{
		assertFalse(DataSourcePatterns.getDataSourceMatches("50-99-77").contains(BioDataSource.CAS));
		assertFalse(DataSourcePatterns.getDataSourceMatches("1-99-77").contains(BioDataSource.CAS));
		assertFalse(DataSourcePatterns.getDataSourceMatches("50-1-7").contains(BioDataSource.CAS));
		assertFalse(DataSourcePatterns.getDataSourceMatches("50-333-7").contains(BioDataSource.CAS));
	}

	@org.junit.Test
	public void testDataSource()
	{
		DataSource ds = BioDataSource.ENSEMBL;
		assertEquals (ds.getFullName(), "Ensembl");
		assertEquals (ds.getSystemCode(), "En");
				
		DataSource ds4 = DataSource.getBySystemCode ("En");
		assertEquals (ds, ds4);
		
		DataSource ds5 = DataSource.getByFullName ("Entrez Gene");
		assertEquals (ds5, BioDataSource.ENTREZ_GENE);
	}

	@org.junit.Test
	public void testDataSourceFilter ()
	{
		// ensembl is primary, affy isn't
		Set<DataSource> f1 = DataSource.getFilteredSet(true, null, null);
		assertTrue (f1.contains(BioDataSource.ENSEMBL_HUMAN));
		assertTrue (f1.contains(BioDataSource.HMDB));
		assertFalse (f1.contains(BioDataSource.AFFY));

		// wormbase is specific for Ce.
		Set<DataSource> f2 = DataSource.getFilteredSet(null, null, Organism.CaenorhabditisElegans);
		assertTrue (f2.contains(BioDataSource.ENSEMBL_CELEGANS));
		assertTrue (f2.contains(BioDataSource.WORMBASE));
		assertFalse (f2.contains(BioDataSource.ZFIN));

		// metabolites
		Set<DataSource> f3 = DataSource.getFilteredSet(null, true, null);
		assertTrue (f3.contains(BioDataSource.HMDB));
		assertFalse (f3.contains(BioDataSource.WORMBASE));
		assertFalse (f3.contains(BioDataSource.ENSEMBL_HUMAN));

		// non-metabolites
		Set<DataSource> f4 = DataSource.getFilteredSet(null, false, null);
		assertTrue (f4.contains(BioDataSource.ENSEMBL_HUMAN));
		assertTrue (f4.contains(BioDataSource.WORMBASE));
		assertFalse (f4.contains(BioDataSource.HMDB));
	}
	
	@org.junit.Test
	public void testAlias()
	{
		DataSource ds = DataSource.getByAlias("ensembl_gene_id");
		assertSame(ds, BioDataSource.ENSEMBL);
	}

	@org.junit.Test
	public void testFromUrn()
	{
		Xref ref = Xref.fromUrn("urn:miriam:ncbigene:3643");
		assertEquals (BioDataSource.ENTREZ_GENE, ref.getDataSource());
		assertEquals ("3643", ref.getId());

		ref = Xref.fromUrn("urn:miriam:blahblahblah:abc");
		assertEquals (DataSource.getByFullName("blahblahblah"), ref.getDataSource());

		ref = Xref.fromUrn("blahblahblha");
		assertNull (ref);
		
		ref = Xref.fromUrn("urn:miriam:obo.go:GO%3A00001234");
		assertEquals (BioDataSource.GENE_ONTOLOGY, ref.getDataSource());
		assertEquals ("GO:00001234", ref.getId());
	}

	@org.junit.Test
	public void testUniqueSystemCodes() {
		BioDataSource.init();
		Set<String> codes = new HashSet<String>();
		Set<DataSource> sources = DataSource.getDataSources();
		Assert.assertNotSame(0, sources.size());
		for (DataSource source : sources) {
			codes.add(source.getSystemCode());
		}
		Assert.assertEquals(sources.size(), codes.size());
	}
}
