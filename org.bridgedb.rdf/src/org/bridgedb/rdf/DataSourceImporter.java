/** To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.rdf.constants.RdfConstants;
import org.bridgedb.rdf.reader.StatementReader;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

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
        StatementReader reader = new StatementReader(file);
        Set<Statement> allStatements = reader.getVoidStatements();
        load(allStatements);
    }

    public static void load(Set<Statement> allStatements) throws IDMapperException {
        loadOrganisms(allStatements);
        loadDataSources(allStatements);
        loadUriPatterns(allStatements);
        loadUriMappings(allStatements);
    }
    
    public static void loadOrganisms(Set<Statement> allStatements) throws IDMapperException {
        Set<Resource> resources = getResourcesByType(allStatements, BridgeDBConstants.ORGANISM_URI);
        for (Resource resource:resources){
            Set<Statement> resourceStatements = getStatementsByResource(resource, allStatements);
            Object orgamism = OrganismRdf.readRdf(resource, resourceStatements);
        }
    }
    
    public static void loadDataSources(Set<Statement> allStatements) throws IDMapperException {
        Set<Resource> resources = getResourcesByType(allStatements, BridgeDBConstants.DATA_SOURCE_URI);
        for (Resource resource:resources){
            Set<Statement> resourceStatements = getStatementsByResource(resource, allStatements);
            DataSource dataSource = DataSourceRdf.readRdf(resource, resourceStatements);
        }
    }
    
    private static void loadUriPatterns(Set<Statement> allStatements) throws BridgeDBException {
        Set<Resource> resources = getResourcesByType(allStatements, BridgeDBConstants.URI_PATTERN_URI);
        for (Resource resource:resources){
            Set<Statement> resourceStatements = getStatementsByResource(resource, allStatements);
            UriPattern pattern = UriPattern.readRdf(resource, resourceStatements);
        }
    }

    private static void loadUriMappings(Set<Statement> allStatements) throws BridgeDBException {
        Set<Resource> resources = getResourcesByType(allStatements, BridgeDBConstants.URI_MAPPING_URI);
        for (Resource resource:resources){
            Set<Statement> resourceStatements = getStatementsByResource(resource, allStatements);
            UriMapping mapping = UriMapping.readRdf(resource, resourceStatements);
        }
    }

    private static Set<Resource> getResourcesByType(Set<Statement> statements, URI type){
        HashSet<Resource> resources = new HashSet<Resource>();
        for (Statement statement:statements){
            if (statement.getPredicate().equals(RdfConstants.TYPE_URI) && statement.getObject().equals(type)){
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
    
}
