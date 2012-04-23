/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

/**
 *
 * @author Christian
 */
public class TestSqlFactory {

    public static SQLAccess createTestSQLAccess() {
        try {
            SQLAccess sqlAccess = SqlFactory.createTestSQLAccess();
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
}
