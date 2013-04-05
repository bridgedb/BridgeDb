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
        linksetLoader.load(root + "originals/ConceptWiki-ChemSpider.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load(root + "originals/ConceptWiki-DrugbankTargets.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load(root + "originals/ConceptWiki-GO.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load(root + "originals/ConceptWiki-MSH.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load(root + "originals/ConceptWiki-NCIM.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load(root + "originals/ConceptWiki-Pdb.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load(root + "originals/ConceptWiki-Swissprot.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load(root + "originals/Chembl13Id-ChemSpider.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load(root + "originals/Chembl13Molecule-Chembl13Id_nov12.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load(root + "originals/Chembl13Targets-Enzyme.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load(root + "originals/Chembl13Targets-Swissprot.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load(root + "originals/ChemSpider-DrugBankDrugs.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load(root + "originals/Chemb13Targets-Chembl13id.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);        
    }

}
