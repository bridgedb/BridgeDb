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

import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author christian
 */
public interface TransitiveChecker {
    
    /**
     * Single entry point for any extra transitivity checks 
     * 
     * This method will not check that the two mappings actually form a transitive, nor check the predicates or justification.
     * 
     * What it could check are things like.
     * 
     * Maxi number of steps in a chain. (Currently not implemented)
     * If the middle DataSource is to be trusted.
     * 
     * Handles all tests on on two possible 
     * @param previous
     * @param newMapping
     * @return False if the any of the extra.
     * @throws BridgeDBException 
     */
    public boolean allowTransitive (AbstractMapping previous, DirectMapping newMapping) throws BridgeDBException;
    
}
