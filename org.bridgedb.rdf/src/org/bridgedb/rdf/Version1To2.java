/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.bridgedb.IDMapperException;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.utils.ConfigReader;

/**
 *
 * @author Christian
 */
public class Version1To2 {
    static final Logger logger = Logger.getLogger(Version1To2.class);

    public static void main(String[] args) throws IDMapperException, IOException {
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
