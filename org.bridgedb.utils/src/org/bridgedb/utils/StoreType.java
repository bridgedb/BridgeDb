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
package org.bridgedb.utils;

import org.bridgedb.IDMapperException;

/**
 *
 * @author Christian
 */
public enum StoreType {
    LIVE, LOAD, TEST;
   
    public static StoreType parseString(String string) throws IDMapperException {
       for(StoreType type:StoreType.values()){
           if (type.toString().equalsIgnoreCase(string)){
               return type;
           }
       }
       throw new IDMapperException ("Unable to parse " + string + " to a StoreTyp. "
               + "Legal values are " + valuesString());
    }
    
    public static String valuesString(){
        String result = StoreType.values()[0].toString();
        for (int i = 1; i< StoreType.values().length; i++){
            result = result + ", " + StoreType.values()[i].toString();
        }
        return result;
    }

}
