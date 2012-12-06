/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.bridgedb.DataSource;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;

/**
 *
 * @author Christian
 */
public class AndraIndetifiersOrg {
   
    private static HashMap<String,String> andraMappings;
    
    public static void main(String[] args) throws BridgeDBException, IOException {
        ConfigReader.logToConsole();
        BioDataSource.init();
        HashMap<String,String> andraMappings = getAndraMappings();
        for (String fullName:andraMappings.keySet()){
            DataSource dataSource = DataSource.getByFullName(fullName);
            System.out.println (dataSource.getFullName());
            System.out.println ("  "+ dataSource.getURN(""));
            System.out.println ("  " + andraMappings.get(fullName));
        }
        ArrayList<String> values = new ArrayList(andraMappings.values());
        Collections.sort(values);
        for (String str : values) {
            System.out.println(str.toString());
        }
    }

    public static String getWikiPathwaysNameSpace(DataSource dataSource){
        getAndraMappings();
        String result = andraMappings.get(dataSource.getFullName());
        if (result != null){
            return result;
        }
        String urn = dataSource.getURN("");
        if (urn.length() >= 11) {
            result = "http://identifiers.org/" + urn.substring(11, urn.length()-1) + "/";
            andraMappings.put(dataSource.getFullName(), result);
            return result;
        }
        if (dataSource.getSystemCode() != null && !dataSource.getSystemCode().isEmpty()){
            result = "http://internal.wikipathways.org/datasource/" + scrub(dataSource.getSystemCode()) + "/";
            andraMappings.put(dataSource.getFullName(), result);
            return result;            
        }
        result = "http://internal.wikipathways.org/datasource/" + scrub(dataSource.getFullName()) + "/";
        andraMappings.put(dataSource.getFullName(), result);
        return result;            
    }
    
    private static String scrub(String original){
        String result = original.replace(" ", "_");
        result = result.replace(":", "_");
        result = result.replace("<", "_");
        result = result.replace(">", "_");
        result = result.replace(",", "_");
        result = result.replace(";", "_");
        return result;
    }
    
    public static HashMap<String,String> getAndraMappings(){
        if (andraMappings == null){
            loadAndraMappings();
        }
        return andraMappings;
    }
            
