/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridgedb.loader;

import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.linkset.LinksetLoader;
import org.bridgedb.linkset.transative.TransativeCreator;
import org.bridgedb.metadata.validator.ValidationType;
import org.bridgedb.utils.StoreType;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class RunLoader {

    private static URI GENERATE_PREDICATE = null;
    private static URI USE_EXISTING_LICENSES = null;
    private static URI NO_DERIVED_BY = null;
    
    public static void main(String[] args) throws IDMapperException, RDFHandlerException, IOException  {

        String root = "C:/Dropbox/linksets/";
        LinksetLoader.clearExistingData(StoreType.LOAD);
        //1-2
        LinksetLoader.parse(root + "originals/ConceptWiki-Chembl2Targets.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //3-4
        LinksetLoader.parse(root + "originals/ConceptWiki-ChemSpider.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //5-6
        LinksetLoader.parse(root + "originals/ConceptWiki-DrugbankTargets.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //7-8
        LinksetLoader.parse(root + "originals/ConceptWiki-GO.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //9-10
        LinksetLoader.parse(root + "originals/ConceptWiki-MSH.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //11-12
        LinksetLoader.parse(root + "originals/ConceptWiki-NCIM.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //13-14
        LinksetLoader.parse(root + "originals/ConceptWiki-Pdb.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //15-16 
        LinksetLoader.parse(root + "originals/ConceptWiki-Swissprot.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //17-18 
        LinksetLoader.parse(root + "originals/Chembl13Id-ChemSpider.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //19-20 
        LinksetLoader.parse(root + "originals/Chembl13Molecule-Chembl13Id.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //21-22
        LinksetLoader.parse(root + "originals/Chembl13Targets-Enzyme.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //23-24 
        LinksetLoader.parse(root + "originals/Chembl13Targets-Swissprot.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //25-26 
        LinksetLoader.parse(root + "originals/ChemSpider-Chembl2Compounds.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //27-28
        LinksetLoader.parse(root + "originals/ChemSpider-DrugBankDrugs.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
    
        //29-30 
        TransativeCreator.createTransative(18,20,root + "transitive/ChemSpider-Chembl13Molecule-via-Chembl13Id.ttl", 
                StoreType.LOAD, GENERATE_PREDICATE, USE_EXISTING_LICENSES, NO_DERIVED_BY);
        LinksetLoader.parse(root + "transitive/ChemSpider-Chembl13Molecule-via-Chembl13Id.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //31-33
        TransativeCreator.createTransative(3,29,root + "transitive/ConceptWiki-Chembl13Molecule-via-ChemSpider.ttl", 
                StoreType.LOAD, GENERATE_PREDICATE, USE_EXISTING_LICENSES, NO_DERIVED_BY);
        LinksetLoader.parse(root + "transitive/ConceptWiki-Chembl13Molecule-via-ChemSpider.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //33-34
        TransativeCreator.createTransative(15,24,root + "transitive/ConceptWiki-Chembl13Targets-via-Swissprot.ttl", 
                StoreType.LOAD, GENERATE_PREDICATE, USE_EXISTING_LICENSES, NO_DERIVED_BY);
        LinksetLoader.parse(root + "transitive/ConceptWiki-Chembl13Targets-via-Swissprot.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //35-36
        TransativeCreator.createTransative(3,25,root + "transitive/ConceptWiki-Chembl2Compounds-via-ChemSpider.ttl", 
                StoreType.LOAD, GENERATE_PREDICATE, USE_EXISTING_LICENSES, NO_DERIVED_BY);
        LinksetLoader.parse(root + "transitive/ConceptWiki-Chembl2Compounds-via-ChemSpider.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //37-38
        TransativeCreator.createTransative(3,27,root + "transitive/ConceptWiki-DrugBankDrugs-via-ChemSpider.ttl", 
                StoreType.LOAD, GENERATE_PREDICATE, USE_EXISTING_LICENSES, NO_DERIVED_BY);
        LinksetLoader.parse(root + "transitive/ConceptWiki-DrugBankDrugs-via-ChemSpider.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
    }

}
