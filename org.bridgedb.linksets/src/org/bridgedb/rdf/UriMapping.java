/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.DataSourcePatterns;
import org.bridgedb.utils.BridgeDBException;

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

    public static void addMapping(DataSource dataSource, UriPattern uriPattern, UriMappingRelationship uriMappingRelationship) {
        UriMapping mapping = getMapping(dataSource, uriPattern);
        System.out.println(mapping);
        mapping.relationships.add(uriMappingRelationship);
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

    public static void init() throws BridgeDBException{
        Set<DataSource> dataSources = DataSource.getDataSources();
        for (DataSource dataSource:dataSources){
            String url = dataSource.getUrl("$id");
            if (url.length() > 3){
                UriPattern uriPattern = UriPattern.byUrlPattern(url);
                addMapping (dataSource, uriPattern, UriMappingRelationship.PRIMARY_URI_PATTERN);
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
}
