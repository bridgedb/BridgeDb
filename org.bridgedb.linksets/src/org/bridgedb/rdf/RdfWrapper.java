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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.linkset.IDMapperLinksetException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
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
public class RdfWrapper {
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
    private static final boolean EXCLUDE_INFERRED =false;

    private static final String NO_CONFIG_FILE = "No config file found";
    private static String path;
    
    private static Properties properties;

    public synchronized static void clear(RdfStoreType type) throws IDMapperLinksetException{
        Repository repository = getRepository(type);
        RepositoryConnection connection = getConnection(repository);
        try {
            connection.clear();
            shutdown(repository, connection);
        } catch (Throwable ex) {
            shutdownAfterError(repository, connection);
            throw new IDMapperLinksetException ("Error clearing the Reposotory. ", ex);
        } 
    }

    /**
     * Calling methods have a STRONG obligation to shut down the Repository! Even on Exceptions!
     * 
     * @param rdfStoreType
     * @return
     * @throws IDMapperLinksetException 
     */
    private static Repository getRepository(RdfStoreType rdfStoreType) throws IDMapperLinksetException {
        File dataDir =getDataDir(rdfStoreType);
        if (!dataDir.exists()){
           throw new IDMapperLinksetException ("Please check RDF settings File " + dataDir + " does not exist");
        }
        if (!dataDir.isDirectory()){
           throw new IDMapperLinksetException ("Please check RDF settings File " + dataDir + " is not a directory");
        }
        Repository repository = new SailRepository(new NativeStore(dataDir));
        try {
            repository.initialize();
        } catch (Throwable ex) {
            try {
                repository.shutDown();
            } catch (Throwable ex1) {
                throw new IDMapperLinksetException ("Error initializing and now shutting down repository ", ex1);
            }
            throw new IDMapperLinksetException ("Error initializing repository ", ex);
        }
        return repository;
    }

     private static File getDataDir(RdfStoreType rdfStoreType) throws IDMapperLinksetException {
        switch (rdfStoreType){
            case MAIN: 
                return new File(getSailNativeStore());
            case LOAD:
                return new File(getLoadSailNativeStore());
            case TEST:
                return new File(getTestSailNativeStore());
            default:
                throw new IDMapperLinksetException ("Unepected RdfStoreType " + rdfStoreType);
        }
    }

    private static RepositoryConnection getConnection(Repository repository) throws IDMapperLinksetException{
        try {
            return repository.getConnection();
        } catch (Throwable ex) {
            try {
                repository.shutDown();
            } catch (Throwable ex1) {
                throw new IDMapperLinksetException ("Unable to get a connection and error shuting down. ", ex1);
            }
            throw new IDMapperLinksetException ("Unable to get a connection. ", ex);
        }      
    }
    
    protected static RepositoryConnection setupConnection(RdfStoreType rdfStoreType) throws RDFHandlerException{
        Repository repository;
        try {
            repository = getRepository (rdfStoreType);
        } catch (IDMapperLinksetException ex) {
           throw new RDFHandlerException("Setup error ", ex);
        }
        try {
            return repository.getConnection();
        } catch (Throwable ex) {
            try {
                repository.shutDown();
            } catch (Throwable ex1) {
                throw new RDFHandlerException ("Unable to get a connection and error shuting down. ", ex1);
            }
            throw new RDFHandlerException ("Unable to get a connection. ", ex);
        }      
    }

    protected static RepositoryResult<Statement> getStatements(RepositoryConnection connection, 
            Resource subject, URI predicate, Value object, Resource... contexts) throws RDFHandlerException {
        try {
            return connection.getStatements(subject, predicate, object, false, contexts);
        } catch (Throwable ex) {
            shutdownAfterError(connection);
            throw new RDFHandlerException("Error getting statement ", ex);
        }
    }
    
    protected static void add(RepositoryConnection connection, 
            Resource subject, URI predicate, Value object, Resource... contexts) throws RDFHandlerException{
        try {
            connection.add(subject, predicate, object, contexts);
        } catch (Throwable ex) {
            shutdownAfterError(connection);
            throw new RDFHandlerException("Error adding ", ex);            
        }
    }
    
