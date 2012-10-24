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
import org.bridgedb.IDMapperException;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.metadata.validator.ValidationType;
import org.bridgedb.metadata.validator.Validator;
import org.bridgedb.mysql.MySQLSpecific;
import org.bridgedb.rdf.IDMapperLinksetException;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.rdf.RdfFactory;
import org.bridgedb.rdf.StatementReader;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.url.URLListener;
import org.bridgedb.utils.Reporter;
import org.bridgedb.utils.StoreType;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFFormat;

/**
 * Main class for loading linksets.
 *
 * The Main method can parse and either input or validate a linkset.
 *
 * @see usage() for a description of the paramters.
 * @author Christian
 */
public class LinksetLoader implements LinksetInterface{
        
    private static String LAST_USED_VOID_ID = "LastUsedVoidId";
    private static String STORE = "store";
    private static String CLEAR_EXISING_DATA = "clearExistingData";
    private static String LOAD = "load";
    private static URI ACCESSED_FROM_NOT_REQUIRED = null;
    private static boolean INCLUDE_WARNINGS = true;
    
    public LinksetLoader() {
    }
    
    @Override
    public String validateString(String info, RDFFormat format, StoreType storeType, 
            ValidationType validationType, boolean includeWarnings) throws IDMapperException {
        LinksetLoaderImplentation loader = new LinksetLoaderImplentation(info, format, validationType, storeType);
        return loader.validityReport(includeWarnings);
    }

    @Override
    public String validateStringAsDatasetVoid(String info, String mimeType) throws IDMapperException {
        RDFFormat format = StatementReader.getRDFFormatByMimeType(mimeType);
        return validateString(info, format, StoreType.LIVE, ValidationType.DATASETVOID, INCLUDE_WARNINGS);
    }
    
    @Override
    public String validateStringAsLinksetVoid(String info, String mimeType) throws IDMapperException {
        RDFFormat format = StatementReader.getRDFFormatByMimeType(mimeType);
        return validateString(info, format, StoreType.LIVE, ValidationType.LINKSETVOID, INCLUDE_WARNINGS);
    }
    
    @Override
    public String validateStringAsLinks(String info, String mimeType) throws IDMapperException {
        RDFFormat format = StatementReader.getRDFFormatByMimeType(mimeType);
        return validateString(info, format, StoreType.LIVE, ValidationType.LINKS, INCLUDE_WARNINGS);
    }

    private String validityFile(File file, StoreType storeType, ValidationType validationType, boolean includeWarnings) 
    		throws IDMapperException {
    	if (!file.exists()) {
    		Reporter.report("File not found: " + file.getAbsolutePath());
    		throw new IDMapperLinksetException("File not found: " + file.getAbsolutePath());
    	} else if (file.isDirectory()){
            StringBuilder builder = new StringBuilder();
            File[] children = file.listFiles();
            for (File child:children){
                builder.append("Report for ");
                builder.append(child.getAbsoluteFile());
                builder.append("\n");
                builder.append(validityFile(child, storeType, validationType, includeWarnings));
                builder.append("\n");
            }
            return builder.toString();
        } else { 
            LinksetLoaderImplentation loader = new LinksetLoaderImplentation(file, validationType, storeType);
            return loader.validityReport(includeWarnings);
        }
    }

    @Override
    public String validateFile(String fileName, StoreType storeType, ValidationType type, boolean includeWarnings) 
            throws IDMapperException {
        if (fileName == null){
            throw new IDMapperException("File name may not be null");
        }
        if (fileName.trim().isEmpty()){
            throw new IDMapperException("File name may not be empty");
        }
        File file = new File(fileName);
        return validityFile(file, storeType, type, includeWarnings);
    }
    
    @Override
    public String validateFileAsDatasetVoid(String fileName) throws IDMapperException {
        return validateFile(fileName, StoreType.LIVE, ValidationType.DATASETVOID, INCLUDE_WARNINGS);
    }
    
    @Override
    public String validateFileAsLinksetVoid(String fileName) throws IDMapperException {
        return validateFile(fileName, StoreType.LIVE, ValidationType.LINKSETVOID, INCLUDE_WARNINGS);
    }
    
    @Override
    public String validateFileAsLinks(String fileName) throws IDMapperException {
        return validateFile(fileName, StoreType.LIVE, ValidationType.LINKS, INCLUDE_WARNINGS);
    }

    @Override
    public void loadString(String info, RDFFormat format, StoreType storeType, ValidationType validationType) 
            throws IDMapperException {
        LinksetLoaderImplentation loader = new LinksetLoaderImplentation(info, format, validationType, storeType);
        loader.validate();
        loader.load();
    }
    
