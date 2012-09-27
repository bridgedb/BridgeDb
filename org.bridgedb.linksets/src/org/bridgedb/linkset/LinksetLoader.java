// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.linkset;

import java.io.File;
import java.io.FileNotFoundException;
import org.bridgedb.IDMapperException;
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

/**
 * Main class for loading linksets.
 *
 * The Main method can parse and either input or validate a linkset.
 *
 * @see usage() for a description of the paramters.
 * @author Christian
 */
public class LinksetLoader {
    
    private static String CALLER_NAME = "org.bridgedb.linkset.LinksetLoader" ;

    /**
     * Constructor for main and test classes
     */
    public LinksetLoader() {
    }


    /**
     * Loads the linkset into existing data
     * @param file Could be either a File or a directory
     * @param arg "load" to add to the load data, "test" to add to the test data
     *     anything else just validates
     * @throws BridgeDbSqlException
     * @throws IDMapperException
     * @throws FileNotFoundException
     */
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

    /**
     * Converts the fileName into a file and then calls parse
     * Should only be called by main() or tests
     *
     * @param fileName name of a file or directory
     * @param arg "load" to add to the load data, "test" to add to the test data
     *     anything else just validates
     * @throws IDMapperException
     * @throws FileNotFoundException
     */
    public void parse (String fileName, String arg) 
    		throws IDMapperException, FileNotFoundException  {
        File file = new File(fileName);
        parse(file, arg);
    }

    /**
     * Main entry poit for loading linksets.
     *
     * @seem usage() fr explanation of the paramters.
     *
     * Unpublisted second arguements of "new" and "testnew" will cause the clearing of all existing data.
     * These will also recreate the database and rdf store.
     * USE WITH CAUTION previous data can not be recovered!
     *
     * @param args
     * @throws BridgeDbSqlException
     */
    public static void main(String[] args) 
            throws BridgeDbSqlException, IDMapperLinksetException, IDMapperException, FileNotFoundException {
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
    }

    private static void usage() {
        Reporter.report("Welcome to the OPS Linkset Loader.");
        Reporter.report("This methods requires the file name (incl path) " +
        		"of the linkset to be loaded.");
        Reporter.report("Please run this again with two paramters");
        Reporter.report("The file name (including path of the linkset");
        Reporter.report("Either  \"validate\" or \"load\" to pick if the " +
        		"file(s) should be just validated or also loaded.");
        System.exit(1);
    }
}
