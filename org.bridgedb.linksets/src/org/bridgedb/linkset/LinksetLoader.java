// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.bridgedb.linkset.rdf.RdfFactory;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.tools.metadata.validator.Validator;
import org.bridgedb.uri.UriListener;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.DirectoriesConfig;
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
    
    static final Logger logger = Logger.getLogger(LinksetLoader.class);

    public LinksetLoader() {
    }
    
    @Override
    public String validateString(String source, String info, RDFFormat format, StoreType storeType, 
            ValidationType validationType, boolean includeWarnings) throws BridgeDBException {
        LinksetLoaderImplentation loader = new LinksetLoaderImplentation(source, info, format, validationType, storeType);
        return loader.validityReport(includeWarnings);
    }
    
    @Override
    public String validateInputStream(String source, InputStream inputStream, RDFFormat format, StoreType storeType, 
            ValidationType validationType, boolean includeWarnings) throws BridgeDBException{
        LinksetLoaderImplentation loader = new LinksetLoaderImplentation(source, inputStream, format, validationType, storeType);
        return loader.validityReport(includeWarnings);        
    }
    
    public String validityFile(File file, StoreType storeType, ValidationType validationType, boolean includeWarnings) 
    		throws BridgeDBException {
    	if (!file.exists()) {
    		throw new BridgeDBException("File not found: " + file.getAbsolutePath());
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
    public String validateAddress(String address, StoreType storeType, ValidationType validationType, boolean includeWarnings) 
            throws BridgeDBException {
        if (address == null){
            throw new BridgeDBException("File name may not be null");
        }
        if (address.trim().isEmpty()){
            throw new BridgeDBException("File name may not be empty");
        }
        File file = new File(address.trim());
        if (file.exists()){
            return validityFile(file, storeType, validationType, includeWarnings);
        } else {
            LinksetLoaderImplentation loader = new LinksetLoaderImplentation(address, validationType, storeType);
            return loader.validityReport(includeWarnings);
        }
    }
    
    @Override
    public String loadString(String source, String info, RDFFormat format, StoreType storeType, ValidationType validationType) 
            throws BridgeDBException {
        LinksetLoaderImplentation loader = new LinksetLoaderImplentation(source, info, format, validationType, storeType);
        loader.validate();
        File file = saveString(info, format, validationType);
        loadFile(file, storeType, validationType);
        return "Loaded file " + file.getAbsolutePath();
    }
    
    @Override
    public String saveString(String source, String info, RDFFormat format, StoreType storeType, ValidationType validationType) 
            throws BridgeDBException {
        LinksetLoaderImplentation loader = new LinksetLoaderImplentation(source, info, format, validationType, storeType);
        loader.validate();
        File file = saveString(info, format, validationType);
        if (validationType.isLinkset()){
            logger.warn("Linkset saved to " + file.getAbsolutePath());
            return "Saved linkset to: " + file.getName();
        } else {
            loadFile(file, StoreType.TEST, validationType);            
            logger.warn("Saved void to: " + file.getName() + " Also loaded into test."); 
            return "Saved void to: " + file.getName() + " Void is temporarily available to validated other voids."
                    + " Please ask an admin to make this permenant.";
        }
    }

    private static int loadFile(File file, StoreType storeType, ValidationType validationType) 
    		throws BridgeDBException {
        if (storeType == null){
            throw new BridgeDBException ("Can not load if no storeType set");
        }
    	if (!file.exists()) {
    		throw new BridgeDBException("File not found: " + file.getAbsolutePath());
    	} else if (file.isDirectory()){
            File[] children = file.listFiles();
            int lastMappingsetID = 0;
            for (File child:children){
                lastMappingsetID = loadFile(child, storeType, validationType);
            }
            return lastMappingsetID;
        } else { 
            LinksetLoaderImplentation loader = new LinksetLoaderImplentation(file, validationType, storeType);
            loader.validate();
            return loader.load();
        }
    }

    public static int loadLinkset(String fileName, StoreType storeType, ValidationType validationType, 
            Set<Integer> chainIds) throws BridgeDBException {
        if (storeType == null){
            throw new BridgeDBException ("Can not load if no storeType set");
        }
        File file = new File(fileName);
    	if (!file.exists()) {
    		throw new BridgeDBException("File not found: " + file.getAbsolutePath());
    	} else if (file.isDirectory()){
    		throw new BridgeDBException("File is a directory: " + file.getAbsolutePath());            
        } else { 
            LinksetLoaderImplentation loader = new LinksetLoaderImplentation(file, validationType, storeType);
            loader.validate();
            return loader.linksetLoad(chainIds);
        }
    }

    private int loadURI(String address, StoreType storeType, ValidationType validationType) 
    		throws BridgeDBException {
        if (storeType == null){
            throw new BridgeDBException ("Can not load if no storeType set");
        }
        LinksetLoaderImplentation loader = new LinksetLoaderImplentation(address, validationType, storeType);
        loader.validate();
        return loader.load();
    }

    @Override
    public int load(String path, StoreType storeType, ValidationType validationType) throws BridgeDBException {
        path = path.trim();
        File file;
        try {
            java.net.URL url = new java.net.URL(path);
            RDFFormat format = null;
            return loadURI(path, storeType, validationType);
        } catch (IOException ex) {
            //ok not a uri so try as a file
            //No cleaner way to do this known
            file = new File(path);
            return loadFile(file, storeType, validationType);
        }
    }
    
    @Override
    public String loadInputStream(String source, InputStream inputStream, RDFFormat format, StoreType storeType, 
            ValidationType validationType) throws BridgeDBException{
        File file = saveInputStream(inputStream, format, validationType);
        if (validationType.isLinkset()){
            logger.warn("Linkset saved to " + file.getAbsolutePath());
            return "Saved linkset to: " + file.getName();
        } else {
            loadFile(file, StoreType.TEST, validationType);            
            logger.warn("Saved void to: " + file.getName() + " Also loaded into test."); 
            return "Saved void to: " + file.getName() + " Void is temporarily available to validated other voids."
                    + " Please ask an admin to make this permenant.";
        }
    }

    @Override
    public String saveInputStream(String source, InputStream inputStream, RDFFormat format, StoreType storeType, 
            ValidationType validationType) throws BridgeDBException{
        File file = saveInputStream(inputStream, format, validationType);
        loadFile(file, storeType, validationType);
        return "Loaded file " + file.getAbsolutePath();
    }

    private void validate(File file, StoreType storeType, ValidationType validationType) 
    		throws BridgeDBException {
    	if (!file.exists()) {
    		throw new BridgeDBException("File not found: " + file.getAbsolutePath());
    	} else if (file.isDirectory()){
            File[] children = file.listFiles();
            for (File child:children){
                loadFile(child, storeType, validationType);
            }
        } else { 
            LinksetLoaderImplentation loader = new LinksetLoaderImplentation(file, validationType, storeType);
            loader.validate();
        }
    }

    @Override
    public void checkStringValid(String source, String info, RDFFormat format, StoreType storeType, 
            ValidationType validationType) throws BridgeDBException {
        LinksetLoaderImplentation loader = new LinksetLoaderImplentation(source, info, format, validationType, storeType);
        loader.validate();
    }

    @Override
    public void checkFileValid(String fileName, StoreType storeType, ValidationType type) throws BridgeDBException {
        File file = new File(fileName.trim());
        validate(file, storeType, type);
    }
    
    public void checkInputStreamValid(String source, InputStream inputStream, RDFFormat format, StoreType storeType, 
            ValidationType validationType) throws BridgeDBException{
        LinksetLoaderImplentation loader = new LinksetLoaderImplentation(source, inputStream, format, validationType, storeType);
        loader.validate();        
    }
    
    @Override
    public void clearExistingData (StoreType storeType) 
    		throws BridgeDBException  {
        if (storeType == null){
            throw new BridgeDBException ("unable to clear mapping of unspecified storeType");
        }
        UriPattern.refreshUriPatterns();
        RdfFactory.clear(storeType);
        logger.info(storeType + " RDF cleared");
        UriListener listener = SQLUriMapper.factory(true, storeType);
        logger.info(storeType + " SQL cleared");                
    }

    public static File saveString(String info, RDFFormat format, ValidationType validationType) throws BridgeDBException {
        File directory = getDirectory(validationType);      
        try {
           File file = File.createTempFile(validationType.getName(), "." + format.getDefaultFileExtension(), directory);
           FileWriter writer = new FileWriter(file);
           writer.append(info);
           writer.close();
           logger.info("Saved String to " + file.getAbsolutePath());
           return file;
        } catch (IOException ex) {
            throw new BridgeDBException("Unable to create new file ", ex);
        }
    }

    public static File saveInputStream(InputStream inputStream, RDFFormat format, ValidationType validationType) throws BridgeDBException {
        try {
            File directory = getDirectory(validationType);      
            File file = File.createTempFile(validationType.getName(), "." + format.getDefaultFileExtension(), directory);
            OutputStream out = new FileOutputStream(file);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            inputStream.close();
            out.flush();
            out.close();
            logger.info("Saved InputStream to " + file.getAbsolutePath());
            return file;
        } catch (IOException ex) {
            throw new BridgeDBException("Unable to create new file ", ex);
        }
    }

    public static File getDirectory(ValidationType validationType) throws BridgeDBException{
        if (validationType.isLinkset()){
            return DirectoriesConfig.getLinksetDirectory();
        } else {
            return DirectoriesConfig.getVoidDirectory();
        }
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
    public static void main(String[] args) throws BridgeDBException {
        ConfigReader.logToConsole();
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
            } catch (BridgeDBException ex) {
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
            } catch (BridgeDBException ex) {
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
                linksetLoader.load(fileName, storeType, validationType);
                Reporter.println("Load successful");
            }catch (Exception e){
                Reporter.println(linksetLoader.validateAddress(fileName, storeType, validationType, true));
            }
        } else {
            Reporter.println(linksetLoader.validateAddress(fileName, storeType, validationType, true));
        }       
    }

    public static void usage(String issue) {
        Reporter.println("Welcome to the OPS Linkset Loader.");
        Reporter.println("This method uses a normal paramter and several (optional) named (-D) style parameters");
        Reporter.println("Required Parameter (following the jar) is:");
        Reporter.println("File or Directory to load");
        Reporter.println("   Name (ideally with path) of the file to be loaded.");
        Reporter.println("   Type of file will be dettermined based on the exstension.");
        Reporter.println("   This may also be a directory if all files in it can be loaded. ");
        Reporter.println("      Includes subdirectories with the same requirement of all loadable. ");
        Reporter.println("Optional -D format (before the jar) Parameters are:");
        Reporter.println(LOAD);
        Reporter.println("   true: loads the file into the rdfstore and if links SQL.");
        Reporter.println("        Loads into the database and rdf specified by " + STORE + ".");
        Reporter.println("   default is to append to the existing database and rdf store");
        Reporter.println(STORE);
        Reporter.println("   Dettermines where (if at all) the data will be stored and read from");
        Reporter.println("   " + StoreType.LIVE + ": Writes into the active database and rdf store");
        Reporter.println("   " + StoreType.LOAD + ": Writes into the secondary database and rdf store");
        Reporter.println("       Note: " + StoreType.LOAD + " defaults to " + StoreType.LIVE + " if not set in the config files");
        Reporter.println("   " + StoreType.TEST + ": Writes into the test database and rdf store");
        Reporter.println("       Note: " + StoreType.TEST + " database and rdf store are erased during junit tests.");
        Reporter.println("   Default is " + StoreType.TEST);
        Reporter.println(Validator.VALIDATION);
        Reporter.println("   " + ValidationType.VOID + ": Checks that all MUST and SHOULD values are present");
        Reporter.println("       Multiple datasets and linksets can be declared but links are not expected");
        Reporter.println("   " + ValidationType.LINKS + ": Checks that all MUST and SHOULD values are present");
        Reporter.println("       See: http://www.openphacts.org/specs/datadesc/");
        Reporter.println("   " + ValidationType.LINKSMINIMAL + ": requires only the absolute mininal void to load the data");
        Reporter.println("   " + ValidationType.ANY_RDF + ": requires only that it is valid rdf.");
        Reporter.println("   Default is " + ValidationType.LINKS);
        Reporter.println(CLEAR_EXISING_DATA);
        Reporter.println("   true: clears the exisiting database and rdfstore.");
        Reporter.println("        Only the database and rdf specified by " + STORE + " are cleared.");
        Reporter.println("   default is to append to the existing database and rdf store");
        Reporter.println(issue);
        System.exit(1);
    }

}
