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
package org.bridgedb.rdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.openrdf.sail.nativerdf.NativeStore;

/**
 *
 * @author Christian
 */
public class RepositoryFactory {
    public static final String CONFIG_FILE_PATH_PROPERTY = "ConfigPath";
    public static final String CONFIG_FILE_PATH_SOURCE_PROPERTY = "ConfigPathSource";
    public static final String SAIL_NATIVE_STORE_PROPERTY = "SailNativeStore";
    public static final String LOAD_SAIL_NATIVE_STORE_PROPERTY = "LoadSailNativeStore";
    public static final String TEST_SAIL_NATIVE_STORE_PROPERTY = "TestSailNativeStore";
    public static final String BASE_URI_PROPERTY = "BaseURI";
    private static final Resource[] ALL_RESOURCES = new Resource[0];
    public static final String CONFIG_FILE_NAME = "rdfConfig.txt";
    private static final Resource ANY_SUBJECT = null;
    private static final URI ANY_PREDICATE = null;
    private static final Value ANY_OBJECT = null;
    private static final boolean MUST_ALREADY_EXIST = true;

    private static final String NO_CONFIG_FILE = "No config file found";
    private static String path;
    
    private static Properties properties;

    public synchronized static void clear(RdfStoreType type) throws RdfException{
        WrappedRepository repository = getRepository(type, false);
        repository.clearAndClose();
     }

    /**
     * Calling methods have a STRONG obligation to shut down the Repository! Even on Exceptions!
     * 
     * @param rdfStoreType
     * @return
     * @throws IDMapperLinksetException 
     */
    public static WrappedRepository getRepository(RdfStoreType rdfStoreType) throws RdfException {
        return getRepository(rdfStoreType, MUST_ALREADY_EXIST);
    }
    
    /**
     * Calling methods have a STRONG obligation to shut down the Repository! Even on Exceptions!
     * 
     * @param rdfStoreType
     * @return
     * @throws IDMapperLinksetException 
     */
    public static WrappedRepository getRepository(RdfStoreType rdfStoreType, boolean exisiting) throws RdfException {
        File dataDir = getDataDir(rdfStoreType);
        if (exisiting) {
            if (!dataDir.exists()){
                throw new RdfException ("Please check RDF settings File " + dataDir + " does not exist");
            }
            if (!dataDir.isDirectory()){
               throw new RdfException ("Please check RDF settings File " + dataDir + " is not a directory");
            }
        }
        Repository repository = new SailRepository(new NativeStore(dataDir));
        return new WrappedRepository(repository);
    }

    private static File getDataDir(RdfStoreType rdfStoreType) throws RdfException {
        switch (rdfStoreType){
            case MAIN: 
                return new File(getSailNativeStore());
            case LOAD:
                return new File(getLoadSailNativeStore());
            case TEST:
                return new File(getTestSailNativeStore());
             default:
                throw new RdfException ("Unepected RdfStoreType " + rdfStoreType);
        }
    }

    public static String configFilePath(){
        try {
            return getProperties().getProperty(CONFIG_FILE_PATH_PROPERTY);
        } catch (IOException ex) {
            return ex.getMessage();
        }
    }
    
    public static String configSource(){
        try {
            return getProperties().getProperty(CONFIG_FILE_PATH_PROPERTY);
        } catch (IOException ex) {
           return ex.getMessage();
        }
    }

