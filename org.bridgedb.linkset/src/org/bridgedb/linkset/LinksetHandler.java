/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.util.ArrayList;
import java.util.List;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
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
    URI subjectTarget;
    URI objectTarget;
    URI subset;
    Literal dateCreated;
    Value creator;
    int linksetId;
    LinkListener listener;

    public LinksetHandler(LinkListener listener){
        this.listener = listener;
    }
    
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
    private void processHeaderStatement(Statement st) throws RDFHandlerException {
        final URI predicate = st.getPredicate();
        final String predicateStr = predicate.stringValue();
        final Value object = st.getObject();
        if (predicateStr.equals(RdfConstants.TYPE)
                && object.stringValue().equals(VoidConstants.DATASET)) {
            if (datasets.size() == 2) {
                throw new RDFHandlerException("Two datasets have already been declared.");
            }
            datasets.add(st.getSubject().stringValue());
        } else if (predicateStr.equals(VoidConstants.SUBJECTSTARGET)) {
            subjectTarget = (URI) object;
        } else if (predicateStr.equals(VoidConstants.OBJECTSTARGET)) {
            objectTarget = (URI) object;
        } else if (predicateStr.equals(VoidConstants.TARGET)) {
            if (subjectTarget == null) {
                subjectTarget = (URI) object;
            } else if (objectTarget == null) {
                objectTarget = (URI) object;
            } else {
                throw new RDFHandlerException("More than two targets have been declared.");
            }
        } else if (predicateStr.equals(VoidConstants.SUBSET)) {
            if (subset != null) {
                throw new RDFHandlerException("Linkset can only be declared to be the subset of at most one dataset.");
            }
            subset = (URI) object;
        } else if (predicateStr.equals(VoidConstants.LINK_PREDICATE)) {
            if (linkPredicate != null) {
                throw new RDFHandlerException("Linkset can only be declared to have one link predicate.");
            }
            linkPredicate = (URI) object;
        } else if (predicate.equals(DctermsConstants.CREATED)) {
            if (dateCreated != null) {
                throw new RDFHandlerException("Linkset can only have one creation date.");
            }
            dateCreated = (Literal) object;
        } else if (predicate.equals(DctermsConstants.CREATOR)) {
            if (creator != null) {
                throw new RDFHandlerException("Linkset can only have one creator.");
            }
            creator = object;
        } else if (linkPredicate != null && predicate.equals(linkPredicate)) {
            /* Assumes all metadata is declared before the links */
            finishProcessingHeader();
            insertLink(st);
        }
        // Ignores any predicate that we do not know how to process
    }
    
    private void finishProcessingHeader() {
        processingHeader = false;
    }

    /**
     * Inserts the given link statement into the data store.
     * @param st link triple
     */
    private void insertLink(Statement st) throws RDFHandlerException {
        try {
            URI subjectURI = (URI)st.getSubject();
            DataSource sourceDataSource = DataSource.getByNameSpace(subjectURI.getNamespace());
            Xref sourceXref = new Xref(subjectURI.getLocalName(), sourceDataSource);
            String predicate = st.getPredicate().stringValue();
            URI objectURI = (URI)st.getObject();
            DataSource targetDataSource = DataSource.getByNameSpace(objectURI.getNamespace());
            Xref targetXref = new Xref(objectURI.getLocalName(), sourceDataSource);
            listener.insertLink(sourceXref, targetXref);
        } catch (ClassCastException ex) {
            throw new RDFHandlerException ("Unepected statement " + st, ex);
        } catch (IDMapperException ex){
            throw new RDFHandlerException ("Error inserting link " + st, ex);            
        }
    }

    @Override
    public void startRDF() throws RDFHandlerException{
        System.out.println ("start");
        super.startRDF();
    } 
}
