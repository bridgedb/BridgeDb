package org.bridgedb.ws;

import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.URLMapperSQL;
import org.bridgedb.sql.SqlFactory;
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
    public static void setupIDMapper() throws IDMapperException{
        connectionOk = false;
        SQLAccess sqlAccess = SqlFactory.createSQLAccess();
        connectionOk = true;
        URLMapperSQL urlMapperSQL = new URLMapperSQL(true, sqlAccess);
        //Use with true version only is database structure has changed. It deletes all tables!!!
        //URLMapperSQL urlMapperSQL = new URLMapperSQL(true, sqlAccess);
        listener = urlMapperSQL;
    }
      
    @Test
    public void loadData() throws IDMapperException{
        defaultLoadData();        
    }
}