    private void load(File file, StoreType storeType, ValidationType validationType) 
    		throws IDMapperException {
        if (storeType == null){
            throw new IDMapperException ("Can not load if no storeType set");
        }
    	if (!file.exists()) {
    		Reporter.report("File not found: " + file.getAbsolutePath());
    		throw new IDMapperLinksetException("File not found: " + file.getAbsolutePath());
    	} else if (file.isDirectory()){
            File[] children = file.listFiles();
            for (File child:children){
                load(child, storeType, validationType);
            }
        } else { 
            LinksetLoaderImplentation loader = new LinksetLoaderImplentation(file, validationType, storeType);
            loader.validate();
            loader.load();
        }
    }

    @Override
    public void loadFile(String fileName, StoreType storeType, ValidationType type) throws IDMapperException {
        File file = new File(fileName);
        load(file, storeType, type);
    }
    
    private void validate(File file, StoreType storeType, ValidationType validationType) 
    		throws IDMapperException {
    	if (!file.exists()) {
    		Reporter.report("File not found: " + file.getAbsolutePath());
    		throw new IDMapperLinksetException("File not found: " + file.getAbsolutePath());
    	} else if (file.isDirectory()){
            File[] children = file.listFiles();
            for (File child:children){
                load(child, storeType, validationType);
            }
        } else { 
            LinksetLoaderImplentation loader = new LinksetLoaderImplentation(file, validationType, storeType);
            loader.validate();
        }
    }

    @Override
    public void checkStringValid(String info, RDFFormat format, StoreType storeType, 
            ValidationType validationType) throws IDMapperException {
        LinksetLoaderImplentation loader = new LinksetLoaderImplentation(info, format, validationType, storeType);
        loader.validate();
    }

    @Override
    public void checkFileValid(String fileName, StoreType storeType, ValidationType type) throws IDMapperException {
        File file = new File(fileName);
        validate(file, storeType, type);
    }
    
    @Override
    public void clearExistingData (StoreType storeType) 
    		throws IDMapperException  {
        if (storeType == null){
            throw new IDMapperException ("unable to clear mapping of unspecified storeType");
        }
        RdfFactory.clear(storeType);
        Reporter.report(storeType + " RDF cleared");
        SQLAccess sqlAccess = SqlFactory.createSQLAccess(storeType);
        URLListener listener = new SQLUrlMapper(true, sqlAccess, new MySQLSpecific());
        Reporter.report(storeType + " SQL cleared");                
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
        StoreType storeType = StoreType.TEST;
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
                if (storeString != null && !validationType.isLinkset()){
                    usage(Validator.VALIDATION + " setting " + validationType + " is not supported for loading.");
                }
            } catch (MetaDataException ex) {
                usage(ex.getMessage());
            }
        }

        LinksetLoader linksetLoader = new LinksetLoader();
        
        String clearExistingDataString = System.getProperty(CLEAR_EXISING_DATA, "false");
        boolean clearExistingData = Boolean.valueOf(clearExistingDataString);
        if (clearExistingData){
            linksetLoader.clearExistingData(storeType);
        }

        String loadString = System.getProperty(LOAD, "true");
        boolean load = Boolean.valueOf(loadString);
        
        if (load){
            try{
                linksetLoader.loadFile(fileName, storeType, validationType);
                Reporter.report("Load successful");
            }catch (Exception e){
                Reporter.report (linksetLoader.validateFile(fileName, storeType, validationType, true));
            }
        } else {
            Reporter.report (linksetLoader.validateFile(fileName, storeType, validationType, true));
        }       
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
        Reporter.report(LOAD);
        Reporter.report("   true: loads the file into the rdfstore and if links SQL.");
        Reporter.report("        Loads into the database and rdf specified by " + STORE + ".");
        Reporter.report("   default is to append to the existing database and rdf store");
        Reporter.report(STORE);
        Reporter.report("   Dettermines where (if at all) the data will be stored and read from");
        Reporter.report("   " + StoreType.LIVE + ": Writes into the active database and rdf store");
        Reporter.report("   " + StoreType.LOAD + ": Writes into the secondary database and rdf store");
        Reporter.report("       Note: " + StoreType.LOAD + " defaults to " + StoreType.LIVE + " if not set in the config files");
        Reporter.report("   " + StoreType.TEST + ": Writes into the test database and rdf store");
        Reporter.report("       Note: " + StoreType.TEST + " database and rdf store are erased during junit tests.");
        Reporter.report("   Default is " + StoreType.TEST);
        Reporter.report(Validator.VALIDATION);
        Reporter.report("   " + ValidationType.DATASETVOID + ": Checks that all MUST and SHOULD values are present");
        Reporter.report("       Multiple datasets can be declared but linksets and links are not expected");
        Reporter.report("   " + ValidationType.LINKSETVOID + ": Checks that all MUST and SHOULD values are present");
        Reporter.report("       Multiple Linksets can be declared but links are not expected");
        Reporter.report("       Included Datasets are validated to Linkset \"Minimal Dataset Description\".");
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
