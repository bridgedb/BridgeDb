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
package org.bridgedb.benchmarking;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.rest.Server;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;


public class ComparisonScript 
{	
	public class SingleTest
	{
		public Map<Xref, Set<Xref>> result;
		long deltaConnect, deltaMapping;

		int consensus = 0;
		int success = 0;
		int total = 0;
		
		IDMapper mapper;
		
		final Connector connector;
		final DataSource alt;
		final DataSource [] dest;

		public SingleTest(Connector connector, DataSource alt, DataSource... dest)
		{
			this.alt = alt;
			this.dest = dest;
			this.connector = connector;
		}
		
		public void doTest(List<Xref> _refs) throws IDMapperException
		{
			List<Xref> refs = new ArrayList<Xref>();
			for (Xref ref : _refs)
			{
				refs.add (new Xref (ref.getId(), alt));
			}
			long start, end;
			start = System.currentTimeMillis();
			mapper = BridgeDb.connect(connector.connectString);
			end = System.currentTimeMillis();
			deltaConnect = end-start;
			
//			Set<DataSource> tgts = mapper.getCapabilities().getSupportedTgtDataSources();
//			for (DataSource ds : tgts)
//			{
//				System.out.println (ds.getFullName() + "[" + ds.getSystemCode() + "]");
//			}

			start = System.currentTimeMillis();
			result = mapper.mapID(refs, dest);
			end = System.currentTimeMillis();
			deltaMapping = end-start;
//			System.out.println ("Startup: " + deltaConnect);

//			for (Xref src : result.keySet())
//			{
//				System.out.println (src);
//				for (Xref dest : result.get(src))
//				{
//					System.out.println("  " + dest);
//				}
//			}
			
			total = refs.size();
			for (Xref key : result.keySet())
				if (result.get(key).size() > 0) success++;
		}
		
		public void filterResult (DataSource base, DataSource expected)
		{
			Map<Xref, Set<Xref>> newResult = new HashMap<Xref, Set<Xref>>();
			for (Xref key : result.keySet())
			{
				newResult.put (Utils.translateDs(key, base), Utils.translateDs (result.get(key), expected));
			}
			result = newResult;
		}

	}

	private static class TestSet
	{
//		private Set<Xref> refs = new HashSet<Xref>();
		public Map<Xref, Set<Xref>> consensus;
		private List<SingleTest> tests = new ArrayList<SingleTest>();
		private final DataSource base;
		private final DataSource expected;
		
		List<Xref> refs;
		
		TestSet (DataSource base, DataSource expected)
		{
			this.base = base;
			this.expected = expected;
		}

		public void mergeTestResults()
		{
			consensus = new HashMap<Xref, Set<Xref>>();
			for (Xref ref : refs)
			{
				Multiset<Set<Xref>> frq = new HashMultiset<Set<Xref>>();
				
				for (SingleTest test : tests)
				{
					frq.add (test.result.get(ref));
				}

				Set<Xref> maxVal = null;
				int max = 0;
				
				for (Set<Xref> key : frq)
				{
					if (frq.count(key) > max)
					{
						maxVal = key;
						max = frq.count(key);
					}
				}
				
				consensus.put (ref, maxVal);
				
				// count amount of consensus per test
				for (SingleTest test : tests)
				{
					if (Utils.safeEquals (consensus.get(ref), test.result.get(ref)))
					{
						test.consensus++;
					}
				}

			}
		}
		
