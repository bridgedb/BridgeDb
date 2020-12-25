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

import java.sql.Connection;
import org.bridgedb.utils.BridgeDBException;

/**
 * A wrapper around the individual SQL DataBase Drivers.
 * <p>
 * Also serves to hide the specific database name, user and password from the rest of the code.
 * <p>
 * Allows MySQL, Virtuoso and any future required drivers to be used without changing SQL code.
 * Allows test, load, live or any other dataBase to be inserted, again without changing the SQL code.
 * @author Christian
 */
public interface SQLAccess {

    /**
     * Allows SQL code to obtain a new Connection without having access to the Database name, user name and password.
     * @return An open Connection
     * @throws BridgeDBException 
     */
    public Connection getConnection()  throws BridgeDBException;
    
}
