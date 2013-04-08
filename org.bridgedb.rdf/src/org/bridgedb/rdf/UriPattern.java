// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import org.bridgedb.DataSource;
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.rdf.constants.RdfConstants;
import org.bridgedb.rdf.constants.VoidConstants;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

/**
 *
 * @author Christian
 */
public class UriPattern extends RdfBase implements Comparable<UriPattern>{

    private final String nameSpace;
    private final String postfix;
    private DataSourceUris dataSourceUris;
    private TreeSet<DataSourceUris> isUriPatternOf = new TreeSet<DataSourceUris>();
    
    private static HashMap<Resource, UriPattern> register = new HashMap<Resource, UriPattern>();
    private static HashMap<String,UriPattern> byNameSpaceOnly = new HashMap<String,UriPattern>();
    private static HashMap<String,HashMap<String,UriPattern>> byNameSpaceAndPostFix = 
            new HashMap<String,HashMap<String,UriPattern>> ();  
    private static HashSet<URI> expectedPredicates = new HashSet<URI>(Arrays.asList(new URI[] {
        BridgeDBConstants.POSTFIX_URI,
        VoidConstants.URI_SPACE_URI,
        RdfConstants.TYPE_URI,
        BridgeDBConstants.HAS_DATA_SOURCE,
        BridgeDBConstants.IS_URI_PATTERN_OF
    }));
            
    private UriPattern(String namespace){
        this.nameSpace = namespace;
        this.postfix = "";
        byNameSpaceOnly.put(namespace, this);
        register.put(getResourceId(), this);
    } 
    
