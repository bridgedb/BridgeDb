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

import java.util.List;
import java.util.Set;
import org.bridgedb.pairs.IdSysCodePair;
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.uri.api.Mapping;

/**
 *
 * @author christian
 */
public abstract class IDSysCodePairMapping extends Mapping{

    public IDSysCodePairMapping(IdSysCodePair idSysCodePairSource, IdSysCodePair idSysCodePairTarget, 
            String predicate, String justification, int mappingSetId, 
            String mappingSource, String mappingResource, String lens){
        super(idSysCodePairSource, idSysCodePairTarget, predicate, justification, mappingSetId, 
                mappingSource, mappingResource, lens);
    }
    
    public IDSysCodePairMapping(IdSysCodePair idSysCodePairSource, IdSysCodePair idSysCodePairTarget, 
            String predicate, String justification, Set<String> mappingSetIds, 
            String mappingSource, String mappingResource, String lens){
        super(idSysCodePairSource, idSysCodePairTarget, predicate, justification, mappingSetIds, 
                mappingSource, mappingResource, lens);
    }

    public IDSysCodePairMapping(IdSysCodePair pair){
        super(pair);
    }

    public abstract boolean createsLoop(IdSysCodePair targetRef);

    public abstract boolean hasMappingToSelf();

    public abstract Set<String> getSysCodesToCheck();

}
