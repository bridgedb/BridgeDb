// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
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
package org.bridgedb.loader;

import java.io.File;
import java.io.IOException;
import org.bridgedb.linkset.LinksetLoader;
import org.bridgedb.linkset.transative.TransativeCreator;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.StoreType;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;

/**
 *  This is a hack as it depends on the files being in the actact locations
 * @author Christian
 */
public class RunLoader {

    private static final boolean LOAD = true;
    
    public static void main(String[] args) throws BridgeDBException, RDFHandlerException, IOException  {
        ConfigReader.logToConsole();

        LinksetLoader linksetLoader = new LinksetLoader();

        String root = "C:/Dropbox/linksets/";
        //String root = "/var/local/ops/linksets/";
        linksetLoader.clearExistingData(StoreType.LOAD);
        //1-2
        linksetLoader.load(root + "originals/ConceptWiki-Chembl2Targets.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //3-4
        linksetLoader.load(root + "originals/ConceptWiki-ChemSpider.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //5-6
        linksetLoader.load(root + "originals/ConceptWiki-DrugbankTargets.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //7-8
        linksetLoader.load(root + "originals/ConceptWiki-GO.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //9-10
        linksetLoader.load(root + "originals/ConceptWiki-MSH.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //11-12
        linksetLoader.load(root + "originals/ConceptWiki-NCIM.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //13-14
        linksetLoader.load(root + "originals/ConceptWiki-Pdb.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //15-16 
        linksetLoader.load(root + "originals/ConceptWiki-Swissprot.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //17-18 
        linksetLoader.load(root + "originals/Chembl13Id-ChemSpider.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //19-20 
        linksetLoader.load(root + "originals/Chembl13Molecule-Chembl13Id_nov12.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //21-22
        linksetLoader.load(root + "originals/Chembl13Targets-Enzyme.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //23-24 
        linksetLoader.load(root + "originals/Chembl13Targets-Swissprot.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //25-26 
        linksetLoader.load(root + "originals/ChemSpider-Chembl2Compounds.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //27-28
        linksetLoader.load(root + "originals/ChemSpider-DrugBankDrugs.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
    
        //29-30 
        File transative = new File(root + "transitive/ChemSpider-Chembl13Molecule-via-Chembl13Id.ttl");
        TransativeCreator.createTransative(18,20, transative,  StoreType.LOAD);
        linksetLoader.load(transative.getAbsolutePath(), StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //31-33
        transative = new File(root + "transitive/ConceptWiki-Chembl13Molecule-via-ChemSpider.ttl");
        TransativeCreator.createTransative(3,29, transative, StoreType.LOAD);
        linksetLoader.load(transative.getAbsolutePath(), StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //33-34
        transative = new File(root + "transitive/ConceptWiki-Chembl13Targets-via-Swissprot.ttl");
        TransativeCreator.createTransative(15,24, transative, StoreType.LOAD);
        linksetLoader.load(root + "transitive/ConceptWiki-Chembl13Targets-via-Swissprot.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //35-36
        transative = new File(root + "transitive/ConceptWiki-Chembl2Compounds-via-ChemSpider.ttl");
        TransativeCreator.createTransative(3,25, transative, StoreType.LOAD);
        linksetLoader.load(transative.getAbsolutePath(), StoreType.LOAD, ValidationType.LINKSMINIMAL);
        //37-38
        transative = new File(root + "transitive/ConceptWiki-DrugBankDrugs-via-ChemSpider.ttl");
        TransativeCreator.createTransative(3,27, transative, StoreType.LOAD);
        linksetLoader.load(transative.getAbsolutePath(), StoreType.LOAD, ValidationType.LINKSMINIMAL);
        
        //39-40  //Inserted in place of Junk
        transative = new File(root + "transitive/ConceptWiki-Chembl13Id-via-ChemSpider.ttl");
        TransativeCreator.createTransative(3,18, transative, StoreType.LOAD);
        linksetLoader.load(transative.getAbsolutePath(), StoreType.LOAD, ValidationType.LINKSMINIMAL);
      
        //41-42
        linksetLoader.load(root + "originals/Chemb13Targets-Chembl13id.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        
        //43-44
        transative = new File(root + "transitive/Swissprot-Chembl13id-via-Chembl13Targets.ttl");
        TransativeCreator.createTransative(24,41, transative, StoreType.LOAD);
        linksetLoader.load(transative.getAbsolutePath(), StoreType.LOAD, ValidationType.LINKSMINIMAL);
        
        //45-46
        transative = new File(root + "transitive/ConceptWiki-Chembl13id-via-Chembl13Targets.ttl");
        TransativeCreator.createTransative(33,41, transative, StoreType.LOAD);
        linksetLoader.load(transative.getAbsolutePath(), StoreType.LOAD, ValidationType.LINKSMINIMAL);
        
        //47-48
        transative = new File(root + "transitive/Enzyme-Chembl13id-via-Chembl13Targets.ttl");
        TransativeCreator.createTransative(22,41, transative, StoreType.LOAD);
        linksetLoader.load(transative.getAbsolutePath(), StoreType.LOAD, ValidationType.LINKSMINIMAL);
        
    }

}
