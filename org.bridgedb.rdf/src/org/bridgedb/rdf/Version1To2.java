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
import java.io.IOException;
import org.apache.log4j.Logger;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;

/**
 *
 * @author Christian
 */
public class Version1To2 {
    static final Logger logger = Logger.getLogger(Version1To2.class);

    public static void main(String[] args) throws BridgeDBException, IOException {
        ConfigReader.logToConsole();
        BioDataSource.init();
        File bioFile = new File("../org.bridgedb.utils/resources/BioDataSource.ttl");
        //BridgeDBRdfHandler.parseRdfFile(bioFile);
        
        File v1File = new File ("../org.bridgedb.rdf/resources/AndraDataSource.ttl");
        //BridgeDBRdfHandler.parseRdfFile(v1File);
        
        
        BridgeDBRdfHandler.writeRdfToFile(bioFile, false);
        BridgeDBRdfHandler.parseRdfFile(bioFile);

        
    }

}
