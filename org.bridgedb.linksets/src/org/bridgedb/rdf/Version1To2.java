/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import org.apache.log4j.Logger;
import org.bridgedb.IDMapperException;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.StoreType;
import org.openrdf.model.Statement;

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
        StatementReaderAndImporter reader1 = new StatementReaderAndImporter(v1File, StoreType.TEST);
        Set<Statement> allStatements1 = reader1.getVoidStatements();
        DataSourceImporter.load(allStatements1);
        
        File v2File = new File("../org.bridgedb.utils/resources/BioDataSource.ttl");
        DataSourceExporter.export(v2File);

        StatementReaderAndImporter reader2 = new StatementReaderAndImporter(v2File, StoreType.TEST);
        Set<Statement> allStatements2 = reader2.getVoidStatements();
        DataSourceImporter.load(allStatements2);
    }

}
