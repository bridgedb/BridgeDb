/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.IOException;
import java.util.HashMap;
import org.bridgedb.DataSource;
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
    private UriPattern dataSourceUrlPattern;
    
    private static final HashMap<DataSource, DataSourceUris> byDataSource = new HashMap<DataSource, DataSourceUris>();
    private static final HashMap<Resource, DataSourceUris> register = new HashMap<Resource, DataSourceUris>();
    
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

        //Alternative names
        
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

        if (!VERSION2){
            String urlPattern = dataSource.getUrl("$id");
            if (urlPattern.length() > 3){
                repositoryConnection.add(id, BridgeDBConstants.URL_PATTERN_URI, new LiteralImpl(urlPattern));
            }
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

    public static void readAllDataSources(RepositoryConnection repositoryConnection) throws BridgeDBException, RepositoryException{
        RepositoryResult<Statement> statements = 
                repositoryConnection.getStatements(null, RdfConstants.TYPE_URI, BridgeDBConstants.DATA_SOURCE_URI, true);
                //repositoryConnection.getStatements(null, null, null, true);
        while (statements.hasNext()) {
            Statement statement = statements.next();
            DataSource ds = readDataSources(repositoryConnection, statement.getSubject());
        }
    }

    public static DataSource readDataSources(RepositoryConnection repositoryConnection, Resource dataSourceId) 
            throws BridgeDBException, RepositoryException{
        DataSourceUris dsu = register.get(dataSourceId);
        if (dsu != null){
            return dsu.inner;
        }
        String fullName = getSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.FULL_NAME_URI);
        String systemCode = getPossibleSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.SYSTEM_CODE_URI);
        DataSource.Builder builder = DataSource.register(systemCode, fullName);
        dsu = DataSourceUris.byDataSource(builder.asDataSource());
        register.put(dataSourceId, dsu);
        
        RepositoryResult<Statement> statements = 
                repositoryConnection.getStatements(dataSourceId, null, null, true);
        while (statements.hasNext()) {
            Statement statement = statements.next();
            try{
                dsu.processStatement(statement, builder);
            } catch (Exception e){
                throw new BridgeDBException ("Error processing statement " + statement, e);
            }
        }
        return builder.asDataSource();
    }
    
    private void processStatement(Statement statement, DataSource.Builder builder) throws BridgeDBException{
        if (statement.getPredicate().equals(RdfConstants.TYPE_URI)){
            //Ignore the type statement
        } else if (statement.getPredicate().equals(BridgeDBConstants.ALTERNATIVE_FULL_NAME_URI)){
            builder.alternativeFullName(statement.getObject().stringValue());
        } else if (statement.getPredicate().equals(BridgeDBConstants.FULL_NAME_URI)){
            //Already used the fullName statement;
        } else if (statement.getPredicate().equals(BridgeDBConstants.ID_EXAMPLE_URI)){
            builder.idExample(statement.getObject().stringValue());
        } else if (statement.getPredicate().equals(BridgeDBConstants.MAIN_URL_URI)){
            builder.mainUrl(statement.getObject().stringValue());
        } else if (statement.getPredicate().equals(BridgeDBConstants.ORGANISM_URI)){
            Value organismId = statement.getObject();
            Object organism = OrganismRdf.byRdfResource(organismId);
            builder.organism(organism);
        } else if (statement.getPredicate().equals(BridgeDBConstants.PRIMAY_URI)){
            builder.primary (Boolean.parseBoolean(statement.getObject().stringValue()));
        } else if (statement.getPredicate().equals(BridgeDBConstants.SYSTEM_CODE_URI)){
            //Already used the systemCode statement;
        } else if (statement.getPredicate().equals(BridgeDBConstants.TYPE_URI)){
            builder.type(statement.getObject().stringValue());
        } else if (statement.getPredicate().equals(BridgeDBConstants.URL_PATTERN_URI)){
            String urlPattern = statement.getObject().stringValue();
            builder.urlPattern(urlPattern);
            dataSourceUrlPattern = getUriPatternFromPattern(builder.asDataSource(), urlPattern);
        } else if (statement.getPredicate().equals(BridgeDBConstants.URN_BASE_URI)){
            builder.urnBase(statement.getObject().stringValue());
 //       } else if (statement.getPredicate().equals(BridgeDBConstants.IDENTIFIERS_ORG_BASE_URI)){
 //           registerNameSpace(builder.asDataSource(), statement.getObject().stringValue(), UriMappingRelationship.IDENTIFERS_ORG);
 //       } else if (statement.getPredicate().equals(BridgeDBConstants.WIKIPATHWAYS_BASE_URI)){
 //           registerNameSpace(builder.asDataSource(), statement.getObject().stringValue(), UriMappingRelationship.WIKIPATHWAYS);
 //       } else if (statement.getPredicate().equals(BridgeDBConstants.SOURCE_RDF_URI)){
 //           registerUriPattern(builder.asDataSource(), statement.getObject().stringValue(), UriMappingRelationship.SOURCE_RDF);
 //       } else if (statement.getPredicate().equals(BridgeDBConstants.BIO2RDF_URI)){
 //           registerUriPattern(builder.asDataSource(), statement.getObject().stringValue(), UriMappingRelationship.BIO2RDF_URI);
        } else {
            throw new BridgeDBException ("Unexpected Statement " + statement);
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
    
    private UriPattern getUriPatternFromPattern(DataSource dataSource, String urlPattern) throws BridgeDBException {
        UriPattern pattern = UriPattern.byUrlPattern(urlPattern);
        pattern.setDataSource(dataSource);
        return pattern;
    }

    private UriPattern getUriPatternFromUriSpace(DataSource dataSource, String uriSpace) throws BridgeDBException {
       UriPattern pattern = UriPattern.byNameSpace(uriSpace);
        pattern.setDataSource(dataSource);
        return pattern;
    }

}
