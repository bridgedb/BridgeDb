// BridgeDb,
// An abstraction layer for identifer mapping services, both local and online.
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

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bridgedb.IDMapperException;
import org.bridgedb.DataSource;
import org.bridgedb.DataSourcePatterns;
import org.bridgedb.Xref;
import org.bridgedb.rdb.DataDerby;
import org.bridgedb.rdb.SimpleGdb;
import org.bridgedb.rdb.SimpleGdbFactory;

import junit.framework.TestCase;

public class Test extends TestCase 
{
	//TODO
	static final String GDB_HUMAN = 
		System.getProperty ("user.home") + File.separator + 
		"PathVisio-Data/gene databases/Hs_Derby_20081119.pgdb";
	static final String GDB_RAT = 
		System.getProperty ("user.home") + File.separator + 
		"PathVisio-Data/gene databases/Rn_Derby_20081119.pgdb";

	boolean eventReceived = false;

	public void setUp()
	{
		// cause static initializer to run.
		BioDataSource.init();
	}

	public void testInit()
	{
		for (DataSource ds : DataSource.getDataSources())
		{
			assertNotNull(ds);
			assertNotNull(ds.getFullName());
			assertNotNull(ds.getSystemCode());
		}
	}
	
	public void testURN()
	{
		Xref ref = new Xref ("3643", BioDataSource.ENTREZ_GENE);
		Xref ref2 = new Xref ("GO:00001", BioDataSource.GENE_ONTOLOGY);
		assertEquals ("urn:miriam:entrez.gene:3643", ref.getURN());
		assertEquals ("urn:miriam:obo.go:GO%3A00001", ref2.getURN());
		
	}

	public void testSpeciesSpecificEnsembl()
	{
		assertEquals (BioDataSource.ENSEMBL_COW, BioDataSource.getSpeciesSpecificEnsembl(Organism.BosTaurus));
		assertEquals (BioDataSource.ENSEMBL_MOSQUITO, BioDataSource.getSpeciesSpecificEnsembl(Organism.AnophelesGambiae));
	}

	public void testBioDataSources()
	{
		assertEquals (BioDataSource.WORMBASE.getOrganism(), Organism.CaenorhabditisElegans);
		assertEquals (BioDataSource.ENSEMBL_CHICKEN.getOrganism(), Organism.GallusGallus);
		assertEquals (BioDataSource.CAS.getType(), "metabolite");
	}
	
	public void testGdbConnect() throws IDMapperException
	{
		assertTrue (new File (GDB_HUMAN).exists()); // if gdb can't be found, rest of test doesn't make sense. 
		SimpleGdb gdb = SimpleGdbFactory.createInstance (GDB_HUMAN, new DataDerby(), 0);
		
		Set<DataSource> supported = gdb.getCapabilities().getSupportedSrcDataSources();
		assertTrue (supported.contains(BioDataSource.ENSEMBL));
		assertFalse (supported.contains(BioDataSource.WORMBASE));

		//symbol must be INSR
		Xref ref = new Xref ("3643", BioDataSource.ENTREZ_GENE);
		//TODO: CD220 is just one possible symbol, not the primary one.
		assertTrue (gdb.getAttributes(ref, "Symbol").contains("CD220"));
		
		// test getting description
		assertTrue (gdb.getAttributes(ref, "Description").iterator().next().startsWith("Insulin receptor Precursor"));
		
		// get all crossrefs
		Set<Xref> crossRefs1 = gdb.mapID(ref, null);
		assertTrue(crossRefs1.contains(new Xref("Hs.465744", BioDataSource.UNIGENE)));
		assertTrue(crossRefs1.contains(new Xref("NM_000208", BioDataSource.REFSEQ)));
		assertTrue(crossRefs1.contains(new Xref("P06213", BioDataSource.UNIPROT)));
		assertTrue(crossRefs1.size() > 10);
		
		// get specific crossrefs for specific database
		Set<Xref> crossRefs2 = gdb.mapID(ref, BioDataSource.AFFY);		
		assertTrue(crossRefs2.contains(new Xref("1572_s_at", BioDataSource.AFFY)));
		assertTrue(crossRefs2.contains(new Xref("207851_s_at", BioDataSource.AFFY)));
		assertTrue(crossRefs2.contains(new Xref("213792_s_at", BioDataSource.AFFY)));
		assertTrue(crossRefs1.size() > crossRefs2.size());

		// get crossrefs by attribute
		Map<Xref, String> crossRefs3 = gdb.freeAttributeSearch("INSR", "Symbol", 100);
		assertTrue(crossRefs3.containsKey(ref));

		// check symbol suggestions
		Map<Xref, String> attrSearchResult = 
			gdb.freeAttributeSearch("INS", "Symbol", 100);
		assertTrue (attrSearchResult.containsValue("INSR"));

		// check id suggestions
		Set<Xref> crossRefs4 = gdb.freeSearch("207851_s_", 100);
		assertTrue (crossRefs4.contains(new Xref("207851_s_at", BioDataSource.AFFY)));
		
		// check free search
		Map<Xref, String> result5 = gdb.freeAttributeSearch("Insulin", "Symbol", 100); 
		
		Xref nonExistingRef = new Xref ("bla", BioDataSource.OTHER); 
		assertEquals(0, gdb.getAttributes(nonExistingRef, "Symbol").size());
		
		// should return empty list, not NULL
		assertEquals (0, gdb.mapID(nonExistingRef).size());
		assertEquals (0, gdb.mapID(nonExistingRef, BioDataSource.AFFY).size());		
		
		gdb.close();
	}
	
	public void testPatterns()
	{
		assertTrue (DataSourcePatterns.getDataSourceMatches("1.1.1.1").contains(BioDataSource.ENZYME_CODE));
		assertTrue (DataSourcePatterns.getDataSourceMatches("50-99-7").contains(BioDataSource.CAS));
		assertTrue (DataSourcePatterns.getDataSourceMatches("HMDB00122").contains(BioDataSource.HMDB));
		assertTrue (DataSourcePatterns.getDataSourceMatches("C00031").contains(BioDataSource.KEGG_COMPOUND));
		assertTrue (DataSourcePatterns.getDataSourceMatches("CHEBI:17925").contains(BioDataSource.CHEBI));
	}

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
	
}
