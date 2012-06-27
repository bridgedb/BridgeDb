package org.bridgedb.virtuoso;

import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.TestSqlFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * 
 * @author Christian
 */
@Ignore
public class IDMapperCapabilitiesTest extends org.bridgedb.IDMapperCapabilitiesTest {
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{
        SQLAccess sqlAccess = TestSqlFactory.createTestVirtuosoAccess();
        VirtuosoMapper urlMapperVirtuoso = new VirtuosoMapper(sqlAccess);
        capabilities = urlMapperVirtuoso;
    }
            
}
