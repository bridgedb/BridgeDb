/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.rdf.LinksetStore;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.openrdf.rio.turtle.TurtleParser;

/**
 *
 * @author Christian
 */
public class LinksetHandler extends RDFHandlerBase{
    boolean processingHeader = true;
    private List<String> datasets = new ArrayList<String>(2);
    URI linkPredicate;
    URI linksetId;
    URLLinkListener listener;
    String provenanceId;
    //Repository myRepository;
    LinksetStore linksetStore;
   //final Resource[] NO_RESOURCES = new Resource[0];
    final Resource linkSetGraph;
    
    public static void testParse (URLLinkListener listener, String fileName) 
            throws IDMapperLinksetException  {
        LinksetStore linksetStore = LinksetStore.testFactory();
        LinksetHandler handler = new LinksetHandler(listener, linksetStore);
        parse (handler, fileName);
    }

    public static void parse (URLLinkListener listener, String fileName) 
            throws IDMapperLinksetException  {
        LinksetStore linksetStore = LinksetStore.factory();
        LinksetHandler handler = new LinksetHandler(listener, linksetStore);
        parse (handler, fileName);
    }

    public static void testClearAndParse (URLLinkListener listener, String fileName) 
            throws IDMapperLinksetException  {
        LinksetStore linksetStore = LinksetStore.testFactory();
        linksetStore.clear();
        LinksetHandler handler = new LinksetHandler(listener, linksetStore);
        parse (handler, fileName);
    }

    public static void clearAndParse (URLLinkListener listener, String fileName) 
            throws IDMapperLinksetException  {
        LinksetStore linksetStore = LinksetStore.factory();
        linksetStore.clear();
        LinksetHandler handler = new LinksetHandler(listener, linksetStore);
        parse (handler, fileName);
    }

    private static void parse (LinksetHandler handler, String fileName) 
            throws IDMapperLinksetException  {
        FileReader reader = null;
        try {
            RDFParser parser = new TurtleParser();
            parser.setRDFHandler(handler);
            parser.setParseErrorListener(new LinksetParserErrorListener());
            parser.setVerifyData(true);
            reader = new FileReader(fileName);
            parser.parse (reader, handler.getDefaultBaseURI());
        } catch (IOException ex) {
            throw new IDMapperLinksetException("Error reading file " + fileName, ex);
        } catch (OpenRDFException ex) {
            throw new IDMapperLinksetException("Error parsing file " + fileName, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(LinksetHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private LinksetHandler(URLLinkListener listener, LinksetStore linksetStore) throws IDMapperLinksetException  {
        try {
            this.listener = listener;
            listener.openInput();
            this.linksetStore = linksetStore;                
            linkSetGraph = linksetStore.createNewGraph();
        } catch (Exception ex) {
            throw new IDMapperLinksetException ("Unable to create LinksetHandler ", ex);
        }
    }
    
    @Override
    public void handleStatement(Statement st) throws RDFHandlerException {
        if (processingHeader) {
            processHeaderStatement(st);
        } else {
            if (st.getPredicate().equals(linkPredicate)) {
                /* Only store those statements that correspond to the link predicate */
                insertLink(st);
            }
        }
    }
    
    public String getDefaultBaseURI(){
        return linkSetGraph.stringValue() + "/";
    }
    
    /**
     * Process an RDF statement that forms part of the VoID header for the 
     * linkset file.
     * 
     * Once the header processor detects that it is starting to process links
     * it sets a flag, inserts the VoID header information into the database,
     * and then goes into a link insert only mode.
     * 
     * @param st an RDF statement
     * @throws RDFHandlerException
     * @throws IRSException 
     */
    private void processHeaderStatement(Statement st) throws RDFHandlerException{
        Resource subject = st.getSubject();
        final URI predicate = st.getPredicate();
        final String predicateStr = predicate.stringValue();
        final Value object = st.getObject();
        if (linkPredicate != null && predicate.equals(linkPredicate)) {
            /* Assumes all metadata is declared before the links */
            finishProcessingHeader(st);
            checkStatement(st);
            insertLink(st);
            return;
        }
        if (predicateStr.equals(VoidConstants.LINK_PREDICATE)) {
            if (linkPredicate != null) {
                throw new RDFHandlerException("Linkset can only be declared to have one link predicate.");
            }
            linkPredicate = (URI) object;
            linksetId = (URI) subject;
        }
        linksetStore.addStatement(subject, predicate, object, linkSetGraph);
    }
   
    private void finishProcessingHeader(Statement firstMap) throws RDFHandlerException {
        processingHeader = false;
        String subjectUriSpace = linksetStore.getSubjectUriSpace(firstMap, linkSetGraph).stringValue();
        String objectUriSpace =  linksetStore.getObjectUriSpace(firstMap, linkSetGraph).stringValue();
        try {
            listener.registerProvenanceLink(linksetId.stringValue(), DataSource.getByNameSpace(subjectUriSpace), 
                    linkPredicate.stringValue(), DataSource.getByNameSpace(objectUriSpace));
        } catch (IDMapperException ex) {
            throw new RDFHandlerException ("Unable to register header info ", ex);
        }
    }

    /**
     * Inserts the given link statement into the data store.
     * @param st link triple
     */
    private void insertLink(Statement st) throws RDFHandlerException {
        try {
            String predicate = st.getPredicate().stringValue();
            if (!predicate.equals(linkPredicate.stringValue())){
                throw new RDFHandlerException (st + " has an unexpected predicate. Expected: " 
                        + linkPredicate);
            }
            listener.insertLink(st.getSubject().stringValue(), st.getObject().stringValue(), provenanceId);
        } catch (ClassCastException ex) {
            throw new RDFHandlerException ("Unepected statement " + st, ex);
        } catch (IDMapperException ex){
            throw new RDFHandlerException ("Error inserting link " + st, ex);            
        }
    }

    @Override
    public void startRDF() throws RDFHandlerException{
        super.startRDF();
    } 
    
    public void endRDF() throws RDFHandlerException{
        super.endRDF();
        try {
            listener.closeInput();
        } catch (IDMapperException ex) {
            throw new RDFHandlerException("Error endingRDF ", ex);
        }
    }

    private void checkStatement(Statement st) throws RDFHandlerException{
     if (!linkPredicate.equals(st.getPredicate())){
            throw new RDFHandlerException("predicateURL " + st.getPredicate()
                    + " does not match the expected pattern " + linkPredicate);            
        }
    }
}
