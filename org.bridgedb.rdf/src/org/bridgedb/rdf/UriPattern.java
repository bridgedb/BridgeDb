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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import org.bridgedb.DataSource;
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.rdf.constants.RdfConstants;
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

    private final String prefix;
    private final String postfix;
    private DataSourceUris dataSourceUris;
    private TreeSet<DataSourceUris> isUriPatternOf = new TreeSet<DataSourceUris>();
    
    private static HashMap<Resource, UriPattern> register = new HashMap<Resource, UriPattern>();
    private static HashMap<String,UriPattern> byPrefixOrNameSpaceOnly = new HashMap<String,UriPattern>();
    private static HashMap<String,HashMap<String,UriPattern>> byPrefixAndPostFix = 
            new HashMap<String,HashMap<String,UriPattern>> ();  
    
    private static HashSet<URI> expectedPredicates = new HashSet<URI>(Arrays.asList(new URI[] {
        BridgeDBConstants.HAS_POSTFIX_URI,
        BridgeDBConstants.HAS_PREFIX_URI,
        RdfConstants.TYPE_URI,
        BridgeDBConstants.HAS_DATA_SOURCE,
        BridgeDBConstants.IS_URI_PATTERN_OF
    }));
            
    private UriPattern(String start){
        prefix = start;
        this.postfix = "";
        registerByPrefixAndNameSpace();
        register.put(getResourceId(), this);
    } 
    
    private UriPattern(String prefix, String postfix){
        this.prefix= prefix;
        if (postfix == null || postfix.isEmpty()){
            this.postfix = "";
            registerByPrefixAndNameSpace();
        } else {
            this.postfix = postfix;
            HashMap<String,UriPattern> postFixMap = byPrefixAndPostFix.get(prefix);
            if (postFixMap == null){
                postFixMap = new HashMap<String,UriPattern>();
            }
            postFixMap.put(postfix, this);
            byPrefixAndPostFix.put(prefix, postFixMap);
        }
        register.put(getResourceId(), this);
    }
  
    public String getPrefix(){
        return prefix;
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

    /**
     * 
     * @param start
     * @return UriPattern or null if more than one UriPattern starts this way,
     */
    public static UriPattern byPrefixOrNameSpace(String start){
        if (byPrefixOrNameSpaceOnly.containsKey(start)) {
            return byPrefixOrNameSpaceOnly.get(start);
        } else {
            return new UriPattern(start);            
        }
   }
    
    private static UriPattern byPrefixAndPostfix(String nameSpace, String postfix) {
        HashMap<String,UriPattern> postFixMap = byPrefixAndPostFix.get(nameSpace);
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
        return byPrefixAndPostFix(nameSpace, postfix);
    }

    public static UriPattern existingByPattern(String uriPattern) {
        if (uriPattern == null || uriPattern.isEmpty()){
            return null;
        }
        String prefixOrNameSpace;
        String postfix;
        String cleanPattern = uriPattern.trim();
        if (cleanPattern.startsWith("<") && cleanPattern.endsWith(">")){
            cleanPattern = cleanPattern.substring(1, cleanPattern.length()-1);
        }
        int idPos = cleanPattern.indexOf("$id");
        if (idPos == -1) {
            prefixOrNameSpace = cleanPattern;
            postfix = "";
        } else {
            prefixOrNameSpace = cleanPattern.substring(0, idPos);
            postfix = cleanPattern.substring(idPos + 3);
        } 
        if (postfix.isEmpty()){
            return byPrefixOrNameSpaceOnly.get(prefixOrNameSpace);
        } else {
            HashMap<String,UriPattern> postFixMap = byPrefixAndPostFix.get(prefixOrNameSpace);
            if (postFixMap == null){
                return null;
            }
            return postFixMap.get(postfix);
        }
    }

    /*public static UriPattern existingByNameSpaceandPrefix(String nameSpace, String postfix) {
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
    }*/

   //public static UriPattern existingByNameSpace(String nameSpace) {
   //     return byNameSpaceOnly.get(nameSpace);
   // }
   
    private static String getNameSpace(String uri){
        String nameSpace = null;
        uri = uri.trim();
        if (uri.contains("#")){
            nameSpace = uri.substring(0, uri.lastIndexOf("#")+1);
        } else if (uri.contains("=")){
            nameSpace = uri.substring(0, uri.lastIndexOf("=")+1);
        } else if (uri.contains("/")){
            nameSpace = uri.substring(0, uri.lastIndexOf("/")+1);
        } else if (uri.contains(":")){
            nameSpace = uri.substring(0, uri.lastIndexOf(":")+1);
        }
        //ystem.out.println(lookupPrefix);
        if (nameSpace == null){
            //Opps not a uri as they all start with http:// similar
            throw new IllegalArgumentException("Uri should have a '#', '=', /, or a ':' in it.");
        }
        if (nameSpace.isEmpty()){
            throw new IllegalArgumentException("Uri should not start with the only '#', '=', /, or ':'.");            
        }
        return nameSpace;
    }
    
   /** 
    * Returns the UriPattern known for this URI if it can be found using the simple Split
    * It looks for a "#" then an "=" then a "/" and finally a ":"
    * Once it finds one it then looks for the last instance of that one.
    *     So if there is for example an ab#12=45 it will split after the # 
    * It then attempts to find a uri pattern based on that prefix.
    * 
    * If no pattern is known it will return null.
    * 
    * UriPatterns which included postfixes will typically not be found this way.
    * Also null may be returned because two or more known UriPatterns have are known to start in the same way
    * 
    */ 
    public final static UriPattern existingByUri(String uri){
        String nameSpace = getNameSpace(uri);
        return byPrefixOrNameSpaceOnly.get(nameSpace);
    }


    private static UriPattern byPrefixAndPostFix(String nameSpace, String postfix) throws BridgeDBException{
        if (postfix.isEmpty() || postfix.equals("NULL")){
            return byPrefixOrNameSpace(nameSpace);
        } else {
            return byPrefixAndPostfix(nameSpace, postfix);
        }
    }
    
    public void setPrimaryDataSource(DataSourceUris dsu) throws BridgeDBException{
        if (dsu == null){
            return;
        }
        if (dataSourceUris ==  null) {       
            dataSourceUris = dsu;
            isUriPatternOf.remove(dsu);
        } else if (dataSourceUris.equals(dsu)){
            //Do nothing;
        } else {
            throw new BridgeDBException("Illegal attempt to set Primary DataSource of " + this + " to " + dsu 
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
            return prefix + "$id";
        } else {
            return prefix + "$id" + postfix;
        }
    }

    public static void addAll(RepositoryConnection repositoryConnection) 
            throws IOException, RepositoryException, BridgeDBException {
        TreeSet<UriPattern> all = new TreeSet(register.values());
        for (UriPattern uriPattern:all){
            uriPattern.add(repositoryConnection);
        }        
    }
    
    public void add(RepositoryConnection repositoryConnection) throws RepositoryException{
        URI id = getResourceId();
        repositoryConnection.add(id, RdfConstants.TYPE_URI, BridgeDBConstants.URI_PATTERN_URI);
        repositoryConnection.add(id, BridgeDBConstants.HAS_PREFIX_URI,  new LiteralImpl(prefix));
        if (!postfix.isEmpty()){
            repositoryConnection.add(id, BridgeDBConstants.HAS_POSTFIX_URI,  new LiteralImpl(postfix));
        }
        if (dataSourceUris != null){
            repositoryConnection.add(id, BridgeDBConstants.HAS_DATA_SOURCE,  dataSourceUris.getResourceId());            
        }
        for (DataSourceUris dsu:isUriPatternOf){
                repositoryConnection.add(id, BridgeDBConstants.IS_URI_PATTERN_OF,  dsu.getResourceId()); 
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
            URI predicate) throws RepositoryException, BridgeDBException{
        Resource id = getPossibleSingletonResource(repositoryConnection, dataSourceId, predicate);
        if (id != null){
            UriPattern result = readUriPattern(repositoryConnection, id);
            result.setPrimaryDataSource(parent);
            return result;
        }
        return null;
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
        String prefix = getPossibleSingletonString(repositoryConnection, uriPatternId, BridgeDBConstants.HAS_PREFIX_URI);
        if (prefix == null){
            pattern = byPattern(uriPatternId.stringValue());
        } else {
            String postfix = getPossibleSingletonString(repositoryConnection, uriPatternId, BridgeDBConstants.HAS_POSTFIX_URI);
            if (postfix == null){
                pattern = UriPattern.byPrefixOrNameSpace(prefix);
            } else {
                pattern = UriPattern.byPrefixAndPostFix(prefix, postfix);
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
        return prefix + id + postfix;
    }

    public String getIdFromUri(String uri) throws BridgeDBException {
        if (!uri.startsWith(prefix)){
            throw new BridgeDBException("Uri " + uri + " does not match UriPattern " + this);
        }
        if (!uri.endsWith(postfix)){
            throw new BridgeDBException("Uri " + uri + " does not match UriPattern " + this);
        }
        return uri.substring(prefix.length(), uri.length() - postfix.length());
    }

    private void registerByPrefixAndNameSpace() {
        registerByPrefixOrNameSpace(prefix);
        String nameSpace = getNameSpace(prefix);
        if (!nameSpace.equals(prefix)){
            registerByPrefixOrNameSpace(nameSpace);
        }
    }
    
    private void registerByPrefixOrNameSpace(String start) {
        if (byPrefixOrNameSpaceOnly.containsKey(start)){
            //Multiple UriPatterns with the same nameSpace and or prefix
            //for example http://bio2rdf.org/biocyc:$id and http://bio2rdf.org/omim:$id
           byPrefixOrNameSpaceOnly.put(start, null);
        } else {
           byPrefixOrNameSpaceOnly.put(start, this);
        }
    }
  

 }
