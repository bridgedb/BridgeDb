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
public class TestTransitiveChecker  implements TransitiveChecker{

    private static final TestTransitiveChecker instance = new TestTransitiveChecker();
    
    private static final Set<String> limitedSysCodes;
    static {
        limitedSysCodes = new HashSet<String>();
    }

    private TestTransitiveChecker(){
    }
    
    public static TestTransitiveChecker getInstance(){
        return instance;
    }
    
    public static void init() throws BridgeDBException{
        //Currently does nothing but this is where you would read a proerties file.
    }
    
    @Override
    public final boolean allowTransitive(AbstractMapping previous, DirectMapping newMapping) throws BridgeDBException {
        return legalMiddle(newMapping.getSource().getSysCode());
    }

    public static boolean legalMiddle(String sysCode) {
        return limitedSysCodes.contains(sysCode);
    }
    
    public static void addAcceptableVai(DataSource dataSource) {
        limitedSysCodes.add(dataSource.getSystemCode());
    }
    
    
}
