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

import java.util.HashSet;
import java.util.Set;
import org.bridgedb.pairs.CodeMapper;
import org.bridgedb.pairs.IdSysCodePair;
import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author christian
 */
public class DirectMapping extends IDSysCodePairMapping {
    private final String id;
    private final int originalId;

    public DirectMapping (IdSysCodePair source, IdSysCodePair target, int id, int symmetric, String predicate, String justification, 
            String mappingSource, String mappingResource, String lens){
        super(source, target, predicate, justification, id, mappingSource, mappingResource, lens);
        this.id = "" + id;
        if (symmetric < 0){
            this.originalId = 0 - symmetric;
        } else {
            this.originalId = id;
        }
    }

    @Override
    public boolean createsLoop(IdSysCodePair targetRef){
        return getSourceSysCode().equals(targetRef.getSysCode());
    }

    @Override
    public boolean hasMappingToSelf() {
        return getSourceSysCode().equals(getTargetSysCode());
    }

    @Override
    public Set<String> getSysCodesToCheck() {
        Set<String> sysCodes = new HashSet<String>();
        sysCodes.add(getSourceSysCode());
        sysCodes.add(getTargetSysCode());
        return sysCodes;
    }

    public String getId() {
        return id;
    }

    @Override
    public void setTargetXrefs(CodeMapper codeMapper) throws BridgeDBException {
        setTarget(codeMapper.toXref(getTargetPair()));
    }

}
