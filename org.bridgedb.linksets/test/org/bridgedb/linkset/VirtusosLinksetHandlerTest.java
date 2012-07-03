/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.mysql.MysqlMapper;
import org.bridgedb.url.WrappedIDMapper;
import org.bridgedb.virtuoso.VirtuosoMapper;
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
        VirtuosoMapper urlMapperVirtuoso = new VirtuosoMapper(true, sqlAccess);
        //URLMapperLinkset mapper = new URLMapperLinkset(); 
        listener = urlMapperVirtuoso;
        idMapper = new WrappedIDMapper(urlMapperVirtuoso);
        LinksetHandlerTest.loadMappings();
        //mapper.printStats();
    }
}