    private UriPattern(String namespace, String postfix){
        this.nameSpace = namespace;
        if (postfix == null || postfix.isEmpty()){
            this.postfix = "";
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
   
    public String getPrefix(){
        return nameSpace;
    }
    
    public String getPostfix(){
        return postfix;
    }
    
    public boolean hasPostfix(){
        return !postfix.isEmpty();
    }
    
    public static void refreshUriPatterns() throws BridgeDBException{
        register.clear();
        BridgeDBRdfHandler.init();
    }
    
    public static Set<UriPattern> getUriPatterns() {
        return new HashSet(register.values());
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
                
    public static UriPattern byPattern(String urlPattern) throws BridgeDBException{
        int pos = urlPattern.indexOf("$id");
        if (pos == -1) {
            throw new BridgeDBException("Urlpattern " + urlPattern + " does not have $id in it.");
        }
        String nameSpace = urlPattern.substring(0, pos);
        String postfix = urlPattern.substring(pos + 3);
        return byNameSpaceAndPostFix(nameSpace, postfix);
    }

    public static UriPattern existingByPattern(String uriPattern) {
        if (uriPattern == null || uriPattern.isEmpty()){
            return null;
        }
        String nameSpace;
        String postfix;
        String cleanPattern = uriPattern.trim();
        if (cleanPattern.startsWith("<") && cleanPattern.endsWith(">")){
            cleanPattern = cleanPattern.substring(1, cleanPattern.length()-1);
        }
        int pos = cleanPattern.indexOf("$id");
        if (pos == -1) {
            nameSpace = cleanPattern;
            postfix = "";
        } else {
            nameSpace = cleanPattern.substring(0, pos);
            postfix = cleanPattern.substring(pos + 3);
        } 
        if (postfix.isEmpty()){
            return byNameSpaceOnly.get(nameSpace);
        } else {
            HashMap<String,UriPattern> postFixMap = byNameSpaceAndPostFix.get(nameSpace);
            if (postFixMap == null){
                return null;
            }
            return postFixMap.get(postfix);
        }
    }

    public static UriPattern existingByNameSpaceandPrefix(String nameSpace, String postfix) {
        if (nameSpace == null || nameSpace.isEmpty()){
            return null;
        }
        if (postfix == null || postfix.isEmpty()){
            return byNameSpaceOnly.get(nameSpace);
        } else {
            HashMap<String,UriPattern> postFixMap = byNameSpaceAndPostFix.get(nameSpace);
            if (postFixMap == null){
                return null;
            }
            return postFixMap.get(postfix);
        }
    }

   public static UriPattern existingByNameSpace(String nameSpace) {
        return byNameSpaceOnly.get(nameSpace);
    }

    public static UriPattern byNameSpaceAndPostFix(String nameSpace, String postfix) throws BridgeDBException{
        if (postfix.isEmpty() || postfix.equals("NULL")){
            return byNameSpace(nameSpace);
        } else {
            return byNameSpaceAndPostfix(nameSpace, postfix);
        }
    }
    
    public void setPrimaryDataSource(DataSourceUris dsu) throws BridgeDBException{
        if (dataSourceUris ==  null) {       
            dataSourceUris = dsu;
            isUriPatternOf.remove(dsu);
        } else if (dataSourceUris.equals(dsu)){
            //Do nothing;
        } else {
            throw new BridgeDBException("Illegal attempt to set primary DataSource of " + this + " to " + dsu 
                    + " has it was already set to " + dataSourceUris);
        }  
    }
       
    public void setDataSource(DataSourceUris dsu) throws BridgeDBException{
        if (dsu == null){
            //ignore request
        } else if (dsu.equals(dataSourceUris)){
            //Do nothing;
        } else {
            isUriPatternOf.add(dsu);
        } 
     }
    
    public DataSource getPrimaryDataSource(){
        if (dataSourceUris == null){
            return null;
        }
        return dataSourceUris.getDataSource();
    }
    
    public DataSource getDataSource(){
        DataSourceUris main = getMainDataSourceUris();
        if (main == null){
            return DataSource.register(getUriPattern(), "Grouping for " + getUriPattern())
                    .urlPattern(getUriPattern())
                    .asDataSource();
        }
        return main.getDataSource();
    }
    
    public DataSourceUris getMainDataSourceUris() {
        if (dataSourceUris != null){
            return dataSourceUris;
        }
        if (isUriPatternOf.size() == 1){
            return isUriPatternOf.iterator().next();
        }
        return null;
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

    public static void addAll(RepositoryConnection repositoryConnection, boolean addPrimaries) 
            throws IOException, RepositoryException, BridgeDBException {
        TreeSet<UriPattern> all = new TreeSet(register.values());
        for (UriPattern uriPattern:all){
            uriPattern.add(repositoryConnection, addPrimaries);
        }        
    }
    
    public void add(RepositoryConnection repositoryConnection, boolean addPrimaries) throws RepositoryException{
        URI id = getResourceId();
        repositoryConnection.add(id, RdfConstants.TYPE_URI, BridgeDBConstants.URI_PATTERN_URI);
        repositoryConnection.add(id, VoidConstants.URI_SPACE_URI,  new LiteralImpl(nameSpace));
        if (!postfix.isEmpty()){
            repositoryConnection.add(id, BridgeDBConstants.POSTFIX_URI,  new LiteralImpl(postfix));
        }
        if (addPrimaries){
            DataSourceUris primary = getMainDataSourceUris();
            if (primary != null){
                repositoryConnection.add(id, BridgeDBConstants.HAS_DATA_SOURCE,  this.getMainDataSourceUris().getResourceId());            
            }        
            for (DataSourceUris dsu:isUriPatternOf){
                if (!dsu.equals(primary)){
                    repositoryConnection.add(id, BridgeDBConstants.IS_URI_PATTERN_OF,  dsu.getResourceId()); 
                }
            }
            
        } else {
            if (dataSourceUris != null){
                repositoryConnection.add(id, BridgeDBConstants.HAS_DATA_SOURCE,  dataSourceUris.getResourceId());            
            }
            for (DataSourceUris dsu:isUriPatternOf){
                 repositoryConnection.add(id, BridgeDBConstants.IS_URI_PATTERN_OF,  dsu.getResourceId()); 
            }
        }
    }        
    
    public static void readAllUriPatterns(RepositoryConnection repositoryConnection) throws BridgeDBException, RepositoryException{
        RepositoryResult<Statement> statements = 
                repositoryConnection.getStatements(null, RdfConstants.TYPE_URI, BridgeDBConstants.URI_PATTERN_URI, true);
        while (statements.hasNext()) {
            Statement statement = statements.next();
            UriPattern pattern = readUriPattern(repositoryConnection, statement.getSubject());
        }
    }

    public static UriPattern readUriPattern(RepositoryConnection repositoryConnection, Resource dataSourceId, DataSourceUris parent, 
            URI primary, URI shared) throws RepositoryException, BridgeDBException{
        return readUriPattern(repositoryConnection, dataSourceId, parent, primary, shared, null, null);
    }
    
    public static UriPattern readUriPattern(RepositoryConnection repositoryConnection, Resource dataSourceId, DataSourceUris parent, 
            URI primary, URI shared, URI old) throws RepositoryException, BridgeDBException{
        String oldString = getPossibleSingletonString(repositoryConnection, dataSourceId, old);
        return readUriPattern(repositoryConnection, dataSourceId, parent, primary, shared, old, oldString);
    }
    
    private static UriPattern readUriPattern(RepositoryConnection repositoryConnection, Resource dataSourceId, DataSourceUris parent, 
            URI primary, URI shared, URI old, String oldString) throws RepositoryException, BridgeDBException{
        Resource primaryId = getPossibleSingletonResource(repositoryConnection, dataSourceId, primary);
        Resource sharedId = getPossibleSingletonResource(repositoryConnection, dataSourceId, shared);
        if (primaryId == null){
            if (sharedId == null){
                if (oldString == null){
                    //nothing found
                    return null;
                } else {
                    UriPattern result = UriPattern.byPattern(oldString);
                    result.setDataSource(parent);
                    return result;
                }
            } else {
                if (oldString != null){
                    throw new BridgeDBException(parent.getResourceId() + " can not have both a " 
                        + old + " and a " + shared + " predicate.");
                }
                UriPattern result = readUriPattern(repositoryConnection, sharedId);
                result.setDataSource(parent);
                return result;
            } 
        } else { 
            if (oldString != null){
                throw new BridgeDBException(parent.getResourceId() + " can not have both a " 
                    + old + " and a " + primary + " predicate.");
            }
            if (sharedId != null){
                throw new BridgeDBException(parent.getResourceId() + " can not have both a " 
                        + primary + " and a " + shared + " predicate.");
            }
            UriPattern result = readUriPattern(repositoryConnection, primaryId);
            result.setPrimaryDataSource(parent);
            return result;
        }        
    }
    
    public static Set<UriPattern> readUriPatterns(RepositoryConnection repositoryConnection, Resource dataSourceId, 
            DataSourceUris parent, URI predicate) throws RepositoryException, BridgeDBException{
        Set<Resource> resources = getAllResources(repositoryConnection,  dataSourceId, predicate);
        HashSet<UriPattern> results = new HashSet<UriPattern>();
        for (Resource resource:resources){
            UriPattern uriPattern = readUriPattern(repositoryConnection, resource);
            uriPattern.setDataSource(parent);
            results.add(uriPattern);
        }
        return results;
    }
    
    public static UriPattern readUriPattern(RepositoryConnection repositoryConnection, Resource uriPatternId) 
            throws BridgeDBException, RepositoryException{
        checkStatements(repositoryConnection, uriPatternId);
        UriPattern pattern;      
        String uriSpace = getPossibleSingletonString(repositoryConnection, uriPatternId, VoidConstants.URI_SPACE_URI);
        if (uriSpace == null){
            pattern = byPattern(uriPatternId.stringValue());
        } else {
            String postfix = getPossibleSingletonString(repositoryConnection, uriPatternId, BridgeDBConstants.POSTFIX_URI);
            if (postfix == null){
                pattern = UriPattern.byNameSpace(uriSpace);
            } else {
                pattern = UriPattern.byNameSpaceAndPostFix(uriSpace, postfix);
            }
        }
        //Constructor registers with standard recource this register with used resource
        register.put((URI)uriPatternId, pattern);
/*        Resource dataSourceID = getPossibleSingletonResource(repositoryConnection, uriPatternId, BridgeDBConstants.HAS_DATA_SOURCE);
        System.out.println(dataSourceID);
        if (dataSourceID != null){
            DataSourceUris dsu = DataSourceUris.readDataSourceUris(repositoryConnection, dataSourceID);
            pattern.setParentDataSource(dsu);
        }
        Set<Resource> resources = getAllResources(repositoryConnection, uriPatternId, BridgeDBConstants.IS_URI_PATTERN_OF);
        for (Resource dataSource:resources){
            System.out.println(dataSourceID);
            DataSourceUris dsu = DataSourceUris.readDataSourceUris(repositoryConnection, dataSource);
            pattern.setNonParentDataSource(dsu);           
        }
*/        return pattern;
    }

    private final static void checkStatements(RepositoryConnection repositoryConnection, Resource dataSourceId) 
            throws BridgeDBException, RepositoryException{
        RepositoryResult<Statement> statements = 
                repositoryConnection.getStatements(dataSourceId, null, null, true);
        while (statements.hasNext()) {
            Statement statement = statements.next();
            try{
                if (!expectedPredicates.contains(statement.getPredicate())){
                    System.err.println("unexpected predicate in statement " + statement);
                }
            } catch (Exception e){
                throw new BridgeDBException ("Error processing statement " + statement, e);
            }
        }
    }
    
    @Override
    public String toString(){
        return getUriPattern();      
    }

    public String getUriSpace() throws BridgeDBException {
        if (hasPostfix()){
            throw new BridgeDBException("UriPattern " + this + " has a postfix");
        }
        return nameSpace;
    }

    public static void checkAllDataSourceUris() throws BridgeDBException{
        for (UriPattern uriPattern:register.values()){
            uriPattern.checkDataSourceUris();
        }        
    }
    
    private void checkDataSourceUris() throws BridgeDBException{
        if (dataSourceUris != null){
            return;  //DataSource has been Set
        }
        if (isUriPatternOf.size() <= 1){
            return;  //Singleton can be used and empty is fine too
        }
        DataSourceUris parent = null;
        for (DataSourceUris dsu:isUriPatternOf){
            if (dsu.getUriParent() != null){
                if (parent == null){
                    parent = dsu.getUriParent();
                } else {
                    System.out.println(this + " has two primary DataSources " + parent + " and " + dsu 
                            + " please delcare one formally");
                }
            }
        }
        if (parent != null){
            dataSourceUris = parent;
        } else {
            String uriPattern = getUriPattern();        
            DataSource dataSource = DataSource.register(uriPattern, uriPattern).urlPattern(uriPattern).asDataSource();
            dataSourceUris = DataSourceUris.byDataSource(dataSource);
        }
        for (DataSourceUris dsu:isUriPatternOf){
            dsu.setUriParent(dataSourceUris); 
        }
    }

    @Override
    public int compareTo(UriPattern other) {
        String thisString = this.getResourceId().stringValue().toLowerCase();
        thisString = thisString.replaceFirst("https://","http://");
        String otherString = other.getResourceId().stringValue().toLowerCase();
        otherString = otherString.replaceFirst("https://","http://");
        return thisString.compareTo(otherString);
     }

    public String getUri(String id) {
        return nameSpace + id + postfix;
    }
    
 }
