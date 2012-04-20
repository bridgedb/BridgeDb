/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.sail.nativerdf.NativeStore;

/**
 *
 * @author Christian
 */
public class RepositoryFactory {
    private static Repository repository;

    public static Repository getRepository() throws RepositoryException {
        if (repository == null){
            File dataDir = new File("C:/temp/sailtest");
            repository = new SailRepository(new NativeStore(dataDir));
            //myRepository = new SailRepository(new MemoryStore());
            repository.initialize();
        }
        return repository;
    }

    public static Value getStringletonObject(Resource subject, URI predicateURI, Resource graph) throws RDFHandlerException{
        List<Statement> list;
        try {
            RepositoryResult<Statement> rr = 
                    getRepository().getConnection().getStatements(subject, predicateURI, null, false, graph);
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
                    " and predicate " + predicateURI);            
        }           
    }
    
    public static List<Value> getObjects(Resource subject, URI predicateURI, Resource graph) throws RDFHandlerException{
        List<Statement> statements;
        List<Value> objects = new ArrayList<Value>();
        try {
            RepositoryResult<Statement> rr = 
                    getRepository().getConnection().getStatements(subject, predicateURI, null, false, graph);
            statements = rr.asList();
        } catch (RepositoryException ex) {
            throw new RDFHandlerException ("Unable to extract statements ", ex);
        }
        for (Statement statement:statements){
            objects.add(statement.getObject());
        }
        return objects;
    }

    public static Boolean containsObject(Resource subject, URI predicateURI, Value object, Resource graph) throws RDFHandlerException{
        List<Statement> list;
        try {
            RepositoryResult<Statement> rr = 
                    getRepository().getConnection().getStatements(subject, predicateURI, null, false, graph);
            list = rr.asList();
            for (Statement statement:list){
                if (statement.getObject().equals(object)){
                    return true;
                }
            }
            return false;
        } catch (RepositoryException ex) {
            throw new RDFHandlerException ("Unable to extract statements ", ex);
        }
    }

    public static void addStatement(Resource subject, URI predicate, Value object, Resource graph) throws RepositoryException{
        getRepository().getConnection().add(subject, predicate, object, graph);
    }
}
