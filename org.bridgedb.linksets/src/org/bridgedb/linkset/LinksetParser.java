/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.FileReader;
import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.linkset.URLLinkListener;
import org.bridgedb.linkset.URLMapperLinkset;
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
public class LinksetParser {
     
    public static void parse (URLLinkListener listener, String fileName, String baseURI) 
            throws IOException, RDFParseException, RDFHandlerException, IDMapperException, RepositoryException{
         RDFParser parser = new TurtleParser();
         RDFHandler handler = new LinksetHandler(listener);
         parser.setRDFHandler(handler);
         parser.setParseErrorListener(new LinksetParserErrorListener());
         parser.setVerifyData(true);
         FileReader reader = new FileReader(fileName);
         parser.parse (reader, baseURI);
    }
    
    public static void main( String[] args ) throws RDFHandlerException, IOException, RDFParseException, IDMapperException, RepositoryException  {
        URLMapperLinkset listener = new URLMapperLinkset();
        parse (listener, "C:/Temp/cs-chembl_small.ttl", "http://foo/bar");
        //parse (listener, "C:/Temp/cw-cs.ttl", "http://foo/bar");
        listener.printStats();
    }
}
