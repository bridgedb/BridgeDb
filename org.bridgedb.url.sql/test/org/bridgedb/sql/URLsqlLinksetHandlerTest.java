/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.linkset.LinksetHandlerTest;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

/**
 *
 * @author Christian
 */
public class URLsqlLinksetHandlerTest extends LinksetHandlerTest {
    
    @BeforeClass
    public static void loadMappings() throws IDMapperException, IOException, RDFParseException, RDFHandlerException{
        SQLAccess sqlAccess = TestURLSqlFactory.createTestSQLAccess();
        URLMapperSQL mapper = new URLMapperSQL(true, sqlAccess); 
        listener = mapper;
        idMapper = mapper;
        LinksetHandlerTest.loadMappings();
    }
}
