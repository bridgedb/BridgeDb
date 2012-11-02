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
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.utils.StoreType;

/**
 *
 * @author Christian
 */
public class RdfConfig {
    public static final String CONFIG_FILE_PATH_PROPERTY = "ConfigPath";
    public static final String CONFIG_FILE_PATH_SOURCE_PROPERTY = "ConfigPathSource";
    public static final String SAIL_NATIVE_STORE_PROPERTY = "SailNativeStore";
    public static final String LOAD_SAIL_NATIVE_STORE_PROPERTY = "LoadSailNativeStore";
    public static final String TEST_SAIL_NATIVE_STORE_PROPERTY = "TestSailNativeStore";
    public static final String BASE_URI_PROPERTY = "BaseURI";
    public static final String CONFIG_FILE_NAME = "rdfConfig.txt";

    private static final String NO_CONFIG_FILE = "No config file found";
    private static String path;
    
    private static Properties properties;

    private static boolean repositoryExists(StoreType storeType) throws IDMapperLinksetException {
        File dataDir = getDataDir(storeType);
        return dataDir.exists();
    }
    
    public static String getTheBaseURI() {
        String result;
        try {
            result = getProperties().getProperty(BASE_URI_PROPERTY).trim();
        } catch (IDMapperLinksetException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return "http://openphacts.cs.man.ac.uk:9090/OPS-IMS";        
    }
    
    public static String getProperty(String key) throws IDMapperLinksetException{
        return getProperties().getProperty(key);
    }
    
    public static File getDataDir(StoreType storeType) throws IDMapperLinksetException {
        switch (storeType){
            case LIVE: 
                return new File(getSailNativeStore());
            case LOAD:
                return new File(getLoadSailNativeStore());
            case TEST:
                return new File(getTestSailNativeStore());
             default:
                throw new IDMapperLinksetException ("Unepected RdfStoreType " + storeType);
        }
    }

    private static String getSailNativeStore(){
        String result;
        try {
            result = getProperties().getProperty(SAIL_NATIVE_STORE_PROPERTY).trim();
        } catch (IDMapperLinksetException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return "../rdf/linksets";
    }

    private static String getLoadSailNativeStore(){
        String result;
        try {
            result = getProperties().getProperty(LOAD_SAIL_NATIVE_STORE_PROPERTY).trim();
        } catch (IDMapperLinksetException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return getSailNativeStore();
    }

    private static String getTestSailNativeStore(){
        String result;
        try {
            result = getProperties().getProperty(TEST_SAIL_NATIVE_STORE_PROPERTY).trim();
        } catch (IDMapperLinksetException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return "../rdf/testLinksets";
    }
 
    private static Properties getProperties() throws IDMapperLinksetException{
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
    private static void load() throws IDMapperLinksetException{
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
    private static boolean loadByEnviromentVariable() throws IDMapperLinksetException {
        String envPath = System.getenv().get("OPS-IMS-CONFIG");
        if (envPath == null || envPath.isEmpty()) return false;
        File envDir = new File(envPath);
        if (!envDir.exists()){
            String error = "Environment Variable OPS-IMS-CONFIG points to " + envPath + 
                    " but no directory found there";
            properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, error) ;
            throw new IDMapperLinksetException (error);
        }
        if (envDir.isDirectory()){
            File envFile = new File(envDir, CONFIG_FILE_NAME);
            if (!envFile.exists()){
                String error = "Environment Variable OPS-IMS-CONFIG points to " + envPath + 
                        " but no " + CONFIG_FILE_NAME + " file found there";
                properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, error) ;
                throw new IDMapperLinksetException (error);
            }
            loadProperties(envFile);
            properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, "OPS-IMS-CONFIG Enviroment Variable");
            return true;
        } else {
            String error = "Environment Variable OPS-IMS-CONFIG points to " + envPath + 
                    " but is not a directory";
            properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, error) ;
            throw new IDMapperLinksetException (error);
        }
    }

    /**
     * Looks for the config file in the directory set up the environment variable "OPS-IMS-CONFIG"
     * @return True if the config files was found. False if the environment variable "OPS-IMS-CONFIG" was unset.
     * @throws IOException Thrown if the environment variable is not null, 
     *    and the config file is not found as indicated, or could not be read.
     */
    private static boolean loadByCatalinaHomeConfigs() throws IDMapperLinksetException {
        String catalinaHomePath = System.getenv().get("CATALINA_HOME");
         if (catalinaHomePath == null || catalinaHomePath.isEmpty()) return false;
        File catalineHomeDir = new File(catalinaHomePath);
        if (!catalineHomeDir.exists()){
            String error = "Environment Variable CATALINA_HOME points to " + catalinaHomePath + 
                    " but no directory found there";
            properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, error) ;
            throw new IDMapperLinksetException (error);
        }
        if (!catalineHomeDir.isDirectory()){
            String error = "Environment Variable CATALINA_HOME points to " + catalinaHomePath + 
                    " but is not a directory";
            properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, error) ;
            throw new IDMapperLinksetException(error);
        }
        File envDir = new File (catalineHomeDir + "/conf/OPS-IMS");
        if (!envDir.exists()) return false; //No hard requirements that catalineHome has a /conf/OPS-IMS
         if (envDir.isDirectory()){
            File envFile = new File(envDir, CONFIG_FILE_NAME);
            if (!envFile.exists()){
                String error = "Environment Variable CATALINA_HOME points to " + catalinaHomePath + 
                        " but subdirectory /conf/OPS-IMS has no " + CONFIG_FILE_NAME + " file.";
                properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, error) ;
                throw new IDMapperLinksetException (error);
            }
            loadProperties(envFile);
            properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, "CATALINA_HOME/conf/OPS-IMS");
            return true;
        } else {
            String error = "Environment Variable CATALINA_HOME points to " + catalinaHomePath  + 
                    " but $CATALINA_HOME/conf/OPS-IMS is not a directory";
            properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, error) ;
            throw new IDMapperLinksetException (error);
       }
    }

    /**
     * Looks for the config file in the run directory.
     * @return True if the file was found, False if it was not found.
     * @throws IOException If there is an error reading the file.
     */
    private static boolean loadDirectly() throws IDMapperLinksetException {
        File envFile = new File(CONFIG_FILE_NAME);
        if (!envFile.exists()) return false;
        loadProperties(envFile);
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
    private static boolean loadFromConfigs() throws IDMapperLinksetException {
        File confFolder = new File ("conf/OPS-IMS");
        if (!confFolder.exists()) return false;
        if (!confFolder.isDirectory()){
            String error = "Expected " + confFolder.getAbsolutePath() + " to be a directory";
            properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, error) ;
            throw new IDMapperLinksetException (error);
        }
        File envFile = new File(confFolder, CONFIG_FILE_NAME);
        if (!envFile.exists()) return false;
        loadProperties(envFile);
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
    private static boolean loadFromParentConfigs() throws IDMapperLinksetException {
        File confFolder = new File ("../conf/OPS-IMS");
        if (!confFolder.exists()) return false;
        if (!confFolder.isDirectory()){
            String error = "Expected " + confFolder.getAbsolutePath() + " to be a directory";
            properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, error) ;
            throw new IDMapperLinksetException (error);
        }
        File envFile = new File(confFolder, CONFIG_FILE_NAME);
        if (!envFile.exists()) return false;
        loadProperties(envFile);
        properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, "From ../conf/OPS-IMS");
        return true;
    }

    private static void loadProperties(File propertyFile) throws IDMapperLinksetException{
        FileInputStream configs = null;
        try {
            configs = new FileInputStream(propertyFile);
            properties.load(configs);
            properties.put(CONFIG_FILE_PATH_PROPERTY, propertyFile.getAbsolutePath());
            String baseURI = properties.getProperty(BASE_URI_PROPERTY).trim();
            if (baseURI.contains("#")){
                throw new IDMapperLinksetException("baseURI " + baseURI 
                        + " contains a ('#') which throws of the webservice. It was found in" 
                        + propertyFile.getAbsolutePath() + " using  " + BASE_URI_PROPERTY);
            }
            if (!(baseURI.endsWith("/"))){
                properties.put(BASE_URI_PROPERTY, baseURI + "/");
            }
        } catch (IOException ex) {
            throw new IDMapperLinksetException ("Exception reading " + propertyFile.getAbsolutePath());
        } finally {
            try {
                configs.close();
            } catch (IOException ex) {
                throw new IDMapperLinksetException ("Exception reading " + propertyFile.getAbsolutePath());
            }
        }
    }
    
    /*public static void list(PrintStream out){
        try {
            getProperties().list(out);
        } catch (IDMapperLinksetException ex) {
            out.print(ex);
        }
    }*/
    
    /*private Value getStringletonObject(Resource subject, URI predicateURI, Resource graph, RdfStoreType type) 
           throws IDMapperLinksetException{
        Repository repository = getRepository(type);
        RepositoryConnection connection = getConnection(repository);
        List<Statement> list;
        try {
            RepositoryResult<Statement> rr = connection.getStatements(subject, predicateURI, null, false, graph);
            list = rr.asList();
            shutdown(repository, connection);
        } catch (RepositoryException ex) {
            shutdownAfterError(repository, connection);
            throw new IDMapperLinksetException ("Error clearing the Reposotory. ", ex);
        } 
        if (list.size() == 1){
            return list.get(0).getObject();
        }
        if (list.isEmpty()){
            return null;
        } else {
            System.err.println(list);
            throw new IDMapperLinksetException("Found more than one statement with subject " + subject + 
                    " and predicate " + predicateURI + " in " + graph);            
        }           
    }*/

}
