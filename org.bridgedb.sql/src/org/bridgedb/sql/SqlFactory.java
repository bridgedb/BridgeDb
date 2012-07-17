package org.bridgedb.sql;

import org.bridgedb.virtuoso.VirtuosoAccess;
import org.bridgedb.mysql.MySQLAccess;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

/**
 * Finds the SQL Configuration file and uses it to open the database with the correct database name, user name and password.
 * <p>
 * @See load() for where and in which order the file will be looked for.
 * @author Christian
 */
public class SqlFactory {
    /**
     * Name of the file assumed to hold the SQl configurations.
     */
    public static final String CONFIG_FILE_NAME = "sqlConfig.txt";

    /**
     * Name of the propetry that will be set by load() to say where the config file was found.
     */
    public static final String CONFIG_FILE_PATH_PROPERTY = "ConfigPath";
    
    /**
     * Name of the property that will be set by load() to say how the config file was found.
     */
    public static final String CONFIG_FILE_PATH_SOURCE_PROPERTY = "ConfigPathSource";
    
    //Name of the properties that will be looked for in the config file.
    public static final String SQL_PORT_PROPERTY = "SqlPort";
    public static final String SQL_USER_PROPERTY = "SqlUser";
    public static final String SQL_PASSWORD_PROPERTY = "SqlPassword";
    public static final String SQL_DATABASE_PROPERTY = "SqlDatabase";
    public static final String LOAD_SQL_DATABASE_PROPERTY = "LoadSqlDatabase";
    public static final String TEST_SQL_DATABASE_PROPERTY = "TestSqlDatabase";
    public static final String TEST_SQL_USER_PROPERTY = "TestSqlUser";
    public static final String TEST_SQL_PASSWORD_PROPERTY = "TestSqlPassword";
            

    private static final String NO_CONFIG_FILE = "No config file found";
    
    private static String path;
    
    private static Properties properties;
    
    /**
     * Create a wrapper around the live SQL Database, 
     *     using the database name, user name and password found in the config file.
     * @return 
     * @throws BridgeDbSqlException 
     */
    public static SQLAccess createSQLAccess() throws BridgeDbSqlException {
        SQLAccess sqlAccess = new MySQLAccess(sqlPort() + "/" + sqlDatabase(), sqlUser(), sqlPassword());
        sqlAccess.getConnection();
        return sqlAccess;
    }

    /**
     * Create a wrapper around the load SQL Database, 
     *     using the database name, user name and password found in the config file.
     * @See sqlLoadDatabase()
     * @return
     * @throws BridgeDbSqlException 
     */
    public static SQLAccess createLoadSQLAccess() throws BridgeDbSqlException {
        SQLAccess sqlAccess = new MySQLAccess(sqlPort() + "/" + sqlLoadDatabase(), sqlUser(), sqlPassword());
        sqlAccess.getConnection();
        return sqlAccess;
    }

    /**
     * Create a wrapper around the load SQL Database, 
     *     using the database name, user name and password found in the config file.
     * @See sqlTestDatabase(), testSqlUser() and testSqlPassword()
     * @return
     * @throws BridgeDbSqlException 
     */
    public static SQLAccess createTestSQLAccess() throws BridgeDbSqlException {
        SQLAccess sqlAccess = new MySQLAccess(sqlPort() + "/" + sqlTestDatabase(), testSqlUser(), testSqlPassword());
        sqlAccess.getConnection();
        return sqlAccess;
    }

    /**
     * Create a wrapper around the Test Virtuosos Database, 
     *     using the hardcoded database name, user name and password.
     * @return
     * @throws BridgeDbSqlException 
     */
    public static SQLAccess createTestVirtuosoAccess() throws BridgeDbSqlException {
        VirtuosoAccess virtuosoAccess = new VirtuosoAccess();
        virtuosoAccess.getConnection();
        return virtuosoAccess;
    }
    
    /**
     * Identified the path where the config file was found.
     * @return The absolutle path of the config file.
     */
    public static String configFilePath(){
        try {
            return getProperties().getProperty(CONFIG_FILE_PATH_PROPERTY);
        } catch (IOException ex) {
            return ex.getMessage();
        }
    }
    
    /**
     * Identifies how the config file was found.
     * @return String saying how the config file was found. 
     */
    public static String configSource(){
        try {
            return getProperties().getProperty(CONFIG_FILE_PATH_SOURCE_PROPERTY);
        } catch (IOException ex) {
           return ex.getMessage();
        }
    }

