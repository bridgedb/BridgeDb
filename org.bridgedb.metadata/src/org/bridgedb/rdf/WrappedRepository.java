/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.IntegerLiteralImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

/**
 *
 * @author Christian
 */
public class WrappedRepository {

    private RepositoryConnection connection;
    private Repository repository;
    private static final Resource ANY_SUBJECT = null;
    private static final URI ANY_PREDICATE = null;
    private static final Value ANY_OBJECT = null;
    private static final boolean EXCLUDE_INFERRED =false;
   
    public WrappedRepository(Repository repository) throws RdfException{
        this.repository = repository;
    }
    
    public List<Statement> getStatementList(Resource subjectResource, URI predicate, Value object, Resource... contexts) 
            throws RdfException {
        RepositoryConnection connection = getConnection();
        try {
            RepositoryResult<Statement> rr = 
                    connection.getStatements(subjectResource, predicate, object, EXCLUDE_INFERRED, contexts);
            List<Statement> results = rr.asList();
            close();
            return results;
        } catch (Throwable ex) {
            error("Error getting statement ", ex);
            return null;
        }
    }

    private RepositoryConnection getConnection() throws RdfException{
        try {
            repository.initialize();
        } catch (RepositoryException ex) {
            error("Error initializing Repository. ", ex);
        }
        try {
            connection =  repository.getConnection();
        } catch (RepositoryException ex) {
            error("Unable to get connection. ", ex);
        }
        return connection;
    }

    public void clearAndClose() throws RdfException {
        RepositoryConnection connection = getConnection();
        try {
            connection.clear();
        } catch (RepositoryException ex) {
            throw new RdfException("Error clearing repository ", ex);
        } finally {
            close();
        }
    }
    
    public Value getPossibleSingeltonObject(Resource subject, URI predicate, Resource... contexts) throws RdfException {
        List<Statement> statements = getStatementList(subject, predicate, ANY_OBJECT, contexts);
        if (statements.size() == 1) {
            Statement statement = statements.get(0);
            return statement.getObject();
        } else if (statements.size() == 0) {
               return null;
        }
        throw new RdfException ("Found more than one Object with Subject " + subject + 
                " and Predicate " + predicate + " in context(s) " + toString(contexts));
    }

    public Value getTheSingeltonObject(Resource subject, URI predicate, Resource... contexts) throws RdfException {
        Value possible = getPossibleSingeltonObject(subject, predicate, contexts);
        if (possible != null) return possible;
        throw new RdfException ("Found no Object with Subject " + subject + " and Predicate " + predicate + 
                " in context(s) " + toString(contexts));
    }
    
    public int getAndIncrementValue(Resource subject, URI predicate, Resource context) 
            throws RdfException {
        RepositoryConnection connection = getConnection();
        int newValue = 1;
        try {
            RepositoryResult<Statement> rr = 
                    connection.getStatements(subject, predicate, ANY_OBJECT, EXCLUDE_INFERRED, context);
            List<Statement> results = rr.asList();
            if (results.isEmpty()){
                newValue = 1;
            } else if (results.size() == 1){
                Literal literal = (Literal)results.get(0).getObject();
                newValue = literal.intValue() + 1;
                connection.remove(results.get(0), context);
            } else {
                throw new RdfException ("Found more than one Object with Subject " + subject + 
                    " and Predicate " + predicate + " in context(s) " + context);
            }
        } catch (Throwable ex) {
            error("Error getting statement ", ex);
        }
        try {
            Literal literal = new IntegerLiteralImpl(BigInteger.valueOf(newValue));
            connection.add(subject, predicate, literal, context);
        } catch (RepositoryException ex) {
            Logger.getLogger(WrappedRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        close();
        return newValue;
    }

    public void addStatements(Set<Statement> statements, Resource... contexts) throws RdfException {
        RepositoryConnection connection = getConnection();
        for (Statement statement:statements){
            try {
                connection.add(statement, contexts);
            } catch (RepositoryException ex) {
                error("Error adding statement " + statement + " to " + toString(contexts), ex);
            }
        }
        close();
    }

   /*public static RepositoryResult<Statement> getStatements(RepositoryConnection connection, 
            Resource subject, URI predicate, Value object, Resource... contexts) throws RDFHandlerException {
        try {
            return connection.getStatements(subject, predicate, object, false, contexts);
        } catch (Throwable ex) {
            shutdownAfterError(connection);
            throw new RDFHandlerException("Error getting statement ", ex);
        }
    }*/
    
    /*
    public static Resource getPossibleSingeltonSubject(RepositoryConnection connection, 
            URI predicate, Value object, Resource... contexts) throws RDFHandlerException {
        try {
            RepositoryResult<Statement> rr = connection.getStatements(null, predicate, object, false, contexts);
            List<Statement> statements = rr.asList();
            if (statements.size() == 1) {
                Statement statement = statements.get(0);
                return statement.getSubject();
            } else if (statements.size() == 0) {
                return null;
            }
        } catch (Throwable ex) {
            shutdownAfterError(connection);
            throw new RDFHandlerException("Error getting statement ", ex);
        }
        shutdownAfterError(connection);
        throw new RDFHandlerException ("Found more than one Subject with Predicate " + predicate + " Object " + object +
                " in context(s) " + toString(contexts));
    }

    public static Resource getTheSingeltonSubject(RepositoryConnection connection, URI predicate, Value object, 
            Resource... contexts) throws RDFHandlerException {
        Resource possible = getPossibleSingeltonSubject(connection, predicate, object, contexts);
        if (possible != null) return possible;
        shutdownAfterError(connection);
        throw new RDFHandlerException ("Found no Subject with Predicate " + predicate + " Object " + object +
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
    
    protected static void add(RepositoryConnection connection, 
            Resource subject, URI predicate, Value object, Resource... contexts) throws RDFHandlerException{
        try {
            connection.add(subject, predicate, object, contexts);
        } catch (Throwable ex) {
            shutdownAfterError(connection);
            throw new RDFHandlerException("Error adding ", ex);            
        }
    }
    
    protected static List<Statement> asList (RepositoryConnection connection, RepositoryResult<Statement> rr) throws RDFHandlerException{
        try {
            return rr.asList();
        } catch (Throwable ex) {
            shutdownAfterError(connection);
            throw new RDFHandlerException("Error converting to list ", ex);            
        }
    }
    
    protected static void remove(RepositoryConnection connection, RepositoryResult<Statement> rr) throws RDFHandlerException{
        try {
            connection.remove(rr);
        } catch (Throwable ex) {
            shutdownAfterError(connection);
            throw new RDFHandlerException("Error removing from connection ", ex);            
        }        
    }
    
    public static void shutdown(RepositoryConnection connection) throws RDFHandlerException{
        try {
            shutdown(connection.getRepository(), connection);
        } catch (IDMapperLinksetException ex) {
            throw new RDFHandlerException("Error shutting down");
        }
    }
     
     private static void shutdown(Repository repository, RepositoryConnection connection) throws IDMapperLinksetException{
        try {
            connection.close();
        } catch (Throwable ex) {
            throw new IDMapperLinksetException ("Error closing connection ", ex);
        } finally {
            try {
                repository.shutDown();
            } catch (Throwable ex) {
                throw new IDMapperLinksetException ("Error shutting down repository ", ex);
            }
        }
    }
    */
            
    private void error(String message, Throwable cause) throws RdfException{
        close();
        throw new RdfException(message, cause);
    }

    private void close(){
        if (connection == null){
            try {
                connection.close();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
        try {
            repository.shutDown();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
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


}
