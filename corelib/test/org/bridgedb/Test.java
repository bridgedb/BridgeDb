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
package org.bridgedb;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.bridgedb.DataDerby;
import org.bridgedb.DataException;
import org.bridgedb.DataSource;
import org.bridgedb.DataSourcePatterns;
import org.bridgedb.SimpleGdb;
import org.bridgedb.SimpleGdbFactory;
import org.bridgedb.Xref;
import org.bridgedb.XrefWithSymbol;

public class Test extends TestCase 
{
	//TODO
	static final String GDB_HUMAN = 
		System.getProperty ("user.home") + File.separator + 
		"PathVisio-Data/gene databases/Hs_Derby_20080102.pgdb";
	static final String GDB_RAT = 
		System.getProperty ("user.home") + File.separator + 
		"PathVisio-Data/gene databases/Rn_Derby_20080102.pgdb";

	boolean eventReceived = false;
	
	public void testGdbConnect() throws DataException
	{
		assertTrue (new File (GDB_HUMAN).exists()); // if gdb can't be found, rest of test doesn't make sense. 
		SimpleGdb gdb = SimpleGdbFactory.createInstance (GDB_HUMAN, new DataDerby(), 0);
		
		//symbol must be INSR
		Xref ref = new Xref ("3643", DataSource.ENTREZ_GENE);				
		assertEquals ("INSR", gdb.getGeneSymbol(ref));
		
		// test getting backpage
		assertTrue (gdb.getBpInfo(ref).startsWith("<TABLE border = 1><TR><TH>Gene ID:<TH>3643<TR>"));
		
		// get all crossrefs
		List<Xref> crossRefs1 = gdb.getCrossRefs(ref);
		assertTrue(crossRefs1.contains(new Xref("Hs.465744", DataSource.UNIGENE)));
		assertTrue(crossRefs1.contains(new Xref("NM_000208", DataSource.REFSEQ)));
		assertTrue(crossRefs1.contains(new Xref("P06213", DataSource.UNIPROT)));
		assertTrue(crossRefs1.size() > 10);
		
		// get specific crossrefs for specific database
		List<Xref> crossRefs2 = gdb.getCrossRefs(ref, DataSource.AFFY);		
		assertTrue(crossRefs2.contains(new Xref("1572_s_at", DataSource.AFFY)));
		assertTrue(crossRefs2.contains(new Xref("207851_s_at", DataSource.AFFY)));
		assertTrue(crossRefs2.contains(new Xref("213792_s_at", DataSource.AFFY)));
		assertTrue(crossRefs1.size() > crossRefs2.size());

		// get crossrefs by attribute
		List<Xref> crossRefs3 = gdb.getCrossRefsByAttribute("Symbol", "INSR");
		assertTrue(crossRefs3.contains(ref));

		// check symbol suggestions
		List<String> symbols1 = gdb.getSymbolSuggestions("INS", 100);
		assertTrue (symbols1.contains("INSR"));

		// check id suggestions
		List<Xref> crossRefs4 = gdb.getIdSuggestions("207851_s_", 100);
		assertTrue (crossRefs4.contains(new Xref("207851_s_at", DataSource.AFFY)));
		
		// check free search
		List<XrefWithSymbol> result5 = gdb.freeSearch ("Insulin", 100); 
		
		Xref nonExistingRef = new Xref ("bla", DataSource.OTHER); 
		assertNull (gdb.getGeneSymbol(nonExistingRef));
		
		// should return empty list, not NULL
		assertEquals (0, gdb.getCrossRefs(nonExistingRef).size());
		assertEquals (0, gdb.getCrossRefs(nonExistingRef, DataSource.AFFY).size());		
		
		gdb.close();
	}
	
	public void testPatterns()
	{
		assertTrue (DataSourcePatterns.getDataSourceMatches("1.1.1.1").contains(DataSource.ENZYME_CODE));
		assertTrue (DataSourcePatterns.getDataSourceMatches("50-99-7").contains(DataSource.CAS));
		assertTrue (DataSourcePatterns.getDataSourceMatches("HMDB00122").contains(DataSource.HMDB));
		assertTrue (DataSourcePatterns.getDataSourceMatches("C00031").contains(DataSource.KEGG_COMPOUND));
		assertTrue (DataSourcePatterns.getDataSourceMatches("CHEBI:17925").contains(DataSource.CHEBI));
	}

}
