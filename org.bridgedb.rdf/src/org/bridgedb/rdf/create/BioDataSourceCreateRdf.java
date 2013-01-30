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
package org.bridgedb.rdf.create;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.DataSourceOverwriteLevel;
import org.bridgedb.IDMapper;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.rdf.BridgeDBRdfHandler;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;

/**
 * This will create rdf files passed purely on the current state of BioDataSource package.
 * 
 * Note: This can not be run as a unit test as BioDataSource loads some data which is different in the DataSource.ttl
 * For example the runPattern for the wikiPathways DataSource which is non Miriam in BioDataSource
 * @author Christian
 */
public class BioDataSourceCreateRdf {
    
    private IDMapper mapper;
    private String name;
    private BufferedWriter buffer;
    private String sourceUriSpace;
    private String targetUriSpace;
    private final String LINK_PREDICATE = "skos:relatedMatch";
    
    static final Logger logger = Logger.getLogger(BioDataSourceCreateRdf.class);

    public static void main(String[] args) throws BridgeDBException, IOException, ClassNotFoundException{
        ConfigReader.logToConsole();

        //Load DataSources from BioDataSource and save that
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
        BioDataSource.init();
        File biofile = new File("../org.bridgedb.rdf/resources/BioDataSource.ttl");
        BridgeDBRdfHandler.writeRdfToFile(biofile, false);
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
        BridgeDBRdfHandler.parseRdfFile(biofile);        
                
        //Write a file marking the primary URIs as currently set
        File primaryfile = new File("../org.bridgedb.rdf/resources/BioDataSourceWithPrimary.ttl");
        BridgeDBRdfHandler.writeRdfToFile(primaryfile, true);
        
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
        BridgeDBRdfHandler.parseRdfFile(primaryfile);
        
     }

}
