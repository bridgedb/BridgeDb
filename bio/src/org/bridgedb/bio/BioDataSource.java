package org.bridgedb.bio;

import java.util.regex.Pattern;

import org.bridgedb.DataSource;
import org.bridgedb.DataSourcePatterns;
import org.bridgedb.DataSource.PrefixUrlMaker;
import org.bridgedb.DataSource.UrlMaker;

public class BioDataSource 
{
	/* 
	 * Make patterns of regular expressions for matching 
	 * the gene identifiers with specific gene databases.
	 * (see, 
	 * http://www.childrens-mercy.org/stats/model/arrayDataManagement.htm ) 
	 */	

	static {
		DataSourcePatterns.registerPattern(
			BioDataSource.SGD, 
			Pattern.compile("S\\d{9}"));
		DataSourcePatterns.registerPattern(
			BioDataSource.FLYBASE, 
			Pattern.compile("(C[RG]\\d{4,5}|FBgn\\d{7})")
		);		
		//genbank (http://www.ncbi.nlm.nih.gov/Sequin/acc.html)		
		DataSourcePatterns.registerPattern(
				BioDataSource.GENBANK, 
				Pattern.compile("(\\w\\d{5})|(\\w{2}\\d{6})|(\\w{3}\\d{5})")
		);
		//interpro
		DataSourcePatterns.registerPattern(
				BioDataSource.INTERPRO, 				
				Pattern.compile("IPR\\d{6}")
			);
		//entrez gene
		DataSourcePatterns.registerPattern(
				BioDataSource.ENTREZ_GENE, 
				Pattern.compile("\\d+")
		);

		//MGI
		DataSourcePatterns.registerPattern(
				BioDataSource.MGI, 
				Pattern.compile("MGI:\\d+")
		);

		DataSourcePatterns.registerPattern (
				BioDataSource.RFAM,
				Pattern.compile ("RF\\d+")
		);
		DataSourcePatterns.registerPattern (
				BioDataSource.IPI,
				Pattern.compile ("IPI\\d+")
		);
		DataSourcePatterns.registerPattern (
				BioDataSource.UCSC,
				Pattern.compile ("uc\\d{3}[a-z]{3}\\.\\d")
		);
		DataSourcePatterns.registerPattern (
				BioDataSource.ILLUMINA,
				Pattern.compile ("ILMN_\\d+")
		);
		DataSourcePatterns.registerPattern (
				BioDataSource.MIRBASE,
				Pattern.compile ("MI\\d+")
		);
		//refseq
		DataSourcePatterns.registerPattern(
				BioDataSource.REFSEQ, 
				Pattern.compile("\\w{2}_\\d+")
		);

		//RGD
		DataSourcePatterns.registerPattern(
				BioDataSource.RGD, 
				Pattern.compile("RGD:\\d+")
		);

		//Swiss Prot (http://expasy.org/sprot/userman.html#AC_line)
		DataSourcePatterns.registerPattern(
				BioDataSource.UNIPROT, 
				Pattern.compile("([A-N,R-][0-9][A-Z][A-Z,0-9][A-Z,0-9][0-9])|([O,P,Q][0-9][A-Z,0-9][A-Z,0-9][A-Z,0-9][0-9])")
		);

		//gene ontology
		DataSourcePatterns.registerPattern(
				BioDataSource.GENE_ONTOLOGY, 
				Pattern.compile("GO:\\d+")
		);

		//unigene
		DataSourcePatterns.registerPattern(
				BioDataSource.UNIGENE, 
				Pattern.compile("[A-Z][a-z][a-z]?\\.\\d+")
		);

		//Wormbase
		DataSourcePatterns.registerPattern(
				BioDataSource.WORMBASE, 
				Pattern.compile("WBGene\\d{8}")
		);

		//affymetrix
		DataSourcePatterns.registerPattern(
				BioDataSource.AFFY, 
				Pattern.compile(".+_at")
		);

		//Ensemble
		DataSourcePatterns.registerPattern(
				BioDataSource.ENSEMBL_HUMAN, 
				Pattern.compile("ENSG\\d{11}")
		);
		DataSourcePatterns.registerPattern(
				BioDataSource.ENSEMBL_MOUSE, 
				Pattern.compile("ENSMUSG\\d{11}")
		);
		DataSourcePatterns.registerPattern(
				BioDataSource.ENSEMBL_RAT, 
				Pattern.compile("ENSRNOG\\d{11}")
		);
		DataSourcePatterns.registerPattern(
				BioDataSource.ENSEMBL_MOSQUITO, 
				Pattern.compile("AGAP\\d{6}")
		);
		DataSourcePatterns.registerPattern(
				BioDataSource.ENSEMBL_BSUBTILIS, 
				Pattern.compile("EBBACG\\d{11}")
		);
		DataSourcePatterns.registerPattern(
				BioDataSource.ENSEMBL_ECOLI, 
				Pattern.compile("EBESCG\\d{11}")
		);
		DataSourcePatterns.registerPattern(
				BioDataSource.ENSEMBL_CHICKEN, 
				Pattern.compile("ENSGALG\\d{11}")
		);
		DataSourcePatterns.registerPattern(
				BioDataSource.ENSEMBL_HORSE, 
				Pattern.compile("ENSECAG\\d{11}")
		);
		DataSourcePatterns.registerPattern(
				BioDataSource.ENSEMBL_XENOPUS, 
				Pattern.compile("ENSXETG\\d{11}")
		);
		DataSourcePatterns.registerPattern(
				BioDataSource.ENSEMBL_CHIMP, 
				Pattern.compile("ENSPTRG\\d{11}")
		);
		DataSourcePatterns.registerPattern(
				BioDataSource.ENSEMBL_COW, 
				Pattern.compile("ENSBTAG\\d{11}")
		);
		DataSourcePatterns.registerPattern(
				BioDataSource.ENSEMBL_DOG, 
				Pattern.compile("ENSCAFG\\d{11}")
		);
		DataSourcePatterns.registerPattern(
				BioDataSource.ENSEMBL_ZEBRAFISH, 
				Pattern.compile("ENSDARG\\d{11}")
		);
		DataSourcePatterns.registerPattern(
				BioDataSource.ENSEMBL_FRUITFLY, 
				Pattern.compile("FBgn\\d{7}")
		);
		DataSourcePatterns.registerPattern(
				BioDataSource.TAIR,
				Pattern.compile("AT[\\dCM]G\\d{5}")
				);
		DataSourcePatterns.registerPattern(
				BioDataSource.GRAMENE_ARABIDOPSIS,
				Pattern.compile("AT[\\dCM]G\\d{5}\\-TAIR\\-G")
		);
		DataSourcePatterns.registerPattern(
				BioDataSource.IRGSP_GENE,
				Pattern.compile("Os\\d{2}g\\d+")
		);
		DataSourcePatterns.registerPattern(
				BioDataSource.GRAMENE_GENES_DB,
				Pattern.compile("GR:\\d+")
		);
		DataSourcePatterns.registerPattern(
				BioDataSource.BIOGRID,
				Pattern.compile("\\d+")
		);
		DataSourcePatterns.registerPattern(
				BioDataSource.NASC_GENE,
				Pattern.compile("AT[\\dCM]G\\d{5}\\-TAIR\\-G")
		);
		DataSourcePatterns.registerPattern(
				BioDataSource.PLANTGDB,
				Pattern.compile("PUT-[\\w\\d-]+")
		);
		//EMBL		
		DataSourcePatterns.registerPattern(
				BioDataSource.EMBL,
				Pattern.compile("\\w{2}\\d{6}")
		);
		
		//HUGO
		DataSourcePatterns.registerPattern(
				BioDataSource.HUGO,
				Pattern.compile("\\d+")
		);
		
		//OMIM (http://www.ncbi.nlm.nih.gov/Omim/omimfaq.html#numbering_system)		
		DataSourcePatterns.registerPattern(
				BioDataSource.OMIM,		
				Pattern.compile("\\d{6}(\\.\\d{4})?")
		);

		//PDB ( http://www.rcsb.org/robohelp_f/#search_database/query_results.htm )
		DataSourcePatterns.registerPattern(
				BioDataSource.PDB, 
				Pattern.compile("\\d[A-Z\\d]{3}")
		);

		//Pfam (http://pfam.sanger.ac.uk/help)
		DataSourcePatterns.registerPattern(
			BioDataSource.PFAM,		
			Pattern.compile("(PF\\d{5})|(PB\\d{6})")
		);

		//Zfin (http://zfin.org/zf_info/dbase/PAPERS/ZFIN_DataModel/sectioniv_1.html)
		DataSourcePatterns.registerPattern(
				BioDataSource.ZFIN, 
				Pattern.compile("ZDB.+")
		);
		
		DataSourcePatterns.registerPattern(
				BioDataSource.AGILENT,
				Pattern.compile("A_\\d+_.+")
		);

		DataSourcePatterns.registerPattern(
				BioDataSource.HMDB,
				Pattern.compile("HMDB\\d{5}")
		);

		DataSourcePatterns.registerPattern(
				BioDataSource.CAS,
				Pattern.compile("\\d+-\\d+-\\d+")
		);
		
		DataSourcePatterns.registerPattern(
				BioDataSource.ENZYME_CODE,
				Pattern.compile("(\\d+\\.){3}\\d+")
		);

		DataSourcePatterns.registerPattern(
				BioDataSource.CHEBI,
				Pattern.compile("CHEBI\\:\\d+")
		);

		DataSourcePatterns.registerPattern(
				BioDataSource.KEGG_COMPOUND,
				Pattern.compile("C\\d+")
		);
	}
	
