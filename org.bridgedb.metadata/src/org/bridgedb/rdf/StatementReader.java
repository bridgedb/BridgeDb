/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import info.aduna.lang.FileFormat;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.utils.Reporter;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFParserRegistry;
import org.openrdf.rio.helpers.RDFHandlerBase;

/**
 *
 * @author Christian
 */
public class StatementReader extends RDFHandlerBase{
 
    public static RDFFormat DEFAULT_FILE_FORMAT = RDFFormat.RDFXML;
    public static String DEFAULT_BASE_URI = "http://no/BaseURI/Set/";

    public static Set<RDFFormat> getSupportedFormats() {
        RDFParserRegistry reg = RDFParserRegistry.getInstance();
        return reg.getKeys();
    }
    
    Set<Statement> statements = new HashSet<Statement>();
    boolean parsed = false;
    
    StatementReader() {
    }
   
    void parse(File file, String baseURI) throws MetaDataException{
        FileReader reader = null;
        try {
            RDFParser parser = getParser(file);
            parser.setRDFHandler(this);
            parser.setParseErrorListener(new LinksetParserErrorListener());
            parser.setVerifyData(true);
            reader = new FileReader(file);
            parser.parse (reader, baseURI);
            parsed = true;
        } catch (IOException ex) {
            throw new MetaDataException("Error reading file " + 
            		file.getAbsolutePath() + " " + ex.getMessage(), ex);
        } catch (OpenRDFException ex) {
            throw new MetaDataException("Error parsing file " + 
            		file.getAbsolutePath()+ " " + ex.getMessage(), ex);
        } finally {
            try {
                if (reader != null){
                    reader.close();
                }
            } catch (IOException ex) {
                Reporter.report(ex.getMessage());
            }
        }        
    }
    
    public static Set<Statement> extractStatements (String  fileName) throws MetaDataException {
        File file = new File(fileName);
        return extractStatements(file);
    }
    
    public static Set<Statement> extractStatements (File file) throws MetaDataException {
        return extractStatements(file, DEFAULT_BASE_URI);
    }
        
    public static Set<Statement> extractStatements (File file, String baseURI) throws MetaDataException {
        StatementReader statementReader = new StatementReader();
        statementReader.parse(file, baseURI);
        return statementReader.statements;
    }
    
    private static RDFParser getParser(File file){
        return getParser(file.getName());
    }
    
    private static RDFParser getParser(String fileName){
        RDFParserRegistry reg = RDFParserRegistry.getInstance();
        FileFormat fileFormat = reg.getFileFormatForFileName(fileName);
        if (fileFormat == null){
            //added bridgeDB/OPS specific extension here if required.           
            Reporter.report("OpenRDF does not know the RDF Format for " + fileName);
            Reporter.report("Using the default format " + DEFAULT_FILE_FORMAT);
            return reg.get(DEFAULT_FILE_FORMAT).getParser();
        }
        if (fileFormat instanceof RDFFormat){
            RDFFormat format = (RDFFormat)fileFormat;
            return reg.get(format).getParser();
        } else {
            return reg.get(DEFAULT_FILE_FORMAT).getParser();
        }
    }
    
    public void handleStatement(Statement st) throws RDFHandlerException {
        statements.add(st);
    }

}
