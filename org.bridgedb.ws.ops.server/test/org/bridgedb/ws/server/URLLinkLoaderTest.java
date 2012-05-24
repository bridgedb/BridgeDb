package org.bridgedb.ws.server;

import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.URLMapperSQL;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.url.URLMapperTestBase;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author Christian
 */
@Ignore
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
        SQLAccess sqlAccess = SqlFactory.createSQLAccess();
        connectionOk = true;
        //Use this to recreate the databases
        //URLMapperSQL urlMapperSQL = new URLMapperSQL(true, sqlAccess);
        //Use this one to keep the database
        URLMapperSQL urlMapperSQL = new URLMapperSQL(false, sqlAccess);
        //Use with true version only is database structure has changed. It deletes all tables!!!
        //URLMapperSQL urlMapperSQL = new URLMapperSQL(true, sqlAccess);
        listener = urlMapperSQL;
    }
      
    @Test
    public void loadData() throws IDMapperException{
        defaultLoadData();        
    }
}
