/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.IDMapperException;
import org.bridgedb.Reporter;
import org.bridgedb.linkset.IDMapperLinksetException;
import org.bridgedb.linkset.VoidConstants;
import org.bridgedb.ops.LinkSetStore;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class HoldingRDFStore implements RdfLoader{

    private RdfStoreType type;
    private ArrayList<Statement> statements = new ArrayList<Statement>();
    String subjectURISpace;
    String targetURISpace;
    URI linksetContext;
    
    private static final URI HIGHEST_LINKSET_ID_PREDICATE = new URIImpl("http://www.bridgedb.org/highested_linkset_id");
    private static final Resource ANY_RESOURCE = null;

    public HoldingRDFStore(RdfStoreType type) throws IDMapperLinksetException{
        this.type = type;
        setContext();
    }
        
    private synchronized void setContext() throws IDMapperLinksetException{     
        try {
            RepositoryConnection connection = RdfWrapper.setupConnection(type);
            Resource subject = new URIImpl(RdfWrapper.getBaseURI() + "/MetaData");
            RepositoryResult<Statement> rr = RdfWrapper.getStatements(connection, 
                    subject, HIGHEST_LINKSET_ID_PREDICATE, null, ANY_RESOURCE);
            Value linksetId = extractLinksetId(connection, rr, subject);
            rr = RdfWrapper.getStatements(connection, 
                    subject, HIGHEST_LINKSET_ID_PREDICATE, null, ANY_RESOURCE);
            RdfWrapper.remove(connection, rr);
            RdfWrapper.add(connection, subject, HIGHEST_LINKSET_ID_PREDICATE, linksetId, ANY_RESOURCE);
            rr = RdfWrapper.getStatements(connection, 
                    subject, HIGHEST_LINKSET_ID_PREDICATE, null, ANY_RESOURCE);
            linksetContext = RdfWrapper.getLinksetURL(linksetId);       
            RdfWrapper.shutdown(connection);
        } catch (RDFHandlerException ex) {
            throw new IDMapperLinksetException("Error setting the context", ex);
        }
     }

    public void clear() throws IDMapperLinksetException {
        RdfWrapper.clear(type);
    }

    public String getDefaultBaseURI() {
        return linksetContext.stringValue() + "/";        
    }

    @Override
    public void addStatement(Statement st) throws RDFHandlerException {
        if (statements == null){
            throw new RDFHandlerException ("Illegal call to addStatement after validateAndSaveVoid() called.");
        }
        statements.add(st);
    }
    
    @Override
    public synchronized void validateAndSaveVoid(Statement firstMap) throws RDFHandlerException{
        Reporter.report("Validation started");
        validate(firstMap);
        RepositoryConnection connection = RdfWrapper.setupConnection(type);
        for (Statement st:statements){
            RdfWrapper.add(connection, st.getSubject(), st.getPredicate(), st.getObject(), linksetContext);
        }
        statements = null;
        RdfWrapper.shutdown(connection);
     }

    private void validate(Statement firstMap) throws RDFHandlerException {
        Value subjectDataSet = findSingletonObject(VoidConstants.SUBJECTSTARGETURI);
        if (subjectDataSet == null){
            subjectURISpace = findURISpace(firstMap.getSubject());
        } else {
            subjectURISpace = findURISpace(subjectDataSet, firstMap.getSubject());
        }
        Value objectDataSet = findSingletonObject(VoidConstants.OBJECTSTARGETURI);
        if (objectDataSet == null){
            targetURISpace = findURISpace(firstMap.getObject());
        } else {
            targetURISpace = findURISpace(objectDataSet, firstMap.getObject());
        }
    }
    
    private String findURISpace(Value subjectDataSet, Value  uri) throws RDFHandlerException {
        Value uriValue = findTheSingletonObject(subjectDataSet, VoidConstants.URI_SPACEURI);
        String uriSpace = uriValue.stringValue();
        if (uri.stringValue().startsWith(uriSpace)){
            return uriSpace;
        }
        throw new RDFHandlerException("Declared URISpace " + uriSpace + " and uri in first link " + uri + " do not match");
    }

    private Value extractLinksetId(RepositoryConnection connection, RepositoryResult<Statement> rr, Resource subject) 
            throws RDFHandlerException{
        List<Statement> list = RdfWrapper.asList(connection, rr);
        int linksetId;
        if (list.size() == 1){
           Value lastLinksetId = list.get(0).getObject();
           linksetId = Integer.parseInt(lastLinksetId.stringValue()) + 1;
        } else if (list.isEmpty()){
             linksetId = 1;
        } else {
            RdfWrapper.shutdownAfterError (connection);
            throw new RDFHandlerException("Found more than one statement with subject " + subject + 
                " and predicate " + HIGHEST_LINKSET_ID_PREDICATE);            
        }           
        return new LiteralImpl("" + linksetId);
    }
    
    private String findURISpace(Value uri) throws RDFHandlerException {
        for (Statement st:statements){
            if (st.getPredicate().equals(VoidConstants.URI_SPACEURI)){
                Value uriValue = st.getObject();
                String uriSpace = uriValue.stringValue();
                if (uri.stringValue().startsWith(uriSpace)){
                    Value dataset =  st.getSubject();
                    findTheSingletonSubject (VoidConstants.TARGETURI, dataset);
                    return uriSpace;
                }
            }
        }
        throw new RDFHandlerException ("Unable to find a " + VoidConstants.TARGETURI + " with " 
                + VoidConstants.URI_SPACEURI + " that covers " + uri);
    }

    private Value findSingletonSubject (URI predicate) throws RDFHandlerException{
        Value subject = null;
        for (Statement st:statements){
            if (st.getPredicate().equals(predicate)){
                if (subject != null){
                    throw new RDFHandlerException ("Found more than one statement with predicate " + predicate);
                }
                subject = st.getSubject();
            }
        }
        return subject;
    }
    
    private Value findSingletonObject (URI predicate) throws RDFHandlerException{
        Value object = null;
        for (Statement st:statements){
            if (st.getPredicate().equals(predicate)){
                if (object != null){
                    throw new RDFHandlerException ("Found more than one statement with predicate " + predicate);
                }
                object = st.getObject();
            }
        }
        return object;
    }
    
    private Value findTheSingletonObject (Value subject, URI predicate) throws RDFHandlerException{
        Value object = null;
        for (Statement st:statements){
            if (st.getSubject().equals(subject) && st.getPredicate().equals(predicate)){
                if (object != null){
                    throw new RDFHandlerException ("Found more than one statement with subject " + subject + 
                            " and predicate " + predicate);
                }
                object = st.getObject();
            }
        }
        if (object == null){
            throw new RDFHandlerException ("Found no statement with subject " + subject +  " and predicate " + predicate);
        }
        return object;
    }

    private Value findTheSingletonSubject (URI predicate, Value object) throws RDFHandlerException{
        Value subject = null;
        for (Statement st:statements){
            if (st.getObject().equals(object) && st.getPredicate().equals(predicate)){
                if (subject != null){
                    throw new RDFHandlerException ("Found more than one statement with predicate " + predicate + 
                            " and Object " + object);
                }
                subject = st.getSubject();
            }
        }
        if (subject == null){
            throw new RDFHandlerException ("Found no statement with predicate " + predicate + 
                            " and Object " + object);
        }
        return subject;
    }

   /* private String createNewGraph(RdfStoreType type) throws IDMapperLinksetException {
        Repository repository = getRepository(type);
        RepositoryConnection connection = getConnection(repository);
        Resource subject = new URIImpl(getBaseURI() + "/MetaData");
        List<Statement> list;
        int linksetId;
        try {
            RepositoryResult<Statement> rr = connection.getStatements(subject, HIGHEST_LINKSET_ID_PREDICATE, null, false, ANY_RESOURCE);
            list = rr.asList();
            Value lastLinksetId;
            if (list.size() == 1){
                lastLinksetId = list.get(0).getObject();
                linksetId = Integer.parseInt(lastLinksetId.stringValue()) + 1;
            } else if (list.isEmpty()){
                linksetId = 1;
            } else {
                shutdownAfterError(repository, connection);
                throw new IDMapperLinksetException("Found more than one statement with subject " + subject + 
                    " and predicate " + HIGHEST_LINKSET_ID_PREDICATE);            
            }           
            connection.remove(rr, ANY_RESOURCE);
            lastLinksetId = new LiteralImpl("" + linksetId);
            connection.add(subject, HIGHEST_LINKSET_ID_PREDICATE, lastLinksetId, ANY_RESOURCE);
        } catch (RepositoryException ex) {
            shutdownAfterError(repository, connection);
            throw new IDMapperLinksetException ("Error clearing the Reposotory. ", ex);
        } 
        shutdown(repository, connection);
        return getBaseURI() + "/linkset/" + linksetId;        
   }
*/

    @Override
    public String getSubjectUriSpace() throws RDFHandlerException {
        if (subjectURISpace != null){
            return this.subjectURISpace;
        }
        throw new RDFHandlerException("run validateAndSaveVoid before calling getSubjectUriSpace()");
     }

    @Override
    public String getTargetUriSpace() throws RDFHandlerException {
        if (targetURISpace != null){
            return this.targetURISpace;
        }
        throw new RDFHandlerException("run validateAndSaveVoid before calling getTargetUriSpace()");
    }

    @Override
    public String getLinksetid() throws RDFHandlerException {
        if (linksetContext != null){
            return linksetContext.stringValue();
        }
        throw new RDFHandlerException("run validateAndSaveVoid before calling getLinksetid()");
    }

    @Override
    public String getInverseLinksetid() throws RDFHandlerException {
        if (linksetContext != null){
            return linksetContext.stringValue() + "/Inverse";
        }
        throw new RDFHandlerException("run validateAndSaveVoid before calling getInverseLinksetid()");
    }
       
}
