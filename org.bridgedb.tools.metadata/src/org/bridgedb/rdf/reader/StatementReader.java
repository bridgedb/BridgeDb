// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.rdf.reader;

import info.aduna.lang.FileFormat;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.Reporter;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFParserFactory;
import org.openrdf.rio.RDFParserRegistry;
import org.openrdf.rio.helpers.RDFHandlerBase;

/**
 *
 * @author Christian
 */
public class StatementReader extends RDFHandlerBase implements VoidStatements {
 
    public static RDFFormat DEFAULT_FILE_FORMAT = RDFFormat.RDFXML;
    public static String DEFAULT_BASE_URI = "http://no/BaseURI/Set/";

    static final Logger logger = Logger.getLogger(StatementReader.class);

    public static Set<RDFFormat> getSupportedFormats() {
        RDFParserRegistry reg = RDFParserRegistry.getInstance();
        return reg.getKeys();
    }
    
    protected Set<Statement> statements = new HashSet<Statement>();
    boolean parsed = false;
    
    public StatementReader(String address) throws BridgeDBException{
        address = address.trim();
        if (address.startsWith("http")){
            parse(address);
        } else {
            parse(new File(address), DEFAULT_BASE_URI);
        }
    }
    
    public StatementReader(File file) throws BridgeDBException{
        parse(file, DEFAULT_BASE_URI);
    }

    public StatementReader(String info, RDFFormat format) throws BridgeDBException{
        parse(info, format, DEFAULT_BASE_URI);
    }

    public StatementReader(InputStream inputStream, RDFFormat format) throws BridgeDBException{
        parse(inputStream, format, DEFAULT_BASE_URI);
    }

    private void parse(String info, RDFFormat format, String baseURI) throws BridgeDBException{
        if (format == null){
            throw new BridgeDBException ("RDFFormat may not be null");
        }
        StringReader reader = new StringReader(info);
        RDFParser parser = getParser(format);
        try {
            parse(reader, parser, baseURI);
        } catch (IOException ex) {
            throw new BridgeDBException("Error reading input" + ex.getMessage(), ex);
        } catch (OpenRDFException ex) {
            throw new BridgeDBException("Error parsing input " + ex.getMessage(), ex);
        } finally {
            if (reader != null){
                reader.close();
            }
        }        
    }
    
