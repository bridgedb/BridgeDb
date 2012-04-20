/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.rdf.RepositoryFactory;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;
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
    Repository myRepository;
   //final Resource[] NO_RESOURCES = new Resource[0];
    final Resource linkSetGraph;
    
    public LinksetHandler(URLLinkListener listener, String graph) throws RepositoryException, IDMapperException {
        this.listener = listener;
        listener.openInput();
        myRepository = RepositoryFactory.getRepository();
        linkSetGraph = new URIImpl(graph);
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
        try {
            myRepository.getConnection().add(subject, predicate, object, linkSetGraph);
        } catch (RepositoryException ex) {
            throw new RDFHandlerException("Unable to save statement to memory", ex);
        }
    }
   
    private String getMatchingUriSpace(Resource subject, String uri) throws RDFHandlerException{
        if (subject == null) return null;
        Value URIspace = RepositoryFactory.getStringletonObject(subject, VoidConstants.URI_SPACEURI, linkSetGraph);
        System.out.println("URIspace:" + URIspace.stringValue());
        System.out.println("uri:" + uri);
        if (URIspace == null){
            return null; 
        }
        if (uri.startsWith(URIspace.stringValue())){
            return URIspace.toString();
        } else {
            System.out.println("no");
        }
        return null;
    }
    
    private String getSubjectUriSpace(Statement firstMap) throws RDFHandlerException, RepositoryException{
        System.out.println("linksetId="+linksetId);
        Resource subject = (Resource)RepositoryFactory.
                getStringletonObject(linksetId, VoidConstants.SUBJECTSTARGETURI, linkSetGraph);
        System.out.println("subject="+subject);
        String URIspace = getMatchingUriSpace(subject,firstMap.getSubject().stringValue());
        if (URIspace != null) return URIspace;
        List<Value> possibles = RepositoryFactory.getObjects(subject, VoidConstants.TARGETURI, linkSetGraph);
        for (Value possible:possibles){
            URIspace = getMatchingUriSpace((Resource)possible, firstMap.getSubject().stringValue());
            if (URIspace != null) {
                RepositoryFactory.addStatement(linksetId, VoidConstants.SUBJECTSTARGETURI, possible, linkSetGraph);
                return URIspace;
            }
        }
        throw new RDFHandlerException ("Unable to find a valid URISpace for the subject");
    }
     
    private String getObjectUriSpace(Statement firstMap) throws RDFHandlerException, RepositoryException{
        Resource subject = (Resource)RepositoryFactory.
                getStringletonObject(linksetId, VoidConstants.OBJECTSTARGETURI, linkSetGraph);
        String URIspace = getMatchingUriSpace(subject,firstMap.getObject().stringValue());
        if (URIspace != null) return URIspace;
        List<Value> possibles = RepositoryFactory.getObjects(subject, VoidConstants.TARGETURI, linkSetGraph);
        for (Value possible:possibles){
            URIspace = getMatchingUriSpace((Resource)possible, firstMap.getObject().stringValue());
            if (URIspace != null) {
                RepositoryFactory.addStatement(linksetId, VoidConstants.OBJECTSTARGETURI, possible, linkSetGraph);
                return URIspace;
            }
        }
        throw new RDFHandlerException ("Unable to find a valid URISpace for the subject");
    }

    private void finishProcessingHeader(Statement firstMap) throws RDFHandlerException {
        processingHeader = false;
        try{
            String subjectUriSpace = getSubjectUriSpace(firstMap);
            String objectUriSpace =  getObjectUriSpace(firstMap);
            try {
                listener.registerProvenanceLink(linksetId.stringValue(), DataSource.getByNameSpace(subjectUriSpace), 
                        linkPredicate.stringValue(), DataSource.getByNameSpace(objectUriSpace));
            } catch (IDMapperException ex) {
                throw new RDFHandlerException ("Unable to register header info ", ex);
            }
        } catch (RepositoryException ex){
            throw new RDFHandlerException(ex);          
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
