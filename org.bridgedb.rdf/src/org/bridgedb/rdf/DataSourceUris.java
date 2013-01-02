/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
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
public class DataSourceUris extends RdfBase {

    private final DataSource inner;
    private DataSource uriParent = null;
    private UriPattern sourceRdfPattern;
    private UriPattern bio2RdfPattern;
    
    private static final HashMap<DataSource, DataSourceUris> byDataSource = new HashMap<DataSource, DataSourceUris>();
    private static final HashMap<Resource, DataSourceUris> register = new HashMap<Resource, DataSourceUris>();
    private static HashSet<URI> expectedPredicates = new HashSet<URI>(Arrays.asList(new URI[] {
        BridgeDBConstants.FULL_NAME_URI,
        RdfConstants.TYPE_URI,
        BridgeDBConstants.ALTERNATIVE_FULL_NAME_URI,
        BridgeDBConstants.BIO2RDF_PATTERN_URI,
        BridgeDBConstants.FULL_NAME_URI,
        BridgeDBConstants.ID_EXAMPLE_URI,
        BridgeDBConstants.IDENTIFIERS_ORG_BASE_URI,
        BridgeDBConstants.IDENTIFERS_ORG_PATTERN_URI,
        BridgeDBConstants.MAIN_URL_URI,
        BridgeDBConstants.ORGANISM_URI,
        BridgeDBConstants.PRIMAY_URI,
        BridgeDBConstants.SOURCE_RDF_PATTERN_URI,
        BridgeDBConstants.SYSTEM_CODE_URI,
        BridgeDBConstants.TYPE_URI,
        BridgeDBConstants.URL_PATTERN_URI,
        BridgeDBConstants.URN_BASE_URI,
        BridgeDBConstants.WIKIPATHWAYS_BASE_URI, //Being ignored as wrong in only file used.
        BridgeDBConstants.WIKIPATHWAYS_PATTERN_URI,
    }));
      
    public static URI getResourceId(DataSource dataSource) {
        return new URIImpl(BridgeDBConstants.DATA_SOURCE1 + "_" + scrub(dataSource.getFullName()));
    }
    
    public static void writeAll(RepositoryConnection repositoryConnection) 
            throws IOException, RepositoryException, BridgeDBException {
        for (DataSource dataSource:DataSource.getDataSources()){
            DataSourceUris dsu = byDataSource(dataSource);
            dsu.writeDataSource(repositoryConnection); 
            dsu.writeUriParent(repositoryConnection);
            dsu.writeUriPatterns(repositoryConnection); 
        }
    }

    public void writeDataSource(RepositoryConnection repositoryConnection) throws IOException, RepositoryException {
        URI id = getResourceId(inner);
        repositoryConnection.add(id, RdfConstants.TYPE_URI, BridgeDBConstants.DATA_SOURCE_URI);         
        repositoryConnection.add(id, BridgeDBConstants.FULL_NAME_URI, new LiteralImpl(inner.getFullName()));

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

        String urlPattern = inner.getUrl("$id");
        if (urlPattern.length() > 3){
            repositoryConnection.add(id, BridgeDBConstants.URL_PATTERN_URI, new LiteralImpl(urlPattern));
        }

        String identifersOrgPattern = inner.getIdentifiersOrgUri("$id");
        if (identifersOrgPattern == null){
            String urnPattern = inner.getURN("");
            if (urnPattern.length() > 1){
                Value urnBase = new LiteralImpl(urnPattern.substring(0, urnPattern.length()-1));
                repositoryConnection.add(id, BridgeDBConstants.URN_BASE_URI, urnBase);
            }
        } else {
            repositoryConnection.add(id, BridgeDBConstants.IDENTIFERS_ORG_PATTERN_URI, new LiteralImpl(identifersOrgPattern));            
        }

        if (inner.getOrganism() != null){
            Organism organism = (Organism)inner.getOrganism();
            repositoryConnection.add(id, BridgeDBConstants.ORGANISM_URI, OrganismRdf.getResourceId(organism));
        }
    }

    private void writeUriParent(RepositoryConnection repositoryConnection) throws RepositoryException {
        if (uriParent != null){ 
            URI id = getResourceId(inner);
            URI parentId = getResourceId(uriParent);
            repositoryConnection.add(id, BridgeDBConstants.HAS_URI_PARENT, parentId);
        }
    }

    private void writeUriPatterns(RepositoryConnection repositoryConnection) throws RepositoryException {
        URI id = getResourceId(inner);
        if (bio2RdfPattern != null){
            repositoryConnection.add(id, BridgeDBConstants.BIO2RDF_PATTERN_URI, bio2RdfPattern.getResourceId());        
        }
        if (sourceRdfPattern != null){
            repositoryConnection.add(id, BridgeDBConstants.SOURCE_RDF_PATTERN_URI, sourceRdfPattern.getResourceId());                        
            System.out.println("£" + sourceRdfPattern);
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
        DataSourceUris dataSourceUris = register.get(dataSourceId);
        if (dataSourceUris != null){
            return dataSourceUris;
        }
        DataSource dataSource = readDataSource(repositoryConnection, dataSourceId);
        dataSourceUris = DataSourceUris.byDataSource(dataSource);
        dataSourceUris.readUriParent(repositoryConnection, dataSourceId);
        dataSourceUris.readUriPatternsStatements(repositoryConnection, dataSourceId);
        register.put(dataSourceId, dataSourceUris);
        return dataSourceUris;
     }    
        
    public static DataSource readDataSource(RepositoryConnection repositoryConnection, Resource dataSourceId) 
            throws BridgeDBException, RepositoryException{
        String fullName = getSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.FULL_NAME_URI);
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
            builder.type(primary);
        }

