/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

import java.io.File;
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
        SQLAccess sqlAccess = URLSqlFactory.createSQLAccess();
        URLMapperSQL urlMapperSQL = null;
        if (args.length == 2){
             urlMapperSQL = new URLMapperSQL(sqlAccess);
        } else if (args.length == 3){
            if (args[2].equals("new")){
                urlMapperSQL = new URLMapperSQL(true, sqlAccess);
            } else {
                usage();
            }
        } else {
            usage();
        }
        LinksetParser parser = new LinksetParser();
        System.out.println("reading " + args[0]);
        File test = new File(args[0]);
        System.out.println(test.exists());
        parser.parse (urlMapperSQL, args[0], args[1]);
    }

    private static void usage() {
        System.out.println("Welcome to the OPS Linkset Loader.");
        System.out.println("This methods requires the file name (incl path) of the linkset to be loaded.");
        System.out.println("Please run this again with two paramters");
        System.out.println("The file name (including path of the linkset");
        System.out.println("The base uri for any ids without a base URI.");
        System.exit(1);
    }
}