	public static final DataSource TAIR = DataSource.register (
			 "A", "TAIR",
			 null, "http://www.arabidopsis.org/",
			 "AT1G35255", true, false, Organism.ArabidopsisThaliana);
		public static final DataSource AGILENT = DataSource.register (
			"Ag", "Agilent", 
			null, null, 
			"A_24_P98555", false, false, null);
		public static final DataSource BIOGRID = DataSource.register (
			"Bg", "BioGrid", 
			null, "http://www.thebiogrid.org/", 
			null, false, false, null);
		public static final DataSource CINT = DataSource.register (
			"C", "Cint", 
			null, null, 
			null, false, false, null);
		public static final DataSource CCDS = DataSource.register (
			"Cc", "CCDS", 
			new PrefixUrlMaker ("http://www.ncbi.nlm.nih.gov/CCDS/CcdsBrowse.cgi?REQUEST=ALLFIELDS&DATA="), null,
			"CCDS43989", false, false, null);
		public static final DataSource CAS = DataSource.register (
			"Ca", "CAS", 
			new PrefixUrlMaker ("http://chem.sis.nlm.nih.gov/chemidplus/direct.jsp?regno="),
			null, null, true, true, null);
		public static final DataSource CHEBI = DataSource.register (
			"Ce", "ChEBI", 
			new PrefixUrlMaker ("http://www.ebi.ac.uk/chebi/searchId=CHEBI:"),
			null, null, true, true, null);
		public static final DataSource HMDB = DataSource.register (
			"Ch", "HMDB", 
			new PrefixUrlMaker ("http://www.hmdb.ca/metabolites/"),
			"http://www.hmdb.ca/", "HMDB00001", true, true, null); // NB: in spite of name, not Human specific!
		public static final DataSource KEGG_COMPOUND = DataSource.register (
			"Ck", "Kegg Compound", 
			new PrefixUrlMaker ("http://www.genome.jp/dbget-bin/www_bget?cpd:"),
			null, null, true, true, null);
		public static final DataSource PUBCHEM = DataSource.register (
			"Cp", "PubChem", 
			new PrefixUrlMaker ("http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid="),
			"http://pubchem.ncbi.nlm.nih.gov/", null, true, true, null);
		public static final DataSource CHEMSPIDER = DataSource.register (
			"Cs", "Chemspider", 
			new UrlMaker ()
			{
				@Override
				public String getUrl(String id) 
				{
					return "http://www.chemspider.com/Chemical-Structure." + id + ".html";
				}
			},			
			"http://www.chemspider.com/", null, true, true, null);		
		public static final DataSource SGD = DataSource.register (
			"D", "SGD", 
			new PrefixUrlMaker ("http://db.yeastgenome.org/cgi-bin/locus.pl?locus="),
			"http://www.yeastgenome.org/", "CUP1-2", true, false, Organism.SaccharomycesCerevisiae);
		public static final DataSource ENZYME_CODE = DataSource.register (
			"E", "EC Number",
			new PrefixUrlMaker ("http://www.brenda-enzymes.info/php/result_flat.php4?ecno="),
			"http://www.brenda-enzymes.info/"
			, "2.7.1.71", true, false, null);
		public static final DataSource ECOLI = DataSource.register (
			"Ec", "Ecoli", 
			null, null, null, true, false, Organism.EscherichiaColi);
		public static final DataSource EMBL = DataSource.register (
			"Em", "EMBL", 
			new PrefixUrlMaker ("http://www.ebi.ac.uk/cgi-bin/emblfetch?style=html&id="), 
			"http://www.ebi.ac.uk/embl", "AL030996", true, false, null);
		/** @deprecated use one of the organism-specific system codes instead */ 
		public static final DataSource ENSEMBL = DataSource.register (
			"En", "Ensembl", 
			new PrefixUrlMaker("http://www.ensembl.org/Homo_sapiens/Search/Summary?_q="), 
			"http://www.ensembl.org", 
			"ENSG00000139618", false, false, null);
		public static final DataSource ENSEMBL_MOSQUITO = DataSource.register (
			"EnAg", "Ensembl Mosquito", 
			new PrefixUrlMaker("http://www.ensembl.org/Anopheles_gambiae/Gene/Summary?_q="), 
			"http://www.ensembl.org", 
			"AGAP006864", true, false, Organism.AnophelesGambiae);
		public static final DataSource GRAMENE_ARABIDOPSIS = DataSource.register (
			"EnAt", "Gramene Arabidopsis", 
			new PrefixUrlMaker("http://www.gramene.org/Arabidopsis_thaliana/Gene/Summary?_q="), 
			"http://www.gramene.org/", 
			"ATMG01360-TAIR-G", true, false, Organism.ArabidopsisThaliana);
		public static final DataSource ENSEMBL_BSUBTILIS = DataSource.register (
			"EnBs", "Ensembl B. subtilis", 
			new PrefixUrlMaker("http://bacteria.ensembl.org/Bacillus/B_subtilis/Gene/Summary?g="), 
			"http://www.ensembl.org", 
			"EBBACG00000000013", true, false, Organism.BacillusSubtilis);
		public static final DataSource ENSEMBL_COW = DataSource.register (
			"EnBt", "Ensembl Cow", 
			new PrefixUrlMaker("http://www.ensembl.org/Bos_taurus/Gene/Summary?g="), 
			"http://www.ensembl.org", 
			"ENSBTAG00000043548", true, false, Organism.BosTaurus);
		public static final DataSource ENSEMBL_CELEGANS = DataSource.register (
			"EnCe", "Ensembl C. elegans", 
			new PrefixUrlMaker("http://www.ensembl.org/Caenorhabditis_elegans/Gene/Summary?g="), 
			"http://www.ensembl.org", 
			"Y42H9B.1", true, false, Organism.CaenorhabditisElegans);
		public static final DataSource ENSEMBL_DOG = DataSource.register (
			"EnCf", "Ensembl Dog", 
			new PrefixUrlMaker("http://www.ensembl.org/Canis_familiaris/Gene/Summary?g="), 
			"http://www.ensembl.org", 
			"ENSCAFG00000025860", true, false, Organism.CanisFamiliaris);
		public static final DataSource ENSEMBL_FRUITFLY = DataSource.register (
			"EnDm", "Ensembl Fruitfly", 
			new PrefixUrlMaker("http://www.ensembl.org/Drosophila_melanogaster/Gene/Summary?g="), 
			"http://www.ensembl.org", 
			"FBgn0032956", true, false, Organism.DrosophilaMelanogaster);
		public static final DataSource ENSEMBL_ZEBRAFISH = DataSource.register (
			"EnDr", "Ensembl Zebrafish", 
			new PrefixUrlMaker("http://www.ensembl.org/Danio_rerio/Gene/Summary?g="), 
			"http://www.ensembl.org", 
			"ENSDARG00000024771", true, false, Organism.DanioRerio);
		public static final DataSource ENSEMBL_ECOLI = DataSource.register (
			"EnEc", "Ensembl E. coli", 
			new PrefixUrlMaker("http://bacteria.ensembl.org/Escherichia_Shigella/E_coli_K12/Gene/Summary?g="), 
			"http://www.ensembl.org", 
			"EBESCG00000000010", true, false, Organism.EscherichiaColi);
		public static final DataSource ENSEMBL_CHICKEN = DataSource.register (
			"EnGg", "Ensembl Chicken", 
			new PrefixUrlMaker("http://www.ensembl.org/Gallus_gallus/Gene/Summary?g="), 
			"http://www.ensembl.org", 
			"ENSGALG00000021736", true, false, Organism.GallusGallus);
		public static final DataSource ENSEMBL_HUMAN = DataSource.register (
			"EnHs", "Ensembl Human", 
			new PrefixUrlMaker("http://www.ensembl.org/Homo_sapiens/Gene/Summary?g="), 
			"http://www.ensembl.org", 
			"ENSG00000139618", true, false, Organism.HomoSapiens);
		public static final DataSource ENSEMBL_MOUSE = DataSource.register (
			"EnMm", "Ensembl Mouse", 
			new PrefixUrlMaker("http://www.ensembl.org/Mus_musculus/Gene/Summary?g="), 
			"http://www.ensembl.org", 
			"ENSMUSG00000017167", true, false, Organism.MusMusculus);
		public static final DataSource GRAMENE_RICE = DataSource.register (
			"EnOj", "Gramene Rice", 
			new PrefixUrlMaker("http://www.gramene.org/Oryza_sativa_japonica/Gene/Summary?db=core;g="), 
			"http://www.gramene.org/", 
			"osa-MIR171a", true, false, Organism.OryzaSativa);
		public static final DataSource ENSEMBL_CHIMP = DataSource.register (
			"EnPt", "Ensembl Chimp", 
			new PrefixUrlMaker("http://www.ensembl.org/Pan_troglodytes/Gene/Summary?g="), 
			"http://www.ensembl.org", 
			"ENSPTRG00000036034", true, false, Organism.PanTroglodytes);
		public static final DataSource ENSEMBL_HORSE = DataSource.register (
			"EnQc", "Ensembl Horse", 
			new PrefixUrlMaker("http://www.ensembl.org/Equus_caballus/Gene/Summary?g="), 
			"http://www.ensembl.org", 
			"ENSECAG00000026160", true, false, Organism.EquusCaballus);
		public static final DataSource ENSEMBL_RAT = DataSource.register (
			"EnRn", "Ensembl Rat", 
			new PrefixUrlMaker("http://www.ensembl.org/Rattus_norvegicus/Gene/Summary?g="), 
			"http://www.ensembl.org", 
			"ENSRNOG00000016648", true, false, Organism.RattusNorvegicus);
		public static final DataSource ENSEMBL_SCEREVISIAE = DataSource.register (
			"EnSc", "Ensembl S. cerevisiae", 
			new PrefixUrlMaker("http://www.ensembl.org/Saccharomyces_cerevisiae/Gene/Summary?g="), 
			"http://www.ensembl.org", 
			"YGR147C", true, false, Organism.SaccharomycesCerevisiae);
		public static final DataSource ENSEMBL_XENOPUS = DataSource.register (
			"EnXt", "Ensembl Xenopus", 
			new PrefixUrlMaker("http://www.ensembl.org/Xenopus_tropicalis/Gene/Summary?g="), 
			"http://www.ensembl.org", 
			"ENSXETG00000029448", true, false, Organism.XenopusTropicalis);
		public static final DataSource FLYBASE = DataSource.register (
			"F", "FlyBase", 
			new PrefixUrlMaker("http://flybase.bio.indiana.edu/.bin/fbidq.html?"), null, 
			"FBgn0031208", true, false, Organism.DrosophilaMelanogaster);
		public static final DataSource GENBANK = DataSource.register (
			"G", "GenBank", 
			null, null, 
			null, false, false, null);
		public static final DataSource CODELINK = DataSource.register (
			"Ge", "CodeLink", 
			null, null, 
			"GE86325", false, false, null);	
		public static final DataSource GRAMENE_GENES_DB = DataSource.register (
			"Gg", "Gramene Genes DB", 
			new PrefixUrlMaker ("http://www.gramene.org/db/genes/search_gene?acc="), 
			"http://www.gramene.org/", 
			"GR:0060184", true, false, null);	
		public static final DataSource GRAMENE_LITERATURE = DataSource.register (
			"Gl", "Gramene Literature", 
			new PrefixUrlMaker ("http://www.gramene.org/db/literature/pub_search?ref_id="), 
			"http://www.gramene.org/", 
			"6200", false, false, null);	
		public static final DataSource GRAMENE_PATHWAY = DataSource.register (
			"Gp", "Gramene Pathway", 
			null, "http://www.gramene.org/pathway", 
			"PROTEIN-KINASE-RXN", false, false, null);	
		public static final DataSource GEN_PEPT = DataSource.register (
			"Gp", "GenPept", 
			null, null, 
			"AAH72400", false, false, null);
		public static final DataSource HUGO = DataSource.register (
			"H", "HUGO", 
			new PrefixUrlMaker ("http://www.genenames.org/data/hgnc_data.php?hgnc_id="),
			"http://www.genenames.org/", 
			"25068", true, false, Organism.HomoSapiens);
		public static final DataSource HSGENE = DataSource.register (
			"Hs", "HsGene", 
			null, null, null, true, false, Organism.HomoSapiens);
		public static final DataSource INTERPRO = DataSource.register (
			"I", "InterPro", 
			new PrefixUrlMaker ("http://www.ebi.ac.uk/interpro/IEntry?ac="),
			"http://www.ebi.ac.uk/interpro", null, false, false, null);
		public static final DataSource ILLUMINA = DataSource.register (
			"Il", "Illumina", 
			null, null, 
			"ILMN_5668", false, false, null);
		public static final DataSource IPI = DataSource.register (
			"Ip", "IPI", 
			new UrlMaker () 
			{
				@Override
				public String getUrl(String id) 
				{			
					return "http://srs.ebi.ac.uk/srsbin/cgi-bin/wgetz?-e+[IPI-acc:" + id + "]";
				}
			}, null, 
			"IPI00020529", false, false, null);
		public static final DataSource IRGSP_GENE = DataSource.register (
			"Ir", "IRGSP Gene", 
			null, "http://rgp.dna.affrc.go.jp/IRGSP/", 
			"Os12g0561000", true, false, null);
		public static final DataSource ENTREZ_GENE = DataSource.register (
			"L", "Entrez Gene", 
			new PrefixUrlMaker ("http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=Retrieve&dopt=full_report&list_uids="),
			"http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene", "1232", true, false, null);
		public static final DataSource MGI = DataSource.register (
			"M", "MGI", 
			new PrefixUrlMaker ("http://www.informatics.jax.org/searches/accession_report.cgi?id="),
			"http://www.informatics.jax.org/", 
			"MGI:2687328", true, false, Organism.MusMusculus);
		public static final DataSource MIRBASE = DataSource.register (
			"Mb", "miRBase", 
			new PrefixUrlMaker ("http://microrna.sanger.ac.uk/cgi-bin/sequences/mirna_entry.pl?acc="),
			null, 
			"MI0000808", true, false, null);
		public static final DataSource MAIZE_GDB = DataSource.register (
			"Mg", "MaizeGDB", 
			new PrefixUrlMaker ("http://www.maizegdb.org/cgi-bin/displaylocusresults.cgi?term="),
			null, 
			"acc1", true, false, Organism.ZeaMays);
		public static final DataSource NASC_GENE = DataSource.register (
			"N", "NASC Gene", 
			null,
			null, "ATMG00960-TAIR-G", true, false, Organism.ArabidopsisThaliana);
		public static final DataSource NUGOWIKI = DataSource.register (
			"Nw", "NuGO wiki", 
			new PrefixUrlMaker ("http://nugowiki.org/index.php/"),
			null, "HMDB00001", false, true, null);
		public static final DataSource OTHER = DataSource.register (
			"O", "Other", 
			null, null, null, true, false, null);
		public static final DataSource ORYZA_BASE = DataSource.register (
			"Ob", "Oryzabase", 
			new PrefixUrlMaker ("http://www.shigen.nig.ac.jp/rice/oryzabase/gateway/gatewayAction.do?target=symbol&id="), 
			"http://www.shigen.nig.ac.jp/rice/oryzabase", "468", true, false, Organism.OryzaSativa);
		public static final DataSource OMIM = DataSource.register (
			"Om", "OMIM", 
			new PrefixUrlMaker ("http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=OMIM&cmd=Search&doptcmdl=Detailed&term=?"),
			"http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=OMIM", "608981", 
			false, false, null);
		public static final DataSource RICE_ENSEMBL_GENE = DataSource.register (
			"Os", "Rice Ensembl Gene", 
			new PrefixUrlMaker ("http://www.gramene.org/Oryza_sativa/geneview?gene="), 
			"http://www.gramene.org/Oryza_sativa", "LOC_Os04g54800", true, false, Organism.OryzaSativa);
		public static final DataSource PDB = DataSource.register (
			"Pd", "PDB", 
			new PrefixUrlMaker ("http://bip.weizmann.ac.il/oca-bin/ocashort?id="),
			"http://www.rcsb.org/pdb/home/home.do", 
			"2Z17", true, false, null);
		public static final DataSource PFAM = DataSource.register (
			"Pf", "Pfam", 
			new PrefixUrlMaker ("http://www.sanger.ac.uk//cgi-bin/Pfam/getacc?"),
			"http://www.sanger.ac.uk/Software/Pfam", 
			null, true, false, null);
		public static final DataSource PLANTGDB = DataSource.register (
			"Pl", "PlantGDB", null, "http://www.plantgdb.org/",
			"PUT-157a-Vitis_vinifera-37378", true, false, null);
		public static final DataSource REFSEQ = DataSource.register (
			"Q", "RefSeq", 
			new UrlMaker() 
			{ 
				private static final String PRE = "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?";
				public String getUrl (String id)
				{
					if(id.startsWith("NM")) 
					{
						return PRE + "db=Nucleotide&cmd=Search&term=" + id;
					} 
					else 
					{
						return PRE + "db=Protein&cmd=search&term=" + id;
					}
				}
			}
			, "http://www.ncbi.nlm.nih.gov/RefSeq", "NM_001054966", true, false, null);
		public static final DataSource RGD = DataSource.register (
			"R", "RGD", 
			new PrefixUrlMaker ("http://rgd.mcw.edu/generalSearch/RgdSearch.jsp?quickSearch=1&searchKeyword="), 
			"http://rgd.mcw.edu/", "1587276", true, false, Organism.RattusNorvegicus);
		public static final DataSource RFAM = DataSource.register (
			"Rf", "Rfam", 
			new PrefixUrlMaker ("http://www.sanger.ac.uk/cgi-bin/Rfam/getacc?"), null, 
			"RF00066", true, false, null);
		public static final DataSource UNIPROT = DataSource.register (
			"S", "Uniprot/TrEMBL", 
			new PrefixUrlMaker ("http://www.expasy.org/uniprot/"),
			"http://www.expasy.uniprot.org/", "P57770", true, false, null);
		public static final DataSource SNP = DataSource.register (
			"Sn", "dbSNP", 
			null, null, null, true, false, null);
		public static final DataSource GENE_ONTOLOGY = DataSource.register (
			"T", "GeneOntology", 
			new PrefixUrlMaker ("http://godatabase.org/cgi-bin/go.cgi?view=details&search_constraint=terms&depth=0&query="), 
			"http://www.geneontology.org/", "GO:0005634", false, false, null);
		public static final DataSource TIGR = DataSource.register (
			"Ti", "J. Craig Venter Institute (formerly TIGR)", 
			null, "http://www.jcvi.org/", "12012.t00308", true, false, null);
		public static final DataSource UNIGENE = DataSource.register (
			"U", "UniGene", 
			new PrefixUrlMaker ("http://www.ncbi.nlm.nih.gov/UniGene/clust.cgi?UGID=1548618&SEARCH="), 
			"http://www.ncbi.nlm.nih.gov/sites/entrez?db=unigene", 
			"Hs.553708", true, false, null);
		public static final DataSource UCSC = DataSource.register (
			"Uc", "UCSC Genome Browser", 
			new PrefixUrlMaker ("http://genome.ucsc.edu/cgi-bin/hgTracks?position="), 
			"http://genome.ucsc.edu/", "uc001tyh.1", true, false, null);
		public static final DataSource WORMBASE = DataSource.register (
			"W", "WormBase", 
			new PrefixUrlMaker ("http://www.wormbase.org/db/gene/gene?name="), 
			"http://www.wormbase.org", "T24D1.1", 
			true, false, Organism.CaenorhabditisElegans);
		public static final DataSource WIKIPEDIA = DataSource.register (
				"Wi", "Wikipedia", 
				new PrefixUrlMaker ("http://en.wikipedia.org/wiki/"), "http://www.wikipedia.org", "Acetate", true, true, null);
		public static final DataSource WHEAT_GENE_CATALOG = DataSource.register (
			"Wc", "Wheat gene catalog (Grain Genes)", 
			new PrefixUrlMaker ("http://wheat.pw.usda.gov/sql?sql=select+distinct+genewgcreference.number+as+WGC,+reference.name+as+Reference,+reference.title+as+Title,+journal.name+as+Journal,+reference.volume+as+Volume,+reference.pages+as+Page+from+reference+inner+join+genewgcreference+on+genewgcreference.referenceid=reference.id+inner+join+journal+on+reference.journalid=journal.id+where+genewgcreference.number="), 
			"http://wheat.pw.usda.gov/", "341", true, false, Organism.TriticumAestivum);
		public static final DataSource WHEAT_GENE_NAMES = DataSource.register (
			"Wn", "Wheat gene names (Grain Genes)", 
			new PrefixUrlMaker ("http://wheat.pw.usda.gov/report?class=gene;name="), 
			"http://wheat.pw.usda.gov/", "5S-Rrna-D1_(Triticum)", true, false, Organism.TriticumAestivum);
		public static final DataSource WHEAT_GENE_REFERENCES= DataSource.register (
			"Wr", "Wheat gene references (Grain Genes)", 
			new PrefixUrlMaker ("http://wheat.pw.usda.gov/cgi-bin/graingenes/report.cgi?class=reference&name="), 
			"http://wheat.pw.usda.gov/", "WGS-95-1333", false, false, Organism.TriticumAestivum);
		public static final DataSource AFFY = DataSource.register (
			"X", "Affy", 
			new PrefixUrlMaker ("http://www.ensembl.org/Homo_sapiens/Search/Summary?species=all;idx=;q="), 
			"http://www.affymetrix.com/", "1851_s_at", false, false, null);
		public static final DataSource ZFIN = DataSource.register (
			"Z", "ZFIN", 
			new PrefixUrlMaker ("http://zfin.org/cgi-bin/webdriver?MIval=aa-markerview.apg&OID="),
			"http://zfin.org", "ZDB-GENE-041118-11", true, false, Organism.DanioRerio);

}
