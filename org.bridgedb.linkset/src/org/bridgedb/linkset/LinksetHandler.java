/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.provenance.Provenance;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.openrdf.sail.memory.MemoryStore;
/**
 *
 * @author Christian
 */
public class LinksetHandler extends RDFHandlerBase{
    boolean processingHeader = true;
    private List<String> datasets = new ArrayList<String>(2);
    URI linkPredicate;
    //URI subjectTarget;
    //URI objectTarget;
    //URI subset;
    //Literal dateCreated;
    //Value creator;
    //int linksetId;
    URLLinkListener listener;
    Provenance provenance;
    Repository myRepository;
    final Resource[] NO_RESOURCES = new Resource[0];
    
    public LinksetHandler(URLLinkListener listener) throws IDMapperException{
        this.listener = listener;
        listener.openInput();
        myRepository = new SailRepository(new MemoryStore());
        try {
            myRepository.initialize();
        } catch (RepositoryException ex) {
            throw new IDMapperException("Unable to set up Repository", ex);
        }
    }
    
    @Override
    public void handleStatement(Statement st) throws RDFHandlerException {
        //System.out.println(st);
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
            finishProcessingHeader();
            checkStatement(st);
            insertLink(st);
            return;
        }
        if (predicateStr.equals(VoidConstants.LINK_PREDICATE)) {
            if (linkPredicate != null) {
                throw new RDFHandlerException("Linkset can only be declared to have one link predicate.");
            }
            linkPredicate = (URI) object;
        }
        try {
            myRepository.getConnection().add(subject, predicate, object, NO_RESOURCES);
        } catch (RepositoryException ex) {
            throw new RDFHandlerException("Unable to save statement to memory", ex);
        }
    }
    
    private Resource getSingletonSubject(String predicateString) throws RDFHandlerException{
        URI predicateURI = new URIImpl(predicateString);
        List<Statement> list;
        try {
            RepositoryResult<Statement> rr = 
                    myRepository.getConnection().getStatements(null, predicateURI, null, false, NO_RESOURCES);
            list = rr.asList();
        } catch (RepositoryException ex) {
            throw new RDFHandlerException ("Unable to extract statements ", ex);
        }
        if (list.size() == 1){
            return list.get(0).getSubject();
        }
        if (list.isEmpty()){
            throw new RDFHandlerException("No statement with predicate " + predicateString + " found");
        } else {
            throw new RDFHandlerException("Found more than one statement with predicate " + predicateString + " found");            
        }    
    }
    
    private Value getStringletonObject(Resource subject, String predicateString) throws RDFHandlerException{
        URI predicateURI = new URIImpl(predicateString);
        return getStringletonObject(subject, predicateURI);
    }
    
    private Value getStringletonObject(Resource subject, URI predicateURI) throws RDFHandlerException{
        List<Statement> list;
        try {
            RepositoryResult<Statement> rr = 
                    myRepository.getConnection().getStatements(subject, predicateURI, null, false, NO_RESOURCES);
            list = rr.asList();
        } catch (RepositoryException ex) {
            throw new RDFHandlerException ("Unable to extract statements ", ex);
        }
        if (list.size() == 1){
            return list.get(0).getObject();
        }
        if (list.isEmpty()){
            throw new RDFHandlerException("No statement with subject " + subject + " and Predicate " + predicateURI + " found");
        } else {
            throw new RDFHandlerException("Found more than one statement with predicate " + predicateURI + " found");            
        }    
        
    }
    
    private String getSubjectUriSpace() throws RDFHandlerException{
        Resource subject = (Resource)getStringletonObject(null, VoidConstants.SUBJECTSTARGET);
        Value URISpace = getStringletonObject(subject, VoidConstants.URI_SPACE);
        return URISpace.stringValue();
    }
     
    private String getObjectUriSpace() throws RDFHandlerException{
        Resource subject = (Resource)getStringletonObject(null, VoidConstants.OBJECTSTARGET);
        Value URISpace = getStringletonObject(subject, VoidConstants.URI_SPACE);
        return URISpace.stringValue();
    }

    private void finishProcessingHeader() throws RDFHandlerException {
        processingHeader = false;
        String subjectUriSpace = getSubjectUriSpace();
        String objectUriSpace =  getObjectUriSpace();
        String creator = getStringletonObject(null, DctermsConstants.CREATOR).stringValue();
        long created = new GregorianCalendar().getTimeInMillis();
        try {
            Literal dateCreated = (Literal) getStringletonObject(null, DctermsConstants.CREATED);
            created = dateCreated.calendarValue().toGregorianCalendar().getTimeInMillis();
        } catch (Exception e){
            //OK no usable date
            System.err.println(e);
        }
        try {
            //TODO this will need more work
            provenance = listener.createProvenance(
                    DataSource.getByNameSpace(subjectUriSpace),
                    linkPredicate.stringValue(), 
                    DataSource.getByNameSpace(objectUriSpace),
                    creator.toString(), created);
        } catch (IDMapperException ex) {
            throw new RDFHandlerException ("Error starting listener ", ex);
        }
    }

    /**
     * Inserts the given link statement into the data store.
     * @param st link triple
     */
    private void insertLink(Statement st) throws RDFHandlerException {
        try {
            String predicate = st.getPredicate().stringValue();
            if (!predicate.equals(provenance.getPredicate())){
                throw new RDFHandlerException (st + " has an unexpected predicate. Expected: " 
                        + provenance.getPredicate());
            }
            listener.insertLink(st.getSubject().stringValue(), st.getObject().stringValue(), provenance);
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
        if (!st.getSubject().stringValue().startsWith(provenance.getSource().getNameSpace())){
            throw new RDFHandlerException("SourceURL " + st.getSubject().stringValue()
                    + " does not match the expected pattern " + provenance.getSource().getNameSpace());
        }
        try {
            if (!provenance.getPredicate().equals(st.getPredicate().stringValue())){
                throw new RDFHandlerException("predicateURL " + st.getPredicate().stringValue()
                        + " does not match the expected pattern " + provenance.getPredicate());            
            }
        } catch (IDMapperException ex) {
            throw new  RDFHandlerException("Unable to get predicate. ", ex);
        }
        if (!st.getObject().stringValue().startsWith(provenance.getTarget().getNameSpace())){
            throw new RDFHandlerException("ObjectURL " + st.getObject().stringValue()
                    + " does not match the expected pattern " + provenance.getSource().getNameSpace());
        }
    }
}
