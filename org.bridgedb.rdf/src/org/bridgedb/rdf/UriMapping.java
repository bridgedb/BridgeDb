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
    
    private static HashMap<DataSource,HashMap<UriPattern, UriMapping>> byDataSource = 
            new HashMap<DataSource,HashMap<UriPattern, UriMapping>>();
    private static HashMap<UriPattern,HashMap<DataSource, UriMapping>> byUriPattern = 
            new HashMap<UriPattern,HashMap<DataSource, UriMapping>>();
    
    private UriMapping(DataSource dataSource, UriPattern uriPattern) {
        this.dataSource = dataSource;
        this.uriPattern = uriPattern;
        this.relationships = new HashSet<UriMappingRelationship>();
        HashMap<UriPattern, UriMapping> mappings1 = byDataSource.get(dataSource);
        if (mappings1 == null){
            mappings1 = new HashMap<UriPattern, UriMapping>();
        }
        mappings1.put(uriPattern, this);
        byDataSource.put(dataSource, mappings1);
        HashMap<DataSource, UriMapping> mappings2 = byUriPattern.get(uriPattern);
        if (mappings2 == null){
            mappings2 = new HashMap<DataSource, UriMapping>();
        }
        mappings2.put(dataSource, this);
        byUriPattern.put(uriPattern, mappings2);
    }

    public static UriMapping addMapping(DataSource dataSource, UriPattern uriPattern, UriMappingRelationship uriMappingRelationship) {
        UriMapping mapping = getMapping(dataSource, uriPattern);
        mapping.addRelationship(uriMappingRelationship);
        return mapping;
    }
    
    //TODO safety checks and add to DataSource UrlPattern, and urnBase
    private void addRelationship(UriMappingRelationship relationship) {
        relationships.add(relationship);
    }
    
    private static UriMapping getMapping(DataSource dataSource, UriPattern uriPattern) {
        HashMap<UriPattern, UriMapping> mappings = byDataSource.get(dataSource);
        UriMapping result = null;
        if (mappings != null){
            result = mappings.get(uriPattern);
        }
        if (result == null){
            result = new UriMapping(dataSource, uriPattern);
        }
        return result;
    }

    public static Set<UriMapping> getAllUriMappings(){
        Set<UriMapping> results = new HashSet<UriMapping>();
        for (HashMap<UriPattern, UriMapping> batch: byDataSource.values()){
            results.addAll(batch.values());
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
            HashMap<DataSource, UriMapping> mappings = byUriPattern.get(pattern);
            if (mappings != null && mappings.size() > 1){
                System.out.println(pattern.getUriPattern());
                for (DataSource dataSource:mappings.keySet()){
                    System.out.println ("   " + dataSource + " " + mappings.get(dataSource).relationships);
                    System.out.println ("       " + DataSourcePatterns.getPatterns().get(dataSource));
                }
            }
        }
    }

    public String getRdfId(){
        String name = DataSourceExporter.scrub(dataSource.getFullName() + "_" + uriPattern.getRdfLabel());
        return ":" + BridgeDBConstants.URI_MAPPING + "_" + name;
    }

    public static void writeAllAsRDF(BufferedWriter writer) throws IOException {
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
        System.out.println(mappingId);
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

}
