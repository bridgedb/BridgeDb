/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridgedb.rdf;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.log4j.Logger;
import org.bridgedb.rdf.constants.DulConstants;
import org.bridgedb.rdf.constants.VoidConstants;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.Reporter;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.RDFWriterFactory;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;
import org.eclipse.rdf4j.rio.n3.N3Writer;
import org.eclipse.rdf4j.rio.ntriples.NTriplesWriter;
import org.eclipse.rdf4j.rio.rdfxml.RDFXMLWriter;
import org.eclipse.rdf4j.rio.rdfxml.util.RDFXMLPrettyWriter;
import org.eclipse.rdf4j.rio.trig.TriGWriter;
import org.eclipse.rdf4j.rio.trix.TriXWriter;
import org.eclipse.rdf4j.rio.turtle.TurtleWriter;

/**
 *
 * @author christian
 */
public class BridgeDbRdfTools {
    
    static final Logger logger = Logger.getLogger(BridgeDbRdfTools.class);

    public static final RDFFormat DEFAULT_FORMAT = RDFFormat.TRIX;
    
    public static String writeRDF(Set<Statement> statements, RDFFormat formatName) throws BridgeDBException{
        StringWriter writer = new StringWriter();
        if (formatName == null) formatName = DEFAULT_FORMAT;
        writeRDF(statements,  formatName, writer);
        return writer.toString();
    }
    
    public static void writeRDF(Set<Statement> statements,  RDFFormat format, Writer writer) throws BridgeDBException{        
    	RDFWriter rdfWriter = null;
        if (format != null) {
			rdfWriter = getWriterIfPossible(format, writer);
        }
        try {
            if (rdfWriter != null){
                rdfWriter.startRDF();
                rdfWriter.handleNamespace("ops", RdfBase.DEFAULT_BASE_URI);
                rdfWriter.handleNamespace("void", VoidConstants.voidns);
                rdfWriter.handleNamespace("dul", DulConstants.dulns);
                for(Statement statement:statements){
                    rdfWriter.handleStatement(statement);
                }
                rdfWriter.endRDF();
            } else {
                writer.flush();
                writer.write("No Writer available for format: " + format + "\n");
            }
       } catch (RDFHandlerException ex) {
            throw new BridgeDBException("Error writing RDF. ", ex);
        } catch (IOException ex) {
            throw new BridgeDBException("Error writing RDF. ", ex);
        }
    }
    
    public static Set<String> getAvaiableWriters(){
        N3Writer n = null;
        NTriplesWriter nt = null;
        RDFXMLPrettyWriter x2 = null;
        RDFXMLWriter x = null;
        TriGWriter tr = null;
        TriXWriter tw = null;
        TurtleWriter t = null;
        HashSet<String> results = new HashSet<String>();
        StringWriter writer = new StringWriter();
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
            RDFWriter rdfWriter = getWriterIfPossible(rdfFormat, writer); 
            if (rdfWriter != null){
                results.add(rdfFormat.getName());
            }
        }
        return results;
    }

    public static Set<RDFFormat> getAvaiableFormats(){
        HashSet<RDFFormat> results = new HashSet<RDFFormat>();
        StringWriter writer = new StringWriter();
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
            RDFWriter rdfWriter = getWriterIfPossible(rdfFormat, writer); 
            if (rdfWriter != null){
                results.add(rdfFormat);
            }
        }
        return results;
    }
    
    /**
     * This method is required as at last check the BinaryRDFWriterFactory was not fully implemeneted.
     * @param format
     * @param writer
     * @return 
     */
    private static RDFWriter getWriterIfPossible(RDFFormat format, Writer writer){
        RDFWriterRegistry register =  RDFWriterRegistry.getInstance();
        Optional<RDFWriterFactory> factory = register.get(format);
        if (factory.get() == null){
            return null;
        }
        try {
            return factory.get().getWriter(writer);
        } catch (Exception ex){
            logger.error(ex);
            return null;
        }
    }
    
    public static void main(String[] args) {
        Reporter.println(getAvaiableWriters().toString());
    }

}
