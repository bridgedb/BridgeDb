/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf.create;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
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
public class LinkSetCombiner {
    
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
    
   public static void convert(Collection<File> inputFiles, File outputFile) throws Exception {
        Repository repository = null;
        RepositoryConnection repositoryConnection = null;
        try {
            repository = new SailRepository(new MemoryStore());
            repository.init();
            repositoryConnection = repository.getConnection();
            for (File inputFile:inputFiles){
                Reporter.println("Parsing " + inputFile.getAbsolutePath());
                repositoryConnection.add(inputFile, DEFAULT_BASE_URI, getFormat(inputFile.getName()));
            }
            writeRDF(repositoryConnection, outputFile);
        } catch (Exception ex) {
            throw new BridgeDBException ("Error parsing RDf file ", ex);
        } finally {
            repositoryConnection.close();
        }
    }

   public static long count(File inputFile) throws Exception {
        Repository repository = null;
        RepositoryConnection repositoryConnection = null;
        try {
            repository = new SailRepository(new MemoryStore());
            repository.init();
            repositoryConnection = repository.getConnection();
            Reporter.println("Parsing (to count)" + inputFile.getAbsolutePath());
            repositoryConnection.add(inputFile, DEFAULT_BASE_URI, getFormat(inputFile.getName()));
            long result = repositoryConnection.size();
            System.out.println("Count is " + result);
            return result;
        } catch (Exception ex) {
            throw new BridgeDBException ("Error parsing RDf file ", ex);
        } finally {
            repositoryConnection.close();
        }
   }
   
    public static void main(String[] args) throws Exception {
        long total = 0;
        HashSet<File> files = new HashSet<File>();
        File f1 = new File("C:/Dropbox/ims/linkset/version1.3/CRS/CHEBI/LINKSET_EXACT_OPS_CHEMSPIDER_CHEBI20131111.ttl");
        total+= count(f1);
        files.add(f1);
        File f2 = new File("C:/Dropbox/ims/linkset/version1.3/CRS/CHEMBL/LINKSET_EXACT_OPS_CHEMSPIDER_CHEMBL20131111.ttl");
        total+=  count(f2);
        files.add(f2);
        File f3 = new File("C:/Dropbox/ims/linkset/version1.3/CRS/DRUGBANK/LINKSET_EXACT_OPS_CHEMSPIDER_DRUGBANK20131111.ttl");
        total+=  count(f3);
        files.add(f3);
        File f4 = new File("C:/Dropbox/ims/linkset/version1.3/CRS/HMDB/LINKSET_EXACT_OPS_CHEMSPIDER_HMDB20131111.ttl");
        total+=  count(f4);
        files.add(f4);
        File f5 = new File("C:/Dropbox/ims/linkset/version1.3/CRS/MESH/LINKSET_EXACT_OPS_CHEMSPIDER_MESH20131111.ttl");
        total+=  count(f5);
        files.add(f5);
        File f6 = new File("C:/Dropbox/ims/linkset/version1.3/CRS/PDB/LINKSET_EXACT_OPS_CHEMSPIDER_PDB20131111.ttl");
        total+=  count(f6);
        files.add(f6);
        File fresult = new File("C:/Temp/CRS_combined.ttl");
        convert(files, fresult);
        long combined = count(fresult);
        System.out.println("Detected " + (combined - total) + " duplicates");
    }
}