		public void runTests() throws IDMapperException
		{
			for (SingleTest single : tests)
			{
				try
				{
					single.doTest(refs);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				single.filterResult (base, expected);
			}
			
		}
		
		public void reportMerged()
		{
			System.out.print ("service:");
			for (SingleTest single : tests)
			{
				System.out.print ("\t" + single.connector.name);
			}
			System.out.println("\tconsensus");

			System.out.print ("connecting (msec):");
			for (SingleTest single : tests)
			{
				System.out.print ("\t" + single.deltaConnect);
			}
			System.out.println();

			System.out.print ("mapping (msec):");
			for (SingleTest single : tests)
			{
				System.out.print ("\t" + single.deltaMapping);
			}
			System.out.println();

			System.out.print ("success%:");
			for (SingleTest single : tests)
			{
				System.out.printf ("\t%2d %3.1f%%", single.success, (double)single.success / (double)single.total * 100.0);
			}
			System.out.println();

			System.out.print ("consensus:");
			for (SingleTest single : tests)
			{
				System.out.printf ("\t%2d %3.1f%%", single.consensus, (double)single.consensus / (double)single.total * 100.0);
			}
			System.out.println();

			for (Xref ref : refs)
			{
				System.out.print (ref);
				for (SingleTest single : tests)
				{
					System.out.print ("\t");
					Utils.printRefSet(single.result.get(ref));
				}
				
				System.out.print ("\t");
				Utils.printRefSet(consensus.get(ref));
				
				System.out.println();
			}
		}

		public void readList(File f)
		{
			refs = new ArrayList<Xref>();
//			int count = 0;
			try {
				BufferedReader in = new BufferedReader(new FileReader(f));
				String line;
				in.readLine(); // skip header line
				while ((line = in.readLine()) != null)
				{
					refs.add (new Xref (line, base));
//					count++;
//					if (count > 10) break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

//	private void initRefs() throws IOException
//	{
//		File f = new File ("randomselection.txt");
//		BufferedReader br = new BufferedReader(new FileReader(f));
//		String line;
//		while ((line = br.readLine()) != null)
//		{
//			String[] fields = line.split("\t");
//			Xref ref = new Xref (fields[0], DataSource.getBySystemCode(fields[1]));
//			refs.add (ref);
//		}
//	}

	enum Connector
	{
		DERBY_LOCAL("pgdb","idmapper-pgdb:/home/martijn/PathVisio-Data/gene databases/Hs_Derby_20090509.pgdb"),
		DERBY_REMOTE("pgdbclient","idmapper-derbyclient:Homo sapiens"),
		
		// PICR can only be used to map proteins
		PICR ("picr", "idmapper-picr:"),
		
		SYNERGIZER_ENSEMBL  ("synergizer(ensembl)", "idmapper-synergizer:authority=ensembl&species=Homo sapiens"),
		BRIDGEWEBSERVICE ("BridgeWebservice", "idmapper-bridgerest:http://webservice.bridgedb.org/Human"),
		BRIDGEWEBSERVICE_LOCAL ("BridgeWebservice(local)", "idmapper-bridgerest:http://localhost:8183/Human"),
		BIOMART ("biomart", "idmapper-biomart:http://www.biomart.org/biomart/martservice?mart=ensembl&dataset=hsapiens_gene_ensembl"),
		CRONOS ("cronos", "idmapper-cronos:hsa"),
		SYNERGIZER_NCBI ("synergizer(ncbi)", "idmapper-synergizer:authority=ncbi&species=Homo sapiens"),
		;

		Connector (String name, String connectString)
		{
			this.name = name;
			this.connectString = connectString;
		}
		
		final String name;
		final String connectString;
	}
	
	
	public void run() throws IOException, ClassNotFoundException, IDMapperException
	{
		List<TestSet> allTests = new ArrayList<TestSet>();
		BioDataSource.init();
		
		// run local BridgeRest service
		Server server = new Server();
		server.run(8183, new File("/home/martijn/prg/bridgedb/webservice/gdb.config"));
		
		Class.forName ("org.bridgedb.webservice.bridgerest.BridgeRest");
		Class.forName ("org.bridgedb.webservice.biomart.IDMapperBiomart");
		Class.forName ("org.bridgedb.rdb.IDMapperRdb");
		Class.forName ("org.bridgedb.webservice.picr.IDMapperPicr");
		Class.forName ("org.bridgedb.webservice.cronos.IDMapperCronos");
		Class.forName ("org.bridgedb.webservice.synergizer.IDMapperSynergizer");
		
//		initRefs();
//		List<Xref> affylist_syn = readList (new File("IDMapping_X_Test.txt"), );

		TestSet ensembl_hgnc = new TestSet(BioDataSource.ENSEMBL, BioDataSource.HUGO);
		ensembl_hgnc.readList(new File("IDMapping_En_Test.txt"));
		ensembl_hgnc.tests.add (new SingleTest(Connector.DERBY_LOCAL, BioDataSource.ENSEMBL_HUMAN, BioDataSource.HUGO)); 
		
		// TODO: Synergizer ensembl produces the symbol of hgnc, not the hgnc identifiers 
//		ensembl_hgnc.tests.add (new SingleTest(Connector.SYNERGIZER_ENSEMBL, DataSource.getByFullName("ensembl_gene_id"),
//				DataSource.getByFullName("hgnc_curated_transcript_name"),
//				DataSource.getByFullName("hgnc_automatic_transcript_name"))); 
		ensembl_hgnc.tests.add (new SingleTest(Connector.SYNERGIZER_NCBI, DataSource.getByFullName("ensembl"),
				DataSource.getByFullName("hgnc"))); 
		ensembl_hgnc.tests.add (new SingleTest(Connector.BRIDGEWEBSERVICE, BioDataSource.ENSEMBL_HUMAN, BioDataSource.HUGO)); 
		ensembl_hgnc.tests.add (new SingleTest(Connector.BRIDGEWEBSERVICE_LOCAL, BioDataSource.ENSEMBL_HUMAN, BioDataSource.HUGO)); 
		ensembl_hgnc.tests.add (new SingleTest(Connector.CRONOS, BioDataSource.ENSEMBL_HUMAN, BioDataSource.HUGO)); 


		TestSet ensembl_entrez = new TestSet(BioDataSource.ENSEMBL, BioDataSource.ENTREZ_GENE);
		ensembl_entrez.readList(new File("IDMapping_En_Test.txt"));
		ensembl_entrez.tests.add (new SingleTest(Connector.DERBY_LOCAL, BioDataSource.ENSEMBL_HUMAN, BioDataSource.ENTREZ_GENE)); 
		ensembl_entrez.tests.add (new SingleTest(Connector.SYNERGIZER_ENSEMBL, DataSource.getByFullName("ensembl_gene_id"),
				DataSource.getByFullName("entrezgene")));
		ensembl_entrez.tests.add (new SingleTest(Connector.SYNERGIZER_NCBI, DataSource.getByFullName("ensembl"),
				DataSource.getByFullName("entrezgene"))); 
		ensembl_entrez.tests.add (new SingleTest(Connector.BRIDGEWEBSERVICE, BioDataSource.ENSEMBL_HUMAN, BioDataSource.ENTREZ_GENE)); 
		ensembl_entrez.tests.add (new SingleTest(Connector.BRIDGEWEBSERVICE_LOCAL, BioDataSource.ENSEMBL_HUMAN, BioDataSource.ENTREZ_GENE)); 
		ensembl_entrez.tests.add (new SingleTest(Connector.BIOMART, DataSource.getByFullName("ensembl_gene_id"),
				DataSource.getByFullName("entrezgene")));
		ensembl_entrez.tests.add (new SingleTest(Connector.CRONOS, 
				BioDataSource.ENSEMBL_HUMAN, BioDataSource.ENTREZ_GENE));
	
		TestSet affy = new TestSet(BioDataSource.AFFY, BioDataSource.ENSEMBL);
		affy.readList (new File("IDMapping_X_Test.txt"));
		affy.tests.add (new SingleTest(Connector.DERBY_LOCAL, BioDataSource.AFFY, BioDataSource.ENSEMBL_HUMAN));
		affy.tests.add (new SingleTest(Connector.SYNERGIZER_ENSEMBL, DataSource.getByFullName("affy_hg_u133a"),
				DataSource.getByFullName("ensembl_gene_id")));
		// affy mapping currently not available in BridgeWebservice
//		affy.tests.add (new SingleTest(Connector.BRIDGEWEBSERVICE, BioDataSource.AFFY, BioDataSource.ENSEMBL_HUMAN));
		affy.tests.add (new SingleTest(Connector.BRIDGEWEBSERVICE_LOCAL, BioDataSource.AFFY, BioDataSource.ENSEMBL_HUMAN));
		affy.tests.add (new SingleTest(Connector.CRONOS, BioDataSource.AFFY, BioDataSource.ENSEMBL_HUMAN)); 
		
		TestSet bridgedb_only = new TestSet(BioDataSource.ENSEMBL, BioDataSource.ENTREZ_GENE);
		bridgedb_only.readList(new File("IDMapping_En_Test.txt"));
		bridgedb_only.tests.add (new SingleTest(Connector.DERBY_LOCAL, BioDataSource.ENSEMBL_HUMAN, BioDataSource.ENTREZ_GENE)); 
		bridgedb_only.tests.add (new SingleTest(Connector.DERBY_REMOTE, BioDataSource.ENSEMBL_HUMAN, BioDataSource.ENTREZ_GENE)); 
		bridgedb_only.tests.add (new SingleTest(Connector.BRIDGEWEBSERVICE, BioDataSource.ENSEMBL_HUMAN, BioDataSource.ENTREZ_GENE)); 
		bridgedb_only.tests.add (new SingleTest(Connector.BRIDGEWEBSERVICE_LOCAL, BioDataSource.ENSEMBL_HUMAN, BioDataSource.ENTREZ_GENE)); 

		// comment test that you want to skip.
		allTests.add (bridgedb_only);
//		allTests.add (ensembl_entrez);
//		allTests.add (affy);
//		allTests.add (ensembl_hgnc);
		
		for (TestSet set : allTests)
		{
			set.runTests();
			set.mergeTestResults();
			set.reportMerged();
		}
		
		server.stop();
		
	}
		
	public static void main(String [] args) throws IOException, ClassNotFoundException, IDMapperException
	{
		ComparisonScript test = new ComparisonScript();
		test.run();
	}
}