    private void parse(String address) throws BridgeDBException{
        InputStreamReader reader = null;
        try {
            UrlReader urlReader = new  UrlReader(address);
            String baseURI = urlReader.getPath();
            RDFParser parser = getParser(baseURI);
            InputStream inputStream = urlReader.getInputStream();
            reader = new InputStreamReader(inputStream);
            parse(reader, parser, baseURI);
        } catch (Exception ex) {
            throw new BridgeDBException("Error reading " + address, ex);
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException ex) {
                    throw new BridgeDBException("Error closing input Stream. ", ex);
                }
            }
        }        
    }
    
    private void parse(InputStream inputStream, RDFFormat format, String baseURI) throws BridgeDBException{
        if (format == null){
            throw new BridgeDBException ("RDFFormat may not be null");
        }
        InputStreamReader reader = new InputStreamReader(inputStream);
        RDFParser parser = getParser(format);
        try {
            parse(reader, parser, baseURI);
        } catch (IOException ex) {
            throw new BridgeDBException("Error reading input" + ex.getMessage(), ex);
        } catch (OpenRDFException ex) {
            throw new BridgeDBException("Error parsing input " + ex.getMessage(), ex);
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException ex) {
                    throw new BridgeDBException("Error closing input Stream. ", ex);
                }
            }
        }        
    }

    private void parse(File file, String baseURI) throws BridgeDBException{
        FileReader reader = null;
        RDFParser parser = getParser(file);
        try {
            reader = new FileReader(file);
            parse(reader, parser, baseURI);
        } catch (IOException ex) {
            throw new BridgeDBException("Error reading file " + 
            		file.getAbsolutePath() + " " + ex.getMessage(), ex);
        } catch (OpenRDFException ex) {
            throw new BridgeDBException("Error parsing file " + 
            		file.getAbsolutePath()+ " " + ex.getMessage(), ex);
        } finally {
            try {
                if (reader != null){
                    reader.close();
                }
            } catch (IOException ex) {
                logger.info(ex.getMessage());
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
            if (reader != null){
                reader.close();
            }
        }        
    }
    
    private static RDFParser getParser(File file){
        String fileName = file.getName();
        return getParser(fileName);
    }
    
    private static RDFParser getParser(String fileName){
        if (fileName.endsWith(".n3")){
            fileName = "try.ttl";
        }
        RDFParserRegistry reg = RDFParserRegistry.getInstance();
        FileFormat fileFormat = reg.getFileFormatForFileName(fileName);
        if (fileFormat == null || !(fileFormat instanceof RDFFormat)){
            //added bridgeDB/OPS specific extension here if required.           
            logger.warn("OpenRDF does not know the RDF Format for " + fileName);
            logger.warn("Using the default format " + DEFAULT_FILE_FORMAT);
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

    public static RDFFormat getRDFFormatByMimeType(String mimeType) throws BridgeDBException{
        if (mimeType == null){
            throw new BridgeDBException("Illegal null input to getRDFFormatByMimeType(String)");
        }
        if (mimeType.isEmpty()){
            throw new BridgeDBException("Illegal empty input to getRDFFormatByMimeType(String)");
        }
        RDFParserRegistry reg = RDFParserRegistry.getInstance();
        FileFormat fileFormat = reg.getFileFormatForMIMEType(mimeType);
        if (fileFormat == null || !(fileFormat instanceof RDFFormat)){
            //added bridgeDB/OPS specific extension here if required.           
            throw new BridgeDBException("OpenRDF does not know the RDF Format for " + mimeType
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
        
    static public void main(String[] args) throws BridgeDBException {
        RDFParserRegistry reg = RDFParserRegistry.getInstance();
        Set<RDFFormat> keys = reg.getKeys();
        for (RDFFormat key:keys){
            Reporter.println("key: "+key);
            Reporter.println("   "+key.getName());
            Reporter.println("   "+key.getDefaultMIMEType());
        }
        Reporter.println(supportedMineTypes());
    }

    @Override
    public Set<Statement> getVoidStatements(){
        return statements;
    }
    
    @Override
    public void resetBaseURI(String newBaseURI) {
        statements = resetBaseURI(newBaseURI, statements);
    }

    private static URI resetBaseURI(String newBaseURI, URI oldURI){
        String oldName = oldURI.stringValue();
        if (oldName.startsWith(DEFAULT_BASE_URI)){
            if (oldName.startsWith(DEFAULT_BASE_URI + "#")){        
                return new URIImpl(oldName.replace(DEFAULT_BASE_URI+"#", newBaseURI));
            } else {
                return new URIImpl(oldName.replace(DEFAULT_BASE_URI, newBaseURI));                
            }
        }
        return oldURI;
    }
    
    private static Value resetBaseURI(String newBaseURI, Value oldValue){
        if (oldValue instanceof URI){
            return resetBaseURI(newBaseURI, (URI)oldValue);
        } else {
            return oldValue;
        }
    }
    
    public static Resource resetBaseURI(String newBaseURI, Resource oldValue){
        if (oldValue instanceof URI){
            return resetBaseURI(newBaseURI, (URI)oldValue);
        } else {
            return oldValue;
        }
    }

    public static Set<Statement> resetBaseURI(String newBaseURI, Set<Statement> oldStatements) {
        Set<Statement> newstatements = new HashSet<Statement>();
        for (Statement statement:oldStatements){
            Resource newResource = resetBaseURI(newBaseURI, statement.getSubject());
            URI newPredicate = resetBaseURI(newBaseURI, statement.getPredicate());
            Value newObject = resetBaseURI(newBaseURI, statement.getObject());
            statement = new StatementImpl(newResource, newPredicate, newObject);
            newstatements.add(statement);
        }
        return newstatements;
    }

}
