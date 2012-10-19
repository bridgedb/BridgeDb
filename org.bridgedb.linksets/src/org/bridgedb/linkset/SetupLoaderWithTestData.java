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
package org.bridgedb.linkset;
import java.io.FileNotFoundException;
import org.bridgedb.IDMapperException;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.metadata.validator.ValidationType;
import org.bridgedb.rdf.RdfReader;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.utils.Reporter;
import org.bridgedb.utils.StoreType;

/**
 * @author Christian
 */
public class SetupLoaderWithTestData {
        
   private static final boolean LOAD_DATA = true;
   
   public static void main(String[] args) throws BridgeDbSqlException, IDMapperException, IDMapperLinksetException, FileNotFoundException, MetaDataException {
        Reporter.report("sample2to1.ttl");
        //String[] args1 = {, "new"};
        LinksetLoader.clearExistingData(StoreType.LOAD);
        LinksetLoader.parse("../org.bridgedb.linksets/test-data/sample1to2.ttl", StoreType.LOAD, 
                ValidationType.LINKSMINIMAL, LOAD_DATA);
        LinksetLoader.parse("../org.bridgedb.linksets/test-data/sample2to3.ttl", StoreType.LOAD, 
                ValidationType.LINKSMINIMAL, LOAD_DATA);
        LinksetLoader.parse("../org.bridgedb.linksets/test-data/sample1to3.ttl", StoreType.LOAD, 
                ValidationType.LINKSMINIMAL, LOAD_DATA);
        LinksetLoader.parse("../org.bridgedb.metadata/test-data/chemspider-void.ttl", StoreType.LOAD, 
                ValidationType.DATASETVOID, LOAD_DATA);
        LinksetLoader.parse("../org.bridgedb.metadata/test-data/chembl-rdf-void.ttl", StoreType.LOAD, 
                ValidationType.DATASETVOID, LOAD_DATA);
        System.out.println(new RdfReader(StoreType.LIVE).getVoidRDF(1));
	}

}
