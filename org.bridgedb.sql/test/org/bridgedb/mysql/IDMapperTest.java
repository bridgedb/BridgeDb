package org.bridgedb.mysql;

import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLIdMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * 
 * @author Christian
 */
//@Ignore
public class IDMapperTest extends org.bridgedb.IDMapperTest {
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{
        connectionOk = false;
        SQLAccess sqlAccess = TestSqlFactory.createTestSQLAccess();
        connectionOk = true;
        idMapper = new SQLIdMapper(false, sqlAccess, new MySQLSpecific());
    }
            
}
