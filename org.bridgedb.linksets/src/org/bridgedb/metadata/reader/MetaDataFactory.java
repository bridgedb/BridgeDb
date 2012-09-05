/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.reader;

import info.aduna.lang.FileFormat;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import javax.activation.FileDataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.linkset.IDMapperLinksetException;
import org.bridgedb.linkset.LinksetParserErrorListener;
import org.bridgedb.metadata.DataSetMetaData;
import org.bridgedb.metadata.DescriptionMetaData;
import org.bridgedb.metadata.LinkSetMetaData;
import org.bridgedb.metadata.MetaData;
import org.bridgedb.metadata.RDFData;
import org.bridgedb.rdf.RdfWrapper;
import org.bridgedb.utils.Reporter;
import org.openrdf.OpenRDFException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFParserRegistry;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.openrdf.rio.turtle.TurtleParser;

/**
 *
 * @author Christian
 */
public class MetaDataFactory extends RDFHandlerBase{
 
    public static RDFFormat DEFAULT_FILE_FORMAT = RDFFormat.RDFXML;
    
    public static MetaData makeSpecific(RDFData input){
        if (input.hasPredicate(DescriptionMetaData.RESOURCE_TYPE)){
            return new DescriptionMetaData(input);
        }
        if (input.hasPredicate(LinkSetMetaData.RESOURCE_TYPE)){
            return new LinkSetMetaData(input);
        }
        if (input.hasPredicate(DataSetMetaData.RESOURCE_TYPE)){
            return new DataSetMetaData(input);
        }       
        //Will b=nearly certainly be invalid but last ditch effort
        return new DescriptionMetaData(input);
    }
    
    public static MetaData readVoid (File file) throws IDMapperException{
        RDFData input = parseVoid(file);
        return makeSpecific(input);
    }
    
    private static RDFData parseVoid (File file) throws IDMapperException{
        if (!file.isFile()){
            throw new IDMapperException (file.getAbsolutePath() + " is not a file");
        }
        Reporter.report("Parsing file:\n\t" + file.getAbsolutePath());
        FileReader reader = null;
        try {
            RDFParser parser = getParser(file);
            RDFDataReader handler = new RDFDataReader();
            parser.setRDFHandler(handler);
            parser.setParseErrorListener(new LinksetParserErrorListener());
            parser.setVerifyData(true);
            reader = new FileReader(file);
            parser.parse (reader, RdfWrapper.getBaseURI());
            return handler.getRDFData();
        } catch (IOException ex) {
            throw new IDMapperLinksetException("Error reading file " + 
            		file.getAbsolutePath() + " " + ex.getMessage(), ex);
        } catch (OpenRDFException ex) {
            throw new IDMapperLinksetException("Error parsing file " + 
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
    
    public static void main(String[] args) {
        RDFParserRegistry reg = RDFParserRegistry.getInstance();
        Set<RDFFormat> formats = reg.getKeys();
        for (RDFFormat format:formats){
            System.out.println(format);
            System.out.println(reg.get(format));
            System.out.println(reg.get(format).getParser());
        }
    }
}
