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
package org.bridgedb.rdf;

import java.io.File;
import java.util.Properties;
import org.bridgedb.IDMapperException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.StoreType;

/**
 *
 * @author Christian
 */
public class RdfConfig {
    public static final String SAIL_NATIVE_STORE_PROPERTY = "SailNativeStore";
    public static final String LOAD_SAIL_NATIVE_STORE_PROPERTY = "LoadSailNativeStore";
    public static final String TEST_SAIL_NATIVE_STORE_PROPERTY = "TestSailNativeStore";
    public static final String BASE_URI_PROPERTY = "BaseURI";
    public static final String CONFIG_FILE_NAME = "rdfConfig.txt";

    private static String path;
    private static String baseURI = null;
    
    private static Properties properties;

    private static boolean repositoryExists(StoreType storeType) throws IDMapperException {
        File dataDir = getDataDir(storeType);
        return dataDir.exists();
    }
    
    public static String getTheBaseURI() throws IDMapperException {
        if (baseURI == null){
            baseURI = getProperties().getProperty(BASE_URI_PROPERTY).trim();
            if (!baseURI.endsWith("/")){
                baseURI = baseURI + "/";
            }
        }
        return baseURI;
    }
    
    public static File getDataDir(StoreType storeType) throws IDMapperException {
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

    public static boolean uniqueLoadRepository() throws IDMapperException{
        return (!getSailNativeStore().equals(getLoadSailNativeStore()));
    }
    
    
    public static String getProperty(String key) throws IDMapperException{
        return getProperties().getProperty(key);
    }
    
    private static String getSailNativeStore() throws IDMapperException{
        String result;
        try {
            result = getProperties().getProperty(SAIL_NATIVE_STORE_PROPERTY).trim();
        } catch (IDMapperLinksetException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return "../rdf/linksets";
    }

    private static String getLoadSailNativeStore() throws IDMapperException{
        String result;
        try {
            result = getProperties().getProperty(LOAD_SAIL_NATIVE_STORE_PROPERTY).trim();
        } catch (IDMapperLinksetException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return getSailNativeStore();
    }

    private static String getTestSailNativeStore() throws IDMapperException{
        String result;
        try {
            result = getProperties().getProperty(TEST_SAIL_NATIVE_STORE_PROPERTY).trim();
        } catch (IDMapperLinksetException ex) {
            return ex.getMessage();
        }
        if (result != null) return result;
        return "../rdf/testLinksets";
    }
 
    private static Properties getProperties() throws IDMapperException{
        if (properties == null){
            properties = ConfigReader.getProperties(CONFIG_FILE_NAME);
        }
        return properties;
    }
    
}
