package org.bridgedb.ws.server;

import java.util.Date;
import org.bridgedb.IDMapperException;
import org.bridgedb.mysql.MySQLSpecific;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.ws.WSOpsMapper;
import org.bridgedb.ws.WSOpsService;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * This class depends on URLListenerTest having loaded the data.
 * 
 * @author Christian
 */
//@Ignore
public class URLMapperTest extends org.bridgedb.url.URLMapperTest {
    
    private static final String CREATOR1 = "testCreator";
    private static final String PREDICATE1 = "testMapping";
    private static final long CREATION1 = new Date().getTime();

    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{
        connectionOk = false;
        SQLAccess sqlAccess = TestSqlFactory.createTestSQLAccess();
        connectionOk = true;
        listener = new SQLUrlMapper(true, sqlAccess, new MySQLSpecific());
        loadData();
        SQLUrlMapper sqlUrlMapper = new SQLUrlMapper(false, sqlAccess, new MySQLSpecific());
        urlMapper = new WSOpsMapper(new WSOpsService(sqlUrlMapper)); 
    }
      
}
