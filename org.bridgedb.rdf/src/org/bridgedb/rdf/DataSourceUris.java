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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.bio.Organism;
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.rdf.constants.RdfConstants;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.BooleanLiteralImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;


/**
 *
 * @author Christian
 */
public class DataSourceUris extends RdfBase implements Comparable<DataSourceUris>, Comparator<DataSource>{

    private final DataSource inner;
    private UriPattern sourceRdfPattern;
    private UriPattern bio2RdfPattern;
    private UriPattern wikiPathwaysPattern;
    private DataSourceUris uriParent = null;
    private final Set<UriPattern> otherUriPatterns = new HashSet<UriPattern>();
    
    private static final HashMap<DataSource, DataSourceUris> byDataSource = new HashMap<DataSource, DataSourceUris>();
    private static final HashMap<Resource, DataSourceUris> register = new HashMap<Resource, DataSourceUris>();
    private static HashSet<URI> expectedPredicates = new HashSet<URI>(Arrays.asList(new URI[] {
        BridgeDBConstants.FULL_NAME_URI,
        RdfConstants.TYPE_URI,
        BridgeDBConstants.ALTERNATIVE_FULL_NAME_URI,
        BridgeDBConstants.HAS_PRIMARY_BIO2RDF_PATTERN_URI,
        BridgeDBConstants.HAS_BIO2RDF_PATTERN_URI,
        BridgeDBConstants.BIO2RDF_PATTERN_URI, //old version
        BridgeDBConstants.FULL_NAME_URI,
        BridgeDBConstants.HAS_URI_PARENT_URI,
        BridgeDBConstants.ID_EXAMPLE_URI,
        BridgeDBConstants.IDENTIFERS_ORG_BASE,
        BridgeDBConstants.HAS_IDENTIFERS_ORG_PATTERN_URI,
        BridgeDBConstants.HAS_PRIMARY_IDENTIFERS_ORG_PATTERN_URI,
        BridgeDBConstants.MAIN_URL_URI,
        BridgeDBConstants.ORGANISM_URI,
        BridgeDBConstants.PRIMAY_URI,
        BridgeDBConstants.HAS_PRIMARY_SOURCE_RDF_PATTERN_URI,
        BridgeDBConstants.HAS_SOURCE_RDF_PATTERN_URI,
        BridgeDBConstants.SOURCE_RDF_PATTERN_URI, //Old version
        BridgeDBConstants.SYSTEM_CODE_URI,
        BridgeDBConstants.TYPE_URI,
        BridgeDBConstants.HAS_URI_PATTERN_URI,
        BridgeDBConstants.HAS_PRIMARY_URI_PATTERN_URI,
        //No need for old version
        BridgeDBConstants.HAS_URL_PATTERN_URI,
        BridgeDBConstants.HAS_PRIMARY_URL_PATTERN_URI,
        BridgeDBConstants.URL_PATTERN_URI, //Old version
        BridgeDBConstants.URN_BASE_URI,
        BridgeDBConstants.HAS_PRIMARY_WIKIPATHWAYS_PATTERN_URI,
        BridgeDBConstants.HAS_WIKIPATHWAYS_PATTERN_URI,
        new URIImpl(BridgeDBConstants.PREFIX + "wikipathways_id_base")
    }));
    private static final boolean NOT_SHARED = false;
    private static final boolean SHARED = true;
      
    public static URI getResourceId(DataSource dataSource) {
        if (dataSource.getFullName() == null){
            return new URIImpl(BridgeDBConstants.DATA_SOURCE1 + "_bysysCode_" + scrub(dataSource.getSystemCode()));
        } else {
            return new URIImpl(BridgeDBConstants.DATA_SOURCE1 + "_" + scrub(dataSource.getFullName()));
        }
    }
    
    Resource getResourceId() {
        return getResourceId(inner);
    }

    public static void writeAll(RepositoryConnection repositoryConnection, boolean addPrimaries) 
            throws IOException, RepositoryException, BridgeDBException {
        writeAll(repositoryConnection, DataSource.getDataSources(), addPrimaries);
    }
    
    public static void writeAll(RepositoryConnection repositoryConnection, Collection<DataSource> dataSources, 
            boolean addPrimaries) throws IOException, RepositoryException, BridgeDBException {
        TreeSet<DataSourceUris> dsus = new TreeSet<DataSourceUris>(); 
        for (DataSource dataSource:dataSources){
            if (dataSource !=null){
                DataSourceUris dsu = byDataSource(dataSource);
                dsus.add(dsu);
            }
        }
        for (DataSourceUris dsu:dsus){
            dsu.writeDataSource(repositoryConnection, addPrimaries); 
            dsu.writeUriPatterns(repositoryConnection, addPrimaries); 
            dsu.writeUriParent(repositoryConnection, addPrimaries);
        }
    }

