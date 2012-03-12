/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

import java.util.Date;
import org.bridgedb.IDMapperException;
import org.bridgedb.linkset.IDMapperAndLinkListenerTest;
import org.bridgedb.provenance.Provenance;
import org.bridgedb.provenance.ProvenanceException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Note: The order of the tests is Important.
 * 
 * @author Christian
 */
public class IDMapperSQLTest extends IDMapperAndLinkListenerTest {
    
    private static final String CREATOR1 = "testCreateProvenance";
    private static final String PREDICATE1 = "testMapping";
    private static final long CREATION1 = new Date().getTime();
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{
        SQLAccess sqlAccess = MySQLAccess.getTestMySQLAccess();
        checkConnection(sqlAccess);
        IDMapperSQL iDMapperSQL = new IDMapperSQL(sqlAccess);
        iDMapperSQL.dropSQLTables();
        iDMapperSQL.createSQLTables();
        idMapper = iDMapperSQL;
        provenanceFactory = iDMapperSQL;
        listener = iDMapperSQL;       
        defaultLoadData();
    }
    
    private static void checkConnection(SQLAccess sqlAccess){
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
    }
    
    @Test
    /**
     * This test will fail if there is no connection.
     * However due to the Assume in setUpClass() it should never be reached if connection fails.
     */
	public void testConnection() throws BridgeDbSqlException {
        SQLAccess access = new MySQLAccess("jdbc:mysql://localhost:3306/imstest", "imstest", "imstest");
        access.getConnection();
    }
    
}
