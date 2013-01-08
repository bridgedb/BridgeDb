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
        //InputStream stream = ConfigReader.getInputStream("BioDataSource.ttl");
        //StatementReaderAndImporter reader = new StatementReaderAndImporter(stream, RDFFormat.TURTLE, StoreType.TEST);
        File v1File = new File ("C:/OpenPhacts/BioDataSource.ttl");
        BridgeDBRdfHandler.parseRdfFile(v1File);
        
        File v2File = new File("../org.bridgedb.utils/resources/BioDataSource.ttl");
        BridgeDBRdfHandler.writeRdfToFile(v2File, true);

        BridgeDBRdfHandler.parseRdfFile(v2File);
        
    }

}
