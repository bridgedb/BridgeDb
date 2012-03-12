/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import java.io.File;
import java.net.MalformedURLException;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTest;
import org.bridgedb.file.IDMapperText;
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
    public static void setupIDMapper() throws IDMapperException, MalformedURLException{
        IDMapper inner;
        SQLAccess sqlAccess = MySQLAccess.getTestMySQLAccess();
        try {
            sqlAccess.getConnection();
            inner = new IDMapperSQL(sqlAccess);
        } catch (BridgeDbSqlException ex){
            System.err.println(ex);
            System.err.println("**** Using file based tests due to SQL Connection error.");
            File INTERFACE_TEST_FILE = new File ("../org.bridgedb/test-data/interfaceTest.txt");
            inner = new IDMapperText(INTERFACE_TEST_FILE.toURL());
        }
        WSService wsService = new WSService();
        idMapper = new WSMapper(wsService);
    }

}
