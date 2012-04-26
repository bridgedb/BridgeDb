/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;

/**
 *
 * @author Christian
 */
public class RdfFactory {
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
    private static Repository productionRepository;
    private static Repository testRepository;

    public static Repository getRepository() throws RepositoryException {
        if (productionRepository == null){
            File dataDir = new File(getSailNativeStore());
            productionRepository = new SailRepository(new NativeStore(dataDir));
            //myRepository = new SailRepository(new MemoryStore());
            productionRepository.initialize();
        }
        return productionRepository;
    }

    public static Repository getLoadRepository() throws RepositoryException {
        if (testRepository == null){
            File dataDir = new File(getLoadSailNativeStore());
            testRepository = new SailRepository(new NativeStore(dataDir));
            //myRepository = new SailRepository(new MemoryStore());
            testRepository.initialize();
        }
        return testRepository;
    }

    public static Repository getTestRepository() throws RepositoryException {
        if (testRepository == null){
            File dataDir = new File(getTestSailNativeStore());
            testRepository = new SailRepository(new NativeStore(dataDir));
            //myRepository = new SailRepository(new MemoryStore());
            testRepository.initialize();
        }
        return testRepository;
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
        return "rdf/linksets";
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
        return "rdf/testLinksets";
    }

    public static String getBaseURI() {
        String result;
        try {
            result = getProperties().getProperty(BASE_URI_PROPERTY);
        } catch (IOException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return "http://openphacts.cs.man.ac.uk:9090/OPS-IMS";        
    }
    
    private static Properties getProperties() throws IOException{
        if (properties == null){
            properties = new Properties();
            load();
        }
        return properties;
    }
    
    private static void load() throws IOException{
        if (loadByEnviromentVariable()) return;
        if (loadDirectly()) return;
        if (loadFromConfigs()) return;
        if (loadFromResources()) return;
        if (loadFromLinksetConfigs()) return;
        if (loadFromLinksetResources()) return;
        properties.put(CONFIG_FILE_PATH_PROPERTY, NO_CONFIG_FILE) ;
        properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, NO_CONFIG_FILE);
    }
    
    private static boolean loadByEnviromentVariable() throws IOException {
        String envPath = System.getenv().get("OPS-IMS-CONFIG");
        if (envPath == null || envPath.isEmpty()) return false;
        File envFile = new File(envPath);
        if (!envFile.exists()){
            throw new FileNotFoundException ("Environment Variable OPS-IMS-CONFIG points to " + envPath + 
                    " but no file found there");
        }
        if (envFile.isDirectory()){
            envFile = new File(envFile, CONFIG_FILE_NAME);
            if (!envFile.exists()){
                throw new FileNotFoundException ("Environment Variable OPS-IMS-CONFIG points to " + envPath + 
                        " but no " + CONFIG_FILE_NAME + " file found there");
            }
        }
        FileInputStream configs = new FileInputStream(envFile);
        properties.load(configs);
        properties.put(CONFIG_FILE_PATH_PROPERTY, envFile.getAbsolutePath());
        properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, "OPS-IMS-CONFIG Enviroment Variable");
        return true;
    }

    private static boolean loadDirectly() throws IOException {
        File envFile = new File(CONFIG_FILE_NAME);
        if (!envFile.exists()) return false;
        FileInputStream configs = new FileInputStream(envFile);
        properties.load(configs);
        properties.put(CONFIG_FILE_PATH_PROPERTY, envFile.getAbsolutePath());
        properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, "From main Directory");
        return true;
    }

    private static boolean loadFromConfigs() throws IOException {
        File confFolder = new File ("conf/OPS-IMS");
        path = confFolder.getAbsolutePath();
        if (!confFolder.exists()) return false;
        if (!confFolder.isDirectory()){
            throw new IOException("Expected " + confFolder.getAbsolutePath() + " to be a directory");
        }
        File envFile = new File(confFolder, CONFIG_FILE_NAME);
        if (!envFile.exists()) return false;
        FileInputStream configs = new FileInputStream(envFile);
        properties.load(configs);
        properties.put(CONFIG_FILE_PATH_PROPERTY, envFile.getAbsolutePath());
        properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, "From conf/OPS-IMS");
        return true;
    }

    private static boolean loadFromResources() throws IOException {
        File resourceFolder = new File ("resources");
        if (!resourceFolder.exists()) return false;
        if (!resourceFolder.isDirectory()){
            throw new IOException("Expected " + resourceFolder.getAbsolutePath() + " to be a directory");
        }
        File envFile = new File(resourceFolder, CONFIG_FILE_NAME);
        if (!envFile.exists()) return false;
        FileInputStream configs = new FileInputStream(envFile);
        properties.load(configs);
        properties.put(CONFIG_FILE_PATH_PROPERTY, envFile.getAbsolutePath());
        properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, "From conf/OPS-IMS");
        return true;
    }

    private static boolean loadFromLinksetConfigs() throws IOException {
        File confFolder = new File ("../org.bridgedb.linkset/conf/OPS-IMS");
        path = confFolder.getAbsolutePath();
        if (!confFolder.exists()) return false;
        if (!confFolder.isDirectory()){
            throw new IOException("Expected " + confFolder.getAbsolutePath() + " to be a directory");
        }
        File envFile = new File(confFolder, CONFIG_FILE_NAME);
        if (!envFile.exists()) return false;
        FileInputStream configs = new FileInputStream(envFile);
        properties.load(configs);
        properties.put(CONFIG_FILE_PATH_PROPERTY, envFile.getAbsolutePath());
        properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, "From conf/OPS-IMS");
        return true;
    }

    private static boolean loadFromLinksetResources() throws IOException {
        File resourceFolder = new File ("../org.bridgedb.linkset/resources");
        if (!resourceFolder.exists()) return false;
        if (!resourceFolder.isDirectory()){
            throw new IOException("Expected " + resourceFolder.getAbsolutePath() + " to be a directory");
        }
        File envFile = new File(resourceFolder, CONFIG_FILE_NAME);
        if (!envFile.exists()) return false;
        FileInputStream configs = new FileInputStream(envFile);
        properties.load(configs);
        properties.put(CONFIG_FILE_PATH_PROPERTY, envFile.getAbsolutePath());
        properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, "From conf/OPS-IMS");
        return true;
    }

    public static void list(PrintStream out){
        try {
            getProperties().list(out);
        } catch (IOException ex) {
            out.print(ex);
        }
    }
    
    public static void main(String[] args) throws IOException {
        //System.out.println(new File(".").getAbsolutePath());
        list(System.out);
        //Map<String, String> env = System.getenv();
        //for (String envName : env.keySet()) {
        //    System.out.format("%s=%s%n",
        //                      envName,
        //                      env.get(envName));
        //}
    }

}
