/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.linkset.LinksetParser;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

/**
 *
 * @author Christian
 */
public class LinksetLoader {
    
   	public static void main(String[] args) 
            throws BridgeDbSqlException, IOException, RDFParseException, RDFHandlerException, IDMapperException {
        if (args.length != 1){
            usage();
        }
        SQLAccess sqlAccess = URLSqlFactory.createSQLAccess();
        URLMapperSQL urlMapperSQL = new URLMapperSQL(sqlAccess);
        LinksetParser parser = new LinksetParser();
        parser.parse (urlMapperSQL, args[0], "http://example.com");
    }

    private static void usage() {
        System.out.println("Welcome to the OPS Linkset Loader.");
        System.out.println("This methods requires the file name (incl path) of the linkset to be loaded.");
        System.out.println("Please run this again with a single paramter of the file name");
        System.exit(1);
    }
}
