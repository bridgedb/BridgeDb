/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.url;

import java.util.HashMap;
import java.util.Map;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;

/**
 * Warning this is a hack.
 * Only taken into consideration when the database is reset by loading using the "new" parameter.
 * After that the only way to get this information in is to hack the SQL database.
 * 
 * Urgently needs replacing with indentifiers.org methods.
 * 
 * @author Christian
 */
public class UriSpaceMapper {
    
    public static Map<String,DataSource> getUriSpaceMappings() throws IDMapperException{
       HashMap <String,DataSource> map = new HashMap <String,DataSource>();
       DataSource dataSource = DataSource.register("TestDS1", "TestDS1"). urlPattern("http://www.foo.com/$id")
                .idExample("123").asDataSource();
       map.put("http://www.foo.com/", dataSource);
       dataSource = DataSource.register("TestDS2", "TestDS2").urlPattern("http://www.example.com/$id")
                .idExample("123").asDataSource();
       map.put("http://www.example.com/", dataSource);
       dataSource = DataSource.register("TestDS3", "TestDS3").URISpace("http://www.example.org#")
                .idExample("123").asDataSource();
       dataSource = DataSource.register("TestDS3", "TestDS3").URISpace("http://www.example.org#")
                .idExample("123").asDataSource();
       map.put("http://www.example.org#", dataSource);
       dataSource = DataSource.register("Chemb 2 Compound", "Chemb 2 Compound").URISpace("http://chem2bio2rdf.org/chembl/resource/chembl_compounds/")
                .idExample("698788").asDataSource();
       map.put("http://chem2bio2rdf.org/chembl/resource/chembl_compounds/", dataSource);
       dataSource = DataSource.register("Chemb 13 Internal", "Chemb 13 Internal ids").URISpace("http://data.kasabi.com/dataset/chembl-rdf/chemblid/")
                .idExample("CHEMBL6329").asDataSource();
       map.put("http://data.kasabi.com/dataset/chembl-rdf/chemblid/", dataSource);
       dataSource = DataSource.register("Chemb 2 Target", "Chemb 2 Target").URISpace("http://chem2bio2rdf.org/chembl/resource/chembl_targets/")
                .idExample("698788").asDataSource();
       map.put("http://chem2bio2rdf.org/chembl/resource/chembl_targets/", dataSource);
       dataSource = DataSource.register("Chembl 13 Molecule", "Chembl 13 Molecule").URISpace("http://data.kasabi.com/dataset/chembl-rdf/molecule/")
                .idExample("m1").asDataSource();
       map.put("http://data.kasabi.com/dataset/chembl-rdf/molecule/", dataSource);
       dataSource = DataSource.register("Chemb 13 Target", "Chembl 13 Target").URISpace("http://data.kasabi.com/dataset/chembl-rdf/target/")
                .idExample("t1").asDataSource();
       map.put("http://data.kasabi.com/dataset/chembl-rdf/target/", dataSource);
       dataSource = DataSource.register("MSH", "Bioontology MESH").URISpace("http://purl.bioontology.org/ontology/MSH/")
                .idExample("C536282").asDataSource();
       map.put("http://purl.bioontology.org/ontology/MSH/", dataSource);
       dataSource = DataSource.register("NCIM", "Bioontology UMLS NCIM").URISpace("http://purl.bioontology.org/ontology/NCIM/")
                .idExample("C3203102").asDataSource();
       map.put("http://purl.bioontology.org/ontology/NCIM/", dataSource);
       dataSource = DataSource.register("GO", "OBO GO").URISpace("http://purl.org/obo/owl/GO#")
                .idExample("GO_0046767").asDataSource();
       map.put("http://purl.org/obo/owl/GO#", dataSource);
       dataSource = DataSource.register("Enzyme", "Uniprot Enzyme").URISpace("http://purl.uniprot.org/enzyme/")
                .idExample("").asDataSource();
       map.put("http://purl.uniprot.org/enzyme/", dataSource);
       dataSource = DataSource.register("Swissprot", "Swissprot part of uniprot").URISpace("http://www.uniprot.org/uniprot/")
                .idExample("O43451").asDataSource();
       map.put("http://purl.uniprot.org/uniprot/", dataSource);
       map.put("http://www.uniprot.org/uniprot/", dataSource);
       dataSource = DataSource.register("ChemSpider", "ChemSpider").URISpace("http://rdf.chemspider.com/")
                .idExample("43").asDataSource();
       map.put("http://rdf.chemspider.com/", dataSource);
       map.put("http://www.chemspider.com/", dataSource);
       dataSource = DataSource.register("ConceptWiki", "ConceptWiki").URISpace("http://www.conceptwiki.org/concept/")
                .idExample("33a28bb2-35ed-4d94-adfd-3c96053cbaaf").asDataSource();
       map.put("http://www.conceptwiki.org/concept/", dataSource);
       dataSource = DataSource.register("Pdb", "pdb").URISpace("http://www.pdb.org/pdb/explore/explore.do?pdbId=")
                .idExample("1ZW8").asDataSource();
       map.put("http://www.pdb.org/pdb/explore/explore.do?pdbId=", dataSource);
       dataSource = DataSource.register("DrugBank drugs", "DrugBank Drugs").URISpace("http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/")
                .idExample("DB02901").asDataSource();
       map.put("http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/", dataSource);
       dataSource = DataSource.register("Drug Bank Target", "Drug Bank Target").URISpace("http://www4.wiwiss.fu-berlin.de/drugbank/resource/targets/")
                .idExample("6511").asDataSource();
       map.put("http://www4.wiwiss.fu-berlin.de/drugbank/resource/targets/", dataSource);
       return map;
    }
}
