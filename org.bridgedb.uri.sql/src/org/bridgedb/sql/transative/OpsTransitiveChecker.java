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
package org.bridgedb.sql.transative;

import java.util.HashSet;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author christian
 */
public class OpsTransitiveChecker implements TransitiveChecker{

    private static final OpsTransitiveChecker instance = new OpsTransitiveChecker();
    private static final Set<String> limitedSysCodes;
    static {
        limitedSysCodes = new HashSet<String>();
        limitedSysCodes.add(DataSource.getExistingByFullName("Chemspider").getSystemCode());
        limitedSysCodes.add(DataSource.getExistingByFullName("OPS Chemical Registry Service").getSystemCode());
        limitedSysCodes.add(DataSource.getExistingByFullName("ChEMBL target component").getSystemCode());
        limitedSysCodes.add(DataSource.getExistingByFullName("Uniprot-TrEMBL").getSystemCode());
        limitedSysCodes.add(DataSource.getExistingByFullName("Ensembl").getSystemCode());
        limitedSysCodes.add(DataSource.getExistingByFullName("DrugBank").getSystemCode());
        limitedSysCodes.add(DataSource.getExistingByFullName("HMDB").getSystemCode());
        limitedSysCodes.add(DataSource.getExistingByFullName("HGNC Accession number").getSystemCode());        
    }
    
    private OpsTransitiveChecker(){
    }
    
    public static OpsTransitiveChecker getInstance(){
        return instance;
    }
    
    public static void init() throws BridgeDBException{
        //Currently does nothing but this is where you would read a proerties file.
    }
    
    @Override
    public final boolean allowTransitive(AbstractMapping previous, DirectMapping newMapping) throws BridgeDBException {
        return limitedSysCodes.contains(newMapping.getSource().getSysCode());
    }

   static Set<String> getOpsCodes(){
        return limitedSysCodes;
    }
    
}
