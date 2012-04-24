/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.File;
import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.rdf.RdfFactory;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.URLMapperSQL;
import org.bridgedb.sql.SqlFactory;
import org.openrdf.OpenRDFException;

/**
 *
 * @author Christian
 */
public class LinksetLoader {
    
   	public static void main(String[] args) throws IDMapperException, IOException, OpenRDFException  {
        SQLAccess sqlAccess = SqlFactory.createSQLAccess();
        URLMapperSQL urlMapperSQL = null;
        if (args.length == 1){
            urlMapperSQL = new URLMapperSQL(sqlAccess);
            LinksetHandler.parse (urlMapperSQL, args[0]);
        } else if (args.length == 2){
            if (args[2].equals("new")){
                urlMapperSQL = new URLMapperSQL(true, sqlAccess);
                LinksetHandler.clearAndParse(urlMapperSQL, args[0]);
        } else {
                usage();
            }
        } else {
            usage();
        }
       LinksetHandler.parse (urlMapperSQL, args[0]);
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
