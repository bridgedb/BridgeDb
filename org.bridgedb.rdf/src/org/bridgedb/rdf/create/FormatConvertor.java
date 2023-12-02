/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf.create;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Optional;

import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.Reporter;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParserRegistry;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.turtle.TurtleWriter;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

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
        Optional<RDFFormat> fileFormat = reg.getFileFormatForFileName(fileName);
        if (fileFormat.get() == null){
            //added bridgeDB/OPS specific extension here if required.  
            throw new BridgeDBException("failed");
        } else {
            return fileFormat.get();
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
            repository.init();
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
