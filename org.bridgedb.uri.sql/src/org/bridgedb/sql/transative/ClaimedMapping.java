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
package org.bridgedb.sql.transative;

import java.util.Set;
import org.bridgedb.pairs.IdSysCodePair;
import org.bridgedb.uri.api.Mapping;
import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author christian
 */
public abstract class ClaimedMapping extends Mapping{

    public ClaimedMapping(IdSysCodePair idSysCodePairSource, IdSysCodePair idSysCodePairTarget, 
            String predicate, String justification, int mappingSetId, 
            String mappingResource, String mappingSource, String lens){
        super(idSysCodePairSource, idSysCodePairTarget, 
            predicate, justification, mappingSetId, 
            mappingResource, mappingSource, lens);
    }
    
    public ClaimedMapping (ClaimedMapping previous, DirectMapping newMapping, String predicate, 
            String justification) throws BridgeDBException{
        super(previous, newMapping, predicate, justification);
    }
    
    public abstract boolean createsLoop(IdSysCodePair targetRef);

    public abstract boolean hasMappingToSelf();

    public abstract Set<String> getSysCodesToCheck();

}
