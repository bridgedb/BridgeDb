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
package org.bridgedb.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.SimpleLayout;

/**
 *
 * @author Christian
 */
public class ConfigReader {
    
    /** 
     * Sets the build into sandbox mode.
     * Warning in SANDBOX mode the system will not build without builder provided config files.
     * If SANDBOX is true in the MASTER branch this is an ERROR. Please fix and contact developer team ASAP!
     */
    public static boolean SANDBOX = true;
    public static final String CONFIG_FILE_NAME = "Config.txt";
    public static final String SANDBOX_CONFIG_FILE_NAME = "SandboxConfig.txt";

    public static final String VOID_OWL_FILE = "VoidInfo.owl";
    
    public static final String CONFIG_FILE_PATH_PROPERTY = "ConfigPath";
    public static final String CONFIG_FILE_PATH_SOURCE_PROPERTY = "ConfigPathSource";
    public static final String LOG_PROPERTIES_FILE = "log4j.properties";
    public static final String SANDBOX_LOG_PROPERTIES_FILE = "sandboxLog4j.properties";
    public static final String[] CONFIG_PROPERTIES_ARRAY = new String[] {CONFIG_FILE_PATH_PROPERTY, CONFIG_FILE_PATH_SOURCE_PROPERTY};
    public static final Set<String> CONFIG_PROPERTIES = new HashSet<String>(Arrays.asList(CONFIG_PROPERTIES_ARRAY));
    
    private InputStream inputStream;
    private String findMethod;
    private String foundAt;
    private String error = null;
    private Properties properties = null;
    private static boolean loggerSetup = false;
    private static ConfigReader propertyReader = null;
    
    static final Logger logger = Logger.getLogger(ConfigReader.class);
    
    public static Properties getProperties() throws BridgeDBException{
        if (propertyReader == null){
            if (SANDBOX){
                propertyReader = new ConfigReader(SANDBOX_CONFIG_FILE_NAME);
            } else {
                propertyReader = new ConfigReader(CONFIG_FILE_NAME);            
            }
            configureLogger();
        }
        return propertyReader.readProperties();
    }
  
    public static InputStream getInputStream(String fileName) throws BridgeDBException{
        ConfigReader finder = new ConfigReader(fileName);
        configureLogger();
        return finder.getInputStream();
    }
        
    public static synchronized void configureLogger() throws BridgeDBException{
        if (!loggerSetup){
            ConfigReader finder;
            if (SANDBOX){
                finder = new ConfigReader(LOG_PROPERTIES_FILE);
            } else { 
                finder = new ConfigReader(SANDBOX_LOG_PROPERTIES_FILE);
            }
            Properties props = finder.readProperties();
            PropertyConfigurator.configure(props);
            logger.info("Logger configured from " + finder.foundAt + " by " + finder.findMethod);
            loggerSetup = true;
            if (SANDBOX){
                logger.warn("WARNING SANDBOX MODE ACTIVE!");
                logger.warn("SANDBOX requires config files not in the normal build");
                logger.warn("To deactivate sandbox change the flag in the class ConfigReader in the package BridgeDB Utils");
                System.err.println("WARNING SANDBOX MODE ACTIVE!");
                System.err.println("SANDBOX requires config files not in the normal build");
                System.err.println("To deactivate sandbox change the flag in the class ConfigReader in the package BridgeDB Utils");            
            }
        }
    }
     
    public static void logToConsole() throws BridgeDBException{
        configureLogger();
        Logger.getRootLogger().addAppender(new ConsoleAppender(new SimpleLayout(), ConsoleAppender.SYSTEM_OUT));
    }
    
    private ConfigReader(String fileName) throws BridgeDBException{
        try {
            if (loadDirectly(fileName)) return;
            if (loadByEnviromentVariable(fileName)) return;
            if (loadByCatalinaHomeConfigs(fileName)) return;
            if (loadFromDirectory(fileName, "../org.bridgedb.utils/resources")) return;
            if (loadFromDirectory(fileName, "../conf/OPS-IMS")) return;
            if (loadFromDirectory(fileName, "conf/OPS-IMS")) return;
            if (loadFromDirectory(fileName, "../../BridgeDb/org.bridgedb.utils/resources")) return;
            if (getInputStreamFromResource(fileName)) return;
            if (getInputStreamFromJar(fileName)) return;
            throw new BridgeDBException("Unable to find " + fileName);
        } catch (IOException ex) {
            error = "Unexpected IOEXception after doing checks.";
            throw new BridgeDBException(error, ex);
        }
    }
    
    private InputStream getInputStream() throws BridgeDBException{
        if (error != null){
            throw new BridgeDBException(error);
        }
        if (inputStream == null){
            error = "InputStream already closed. Illegal attempt to use again.";         
            throw new BridgeDBException(error);
        }
        return inputStream;
    }
    
    private Properties readProperties() throws BridgeDBException{
        if (properties == null){
            properties = new Properties();           
            try {
                properties.load(getInputStream());
                properties.put(CONFIG_FILE_PATH_PROPERTY, foundAt);
                properties.put(CONFIG_FILE_PATH_SOURCE_PROPERTY, findMethod);
                inputStream.close();
                inputStream = null;
            } catch (IOException ex) {
                error = "Unexpected file not fond exception after file.exists returns true.";
                throw new BridgeDBException("Unexpected file not fond exception after file.exists returns true.", ex);
            }
        }
        return properties;
    }
    
    private boolean loadDirectly(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        if (!file.exists()) return false;
        inputStream = new FileInputStream(file);
        findMethod = "Loaded from run Directory.";
        foundAt = file.getAbsolutePath();
        if (loggerSetup){
            logger.info("Loaded file " + fileName + " directly from " + foundAt);    
        }
        return true;
    }

