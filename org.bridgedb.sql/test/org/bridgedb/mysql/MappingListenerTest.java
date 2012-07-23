package org.bridgedb.mysql;

import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLIdMapper;
import org.bridgedb.sql.SQLListener;
import org.bridgedb.sql.TestSqlFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * 
 * @author Christian
 */
//@Ignore 
public class MappingListenerTest extends org.bridgedb.mapping.MappingListenerTest {
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{
        connectionOk = false;
        SQLAccess sqlAccess = TestSqlFactory.createTestSQLAccess();
        listener = new SQLListener(true, sqlAccess, new MySQLSpecific());
        loadData();
        idMapper = new SQLIdMapper(false, sqlAccess, new MySQLSpecific());
        connectionOk = true;
        capabilities = idMapper.getCapabilities(); 
        report("setup");
    }
            
}
