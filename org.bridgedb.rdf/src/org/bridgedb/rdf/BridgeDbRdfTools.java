/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridgedb.rdf;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.bridgedb.rdf.constants.DulConstants;
import org.bridgedb.rdf.constants.VoidConstants;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.Reporter;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterFactory;
import org.openrdf.rio.RDFWriterRegistry;
import org.openrdf.rio.n3.N3Writer;
import org.openrdf.rio.ntriples.NTriplesWriter;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriter;
import org.openrdf.rio.trig.TriGWriter;
import org.openrdf.rio.trix.TriXWriter;
import org.openrdf.rio.turtle.TurtleWriter;

/**
 *
 * @author christian
 */
public class BridgeDbRdfTools {
    
    static final Logger logger = Logger.getLogger(BridgeDbRdfTools.class);

    public static final String DEFAULT_FORMAT = "TriX";
    
    public static String writeRDF(Set<Statement> statements, String formatName) throws BridgeDBException{
        StringWriter writer = new StringWriter();
        if (formatName == null){
            formatName = DEFAULT_FORMAT;
        }
        RDFFormat rdfFormat = RDFFormat.valueOf(formatName);
        writeRDF(statements,  rdfFormat, writer);
        return writer.toString();
    }
    
    public static void writeRDF(Set<Statement> statements,  RDFFormat format, Writer writer) throws BridgeDBException{        
        RDFWriter rdfWriter = getWriterIfPossible(format, writer); 
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
                writer.write("No Writer available for ");
                writer.write(format.toString());
                writer.write("\n");
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
        for (RDFFormat rdfFormat:RDFFormat.values()){
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
        for (RDFFormat rdfFormat:RDFFormat.values()){
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
        RDFWriterFactory factory = register.get(format);
        if (factory == null){
            return null;
        }
        try {
            return factory.getWriter(writer);
        } catch (Exception ex){
            logger.error(ex);
            return null;
        }
    }
    
    public static void main(String[] args) {
        Reporter.println(getAvaiableWriters().toString());
    }

}
