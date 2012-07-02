package org.bridgedb.linkset;

import java.io.File;
import org.bridgedb.linkset.constants.VoidConstants;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Reporter;
import org.bridgedb.rdf.RdfLoader;
import org.bridgedb.rdf.RdfStoreType;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
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
    String linksetId;
    String inverseLinksetId;       
    boolean loaded = false;
    
//    URI linksetId;
//    URI inverseLinksetId;
    URLLinkListener listener;
    RdfLoader rdfLoader;
    
    LinksetHandler(URLLinkListener listener, RdfLoader rdfLoader) throws IDMapperLinksetException  {
        try {
            this.listener = listener;
            this.rdfLoader = rdfLoader;                
         } catch (Exception ex) {
            throw new IDMapperLinksetException ("Unable to create LinksetHandler ", ex);
        }
    }
    
    void parse (File file) throws IDMapperException  {
        if (!file.isFile()){
            throw new IDMapperException (file.getAbsolutePath() + " is not a file");
        }
        Reporter.report("Parsing " + file.getAbsolutePath());
        listener.openInput();
        FileReader reader = null;
        try {
            RDFParser parser = new TurtleParser();
            parser.setRDFHandler(this);
            parser.setParseErrorListener(new LinksetParserErrorListener());
            parser.setVerifyData(true);
            reader = new FileReader(file);
            parser.parse (reader, getDefaultBaseURI());
        } catch (IOException ex) {
            throw new IDMapperLinksetException("Error reading file " + file.getAbsolutePath() + " " + ex.getMessage(), ex);
        } catch (OpenRDFException ex) {
            throw new IDMapperLinksetException("Error parsing file " + file.getAbsolutePath()+ " " + ex.getMessage(), ex);
        } finally {
            try {
                if (reader != null){
                    reader.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(LinksetHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
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
        return rdfLoader.getDefaultBaseURI();
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
        final Value object = st.getObject();
        if (linkPredicate != null && predicate.equals(linkPredicate)) {
            /* Assumes all metadata is declared before the links */
            finishProcessingHeader(st);
            insertLink(st);
            return;
        }
        if (predicate.equals(VoidConstants.LINK_PREDICATE)) {
            if (linkPredicate != null) {
                throw new RDFHandlerException("Linkset can only be declared to have one link predicate.");
            }
            linkPredicate = (URI) object;
        }
        rdfLoader.addStatement(st);
    }
   
    private void finishProcessingHeader(Statement firstMap) throws RDFHandlerException {
        processingHeader = false;
        rdfLoader.validateAndSaveVoid(firstMap);
        String subjectUriSpace = rdfLoader.getSubjectUriSpace();
        String targetUriSpace = rdfLoader.getTargetUriSpace();
        DataSource subjectDataSource = DataSource.getByURISpace(subjectUriSpace);
        DataSource targetDataSource = DataSource.getByURISpace(targetUriSpace);
        linksetId = rdfLoader.getLinksetid();
        inverseLinksetId = rdfLoader.getInverseLinksetid();       
        try {
            listener.registerLinkSet(linksetId, subjectDataSource, linkPredicate.stringValue(), targetDataSource, 
                    rdfLoader.isTransative());
            listener.registerLinkSet(inverseLinksetId, targetDataSource, linkPredicate.stringValue(), subjectDataSource, 
                    rdfLoader.isTransative());
        } catch (IDMapperException ex) {
            throw new RDFHandlerException ("Unable to register header info ", ex);
        }
        loaded = true;
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
            listener.insertLink(st.getSubject().stringValue(), st.getObject().stringValue(), 
                    linksetId, inverseLinksetId);
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
        if (!loaded){
            throw new RDFHandlerException("Linkset not saved as end of void headder not found");
        }
    }

    private void checkStatementX(Statement st) throws RDFHandlerException{
     if (!linkPredicate.equals(st.getPredicate())){
            throw new RDFHandlerException("predicateURL " + st.getPredicate()
                    + " does not match the expected pattern " + linkPredicate);            
        }
    }
    public static void parseX (URLLinkListener listener, RdfLoader rdfLoader, String fileName, RdfStoreType type) 
            throws IDMapperLinksetException  {
        LinksetHandler handler = new LinksetHandler(listener, rdfLoader);
        parse (handler, fileName, type);
    }

    private static void parse (LinksetHandler handler, String fileName, RdfStoreType type) 
            throws IDMapperLinksetException  {
        Reporter.report("Parsing " + fileName);
        FileReader reader = null;
        try {
            RDFParser parser = new TurtleParser();
            parser.setRDFHandler(handler);
            parser.setParseErrorListener(new LinksetParserErrorListener());
            parser.setVerifyData(true);
            reader = new FileReader(fileName);
            parser.parse (reader, handler.getDefaultBaseURI());
        } catch (IOException ex) {
            throw new IDMapperLinksetException("Error reading file " + fileName + " " + ex.getMessage(), ex);
        } catch (OpenRDFException ex) {
            throw new IDMapperLinksetException("Error parsing file " + fileName+ " " + ex.getMessage(), ex);
        } finally {
            try {
                if (reader != null){
                    reader.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(LinksetHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
