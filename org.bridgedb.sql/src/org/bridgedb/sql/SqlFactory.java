/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author Christian
 */
public class SqlFactory {
    public static final String CONFIG_FILE_PATH_PROPERTY = "ConfigPath";
    public static final String CONFIG_FILE_PATH_SOURCE_PROPERTY = "ConfigPathSource";
    public static final String SQL_PORT_PROPERTY = "SqlPort";
    public static final String SQL_USER_PROPERTY = "SqlUser";
    public static final String SQL_PASSWORD_PROPERTY = "SqlPassword";
    public static final String SQL_DATABASE_PROPERTY = "SqlDatabase";
    public static final String LOAD_SQL_DATABASE_PROPERTY = "LoadSqlDatabase";
    public static final String TEST_SQL_DATABASE_PROPERTY = "TestSqlDatabase";
    public static final String TEST_SQL_USER_PROPERTY = "TestSqlUser";
    public static final String TEST_SQL_PASSWORD_PROPERTY = "TestSqlPassword";
            
    public static final String CONFIG_FILE_NAME = "sqlConfig.txt";

    private static final String NO_CONFIG_FILE = "No config file found";
    private static String path;
    
    private static Properties properties;
    
    public static SQLAccess createSQLAccess() throws BridgeDbSqlException {
        SQLAccess sqlAccess = new MySQLAccess(sqlPort() + "/" + sqlDatabase(), sqlUser(), sqlPassword());
        sqlAccess.getConnection();
        return sqlAccess;
    }

    public static SQLAccess createLoadSQLAccess() throws BridgeDbSqlException {
        SQLAccess sqlAccess = new MySQLAccess(sqlPort() + "/" + sqlLoadDatabase(), sqlUser(), sqlPassword());
        sqlAccess.getConnection();
        return sqlAccess;
    }

    public static SQLAccess createTestSQLAccess() throws BridgeDbSqlException {
        SQLAccess sqlAccess = new MySQLAccess(sqlPort() + "/" + sqlTestDatabase(), testSqlUser(), testSqlPassword());
        sqlAccess.getConnection();
        return sqlAccess;
    }

    public static SQLAccess createTestVirtuosoAccess() throws BridgeDbSqlException {
        VirtuosoAccess virtuosoAccess = new VirtuosoAccess();
        virtuosoAccess.getConnection();
        return virtuosoAccess;
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
            return getProperties().getProperty(CONFIG_FILE_PATH_SOURCE_PROPERTY);
        } catch (IOException ex) {
           return ex.getMessage();
        }
    }

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

    private static String testSqlPassword(){
        String result;
        try {
            result = getProperties().getProperty(TEST_SQL_PASSWORD_PROPERTY);
        } catch (IOException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return "qw";//sqlPassword();
    }
            
    private static String testSqlUser(){
        String result;
        try {
            result = getProperties().getProperty(TEST_SQL_USER_PROPERTY);
        } catch (IOException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return "qwe2";//sqlUser();
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
        if (loadFromSqlConfigs()) return;
        if (loadFromSqlResources()) return;
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
        //    ystem.out.format("%s=%s%n",
        //                      envName,
        //                      env.get(envName));
        //}
    }



    
}
