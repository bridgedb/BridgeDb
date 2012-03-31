/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.linkset.LinksetHandlerTest;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.URLMapperSQL;
import org.bridgedb.sql.URLSqlFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

/**
 * 
 * @author Christian
 */
public class TestDataToMainServerTest extends LinksetHandlerTest {
    
    @BeforeClass
    public static void loadMappings() throws IDMapperException, IOException, RDFParseException, RDFHandlerException{
        SQLAccess sqlAccess = URLSqlFactory.createSQLAccess();
        URLMapperSQL mapper = new URLMapperSQL(sqlAccess); 
        listener = mapper;
        idMapper = mapper;
        LinksetHandlerTest.loadMappings();
    }
}
