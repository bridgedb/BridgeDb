package org.bridgedb.mysql;

import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.TestSqlFactory;
import org.junit.BeforeClass;

/**
 * This class depends on URLasIDMapperSQLTest having loaded the data.
 * 
 * @author Christian
 */
public class OpsMapperTest extends org.bridgedb.ops.OpsMapperTest {
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{

        connectionOk = false;
        SQLAccess sqlAccess = TestSqlFactory.createTestSQLAccess();
        connectionOk = true;
        MysqlMapper urlMapperSQL = new MysqlMapper(sqlAccess);
        opsMapper = urlMapperSQL;
    }
            
}
