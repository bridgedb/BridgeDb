package org.bridgedb.virtuoso;

import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.TestSqlFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * This class depends on URLasIDMapperSQLTest having loaded the data.
 * 
 * @author Christian
 */
@Ignore
public class SourceTargetCounterTest extends org.bridgedb.statistics.SourceTargetCounterTest {
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{

        connectionOk = false;
        SQLAccess sqlAccess = TestSqlFactory.createTestVirtuosoAccess();
        connectionOk = true;
        VirtuosoMapper urlMapperVirtuoso = new VirtuosoMapper(sqlAccess);
        opsMapper = urlMapperVirtuoso;
    }
            
}
