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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains regular expression patterns for identifiers
 * Can be used to guess the DataSource of an identifier
 * of unknown origin.
 */
public class DataSourcePatterns 
{
	private static Map<DataSource, Pattern> patterns;
	/* 
	 * Make patterns of regular expressions for matching 
	 * the gene identifiers with specific gene databases.
	 * (see, 
	 * http://www.childrens-mercy.org/stats/model/arrayDataManagement.htm ) 
	 */	
	static
	{
		//Not all patterns are tested yet.
		patterns = new HashMap<DataSource, Pattern>();
		patterns.put(
			DataSource.SGD, 
			Pattern.compile("S\\d{9}"));
		patterns.put(
			DataSource.FLYBASE, 
			Pattern.compile("FB//w{2}//d{7}"));		
		//genbank (http://www.ncbi.nlm.nih.gov/Sequin/acc.html)		
		patterns.put(
				DataSource.GENBANK, 
				Pattern.compile("(\\w\\d{5})|(\\w{2}\\d{6})|(\\w{3}\\d{5})")
		);
		//interpro
		patterns.put(
				DataSource.INTERPRO, 				
				Pattern.compile("IPR\\d{6}")
			);
		//entrez gene
		patterns.put(
				DataSource.ENTREZ_GENE, 
				Pattern.compile("\\d{3,4}")
		);

		//MGI
		patterns.put(
				DataSource.MGI, 
				Pattern.compile("MGI://d+")
		);

		//refseq
		patterns.put(
				DataSource.REFSEQ, 
				Pattern.compile("\\w{2}_\\d+")
		);

		//RGD
		patterns.put(
				DataSource.RGD, 
				Pattern.compile("RGD:\\d+")
		);

		//Swiss Prot (http://expasy.org/sprot/userman.html#AC_line)
		patterns.put(
				DataSource.UNIPROT, 
				Pattern.compile("([A-N,R-][0-9][A-Z][A-Z,0-9][A-Z,0-9][0-9])|([O,P,Q][0-9][A-Z,0-9][A-Z,0-9][A-Z,0-9][0-9])")
		);

		//gene ontology
		patterns.put(
				DataSource.GENE_ONTOLOGY, 
				Pattern.compile("GO:\\d+")
		);

		//unigene
		patterns.put(
				DataSource.UNIGENE, 
				Pattern.compile("\\w{2}\\.\\d+")
		);

		//Wormbase
		patterns.put(
				DataSource.WORMBASE, 
				Pattern.compile("WBGene\\d{8}")
		);

		//affymetrix
		patterns.put(
				DataSource.AFFY, 
				Pattern.compile(".+_at")
		);

		//Ensemble
		patterns.put(
				DataSource.ENSEMBL, 
				Pattern.compile("ENSG\\d{11}")
		);

		//EMBL		
		patterns.put(
				DataSource.EMBL,
				Pattern.compile("\\w{2}\\d{6}")
		);
		
		//HUGO
		//not yet found
		
		//OMIM (http://www.ncbi.nlm.nih.gov/Omim/omimfaq.html#numbering_system)		
		patterns.put(
				DataSource.OMIM,		
				Pattern.compile("\\d{6}(\\.\\d{4})?")
		);

		//PDB ( http://www.rcsb.org/robohelp_f/#search_database/query_results.htm )
		patterns.put(
				DataSource.PDB, 
				Pattern.compile("[0-9][a-z,0-9][a-z,0-9][a-z,0-9][a-z,0-9]")
		);

		//Pfam (http://pfam.sanger.ac.uk/help)
		patterns.put(
			DataSource.PFAM,		
			Pattern.compile("(PF\\d{5})|(PB\\d{6})")
		);

		//Zfin (http://zfin.org/zf_info/dbase/PAPERS/ZFIN_DataModel/sectioniv_1.html)
		patterns.put(
				DataSource.ZFIN, 
				Pattern.compile("ZDB.+")
		);
		
		patterns.put(
				DataSource.AGILENT,
				Pattern.compile("A_\\d+_.+")
		);

		patterns.put(
				DataSource.HMDB,
				Pattern.compile("HMDB\\d{5}")
		);

		patterns.put(
				DataSource.CAS,
				Pattern.compile("\\d+-\\d+-\\d+")
		);
		
		patterns.put(
				DataSource.ENZYME_CODE,
				Pattern.compile("(\\d+\\.){3}\\d+")
		);

		patterns.put(
				DataSource.CHEBI,
				Pattern.compile("CHEBI\\:\\d+")
		);

		patterns.put(
				DataSource.KEGG_COMPOUND,
				Pattern.compile("C\\d+")
		);

	}

	/**
	 * Convenience method. 
	 * Returns a set of patterns which matches the given id.
	 */
	public static Set<DataSource> getDataSourceMatches (String id)
	{
		Set<DataSource> result = new HashSet<DataSource>();
		for (DataSource ds : patterns.keySet())
		{
			Matcher m = patterns.get(ds).matcher(id);					
			if (m.matches()) result.add (ds);			
		}
		return result;
	}
	
	/**
	 * Return all known data patterns, mapped to
	 * their DataSource.
	 * For example, this map will contain:
	 *    DataSource.ENSEMBL -> Pattern.compile("ENSG\d+")
	 *    
	 * There is not guaranteed to be a Pattern for every
	 * DataSource constant.
	 */
	public static Map<DataSource, Pattern> getPatterns()
	{
		return patterns;
	}
}
