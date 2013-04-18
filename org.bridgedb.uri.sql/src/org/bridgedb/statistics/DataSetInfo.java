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
package org.bridgedb.statistics;

import java.util.HashSet;
import java.util.Set;

/**
 * Holder class for the main Meta Data of MappingSet.
 *
 * Does not include everything in the void header but only what is captured in the SQL.
 * @author Christian
 */
public class DataSetInfo {
    private final String sysCode;
    private final String fullName;

    public DataSetInfo(String sysCode, String fullName){
        this.sysCode = sysCode;
        this.fullName = fullName;
    }

    /**
     * @return the sysCode
     */
    public String getSysCode() {
        return sysCode;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }
       
    public boolean equals(Object other){
        if (other instanceof DataSetInfo){
            DataSetInfo dsOther = (DataSetInfo)other;
            return this.sysCode.equals(dsOther.sysCode);
        }
        return false;
    }
    
    public int compareTo(DataSetInfo other){
        return this.sysCode.compareTo(other.sysCode);
    }
    
    public String toString(){
        if (sysCode == null || sysCode.isEmpty()){
            return fullName;
        } else {
            return sysCode;
        }
    }
 }
