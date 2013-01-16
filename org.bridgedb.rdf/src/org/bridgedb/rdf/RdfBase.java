// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009 BridgeDb developers
// Copyright 2012  Christian Y. A. Brenninkmeijer
// Copyright 2012  OpenPhacts
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

import java.util.HashSet;
import java.util.Set;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

/**
 *
 * @author Christian
 */
public abstract class RdfBase {
    public static String DEFAULT_BASE_URI = "http://no/BaseURI/Set/";

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
        id = id.replace(DEFAULT_BASE_URI, ":");
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
        Value result = getPossibleSingleton(repositoryConnection, id, predicate);
        if (result == null) {
            return null;
        } else {     
            return result.stringValue();
        }
    }

    static Resource getPossibleSingletonResource(RepositoryConnection repositoryConnection, Resource id, 
            URI predicate) throws RepositoryException, BridgeDBException {
        Value value = getPossibleSingleton(repositoryConnection, id, predicate);
        return toResource(value);
    }

    private static Resource toResource(Value value){
        if (value == null) {
            return null;
        } else if (value instanceof Resource){
            return (Resource)value;
        }
        return new URIImpl(value.stringValue());        
    }
    
    static Value getPossibleSingleton(RepositoryConnection repositoryConnection, Resource id, 
            URI predicate) throws RepositoryException, BridgeDBException {
        RepositoryResult<Statement> statements = 
                repositoryConnection.getStatements(id, predicate, null, true);
        if (statements.hasNext()) {
            Statement statement = statements.next();
            if (statements.hasNext()) {
                throw new BridgeDBException("Found more than one statement with resource " + id 
                        + " and predicate " + predicate + "\nFound: " + statement + "\n\t" + statements.next());
            } else {
                return statement.getObject();
            }     
        }
        return null;
    }

    static Set<String> getAllStrings(RepositoryConnection repositoryConnection, Resource id, URI predicate) 
            throws RepositoryException {
        HashSet<String> results = new HashSet<String>();
        RepositoryResult<Statement> statements = 
                repositoryConnection.getStatements(id, predicate, null, true);
        while(statements.hasNext()) {
            Statement statement = statements.next();
            results.add(statement.getObject().stringValue());
        }
        return results;
    }

    static Set<Resource> getAllResources(RepositoryConnection repositoryConnection, Resource id, URI predicate) 
            throws RepositoryException {
        HashSet<Resource> results = new HashSet<Resource>();
        RepositoryResult<Statement> statements = 
                repositoryConnection.getStatements(id, predicate, null, true);
        while(statements.hasNext()) {
            Statement statement = statements.next();
            Value value = statement.getObject();
            if (value != null){
                results.add(toResource(value));
            }
        }
        return results;
    }
}
