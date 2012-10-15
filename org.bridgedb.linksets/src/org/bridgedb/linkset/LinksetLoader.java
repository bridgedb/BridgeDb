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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.IDMapperException;
import org.bridgedb.metadata.LinksetVoidInformation;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.metadata.validator.ValidationType;
import org.bridgedb.metadata.validator.Validator;
import org.bridgedb.mysql.MySQLSpecific;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.rdf.RDFWriter;
import org.bridgedb.rdf.RdfFactory;
import org.bridgedb.rdf.RdfLoader;
import org.bridgedb.rdf.RdfWrapper;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.url.URLListener;
import org.bridgedb.utils.Reporter;
import org.bridgedb.utils.StoreType;

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
    private static String STORE = "store";
    private static String CLEAR_EXISING_DATA = "clearExistingData";
    
   /**
     * Loads the linkset into existing data
     * @param file Could be either a File or a directory
     * @param arg "load" to add to the load data, "test" to add to the test data
     *     anything else just validates
     * @throws BridgeDbSqlException
     * @throws IDMapperException
     * @throws FileNotFoundException
     */
    private static void parse(File file, StoreType storeType, ValidationType validationType) 
    		throws IDMapperException {
    	if (!file.exists()) {
    		Reporter.report("File not found: " + file.getAbsolutePath());
    		throw new IDMapperLinksetException("File not found: " + file.getAbsolutePath());
    	} else if (file.isDirectory()){
            File[] children = file.listFiles();
            for (File child:children){
                parse(child, storeType, validationType);
            }
        } else { 
            LinksetVoidInformation validator = new LinksetVoidInformation(file, validationType);
            Reporter.report("Validation successful");       
            if (storeType == null){
                return; //Validation done and no loading requested.
            }
            Reporter.report("Started loading " + file.getAbsolutePath());                
            SQLAccess sqlAccess = SqlFactory.createSQLAccess(storeType);
            URLListener listener = new SQLUrlMapper(false, sqlAccess, new MySQLSpecific());
            RdfLoader rdfLoader = new RDFWriter(storeType, validator, listener, CALLER_NAME);
            LinksetHandler handler = new LinksetHandler (rdfLoader);
            handler.parse(file);
            Reporter.report("Loading of " + file.getAbsolutePath() + " successful");
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
    public static void clearExistingData (StoreType storeType) 
    		throws IDMapperException  {
        RdfFactory.clear(storeType);
        Reporter.report(storeType + " RDF cleared");
        SQLAccess sqlAccess = SqlFactory.createSQLAccess(storeType);
        URLListener listener = new SQLUrlMapper(true, sqlAccess, new MySQLSpecific());
        Reporter.report(storeType + " SQL cleared");                
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
    public static void parse (String fileName, StoreType storeType, ValidationType type) throws IDMapperException {
        File file = new File(fileName);
        parse(file, storeType, type);
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
    public static void main(String[] args) throws IDMapperException {
         if (args.length != 1){
            usage("Please specify a file/directory and use -D format for all other arguements.");
        }

        String fileName = args[0];
        if (fileName == null || fileName.isEmpty()){
            usage("Please specify the file or directory top be loaded.");
        }

        String storeString = System.getProperty(STORE);
        StoreType storeType = null;
        if (storeString != null && !storeString.isEmpty()){
            try {
                storeType = StoreType.parseString(storeString);
            } catch (IDMapperException ex) {
                usage(ex.getMessage());
            }
        }

        String validationString = System.getProperty(Validator.VALIDATION);
        ValidationType validationType = null;
        if (validationString == null || validationString.isEmpty()){
            validationType = ValidationType.LINKS;
        } else {
            try {
                validationType = ValidationType.parseString(validationString);
                System.out.println(validationType);
                System.out.println(validationType.isLinkset());
                if (storeString != null && !validationType.isLinkset()){
                    usage(Validator.VALIDATION + " setting " + validationType + " is not supported for loading.");
                }
            } catch (MetaDataException ex) {
                usage(ex.getMessage());
            }
        }

        String clearExistingDataString = System.getProperty(CLEAR_EXISING_DATA, "false");
        boolean clearExistingData = Boolean.valueOf(clearExistingDataString);
        if (clearExistingData){
            if (storeType == null){
                throw new IDMapperException("Unable to " + CLEAR_EXISING_DATA + "if no " + STORE + " specified!");
            }
            clearExistingData(storeType);
        }
        parse (fileName, storeType, validationType);
    }

    public static void usage(String issue) {
        Reporter.report("Welcome to the OPS Linkset Loader.");
        Reporter.report("This method uses a normal paramter and several (optional) named (-D) style parameters");
        Reporter.report("Required Parameter (following the jar) is:");
        Reporter.report("File or Directory to load");
        Reporter.report("   Name (ideally with path) of the file to be loaded.");
        Reporter.report("   Type of file will be dettermined based on the exstension.");
        Reporter.report("   This may also be a directory if all files in it can be loaded. ");
        Reporter.report("      Includes subdirectories with the same requirement of all loadable. ");
        Reporter.report("Optional -D format (before the jar) Parameters are:");
        Reporter.report(STORE);
        Reporter.report("   Dettermines where (if at all) the data will be stored ");
        Reporter.report("   " + StoreType.LIVE + ": Writes into the active database and rdf store");
        Reporter.report("   " + StoreType.LOAD + ": Writes into the secondary database and rdf store");
        Reporter.report("       Note: " + StoreType.LOAD + " defaults to " + StoreType.LIVE + " if not set in the config files");
        Reporter.report("   " + StoreType.TEST + ": Writes into the test database and rdf store");
        Reporter.report("       Note: " + StoreType.TEST + " database and rdf store are erased during junit tests.");
        Reporter.report("   Default is to Validate only.");
        Reporter.report(Validator.VALIDATION);
        Reporter.report("   " + ValidationType.LINKS + ": Checks that all MUST and SHOULD values are present");
        Reporter.report("       See: http://www.openphacts.org/specs/datadesc/");
        Reporter.report("   " + ValidationType.LINKSMINIMAL + ": requires only the absolute mininal void to load the data");
        Reporter.report("   Default is " + ValidationType.LINKS);
        Reporter.report(CLEAR_EXISING_DATA);
        Reporter.report("   true: clears the exisiting database and rdfstore.");
        Reporter.report("        Only the database and rdf specified by " + STORE + " are cleared.");
        Reporter.report("   default is to append to the existing database and rdf store");
        Reporter.report(issue);
        System.exit(1);
    }
}
