/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.mapping.MappingListener;
import org.bridgedb.mysql.MySQLSpecific;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.rdf.RDFWriter;
import org.bridgedb.rdf.RDFValidator;
import org.bridgedb.rdf.RdfLoader;
import org.bridgedb.rdf.RdfStoreType;
import org.bridgedb.rdf.RdfWrapper;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.url.URLListener;
import org.bridgedb.utils.Reporter;
import org.openrdf.OpenRDFException;

/**
 *
 * @author Christian
 */
public class LinksetLoader {
    
    private static String CALLER_NAME = "org.bridgedb.linkset.LinksetLoader" ;
    
    LinksetLoader() {
    	
    }
    
    private static void parse(File file, String arg) 
    		throws BridgeDbSqlException, IDMapperException, FileNotFoundException {
    	if (!file.exists()) {
    		Reporter.report("File not found: " + file.getAbsolutePath());
    		throw new FileNotFoundException();
    	} else if (file.isDirectory()){
            File[] children = file.listFiles();
            for (File child:children){
                parse(child, arg);
            }
        } else {    
            RDFValidator validator = new RDFValidator(true);
            LinksetHandler handler = new LinksetHandler(validator);
            handler.parse(file);
            Reporter.report("Validation successful");                
            if (arg.equals("load")){
                Reporter.report("Started loading " + file.getAbsolutePath());                
                SQLAccess sqlAccess = SqlFactory.createLoadSQLAccess();
                URLListener listener = new SQLUrlMapper(false, sqlAccess, new MySQLSpecific());
                RdfLoader rdfLoader = new RDFWriter(RdfStoreType.LOAD, validator, listener, CALLER_NAME);
                handler = new LinksetHandler (rdfLoader);
                handler.parse(file);
                Reporter.report("Loading of " + file.getAbsolutePath() + " successful");
            } else if (arg.equals("test")){
                Reporter.report("Started test loading " + file.getAbsolutePath());                
                SQLAccess sqlAccess = SqlFactory.createTestSQLAccess();
                URLListener listener = new SQLUrlMapper(false, sqlAccess, new MySQLSpecific());
                RdfLoader rdfLoader = new RDFWriter(RdfStoreType.TEST, validator, listener, CALLER_NAME);
                handler = new LinksetHandler (rdfLoader);
                handler.parse(file);
                Reporter.report("Loading of " + file.getAbsolutePath() + " successful");
            } else if (arg.equals("validate")){
                //Already validated.
            } else {
                 usage();
            }
        }
    }

    void parse (String fileName, String arg) 
    		throws IDMapperException, FileNotFoundException  {
        File file = new File(fileName);
        parse(file, arg);
    }
            
    public static void main(String[] args) throws BridgeDbSqlException, IDMapperException {
    	try {
    		if (args.length == 2){
    			LinksetLoader loader = new LinksetLoader();
    			if (args[1].equals("new")){
    				RdfWrapper.clear(RdfStoreType.LOAD);
    				Reporter.report("Laod RDF cleared");
    				SQLAccess sqlAccess = SqlFactory.createLoadSQLAccess();
    				URLListener listener = new SQLUrlMapper(true, sqlAccess, new MySQLSpecific());
    				Reporter.report("Load SQL cleared");                
    				loader.parse(args[0], "load");
    			} else if (args[1].equals("testnew")){
    				RdfWrapper.clear(RdfStoreType.TEST);
    				Reporter.report("Laod RDF cleared");
    				SQLAccess sqlAccess = SqlFactory.createTestSQLAccess();
    				URLListener listener = new SQLUrlMapper(true, sqlAccess, new MySQLSpecific());
    				Reporter.report("Test SQL cleared");
    				loader.parse(args[0], "test");
    			} else {
    				loader.parse(args[0], args[1]);
    			}
    		} else {
    			usage();
    		}
    	} catch (FileNotFoundException e) {
    		System.exit(1);
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
