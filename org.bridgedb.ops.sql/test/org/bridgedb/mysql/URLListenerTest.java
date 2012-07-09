package org.bridgedb.mysql;

import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * 
 * @author Christian
 */
//@Ignore
public class URLListenerTest extends org.bridgedb.url.URLListenerTest {
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{
        SQLAccess sqlAccess = TestSqlFactory.createTestSQLAccess();
        listener = new SQLUrlMapper(true, sqlAccess, new MySQLSpecific());
        report("setup");
    }
            
}