    private static HashMap<String,String> loadAndraMappings(){   
        andraMappings = new HashMap();
        andraMappings.put("Ensembl Horse", "http://identifiers.org/ensembl/");
        andraMappings.put("CTD Chemical", "http://identifiers.org/ctd.chemical/");
        andraMappings.put("UniProt", "http://identifiers.org/uniprot/");
        andraMappings.put("LIPID MAPS", "http://identifiers.org/lipidmaps/");
        andraMappings.put("UniGene", "http://identifiers.org/unigene/");
        andraMappings.put("ENSEMBL", "http://identifiers.org/ensembl/");
        andraMappings.put("ZFIN", "http://identifiers.org/zfin/");
        andraMappings.put("Entrez Gene", "http://identifiers.org/ncbigene/");
        andraMappings.put("Gramene Genes DB", "http://identifiers.org/gramene.gene/");
        andraMappings.put("ChemIDplus", "http://identifiers.org/chemidplus/");
        andraMappings.put("Rice Ensembl Gene", "http://identifiers.org/ensembl/");
        andraMappings.put("RGD", "http://identifiers.org/rgd/");
        andraMappings.put("Wikipedia", "http://identifiers.org/wikipedia.en/");
        andraMappings.put("Gramene Maize", "http://identifiers.org/gramene.gene/");
        andraMappings.put("Ensembl Zebrafish", "http://identifiers.org/ensembl/");
        andraMappings.put("Pubmed", "http://identifiers.org/pubmed/");
        andraMappings.put("Ensembl Chimp", "http://identifiers.org/ensembl/");
        andraMappings.put("enzyme", "http://identifiers.org/ec-code/");
        andraMappings.put("miRBase", "http://identifiers.org/mirbase/");
        andraMappings.put("Uniprot/TrEMBL", "http://identifiers.org/uniprot/");
        andraMappings.put("RefSeq", "http://identifiers.org/refseq/");
        andraMappings.put("TAIR", "http://identifiers.org/tair.locus/");
        andraMappings.put("CAS", "http://identifiers.org/cas/");
        andraMappings.put("COMPOUND", "http://identifiers.org/kegg.compound/");
        andraMappings.put("WormBase", "http://identifiers.org/wormbase/");
        andraMappings.put("HUGO", "http://identifiers.org/hgnc/");
        andraMappings.put("KEGG Pathway", "http://identifiers.org/kegg.pathway/");
        andraMappings.put("PubChem-compound", "http://identifiers.org/pubchem.compound/");
        andraMappings.put("Other", "http://internal.wikipathways.org/datasource/other/");
        andraMappings.put("Pubchem-compound", "http://identifiers.org/pubchem.compound/");
        andraMappings.put("Chemspider", "http://identifiers.org/chemspider/");
        andraMappings.put("Kegg enzyme", "http://identifiers.org/ec-code/");
        andraMappings.put("SwissProt", "http://identifiers.org/uniprot/");
        andraMappings.put("GeneDB", "http://identifiers.org/genedb/");
        andraMappings.put("KEGG Genes", "http://identifiers.org/kegg.genes/");
        andraMappings.put("Kegg ortholog", "http://identifiers.org/kegg.orthology/");
        andraMappings.put("MGI", "http://identifiers.org/mgd/");
        andraMappings.put("Ensembl Cow", "http://identifiers.org/ensembl/");
        andraMappings.put("Enzyme Nomenclature", "http://identifiers.org/ec-code/");
        andraMappings.put("Pubchem compound", "http://identifiers.org/pubchem.compound/");
        andraMappings.put("Ensembl Fruitfly", "http://identifiers.org/ensembl/");
        andraMappings.put("HGNC", "http://identifiers.org/hgnc/");
        andraMappings.put("EMBL", "http://internal.wikipathways.org/datasource/embl/");
        andraMappings.put("Ensembl", "http://identifiers.org/ensembl/");
        andraMappings.put("3DMET", "http://identifiers.org/3dmet/");
        andraMappings.put("InChI", "http://identifiers.org/inchi/");
        andraMappings.put("ISBN", "http://identifiers.org/isbn/");
        andraMappings.put("EC Number", "http://identifiers.org/ec-code/");
        andraMappings.put("Pubchem", "http://identifiers.org/pubchem.compound/");
        andraMappings.put("Ensembl Rat", "http://identifiers.org/ensembl/");
        andraMappings.put("PubChem", "http://identifiers.org/pubchem.compound/");
        andraMappings.put("InChIKey", "http://identifiers.org/inchi/");
        andraMappings.put("Kegg Compound", "http://identifiers.org/kegg.compound/");
        andraMappings.put("Ensembl C. elegans", "http://identifiers.org/ensembl/");
        andraMappings.put("Ensembl Celegans", "http://identifiers.org/ensembl/");
        andraMappings.put("TubercuList", "http://identifiers.org/myco.tuber/");
        andraMappings.put("Ensembl Yeast", "http://identifiers.org/ensembl/");
        andraMappings.put("Reactome", "http://identifiers.org/reactome/");
        andraMappings.put("Ensembl Chicken", "http://identifiers.org/ensembl/");
        andraMappings.put("hmdbid", "http://identifiers.org/hmdb/");
        andraMappings.put("Ensembl Pig", "http://identifiers.org/ensembl/");
        andraMappings.put("FlyBase", "http://identifiers.org/flybase/");
        andraMappings.put("miRBase Sequence", "http://identifiers.org/mirbase/");
        andraMappings.put("Ensembl Human", "http://identifiers.org/ensembl/");
        andraMappings.put("PubChem-substance", "http://identifiers.org/pubchem.substance/");
        andraMappings.put("SGD", "http://identifiers.org/sgd/");
        andraMappings.put("Pfam", "http://identifiers.org/pfam/");
        andraMappings.put("Ensembl Mosquito_Ag", "http://identifiers.org/ensembl/");
        andraMappings.put("Affy", "http://internal.wikipathways.org/datasource/affy/");
        andraMappings.put("Ensembl M. tuberculosis", "http://identifiers.org/ensembl/");
        andraMappings.put("Ensembl Mouse", "http://identifiers.org/ensembl/");
        andraMappings.put("PubMed", "http://identifiers.org/pubmed/");
        andraMappings.put("GenBank", "http://identifiers.org/insdc/");
        andraMappings.put("OMIM", "http://identifiers.org/omim/");
        andraMappings.put("WikiPathways", "http://identifiers.org/wikipathways/");
        andraMappings.put("GLYCAN", "http://identifiers.org/kegg.glycan/");
        andraMappings.put("Gramene Rice", "http://identifiers.org/gramene.gene/");
        andraMappings.put("Gramene Arabidopsis", "http://identifiers.org/gramene.gene/");
        andraMappings.put("ChEMBL compound", "http://identifiers.org/chembl.compound/");
        andraMappings.put("HMDB", "http://identifiers.org/hmdb/");
        andraMappings.put("ChEBI", "http://identifiers.org/obo.chebi/");
        andraMappings.put("Ensembl B. subtilis", "http://identifiers.org/ensembl/");
        andraMappings.put("Ensembl Dog", "http://identifiers.org/ensembl/");        return andraMappings;
   }
}
