// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
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
import java.util.List;
import java.util.Set;

import org.bridgedb.DataDerby;
import org.bridgedb.DataException;
import org.bridgedb.DataSource;
import org.bridgedb.DataSourcePatterns;
import org.bridgedb.SimpleGdb;
import org.bridgedb.SimpleGdbFactory;
import org.bridgedb.Xref;
import org.bridgedb.XrefWithSymbol;

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
	
	public void testGdbConnect() throws DataException
	{
		assertTrue (new File (GDB_HUMAN).exists()); // if gdb can't be found, rest of test doesn't make sense. 
		SimpleGdb gdb = SimpleGdbFactory.createInstance (GDB_HUMAN, new DataDerby(), 0);
		
		//symbol must be INSR
		Xref ref = new Xref ("3643", BioDataSource.ENTREZ_GENE);
		//TODO: CD220 is just one possible symbol, not the primary one.
		assertEquals ("CD220", gdb.getGeneSymbol(ref));
		
		// test getting backpage
		assertTrue (gdb.getBpInfo(ref).startsWith("<TABLE border = 1><TR><TH>Gene ID:<TH>3643<TR>"));
		
		// get all crossrefs
		List<Xref> crossRefs1 = gdb.getCrossRefs(ref);
		assertTrue(crossRefs1.contains(new Xref("Hs.465744", BioDataSource.UNIGENE)));
		assertTrue(crossRefs1.contains(new Xref("NM_000208", BioDataSource.REFSEQ)));
		assertTrue(crossRefs1.contains(new Xref("P06213", BioDataSource.UNIPROT)));
		assertTrue(crossRefs1.size() > 10);
		
		// get specific crossrefs for specific database
		List<Xref> crossRefs2 = gdb.getCrossRefs(ref, BioDataSource.AFFY);		
		assertTrue(crossRefs2.contains(new Xref("1572_s_at", BioDataSource.AFFY)));
		assertTrue(crossRefs2.contains(new Xref("207851_s_at", BioDataSource.AFFY)));
		assertTrue(crossRefs2.contains(new Xref("213792_s_at", BioDataSource.AFFY)));
		assertTrue(crossRefs1.size() > crossRefs2.size());

		// get crossrefs by attribute
		List<Xref> crossRefs3 = gdb.getCrossRefsByAttribute("Symbol", "INSR");
		assertTrue(crossRefs3.contains(ref));

		// check symbol suggestions
		List<String> symbols1 = gdb.getSymbolSuggestions("INS", 100);
		assertTrue (symbols1.contains("INSR"));

		// check id suggestions
		List<Xref> crossRefs4 = gdb.getIdSuggestions("207851_s_", 100);
		assertTrue (crossRefs4.contains(new Xref("207851_s_at", BioDataSource.AFFY)));
		
		// check free search
		List<XrefWithSymbol> result5 = gdb.freeSearch ("Insulin", 100); 
		
		Xref nonExistingRef = new Xref ("bla", BioDataSource.OTHER); 
		assertNull (gdb.getGeneSymbol(nonExistingRef));
		
		// should return empty list, not NULL
		assertEquals (0, gdb.getCrossRefs(nonExistingRef).size());
		assertEquals (0, gdb.getCrossRefs(nonExistingRef, BioDataSource.AFFY).size());		
		
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