    /**
     * Looks for the config file in the directory set up the environment variable "OPS-IMS-CONFIG"
     * @return True if the config files was found. False if the environment variable "OPS-IMS-CONFIG" was unset.
     * @throws IOException Thrown if the environment variable is not null, 
     *    and the config file is not found as indicated, or could not be read.
     */
    private boolean loadByEnviromentVariable(String fileName) throws BridgeDBException, FileNotFoundException{
        String envPath = System.getenv().get("OPS-IMS-CONFIG");
        if (envPath == null || envPath.isEmpty()) {
            logger.warn("No environment variable OPS-IMS-CONFIG found");
            return false;
        }
        File envDir = new File(envPath);
        if (!envDir.exists()){
            error = "Environment Variable OPS-IMS-CONFIG points to " + envPath + 
                    " but no directory found there";
            throw new BridgeDBException (error);
        }
        if (envDir.isDirectory()){
            File file = new File(envDir, fileName);
            if (!file.exists()){
                return false;
            }
            inputStream = new FileInputStream(file);
            findMethod = "Loaded from Environment Variable.";
            foundAt = file.getAbsolutePath();
            if (loggerSetup){
                logger.info("Loaded file " + fileName + " using OPS-IMS-CONFIG from " + foundAt);    
            }
            return true;
        } else {
            String error = "Environment Variable OPS-IMS-CONFIG points to " + envPath + 
                    " but is not a directory";
            throw new BridgeDBException (error);
        }
    }
  
    /**
     * Looks for the config file in the directory set up the environment variable "OPS-IMS-CONFIG"
     * @return True if the config files was found. False if the environment variable "OPS-IMS-CONFIG" was unset.
     * @throws IOException Thrown if the environment variable is not null, 
     *    and the config file is not found as indicated, or could not be read.
     */
    private boolean loadByCatalinaHomeConfigs(String fileName) throws BridgeDBException, FileNotFoundException {
        String catalinaHomePath = System.getenv().get("CATALINA_HOME");
        if (catalinaHomePath == null || catalinaHomePath.isEmpty()) {
            logger.warn("No enviroment variable CATALINA_HOME found");
            return false;
        }
        File catalineHomeDir = new File(catalinaHomePath);
        if (!catalineHomeDir.exists()){
            error = "Environment Variable CATALINA_HOME points to " + catalinaHomePath + 
                    " but no directory found there";
            throw new BridgeDBException(error);
        }
        if (!catalineHomeDir.isDirectory()){
            error = "Environment Variable CATALINA_HOME points to " + catalinaHomePath + 
                    " but is not a directory";
            throw new BridgeDBException(error);
        }
        File envDir = new File (catalineHomeDir + "/conf/OPS-IMS");
        if (!envDir.exists()) return false; //No hard requirements that catalineHome has a /conf/OPS-IMS
        if (envDir.isDirectory()){
            File file = new File(envDir, fileName);
            if (!file.exists()){
                return false;
            }
            inputStream = new FileInputStream(file);
            findMethod = "Loaded from CATALINA_HOME configs.";
            foundAt = file.getAbsolutePath();
            if (loggerSetup){
                logger.info("Loaded file " + fileName + " using CATALINA_HOME from " + foundAt);    
            }
            return true;
        } else {
            error = "Environment Variable CATALINA_HOME points to " + catalinaHomePath  + 
                    " but $CATALINA_HOME/conf/OPS-IMS is not a directory";
            throw new BridgeDBException (error);
       }
    }
    
    /**
     * Looks for the config file in the conf/OPS-IMS sub directories of the run directory.
     * <p>
     * For tomcat conf would then be a sister directory of webapps.
     * @return True if the file was found, False if it was not found.
     * @throws IOException If there is an error reading the file.
     */
    private boolean loadFromDirectory(String fileName, String directoryName) throws FileNotFoundException {
        File directory = new File (directoryName);
        if (!directory.exists()) {
            logger.warn("No file directory found at: " + directoryName);
            return false;
        }
        if (!directory.isDirectory()){
            return false;
        }
        File file = new File(directory, fileName);
        if (!file.exists()) return false;
            if (!file.exists()){
                return false;
            }
            inputStream = new FileInputStream(file);
            findMethod = "Loaded from directory: " + directoryName;
            foundAt = file.getAbsolutePath();
            if (loggerSetup){
                logger.info("Loaded file " + fileName + " from " + foundAt);    
            }
            return true;
    }

    private boolean getInputStreamFromResource(String name) throws FileNotFoundException{
        java.net.URL url = this.getClass().getResource(name);
        if (url != null){
            String fileName = url.getFile();
            File file = new File(fileName);
            if (!file.exists()){
                return false;
            }
            inputStream = new FileInputStream(file);
            findMethod = "Loaded from Resource URI";
            foundAt = file.getAbsolutePath();
            if (loggerSetup){
                logger.info("Loaded file " + fileName + " from Jar ");    
            }
            return true;
        }
        return false;
    }

    private boolean getInputStreamFromJar(String name) throws IOException{
        ZipInputStream zip = null;
        CodeSource src = this.getClass().getProtectionDomain().getCodeSource();
        URL jar = src.getLocation();
        zip = new ZipInputStream( jar.openStream());
        ZipEntry ze = null;
        while( ( ze = zip.getNextEntry() ) != null ) {
            if (name.equals(ze.getName())){
                inputStream = zip;
                findMethod = "Loaded by unzipping jar";
                foundAt = "Inside jar file";
                if (loggerSetup){
                    logger.info("Loaded file " + name + " by unziiping the Jar ");    
                }
                return true;
            }
        }
        return false;
    }
}
