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
package org.bridgedb.uri.loader.transative;

import java.io.File;
import org.apache.log4j.Logger;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;

/**
 *
 * @author Christian
 */
public class TransativeConfig extends ConfigReader{
    
    public static final String TRANSATIVE_DIRECTORY_PROPERTY = "TransitiveDirectory";
    public static final String TRANSATIVE_BASE_URI = "TransitiveBaseUri";
    public static final String TEST_DIRECTORY_PROPERTY = "TestDirectory";
 
    private static final Logger logger = Logger.getLogger(TransativeConfig.class);
    private static File testDir = null;
    
    private TransativeConfig(){
        super();
    }
    
    public static File getTransativeDirectory() throws BridgeDBException {
        if (useTest){
            return getTestDirectory();
        }
        return getDirectory(TRANSATIVE_DIRECTORY_PROPERTY, "Transative");
    }

    public static String getTransitiveBaseUri() throws BridgeDBException{
        return getProperties().getProperty(TRANSATIVE_BASE_URI);
    }

    private static File getTestDirectory() throws BridgeDBException {
        if (testDir == null){
            testDir = getDirectory(TEST_DIRECTORY_PROPERTY, "TestDirectory");
            deleteChildren(testDir);
            if (!testDir.exists()){
                boolean made = testDir.mkdir();
                if (made == false){
                    throw new BridgeDBException("Unable to create testdir " + testDir.getAbsolutePath());                
                }
            }
        }
        return testDir;
    }        

    private static File getDirectory(String property, String type) throws BridgeDBException {
        String fileName = getProperties().getProperty(property);
        if (fileName == null || fileName.isEmpty()){
            int error = 1/0;
            logger.warn("No directory property found for " + type + " so just using " + type + " as a relative file name");
            fileName = type;
        }
        File file = new File(fileName);
        if (!file.exists()){
            File parent = file.getParentFile();
            if (parent == null || parent.isDirectory()){
                boolean made = file.mkdir();
                if (!made){
                    throw new BridgeDBException("Unable to create " + type + " directory " + file.getAbsolutePath());
                }
            } else {
                throw new BridgeDBException("No parent (" + parent.getAbsolutePath() + ")found for " + type + " directory " + file.getAbsolutePath());
            }
        }
        if (!file.isDirectory()){
            throw new BridgeDBException(type + " directory " + file.getAbsolutePath() + " is not a directory");            
        }
        return file;
    }
     
    private static void deleteChildren(File testFile) throws BridgeDBException {
        if (testFile.isFile()){
            checkOkToDelete(testFile);
            boolean check = testFile.delete();
            if (!check){
                logger.warn("Unable to delete test file "+ testFile);
            }
        } else if (testFile.isDirectory()){
            File[] children = testFile.listFiles();
            for (File child:children){
                deleteChildren(child);
            }
            testFile.delete();
        }
    }

    private static void checkOkToDelete(File file) throws BridgeDBException {
        String name = file.getName();
        if (name.endsWith(".ttl")) return ;
        if (name.endsWith(".n3")) return ;
        if (name.endsWith(".xml")) return ;
        if (name.endsWith(".txt")) return ;
        throw new BridgeDBException("Unexpected file being deleted " + file.getAbsolutePath());
    }

}
