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
package org.bridgedb.sql;

/**
 * Specifies how the particular choosen DataBase System acts in cases where not all systems act the same.
 * @author Christian
 */
public interface SQLSpecific {
    
    /**
     * Identifies if the underlying System support checking if a previouly open Connection is still valid.
     * @return True if and only if the connection.isValid() call will not throw an error.
     */
    public boolean supportsIsValid();
    
    /**
     * Identifies if the underlying System is known to support multiple insertions.
     * @return True if and olny if insertions in the format INSERT INTO ... (...) VALUES (...),(...),(...) 
     *     will not throw an error
     */
    public boolean supportsMultipleInserts();
        
    /**
     * This identifies version of SQL such as MySQL that use "LIMIT" to restrict the number of tuples returned.
     */
    public boolean supportsTop();
    
    /**
     * This identifies version of SQL such as Virtuoso that use "TOP" to restrict the number of tuples returned.
     */
    public boolean supportsLimit();
    
    /**
     * Returns the specific String that is used when creating an Auto Increment Column.
     * This identifies a column where the DataBase system will automatically ad the next available id on an insert.
     * @return 
     */
    public String getAutoIncrementCommand();
}
