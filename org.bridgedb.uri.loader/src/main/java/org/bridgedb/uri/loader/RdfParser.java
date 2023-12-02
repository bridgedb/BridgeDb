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
package org.bridgedb.uri.loader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.bridgedb.uri.loader.transative.TransativeConfig;
import org.bridgedb.utils.BridgeDBException;
import org.eclipse.rdf4j.common.exception.RDF4JException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFParserFactory;
import org.eclipse.rdf4j.rio.RDFParserRegistry;
import org.eclipse.rdf4j.rio.turtle.TurtleParser;

public class RdfParser {
    
    private final RDFHandler handler;
    public static RDFParser DEFAULT_PARSER = new TurtleParser();
    private static final String GET_FORMAT_FROM_ADDRESS = null;
    
    public RdfParser(RDFHandler handler){
        this.handler = handler;
    }
    
    static final Logger logger = Logger.getLogger(RdfParser.class);
     
    public void parse(String baseURI, File file) throws BridgeDBException{
        parse(baseURI, file, GET_FORMAT_FROM_ADDRESS);
    }
    
    public void parse(String baseURI, File file, String rdfFormatName) throws BridgeDBException{
        logger.info("Parsing: " + file.getAbsolutePath());
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            RDFParser parser = getParser(file.getName(), rdfFormatName);
            parser.setRDFHandler(handler);
            parser.setParseErrorListener(new LinksetParserErrorListener());
            parser.parse (reader, baseURI);
        } catch (IOException ex) {
            throw new BridgeDBException("Error reading " + file.getAbsolutePath() + " " + ex.getMessage(), ex);
        } catch (RDF4JException ex) {
            throw new BridgeDBException("Error parsing " + file.getAbsolutePath() + " " + ex.getMessage(), ex);
        } finally {
            try {
                if (reader != null){
                    reader.close();
                }
            } catch (IOException ex) {
                throw new BridgeDBException("Error closing inputStream ", ex);
            }
        }
    }
    
    public void parse(String uri) throws BridgeDBException {
        parse(uri, GET_FORMAT_FROM_ADDRESS);        
    }
    
    public void parse(String uri, String rdfFormatName) throws BridgeDBException {
        InputStream stream = getInputStream(uri);
        parse(stream, uri, rdfFormatName);
    }

    public void parse(InputStream stream, String mappingSource, String rdfFormatName) throws BridgeDBException {
        logger.info("Parsing: " + mappingSource);
        try {
            RDFParser parser = getParser(mappingSource, rdfFormatName);
            parser.setRDFHandler(handler);
            parser.setParseErrorListener(new LinksetParserErrorListener());
            parser.parse (stream, mappingSource);
        } catch (IOException ex) {
            throw new BridgeDBException("Error reading " + mappingSource + " " + ex.getMessage(), ex);
        } catch (RDF4JException ex) {
            throw new BridgeDBException("Error parsing " + mappingSource + " " + ex.getMessage(), ex);
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {
                throw new BridgeDBException("Error closing inputStream ", ex);
            }
        }
    }

    public InputStream getInputStream(String uri) throws BridgeDBException {
        URL url;
        try {
            url = new URL(uri);
        } catch (MalformedURLException ex) {
            throw new BridgeDBException ("Unable to convert String to Uri:" + uri, ex);
        }
        InputStream inputStream;
        try {
            return url.openStream();
        } catch (IOException ex) {
            throw new BridgeDBException ("Unable to convert String to Uri:" + uri, ex);
        }
    }

    public static IRI fileToURL(File file) throws BridgeDBException{
        String baseURI = TransativeConfig.getTransitiveBaseUri();
        if (baseURI == null || baseURI.isEmpty()){
            return fileToURI(file); 
        }
        if (file.getParent().equals(TransativeConfig.getTransativeDirectory())){
            return SimpleValueFactory.getInstance().createIRI(baseURI + file.getName());
        } else {
            return RdfParser.fileToURI(file);
        }
    }
    
    private static IRI fileToURI(File file) throws BridgeDBException{
        try {
            String uri = file.toURI().toURL().toExternalForm();
            return SimpleValueFactory.getInstance().createIRI(uri);
        } catch (MalformedURLException ex) {
            throw new BridgeDBException("Unable to convert file to URI", ex);
        }
    }

    public static RDFParser getParser(String address, String rdfFormatName) throws BridgeDBException{
        RDFParserRegistry reg = RDFParserRegistry.getInstance();
        RDFFormat format = null;
        if (rdfFormatName == null || rdfFormatName.isEmpty()){
            if (address.endsWith(".gz")){
                address = address.substring(0, address.length()-3);
            }
            if (address.endsWith(".n3")){
                address = "try.ttl";
            }
            Optional<RDFFormat> fileFormat = reg.getFileFormatForFileName(address);
            if (fileFormat.isEmpty()){
                //added bridgeDB/OPS specific extension here if required.           
                logger.warn("OpenRDF does not know the RDF Format for " + address);
                logger.warn("Using the default format " + DEFAULT_PARSER);
                return DEFAULT_PARSER;
            }
            format = fileFormat.get();
        } else {
            List<RDFFormat> rdfFormats = new ArrayList<>();
            rdfFormats.add(RDFFormat.N3);
            rdfFormats.add(RDFFormat.NQUADS);
            rdfFormats.add(RDFFormat.NTRIPLES);
            rdfFormats.add(RDFFormat.JSONLD);
            rdfFormats.add(RDFFormat.RDFA);
            rdfFormats.add(RDFFormat.RDFJSON);
            rdfFormats.add(RDFFormat.RDFXML);
            rdfFormats.add(RDFFormat.TRIG);
            rdfFormats.add(RDFFormat.TRIX);
            rdfFormats.add(RDFFormat.TURTLE);
            for (RDFFormat rdfFormat: rdfFormats){
                if (rdfFormat.getName().equalsIgnoreCase(rdfFormatName)){
                    format = rdfFormat;
                }
                if (format == null){
                    throw new BridgeDBException("No RdfFormat with name " + rdfFormatName + " known");
                }
            }
        }
        Optional<RDFParserFactory> factory = reg.get(format);
        return factory.get().getParser();
    }

 }
