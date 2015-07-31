/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf.create;

import info.aduna.lang.FileFormat;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.Reporter;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFParserRegistry;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.turtle.TurtleWriter;
import org.openrdf.sail.memory.MemoryStore;

/**
 *
 * @author Christian
 */
public class FormatConvertor {
    
   public static String DEFAULT_BASE_URI = "http://no/BaseURI/Set/";
   
    private static RDFFormat getFormat(String fileName) throws BridgeDBException{
        if (fileName.endsWith(".n3")){
            fileName = "try.ttl";
        }
        RDFParserRegistry reg = RDFParserRegistry.getInstance();
        FileFormat fileFormat = reg.getFileFormatForFileName(fileName);
        if (fileFormat == null || !(fileFormat instanceof RDFFormat)){
            //added bridgeDB/OPS specific extension here if required.  
            throw new BridgeDBException("failed");
        } else {
            return (RDFFormat)fileFormat;
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
        rdfWriter.handleNamespace(BridgeDBConstants.PREFIX_NAME, BridgeDBConstants.PREFIX);
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
    
   public static void convert(File inputFile, File outputFile) throws Exception {
        Reporter.println("Parsing " + inputFile.getAbsolutePath());
        Repository repository = null;
        RepositoryConnection repositoryConnection = null;
        try {
            repository = new SailRepository(new MemoryStore());
            repository.initialize();
            repositoryConnection = repository.getConnection();
            repositoryConnection.add(inputFile, DEFAULT_BASE_URI, getFormat(inputFile.getName()));
            writeRDF(repositoryConnection, outputFile);
        } catch (Exception ex) {
            throw new BridgeDBException ("Error parsing RDf file ", ex);
        } finally {
            repositoryConnection.close();
        }
    }

    public static void main(String[] args) throws Exception {
        convert(new File("C:/Temp/biomodels_void-dcat.rdf"), new File("C:/Temp/biomodels_void-dcat.ttl"));
        convert(new File("C:/Temp/biomodels_void-ops.rdf"), new File("C:/Temp/biomodels_void-ops.ttl"));
        convert(new File("C:/Temp/example_void-id.org.rdf"), new File("C:/Temp/example_void-id.org.ttl"));
    }
}
