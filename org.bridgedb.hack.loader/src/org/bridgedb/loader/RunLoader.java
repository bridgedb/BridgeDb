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
import org.bridgedb.linkset.transative.TransativeFinder;
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
        TransativeFinder transativeFinder = new TransativeFinder(StoreType.LOAD);

        linksetLoader.clearExistingData(StoreType.LOAD);

        linksetLoader.load("https://www.dropbox.com/sh/6dov4e3drd2nvs7/0BCh1lgh5Y/ChemblOldMolecule-ChemblOldId.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("https://www.dropbox.com/sh/6dov4e3drd2nvs7/o0uW19eXTP/ChemblOldTargets-Enzyme.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("https://www.dropbox.com/sh/6dov4e3drd2nvs7/XUg5S95NCN/ChemblOldTargets-Swissprot.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("https://www.dropbox.com/sh/6dov4e3drd2nvs7/rIGy-VEUZz/ChembOldTargets-ChemblOldId.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);        
        linksetLoader.load("https://www.dropbox.com/sh/6dov4e3drd2nvs7/W-nCm0Hnng/ConceptWiki-ChemSpider.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("https://www.dropbox.com/sh/6dov4e3drd2nvs7/-R2Lk9v6LR/Chembl13Id-ChemSpider.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("https://www.dropbox.com/sh/6dov4e3drd2nvs7/kSw8IvOeoG/ConceptWiki-DrugbankTargets.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("https://www.dropbox.com/sh/6dov4e3drd2nvs7/DC2-xLuP17/ConceptWiki-GO.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("https://www.dropbox.com/sh/6dov4e3drd2nvs7/b_Z9uhjI4q/ConceptWiki-MSH.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("https://www.dropbox.com/sh/6dov4e3drd2nvs7/047fIfSYK_/ConceptWiki-NCIM.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("https://www.dropbox.com/sh/6dov4e3drd2nvs7/07u4Id54ax/ConceptWiki-Pdb.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("https://www.dropbox.com/sh/6dov4e3drd2nvs7/yONUjfxBcQ/ConceptWiki-Swissprot.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("https://www.dropbox.com/sh/6dov4e3drd2nvs7/8RQlU-RH7m/ChemSpider-DrugBankDrugs.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);
        transativeFinder.UpdateTransative();

        linksetLoader.load("https://www.dropbox.com/s/0w7tgw4zyou5aqs/LINKSET_EXACTMATCH_CHEBI20121023.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);
        transativeFinder.UpdateTransative();
        //Chebi -> chebi
        linksetLoader.load("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebi102/ChEBI102VoID.ttl", 
                StoreType.LOAD, ValidationType.VOID);
        linksetLoader.load("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebi102/has_functional_parentChEBI102Linkset.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebi102/has_parent_hydrideChEBI102Linkset.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebi102/has_partChEBI102Linkset.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebi102/has_roleChEBI102Linkset.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);        
        linksetLoader.load("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebi102/is_conjugate_acid_ofChEBI102Linkset.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebi102/is_conjugate_base_ofChEBI102Linkset.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebi102/is_enantiomer_ofChEBI102Linkset.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebi102/is_tautomer_ofChEBI102Linkset.ttl", 
                StoreType.LOAD, ValidationType.LINKSMINIMAL);
        transativeFinder.UpdateTransative();

    }

}
