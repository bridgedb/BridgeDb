// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
// Copyright 2006-2009 BridgeDb developers
// Copyright 2012-2013 Christian Brenninkmeijer
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
package org.bridgedb.bio;

import java.util.Comparator;
import org.bridgedb.DataSource;

/**
 *
 * @author Christian
 */
public class DataSourceComparator implements Comparator<DataSource>{

    @Override
    public int compare(DataSource dataSource1, DataSource dataSource2) {
        int result = softCompare(dataSource1.getFullName(), dataSource2.getFullName());
        if (result != 0){
            return result;
        }
        result = softCompare(dataSource1.getSystemCode(), dataSource2.getSystemCode());
        if (result != 0){
            return result;
        }
        return dataSource1.hashCode() - dataSource2.hashCode();
    }

    private int softCompare(String value1, String value2) {
        if (value1 == null || value1.trim().isEmpty()){
           if (value2 == null || value2.trim().isEmpty()){
               return 0;
           } else {
               return -1;
           }
        } else {
           if (value2 == null || value2.trim().isEmpty()){
               return 1;
           } else {
               //Try Ignore case first
               int result = value1.toLowerCase().compareTo(value2.toLowerCase());
               if (result != 0){
                   return result;
               }
               //If that is tied try with case to keep Pubmed and PubMed order consistant.
               return value1.compareTo(value2);
           }
        }
    }
}
