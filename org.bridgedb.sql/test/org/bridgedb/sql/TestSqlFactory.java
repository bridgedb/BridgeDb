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

import org.bridgedb.utils.StoreType;
import org.junit.Test;

/**
 * Methods to generate the SQLAccess for MYSQl and Virtuso
 *
 * Set up in such a way that if the SQL connections fails the Tests will abort with a warning
 * WARNING: Due to the way JUnits work this will not generate a Fauilure, error or skipped count!
 *
 * @author Christian
 */
public class TestSqlFactory {

    public static SQLAccess createTestSQLAccess() {
        try {
            SQLAccess sqlAccess = SqlFactory.createSQLAccess(StoreType.TEST);
            sqlAccess.getConnection();
            return sqlAccess;
        } catch (BridgeDbSqlException ex) {
            System.err.println(ex);
            System.err.println("**** SKIPPPING tests due to Connection error.");
            System.err.println("To run these test you must have the following:");
            System.err.println("1. A MYSQL server running as configured " + SqlFactory.CONFIG_FILE_NAME);
            System.err.println("1a. Location of that file can be set by Enviroment Variable OPS-IMS-CONFIG");
            System.err.println("1b. Otherwise it will be looked for in the run path then conf/OPS-IMS then resources ");
            System.err.println("1c.     then the conf/OPS-IMS and resources in the SQL project in that order");
            System.err.println("1d. Failing that the defaults in SqlFactory.java will be used.");
            System.err.println("2. Full rights for test user on the test database required.");
            org.junit.Assume.assumeTrue(false);        
            return null;
         }
    } 

    public static SQLAccess createTestVirtuosoAccess() {
        try {
            SQLAccess sqlAccess = SqlFactory.createTestVirtuosoAccess();
            sqlAccess.getConnection();
            return sqlAccess;
        } catch (BridgeDbSqlException ex) {
            System.err.println(ex);
            System.err.println("**** SKIPPPING Virtuosos tests due to Connection error.");
            org.junit.Assume.assumeTrue(false);        
            return null;
         }
    }

    @Test
    public void testNothing(){
    }

}
