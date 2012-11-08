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
package org.bridgedb.sql;

import org.bridgedb.virtuoso.VirtuosoAccess;
import org.bridgedb.mysql.MySQLAccess;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.bridgedb.IDMapperException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.StoreType;

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
    
    //Name of the properties that will be looked for in the config file.
    public static final String SQL_PORT_PROPERTY = "SqlPort";
    public static final String SQL_USER_PROPERTY = "SqlUser";
    public static final String SQL_PASSWORD_PROPERTY = "SqlPassword";
    public static final String SQL_DATABASE_PROPERTY = "SqlDatabase";
    public static final String LOAD_SQL_DATABASE_PROPERTY = "LoadSqlDatabase";
    public static final String TEST_SQL_DATABASE_PROPERTY = "TestSqlDatabase";
    public static final String TEST_SQL_USER_PROPERTY = "TestSqlUser";
    public static final String TEST_SQL_PASSWORD_PROPERTY = "TestSqlPassword";
            
    private static Properties properties; 
    //TODO get from properties
    private static boolean useMySQL = true;
    
    static final Logger logger = Logger.getLogger(SqlFactory.class);
    
    /**
     * Create a wrapper around the live SQL Database, 
     *     using the database name, user name and password found in the config file.
     * @return 
     * @throws BridgeDbSqlException 
     */
    public static SQLAccess createTheSQLAccess(StoreType type) throws BridgeDbSqlException {
        SQLAccess sqlAccess;
        if (useMySQL){
            switch (type){
                case LIVE:
                    sqlAccess = new MySQLAccess(sqlPort() + "/" + sqlDatabase(), sqlUser(), sqlPassword());
                    logger.info("Connecting to Live MYSQL database " + sqlDatabase());
                    break;
                case LOAD:
                    sqlAccess = new MySQLAccess(sqlPort() + "/" + sqlLoadDatabase(), sqlUser(), sqlPassword());
                    logger.info("Connecting to Load MYSQL database " + sqlLoadDatabase());
                    break;
                case TEST:
                    sqlAccess = new MySQLAccess(sqlPort() + "/" + sqlTestDatabase(), testSqlUser(), testSqlPassword());
                    logger.info("Connecting to test MYSQL database " + sqlTestDatabase());
                    break;
                default:     
                    throw new UnsupportedOperationException("Unexpected StoreType " + type);
            } 
        } else {       
            logger.info("Connecting to hardcoded Virtuoso database. Ignoring StoreType");
            switch (type){
                case LIVE:
                    sqlAccess = new VirtuosoAccess();;
                    break;
                case LOAD:
                    sqlAccess = new VirtuosoAccess();;
                    break;
                case TEST:
                    sqlAccess = new VirtuosoAccess();;
                    break;
                default:     
                    throw new UnsupportedOperationException("Unexpected StoreType " + type);
            }
        }
        sqlAccess.getConnection();
        return sqlAccess;
    }

    public static void setUseMySQL(boolean forceMySQL){
        useMySQL = forceMySQL;
    }
    
     /**
     * Identifies the port number the SQL services can be found at.
     * @return Port number is specified otherwise default of 3306
     */
    private static String sqlPort(){
        String result;
        try {
            result = getProperties().getProperty(SQL_PORT_PROPERTY).trim();
        } catch (IDMapperException ex) {
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
            result = getProperties().getProperty(SQL_PASSWORD_PROPERTY).trim();
        } catch (IDMapperException ex) {
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
            result = getProperties().getProperty(SQL_USER_PROPERTY).trim();
        } catch (IDMapperException ex) {
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
            result = getProperties().getProperty(SQL_DATABASE_PROPERTY).trim();
        } catch (IDMapperException ex) {
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
            result = getProperties().getProperty(LOAD_SQL_DATABASE_PROPERTY).trim();
        } catch (IDMapperException ex) {
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
            result = getProperties().getProperty(TEST_SQL_DATABASE_PROPERTY).trim();
        } catch (IDMapperException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return "imstest";
    }

    /**
     * Identifies the password to use for the test databases
     * @return Password specified or the default of "imstest"
     */
    private static String testSqlPassword(){
        String result;
        try {
            result = getProperties().getProperty(TEST_SQL_PASSWORD_PROPERTY).trim();
        } catch (IDMapperException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return "imstest";
       }
            
    /**
     * Identifies the user name to use for the test databases
     * @return User name specified or the default of "imstest"
     */
    private static String testSqlUser(){
        String result;
        try {
            result = getProperties().getProperty(TEST_SQL_USER_PROPERTY).trim();
        } catch (IDMapperException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
     return "imstest";
       }

    /**
     * Returns all the properties found in the config file as well as those set during loading.
     * @return
     * @throws IOException 
     */
    private static Properties getProperties() throws IDMapperException{
        if (properties == null){
            properties = ConfigReader.getProperties(CONFIG_FILE_NAME);
        }
        return properties;
    }

    static boolean supportsIsValid() {
        if (useMySQL){
            return true;
        } else {
            return false;
        }        
    }

    static String getAutoIncrementCommand() {
        if (useMySQL){
            return "AUTO_INCREMENT";
        } else {
            return "IDENTITY";
        }
    }

    static boolean supportsMultipleInserts() {
        if (useMySQL){
            return true;
        } else {
            //TODO work out why this has to be false;
            return false;
        }        
    }

    static boolean supportsLimit() {
        if (useMySQL){
            return true;
        } else {
            //TODO work out why this has to be false;
            return false;
        }        
    }

    static boolean supportsTop() {
        if (useMySQL){
            return false;
        } else {
            //TODO work out why this has to be false;
            return true;
        }        
    }
          
}
