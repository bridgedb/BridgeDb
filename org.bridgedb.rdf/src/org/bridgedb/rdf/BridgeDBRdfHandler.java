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
public class BridgeDBRdfHandler {
   
    static final Logger logger = Logger.getLogger(BridgeDBRdfHandler.class);
    public static String DEFAULT_BASE_URI = "http://no/BaseURI/Set/";
    public static RDFFormat DEFAULT_FILE_FORMAT = RDFFormat.RDFXML;
   
    public static void main(String[] args) throws RepositoryException, BridgeDBException, IOException, RDFParseException, RDFHandlerException {
        ConfigReader.logToConsole();
        File file1 = new File ("C:/OpenPhacts/BioDataSource.ttl");
        parseRdfFile(file1);
        File file2 = new File ("c:/temp/writertest.ttl");
        writeRdfToFile(file2);
    }

    public static void parseRdfFile(File file) throws BridgeDBException{
        Repository repository = null;
        RepositoryConnection repositoryConnection = null;
        try {
            repository = new SailRepository(new MemoryStore());
            repository.initialize();
            repositoryConnection = repository.getConnection();
            repositoryConnection.add(file, DEFAULT_BASE_URI, getFormat(file));
            DataSourceUris.readAllDataSources(repositoryConnection);
            UriPattern.readAllUriPatterns(repositoryConnection);      
        } catch (Exception ex) {
            throw new BridgeDBException ("Error parsing RDf file ", ex);
        } finally {
            shutDown(repository, repositoryConnection);
        }
    }
    
    public static void writeRdfToFile(File file) throws BridgeDBException{
        Repository repository = null;
        RepositoryConnection repositoryConnection = null;
        try {
            repository = new SailRepository(new MemoryStore());
            repository.initialize();
            repositoryConnection = repository.getConnection();
            DataSourceUris.addAll(repositoryConnection);
            OrganismRdf.addAll(repositoryConnection);
            UriPattern.addAll(repositoryConnection);
            writeRDF(repositoryConnection, file);        
        } catch (Exception ex) {
            throw new BridgeDBException ("Error writing Rdf to file ", ex);
        } finally {
            shutDown(repository, repositoryConnection);
        }
    }
    
    private static void writeRDF(RepositoryConnection repositoryConnection, File file) 
            throws IOException, RDFHandlerException, RepositoryException{
        Writer writer = new FileWriter (file);
        TurtleWriter turtleWriter = new TurtleWriter(writer);
        writeRDF(repositoryConnection, turtleWriter);
        writer.close();
    }
    
    private static void writeRDF(RepositoryConnection repositoryConnection, RDFWriter rdfWriter) 
            throws IOException, RDFHandlerException, RepositoryException{ 
        rdfWriter.handleNamespace(BridgeDBConstants.PREFIX_NAME1, BridgeDBConstants.PREFIX);
        rdfWriter.handleNamespace("", DEFAULT_BASE_URI);
        rdfWriter.startRDF();
        RepositoryResult<Statement> statements = 
                repositoryConnection.getStatements(null, null, null, true);
        while (statements.hasNext()) {
            Statement statement = statements.next();
            rdfWriter.handleStatement(statement);
        }
        rdfWriter.endRDF();
    }
    
    private static void shutDown(Repository repository, RepositoryConnection repositoryConnection){
        if (repositoryConnection != null){
            try {            
                repositoryConnection.close();
            } catch (RepositoryException ex) {
                logger.error("Error closing connection", ex);
            }
        }
        if (repository != null){
            try {            
                repository.shutDown();
            } catch (RepositoryException ex) {
                logger.error("Error shutting down repository", ex);
            }
        }
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