    public void writeDataSource(RepositoryConnection repositoryConnection, boolean addPrimaries) throws IOException, RepositoryException, BridgeDBException {
        Resource id = getResourceId();
        repositoryConnection.add(id, RdfConstants.TYPE_URI, BridgeDBConstants.DATA_SOURCE_URI);         
        
        if (inner.getFullName() != null){
            repositoryConnection.add(id, BridgeDBConstants.FULL_NAME_URI, new LiteralImpl(inner.getFullName()));
        }

        if (inner.getSystemCode() != null && (!inner.getSystemCode().trim().isEmpty())){
            repositoryConnection.add(id, BridgeDBConstants.SYSTEM_CODE_URI, new LiteralImpl(inner.getSystemCode()));
        }

        for (String alternativeFullName:inner.getAlternativeFullNames()){
            repositoryConnection.add(id, BridgeDBConstants.ALTERNATIVE_FULL_NAME_URI, new LiteralImpl(alternativeFullName));            
        }
        
        if (inner.getMainUrl() != null){
            repositoryConnection.add(id, BridgeDBConstants.MAIN_URL_URI, new LiteralImpl(inner.getMainUrl()));
        }

        if (inner.getExample() != null && inner.getExample().getId() != null){
            repositoryConnection.add(id, BridgeDBConstants.ID_EXAMPLE_URI, new LiteralImpl(inner.getExample().getId()));
        }
 
        if (inner.isPrimary()){
            repositoryConnection.add(id, BridgeDBConstants.PRIMAY_URI, BooleanLiteralImpl.TRUE);
        } else {
            repositoryConnection.add(id, BridgeDBConstants.PRIMAY_URI, BooleanLiteralImpl.FALSE);
        }
 
        if (inner.getType() != null){
            repositoryConnection.add(id, BridgeDBConstants.TYPE_URI, new LiteralImpl(inner.getType()));
        } 

        writeUriPattern(repositoryConnection, BridgeDBConstants.HAS_PRIMARY_URL_PATTERN_URI, 
                BridgeDBConstants.HAS_URL_PATTERN_URI, getDataSourceUrl(), addPrimaries);

        String identifersOrgPattern = inner.getIdentifiersOrgUri("$id");
        if (identifersOrgPattern == null){
            String urnPattern = inner.getURN("");
            if (urnPattern.length() > 1){
                Value urnBase = new LiteralImpl(urnPattern.substring(0, urnPattern.length()-1));
                repositoryConnection.add(id, BridgeDBConstants.URN_BASE_URI, urnBase);
            }
        } else {            
            UriPattern identifersOrgUriPattern = UriPattern.byPattern(identifersOrgPattern);
            writeUriPattern(repositoryConnection, BridgeDBConstants.HAS_PRIMARY_IDENTIFERS_ORG_PATTERN_URI, 
                BridgeDBConstants.HAS_IDENTIFERS_ORG_PATTERN_URI, UriPattern.byPattern(identifersOrgPattern), addPrimaries);
        }

        if (inner.getOrganism() != null){
            Organism organism = (Organism)inner.getOrganism();
            repositoryConnection.add(id, BridgeDBConstants.ORGANISM_URI, OrganismRdf.getResourceId(organism));
        }
    }

    private void writeUriPatterns(RepositoryConnection repositoryConnection, boolean addPrimaries) throws RepositoryException, BridgeDBException {
        writeUriPattern(repositoryConnection, BridgeDBConstants.HAS_PRIMARY_BIO2RDF_PATTERN_URI, 
                BridgeDBConstants.HAS_BIO2RDF_PATTERN_URI, bio2RdfPattern, addPrimaries);
        writeUriPattern(repositoryConnection, BridgeDBConstants.HAS_PRIMARY_SOURCE_RDF_PATTERN_URI, 
                BridgeDBConstants.HAS_SOURCE_RDF_PATTERN_URI, sourceRdfPattern, addPrimaries);
        writeUriPattern(repositoryConnection, BridgeDBConstants.HAS_PRIMARY_WIKIPATHWAYS_PATTERN_URI, 
                BridgeDBConstants.HAS_WIKIPATHWAYS_PATTERN_URI, getWikiPathwaysPattern(), addPrimaries);
        for (UriPattern pattern:this.otherUriPatterns){
            writeUriPattern(repositoryConnection, BridgeDBConstants.HAS_PRIMARY_URI_PATTERN_URI,  
                BridgeDBConstants.HAS_URI_PATTERN_URI, pattern, addPrimaries);
        }
    }

