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
package org.bridgedb.sql;

import org.apache.log4j.Logger;
import org.bridgedb.mysql.MySQLAccess;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.virtuoso.VirtuosoAccess;

/**
 * Finds the SQL Configuration file and uses it to open the database with the correct database name, user name and password.
 * <p>
 * See load() for where and in which order the file will be looked for.
 * @author Christian
 */
public class SqlFactory extends ConfigReader{
    /**
     * Name of the file assumed to hold the SQl configurations.
     */
    
    //Name of the properties that will be looked for in the config file.
    public static final String SQL_PORT_PROPERTY = "SqlPort";
    public static final String SQL_USER_PROPERTY = "SqlUser";
    public static final String SQL_PASSWORD_PROPERTY = "SqlPassword";
    public static final String SQL_DATABASE_PROPERTY = "SqlDatabase";
    public static final String TEST_SQL_DATABASE_PROPERTY = "TestSqlDatabase";
    public static final String TEST_SQL_USER_PROPERTY = "TestSqlUser";
    public static final String TEST_SQL_PASSWORD_PROPERTY = "TestSqlPassword";
    public static final String MYSQL_ENGINE_PROPERTY = "mysql.engine";
    
            
    //TODO get from properties
    private static boolean useMySQL = true;
    
    static final Logger logger = Logger.getLogger(SqlFactory.class);
    
    /**
     * Create a wrapper around the live SQL Database, 
     *     using the database name, user name and password found in the config file.
     * @return sqlAccess - a wrapper around the individual SQL database drivers
     * @throws BridgeDBException - if something goes wrong with the connection
     */
    public static SQLAccess createTheSQLAccess() throws BridgeDBException {
        SQLAccess sqlAccess;
        if (useMySQL){
            if (useTest){
                sqlAccess = new MySQLAccess(sqlPort() + "/" + sqlTestDatabase(), testSqlUser(), testSqlPassword());
                logger.info("Connecting to test MYSQL database " + sqlTestDatabase());
            } else {
                sqlAccess = new MySQLAccess(sqlPort() + "/" + sqlDatabase(), sqlUser(), sqlPassword());
                logger.info("Connecting to Live MYSQL database " + sqlDatabase());
            }
        } else {       
            sqlAccess = new VirtuosoAccess();;
            logger.info("Connecting to hardcoded Virtuoso database. Ignoring StoreType");
        }
        sqlAccess.getConnection();
        return sqlAccess;
    }

    /**
     * Create a wrapper around the live SQL Database, 
     *     using the database name, user name and password found in the config file.
     * @param database - the name of the database
     * @return sqlAccess - a wrapper around the individual SQL database drivers
     * @throws BridgeDBException - if something goes wrong with the connection
     */
    public static SQLAccess createASQLAccess(String database) throws BridgeDBException {
        SQLAccess sqlAccess=  new MySQLAccess(sqlPort() + "/" + database, sqlUser(), sqlPassword());
        return sqlAccess;
    }

    public static void setUseMySQL(boolean forceMySQL){
        useMySQL = forceMySQL;
    }
    
     /**
     * Identifies the port number the SQL services can be found at.
     * @return Port number is specified otherwise default of 3306
     */
    private static String sqlPort() throws BridgeDBException{
        String result;
        result = getProperties().getProperty(SQL_PORT_PROPERTY);
        if (result != null) {
            return result.trim();
        }
        return "jdbc:mysql://localhost:3306";
    }

    /**
     * Identifies the password to use for the live and load databases
     * @return Password specified or the default of "ims"
     */
    private static String sqlPassword() throws BridgeDBException{
        String result;
        result = getProperties().getProperty(SQL_PASSWORD_PROPERTY);
        if (result != null) {
            return result.trim();
        }
        return "ims";
    }
            
    /**
     * Identifies the user name to use for the live and load databases
     * @return User name specified or the default of "ims"
     */
    private static String sqlUser() throws BridgeDBException{
        String result;
        result = getProperties().getProperty(SQL_USER_PROPERTY);
        if (result != null) {
            return result.trim();
        }
        return "ims";
    }

    /**
     * Identifies the database name to use for the live database
     * @return Database name specified or the default of "ims"
     */
    private static String sqlDatabase() throws BridgeDBException{
        String result;
        result = getProperties().getProperty(SQL_DATABASE_PROPERTY);
        if (result != null) {
            return result.trim();
        }
        return "ims";
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
        } catch (BridgeDBException ex) {
            return ex.getMessage();
        }
        if (result != null) {
            return result.trim();
        }
        return "imstest";
    }

    /**
     * Identifies the password to use for the test databases
     * @return Password specified or the default of "imstest"
     */
    private static String testSqlPassword(){
        String result;
        try {
            result = getProperties().getProperty(TEST_SQL_PASSWORD_PROPERTY);
        } catch (BridgeDBException ex) {
            return ex.getMessage();
        }
        if (result != null) {
            return result.trim();
        }
        return "imstest";
       }
            
    /**
     * Identifies the user name to use for the test databases
     * @return User name specified or the default of "imstest"
     */
    private static String testSqlUser(){
        String result;
        try {
            result = getProperties().getProperty(TEST_SQL_USER_PROPERTY);
        } catch (BridgeDBException ex) {
            return ex.getMessage();
        }
        if (result != null) {
            return result.trim();
        }
            return "imstest";
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
     
    public static String configs() {
        try {
            if (useTest){
                return "TEST: " + sqlPort() + "/" + sqlTestDatabase() + " user:" + testSqlUser() + " password: " + testSqlPassword();
            } else {
                return sqlPort() + "/" + sqlDatabase() + " user:" + sqlUser() + " password:" + sqlPassword();
            }
        } catch (BridgeDBException ex) {
            return ex.getMessage();
        }
    }

    static boolean inSQLMode() {
        return useMySQL;
    }
    
    public static String engineSetting(){
        if (useTest){
            try {
                String engine = getProperties().getProperty(MYSQL_ENGINE_PROPERTY);
                if (engine == null || engine.isEmpty()){
                     return "";
                } 
                return " ENGINE = " + engine;            
            } catch (BridgeDBException ex) {
                //Ignore the property and use defualt
                return "";
            }
        } else {
            return "";
        }
        
    }
    
}
