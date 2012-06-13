/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.File;
import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.mysql.URLMapperSQL;
import org.bridgedb.sql.SQLBase;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.virtuoso.URLMapperVirtuoso;
import org.openrdf.OpenRDFException;

/**
 *
 * @author Christian
 */
public class TestLoader extends LinksetLoader{
    
    public static void main(String[] args) throws IDMapperException, IOException, OpenRDFException  {
        SQLAccess sqlAccess = SqlFactory.createTestVirtuosoAccess();
        URLMapperVirtuoso urlMapperVirtuoso = new URLMapperVirtuoso(sqlAccess);
        parse (urlMapperVirtuoso, "D:/OpenPhacts/linksets");
        
        //URLMapperSQL urlMapperSQL = new URLMapperSQL(sqlAccess);
        //parse (urlMapperSQL, "D:/OpenPhacts/linksets");
        //URLMapperSQL urlMapperSQL = new URLMapperSQL(true, sqlAccess);
        //clearAndParse(urlMapperSQL, args[0]);
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
