/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTest;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.IDMapperSQL;
import org.bridgedb.sql.MySQLAccess;
import org.bridgedb.sql.SQLAccess;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class WSIDMapperTest extends IDMapperTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{
        SQLAccess sqlAccess = new MySQLAccess("jdbc:mysql://localhost:3306/imstest", "imstest", "imstest");
        boolean connectionOk = false;
        try {
            sqlAccess.getConnection();
            connectionOk = true;
        } catch (BridgeDbSqlException ex) {
            System.err.println(ex);
            System.err.println("**** SKIPPPING IDMapperSQLTest tests due to Connection error.");
            System.err.println("To run these test you must have the following:");
            System.err.println("1. A MYSQL server running on port 3306");
            System.err.println("2. A database \"imstest\" setup on that server");
            System.err.println("3. A user \"imstest\" with password \"imstest\"");
            System.err.println("4. Full rights for user \"imstest\" on the database \"imstest\".");
            System.err.println("      DO NOT GRANT \"imstest\" RIGHTS TO OTHER DATABASES.");
         }
        org.junit.Assume.assumeTrue(connectionOk);        
        IDMapperSQL iDMapperSQL = new IDMapperSQL(sqlAccess);
        idMapper = iDMapperSQL;
    }

}
