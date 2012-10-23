// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.rdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.rdfxml.RDFXMLWriter;

/**
 *
 * @author Christian
 */
public class RdfWrapper {
    public static final Resource ANY_SUBJECT = null;
    public static final URI ANY_PREDICATE = null;
    public static final Value ANY_OBJECT = null;
    public static final boolean EXCLUDE_INFERRED =false;

    private RepositoryConnection hiddenConnection;
    
    RdfWrapper(RepositoryConnection connection){
        this.hiddenConnection = connection;
    }

    private RepositoryConnection getConnection() {
        if (hiddenConnection == null){
            throw new IllegalStateException("No calls allowed after shutdown or an Exception");
        }
        return hiddenConnection;
    }

    public synchronized void clear() throws IDMapperLinksetException{
        try {
            RepositoryConnection connection = this.getConnection();
            connection.clear();
            shutdown();
        } catch (Throwable ex) {
            shutdownAfterError();
            throw new IDMapperLinksetException ("Error clearing the Reposotory. ", ex);
        }
    }

    public List<Statement> getStatementList(Value subject, URI predicate, Value object, Resource... contexts) 
            throws RDFHandlerException {
        RepositoryConnection connection = this.getConnection();
        try {
            Resource subjectResource = (Resource)subject;
            RepositoryResult<Statement> rr = 
                    connection.getStatements(subjectResource, predicate, object, false, contexts);
            return rr.asList();
        } catch (Throwable ex) {
            shutdownAfterError();
            throw new RDFHandlerException("Error getting statement ", ex);
        }
    }
    
    private Resource getPossibleSingeltonSubject(URI predicate, Value object, Resource... contexts) 
            throws RDFHandlerException {
        RepositoryConnection connection = this.getConnection();
        List<Statement> statements;
        try {
            RepositoryResult<Statement> rr = connection.getStatements(null, predicate, object, false, contexts);
            statements = rr.asList();
            if (statements.size() == 1) {
                Statement statement = statements.get(0);
                return statement.getSubject();
            } else if (statements.size() == 0) {
                return null;
            }
        } catch (Throwable ex) {
            shutdownAfterError();
            throw new RDFHandlerException("Error getting statement ", ex);
        }
        shutdownAfterError();
        throw new RDFHandlerException ("Found more than one Subject with Predicate " + predicate + " Object " + object +
                " in context(s) " + toString(contexts) + "\n" + statements);
    }

    public Resource getTheSingeltonSubject(URI predicate, Value object, Resource... contexts) throws RDFHandlerException {
        Resource possible = getPossibleSingeltonSubject(predicate, object, contexts);
        if (possible != null) return possible;
        shutdownAfterError();
        throw new RDFHandlerException ("Found no Subject with Predicate " + predicate + " Object " + object +
                " in context(s) " + toString(contexts));
    }
    
    public Value getPossibleSingeltonObject(Value subject, URI predicate, Resource... contexts) throws RDFHandlerException {
        RepositoryConnection connection = this.getConnection();
        try {
            Resource subjectResource = (Resource)subject;
            RepositoryResult<Statement> rr = connection.getStatements(subjectResource, predicate, null, false, contexts);
            List<Statement> statements = rr.asList();
            if (statements.size() == 1) {
                Statement statement = statements.get(0);
                return statement.getObject();
            } else if (statements.size() == 0) {
                return null;
            }
        } catch (Throwable ex) {
            shutdownAfterError();
            throw new RDFHandlerException("Error getting statement ", ex);
        }
        shutdownAfterError();
        throw new RDFHandlerException ("Found more than one Object with Subject " + subject + 
                " and Predicate " + predicate + " in context(s) " + toString(contexts));
    }

    public Value getTheSingeltonObject(Value subject, URI predicate, Resource... contexts) 
            throws RDFHandlerException {
        Value possible = getPossibleSingeltonObject(subject, predicate, contexts);
        if (possible != null) return possible;
        shutdownAfterError();
        throw new RDFHandlerException ("Found no Object with Subject " + subject + " and Predicate " + predicate + 
                " in context(s) " + toString(contexts));
    }
    
