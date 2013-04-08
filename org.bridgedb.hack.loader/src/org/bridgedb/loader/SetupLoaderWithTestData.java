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

import java.io.IOException;
import org.bridgedb.linkset.LinksetLoader;
import org.bridgedb.linkset.transative.TransativeFinder;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.StoreType;
import org.openrdf.rio.RDFHandlerException;

/**
 * @author Christian
 */
public class SetupLoaderWithTestData {

   private static final boolean LOAD_DATA = true;
   
   public static void main(String[] args) throws BridgeDBException, RDFHandlerException, IOException {
        ConfigReader.logToConsole();
        LinksetLoader linksetLoader = new LinksetLoader();
        linksetLoader.clearExistingData(StoreType.LOAD);
        TransativeFinder transativeFinder = new TransativeFinder(StoreType.LOAD);
        linksetLoader.load("../org.bridgedb.linksets/test-data/cw-cs.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("../org.bridgedb.linksets/test-data/cs-cm.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("../org.bridgedb.tools.metadata/test-data/chemspider-void.ttl", StoreType.LOAD, ValidationType.VOID);
        linksetLoader.load("../org.bridgedb.tools.metadata/test-data/chembl-rdf-void.ttl", StoreType.LOAD, ValidationType.VOID);
        linksetLoader.load("../org.bridgedb.linksets/test-data/cw-cs_test_profile.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("../org.bridgedb.linksets/test-data/cs-cm_test_profile.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
       linksetLoader.load("../org.bridgedb.linksets/test-data/cw-ct.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("../org.bridgedb.linksets/test-data/cw-dd.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        linksetLoader.load("../org.bridgedb.linksets/test-data/cw-dt.ttl", StoreType.LOAD, ValidationType.LINKSMINIMAL);
        transativeFinder.UpdateTransative();
	}

}
