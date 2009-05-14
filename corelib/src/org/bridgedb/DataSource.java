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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
contains information about a certain DataSource, such as
<ul>
<li>It's full name ("Ensembl")
<li>It's system code ("En")
<li>It's main url ("http://www.ensembl.org")
<li>Id-specific url's ("http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=" + id)
</ul>
The DataSource class uses the extensible enum pattern.
You can't instantiate DataSources directly, instead you have to use one of
the constants such as DataSource.ENSEMBL, or 
the "getBySystemcode" or "getByFullname" methods.
These methods return a predefined DataSource object if it exists.
If a predefined DataSource for a requested SystemCode doesn't exists,
a new one springs to life automatically. This can be used 
when the user requests new, unknown data sources. If you call
getBySystemCode twice with the same argument, it is guaranteed
that you get the same return object. However, there is no way
to combine a new DataSource with a new FullName unless you use 
the "register" method.
<p>
This way any number of pre-defined DataSources can be used, 
but plugins can define new ones and you can
handle unknown systemcodes that occur in Gpml in the same 
way as predefined ones.
<p>
PathVisio should never have to refer to system codes as Strings, except
<ul>
<li>in low level SQL code dealing with Gdb's, Gex and MAPP's (MAPPFormat.java)
<li>in low level GPML code (GPMLFormat.java)
</ul>
The preferred way to refer to a specific database is using a 
constant defined here, e.g. "DataSource.ENSEMBL"
*/
public final class DataSource
{
	private static Map<String, DataSource> bySysCode = new HashMap<String, DataSource>();
	private static Map<String, DataSource> byFullName = new HashMap<String, DataSource>();
	private static Set<DataSource> registry = new HashSet<DataSource>();
	
