/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.util.ArrayList;
import java.util.List;
import org.bridgedb.linkset.IDMapperLinksetException;
import org.bridgedb.linkset.VoidConstants;
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
public class LinksetStore {
    private static LinksetStore production;
    private static LinksetStore testStore;

    private Repository repository;    
    private final Resource metaData;
    
    private static final URI HIGHEST_LINKSET_ID_PREDICATE = new URIImpl("http://www.bridgedb.org/highested_linkset_id");
    
    //CONSTANTS to make code easier to read
    private static final Resource ANY_SUBJECT = null;
    private static final URI ANY_PREDICATE = null;
    private static final Value ANY_OBJECT = null;
    private static final boolean EXCLUDE_INFERRED =false;
    private static final Resource ANY_RESOURCE = null;
    
    public static LinksetStore factory() throws IDMapperLinksetException{
        if (production == null){
            production = new LinksetStore(false);
        }
        return production;
    }
            
    public static LinksetStore testFactory() throws IDMapperLinksetException{
        if (testStore == null){
            testStore = new LinksetStore(true);
        }
        return testStore;
    }

    private LinksetStore(boolean test) throws IDMapperLinksetException{
        try {
            if (test){
                repository = RdfFactory.getTestRepository();
            } else {
                repository = RdfFactory.getRepository();                
            }
            String baseURI = RdfFactory.getBaseURI();
            metaData = new URIImpl(baseURI + "/MetaData");
        } catch (RepositoryException ex) {
            throw new IDMapperLinksetException("Unable to connect to rdfstore", ex);
        }
    }
    
    public Resource createNewGraph() throws RDFHandlerException {
        String baseURI = RdfFactory.getBaseURI(); 
        Value lastLinksetId = getStringletonObject(metaData, HIGHEST_LINKSET_ID_PREDICATE, ANY_RESOURCE);
        int linksetId;
        if (lastLinksetId == null){
            linksetId = 1;
        } else {
            linksetId = Integer.parseInt(lastLinksetId.stringValue()) + 1;
        }
        lastLinksetId = new LiteralImpl("" + linksetId);
        updateObject(metaData, HIGHEST_LINKSET_ID_PREDICATE, lastLinksetId);
        return new URIImpl(baseURI + "/linkset/" + linksetId);
    }

    public void addStatement(Resource subject, URI predicate, Value object, Resource... graphs) 
            throws RDFHandlerException{
        try {
            getConnection().add(subject, predicate, object, graphs);
        } catch (RepositoryException ex) {
            throw new RDFHandlerException("Error adding a statment", ex);
        }
    }

    public Resource getLinksetId(Resource linkSetGraph) throws RDFHandlerException{
        Value linksetId = getStringletonObject(null, VoidConstants.LINK_PREDICATE_URI, linkSetGraph);
        if (linksetId != null) return (Resource)linksetId;
        throw new RDFHandlerException("Graph " + linkSetGraph + " doe snot have a " + VoidConstants.LINK_PREDICATE_URI 
                + " statement");
    }
    
    public URI getSubjectUriSpace(Statement linkStatement, Resource linkSetGraph) 
            throws RDFHandlerException {
        URI subjectURI = (URI)linkStatement.getSubject();
        Resource subjectDataSet = (Resource)getStringletonObject(ANY_SUBJECT, VoidConstants.SUBJECTSTARGETURI, linkSetGraph);
        if (subjectDataSet != null){
            return getUriSpace(subjectDataSet, linkSetGraph, subjectURI, true);
        }
        List<Value> dataSets = getObjects(null, VoidConstants.TARGETURI, linkSetGraph);
        for (Value dataSet:dataSets){
            URI URIspace = getUriSpace((Resource)dataSet, linkSetGraph, subjectURI, false);
            if (URIspace != null) {
                addStatement(getLinksetId(linkSetGraph), VoidConstants.SUBJECTSTARGETURI, dataSet, linkSetGraph);
                return URIspace;
            }
        }
        throw new RDFHandlerException ("Unable to find a valid URISpace for the subject " + subjectURI);
    }
     
    public URI getObjectUriSpace(Statement linkStatement, Resource linkSetGraph) 
            throws RDFHandlerException {
        URI objectURI = (URI)linkStatement.getObject();
        Resource objectDataSet = (Resource)getStringletonObject(ANY_SUBJECT, VoidConstants.OBJECTSTARGETURI, linkSetGraph);
        if (objectDataSet != null){
            return getUriSpace(objectDataSet, linkSetGraph, objectURI, true);
        }
        List<Value> dataSets = getObjects(null, VoidConstants.TARGETURI, linkSetGraph);
        for (Value dataSet:dataSets){
            URI URIspace = getUriSpace((Resource)dataSet, linkSetGraph, objectURI, false);
            if (URIspace != null) {             
                addStatement(getLinksetId(linkSetGraph), VoidConstants.OBJECTSTARGETURI, dataSet, linkSetGraph);
                return URIspace;
            }
        }
        throw new RDFHandlerException ("Unable to find a valid URISpace for the object " + objectURI) ;
    }

