// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
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
       map.put("http://rdf.example.com/", dataSource);
       dataSource = DataSource.register("TestDS3", "TestDS3")
               .urlPattern("http://www.example.org#$id")
                .idExample("123").asDataSource();
       map.put("http://www.example.org#", dataSource);
       map.put("http://rdf.example.org#", dataSource);
       dataSource = DataSource.register("Chembl 2 Compound", "Chembl 2 Compound")
               .urlPattern("http://chem2bio2rdf.org/chembl/resource/chembl_compounds/$id")
                .idExample("698788").asDataSource();
       map.put("http://chem2bio2rdf.org/chembl/resource/chembl_compounds/", dataSource);
       dataSource = DataSource.register("Chembl 13 Internal", "Chembl 13 Internal ids")
               .urlPattern("http://data.kasabi.com/dataset/chembl-rdf/chemblid/$id")
                .idExample("CHEMBL6329").asDataSource();
       map.put("http://data.kasabi.com/dataset/chembl-rdf/chemblid/", dataSource);
       dataSource = DataSource.register("Chembl 2 Target", "Chembl 2 Target")
               .urlPattern("http://chem2bio2rdf.org/chembl/resource/chembl_targets/$id")
                .idExample("698788").asDataSource();
       map.put("http://chem2bio2rdf.org/chembl/resource/chembl_targets/", dataSource);
       dataSource = DataSource.register("Chembl 13 Molecule", "Chembl 13 Molecule")
               .urlPattern("http://data.kasabi.com/dataset/chembl-rdf/molecule/$id")
                .idExample("m1").asDataSource();
       map.put("http://data.kasabi.com/dataset/chembl-rdf/molecule/", dataSource);
       dataSource = DataSource.register("Chembl 13 Target", "Chembl 13 Target")
               .urlPattern("http://data.kasabi.com/dataset/chembl-rdf/target/$id")
                .idExample("t1").asDataSource();
       map.put("http://data.kasabi.com/dataset/chembl-rdf/target/", dataSource);
       dataSource = DataSource.register("MSH", "Bioontology MESH")
               .urlPattern("http://purl.bioontology.org/ontology/MSH/$id")
                .idExample("C536282").asDataSource();
       map.put("http://purl.bioontology.org/ontology/MSH/", dataSource);
       dataSource = DataSource.register("NCIM", "Bioontology UMLS NCIM")
               .urlPattern("http://purl.bioontology.org/ontology/NCIM/$id")
                .idExample("C3203102").asDataSource();
       map.put("http://purl.bioontology.org/ontology/NCIM/", dataSource);
       dataSource = DataSource.register("GO", "OBO GO")
               .urlPattern("http://purl.org/obo/owl/GO#$id")
                .idExample("GO_0046767").asDataSource();
       map.put("http://purl.org/obo/owl/GO#", dataSource);
       dataSource = DataSource.register("Enzyme", "Uniprot Enzyme")
               .urlPattern("http://purl.uniprot.org/enzyme/$id")
                .idExample("").asDataSource();
       map.put("http://purl.uniprot.org/enzyme/", dataSource);
       dataSource = DataSource.register("Swissprot", "Swissprot part of uniprot")
               .urlPattern("http://www.uniprot.org/uniprot/$id")
                .idExample("O43451").asDataSource();
       map.put("http://purl.uniprot.org/uniprot/", dataSource);
       map.put("http://www.uniprot.org/uniprot/", dataSource);
       dataSource = DataSource.register("ChemSpider", "ChemSpider")
               .urlPattern("http://rdf.chemspider.com/$id")
                .idExample("43").asDataSource();
       map.put("http://rdf.chemspider.com/", dataSource);
       map.put("http://www.chemspider.com/", dataSource);
       dataSource = DataSource.register("ConceptWiki", "ConceptWiki")
               .urlPattern("http://www.conceptwiki.org/concept/$id")
                .idExample("33a28bb2-35ed-4d94-adfd-3c96053cbaaf").asDataSource();
       map.put("http://www.conceptwiki.org/concept/", dataSource);
       dataSource = DataSource.register("Pdb", "pdb")
               .urlPattern("http://www.pdb.org/pdb/explore/explore.do?pdbId=$id")
                .idExample("1ZW8").asDataSource();
       map.put("http://www.pdb.org/pdb/explore/explore.do?pdbId=", dataSource);
       dataSource = DataSource.register("DrugBank drugs", "DrugBank Drugs")
               .urlPattern("http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/$id")
                .idExample("DB02901").asDataSource();
       map.put("http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/", dataSource);
       dataSource = DataSource.register("Drug Bank Target", "Drug Bank Target")
               .urlPattern("http://www4.wiwiss.fu-berlin.de/drugbank/resource/targets/$id")
                .idExample("6511").asDataSource();
       map.put("http://www4.wiwiss.fu-berlin.de/drugbank/resource/targets/", dataSource);
       return map;
    }
}