	public static final DataSource TAIR = new DataSource (
		 "A", "TAIR",
		 null, "http://www.arabidopsis.org/",
		 "AT1G35255", true, false, Organism.ArabidopsisThaliana);
	public static final DataSource AGILENT = new DataSource (
		"Ag", "Agilent", 
		null, null, 
		"A_24_P98555", false, false, null);
	public static final DataSource BIOGRID = new DataSource (
		"Bg", "BioGrid", 
		null, "http://www.thebiogrid.org/", 
		null, false, false, null);
	public static final DataSource CINT = new DataSource (
		"C", "Cint", 
		null, null, 
		null, false, false, null);
	public static final DataSource CCDS = new DataSource (
		"Cc", "CCDS", 
		new PrefixUrlMaker ("http://www.ncbi.nlm.nih.gov/CCDS/CcdsBrowse.cgi?REQUEST=ALLFIELDS&DATA="), null,
		"CCDS43989", false, false, null);
	public static final DataSource CAS = new DataSource (
		"Ca", "CAS", 
		new PrefixUrlMaker ("http://chem.sis.nlm.nih.gov/chemidplus/direct.jsp?regno="),
		null, null, true, true, null);
	public static final DataSource CHEBI = new DataSource (
		"Ce", "ChEBI", 
		new PrefixUrlMaker ("http://www.ebi.ac.uk/chebi/searchId=CHEBI:"),
		null, null, true, true, null);
	public static final DataSource HMDB = new DataSource (
		"Ch", "HMDB", 
		new PrefixUrlMaker ("http://www.hmdb.ca/metabolites/"),
		"http://www.hmdb.ca/", "HMDB00001", true, true, null); // NB: in spite of name, not Human specific!
	public static final DataSource KEGG_COMPOUND = new DataSource (
		"Ck", "Kegg Compound", 
		new PrefixUrlMaker ("http://www.genome.jp/dbget-bin/www_bget?cpd:"),
		null, null, true, true, null);
	public static final DataSource PUBCHEM = new DataSource (
		"Cp", "PubChem", 
		new PrefixUrlMaker ("http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid="),
		"http://pubchem.ncbi.nlm.nih.gov/", null, true, true, null);
	public static final DataSource CHEMSPIDER = new DataSource (
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
	public static final DataSource SGD = new DataSource (
		"D", "SGD", 
		new PrefixUrlMaker ("http://db.yeastgenome.org/cgi-bin/locus.pl?locus="),
		"http://www.yeastgenome.org/", "CUP1-2", true, false, Organism.SaccharomycesCerevisiae);
	public static final DataSource ENZYME_CODE = new DataSource (
		"E", "EC Number",
		new PrefixUrlMaker ("http://www.brenda-enzymes.info/php/result_flat.php4?ecno="),
		"http://www.brenda-enzymes.info/"
		, "2.7.1.71", true, false, null);
	public static final DataSource ECOLI = new DataSource (
		"Ec", "Ecoli", 
		null, null, null, true, false, Organism.EscherichiaColi);
	public static final DataSource EMBL = new DataSource (
		"Em", "EMBL", 
		new PrefixUrlMaker ("http://www.ebi.ac.uk/cgi-bin/emblfetch?style=html&id="), 
		"http://www.ebi.ac.uk/embl", "AL030996", true, false, null);
	/** @deprecated use one of the organism-specific system codes instead */ 
	public static final DataSource ENSEMBL = new DataSource (
		"En", "Ensembl", 
		new PrefixUrlMaker("http://www.ensembl.org/Homo_sapiens/Search/Summary?_q="), 
		"http://www.ensembl.org", 
		"ENSG00000139618", false, false, null);
	public static final DataSource ENSEMBL_MOSQUITO = new DataSource (
		"EnAg", "Ensembl Mosquito", 
		new PrefixUrlMaker("http://www.ensembl.org/Anopheles_gambiae/Gene/Summary?_q="), 
		"http://www.ensembl.org", 
		"AGAP006864", true, false, Organism.AnophelesGambiae);
	public static final DataSource GRAMENE_ARABIDOPSIS = new DataSource (
		"EnAt", "Gramene Arabidopsis", 
		new PrefixUrlMaker("http://www.gramene.org/Arabidopsis_thaliana/Gene/Summary?_q="), 
		"http://www.gramene.org/", 
		"ATMG01360-TAIR-G", true, false, Organism.ArabidopsisThaliana);
	public static final DataSource ENSEMBL_BSUBTILIS = new DataSource (
		"EnBs", "Ensembl B. subtilis", 
		new PrefixUrlMaker("http://bacteria.ensembl.org/Bacillus/B_subtilis/Gene/Summary?g="), 
		"http://www.ensembl.org", 
		"EBBACG00000000013", true, false, Organism.BacillusSubtilis);
	public static final DataSource ENSEMBL_COW = new DataSource (
		"EnBt", "Ensembl Cow", 
		new PrefixUrlMaker("http://www.ensembl.org/Bos_taurus/Gene/Summary?g="), 
		"http://www.ensembl.org", 
		"ENSBTAG00000043548", true, false, Organism.BosTaurus);
	public static final DataSource ENSEMBL_CELEGANS = new DataSource (
		"EnCe", "Ensembl C. elegans", 
		new PrefixUrlMaker("http://www.ensembl.org/Caenorhabditis_elegans/Gene/Summary?g="), 
		"http://www.ensembl.org", 
		"Y42H9B.1", true, false, Organism.CaenorhabditisElegans);
	public static final DataSource ENSEMBL_DOG = new DataSource (
		"EnCf", "Ensembl Dog", 
		new PrefixUrlMaker("http://www.ensembl.org/Canis_familiaris/Gene/Summary?g="), 
		"http://www.ensembl.org", 
		"ENSCAFG00000025860", true, false, Organism.CanisFamiliaris);
	public static final DataSource ENSEMBL_FRUITFLY = new DataSource (
		"EnDm", "Ensembl Fruitfly", 
		new PrefixUrlMaker("http://www.ensembl.org/Drosophila_melanogaster/Gene/Summary?g="), 
		"http://www.ensembl.org", 
		"FBgn0032956", true, false, Organism.DrosophilaMelanogaster);
	public static final DataSource ENSEMBL_ZEBRAFISH = new DataSource (
		"EnDr", "Ensembl Zebrafish", 
		new PrefixUrlMaker("http://www.ensembl.org/Danio_rerio/Gene/Summary?g="), 
		"http://www.ensembl.org", 
		"ENSDARG00000024771", true, false, Organism.DanioRero);
	public static final DataSource ENSEMBL_ECOLI = new DataSource (
		"EnEc", "Ensembl E. coli", 
		new PrefixUrlMaker("http://bacteria.ensembl.org/Escherichia_Shigella/E_coli_K12/Gene/Summary?g="), 
		"http://www.ensembl.org", 
		"EBESCG00000000010", true, false, Organism.EscherichiaColi);
	public static final DataSource ENSEMBL_CHICKEN = new DataSource (
		"EnGg", "Ensembl Chicken", 
		new PrefixUrlMaker("http://www.ensembl.org/Gallus_gallus/Gene/Summary?g="), 
		"http://www.ensembl.org", 
		"ENSGALG00000021736", true, false, Organism.GallusGallus);
	public static final DataSource ENSEMBL_HUMAN = new DataSource (
		"EnHs", "Ensembl Human", 
		new PrefixUrlMaker("http://www.ensembl.org/Homo_sapiens/Gene/Summary?g="), 
		"http://www.ensembl.org", 
		"ENSG00000139618", true, false, Organism.HomoSapiens);
	public static final DataSource ENSEMBL_MOUSE = new DataSource (
		"EnMm", "Ensembl Mouse", 
		new PrefixUrlMaker("http://www.ensembl.org/Mus_musculus/Gene/Summary?g="), 
		"http://www.ensembl.org", 
		"ENSMUSG00000017167", true, false, Organism.MusMusculus);
	public static final DataSource GRAMENE_RICE = new DataSource (
		"EnOj", "Gramene Rice", 
		new PrefixUrlMaker("http://www.gramene.org/Oryza_sativa_japonica/Gene/Summary?db=core;g="), 
		"http://www.gramene.org/", 
		"osa-MIR171a", true, false, Organism.OryzaSativa);
	public static final DataSource ENSEMBL_CHIMP = new DataSource (
		"EnPt", "Ensembl Chimp", 
		new PrefixUrlMaker("http://www.ensembl.org/Pan_troglodytes/Gene/Summary?g="), 
		"http://www.ensembl.org", 
		"ENSPTRG00000036034", true, false, Organism.PanTroglodytes);
	public static final DataSource ENSEMBL_HORSE = new DataSource (
		"EnQc", "Ensembl Horse", 
		new PrefixUrlMaker("http://www.ensembl.org/Equus_caballus/Gene/Summary?g="), 
		"http://www.ensembl.org", 
		"ENSECAG00000026160", true, false, Organism.EquusCaballus);
	public static final DataSource ENSEMBL_RAT = new DataSource (
		"EnRn", "Ensembl Rat", 
		new PrefixUrlMaker("http://www.ensembl.org/Rattus_norvegicus/Gene/Summary?g="), 
		"http://www.ensembl.org", 
		"ENSRNOG00000016648", true, false, Organism.RattusNorvegicus);
	public static final DataSource ENSEMBL_SCEREVISIAE = new DataSource (
		"EnSc", "Ensembl S. cerevisiae", 
		new PrefixUrlMaker("http://www.ensembl.org/Saccharomyces_cerevisiae/Gene/Summary?g="), 
		"http://www.ensembl.org", 
		"YGR147C", true, false, Organism.SaccharomycesCerevisiae);
	public static final DataSource ENSEMBL_XENOPUS = new DataSource (
		"EnXt", "Ensembl Xenopus", 
		new PrefixUrlMaker("http://www.ensembl.org/Xenopus_tropicalis/Gene/Summary?g="), 
		"http://www.ensembl.org", 
		"ENSXETG00000029448", true, false, Organism.XenopusTropicalis);
	public static final DataSource FLYBASE = new DataSource (
		"F", "FlyBase", 
		new PrefixUrlMaker("http://flybase.bio.indiana.edu/.bin/fbidq.html?"), null, 
		"FBgn0031208", true, false, Organism.DrosophilaMelanogaster);
	public static final DataSource GENBANK = new DataSource (
		"G", "GenBank", 
		null, null, 
		null, false, false, null);
	public static final DataSource CODELINK = new DataSource (
		"Ge", "CodeLink", 
		null, null, 
		"GE86325", false, false, null);	
	public static final DataSource GRAMENE_GENES_DB = new DataSource (
		"Gg", "Gramene Genes DB", 
		new PrefixUrlMaker ("http://www.gramene.org/db/genes/search_gene?acc="), 
		"http://www.gramene.org/", 
		"GR:0060184", true, false, null);	
	public static final DataSource GRAMENE_LITERATURE = new DataSource (
		"Gl", "Gramene Literature", 
		new PrefixUrlMaker ("http://www.gramene.org/db/literature/pub_search?ref_id="), 
		"http://www.gramene.org/", 
		"6200", false, false, null);	
	public static final DataSource GRAMENE_PATHWAY = new DataSource (
		"Gp", "Gramene Pathway", 
		null, "http://www.gramene.org/pathway", 
		"PROTEIN-KINASE-RXN", false, false, null);	
	public static final DataSource GEN_PEPT = new DataSource (
		"Gp", "GenPept", 
		null, null, 
		"AAH72400", false, false, null);
	public static final DataSource HUGO = new DataSource (
		"H", "HUGO", 
		new PrefixUrlMaker ("http://www.genenames.org/data/hgnc_data.php?hgnc_id="),
		"http://www.genenames.org/", 
		"25068", true, false, Organism.HomoSapiens);
	public static final DataSource HSGENE = new DataSource (
		"Hs", "HsGene", 
		null, null, null, true, false, Organism.HomoSapiens);
	public static final DataSource INTERPRO = new DataSource (
		"I", "InterPro", 
		new PrefixUrlMaker ("http://www.ebi.ac.uk/interpro/IEntry?ac="),
		"http://www.ebi.ac.uk/interpro", null, false, false, null);
	public static final DataSource ILLUMINA = new DataSource (
		"Il", "Illumina", 
		null, null, 
		"ILMN_5668", false, false, null);
	public static final DataSource IPI = new DataSource (
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
	public static final DataSource IRGSP_GENE = new DataSource (
		"Ir", "IRGSP Gene", 
		null, "http://rgp.dna.affrc.go.jp/IRGSP/", 
		"Os12g0561000", true, false, null);
	public static final DataSource ENTREZ_GENE = new DataSource (
		"L", "Entrez Gene", 
		new PrefixUrlMaker ("http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=Retrieve&dopt=full_report&list_uids="),
		"http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene", "1232", true, false, null);
	public static final DataSource MGI = new DataSource (
		"M", "MGI", 
		new PrefixUrlMaker ("http://www.informatics.jax.org/searches/accession_report.cgi?id="),
		"http://www.informatics.jax.org/", 
		"MGI:2687328", true, false, Organism.MusMusculus);
	public static final DataSource MIRBASE = new DataSource (
		"Mb", "miRBase", 
		new PrefixUrlMaker ("http://microrna.sanger.ac.uk/cgi-bin/sequences/mirna_entry.pl?acc="),
		null, 
		"MI0000808", true, false, null);
	public static final DataSource MAIZE_GDB = new DataSource (
		"Mg", "MaizeGDB", 
		new PrefixUrlMaker ("http://www.maizegdb.org/cgi-bin/displaylocusresults.cgi?term="),
		null, 
		"acc1", true, false, Organism.ZeaMays);
	public static final DataSource NASC_GENE = new DataSource (
		"N", "NASC Gene", 
		null,
		null, "ATMG00960-TAIR-G", true, false, Organism.ArabidopsisThaliana);
	public static final DataSource NUGOWIKI = new DataSource (
		"Nw", "NuGO wiki", 
		new PrefixUrlMaker ("http://nugowiki.org/index.php/"),
		null, "HMDB00001", false, true, null);
	public static final DataSource OTHER = new DataSource (
		"O", "Other", 
		null, null, null, true, false, null);
	public static final DataSource ORYZA_BASE = new DataSource (
		"Ob", "Oryzabase", 
		new PrefixUrlMaker ("http://www.shigen.nig.ac.jp/rice/oryzabase/gateway/gatewayAction.do?target=symbol&id="), 
		"http://www.shigen.nig.ac.jp/rice/oryzabase", "468", true, false, Organism.OryzaSativa);
	public static final DataSource OMIM = new DataSource (
		"Om", "OMIM", 
		new PrefixUrlMaker ("http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=OMIM&cmd=Search&doptcmdl=Detailed&term=?"),
		"http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=OMIM", "608981", 
		false, false, null);
	public static final DataSource RICE_ENSEMBL_GENE = new DataSource (
		"Os", "Rice Ensembl Gene", 
		new PrefixUrlMaker ("http://www.gramene.org/Oryza_sativa/geneview?gene="), 
		"http://www.gramene.org/Oryza_sativa", "LOC_Os04g54800", true, false, Organism.OryzaSativa);
	public static final DataSource PDB = new DataSource (
		"Pd", "PDB", 
		new PrefixUrlMaker ("http://bip.weizmann.ac.il/oca-bin/ocashort?id="),
		"http://www.rcsb.org/pdb/home/home.do", 
		"2Z17", true, false, null);
	public static final DataSource PFAM = new DataSource (
		"Pf", "Pfam", 
		new PrefixUrlMaker ("http://www.sanger.ac.uk//cgi-bin/Pfam/getacc?"),
		"http://www.sanger.ac.uk/Software/Pfam", 
		null, true, false, null);
	public static final DataSource PLANTGDB = new DataSource (
		"Pl", "PlantGDB", null, "http://www.plantgdb.org/",
		"PUT-157a-Vitis_vinifera-37378", true, false, null);
	public static final DataSource REFSEQ = new DataSource (
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
	public static final DataSource RGD = new DataSource (
		"R", "RGD", 
		new PrefixUrlMaker ("http://rgd.mcw.edu/generalSearch/RgdSearch.jsp?quickSearch=1&searchKeyword="), 
		"http://rgd.mcw.edu/", "1587276", true, false, Organism.RattusNorvegicus);
	public static final DataSource RFAM = new DataSource (
		"Rf", "Rfam", 
		new PrefixUrlMaker ("http://www.sanger.ac.uk/cgi-bin/Rfam/getacc?"), null, 
		"RF00066", true, false, null);
	public static final DataSource UNIPROT = new DataSource (
		"S", "Uniprot/TrEMBL", 
		new PrefixUrlMaker ("http://www.expasy.org/uniprot/"),
		"http://www.expasy.uniprot.org/", "P57770", true, false, null);
	public static final DataSource SNP = new DataSource (
		"Sn", "dbSNP", 
		null, null, null, true, false, null);
	public static final DataSource GENE_ONTOLOGY = new DataSource (
		"T", "GeneOntology", 
		new PrefixUrlMaker ("http://godatabase.org/cgi-bin/go.cgi?view=details&search_constraint=terms&depth=0&query="), 
		"http://www.geneontology.org/", "GO:0005634", false, false, null);
	public static final DataSource TIGR = new DataSource (
		"Ti", "J. Craig Venter Institute (formerly TIGR)", 
		null, "http://www.jcvi.org/", "12012.t00308", true, false, null);
	public static final DataSource UNIGENE = new DataSource (
		"U", "UniGene", 
		new PrefixUrlMaker ("http://www.ncbi.nlm.nih.gov/UniGene/clust.cgi?UGID=1548618&SEARCH="), 
		"http://www.ncbi.nlm.nih.gov/sites/entrez?db=unigene", 
		"Hs.553708", true, false, null);
	public static final DataSource UCSC = new DataSource (
		"Uc", "UCSC Genome Browser", 
		new PrefixUrlMaker ("http://genome.ucsc.edu/cgi-bin/hgTracks?position="), 
		"http://genome.ucsc.edu/", "uc001tyh.1", true, false, null);
	public static final DataSource WORMBASE = new DataSource (
		"W", "WormBase", 
		new PrefixUrlMaker ("http://www.wormbase.org/db/gene/gene?name="), 
		"http://www.wormbase.org", "T24D1.1", 
		true, false, Organism.CaenorhabditisElegans);
	public static final DataSource WIKIPEDIA = new DataSource (
			"Wi", "Wikipedia", 
			new PrefixUrlMaker ("http://en.wikipedia.org/wiki/"), "http://www.wikipedia.org", "Acetate", true, true, null);
	public static final DataSource WHEAT_GENE_CATALOG = new DataSource (
		"Wc", "Wheat gene catalog (Grain Genes)", 
		new PrefixUrlMaker ("http://wheat.pw.usda.gov/sql?sql=select+distinct+genewgcreference.number+as+WGC,+reference.name+as+Reference,+reference.title+as+Title,+journal.name+as+Journal,+reference.volume+as+Volume,+reference.pages+as+Page+from+reference+inner+join+genewgcreference+on+genewgcreference.referenceid=reference.id+inner+join+journal+on+reference.journalid=journal.id+where+genewgcreference.number="), 
		"http://wheat.pw.usda.gov/", "341", true, false, Organism.TriticumAestivum);
	public static final DataSource WHEAT_GENE_NAMES = new DataSource (
		"Wn", "Wheat gene names (Grain Genes)", 
		new PrefixUrlMaker ("http://wheat.pw.usda.gov/report?class=gene;name="), 
		"http://wheat.pw.usda.gov/", "5S-Rrna-D1_(Triticum)", true, false, Organism.TriticumAestivum);
	public static final DataSource WHEAT_GENE_REFERENCES= new DataSource (
		"Wr", "Wheat gene references (Grain Genes)", 
		new PrefixUrlMaker ("http://wheat.pw.usda.gov/cgi-bin/graingenes/report.cgi?class=reference&name="), 
		"http://wheat.pw.usda.gov/", "WGS-95-1333", false, false, Organism.TriticumAestivum);
	public static final DataSource AFFY = new DataSource (
		"X", "Affy", 
		new PrefixUrlMaker ("http://www.ensembl.org/Homo_sapiens/Search/Summary?species=all;idx=;q="), 
		"http://www.affymetrix.com/", "1851_s_at", false, false, null);
	public static final DataSource ZFIN = new DataSource (
		"Z", "ZFIN", 
		new PrefixUrlMaker ("http://zfin.org/cgi-bin/webdriver?MIval=aa-markerview.apg&OID="),
		"http://zfin.org", "ZDB-GENE-041118-11", true, false, Organism.DanioRero);

	private String sysCode = null;
	private String fullName = null;
	private String mainUrl = null;
	private UrlMaker urlMaker = null;
	private Organism organism = null;
	private boolean isPrimary= true;
	private boolean isMetabolite = false;
	private String idExample = null;
	
	/**
	 * Constructor is private, so that we don't
	 * get any standalone DataSources.
	 * That way we can make sure that two DataSources
	 * pointing to the same datbase are really the same.
	 * 
	 * @param sysCode short unique code between 1-4 letters, originally used by GenMAPP
	 * @param fullName full name used in GPML
	 * @param urlMaker turns an identifier into a valid link-out.
	 * @param mainUrl url of homepage 
	 * @param organism organism for which this system code is suitable, or null for any / not applicable
	 * @param isPrimary secondary id's such as EC numbers, Gene Ontology or vendor-specific systems occur in data or linkouts,
	 * 	but their use in pathways is discouraged
	 * @param isMetabolite true if this DataSource describes metabolites 
	 * @param idExample an example id from this system
	 */
	private DataSource (String sysCode, String fullName, 
			UrlMaker urlMaker, String mainUrl,
			String idExample, boolean isPrimary, boolean isMetabolite, Organism organism)
	{
		this.sysCode = sysCode;
		this.fullName = fullName;
		this.mainUrl = mainUrl;
		this.urlMaker = urlMaker;
		this.idExample = idExample;
		this.isPrimary = isPrimary;
		this.isMetabolite = isMetabolite;
		this.organism = organism;
		
		registry.add (this);
		if (sysCode != null || "".equals(sysCode))
			bySysCode.put(sysCode, this);
		if (fullName != null || "".equals(fullName));
			byFullName.put(fullName, this);
	}
	
	/** 
	 * turn id into url pointing to info page on the web, e.g. "http://www.ensembl.org/get?id=ENSG..."
	 * @param id identifier to use in url
	 * @return Url
	 */
	public String getUrl(String id)
	{
		if (urlMaker != null)
			return urlMaker.getUrl(id); 
		else 
			return null;
	}
				
	/** 
	 * returns full name of DataSource e.g. "Ensembl". 
	 * May return null if only the system code is known. 
	 * Also used as identifier in GPML
	 * @return full name of DataSource 
	 */
	public String getFullName()
	{
		return fullName;
	}
	
	/** 
	 * returns GenMAPP SystemCode, e.g. "En". May return null,
	 * if only the full name is known.
	 * Also used as identifier in
	 * <ol> 
	 * <li>Gdb databases, 
	 * <li>Gex databases.
	 * <li>Imported data
	 * <li>the Mapp format.
	 * </ol> 
	 * We should try not to use the system code anywhere outside
	 * these 4 uses.
	 * @return systemcode, a short unique code.
	 */
	public String getSystemCode()
	{
		return sysCode;
	}
	
	/**
	 * Return the main Url for this datasource,
	 * that can be used to refer to the datasource in general.
	 * (e.g. http://www.ensembl.org/)
	 * 
	 * May return null in case the main url is unknown.
	 * @return main url
	 */
	public String getMainUrl()
	{	
		return mainUrl;
	}
	
	/** 
	 * so new system codes can be added easily by 
	 * plugins. url and urlMaker may be null 
	 * @param sysCode short unique code between 1-4 letters, originally used by GenMAPP
	 * @param fullName full name used in GPML
	 * @param urlMaker turns an identifier into a valid link-out.
	 * @param mainUrl url of homepage 
	 * @param organism organism for which this system code is suitable, or null for any / not applicable
	 * @param isPrimary secondary id's such as EC numbers, Gene Ontology or vendor-specific systems occur in data or linkouts,
	 * 	but their use in pathways is discouraged
	 * @param isMetabolite true if this DataSource describes metabolites 
	 * @param exampleId an example id from this system
	 */
	public static void register(String sysCode, String fullName, UrlMaker urlMaker, String mainUrl, 
			String exampleId, boolean isPrimary, boolean isMetabolite, Organism organism)
	{
		new DataSource (sysCode, fullName, urlMaker, mainUrl, exampleId, isPrimary, isMetabolite, organism);
	}
	
	/** 
	 * @param systemCode short unique code to query for
	 * @return pre-existing DataSource object by system code, 
	 * 	if it exists, or creates a new one. 
	 */
	public static DataSource getBySystemCode(String systemCode)
	{
		if (!bySysCode.containsKey(systemCode))
		{
			register (systemCode, null, null, null, null, false, false, null);
		}
		return bySysCode.get(systemCode);
	}
	
	/** 
	 * returns pre-existing DataSource object by 
	 * full name, if it exists, 
	 * or creates a new one. 
	 * @param fullName full name to query for
	 * @return DataSource
	 */
	public static DataSource getByFullName(String fullName)
	{
		if (!byFullName.containsKey(fullName))
		{
			register (null, fullName, null, null, null, false, false, null);
		}
		return byFullName.get(fullName);
	}
	
	/**
		get all registered datasoures as a set.
		@return set of all registered DataSources
	*/ 
	static public Set<DataSource> getDataSources()
	{
		return registry;
	}
	
	/**
	 * returns a filtered subset of available datasources.
	 * @param primary Filter for specified primary-ness. If null, don't filter on primary-ness.
	 * @param metabolite Filter for specified metabolite-ness. If null, don't filter on metabolite-ness.
	 * @param o Filter for specified organism. If null, don't filter on organism.
	 * @return filtered set.
	 */
	static public Set<DataSource> getFilteredSet (Boolean primary, Boolean metabolite, Organism o)
	{
		final Set<DataSource> result = new HashSet<DataSource>();
		for (DataSource ds : registry)
		{
			if (
					(primary == null || ds.isPrimary == primary) &&
					(metabolite == null || ds.isMetabolite == metabolite) &&
					(o == null || ds.organism == null || o == ds.organism))
			{
				result.add (ds);
			}
		}
		return result;
	}
	
	/**
	 * Get a list of all non-null full names.
	 * <p>
	 * Warning: the ordering of this list is undefined.
	 * Two subsequent calls may give different results.
	 * @return List of full names
	 */
	static public List<String> getFullNames()
	{
		final List<String> result = new ArrayList<String>();
		result.addAll (byFullName.keySet());
		return result;
	}
	/**
	 * The string representation of a DataSource is equal to
	 * it's full name. (e.g. "Ensembl")
	 * @return String representation
	 */
	public String toString()
	{
		return fullName;
	}
	
	/** an UrlMaker knows how to turn an id into an Url string. */
	public static abstract class UrlMaker
	{
		/**
		 * Generate an Url from an identifier.
		 * @param id identifier to use in Url 
		 * @return url based on the identifier */
		public abstract String getUrl(String id);
	}
	
	/** Implements most common way an Url is made: add Id to a prefix. */ 
	public static class PrefixUrlMaker extends UrlMaker
	{
		private final String prefix;
		
		/** @param prefix prefix to use in url */
		public PrefixUrlMaker(String prefix)
		{
			this.prefix = prefix;
		}
		
		/**
		 * Generate url from identifier.
		 * @param id identifier to use in url 
		 * @return Simply returns prefix + id */
		@Override
		public String getUrl(String id) 
		{
			return prefix + id;
		}
	}	

	/**
	 * @return example Xref, mostly for testing purposes
	 */
	public Xref getExample ()
	{
		return new Xref (idExample, this);
	}
	
	/**
	 * @return if this is a primary DataSource or not. Primary DataSources 
	 * are preferred when annotating models.
	 */
	public boolean isPrimary()
	{
		return isPrimary();
	}
	
	/**
	 * @return if this DataSource describes metabolites or not.
	 */
	public boolean isMetabolite()
	{
		return isMetabolite;
	}

	/**
	 * @return Organism that this DataSource describes, or null if multiple / not applicable.
	 */
	public Organism getOrganism()
	{
		return organism;
	}

}
