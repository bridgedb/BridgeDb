/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

import java.util.Date;
import org.bridgedb.IDMapperException;
import org.bridgedb.linkset.IDMapperAndLinkListenerTest;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * 
 * @author Christian
 */
public class IDMapperSQLTest extends IDMapperAndLinkListenerTest {
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{
        connectionOk = false;
        SQLAccess sqlAccess = TestURLSqlFactory.createTestSQLAccess();
        connectionOk = true;
        URLMapperSQL urlMapperSQL = new URLMapperSQL(sqlAccess);
        idMapper = urlMapperSQL;
        //provenanceFactory = iDMapperSQL;
        listener = urlMapperSQL;     
    }
            
}