    private static String toString (Resource... contexts){
        if (contexts.length == 0){
            return "all";
        }
        StringBuilder toString = new StringBuilder("[");
        for (Resource context: contexts){
            toString.append(context);
            toString.append(", ");
        }
        toString.append("] ");  
        return toString.toString();
    }
    
    public void add(Resource subject, URI predicate, Value object, Resource... contexts) 
            throws RDFHandlerException{
        RepositoryConnection connection = this.getConnection();
        try {
            connection.add(subject, predicate, object, contexts);
        } catch (Throwable ex) {
            shutdownAfterError();
            throw new RDFHandlerException("Error adding ", ex);            
        }
    }
    
    protected List<Statement> asList (RepositoryResult<Statement> rr) throws RDFHandlerException{
        try {
            return rr.asList();
        } catch (Throwable ex) {
            shutdownAfterError();
            throw new RDFHandlerException("Error converting to list ", ex);            
        }
    }
    
    protected void remove(RepositoryResult<Statement> rr) throws RDFHandlerException{
        RepositoryConnection connection = this.getConnection();
        try {
            connection.remove(rr);
        } catch (Throwable ex) {
            shutdownAfterError();
            throw new RDFHandlerException("Error removing from connection ", ex);            
        }        
    }
    
    private void shutdownAfterError(){
        try {
            shutdown();
        } catch (RDFHandlerException ex1) {
            //print the stack trace as the caller will throw an exception
            ex1.printStackTrace();
        }
    }
    
    public void shutdown() throws RDFHandlerException{
        if (hiddenConnection == null){
            return; //Already closed so no problem
        }
        Repository repository = hiddenConnection.getRepository();
        try {
            hiddenConnection.close();
            this.hiddenConnection = null;
        } catch (Throwable ex) {
            throw new RDFHandlerException ("Error closing connection ", ex);
        } finally {
            try {
                repository.shutDown();
            } catch (Throwable ex) {
                throw new RDFHandlerException ("Error shutting down repository ", ex);
            }
        }
    }
    
    List<String> getContextNames() throws RDFHandlerException {
        RepositoryConnection connection = this.getConnection();
        try {
            RepositoryResult<Resource> rr = connection.getContextIDs();
            List<Resource> resources = rr.asList();
            ArrayList<String> linksetNames = new ArrayList<String>();
            for (Resource resource:resources){
                linksetNames.add(resource.stringValue());
            }
            shutdown();
            return linksetNames;
        } catch (Throwable ex) {
            shutdown();
            throw new RDFHandlerException ("Error extracting context names.", ex);
        }
    }

    //public static URI getLinksetURL(Value linksetId){
    //    return new URIImpl(RdfWrapper.getTheBaseURI() + "linkset/" + linksetId.stringValue());  
    //}
    
    String getRDF(Resource graph) throws IDMapperLinksetException {
        StringOutputStream stringOutputStream = new StringOutputStream();            
        RDFXMLWriter writer = new RDFXMLWriter(stringOutputStream);
        writer.startRDF();
        RepositoryResult<Statement> rr;
        RepositoryConnection connection = this.getConnection();
        try {
            rr = 
                    connection.getStatements(ANY_SUBJECT, ANY_PREDICATE, ANY_OBJECT, EXCLUDE_INFERRED, graph);
        } catch (Throwable ex) {
            shutdownAfterError();
            throw new IDMapperLinksetException ("Error extracting rdf.", ex);
        }
        try {
            while (rr.hasNext()){
                Statement st = rr.next();
                writer.handleStatement(st);
            }
            writer.endRDF();
            shutdown();
            return stringOutputStream.toString();
        } catch (Throwable ex) {
            shutdownAfterError();
            throw new IDMapperLinksetException ("Error extracting rdf.", ex);
        }
    }
 
}
