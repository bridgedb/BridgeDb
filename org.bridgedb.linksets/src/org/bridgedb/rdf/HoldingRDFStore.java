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
import org.bridgedb.linkset.constants.DctermsConstants;
import org.bridgedb.linkset.constants.PavConstants;
import org.bridgedb.linkset.constants.RdfConstants;
import org.bridgedb.linkset.constants.VoidConstants;
import org.bridgedb.ops.LinkSetStore;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
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
    Resource linksetResource;
    
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
        for (Statement st:statements){
            System.out.println(st);
        }
        linksetResource = findTheSingletonSubject(RdfConstants.TYPE_URI, VoidConstants.LINKSET_URI);
        //Provide details of the licence under which the dataset is published using the dcterms:license property.
        findOneofManyObject(linksetResource, DctermsConstants.LICENSE);
        //The linkset authorship, i.e. the agent that generated the intellectual knowledge
        validateLinksetProvenance();
        subjectURISpace = validateDataSetAndExtractUriSpace(firstMap.getSubject(), VoidConstants.SUBJECTSTARGETURI);
        targetURISpace = validateDataSetAndExtractUriSpace(firstMap.getObject(), VoidConstants.OBJECTSTARGETURI);
    }
    
    private void validateLinksetProvenance() throws RDFHandlerException {
        Value by = findPossibleObject(linksetResource, PavConstants.AUTHORED_BY);
        if (by != null){
            findTheSingletonObject(linksetResource, PavConstants.AUTHORED_ON);
            return;
        }
        by = findPossibleObject(linksetResource, PavConstants.CREATED_BY);
        if (by != null){
            findTheSingletonObject(linksetResource, PavConstants.CREATED_ON);
            return;
        }
        by = findPossibleObject(linksetResource, DctermsConstants.CREATOR);
        if (by != null){
            findTheSingletonObject(linksetResource, DctermsConstants.CREATED);
            return;
        }
        throw new RDFHandlerException(linksetResource + " must have " + PavConstants.AUTHORED_BY + ", " + 
                PavConstants.CREATED_BY + " or " + DctermsConstants.CREATOR);
    }


    /**
     * Based on http://www.cs.man.ac.uk/~graya/ops/mappingspec/
     * 
     * @param fullURI
     * @param targetPredicate
     * @return
     * @throws RDFHandlerException 
     */
    private String validateDataSetAndExtractUriSpace(Value fullURI, URI targetPredicate) throws RDFHandlerException{
        Value dataSetId = findPossibleSingletonObject(targetPredicate);
        if (dataSetId == null){
            dataSetId = findAndRegisterDataSetIdBasedOnUriSpace(fullURI, targetPredicate);
        }
        //Declare that we have a dataset using void:dataset.
        System.out.println(targetPredicate + " -> " + dataSetId);
        checkStatementExists(dataSetId, RdfConstants.TYPE_URI, VoidConstants.DATASET_URI);
        //Provide details of the licence under which the dataset is published using the dcterms:license property.
        findOneofManyObject(dataSetId, DctermsConstants.LICENSE);
        //There must be a version or a Date
        checkHasVersionOrDate(dataSetId);
        //The type of the resource being linked is declared with the dcterm:subject predicate.
        findOneofManyObject(dataSetId, DctermsConstants.SUBJECT);        
        //The URI namespace for the resources being linked is declared using the void:uriSpace property
        Value uriValue = findTheSingletonObject(dataSetId, VoidConstants.URI_SPACEURI);
        String uriSpace = uriValue.stringValue();
        if (fullURI.stringValue().startsWith(uriSpace)){
            return uriSpace;
        }
        throw new RDFHandlerException("Declared URISpace " + uriSpace + " and uri in first link " + fullURI + " do not match");
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
    
    private Value findAndRegisterDataSetIdBasedOnUriSpace(Value fullURI, URI targetPredicate) throws RDFHandlerException {
        for (Statement st:statements){
            if (st.getPredicate().equals(VoidConstants.URI_SPACEURI)){
                Value uriValue = st.getObject();
                String uriSpace = uriValue.stringValue();
                if (fullURI.stringValue().startsWith(uriSpace)){
                    System.out.println("Found ");
                    System.out.println(st);
                    Resource dataSetId =  st.getSubject();
                    checkStatementExists(linksetResource, VoidConstants.TARGETURI, dataSetId);
                    Statement newStatement = new StatementImpl(dataSetId, targetPredicate, uriValue);
                    System.out.println(newStatement);
                    statements.add(newStatement);
                    System.out.println(dataSetId);
                    return dataSetId;
                }
            }
        }
        throw new RDFHandlerException ("Unable to find a " + VoidConstants.TARGETURI + " with " 
                + VoidConstants.URI_SPACEURI + " that covers " + fullURI);
    }

   private void checkHasVersionOrDate(Value dataSetId) throws RDFHandlerException {
        Value license = findPossibleSingletonObject (dataSetId, PavConstants.VERSION);
        if (license != null) return;
        Value date = findPossibleSingletonObject (dataSetId, PavConstants.CREATED_ON);
        if (date != null) return;
        date = findPossibleSingletonObject (dataSetId, PavConstants.DERIVED_ON);
        if (date != null) return;
        date = findPossibleSingletonObject (dataSetId, PavConstants.IMPORTED_ON);
        if (date != null) return;
        date = findPossibleSingletonObject (dataSetId, PavConstants.MODIFIED_ON);
        if (date != null) return;
        date = findPossibleSingletonObject (dataSetId, PavConstants.RETRIEVED_ON);
        if (date != null) return;
        throw new RDFHandlerException ("Could not find a Version for DataSet " + dataSetId + 
                " Please include at least one of " + PavConstants.VERSION + ", " + PavConstants.CREATED_ON + ", " 
                + PavConstants.DERIVED_ON + ", " + PavConstants.IMPORTED_ON + ", " + PavConstants.MODIFIED_ON + ", " +
                PavConstants.RETRIEVED_ON);
   }
       
   private Resource findPossibleSingletonSubject (URI predicate) throws RDFHandlerException{
        Resource subject = null;
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
    
   private Resource findPossibleSingletonSubject (URI predicate, Value object) throws RDFHandlerException{
        Resource subject = null;
        for (Statement st:statements){
            if (st.getPredicate().equals(predicate) && st.getObject().equals(object)){
                if (subject != null){
                    throw new RDFHandlerException ("Found more than one statement with predicate " + predicate + 
                            " and object " + object);
                }
                subject = st.getSubject();
            }
        }
        return subject;
    }

    private Value findPossibleSingletonObject (URI predicate) throws RDFHandlerException{
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
    
    private Value findPossibleSingletonObject (Value subject, URI predicate) throws RDFHandlerException{
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
        return object;
    }

    private Value findPossibleObject (Value subject, URI predicate) throws RDFHandlerException{
        Value object = null;
        for (Statement st:statements){
            if (st.getSubject().equals(subject) && st.getPredicate().equals(predicate)){
                return object = st.getObject();
            }
        }
        return object;
    }

    private Value findTheSingletonObject (Value subject, URI predicate) throws RDFHandlerException{
        Value object = findPossibleSingletonObject(subject, predicate);
        if (object == null){
            throw new RDFHandlerException ("Found no statement with subject " + subject +  " and predicate " + predicate);
        }
        return object;
    }

    private Value findOneofManyObject (Value subject, URI predicate) throws RDFHandlerException{
        for (Statement st:statements){
            if (st.getSubject().equals(subject) && st.getPredicate().equals(predicate)){
                return st.getObject();
            }
        }
        throw new RDFHandlerException ("Found no statement with subject " + subject +  " and predicate " + predicate);
   }

   private void checkStatementExists (Value subject, URI predicate, Value object) throws RDFHandlerException{
         for (Statement st:statements){
            if (st.getSubject().equals(subject) && st.getPredicate().equals(predicate) && st.getObject().equals(object)){
                return;
            }
        }
        throw new RDFHandlerException ("Found no statement with subject " + subject +  " predicate " + predicate + 
                " and object " + object);
    }

    private Resource findTheSingletonSubject (URI predicate, Value object) throws RDFHandlerException{
        Resource subject = null;
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
