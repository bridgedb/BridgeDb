package org.bridgedb.mysql;

import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.url.URLMapperTestBase;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author Christian
 */
//@Ignore 
public class URLLinkLoaderTest extends org.bridgedb.linkset.URLLinkLoaderTest {
    
    @BeforeClass
    public static void setupURLs() throws IDMapperException{
        URLMapperTestBase.setupURLs();
        link1to2 = "http://localhost:8080/OPS-IMS/linkset/1";
        link1to3 = "http://localhost:8080/OPS-IMS/linkset/3";
        link2to1 = "http://localhost:8080/OPS-IMS/linkset/2";
        link2to3 = "http://localhost:8080/OPS-IMS/linkset/5";
        link3to1 = "http://localhost:8080/OPS-IMS/linkset/4";
        link3to2 = "http://localhost:8080/OPS-IMS/linkset/6";
    }
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{
        connectionOk = false;
        SQLAccess sqlAccess = TestSqlFactory.createTestSQLAccess();
        connectionOk = true;
        MysqlMapper urlMapperSQL = new MysqlMapper(true, sqlAccess);
        listener = urlMapperSQL;
    }
      
    @Test
    public void loadData() throws IDMapperException{
        defaultLoadData();        
    }
}
