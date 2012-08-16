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

import org.bridgedb.IDMapperException;

/**
 * A BridgeDB Exception thrown by the SQL modules.
 * @author Christian
 */
public class BridgeDbSqlException extends IDMapperException{

    public BridgeDbSqlException(String msg, Exception ex) {
        super(msg, ex);
    }

    public BridgeDbSqlException(String msg, Exception ex, String query) {
        super(msg + ":" + query, ex);
        System.err.println(query);
    }

    public BridgeDbSqlException(String msg) {
        super (msg);
    }

}