    public static String getSailNativeStore(){
        String result;
        try {
            result = getProperties().getProperty(SAIL_NATIVE_STORE_PROPERTY);
        } catch (IOException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return "../rdf/linksets";
    }

    public static String getLoadSailNativeStore(){
        String result;
        try {
            result = getProperties().getProperty(LOAD_SAIL_NATIVE_STORE_PROPERTY);
        } catch (IOException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return getSailNativeStore();
    }

    public static String getTestSailNativeStore(){
        String result;
        try {
            result = getProperties().getProperty(TEST_SAIL_NATIVE_STORE_PROPERTY);
        } catch (IOException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return "../rdf/testLinksets";
    }

    public static String getBaseURI() {
        String result;
        try {
            result = getProperties().getProperty(BASE_URI_PROPERTY);
        } catch (IOException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return "http://openphacts.cs.man.ac.uk:9090/OPS-IMS/";        
    }
    

    public static URI getLinksetURL(Value linksetId){
        return new URIImpl(RepositoryFactory.getBaseURI() + "/linkset/" + linksetId.stringValue());  
    }
    
    public static URI getLinksetURL(int linksetId){
        return new URIImpl(RepositoryFactory.getBaseURI() + "/linkset/" + linksetId);  
    }
  
    static String getRDF(RdfStoreType rdfStoreType, int linksetId) throws RdfException {
        WrappedRepository repository = getRepository(rdfStoreType, true);
        StringOutputStream stringOutputStream = new StringOutputStream();            
        RDFXMLWriter writer = new RDFXMLWriter(stringOutputStream);
        writer.startRDF();
        Resource linkSetGraph = getLinksetURL(linksetId);
        List<Statement> statements = repository.getStatementList(ANY_SUBJECT, ANY_PREDICATE, ANY_OBJECT, linkSetGraph);
        for (Statement statement:statements){
            try {
                writer.handleStatement(statement);
            } catch (RDFHandlerException ex) {
                throw new RdfException ("Unable to write statement " + statement, ex);
            }
        }
        return stringOutputStream.toString();
    }
 
    private static Properties getProperties() throws IOException{
        if (properties == null){
            properties = new Properties();
            load();
        }
        return properties;
    }
    
    /** 
     * Loads the config file looks in various places but always stopping when it is found.
     * Any farther config files in other locations are then ignored.
     * <p>
     * Sets the CONFIG_FILE_PATH_PROPERTY and CONFIG_FILE_PATH_SOURCE_PROPERTY.
     * <p>
     * Search order is
     * <ul>
     * <li>@See loadByEnviromentVariable()
     * <li>@See loadDirectly()
     * <li>@loadFromResources()
     * <li>@loadFromSqlConfigs()
     * <li>loadFromSqlResources()
     * </ul>
     * @throws IOException 
     */
    private static void load() throws IOException{
        if (loadByEnviromentVariable()) return;
        if (loadByCatalinaHomeConfigs()) return;
        if (loadDirectly()) return;
        if (loadFromConfigs()) return;
        if (loadFromParentConfigs()) return;
        properties.put(CONFIG_FILE_PATH_PROPERTY, NO_CONFIG_FILE) ;
        properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, NO_CONFIG_FILE);
    }
    
    /**
     * Looks for the config file in the directory set up the environment variable "OPS-IMS-CONFIG"
     * @return True if the config files was found. False if the environment variable "OPS-IMS-CONFIG" was unset.
     * @throws IOException Thrown if the environment variable is not null, 
     *    and the config file is not found as indicated, or could not be read.
     */
    private static boolean loadByEnviromentVariable() throws IOException {
        String envPath = System.getenv().get("OPS-IMS-CONFIG");
        if (envPath == null || envPath.isEmpty()) return false;
        File envDir = new File(envPath);
        if (!envDir.exists()){
            String error = "Environment Variable OPS-IMS-CONFIG points to " + envPath + 
                    " but no directory found there";
            properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, error) ;
            throw new FileNotFoundException (error);
        }
        if (envDir.isDirectory()){
            File envFile = new File(envDir, CONFIG_FILE_NAME);
            if (!envFile.exists()){
                String error = "Environment Variable OPS-IMS-CONFIG points to " + envPath + 
                        " but no " + CONFIG_FILE_NAME + " file found there";
                properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, error) ;
                throw new FileNotFoundException (error);
            }
            FileInputStream configs = new FileInputStream(envFile);
            properties.load(configs);
            properties.put(CONFIG_FILE_PATH_PROPERTY, envFile.getAbsolutePath());
            properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, "OPS-IMS-CONFIG Enviroment Variable");
            return true;
        } else {
            String error = "Environment Variable OPS-IMS-CONFIG points to " + envPath + 
                    " but is not a directory";
            properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, error) ;
            throw new FileNotFoundException (error);
        }
    }

    /**
     * Looks for the config file in the directory set up the environment variable "OPS-IMS-CONFIG"
     * @return True if the config files was found. False if the environment variable "OPS-IMS-CONFIG" was unset.
     * @throws IOException Thrown if the environment variable is not null, 
     *    and the config file is not found as indicated, or could not be read.
     */
    private static boolean loadByCatalinaHomeConfigs() throws IOException {
        String catalinaHomePath = System.getenv().get("CATALINA_HOME");
         if (catalinaHomePath == null || catalinaHomePath.isEmpty()) return false;
        File catalineHomeDir = new File(catalinaHomePath);
        if (!catalineHomeDir.exists()){
            String error = "Environment Variable CATALINA_HOME points to " + catalinaHomePath + 
                    " but no directory found there";
            properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, error) ;
            throw new FileNotFoundException (error);
        }
        if (!catalineHomeDir.isDirectory()){
            String error = "Environment Variable CATALINA_HOME points to " + catalinaHomePath + 
                    " but is not a directory";
            properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, error) ;
            throw new FileNotFoundException (error);
        }
        File envDir = new File (catalineHomeDir + "/conf/OPS-IMS");
        if (!envDir.exists()) return false; //No hard requirements that catalineHome has a /conf/OPS-IMS
         if (envDir.isDirectory()){
            File envFile = new File(envDir, CONFIG_FILE_NAME);
            if (!envFile.exists()){
                String error = "Environment Variable CATALINA_HOME points to " + catalinaHomePath + 
                        " but subdirectory /conf/OPS-IMS has no " + CONFIG_FILE_NAME + " file.";
                properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, error) ;
                throw new FileNotFoundException (error);
            }
            FileInputStream configs = new FileInputStream(envFile);
            properties.load(configs);
            properties.put(CONFIG_FILE_PATH_PROPERTY, envFile.getAbsolutePath());
            properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, "CATALINA_HOME/conf/OPS-IMS");
            return true;
        } else {
            String error = "Environment Variable CATALINA_HOME points to " + catalinaHomePath  + 
                    " but $CATALINA_HOME/conf/OPS-IMS is not a directory";
            properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, error) ;
            throw new FileNotFoundException (error);
       }
    }

    /**
     * Looks for the config file in the run directory.
     * @return True if the file was found, False if it was not found.
     * @throws IOException If there is an error reading the file.
     */
    private static boolean loadDirectly() throws IOException {
        File envFile = new File(CONFIG_FILE_NAME);
        if (!envFile.exists()) return false;
        FileInputStream configs = new FileInputStream(envFile);
        properties.load(configs);
        properties.put(CONFIG_FILE_PATH_PROPERTY, envFile.getAbsolutePath());
        properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, "From main Directory");
        return true;
    }

    /**
     * Looks for the config file in the conf/OPS-IMS sub directories of the run directory.
     * <p>
     * For tomcat conf would then be a sister directory of webapps.
     * @return True if the file was found, False if it was not found.
     * @throws IOException If there is an error reading the file.
     */
    private static boolean loadFromConfigs() throws IOException {
        File confFolder = new File ("conf/OPS-IMS");
        if (!confFolder.exists()) return false;
        if (!confFolder.isDirectory()){
            String error = "Expected " + confFolder.getAbsolutePath() + " to be a directory";
            properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, error) ;
            throw new FileNotFoundException (error);
        }
        File envFile = new File(confFolder, CONFIG_FILE_NAME);
        if (!envFile.exists()) return false;
        FileInputStream configs = new FileInputStream(envFile);
        properties.load(configs);
        properties.put(CONFIG_FILE_PATH_PROPERTY, envFile.getAbsolutePath());
        properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, "From conf/OPS-IMS");
        return true;
    }

    /**
     * Looks for the config file in the conf/OPS-IMS sub directories of the run directory.
     * <p>
     * For tomcat conf would then be a sister directory of webapps.
     * @return True if the file was found, False if it was not found.
     * @throws IOException If there is an error reading the file.
     */
    private static boolean loadFromParentConfigs() throws IOException {
        File confFolder = new File ("../conf/OPS-IMS");
        if (!confFolder.exists()) return false;
        if (!confFolder.isDirectory()){
            String error = "Expected " + confFolder.getAbsolutePath() + " to be a directory";
            properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, error) ;
            throw new FileNotFoundException (error);
        }
        File envFile = new File(confFolder, CONFIG_FILE_NAME);
        if (!envFile.exists()) return false;
        FileInputStream configs = new FileInputStream(envFile);
        properties.load(configs);
        properties.put(CONFIG_FILE_PATH_PROPERTY, envFile.getAbsolutePath());
        properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, "From ../conf/OPS-IMS");
        return true;
    }

    public static void list(PrintStream out){
        try {
            getProperties().list(out);
        } catch (IOException ex) {
            out.print(ex);
        }
    }
    
}
