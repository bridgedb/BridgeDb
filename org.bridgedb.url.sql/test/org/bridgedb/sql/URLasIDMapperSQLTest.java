/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

import java.util.Date;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTest;
import org.bridgedb.linkset.IDMapperAndLinkListenerTest;
import org.junit.BeforeClass;

/**
 * 
 * @author Christian
 */
public class URLasIDMapperSQLTest extends IDMapperAndLinkListenerTest {
    
    private static final String CREATOR1 = "testCreateProvenance";
    private static final String PREDICATE1 = "testMapping";
    private static final long CREATION1 = new Date().getTime();
    private static URLMapperSQL urlMapperSQL;
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{
        connectionOk = false;
        SQLAccess sqlAccess = TestURLSqlFactory.createTestSQLAccess();
        connectionOk = true;
        urlMapperSQL = new URLMapperSQL(true, sqlAccess);
        idMapper = urlMapperSQL;
        //provenanceFactory = urlMapperSQL;
        listener = urlMapperSQL;     
        defaultLoadData();
    }
            
}
