/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.rdf.constants.RdfConstants;
import org.bridgedb.rdf.constants.VoidConstants;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

/**
 *
 * @author Christian
 */
public class UriPattern extends RdfBase {

    private final String nameSpace;
    private final String postfix;
    private DataSource dataSource;
    
    private static HashMap<Resource, UriPattern> register = new HashMap<Resource, UriPattern>();
    private static HashMap<String,UriPattern> byNameSpaceOnly = new HashMap<String,UriPattern>();
    private static HashMap<String,HashMap<String,UriPattern>> byNameSpaceAndPostFix = 
            new HashMap<String,HashMap<String,UriPattern>> ();  

    private UriPattern(String namespace){
        this.nameSpace = namespace;
        this.postfix = null;
        byNameSpaceOnly.put(namespace, this);
        register.put(getResourceId(), this);
    } 
    
    private UriPattern(String namespace, String postfix){
        this.nameSpace = namespace;
        if (postfix == null || postfix.isEmpty()){
            this.postfix = null;
            byNameSpaceOnly.put(namespace, this);    
        } else {
            this.postfix = postfix;
            HashMap<String,UriPattern> postFixMap = byNameSpaceAndPostFix.get(namespace);
            if (postFixMap == null){
                postFixMap = new HashMap<String,UriPattern>();
            }
            postFixMap.put(postfix, this);
            byNameSpaceAndPostFix.put(namespace, postFixMap);
        }
        register.put(getResourceId(), this);
    }
   
    public static UriPattern byNameSpace(String nameSpace){
        UriPattern result = byNameSpaceOnly.get(nameSpace);
        if (result == null){
            result = new UriPattern(nameSpace);
        }
        return result;
    }
    
    private static UriPattern byNameSpaceAndPostfix(String nameSpace, String postfix) {
        HashMap<String,UriPattern> postFixMap = byNameSpaceAndPostFix.get(nameSpace);
        if (postFixMap == null){
            return new UriPattern(nameSpace, postfix);
        }
        UriPattern result = postFixMap.get(postfix);
        if (result == null){
            return new UriPattern(nameSpace, postfix);
        }
        return result;
    }
                
    public static UriPattern byUrlPattern(String urlPattern) throws BridgeDBException{
        int pos = urlPattern.indexOf("$id");
        if (pos == -1) {
            throw new BridgeDBException("Urlpattern should have $id in it");
        }
        String nameSpace = urlPattern.substring(0, pos);
        String postfix = urlPattern.substring(pos + 3);
        return byNameSpaceAndPostFix(nameSpace, postfix);
    }

    public static UriPattern byNameSpaceAndPostFix(String nameSpace, String postfix) throws BridgeDBException{
        if (postfix.isEmpty()){
            return byNameSpace(nameSpace);
        } else {
            return byNameSpaceAndPostfix(nameSpace, postfix);
        }
    }
    
    public void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    }
    
    public DataSource getDataSource(){
        return dataSource;
    }
    
    public final URI getResourceId(){
        return new URIImpl(getUriPattern());
    }
    
    public String getUriPattern() {
        if (postfix == null){
            return nameSpace + "$id";
        } else {
            return nameSpace + "$id" + postfix;
        }
    }

    public static void addAll(RepositoryConnection repositoryConnection) throws IOException, RepositoryException {
        for (UriPattern uriPattern:register.values()){
            uriPattern.add(repositoryConnection);
        }        
    }
    
    public void add(RepositoryConnection repositoryConnection) throws RepositoryException{
        URI id = getResourceId();
        repositoryConnection.add(id, RdfConstants.TYPE_URI, BridgeDBConstants.URI_PATTERN_URI);
        repositoryConnection.add(id, VoidConstants.URI_SPACE_URI,  new LiteralImpl(nameSpace));
        if (postfix != null){
            repositoryConnection.add(id, BridgeDBConstants.POSTFIX_URI,  new LiteralImpl(postfix));
        }
    }        
    
    public static void readAllUriPatterns(RepositoryConnection repositoryConnection) throws BridgeDBException, RepositoryException{
        RepositoryResult<Statement> statements = 
                repositoryConnection.getStatements(null, RdfConstants.TYPE_URI, BridgeDBConstants.URL_PATTERN_URI, true);
        while (statements.hasNext()) {
            Statement statement = statements.next();
            UriPattern pattern = readUriPattern(repositoryConnection, statement.getSubject());
        }
    }

    public static UriPattern readUriPattern(RepositoryConnection repositoryConnection, Resource id) 
            throws BridgeDBException, RepositoryException{
        UriPattern pattern = register.get(id);        
        if (pattern != null){
            return pattern;
        }
        String uriSpace = getSingletonString(repositoryConnection, id, VoidConstants.URI_SPACE_URI);
        String postfix = getPossibleSingletonString(repositoryConnection, id, BridgeDBConstants.POSTFIX_URI);
        if (postfix == null){
            pattern = UriPattern.byNameSpace(uriSpace);
        } else {
            pattern = UriPattern.byNameSpaceAndPostFix(uriSpace, postfix);
        }
        RepositoryResult<Statement> statements = 
                repositoryConnection.getStatements(id, null, null, true);
        while (statements.hasNext()) {
            Statement statement = statements.next();
            pattern.processStatement(statement);
        }
        //Constructor registers with standard recource this register with used resource
        register.put((URI)id, pattern);
        return pattern;
    }

    //Currently just checks for unexpected statements
    private void processStatement(Statement statement) throws BridgeDBException{
        if (statement.getPredicate().equals(BridgeDBConstants.POSTFIX_URI)){
            //Do nothing as already have the postfix
        } else if (statement.getPredicate().equals(VoidConstants.URI_SPACE_URI)){
            //Do nothing as laready have the uri space
        } else  {
             throw new BridgeDBException ("Unexpected Statement " + statement);
       }
    }

    @Override
    public String toString(){
        return getUriPattern();      
    }
    
}
