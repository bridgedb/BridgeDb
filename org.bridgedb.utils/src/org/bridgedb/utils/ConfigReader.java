/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.bridgedb.IDMapperException;

/**
 *
 * @author Christian
 */
public class ConfigReader {
    
    public static final String CONFIG_FILE_PATH_PROPERTY = "ConfigPath";
    public static final String CONFIG_FILE_PATH_SOURCE_PROPERTY = "ConfigPathSource";
    private InputStream inputStream;
    private String findMethod;
    private String foundAt;
    private String error = null;
    private Properties properties;
    
    public static Properties getProperties(String fileName) throws IDMapperException{
        ConfigReader finder = new ConfigReader(fileName);
        return finder.getProperties();
    }
    
    public static InputStream getInputStream(String fileName) throws IDMapperException{
        ConfigReader finder = new ConfigReader(fileName);
        return finder.getInputStream();
    }

    private ConfigReader(String fileName) throws IDMapperException{
        try {
            if (loadByEnviromentVariable(fileName)) return;
            if (loadByCatalinaHomeConfigs(fileName)) return;
            if (loadFromDirectory(fileName, "../org.bridgedb.utils/resources")) return;
            if (loadFromDirectory(fileName, "resources")) return;
            if (loadDirectly(fileName)) return;
            if (getInputStreamFromResource(fileName)) return;
            if (getInputStreamFromJar(fileName)) return;
        } catch (IOException ex) {
            error = "Unexpected IOEXception after doing checks.";
            throw new IDMapperException(error, ex);
        }
    }
    
    private InputStream getInputStream() throws IDMapperException{
        if (error != null){
            throw new IDMapperException(error);
        }
        if (inputStream == null){
            error = "InputStream already closed. Illegal attempt to use again.";         
            throw new IDMapperException(error);
        }
        return inputStream;
    }
    
    private Properties getProperties() throws IDMapperException{
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
                throw new IDMapperException("Unexpected file not fond exception after file.exists returns true.", ex);
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
        return true;
    }

    /**
     * Looks for the config file in the directory set up the environment variable "OPS-IMS-CONFIG"
     * @return True if the config files was found. False if the environment variable "OPS-IMS-CONFIG" was unset.
     * @throws IOException Thrown if the environment variable is not null, 
     *    and the config file is not found as indicated, or could not be read.
     */
    private boolean loadByEnviromentVariable(String fileName) throws IDMapperException, FileNotFoundException{
        String envPath = System.getenv().get("OPS-IMS-CONFIG");
        if (envPath == null || envPath.isEmpty()) return false;
        File envDir = new File(envPath);
        if (!envDir.exists()){
            error = "Environment Variable OPS-IMS-CONFIG points to " + envPath + 
                    " but no directory found there";
            throw new IDMapperException (error);
        }
        if (envDir.isDirectory()){
            File file = new File(envDir, fileName);
            if (!file.exists()){
                return false;
            }
            inputStream = new FileInputStream(file);
            findMethod = "Loaded from Environment Variable.";
            foundAt = file.getAbsolutePath();
            return true;
        } else {
            String error = "Environment Variable OPS-IMS-CONFIG points to " + envPath + 
                    " but is not a directory";
            throw new IDMapperException (error);
        }
    }
  
    /**
     * Looks for the config file in the directory set up the environment variable "OPS-IMS-CONFIG"
     * @return True if the config files was found. False if the environment variable "OPS-IMS-CONFIG" was unset.
     * @throws IOException Thrown if the environment variable is not null, 
     *    and the config file is not found as indicated, or could not be read.
     */
    private boolean loadByCatalinaHomeConfigs(String fileName) throws IDMapperException, FileNotFoundException {
        String catalinaHomePath = System.getenv().get("CATALINA_HOME");
        if (catalinaHomePath == null || catalinaHomePath.isEmpty()) return false;
        File catalineHomeDir = new File(catalinaHomePath);
        if (!catalineHomeDir.exists()){
            error = "Environment Variable CATALINA_HOME points to " + catalinaHomePath + 
                    " but no directory found there";
            throw new IDMapperException (error);
        }
        if (!catalineHomeDir.isDirectory()){
            error = "Environment Variable CATALINA_HOME points to " + catalinaHomePath + 
                    " but is not a directory";
            throw new IDMapperException(error);
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
            return true;
        } else {
            error = "Environment Variable CATALINA_HOME points to " + catalinaHomePath  + 
                    " but $CATALINA_HOME/conf/OPS-IMS is not a directory";
            throw new IDMapperException (error);
       }
    }
    
    /**
     * Looks for the config file in the conf/OPS-IMS sub directories of the run directory.
     * <p>
     * For tomcat conf would then be a sister directory of webapps.
     * @return True if the file was found, False if it was not found.
     * @throws IOException If there is an error reading the file.
     */
    private boolean loadFromDirectory(String fileName, String directoryName) throws IDMapperException, FileNotFoundException {
        File directory = new File (directoryName);
        if (!directory.exists()) {
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
                return true;
            }
        }
        return false;
    }
}
