/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import info.aduna.lang.FileFormat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.utils.Reporter;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFParserFactory;
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
   
    void parse(String info, RDFFormat format, String baseURI) throws MetaDataException{
        if (format == null){
            throw new MetaDataException ("RDFFormat may not be null");
        }
        StringReader reader = new StringReader(info);
        RDFParser parser = getParser(format);
        try {
            parse(reader, parser, baseURI);
        } catch (IOException ex) {
            throw new MetaDataException("Error reading input" + ex.getMessage(), ex);
        } catch (OpenRDFException ex) {
            throw new MetaDataException("Error parsing input " + ex.getMessage(), ex);
        } finally {
            if (reader != null){
                reader.close();
            }
        }        
    }
    
    void parse(File file, String baseURI) throws MetaDataException{
        FileReader reader = null;
        RDFParser parser = getParser(file);
        try {
            reader = new FileReader(file);
            parse(reader, parser, baseURI);
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
    
    void parse(Reader reader, RDFParser parser, String baseURI) throws IOException, OpenRDFException {
        try {
            parser.setRDFHandler(this);
            parser.setParseErrorListener(new LinksetParserErrorListener());
            parser.setVerifyData(true);
            parser.parse (reader, baseURI);
            parsed = true;
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
        String fileName = file.getName();
        RDFParserRegistry reg = RDFParserRegistry.getInstance();
        FileFormat fileFormat = reg.getFileFormatForFileName(fileName);
        if (fileFormat == null || !(fileFormat instanceof RDFFormat)){
            //added bridgeDB/OPS specific extension here if required.           
            Reporter.report("OpenRDF does not know the RDF Format for " + fileName);
            Reporter.report("Using the default format " + DEFAULT_FILE_FORMAT);
            return reg.get(DEFAULT_FILE_FORMAT).getParser();
        } else {
            RDFFormat format = (RDFFormat)fileFormat;
            return reg.get(format).getParser();
        }
    }
    
    private RDFParser getParser(RDFFormat format) {
        RDFParserRegistry reg = RDFParserRegistry.getInstance();
        RDFParserFactory factory = reg.get(format);
        return reg.get(format).getParser();
    }

    public static RDFFormat getRDFFormatByMimeType(String mimeType) throws MetaDataException{
        if (mimeType == null){
            throw new MetaDataException("Illegal null input to getRDFFormatByMimeType(String)");
        }
        if (mimeType.isEmpty()){
            throw new MetaDataException("Illegal empty input to getRDFFormatByMimeType(String)");
        }
        RDFParserRegistry reg = RDFParserRegistry.getInstance();
        FileFormat fileFormat = reg.getFileFormatForMIMEType(mimeType);
        if (fileFormat == null || !(fileFormat instanceof RDFFormat)){
            //added bridgeDB/OPS specific extension here if required.           
            throw new MetaDataException("OpenRDF does not know the RDF Format for " + mimeType
                    + ". Legal values are " + supportedMineTypes());
        } else {
            return (RDFFormat)fileFormat;
        }
    }

    public void handleStatement(Statement st) throws RDFHandlerException {
        statements.add(st);
    }

    public static String supportedMineTypes(){
        List<String> mineTypes = new ArrayList<String>();
        RDFParserRegistry reg = RDFParserRegistry.getInstance();
        Set<RDFFormat> keys = reg.getKeys();
        for (RDFFormat key:keys){
            mineTypes.addAll(key.getMIMETypes());
        }
        return mineTypes.toString();
    }
        
    static public void main(String[] args) {
        RDFParserRegistry reg = RDFParserRegistry.getInstance();
        Set<RDFFormat> keys = reg.getKeys();
        for (RDFFormat key:keys){
            System.out.println(key);
        }
        System.out.println(supportedMineTypes());
    }

}
