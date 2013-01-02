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
    
    private static final HashMap<DataSource, DataSourceUris> byDataSource = new HashMap<DataSource, DataSourceUris>();
    private static final HashMap<Resource, DataSourceUris> register = new HashMap<Resource, DataSourceUris>();
    private static HashSet<URI> expectedPredicates = new HashSet<URI>(Arrays.asList(new URI[] {
        BridgeDBConstants.FULL_NAME_URI,
        RdfConstants.TYPE_URI,
        BridgeDBConstants.ALTERNATIVE_FULL_NAME_URI,
        BridgeDBConstants.FULL_NAME_URI,
        BridgeDBConstants.ID_EXAMPLE_URI,
        BridgeDBConstants.MAIN_URL_URI,
        BridgeDBConstants.ORGANISM_URI,
        BridgeDBConstants.PRIMAY_URI,
        BridgeDBConstants.SYSTEM_CODE_URI,
        BridgeDBConstants.TYPE_URI,
        BridgeDBConstants.URL_PATTERN_URI,
        BridgeDBConstants.URN_BASE_URI,
    }));
      
    public static URI getResourceId(DataSource dataSource) {
        return new URIImpl(BridgeDBConstants.DATA_SOURCE1 + "_" + scrub(dataSource.getFullName()));
    }
    
    public static void addAll(RepositoryConnection repositoryConnection) throws IOException, RepositoryException {
        for (DataSource dataSource:DataSource.getDataSources()){
            add(repositoryConnection, dataSource); 
        }
    }

    public static void add(RepositoryConnection repositoryConnection, DataSource dataSource) throws IOException, RepositoryException {
        URI id = getResourceId(dataSource);
        repositoryConnection.add(id, RdfConstants.TYPE_URI, BridgeDBConstants.DATA_SOURCE_URI);         
        repositoryConnection.add(id, BridgeDBConstants.FULL_NAME_URI, new LiteralImpl(dataSource.getFullName()));

        if (dataSource.getSystemCode() != null && (!dataSource.getSystemCode().trim().isEmpty())){
            repositoryConnection.add(id, BridgeDBConstants.SYSTEM_CODE_URI, new LiteralImpl(dataSource.getSystemCode()));
        }

        for (String alternativeFullName:dataSource.getAlternativeFullNames()){
            repositoryConnection.add(id, BridgeDBConstants.ALTERNATIVE_FULL_NAME_URI, new LiteralImpl(alternativeFullName));            
        }
        
        if (dataSource.getMainUrl() != null){
            repositoryConnection.add(id, BridgeDBConstants.MAIN_URL_URI, new LiteralImpl(dataSource.getMainUrl()));
        }

        if (dataSource.getExample() != null && dataSource.getExample().getId() != null){
            repositoryConnection.add(id, BridgeDBConstants.ID_EXAMPLE_URI, new LiteralImpl(dataSource.getExample().getId()));
        }

        if (dataSource.isPrimary()){
            repositoryConnection.add(id, BridgeDBConstants.PRIMAY_URI, BooleanLiteralImpl.TRUE);
        } else {
            repositoryConnection.add(id, BridgeDBConstants.PRIMAY_URI, BooleanLiteralImpl.FALSE);
        }
 
        if (dataSource.getType() != null){
            repositoryConnection.add(id, BridgeDBConstants.TYPE_URI, new LiteralImpl(dataSource.getType()));
        }

        String urlPattern = dataSource.getUrl("$id");
        if (urlPattern.length() > 3){
            repositoryConnection.add(id, BridgeDBConstants.URL_PATTERN_URI, new LiteralImpl(urlPattern));
        }

        if (!VERSION2){
            String urnPattern = dataSource.getURN("");
            if (urnPattern.length() > 1){
                repositoryConnection.add(id, BridgeDBConstants.URN_BASE_URI, 
                        new LiteralImpl(urnPattern.substring(0, urnPattern.length()-1)));
            }
        }

        if (dataSource.getOrganism() != null){
            Organism organism = (Organism)dataSource.getOrganism();
            repositoryConnection.add(id, BridgeDBConstants.ORGANISM_URI, OrganismRdf.getResourceId(organism));
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
        DataSource dataSource = readDataSources(repositoryConnection, dataSourceId);
        dataSourceUris = DataSourceUris.byDataSource(dataSource);
        register.put(dataSourceId, dataSourceUris);
        return dataSourceUris;
     }    
        
    public static DataSource readDataSources(RepositoryConnection repositoryConnection, Resource dataSourceId) 
            throws BridgeDBException, RepositoryException{
        return readDataSourcesStatements(repositoryConnection, dataSourceId);
    }
    
    public static DataSource readDataSourcesStatements(RepositoryConnection repositoryConnection, Resource dataSourceId) 
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
    
    private void readUriPatterns(RepositoryConnection repositoryConnection, Resource dataSourceId) 
            throws BridgeDBException, RepositoryException{
        String identifiers_org_base = 
                getSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.IDENTIFERS_ORG_PATTERN_URI);
        setIdentifiersOrgBase(identifiers_org_base);
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
    
    private DataSourceUris(DataSource wraps){
        inner = wraps;
        byDataSource.put(inner, this);
    }
    
    public static DataSourceUris byDataSource(DataSource dataSource){
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
                inner.setIdentifiersOrgUri(identifiersOrgBase);
            } catch (IDMapperException ex) {
                throw new BridgeDBException("Unable to set Identifiers Org Base to " + identifiersOrgBase, ex);
            }
        }
    }
    
}
