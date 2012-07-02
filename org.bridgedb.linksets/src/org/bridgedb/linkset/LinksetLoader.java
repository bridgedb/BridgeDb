/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.File;
import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.mysql.MysqlMapper;
import org.bridgedb.rdf.HoldingRDFStore;
import org.bridgedb.rdf.LinksetValidator;
import org.bridgedb.rdf.RdfLoader;
import org.bridgedb.rdf.RdfStoreType;
import org.bridgedb.sql.SQLBase;
import org.bridgedb.sql.SqlFactory;
import org.openrdf.OpenRDFException;

/**
 *
 * @author Christian
 */
public class LinksetLoader {
    
    public static void main(String[] args) throws BridgeDbSqlException, IDMapperLinksetException {
        if (args.length == 2){
            if (args[1].equals("load")){
                SQLAccess sqlAccess = SqlFactory.createLoadSQLAccess();
                URLLinkListener listener = new MysqlMapper(sqlAccess);
                RdfLoader rdfLoader = new HoldingRDFStore(RdfStoreType.LOAD, true, false);
                LinksetHandler handler = new LinksetHandler (listener, rdfLoader);
                handler.parse (args[0]);
                System.out.println("Loading of " + args[0] + " successful");
            } else if (args[1].equals("test")){
                SQLAccess sqlAccess = SqlFactory.createTestSQLAccess();
                URLLinkListener listener = new MysqlMapper(sqlAccess);
                RdfLoader rdfLoader = new HoldingRDFStore(RdfStoreType.TEST, true, false);
                LinksetHandler handler = new LinksetHandler (listener, rdfLoader);
                handler.parse (args[0]);
                System.out.println("Loading of " + args[0] + " successful");
            } else if (args[1].equals("force")){
                SQLAccess sqlAccess = SqlFactory.createLoadSQLAccess();
                URLLinkListener listener = new MysqlMapper(sqlAccess);
                RdfLoader rdfLoader = new HoldingRDFStore(RdfStoreType.LOAD, false, false);
                LinksetHandler handler = new LinksetHandler (listener, rdfLoader);
                handler.parse (args[0]);
                System.out.println("Loading of " + args[0] + " successful");
            } else if (args[1].equals("testforce")){
                SQLAccess sqlAccess = SqlFactory.createTestSQLAccess();
                URLLinkListener listener = new MysqlMapper(sqlAccess);
                RdfLoader rdfLoader = new HoldingRDFStore(RdfStoreType.TEST, false, false);
                LinksetHandler handler = new LinksetHandler (listener, rdfLoader);
                handler.parse (args[0]);
                System.out.println("Loading of " + args[0] + " successful");
            } else if (args[1].equals("validate")){
                URLLinkListener listener = new ValidatingLinkListener();
                RdfLoader rdfLoader = new LinksetValidator();
                LinksetHandler handler = new LinksetHandler (listener, rdfLoader);
                handler.parse (args[0]);
                System.out.println("validation of " + args[0] + " successful");
            } else if (args[1].equals("new")){
                SQLAccess sqlAccess = SqlFactory.createLoadSQLAccess();
                URLLinkListener listener = new MysqlMapper(true, sqlAccess);
                RdfLoader rdfLoader = new HoldingRDFStore(RdfStoreType.LOAD, true, true);
                System.out.println("rdf clear");
                LinksetHandler handler = new LinksetHandler (listener, rdfLoader);
                handler.parse (args[0]);
                System.out.println("Clear Loading of " + args[0] + " successful");
            } else if (args[1].equals("forcenew")){
                SQLAccess sqlAccess = SqlFactory.createLoadSQLAccess();
                URLLinkListener listener = new MysqlMapper(true, sqlAccess);
                RdfLoader rdfLoader = new HoldingRDFStore(RdfStoreType.LOAD, false, true);
                System.out.println("rdf clear");
                LinksetHandler handler = new LinksetHandler (listener, rdfLoader);
                handler.parse (args[0]);
                System.out.println("Clear Loading of " + args[0] + " successful");
            } else if (args[1].equals("testnew")){
                SQLAccess sqlAccess = SqlFactory.createTestSQLAccess();
                URLLinkListener listener = new MysqlMapper(true, sqlAccess);
                RdfLoader rdfLoader = new HoldingRDFStore(RdfStoreType.TEST, true, true);
                System.out.println("rdf clear");
                LinksetHandler handler = new LinksetHandler (listener, rdfLoader);
                handler.parse (args[0]);
                System.out.println("Clear Loading of " + args[0] + " successful");
            } else {
                usage();
            }
        } else {
            usage();
        }
    }

    private static void usage() {
        System.out.println("Welcome to the OPS Linkset Loader.");
        System.out.println("This methods requires the file name (incl path) of the linkset to be loaded.");
        System.out.println("Please run this again with two paramters");
        System.out.println("The file name (including path of the linkset");
        System.out.println("Either  \"validate\" or \"load\" to pick if the file(s) should be just validated or also loaded.");
        System.exit(1);
    }
}
