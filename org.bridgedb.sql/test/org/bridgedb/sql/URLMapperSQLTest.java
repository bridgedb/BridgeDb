/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

import java.util.Date;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.linkset.IDMapperAndLinkListenerTest;
import org.bridgedb.url.URLMapperTest;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class depends on URLasIDMapperSQLTest having loaded the data.
 * 
 * @author Christian
 */
public class URLMapperSQLTest extends URLMapperTest {
    
    private static final String CREATOR1 = "testCreateProvenance";
    private static final String PREDICATE1 = "testMapping";
    private static final long CREATION1 = new Date().getTime();

    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{
        DataSource.register("TestDS1", "TestDS1").nameSpace("example:");
        map1URL1 = "example:123";
        map2URL1 = "example:456";
        map3URL1 = "example:789";

        connectionOk = false;
        SQLAccess sqlAccess = TestSqlFactory.createTestURLSQLAccess();
        connectionOk = true;
        urlMapper = new URLMapperSQL(sqlAccess);
    }
            
}