        String type = getPossibleSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.TYPE_URI);
        if (type != null){
            builder.type(type);
        }

        String urlPattern = getPossibleSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.URL_PATTERN_URI);
        if (urlPattern != null){
            builder.urlPattern(urlPattern);
        }
        
        String urnBase = getPossibleSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.URN_BASE_URI);
        if (urnBase != null){
            builder.urnBase(urnBase);
        }
        
        return builder.asDataSource();
    }
    
    private void readUriParent(RepositoryConnection repositoryConnection, Resource dataSourceId) throws RepositoryException, BridgeDBException {
        Value parentId = getPossibleSingleton(repositoryConnection, dataSourceId, BridgeDBConstants.HAS_URI_PARENT);
        if (parentId != null){
            DataSourceUris dataSourceUris = register.get(parentId.stringValue());
            DataSource parent;
            if (dataSourceUris != null){
                parent = dataSourceUris.inner;
            } else {
                //Read only the DataSource part for now to avoid a loop with bad chained parent
                parent = readDataSource(repositoryConnection, (Resource)parentId);
            }
            setUriParent(parent);
        }
    }

    private void readUriPatternsStatements(RepositoryConnection repositoryConnection, Resource dataSourceId) 
            throws BridgeDBException, RepositoryException{
        String identifiersOrgPattern = 
                getPossibleSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.IDENTIFERS_ORG_PATTERN_URI);
        setIdentifiersOrgPattern(identifiersOrgPattern);
        String identifiersOrgBase = 
                getPossibleSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.IDENTIFIERS_ORG_BASE_URI);
        setIdentifiersOrgBase(identifiersOrgBase);
        String bio2Pattern = 
                getPossibleSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.BIO2RDF_PATTERN_URI);
        bio2RdfPattern = addPattern(bio2Pattern);
        String sourcePattern = 
                getPossibleSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.SOURCE_RDF_PATTERN_URI);
        sourceRdfPattern = addPattern(sourcePattern);        
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
        DataSourceUris result = byDataSource.get(dataSource);
        if (result == null){
            result = new DataSourceUris(dataSource);
        }
        return result;
    }
    
    public void setUriParent(DataSource parent) throws BridgeDBException{
        if (parent.equals(uriParent)){
            return;  //already set. Also checks that replacedBy is not null
        }
        if (uriParent != null) {
            throw new BridgeDBException("Illegal attempt to set UriParent of  " + inner + " with "  + parent
                    + ". Uri Parent was previously set to " + uriParent);             
        }
        if (inner.equals(parent)){
            throw new BridgeDBException("Illegal attempt to set UriParent of  " + inner + " with itself ");
        }
        DataSourceUris parentPlus = byDataSource(parent);
        if (parentPlus.uriParent != null){
            throw new BridgeDBException("Illegal attempt to set UriParent of  " + inner + " with "  + parent
                    + ". As parent has a UriParent of " + parentPlus.uriParent + " set.");             
        }
        for (DataSourceUris plus: byDataSource.values()){
            if (plus.uriParent != null){
                if (plus.uriParent.equals(inner)){
                    throw new BridgeDBException("Illegal attempt to set UriParent of  " + inner + " with "  + parent
                        + ". As " + inner + " is itself a UriParent of " + plus.inner);             
                }
            }
        }
        uriParent = parent;
    }

    public void setIdentifiersOrgBase(String identifiersOrgBase) throws BridgeDBException {
        if (identifiersOrgBase != null){
            try {
                inner.setIdentifiersOrgUriBase(identifiersOrgBase);
            } catch (IDMapperException ex) {
                throw new BridgeDBException("Unable to set Identifiers Org Base to " + identifiersOrgBase, ex);
            }
            System.err.println("depricated " + BridgeDBConstants.IDENTIFIERS_ORG_BASE_URI + " used");
            UriPattern pattern = UriPattern.byUrlPattern(inner.getIdentifiersOrgUri("$id"));
            pattern.setDataSource(inner);
        }
    }
    
    public void setIdentifiersOrgPattern(String identifiersOrgPattern) throws BridgeDBException {
        if (identifiersOrgPattern != null){
            if (identifiersOrgPattern.endsWith("$id")){
                try {
                    String identifiersOrgBase = identifiersOrgPattern.substring(0, identifiersOrgPattern.length()-3);
                    inner.setIdentifiersOrgUriBase(identifiersOrgBase);
                } catch (IDMapperException ex) {
                    throw new BridgeDBException("Unable to set Identifiers Org pattern to " + identifiersOrgPattern, ex);
                }
                UriPattern pattern = UriPattern.byUrlPattern(identifiersOrgPattern);
                pattern.setDataSource(inner);          
            } else {
                throw new BridgeDBException("Identifersorg Pattern must end with $id");
            }
        }
    }

    private void loadDataSourceUriPatterns() throws BridgeDBException {
        String pattern = inner.getUrl("$id");
        addPattern(pattern);
        pattern = inner.getIdentifiersOrgUri("$id");
        addPattern(pattern);        
   }

    private UriPattern addPattern(String pattern) throws BridgeDBException {
        if (pattern == null || pattern.isEmpty() || pattern.equals("$id") || pattern.equals("null")){
            return null;
        }
        UriPattern uriPattern =  UriPattern.byUrlPattern(pattern);
        uriPattern.setDataSource(inner);
        return uriPattern;
    }

}
