/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.provenance.ProvenanceFactory;
import org.bridgedb.provenance.SimpleProvenanceFactory;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.turtle.TurtleParser;

/**
 *
 * @author Christian
 */
public class LinksetParser {
     
    public static void parse (LinkListener listener, ProvenanceFactory provenanceFactory, String fileName, String baseURI) 
            throws IOException, RDFParseException, RDFHandlerException{
         RDFParser parser = new TurtleParser();
         RDFHandler handler = new LinksetHandler(listener, provenanceFactory );
         parser.setRDFHandler(handler);
         parser.setParseErrorListener(new LinksetParserErrorListener());
         parser.setVerifyData(true);
         FileReader reader = new FileReader(fileName);
         parser.parse (reader, baseURI);
    }
    
    public static void main( String[] args ) throws RDFHandlerException, IOException, RDFParseException  {
        IDMapperLinkset listener = new IDMapperLinkset();
        ProvenanceFactory provenanceFactory = new SimpleProvenanceFactory();
        parse (listener, provenanceFactory, "C:/Temp/cs-chembl_small.ttl", "http://foo/bar");
        //parse (listener, "C:/Temp/cw-cs.ttl", "http://foo/bar");
        listener.printStats();
    }
}