    /**
     * Identifies the port number the SQL services can be found at.
     * @return Port number is specified otherwise default of 3306
     */
    private static String sqlPort(){
        String result;
        try {
            result = getProperties().getProperty(SQL_PORT_PROPERTY);
        } catch (IOException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return "jdbc:mysql://localhost:3306";
    }

    /**
     * Identifies the password to use for the live and load databases
     * @return Password specified or the default of "ims"
     */
    private static String sqlPassword(){
        String result;
        try {
            result = getProperties().getProperty(SQL_PASSWORD_PROPERTY);
        } catch (IOException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return "ims";
    }
            
    /**
     * Identifies the user name to use for the live and load databases
     * @return User name specified or the default of "ims"
     */
    private static String sqlUser(){
        String result;
        try {
            result = getProperties().getProperty(SQL_USER_PROPERTY);
        } catch (IOException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return "ims";
    }

    /**
     * Identifies the database name to use for the live database
     * @return Database name specified or the default of "ims"
     */
    private static String sqlDatabase(){
        String result;
        try {
            result = getProperties().getProperty(SQL_DATABASE_PROPERTY);
        } catch (IOException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return "ims";
    }

    /**
     * Identifies the database name to use for the load database
     * @return Database name specified. Otherwise defaults to the live database.
     */
    private static String sqlLoadDatabase(){
        String result;
        try {
            result = getProperties().getProperty(LOAD_SQL_DATABASE_PROPERTY);
        } catch (IOException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return sqlDatabase();
    }

    /**
     * Identifies the database name to use for the test database.
     * <p>
     * Warning some unit test delete all values in the database so this should NEVER be the same as the live database.
     * @return Database name specified or the default of "imstest"
     */
    private static String sqlTestDatabase(){
        String result;
        try {
            result = getProperties().getProperty(TEST_SQL_DATABASE_PROPERTY);
        } catch (IOException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return "imstest";
    }

    /**
     * Identifies the password to use for the test databases
     * @return Password specified or defaults to live password
     */
    private static String testSqlPassword(){
        String result;
        try {
            result = getProperties().getProperty(TEST_SQL_PASSWORD_PROPERTY);
        } catch (IOException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return sqlPassword();
    }
            
    /**
     * Identifies the user name to use for the test databases
     * @return User name specified or defaults to live user name
     */
    private static String testSqlUser(){
        String result;
        try {
            result = getProperties().getProperty(TEST_SQL_USER_PROPERTY);
        } catch (IOException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return sqlUser();
    }

    /**
     * Returns all the properties found in the config file as well as those set during loading.
     * @return
     * @throws IOException 
     */
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
        if (loadDirectly()) return;
        if (loadFromConfigs()) return;
        if (loadFromResources()) return;
        if (loadFromSqlConfigs()) return;
        if (loadFromSqlResources()) return;
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

    /**
     * Looks for the config file in the "resources" sub directories of the run directory.
     * <p>
     * This is where the netbeans SQL project will look for it. (Assuing previous loads failed)
     * @return True if the file was found, False if it was not found.
     * @throws IOException If there is an error reading the file.
     */
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

    /**
     * Looks for the config file in the conf/OPS-IMS sub directories of the SQL project.
     * <p>
     * Currently not used.
     * @return True if the file was found, False if it was not found.
     * @throws IOException If there is an error reading the file.
     */
    private static boolean loadFromSqlConfigs() throws IOException {
        File confFolder = new File ("../org.bridgedb.sql/conf/OPS-IMS");
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

    /**
     * Looks for the config file in the "resources" sub directories of the SQL project.
     * <p>
     * This is where the netbeans project that depend on the SQL project will look for it. (Assuing previous loads failed)
     * @return True if the file was found, False if it was not found.
     * @throws IOException If there is an error reading the file.
     */
    private static boolean loadFromSqlResources() throws IOException {
        File resourceFolder = new File ("../org.bridgedb.sql/resources");
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

    /** 
     * Outputs a list of all the properties to the PrintStream. 
     * @param out A PrintStream such as System.out
     */
    public static void list(PrintStream out){
        try {
            getProperties().list(out);
        } catch (IOException ex) {
            out.print(ex);
        }
    }
      
}
