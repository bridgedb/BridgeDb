/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

import java.io.IOException;
import org.bridgedb.linkset.LinksetParser;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

/**
 *
 * @author Christian
 */
public class tester {
    
    public static void main( String[] args ) throws BridgeDbSqlException, IOException, RDFParseException, RDFHandlerException {
        SQLAccess access = new MySQLAccess("jdbc:mysql://localhost:3306/ims", "ims", "ims");
        IDMapperSQL idMapperSQL = new IDMapperSQL(access);
        idMapperSQL.dropSQLTables();
        idMapperSQL.createSQLTables();
        LinksetParser.parse (idMapperSQL, idMapperSQL, "C:/Temp/cs-chembl_small.ttl", "http://foo/bar");
        LinksetParser.parse (idMapperSQL, idMapperSQL, "C:/Temp/cs-chembl_small.ttl", "http://foo/bar");
        //LinksetParser.parse (idMapperSQL, "C:/Temp/cw-cs.ttl", "http://foo/bar");
    }    
}
