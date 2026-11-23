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
package org.bridgedb.uri.tools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;

/**
 *
 * @author Christian
 */
public class GraphResolver {

   private HashMap<String,Set<RegexUriPattern>> allowedUriPattern;
    
    private final static String PROPERTIES_FILE = "graph.properties";
    private final static String PROPERTY_PREFIX = "context.";
    private final static String PATTERN  = "pattern";
    private final static String GRAPH_POSTFIX  = ".graph";
    
    private static GraphResolver instance;
    
    public static GraphResolver getInstance() throws BridgeDBException{
        if (instance == null){
            UriPattern.refreshUriPatterns();
            instance = new GraphResolver();
        }
        return instance;
    }
    
    private GraphResolver() throws BridgeDBException{
        readProperties();
    }
    
    private void readProperties() throws BridgeDBException{
        allowedUriPattern = new HashMap<String,Set<RegexUriPattern>>();
        Properties properties = ConfigReader.getProperties(PROPERTIES_FILE);
        Set<String> keys = properties.stringPropertyNames();
        for (String key:keys){
            if (key.startsWith(PROPERTY_PREFIX)){
                String[] parts = key.split("\\.");
                if (parts[2].equals(PATTERN)){
                    String graphKey = PROPERTY_PREFIX + parts[1] + GRAPH_POSTFIX;
                    String graph =  properties.getProperty(graphKey);
                    String pattern = properties.getProperty(key);
                    addPatterns(graph, pattern);
                }
            }
        }
    }
    
    private void addPatterns(String graph, String pattern) throws BridgeDBException{
        Set<RegexUriPattern> uriPatterns = RegexUriPattern.existingByPattern(pattern);
        //unable to set default some example CHEBI have different URIs in different contexts
        //UriPattern test = UriPattern.existingByPattern(pattern);
        //test.setGraphDefault();
        if (uriPatterns.isEmpty() ){
            throw new BridgeDBException("no UriPattern known for " + pattern);
        }
        addPatterns(graph, uriPatterns);
    }
    
    private void addPatterns(String graph, Set<RegexUriPattern> uriPatterns) throws BridgeDBException{
        Set<RegexUriPattern> patterns = allowedUriPattern.get(graph);
        if (patterns == null){
            patterns = new HashSet<RegexUriPattern>();
        }
        patterns.addAll(uriPatterns);
        allowedUriPattern.put(graph, patterns);
    }

    private void addPattern(String graph, RegexUriPattern uriPattern) throws BridgeDBException{
        Set<RegexUriPattern> patterns = allowedUriPattern.get(graph);
        if (patterns == null){
            patterns = new HashSet<RegexUriPattern>();
        }
        patterns.add(uriPattern);
        allowedUriPattern.put(graph, patterns);
    }

    public static Set<String> knownGraphs() throws BridgeDBException{
        return getInstance().allowedUriPattern.keySet();
    }
    
    public static Set<RegexUriPattern> getUriPatternsForGraph(String graph) throws BridgeDBException {
        if (graph == null || graph.isEmpty()){
            return new HashSet<RegexUriPattern>();
        }
        GraphResolver resolver = getInstance();
        Set<RegexUriPattern> results = resolver.getAllowedPatterns(graph);
        if (results == null){
            throw new BridgeDBException ("Unkown Graph " + graph);
        }
        return results;
    }

    public static void addMapping(String graph, String pattern) throws BridgeDBException{
        GraphResolver gr = getInstance();
        gr.addPatterns(graph, pattern); 
    }
    
    public static void addMapping(String graph, UriPattern uriPattern) throws BridgeDBException{
        GraphResolver gr = getInstance();
        gr.addPatterns(graph, RegexUriPattern.byPattern(uriPattern));        
    }
    
    public static void addMapping(String graph, RegexUriPattern uriPattern) throws BridgeDBException{
        GraphResolver gr = getInstance();
        gr.addPattern(graph, uriPattern);        
    }

    public static void addTestMappings() throws BridgeDBException{
        GraphResolver gr = getInstance();
        gr.addPatterns("http://www.conceptwiki.org", "http://www.conceptwiki.org/concept/$id");
        gr.addPatterns("http://www.chemspider.com", "http://rdf.chemspider.com/$id");
    }

    private Set<RegexUriPattern> getAllowedPatterns(String graph) {
        return allowedUriPattern.get(graph);
    }

    public Map<String, Set<RegexUriPattern>> getAllowedUriPatterns() {
        return allowedUriPattern;
    }
 }
