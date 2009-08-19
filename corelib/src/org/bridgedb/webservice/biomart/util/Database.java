// BridgeDb,
// An abstraction layer for identifer mapping services, both local and online.
// Copyright 2006-2009 BridgeDb developers
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

package org.bridgedb.webservice.biomart.util;

import java.util.Map;

/**
 * Database, corresponding to database/mart in BioMart.
 * @author gjj
 */
public class Database {
    private String dbname;
    private Map<String, String> param;

    /**
     *
     * @param dbname database name
     * @param param database parameters
     */
    public Database(String dbname, Map<String, String> param) {
        this.dbname = dbname;
        this.param = param;
    }

    /**
     *
     * @return database name
     */
    public String getName() {
        return dbname;
    }

    /**
     *
     * @return database parameters
     */
    public Map<String, String> getParam() {
        return param;
    }

    /**
     *
     * @return true if visible; false otherwise
     */
    public boolean visible() {
        return param.get("visible").equals("1");
    }

    /**
     *
     * @return database display name
     */
    public String displayName() {
        return param.get("displayName");
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return displayName();
    }
}
