/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.DataSourcePatterns;
import org.bridgedb.IDMapperException;
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public class UriMapping {

    private final DataSource dataSource;
    private final UriPattern uriPattern;
    private final Set<UriMappingRelationship> relationships;
            
    private static HashMap<DataSource, HashSet<UriMapping>> byDataSource = 
            new HashMap<DataSource, HashSet<UriMapping>>();
    private static HashMap<UriPattern, HashSet<UriMapping>> byUriPattern = 
            new HashMap<UriPattern, HashSet<UriMapping>>();
    
    private UriMapping(DataSource dataSource, UriPattern uriPattern) {
        this.dataSource = dataSource;
        this.uriPattern = uriPattern;
        this.relationships = new HashSet<UriMappingRelationship>();
        HashSet<UriMapping> mappings = byDataSource.get(dataSource);
        if (mappings == null){
            mappings = new HashSet<UriMapping>();
        }
        mappings.add(this);
        byDataSource.put(dataSource, mappings);
        mappings = byUriPattern.get(uriPattern);
        if (mappings == null){
            mappings = new HashSet<UriMapping>();
        }
        mappings.add(this);
        byUriPattern.put(uriPattern, mappings);
    }

    public static UriMapping addMapping(DataSource dataSource, UriPattern uriPattern, 
            UriMappingRelationship uriMappingRelationship) throws BridgeDBException {
        UriMapping mapping = getMapping(dataSource, uriPattern);
        mapping.addRelationship(uriMappingRelationship);
        return mapping;
    }
    
    private void addRelationship(UriMappingRelationship relationship) throws BridgeDBException {
        if (!relationship.multiplesUriPatternsAllowed()){
            HashSet<UriMapping> mappings = byDataSource.get(dataSource);
            for (UriMapping mapping:mappings){
                for (UriMappingRelationship exisitingRelationship:mapping.relationships){
                    if (exisitingRelationship == relationship){
                        if (mapping.uriPattern == this.uriPattern){
                            return; //already a known relationship so stop checking
                        } else {
                            throw new BridgeDBException("Relationship " + relationship + " already exists for "
                                    + dataSource + " as " + mapping.uriPattern 
                                    + " which is not the same as " + this.uriPattern);
                        }
                    }
                }
            }
        }
        relationships.add(relationship);
    }
    
    private static UriMapping getMapping(DataSource dataSource, UriPattern uriPattern) {
        HashSet<UriMapping> mappings = byDataSource.get(dataSource);
        UriMapping result = null;
        if (mappings != null){
            for (UriMapping mapping:mappings){
                if (mapping.uriPattern.equals(uriPattern)){
                    result = mapping;
                }
            }
        }
        if (result == null){
            result = new UriMapping(dataSource, uriPattern);
        }
        return result;
    }

    public static Set<UriMapping> getAllUriMappings(){
        Set<UriMapping> results = new HashSet<UriMapping>();
        for (HashSet<UriMapping> batch: byDataSource.values()){
            results.addAll(batch);
        }
        return results;
    }
    
    public static void init() throws BridgeDBException{
        Set<DataSource> dataSources = DataSource.getDataSources();
        for (DataSource dataSource:dataSources){
            String url = dataSource.getUrl("$id");
            if (url.length() > 3){
                UriPattern uriPattern = UriPattern.byUrlPattern(url);
                addMapping (dataSource, uriPattern, UriMappingRelationship.DATA_SOURCE_URL_PATTERN);
            }
        }
        showSharedUriPatterns();
    }

    public static void showSharedUriPatterns(){
        for (UriPattern pattern: byUriPattern.keySet()){
            HashSet<UriMapping> mappings = byUriPattern.get(pattern);
            if (mappings.size() > 1){
                System.out.println(pattern.getUriPattern());
                for (UriMapping mapping:mappings){
                    System.out.println (mapping);
                }
            }
        }
    }

    public String getRdfId(){
        String name = DataSourceExporter.scrub(dataSource.getFullName() + "_" + uriPattern.getRdfLabel());
        return ":" + BridgeDBConstants.URI_MAPPING + "_" + name;
    }

    public static void writeAllAsRDF(BufferedWriter writer) throws IOException, BridgeDBException {
        loadMappingsFromDataSources();
        for (UriMapping mapping:getAllUriMappings()){
            mapping.writeAsRDF(writer);
        }
    }
    
    public void writeAsRDF(BufferedWriter writer) throws IOException{
        writer.write(getRdfId());
        writer.write(" a ");
        writer.write(BridgeDBConstants.URI_MAPPING_SHORT);
        writer.write(";");
        writer.newLine();
        
        for (UriMappingRelationship relationship:relationships){
            writer.write("         ");
            writer.write(BridgeDBConstants.HAS_RELATIONSHIP_SHORT);
            writer.write(" ");
            writer.write(relationship.getRdfId());
            writer.write(";");
            writer.newLine();
        }

        writer.write("         ");
        writer.write(BridgeDBConstants.HAS_DATA_SOURCE_SHORT);
        writer.write(" ");
        writer.write(DataSourceRdf.getRdfId(dataSource));
        writer.write(";");
        writer.newLine();

        writer.write("         ");
        writer.write(BridgeDBConstants.HAS_URI_PATTERN_SHORT);
        writer.write(" ");
        writer.write(uriPattern.getRdfId());
        writer.write(".");
        writer.newLine();
    }

    static UriMapping readRdf(Resource mappingId, Set<Statement> uriMappingStatements) throws BridgeDBException {
        Value uriPatternId = null;
        Value dataSourceId = null;
        HashSet<Value> relationshipIds = new HashSet<Value>();
        for (Statement statement:uriMappingStatements){
            if (statement.getPredicate().equals(BridgeDBConstants.HAS_URI_PATTERN_URI)){
                uriPatternId = statement.getObject();
            } else if (statement.getPredicate().equals(BridgeDBConstants.HAS_DATA_SOURCE_URI)){
                dataSourceId = statement.getObject();
            } else if (statement.getPredicate().equals(BridgeDBConstants.HAS_RELATIONSHIP_URI)){
                relationshipIds.add(statement.getObject());
            }
        }
        if (uriPatternId == null){
            throw new BridgeDBException ("UriMapping " + mappingId + " does not have a " + 
                    BridgeDBConstants.HAS_URI_PATTERN_URI);
        } 
        if (dataSourceId == null){
            throw new BridgeDBException ("UriMapping " + mappingId + " does not have a " + 
                    BridgeDBConstants.HAS_DATA_SOURCE_URI);
        } 
        if (relationshipIds.isEmpty()){
            throw new BridgeDBException ("UriMapping " + mappingId + " must have at least one " + 
                    BridgeDBConstants.HAS_RELATIONSHIP_URI);
        } 
        UriPattern uriPattern = UriPattern.byRdfResource(uriPatternId);
        DataSource dataSource = DataSourceRdf.byRdfResource(dataSourceId);
        UriMapping mapping = getMapping(dataSource, uriPattern);
        for (Value relationshipId:relationshipIds){
            UriMappingRelationship relationship = UriMappingRelationship.byRdfResource(relationshipId);
            mapping.addRelationship(relationship);
        }
        return mapping;
    }

    public DataSource getDataSource(){
        return dataSource;
    }
    
    public UriPattern getUriPattern(){
        return uriPattern;
    }

    private static void loadMappingsFromDataSources() throws BridgeDBException {
        for (DataSource dataSource:DataSource.getDataSources()){
            String urlPattern = dataSource.getUrl("$id");
            if (urlPattern.length() > 3){
                UriPattern pattern = UriPattern.byUrlPattern(urlPattern);
                UriMapping.addMapping(dataSource, pattern, UriMappingRelationship.DATA_SOURCE_URL_PATTERN);
            }
            String identifiersOrgUri = dataSource.getIdentifiersOrgUri("$id");
            if (identifiersOrgUri != null){
                UriPattern pattern = UriPattern.byUrlPattern(identifiersOrgUri);
                UriMapping.addMapping(dataSource, pattern, UriMappingRelationship.IDENTIFERS_ORG);
            }
        }
    }
    
    private void findWikiPathwaysMapping(){
        
//        sourceRDFURI -> bio2RDF -> urlPattern
    }
    
    public String toString(){
        return dataSource + " -> " + uriPattern + " as " + relationships;
    }
}
