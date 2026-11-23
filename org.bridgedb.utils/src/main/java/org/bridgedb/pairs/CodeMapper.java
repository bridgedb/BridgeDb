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
package org.bridgedb.pairs;

import org.bridgedb.Xref;
import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author Christian
 */
public interface CodeMapper {
   
    /**
     * Creates an IdSysCodePair based on a xref.
     * 
     * Changes the ref.id and dataSource.systemCode if required.
     * 
     * @param xref
     * @return The pair used to represent this xref
     * @throws BridgeDBException Normally thrown if the xref is invalid or incomplete.
     */
    public IdSysCodePair toIdSysCodePair(Xref xref) throws BridgeDBException;
    
    
    /**
     * Creates a xref based on an IdSysCodePair 
     * 
     * Changes the ref.id and dataSource.systemCode if required.
     * 
     * @param pair
     * @return The xref used to represent this pair
     * @throws BridgeDBException Normally thrown if the pair is invalid, incomplete or can not be converted to a valid xref.
     *     If the syscode does not convert to a valid DataSource
     */    
    public Xref toXref(IdSysCodePair pair) throws BridgeDBException;

    
}
