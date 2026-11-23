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

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.bridgedb.utils.BridgeDBException;

/**
 * Basic code mapper which always uses xref.id and DataSource.systemCode
 * 
 * @author Christian
 */
public class SyscodeBasedCodeMapper implements  CodeMapper{

    @Override
    public IdSysCodePair toIdSysCodePair(Xref xref) {
        String id = xref.getId();
        String sysCode = xref.getDataSource().getSystemCode();
        return new IdSysCodePair(id, sysCode);
    }

    @Override
    public Xref toXref(IdSysCodePair pair) throws BridgeDBException{
        DataSource dataSource = DataSource.getExistingBySystemCode(pair.getSysCode());
        String id = pair.getId();
        return new Xref(id, dataSource);
    }
            
}
