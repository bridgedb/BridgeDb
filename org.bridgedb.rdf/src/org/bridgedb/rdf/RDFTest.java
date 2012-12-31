/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import info.aduna.lang.FileFormat;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParserRegistry;
import org.openrdf.sail.memory.MemoryStore;

/**
 *
 * @author Christian
 */
public class RDFTest {
   
    static final Logger logger = Logger.getLogger(RDFTest.class);
    public static String DEFAULT_BASE_URI = "http://no/BaseURI/Set/";
    public static RDFFormat DEFAULT_FILE_FORMAT = RDFFormat.RDFXML;
   
    public static void main(String[] args) throws RepositoryException, BridgeDBException, IOException, RDFParseException {
        ConfigReader.logToConsole();
        Repository myRepository = new SailRepository(new MemoryStore());
        myRepository.initialize();
        File file = new File ("C:/OpenPhacts/BioDataSource.ttl");
        RepositoryConnection con = myRepository.getConnection();
        con.add(file, DEFAULT_BASE_URI, getFormat(file));
        //RepositoryResult<Statement> statements = con.getStatements(null, null, null, true);
        //while (statements.hasNext()) {
        //    Statement statement = statements.next();
        //    System.out.println(statement);
        //}
        DataSourceRdf.readAllDataSources(con);
        con.close();
    }

    private static RDFFormat getFormat(File file){
        String fileName = file.getName();
        if (fileName.endsWith(".n3")){
            fileName = "try.ttl";
        }
        RDFParserRegistry reg = RDFParserRegistry.getInstance();
        FileFormat fileFormat = reg.getFileFormatForFileName(fileName);
        if (fileFormat == null || !(fileFormat instanceof RDFFormat)){
            //added bridgeDB/OPS specific extension here if required.           
            logger.warn("OpenRDF does not know the RDF Format for " + fileName);
            logger.warn("Using the default format " + DEFAULT_FILE_FORMAT);
            return DEFAULT_FILE_FORMAT;
        } else {
            return (RDFFormat)fileFormat;
        }
    }
    

}
