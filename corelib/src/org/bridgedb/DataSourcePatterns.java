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
			Pattern.compile("(C[RG]\\d{4,5}|FBgn\\d{7})")
		);		
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
				Pattern.compile("\\d+")
		);

		//MGI
		patterns.put(
				DataSource.MGI, 
				Pattern.compile("MGI:\\d+")
		);

		patterns.put (
				DataSource.RFAM,
				Pattern.compile ("RF\\d+")
		);
		patterns.put (
				DataSource.IPI,
				Pattern.compile ("IPI\\d+")
		);
		patterns.put (
				DataSource.UCSC,
				Pattern.compile ("uc\\d{3}[a-z]{3}\\.\\d")
		);
		patterns.put (
				DataSource.ILLUMINA,
				Pattern.compile ("ILMN_\\d+")
		);
		patterns.put (
				DataSource.MIRBASE,
				Pattern.compile ("MI\\d+")
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
				Pattern.compile("[A-Z][a-z][a-z]?\\.\\d+")
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
				DataSource.ENSEMBL_HUMAN, 
				Pattern.compile("ENSG\\d{11}")
		);
		patterns.put(
				DataSource.ENSEMBL_MOUSE, 
				Pattern.compile("ENSMUSG\\d{11}")
		);
		patterns.put(
				DataSource.ENSEMBL_RAT, 
				Pattern.compile("ENSRNOG\\d{11}")
		);
		patterns.put(
				DataSource.ENSEMBL_MOSQUITO, 
				Pattern.compile("AGAP\\d{6}")
		);
		patterns.put(
				DataSource.ENSEMBL_BSUBTILIS, 
				Pattern.compile("EBBACG\\d{11}")
		);
		patterns.put(
				DataSource.ENSEMBL_ECOLI, 
				Pattern.compile("EBESCG\\d{11}")
		);
		patterns.put(
				DataSource.ENSEMBL_CHICKEN, 
				Pattern.compile("ENSGALG\\d{11}")
		);
		patterns.put(
				DataSource.ENSEMBL_HORSE, 
				Pattern.compile("ENSECAG\\d{11}")
		);
		patterns.put(
				DataSource.ENSEMBL_XENOPUS, 
				Pattern.compile("ENSXETG\\d{11}")
		);
		patterns.put(
				DataSource.ENSEMBL_CHIMP, 
				Pattern.compile("ENSPTRG\\d{11}")
		);
		patterns.put(
				DataSource.ENSEMBL_COW, 
				Pattern.compile("ENSBTAG\\d{11}")
		);
		patterns.put(
				DataSource.ENSEMBL_DOG, 
				Pattern.compile("ENSCAFG\\d{11}")
		);
		patterns.put(
				DataSource.ENSEMBL_ZEBRAFISH, 
				Pattern.compile("ENSDARG\\d{11}")
		);
		patterns.put(
				DataSource.ENSEMBL_FRUITFLY, 
				Pattern.compile("FBgn\\d{7}")
		);
		patterns.put(
				DataSource.TAIR,
				Pattern.compile("AT[\\dCM]G\\d{5}")
				);
		patterns.put(
				DataSource.GRAMENE_ARABIDOPSIS,
				Pattern.compile("AT[\\dCM]G\\d{5}\\-TAIR\\-G")
		);
		patterns.put(
				DataSource.IRGSP_GENE,
				Pattern.compile("Os\\d{2}g\\d+")
		);
		patterns.put(
				DataSource.GRAMENE_GENES_DB,
				Pattern.compile("GR:\\d+")
		);
		patterns.put(
				DataSource.BIOGRID,
				Pattern.compile("\\d+")
		);
		patterns.put(
				DataSource.NASC_GENE,
				Pattern.compile("AT[\\dCM]G\\d{5}\\-TAIR\\-G")
		);
		patterns.put(
				DataSource.PLANTGDB,
				Pattern.compile("PUT-[\\w\\d-]+")
		);
		//EMBL		
		patterns.put(
				DataSource.EMBL,
				Pattern.compile("\\w{2}\\d{6}")
		);
		
		//HUGO
		patterns.put(
				DataSource.HUGO,
				Pattern.compile("\\d+")
		);
		
		//OMIM (http://www.ncbi.nlm.nih.gov/Omim/omimfaq.html#numbering_system)		
		patterns.put(
				DataSource.OMIM,		
				Pattern.compile("\\d{6}(\\.\\d{4})?")
		);

		//PDB ( http://www.rcsb.org/robohelp_f/#search_database/query_results.htm )
		patterns.put(
				DataSource.PDB, 
				Pattern.compile("\\d[A-Z\\d]{3}")
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
