/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.mysql.URLMapperSQL;
import org.bridgedb.virtuoso.URLMapperVirtuoso;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.openrdf.OpenRDFException;

/**
 *
 * @author Christian
 */
@Ignore
public class VirtusosLinksetHandlerTest extends LinksetHandlerTest {
    
    @BeforeClass
    public static void loadMappings() throws IDMapperException, IOException, OpenRDFException{
        SQLAccess sqlAccess = TestSqlFactory.createTestVirtuosoAccess();
        URLMapperVirtuoso urlMapperVirtuoso = new URLMapperVirtuoso(true, sqlAccess);
        //URLMapperLinkset mapper = new URLMapperLinkset(); 
        listener = urlMapperVirtuoso;
        idMapper = urlMapperVirtuoso;
        LinksetHandlerTest.loadMappings();
        //mapper.printStats();
    }
}
