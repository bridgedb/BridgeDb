package org.bridgedb.sql;

import org.bridgedb.mysql.URLMapperSQL;
import org.bridgedb.IDMapperException;
import org.bridgedb.url.URLMapperTestBase;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author Christian
 */
public class URLLinkLoaderTest extends org.bridgedb.linkset.URLLinkLoaderTest {
    
    @BeforeClass
    public static void setupURLs() throws IDMapperException{
        URLMapperTestBase.setupURLs();
        link1to2 = "http://localhost:8080/OPS-IMS/linkset/1/#Test1_2";
        link1to3 = "http://localhost:8080/OPS-IMS/linkset/2/#Test1_3";
        link2to1 = "http://localhost:8080/OPS-IMS/linkset/1/#Test1_2/inverted";
        link2to3 = "http://localhost:8080/OPS-IMS/linkset/3/#Test2_3";
        link3to1 = "http://localhost:8080/OPS-IMS/linkset/2/#Test1_3/inverted";
        link3to2 = "http://localhost:8080/OPS-IMS/linkset/3/#Test2_3/inverted";
    }
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{
        connectionOk = false;
        SQLAccess sqlAccess = TestSqlFactory.createTestSQLAccess();
        connectionOk = true;
        URLMapperSQL urlMapperSQL = new URLMapperSQL(true, sqlAccess);
        listener = urlMapperSQL;
    }
      
    @Test
    public void loadData() throws IDMapperException{
        defaultLoadData();        
    }
}
