/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.File;
import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.Reporter;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.mysql.MysqlMapper;
import org.bridgedb.rdf.HoldingRDFStore;
import org.bridgedb.rdf.LinksetValidator;
import org.bridgedb.rdf.RdfLoader;
import org.bridgedb.rdf.RdfStoreType;
import org.bridgedb.rdf.RdfWrapper;
import org.bridgedb.sql.SQLBase;
import org.bridgedb.sql.SqlFactory;
import org.openrdf.OpenRDFException;

/**
 *
 * @author Christian
 */
public class LinksetLoader {
    
    private static void parse(File file, String arg) throws BridgeDbSqlException, IDMapperException {
        if (file.isDirectory()){
            File[] children = file.listFiles();
            for (File child:children){
                parse(child, arg);
            }
        } else {    
            if (arg.equals("load")){
                SQLAccess sqlAccess = SqlFactory.createLoadSQLAccess();
                URLLinkListener listener = new MysqlMapper(sqlAccess);
                RdfLoader rdfLoader = new HoldingRDFStore(RdfStoreType.LOAD, true);
                LinksetHandler handler = new LinksetHandler (listener, rdfLoader);
                handler.parse(file);
                Reporter.report("Loading of " + file.getAbsolutePath() + " successful");
            } else if (arg.equals("test")){
                SQLAccess sqlAccess = SqlFactory.createTestSQLAccess();
                URLLinkListener listener = new MysqlMapper(sqlAccess);
                RdfLoader rdfLoader = new HoldingRDFStore(RdfStoreType.TEST, true);
                LinksetHandler handler = new LinksetHandler (listener, rdfLoader);
                handler.parse(file);
                Reporter.report("Test loading of " + file.getAbsolutePath() + " successful");
            } else if (arg.equals("force")){
                SQLAccess sqlAccess = SqlFactory.createLoadSQLAccess();
                URLLinkListener listener = new MysqlMapper(sqlAccess);
                RdfLoader rdfLoader = new HoldingRDFStore(RdfStoreType.LOAD, false);
                LinksetHandler handler = new LinksetHandler (listener, rdfLoader);
                handler.parse(file);
                Reporter.report("Loading (with limited validation) of " + file.getAbsolutePath() + " successful");
            } else if (arg.equals("testforce")){
                SQLAccess sqlAccess = SqlFactory.createTestSQLAccess();
                URLLinkListener listener = new MysqlMapper(sqlAccess);
                RdfLoader rdfLoader = new HoldingRDFStore(RdfStoreType.TEST, false);
                LinksetHandler handler = new LinksetHandler (listener, rdfLoader);
                handler.parse(file);
                Reporter.report("Test loading (with limited validation) of " + file.getAbsolutePath() + " successful");
            } else if (arg.equals("validate")){
                URLLinkListener listener = new ValidatingLinkListener();
                RdfLoader rdfLoader = new LinksetValidator();
                LinksetHandler handler = new LinksetHandler (listener, rdfLoader);
                handler.parse(file);
                Reporter.report("Validation of " + file.getAbsolutePath() + " successful");
            } else {
                 usage();
            }
        }
    }

    private static void parse (String fileName, String arg) throws IDMapperException  {
        File file = new File(fileName);
        parse(file, arg);
    }
            
    public static void main(String[] args) throws BridgeDbSqlException, IDMapperException {
        if (args.length == 2){
            if (args[1].equals("new")){
                RdfWrapper.clear(RdfStoreType.LOAD);
                Reporter.report("Laod RDF cleared");
                SQLAccess sqlAccess = SqlFactory.createLoadSQLAccess();
                URLLinkListener listener = new MysqlMapper(true, sqlAccess);
                Reporter.report("Load SQL cleared");
                parse(args[0], "load");
            } else if (args[1].equals("testnew")){
                RdfWrapper.clear(RdfStoreType.TEST);
                Reporter.report("Laod RDF cleared");
                SQLAccess sqlAccess = SqlFactory.createTestSQLAccess();
                URLLinkListener listener = new MysqlMapper(true, sqlAccess);
                Reporter.report("Test SQL cleared");
                parse(args[0], "test");
            } else {
                parse(args[0], args[1]);
            }
        } else {
            usage();
        }
    }

    private static void usage() {
        Reporter.report("Welcome to the OPS Linkset Loader.");
        Reporter.report("This methods requires the file name (incl path) of the linkset to be loaded.");
        Reporter.report("Please run this again with two paramters");
        Reporter.report("The file name (including path of the linkset");
        Reporter.report("Either  \"validate\" or \"load\" to pick if the file(s) should be just validated or also loaded.");
        System.exit(1);
    }
}