    public List<Resource> getLinksetGraphs() throws IDMapperLinksetException{
        try {
            RepositoryResult<Resource> rr = getConnection().getContextIDs();
            return rr.asList();
        } catch (RepositoryException ex) {
            throw new IDMapperLinksetException("Unable to get graphs ", ex);
        }
    }
    
    public List<Statement> getStatemenets (Resource linkSetGraph) throws IDMapperLinksetException {
        try {
            RepositoryResult<Statement> rr = 
                getConnection().getStatements(ANY_SUBJECT, ANY_PREDICATE, ANY_OBJECT, EXCLUDE_INFERRED, linkSetGraph); 
            return rr.asList();
        } catch (RepositoryException ex) {
            throw new IDMapperLinksetException("Unable to clear repository ", ex);
        }
    }
   
    public void clear() throws IDMapperLinksetException{
        try {
            getConnection().clear();
        } catch (RepositoryException ex) {
            throw new IDMapperLinksetException("Unable to clear repository ", ex);
        }
    }
    
    private RepositoryConnection getConnection() throws RepositoryException{
        return repository.getConnection();
    }
    
    private URI getUriSpace(Resource dataSet, Resource linkSetGraph, URI fullURI, boolean strict) throws RDFHandlerException{
        URI uriSpace = (URI)getStringletonObject(dataSet, VoidConstants.URI_SPACEURI, linkSetGraph);
        if (uriSpace == null){
            throw new RDFHandlerException("DataSet " + dataSet + " in linkSet " + linkSetGraph + " does not have a " +
                    VoidConstants.URI_SPACEURI + " statement .");
        }
        if (fullURI.stringValue().startsWith(uriSpace.stringValue())){
            return uriSpace;
        }
        if (strict){
            throw new RDFHandlerException ("URI " + fullURI + " does not match the declared uriSpace " + uriSpace + 
                    " for dataSet " + dataSet);
        }
        return null;
    }
        
    private Value getStringletonObject(Resource subject, URI predicateURI, Resource graph) throws RDFHandlerException{
        List<Statement> list;
        try {
            RepositoryResult<Statement> rr = getConnection().getStatements(subject, predicateURI, null, false, graph);
            list = rr.asList();
        } catch (RepositoryException ex) {
            throw new RDFHandlerException ("Unable to extract statements ", ex);
        }
        if (list.size() == 1){
            return list.get(0).getObject();
        }
        if (list.isEmpty()){
            return null;
        } else {
            System.err.println(list);
            throw new RDFHandlerException("Found more than one statement with subject " + subject + 
                    " and predicate " + predicateURI + " in " + graph);            
        }           
    }

    private List<Value> getObjects(Resource subject, URI predicateURI, Resource graph) throws RDFHandlerException{
        List<Statement> statements;
        List<Value> objects = new ArrayList<Value>();
        try {
            RepositoryResult<Statement> rr = 
                    getConnection().getStatements(subject, predicateURI, null, false, graph);
            statements = rr.asList();
        } catch (RepositoryException ex) {
            throw new RDFHandlerException ("Unable to extract statements ", ex);
        }
        for (Statement statement:statements){
            objects.add(statement.getObject());
        }
        return objects;
    }

    private void updateObject(Resource subject, URI predicate, Value newValue, Resource... graph) throws RDFHandlerException  {
        try {
            RepositoryConnection connection = getConnection();
            RepositoryResult<Statement> previous = connection.getStatements(subject, predicate, null, false, graph);
            connection.remove(previous, graph);
            connection.add(subject, predicate, newValue, graph);
        } catch (RepositoryException ex) {
           throw new RDFHandlerException("Error updating Object ", ex);
        }
    }

    private void showAll() throws IDMapperLinksetException{
        List<Resource> graphs = getLinksetGraphs();
        for (Resource graph:graphs){
            System.out.println();
            System.out.println(graph);
            System.out.println(this.getStatemenets(graph));
        }
    }
    
    public static void main(String[] args) throws IDMapperLinksetException {
        System.out.println("production");
        LinksetStore linksetStore = LinksetStore.factory();
        linksetStore.showAll();
        System.out.println("Test");
        linksetStore = LinksetStore.testFactory();
        linksetStore.showAll();
    }
}
