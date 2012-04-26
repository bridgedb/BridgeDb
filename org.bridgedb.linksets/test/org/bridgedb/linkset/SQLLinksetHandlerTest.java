/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.sql.URLMapperSQL;
import org.junit.BeforeClass;
import org.openrdf.OpenRDFException;

/**
 *
 * @author Christian
 */
public class SQLLinksetHandlerTest extends LinksetHandlerTest {
    
    @BeforeClass
    public static void loadMappings() throws IDMapperException, IOException, OpenRDFException{
        SQLAccess sqlAccess = TestSqlFactory.createTestSQLAccess();
        URLMapperSQL urlMapperSQL = new URLMapperSQL(true, sqlAccess);
        //URLMapperLinkset mapper = new URLMapperLinkset(); 
        listener = urlMapperSQL;
        idMapper = urlMapperSQL;
        LinksetHandlerTest.loadMappings();
        //mapper.printStats();
    }
}
