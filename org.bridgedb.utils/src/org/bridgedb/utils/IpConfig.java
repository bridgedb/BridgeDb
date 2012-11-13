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
package org.bridgedb.utils;
import org.bridgedb.rdf.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;
import org.bridgedb.IDMapperException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.StoreType;

/**
 *
 * @author Christian
 */
public class IpConfig {

    static final Logger logger = Logger.getLogger(IpConfig.class);

    public static final String CONFIG_FILE_NAME = "IP_Register.txt";

    private static Properties properties;

    public static String checkIPAddress(String ipAddress) throws IDMapperException{
        String owner = getProperties().getProperty(ipAddress);
        if (owner == null){
            logger.warn("Attempt to check IP address " + ipAddress);
        }
        return owner;
    }
    
    private static Properties getProperties() throws IDMapperException{
        if (properties == null){
            properties = ConfigReader.getProperties(CONFIG_FILE_NAME);
        }
        return properties;
    }
    
    public static void main(String[] args) throws IDMapperException  {
        Set<Object> keys = getProperties().keySet();
        System.out.println(keys);
    }
}
