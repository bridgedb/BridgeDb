/** To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.bio.Organism;
import org.bridgedb.metadata.constants.BridgeDBConstants;
import org.bridgedb.metadata.constants.RdfConstants;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.StoreType;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Christian
 */
public class DataSourceImporter {
    
    static final Logger logger = Logger.getLogger(DataSourceImporter.class);
    
    public static void main(String[] args) throws IDMapperException {
        ConfigReader.logToConsole();
        //InputStream stream = ConfigReader.getInputStream("BioDataSource.ttl");
        //StatementReaderAndImporter reader = new StatementReaderAndImporter(stream, RDFFormat.TURTLE, StoreType.TEST);
        File file = new File ("C:/OpenPhacts/BioDataSource.ttl");
        StatementReaderAndImporter reader = new StatementReaderAndImporter(file, StoreType.TEST);
        Set<Statement> allStatements = reader.getVoidStatements();
        load(allStatements);
    }

    public static void load(Set<Statement> allStatements) throws IDMapperException {
        loadDataSources(allStatements);
        loadUriPatterns(allStatements);
        linkUriPatterns(allStatements);    
    }
    
    public static void loadDataSources(Set<Statement> allStatements) throws IDMapperException {
        Set<Resource> resources = getDataSourceResources(allStatements);
        for (Resource resource:resources){
            System.out.println(resource);
            Set<Statement> resourceStatements = getStatementsByResource(resource, allStatements);
            DataSource dataSource = createDataSource(resourceStatements, allStatements);
        }
    }
    
    private static void loadUriPatterns(Set<Statement> allStatements) {
    //    throw new UnsupportedOperationException("Not yet implemented");
    }

    private static void linkUriPatterns(Set<Statement> allStatements) {
    //    throw new UnsupportedOperationException("Not yet implemented");
    }
    

    private static Set<Resource> getDataSourceResources(Set<Statement> statements){
        HashSet<Resource> resources = new HashSet<Resource>();
        for (Statement statement:statements){
            if (statement.getPredicate().equals(RdfConstants.TYPE_URI) && statement.getObject().equals(BridgeDBConstants.DATA_SOURCE_URI)){
                resources.add(statement.getSubject());
            }
        }
        return resources;
    }
    
    private static Set<Statement> getStatementsByResource(Resource resource, Set<Statement> statements){
        HashSet<Statement> subset = new HashSet<Statement>();
        for (Statement statement:statements){
            if (statement.getSubject().equals(resource)){
                subset.add(statement);
            }
        }
        return subset;    
    }
    
    private static DataSource createDataSource(Set<Statement> dataSourceStatements, Set<Statement> allStatements) throws BridgeDBException{
        String fullName = null;
        String idExample = null;
        String mainUrl = null;
        Object organism = null;
        String primary = null;
        String systemCode = null;
        String type = null;
        String urlPattern = null;
        String urnBase = null;
        String identifiersOrgBase = null;
        String wikipathwaysBase = null;
        String bio2RDFPattern = null;
        String sourceRDFURIPattern = null;
        
        for (Statement statement:dataSourceStatements){
            if (statement.getPredicate().equals(RdfConstants.TYPE_URI)){
                //Ignore the type statement
            } else if (statement.getPredicate().equals(BridgeDBConstants.FULL_NAME_URI)){
                fullName = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.ID_EXAMPLE_URI)){
                idExample = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.MAIN_URL_URI)){
                mainUrl = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.ORGANISM_URI)){
                Value organismId = statement.getObject();
                organism = getOrganism(organismId, allStatements);
            } else if (statement.getPredicate().equals(BridgeDBConstants.PRIMAY_URI)){
                primary = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.SYSTEM_CODE_URI)){
                systemCode = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.TYPE_URI)){
                type = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.URL_PATTERN_URI)){
                urlPattern = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.URN_BASE_URI)){
                urnBase = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.IDENTIFIERS_ORG_BASE_URI)){
                identifiersOrgBase = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.WIKIPATHWAYS_BASE_URI)){
                wikipathwaysBase = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.SOURCE_RDF_URI)){
                sourceRDFURIPattern = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.BIO2RDF_URI)){
                bio2RDFPattern = statement.getObject().stringValue();
            } else {
                throw new BridgeDBException ("Unexpected Statement " + statement);
            }
        }
        DataSource.Builder builder = DataSource.register(systemCode, fullName);
        if (mainUrl != null) {
            builder.mainUrl(mainUrl);
        }
        if (urlPattern != null) {
            builder.urlPattern(urlPattern);
        }
        if (idExample != null) {
            builder.idExample(idExample);
        }
        if (type != null) {
            builder.type(type);
        }
        if (organism != null) {
            builder.organism(organism);
        }					      
        if (primary != null) {
            builder.primary (Boolean.parseBoolean(primary));
        }					      
        if (urnBase != null) {
            builder.urnBase(urnBase);
        }
        DataSource dataSource = builder.asDataSource();
        registerUriPattern(dataSource, urlPattern, UriMappingRelationship.URN_BASE);
        registerNameSpace(dataSource, identifiersOrgBase, UriMappingRelationship.IDENTIFERS_ORG);
        registerNameSpace(dataSource, wikipathwaysBase, UriMappingRelationship.WIKIPATHWAYS);
        registerUriPattern(dataSource, sourceRDFURIPattern, UriMappingRelationship.SOURCE_RDF);
        registerUriPattern(dataSource, bio2RDFPattern, UriMappingRelationship.BIO2RDF_URI);
        return dataSource;
    }

    private static Object getOrganism(Value organismId, Set<Statement> allStatements) throws BridgeDBException {
        for (Statement statement:allStatements){
            if (statement.getSubject().equals(organismId) && 
                    statement.getPredicate().equals(BridgeDBConstants.LATIN_NAME_URI)){
                String latinName = statement.getObject().stringValue();
                Organism result =  Organism.fromLatinName(latinName);
                if (result != null){
                    return result;
                }
                throw new BridgeDBException("No Orgamism with LatinName " + latinName + " for " + organismId);
            }
        }
        throw new BridgeDBException("No Orgamism found for " + organismId);
    }

    private static void registerUriPattern(DataSource dataSource, String urlPattern, UriMappingRelationship uriMappingRelationship) throws BridgeDBException {
        if (urlPattern == null || urlPattern.isEmpty()) {
            return;
        }
        System.out.println(dataSource.getFullName() + " " + uriMappingRelationship + " = " + urlPattern);
        UriPattern pattern = UriPattern.byUrlPattern(urlPattern);
        UriMapping.addMapping(dataSource, pattern, uriMappingRelationship);
    }

    private static void registerNameSpace(DataSource dataSource, String nameSpace, UriMappingRelationship uriMappingRelationship) throws BridgeDBException {
        if (nameSpace == null || nameSpace.isEmpty()) {
            return;
        }
        System.out.println(nameSpace);
        UriPattern pattern = UriPattern.byNameSpace(nameSpace);
        UriMapping.addMapping(dataSource, pattern, uriMappingRelationship);
    }
}
