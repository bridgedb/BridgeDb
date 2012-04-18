/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.FileReader;
import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.turtle.TurtleParser;

/**
 *
 * @author Christian
 */
public class Tester {
    
    public static void parse (URLLinkListener listener, String fileName, String baseURI) 
            throws IOException, RDFParseException, RDFHandlerException, IDMapperException, RepositoryException{
         RDFParser parser = new TurtleParser();
         //N3ParserFactory factory = new N3ParserFactory();
         //RDFParser parser = factory.getParser();
         RDFHandler handler = new LinksetHandler(listener );
         parser.setRDFHandler(handler);
         parser.setParseErrorListener(new LinksetParserErrorListener());
         parser.setVerifyData(false);
         FileReader reader = new FileReader(fileName);
         parser.parse (reader, baseURI);
    }

    public static void main(String[] args) throws IOException, RDFParseException, RDFHandlerException, IDMapperException, RepositoryException {
        URLMapperLinkset listener = new URLMapperLinkset();
        parse (listener, "resources/cs2chemblExample.ttl", "http://foo/bar");
       // parse (listener, "resources/cw-cs_linkset.ttl", "http://foo/bar");

        //parse (listener, "C:/Temp/cw-cs.ttl", "http://foo/bar");
        listener.printStats();
    }
}
