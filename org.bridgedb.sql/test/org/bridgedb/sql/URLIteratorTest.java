/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

import org.bridgedb.mysql.URLMapperSQL;
import java.util.Date;
import org.bridgedb.IDMapperException;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * 
 * @author Christian
 */
@Ignore
public class URLIteratorTest extends org.bridgedb.url.URLIteratorTest {
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{
        SQLAccess sqlAccess = TestSqlFactory.createTestSQLAccess();
        URLMapperSQL urlMapperSQL = new URLMapperSQL(sqlAccess);
        urlIterator = urlMapperSQL;
    }
            
}