    protected static List<Statement> asList (RepositoryConnection connection, RepositoryResult<Statement> rr) throws RDFHandlerException{
        try {
            return rr.asList();
        } catch (Throwable ex) {
            shutdownAfterError(connection);
            throw new RDFHandlerException("Error converting to list ", ex);            
        }
    }
    
    protected static void remove(RepositoryConnection connection, RepositoryResult<Statement> rr) throws RDFHandlerException{
        try {
            connection.remove(rr);
        } catch (Throwable ex) {
            shutdownAfterError(connection);
            throw new RDFHandlerException("Error removing from connection ", ex);            
        }        
    }
    
    static void shutdown(RepositoryConnection connection) throws RDFHandlerException{
        try {
            shutdown(connection.getRepository(), connection);
        } catch (IDMapperLinksetException ex) {
            throw new RDFHandlerException("Error shutting down");
        }
    }
     
     private static void shutdown(Repository repository, RepositoryConnection connection) throws IDMapperLinksetException{
        try {
            connection.close();
        } catch (Throwable ex) {
            throw new IDMapperLinksetException ("Error closing connection ", ex);
        } finally {
            try {
                repository.shutDown();
            } catch (Throwable ex) {
                throw new IDMapperLinksetException ("Error shutting down repository ", ex);
            }
        }
    }
    
    protected static void shutdownAfterError(RepositoryConnection connection){
        shutdownAfterError(connection.getRepository(), connection);
    }
    
    private static void shutdownAfterError(Repository repository, RepositoryConnection connection){
        try {
            connection.close();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        try {
            repository.shutDown();
        } catch (Throwable ex) {
            ex.printStackTrace();
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
    
    static List<String> getContextNames(RdfStoreType rdfStoreType) throws IDMapperLinksetException {
        Repository repository = getRepository(rdfStoreType);
        RepositoryConnection connection = getConnection(repository);
        try {
            RepositoryResult<Resource> rr = connection.getContextIDs();
            List<Resource> resources = rr.asList();
            ArrayList<String> linksetNames = new ArrayList<String>();
            for (Resource resource:resources){
                linksetNames.add(resource.stringValue());
            }
            shutdown(repository, connection);
            return linksetNames;
        } catch (Throwable ex) {
            shutdownAfterError(repository, connection);
            throw new IDMapperLinksetException ("Error extracting context names.", ex);
        }
    }

    static URI getLinksetURL(Value linksetId){
        return new URIImpl(RdfWrapper.getBaseURI() + "/linkset/" + linksetId.stringValue());  
    }
    
    static URI getLinksetURL(int linksetId){
        return new URIImpl(RdfWrapper.getBaseURI() + "/linkset/" + linksetId);  
    }
  
    static String getRDF(RdfStoreType rdfStoreType, int linksetId) throws IDMapperLinksetException {
        Repository repository = getRepository(rdfStoreType);
        RepositoryConnection connection = getConnection(repository);
        StringOutputStream stringOutputStream = new StringOutputStream();            
        RDFXMLWriter writer = new RDFXMLWriter(stringOutputStream);
        writer.startRDF();
        Resource linkSetGraph = getLinksetURL(linksetId);
        RepositoryResult<Statement> rr;
        try {
            rr = 
                    connection.getStatements(ANY_SUBJECT, ANY_PREDICATE, ANY_OBJECT, EXCLUDE_INFERRED, linkSetGraph);
        } catch (Throwable ex) {
            shutdownAfterError(repository, connection);
            throw new IDMapperLinksetException ("Error extracting rdf.", ex);
        }
        try {
            while (rr.hasNext()){
                Statement st = rr.next();
                writer.handleStatement(st);
            }
            writer.endRDF();
            shutdown(repository, connection);
            return stringOutputStream.toString();
        } catch (Throwable ex) {
            shutdownAfterError(repository, connection);
            throw new IDMapperLinksetException ("Error extracting rdf.", ex);
        }
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
        //ystem.out.println(new File(".").getAbsolutePath());
        list(System.out);
        //Map<String, String> env = System.getenv();
        //for (String envName : env.keySet()) {
        //    System.out.format("%s=%s%n",
        //                      envName,
        //                      env.get(envName));
        //}
    }

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
