/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import org.bridgedb.rdf.reader.StatementReader;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

/**
 *
 * @author Christian
 */
public abstract class RdfBase {
    static final boolean VERSION2 = true;

    static String scrub(String original){
        String result = original.replaceAll("\\W", "_");
        while(result.contains("__")){
            result = result.replace("__", "_");
        }
        if (result.endsWith("_")){
            result = result.substring(0, result.length()-1);
        }
        return result;
    }
    
    static String convertToShortName(Value value) {
        String id = value.stringValue();
        id = id.replace(StatementReader.DEFAULT_BASE_URI, ":");
        return id;
    }

    static String getSingletonString(RepositoryConnection repositoryConnection, Resource id, URI predicate) 
            throws BridgeDBException, RepositoryException {
        String result = getPossibleSingletonString(repositoryConnection, id, predicate);
        if (result == null){
            throw new BridgeDBException("No statement found with resource " + id + " and predicate " + predicate);
        }
        return result;
    }

    static String getPossibleSingletonString(RepositoryConnection repositoryConnection, Resource id, 
            URI predicate) throws RepositoryException, BridgeDBException {
        RepositoryResult<Statement> statements = 
                repositoryConnection.getStatements(id, predicate, null, true);
        if (statements.hasNext()) {
            Statement statement = statements.next();
            String result = statement.getObject().stringValue();
            if (statements.hasNext()) {
                throw new BridgeDBException("Found more than one statement with resource " + id 
                        + " and predicate " + predicate);
            }      
            return result;
        }
        return null;
    }


}