    private void writeUriPattern(RepositoryConnection repositoryConnection, URI primary, URI shared, 
            UriPattern pattern, boolean addPrimaries) throws RepositoryException {
        if (pattern != null){
            if (inner.equals(pattern.getDataSource())){
                repositoryConnection.add(getResourceId(), primary, pattern.getResourceId());
            } else if (addPrimaries && this.equals(pattern.getMainDataSourceUris())){
                repositoryConnection.add(getResourceId(), primary, pattern.getResourceId());
            } else {
                repositoryConnection.add(getResourceId(), shared, pattern.getResourceId());                
            }
        }
    }
    
    private void writeUriParent(RepositoryConnection repositoryConnection, boolean addPrimaries) throws RepositoryException {
        DataSourceUris parent;
        if (addPrimaries){
            parent = getUriParent();
        } else {
            parent = uriParent;
        }  
        if (parent != null){
            repositoryConnection.add(getResourceId(),BridgeDBConstants.HAS_URI_PARENT_URI, parent.getResourceId());
        }
    }
    
    public static void readAllDataSourceUris(RepositoryConnection repositoryConnection) throws BridgeDBException, RepositoryException{
        RepositoryResult<Statement> statements = 
                repositoryConnection.getStatements(null, RdfConstants.TYPE_URI, BridgeDBConstants.DATA_SOURCE_URI, true);
                //repositoryConnection.getStatements(null, null, null, true);
        while (statements.hasNext()) {
            Statement statement = statements.next();
            DataSourceUris dataSourceUris = readDataSourceUris(repositoryConnection, statement.getSubject());
        }
    }

    public static DataSourceUris readDataSourceUris(RepositoryConnection repositoryConnection, Resource dataSourceId) 
            throws BridgeDBException, RepositoryException {
        checkStatements(repositoryConnection, dataSourceId);
        DataSource dataSource = readDataSource(repositoryConnection, dataSourceId);
        DataSourceUris dataSourceUris = register.get(dataSourceId);
        if (dataSourceUris != null){
            if (dataSourceUris.inner.equals(dataSource)){
                //Ok fine
            } else {
                throw new BridgeDBException("Resource " + dataSourceId + " allready mapped to " + dataSourceUris.inner +
                        " while new RDF maps it to " + dataSource);
            } 
        } else {
            dataSourceUris = DataSourceUris.byDataSource(dataSource);
            register.put(dataSourceId, dataSourceUris);
        }
        dataSourceUris.readUriPatternsStatements(repositoryConnection, dataSourceId);
        return dataSourceUris;
     }    
        
