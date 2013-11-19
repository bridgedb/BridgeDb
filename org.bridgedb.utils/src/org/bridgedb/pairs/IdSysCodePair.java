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

/**
 * A thin wrapper around two String which represent the Id and dataSourceCode parts of a potential Xref.
 * 
 * This is required as there may be case where the ID saved in the database is different to the typical one in the xref.
 * This is required for xrefs like "ChEBI"
 * These refs contain "CHEBI:" as part of the id. Such as CHEBI:36927
 * Which is fine for URIs like:
 * http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI:36927 or
 * http://identifiers.org/obo.chebi/CHEBI:36927
 * but does not work for others like:
 * http://purl.org/obo/owl/CHEBI#CHEBI_36927 or
 * http://purl.obolibrary.org/obo/CHEBI_36927
 * 
 * Only in cases like the one above where this is required will the id nd syscode not be the same as xref.id and dataSource.systemCode
 * 
 * @author Christian
 */
public class IdSysCodePair {

    private final String id;
    private final String sysCode;
    private Xref original;
    
    public IdSysCodePair(String id, String dataSourceCode){
        this.id = id;
        this.sysCode = dataSourceCode;
        original = null;
    }
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the sysCode
     */
    public String getSysCode() {
        return sysCode;
    }

    @Override
    public boolean equals(Object other){
        if (other instanceof IdSysCodePair){
            IdSysCodePair pair = (IdSysCodePair)other;
            return (id.equals(pair.id) && sysCode.equals(pair.sysCode));
        }
        return false;
    }
    
    @Override
    public String toString(){
        return "id: " + id + " sysCode: " + sysCode;
    }
}
