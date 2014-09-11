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
package org.bridgedb.sql;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bridgedb.pairs.IdSysCodePair;

/**
 *
 * @author christian
 */
public class TransitiveMapping extends AbstractMapping {
    private final List<DirectMapping> via;
    private static final String NEW_LINE = System.getProperty("line.separator");
    
    //private final Set<String> inboundSyscodes = new HashSet<String>();
    
    public TransitiveMapping (AbstractMapping previous, DirectMapping newMapping){
        super(previous.getSource(), newMapping.getTarget());
        if (previous instanceof DirectMapping ){
            via = new ArrayList<DirectMapping>();
            via.add((DirectMapping)previous);
        } else {
            TransitiveMapping previousT = (TransitiveMapping)previous;  
            via = new ArrayList<DirectMapping>(previousT.getVia());
        }
        via.add(newMapping);
    }

    List<DirectMapping> getVia() {
        return via;
    }
    
    boolean createsLoop(IdSysCodePair targetRef){
        return getSource().getSysCode().equals(targetRef.getSysCode());
    }

    public String toString(){
        StringBuilder builder = new StringBuilder(super.toString());
        for (DirectMapping mapping:via){
            builder.append(NEW_LINE).append("\t").append(mapping);
        }
        return builder.toString();
    }
}