    public static DataSource readDataSource(RepositoryConnection repositoryConnection, Resource dataSourceId) 
            throws BridgeDBException, RepositoryException{
        String fullName = getPossibleSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.FULL_NAME_URI);
        String systemCode = getPossibleSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.SYSTEM_CODE_URI);
        DataSource.Builder builder = DataSource.register(systemCode, fullName);

        Set<String> alternativeNames = getAllStrings(repositoryConnection, dataSourceId, BridgeDBConstants.ALTERNATIVE_FULL_NAME_URI);
        for (String alternativeName:alternativeNames){
            builder.alternativeFullName(alternativeName);            
        }
 
        String idExample = getPossibleSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.ID_EXAMPLE_URI);
        if (idExample != null){
            builder.idExample(idExample);
        }
        
        String mainUrl = getPossibleSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.MAIN_URL_URI);
        if (mainUrl != null){
            builder.mainUrl(mainUrl);
        }
  
        Value organismId = getPossibleSingleton(repositoryConnection, dataSourceId, BridgeDBConstants.ORGANISM_URI);
        if (organismId != null){
            Object organism = OrganismRdf.byRdfResource(organismId);
            builder.organism(organism);
        }
            
        String primary = getPossibleSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.PRIMAY_URI);
        if (primary != null){
            builder.primary(Boolean.parseBoolean(primary));
        }

        String type = getPossibleSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.TYPE_URI);
        if (type != null){
            builder.type(type);
        }

        readUrlPattern(repositoryConnection, dataSourceId, builder);
        
        String urnBase = getPossibleSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.URN_BASE_URI);
        if (urnBase != null){
            builder.urnBase(urnBase);
        }
        
        return builder.asDataSource();
    }
    
    private static void readUrlPattern(RepositoryConnection repositoryConnection, Resource dataSourceId, 
            DataSource.Builder builder) throws BridgeDBException, RepositoryException{
        DataSourceUris dsu = byDataSource(builder.asDataSource());
        UriPattern uriPattern = UriPattern.readUriPattern(repositoryConnection, dataSourceId, dsu, 
                BridgeDBConstants.HAS_PRIMARY_URL_PATTERN_URI, BridgeDBConstants.HAS_URL_PATTERN_URI, 
                BridgeDBConstants.URL_PATTERN_URI);
        if (uriPattern != null){
            builder.urlPattern(uriPattern.getUriPattern());
        }       
    }
    
    private void readUriPatternsStatements(RepositoryConnection repositoryConnection, Resource dataSourceId) 
            throws BridgeDBException, RepositoryException{
        String identifiersOrgBase = getPossibleSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.IDENTIFERS_ORG_BASE);
        if (identifiersOrgBase != null){
            try {
                inner.setIdentifiersOrgUriBase(identifiersOrgBase);
            } catch (IDMapperException ex) {
                throw new BridgeDBException(ex);
            }
        }
        
        UriPattern identifiersOrgPattern = UriPattern.readUriPattern(repositoryConnection, dataSourceId, this, 
                BridgeDBConstants.HAS_PRIMARY_IDENTIFERS_ORG_PATTERN_URI, BridgeDBConstants.HAS_IDENTIFERS_ORG_PATTERN_URI);
        if (identifiersOrgPattern != null){
            try {
                inner.setIdentifiersOrgUriBase(identifiersOrgPattern.getUriSpace());
            } catch (IDMapperException ex) {
                throw new BridgeDBException(ex);
            }
        }
         
        bio2RdfPattern = UriPattern.readUriPattern(repositoryConnection, dataSourceId, this,
                BridgeDBConstants.HAS_PRIMARY_BIO2RDF_PATTERN_URI, BridgeDBConstants.HAS_BIO2RDF_PATTERN_URI, 
                BridgeDBConstants.BIO2RDF_PATTERN_URI);
        sourceRdfPattern = UriPattern.readUriPattern(repositoryConnection, dataSourceId, this, 
                BridgeDBConstants.HAS_PRIMARY_SOURCE_RDF_PATTERN_URI, BridgeDBConstants.HAS_SOURCE_RDF_PATTERN_URI, 
                BridgeDBConstants.SOURCE_RDF_PATTERN_URI); 
        wikiPathwaysPattern = UriPattern.readUriPattern(repositoryConnection, dataSourceId, this, 
                BridgeDBConstants.HAS_PRIMARY_WIKIPATHWAYS_PATTERN_URI, BridgeDBConstants.HAS_WIKIPATHWAYS_PATTERN_URI);  
        
        this.otherUriPatterns.addAll(UriPattern.readUriPatterns(repositoryConnection, dataSourceId, 
                BridgeDBConstants.HAS_PRIMARY_URI_PATTERN_URI));
        this.otherUriPatterns.addAll(UriPattern.readUriPatterns(repositoryConnection, dataSourceId, 
                BridgeDBConstants.HAS_URI_PATTERN_URI));
        
        //make sure there are no specific uris in other.
        otherUriPatterns.remove(bio2RdfPattern);
        otherUriPatterns.remove(sourceRdfPattern);
        otherUriPatterns.remove(wikiPathwaysPattern);
        String identifersOrgPattern = inner.getIdentifiersOrgUri("$id");
        if (identifersOrgPattern != null){
            UriPattern identifersOrgUriPattern = UriPattern.byPattern(identifersOrgPattern);
            otherUriPatterns.remove(identifersOrgUriPattern);    
        }
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
    
    private DataSourceUris(DataSource wraps) throws BridgeDBException{
        inner = wraps;
        byDataSource.put(inner, this);
        loadDataSourceUriPatterns();
    }
    
    public static DataSourceUris byDataSource(DataSource dataSource) throws BridgeDBException{
        if (dataSource == null){
            return null;
        }
        DataSourceUris result = byDataSource.get(dataSource);
        if (result == null){
            result = new DataSourceUris(dataSource);
        }
        return result;
    }
    
    private void loadDataSourceUriPatterns() throws BridgeDBException {
        String pattern = inner.getUrl("$id");
        addPattern(pattern);
        pattern = inner.getIdentifiersOrgUri("$id");
        addPattern(pattern);        
   }

   private UriPattern addPrimaryPattern(String pattern) throws BridgeDBException {
        if (pattern == null || pattern.isEmpty() || pattern.equals("$id") || pattern.equals("null")){
            return null;
        }
        UriPattern uriPattern =  UriPattern.byPattern(pattern);
        uriPattern.setPrimaryDataSource(this);
        return uriPattern;
    }

   private UriPattern addPattern(String pattern) throws BridgeDBException {
        if (pattern == null || pattern.isEmpty() || pattern.equals("$id") || pattern.equals("null")){
            return null;
        }
        UriPattern uriPattern =  UriPattern.byPattern(pattern);
        uriPattern.setDataSource(this);
        return uriPattern;
    }

     DataSource getDataSource() {
        return inner;
    }

    public Set<UriPattern> getUriPatterns() throws BridgeDBException{
        HashSet<UriPattern> results = new HashSet<UriPattern>();
        results.add(getDataSourceUrl());
        results.add(wikiPathwaysPattern);
        results.add(sourceRdfPattern);
        results.add(bio2RdfPattern);
        String identifersOrgPattern = inner.getIdentifiersOrgUri("$id");
        if (identifersOrgPattern != null){
            UriPattern identifersOrgUriPattern = UriPattern.byPattern(identifersOrgPattern);
            results.add(identifersOrgUriPattern);
        }
        results.addAll(otherUriPatterns);
        //Avoids having to check all of the above for null;
        results.remove(null);
        return results;
    }
    
    public UriPattern getWikiPathwaysPattern() throws BridgeDBException {
        //sourceRDFURI -> bio2RDF -> urlPattern
        if (wikiPathwaysPattern != null){
            return wikiPathwaysPattern;
        }
        if (sourceRdfPattern != null){
            return sourceRdfPattern;
        }
        if (bio2RdfPattern != null){
            return bio2RdfPattern;
        }
        return getDataSourceUrl();
    }

    private UriPattern getDataSourceUrl() throws BridgeDBException {
        String urlPattern = inner.getUrl("$id");
        if (urlPattern.length() > 3){
            return UriPattern.byPattern(urlPattern);
        }
        return null;
    }

    void setUriParent(DataSource parent) throws BridgeDBException {
        if (parent == null){
            throw new BridgeDBException ("Parent may not be null");
        }
        DataSourceUris parentDSU = byDataSource(parent);
        setUriParent(parentDSU);
    }

    public DataSourceUris getUriParent() {
        DataSourceUris parentDSU = uriParent;
        if (parentDSU == null){
            return null;
        }
        while (parentDSU.uriParent != null){
            parentDSU = parentDSU.uriParent;
        }
        return parentDSU;
    }

    void setUriParent(DataSourceUris parentDSU) throws BridgeDBException {
        if (parentDSU == null){
            throw new BridgeDBException ("Parent may not be null");
        }
        if (parentDSU.equals(this)){
            return; //Ignore attempt to set parent to self;
        }       
        if (parentDSU.equals(uriParent)){
            return; //already set
        }
        if (uriParent == null){
            DataSourceUris grandParent = parentDSU.uriParent;
            while (grandParent != null){
                if (grandParent.equals(this)){
                    throw new BridgeDBException("Illeagl attemp to create a circular reference");
                }
                grandParent = grandParent.uriParent;
            }
            uriParent = parentDSU;
        } else {
            throw new BridgeDBException ("Parent on " + this + " already set to " + uriParent 
                + " so can not set to " + uriParent);
        }
    }
    
    @Override
    public String toString(){
        return inner.toString();
    }

    @Override
    public int compareTo(DataSourceUris other) {
        return compare(inner, other.inner);
    }

    @Override
    public int compare(DataSource dataSource1, DataSource dataSource2) {
        int result = softCompare(dataSource1.getFullName(), dataSource2.getFullName());
        if (result != 0){
            return result;
        }
        result = softCompare(dataSource1.getSystemCode(), dataSource2.getSystemCode());
        if (result != 0){
            return result;
        }
        return dataSource1.hashCode() - dataSource2.hashCode();
    }

    private int softCompare(String value1, String value2) {
        if (value1 == null || value1.trim().isEmpty()){
           if (value2 == null || value2.trim().isEmpty()){
               return 0;
           } else {
               return -1;
           }
        } else {
           if (value2 == null || value2.trim().isEmpty()){
               return 1;
           } else {
               //Try Ignore case first
               int result = value1.toLowerCase().compareTo(value2.toLowerCase());
               if (result != 0){
                   return result;
               }
               //If that is tied try with case to keep Pubmed and PubMed order consistant.
               return value1.compareTo(value2);
           }
        }
    }
}
