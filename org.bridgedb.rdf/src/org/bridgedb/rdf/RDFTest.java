/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import info.aduna.lang.FileFormat;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import org.apache.log4j.Logger;
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.rdf.constants.RdfConstants;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParserRegistry;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.turtle.TurtleWriter;
import org.openrdf.sail.memory.MemoryStore;

/**
 *
 * @author Christian
 */
public class RDFTest {
   
    static final Logger logger = Logger.getLogger(RDFTest.class);
    public static String DEFAULT_BASE_URI = "http://no/BaseURI/Set/";
    public static RDFFormat DEFAULT_FILE_FORMAT = RDFFormat.RDFXML;
   
    public static void main(String[] args) throws RepositoryException, BridgeDBException, IOException, RDFParseException, RDFHandlerException {
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
        UriPattern.readAllUriPatterns(con);
        writeRDF(con);
        con.close();
    }

    private static void writeRDF(RepositoryConnection repositoryConnection) throws IOException, RDFHandlerException, RepositoryException{
        File file = new File ("c:/temp/writertest.ttl");
        Writer writer = new FileWriter (file);
        TurtleWriter turtleWriter = new TurtleWriter(writer);
        writeRDF(repositoryConnection, turtleWriter);
        writer.close();
    }
    
    private static void writeRDF(RepositoryConnection repositoryConnection, RDFWriter rdfWriter) 
            throws IOException, RDFHandlerException, RepositoryException{ 
        rdfWriter.handleNamespace(BridgeDBConstants.PREFIX_NAME, BridgeDBConstants.PREFIX);
        rdfWriter.handleNamespace("", DEFAULT_BASE_URI);
        rdfWriter.handleNamespace("junk", "JUNK");
        rdfWriter.startRDF();
        RepositoryResult<Statement> statements = 
                repositoryConnection.getStatements(null, null, null, true);
        while (statements.hasNext()) {
            Statement statement = statements.next();
            rdfWriter.handleStatement(statement);
        }
        rdfWriter.endRDF();
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
