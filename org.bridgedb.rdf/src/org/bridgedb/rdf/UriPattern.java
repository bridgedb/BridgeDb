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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.bridgedb.DataSource;
import org.bridgedb.DataSourcePatterns;
import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.rdf.constants.RdfConstants;
import org.bridgedb.rdf.identifiers.org.IdentifersOrgReader;
import org.bridgedb.utils.BridgeDBException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;

/**
 *
 * @author Christian
 */
public class UriPattern extends RdfBase implements Comparable<UriPattern>{

    private final String prefix;
    private final String postfix;
    private UriPatternType patternType;
    private final Set<String>sysCodes;
    //unable to set default some example CHEBI have different URIs in different contexts
    //private boolean isGraphDefault;
    
    private static HashMap<String,UriPattern> byPattern = new HashMap<String,UriPattern>();
    private static HashMap<String,Set<UriPattern>> byCode = new HashMap<String,Set<UriPattern>>();
    static boolean initialized = false;
    
    private static HashSet<IRI> expectedPredicates = new HashSet<IRI>(Arrays.asList(new IRI[] {
        BridgeDBConstants.HAS_POSTFIX_URI,
        BridgeDBConstants.HAS_PREFIX_URI,
        RdfConstants.TYPE_URI,
        BridgeDBConstants.HAS_DATA_SOURCE,
        BridgeDBConstants.IS_URI_PATTERN_OF
    }));
              
    private UriPattern(String pattern, UriPatternType patternType) throws BridgeDBException{        
        int pos = pattern.indexOf("$id");
        if (pos == -1) {
            throw new BridgeDBException("Pattern " + pattern + " does not have $id in it and is not known.");
        }
        prefix = pattern.substring(0, pos);
        postfix = pattern.substring(pos + 3);
        byPattern.put(pattern, this);
        this.patternType = patternType;
        sysCodes = new HashSet<String>();
        //isGraphDefault = false;
     }
    
     public String getPrefix(){
        return prefix;
    }
    
    public String getPostfix(){
        return postfix;
    }
    
    public boolean hasPostfix(){
        return !postfix.isEmpty();
    }
    
    public static void refreshUriPatterns() throws BridgeDBException{
        if (initialized){
            return;
        }
        if (DataSource.getDataSources().size() < 20) DataSourceTxt.init();
        DataSourceMetaDataProvidor.assumeUnknownsAreBio();
        BridgeDBRdfHandler.init();
        IdentifersOrgReader.init();
        registerUriPatterns();
        initialized = true;
    }

    public static void registerUriPatterns() throws BridgeDBException{
        for (DataSource dataSource:DataSource.getDataSources()){
            String url = dataSource.getKnownUrl("$id");
            if (url != null){
                register(url, dataSource.getSystemCode(), UriPatternType.mainUrlPattern);
            }
            String identifersOrgUrl = dataSource.getIdentifiersOrgUri("$id");
            if (identifersOrgUrl != null){
                register(identifersOrgUrl, dataSource.getSystemCode(), UriPatternType.identifiersOrgPatternSimple);
                String identifersOrgInfoUrl = identifersOrgUrl.replace("identifiers.org","info.identifiers.org");
                register(identifersOrgInfoUrl, dataSource.getSystemCode(), UriPatternType.identifiersOrgPatternInfo);
            }
        }
    }

    public static SortedSet<UriPattern> getUriPatterns() {
        return new TreeSet<UriPattern>(byPattern.values());
    }
             
    public static UriPattern register(String pattern, String sysCode, UriPatternType patternType) throws BridgeDBException{
        if (pattern == null || pattern.isEmpty()){
            throw new BridgeDBException ("Illegal empty or null uriPattern: " + pattern);
        }
        if (sysCode == null || sysCode.isEmpty()){
            throw new BridgeDBException ("Illegal empty or null sysCode: " + sysCode);
        }
        UriPattern result = byPattern.get(pattern);
        if (result == null){
            result = new UriPattern(pattern, patternType);
        }
        result.registerSysCode(sysCode);
        if (result.patternType == patternType){
            return result;
        }
        switch (patternType){
            case mainUrlPattern:
                if (result.patternType == UriPatternType.dataSourceUriPattern){
                    if (result.sysCodes.size() == 1){
                        result.patternType = UriPatternType.mainUrlPattern;
                    }
                    return result;
                }
                break;
            case dataSourceUriPattern:
                if (result.patternType == UriPatternType.mainUrlPattern){
                    if (result.sysCodes.size() > 1){
                        result.patternType = UriPatternType.dataSourceUriPattern;
                    }
                    return result;
                }
            default:
                throw new BridgeDBException("UriPattern " + pattern + " already set to type " + result.patternType 
                        + " so unable to set to " + patternType);
        }
        return result;
    }
    
    private void registerSysCode(String sysCode){
        for (String knownCode: sysCodes){
            if (knownCode.equals(sysCode)){
                return;
            }
        }
        sysCodes.add(sysCode);
        Set<UriPattern> patterns = byCode.get(sysCode);
        if (patterns == null){
            patterns = new HashSet<UriPattern>();
        }
        patterns.add(this);
        byCode.put(sysCode, patterns);        
    }
    
    public static UriPattern byPattern(String pattern) throws BridgeDBException {
        if (pattern == null || pattern.isEmpty()){
            return null;
        }
        if (!pattern.contains("$id")){
            pattern = pattern + "$id";
        }
        return byPattern.get(pattern);
    }

    public static UriPattern existingByPattern(String pattern) throws BridgeDBException {
        UriPattern result = byPattern(pattern);
        if (result == null){
            throw new BridgeDBException ("No UriPattern known for: " + pattern);            
        }       
        return result;
    }

    public static UriPattern existingByPrefixAndPostfix(String prefix, String postfix) throws BridgeDBException {
        if (postfix == null){
            return existingByPattern(prefix + "$id");
        }
        return existingByPattern(prefix + "$id" + postfix);
    }

    public final IRI getResourceId(){
        return SimpleValueFactory.getInstance().createIRI(getUriPattern());
    }
    
    public String getUriPattern() {
        if (postfix == null){
            return prefix + "$id";
        } else {
            return prefix + "$id" + postfix;
        }
    }

    public static void addAll(RepositoryConnection repositoryConnection) 
            throws IOException, RepositoryException, BridgeDBException {
        for (UriPattern pattern:getUriPatterns()){
            pattern.add(repositoryConnection);
        }
   }
    
    public void add(RepositoryConnection repositoryConnection) throws RepositoryException{
        IRI id = getResourceId();
        repositoryConnection.add(id, RdfConstants.TYPE_URI, BridgeDBConstants.URI_PATTERN_URI);
        repositoryConnection.add(id, BridgeDBConstants.HAS_PREFIX_URI,  SimpleValueFactory.getInstance().createLiteral(prefix));
        if (!postfix.isEmpty()){
            repositoryConnection.add(id, BridgeDBConstants.HAS_POSTFIX_URI,  SimpleValueFactory.getInstance().createLiteral(postfix));
        }
    }        
    
    public static UriPattern readUriPattern(RepositoryConnection repositoryConnection, Resource uriPatternId, 
            String code, UriPatternType patternType) throws BridgeDBException, RepositoryException{
        //TODO handle the extra statements
        //checkStatements(repositoryConnection, uriPatternId);
        UriPattern pattern;      
        String prefix = getPossibleSingletonString(repositoryConnection, uriPatternId, BridgeDBConstants.HAS_PREFIX_URI);
        if (prefix == null){
            String uriPattern = uriPatternId.stringValue();
            pattern = register(uriPattern, code, patternType);
        } else {
            String postfix = getPossibleSingletonString(repositoryConnection, uriPatternId, BridgeDBConstants.HAS_POSTFIX_URI);
            if (postfix == null){
                pattern = register(prefix + "$id", code, patternType);
            } else {
                pattern = register(prefix + "$id" + postfix, code, patternType);
            }
        }
        //Add any ither stuff here
        return pattern;
    }

    @Override
    public String toString(){
        return getUriPattern();      
    }

    @Override
    public int compareTo(UriPattern other) {
        String thisString = this.getResourceId().stringValue().toLowerCase();
        thisString = thisString.replaceFirst("https://","http://");
        String otherString = other.getResourceId().stringValue().toLowerCase();
        otherString = otherString.replaceFirst("https://","http://");
        return thisString.compareTo(otherString);
     }

    public String getUri(String id) {
        return prefix + id + postfix;
    }

    public String getIdFromUri(String uri) throws BridgeDBException {
        if (!uri.startsWith(prefix)){
            throw new BridgeDBException("Uri " + uri + " does not match UriPattern " + this);
        }
        if (!uri.endsWith(postfix)){
            throw new BridgeDBException("Uri " + uri + " does not match UriPattern " + this);
        }
        return uri.substring(prefix.length(), uri.length() - postfix.length());
    }

    public static SortedSet<UriPattern> byCodeAndType(String code, UriPatternType patternType){
        TreeSet<UriPattern> results = new TreeSet<UriPattern>();
        Set<UriPattern> possibles = byCode.get(code);
        if (possibles != null){
            for (UriPattern possible:possibles){
                if (possible.patternType == patternType){
                    results.add(possible);
                }
            }
        }
        return results;
    }

    public static Set<UriPattern> byCode(String code){
        Set<UriPattern> possibles = byCode.get(code);
        if (possibles == null){
            possibles = new HashSet<UriPattern>();
        }
        return possibles;
    }

    public UriPatternType getType(){
        return patternType;
    }
    
    public Set<String> getSysCodes(){
        return sysCodes;
    }
    
    public static void checkRegexPatterns() throws BridgeDBException{
        for (UriPattern uriPattern:byPattern.values()){
            if (uriPattern.sysCodes.size() > 1){
                Set<String> patterns = new HashSet<String>();
                for (String sysCode:uriPattern.sysCodes){
                    DataSource dataSource = DataSource.getExistingBySystemCode(sysCode);
                    Pattern regex = DataSourcePatterns.getPatterns().get(dataSource);
                    if (regex == null || regex.pattern().isEmpty()){
                        throw new BridgeDBException("UriPattern " + uriPattern 
                                + " is registered to " + uriPattern.sysCodes + " but DataSource " + dataSource 
                                + " has regex pattern " + regex);
                    }
                    if (patterns.contains(regex.pattern())){
                        throw new BridgeDBException("UriPattern " + uriPattern 
                                + " is registered to " + uriPattern.sysCodes 
                                + " but at least two have the regex pattern " + regex);
                    }
                    patterns.add(regex.pattern());
                }
            }
        }
    }
    
    //unable to set default some example CHEBI have different URIs in different contexts
    /*public void setGraphDefault() throws BridgeDBException{
        for (String code:this.sysCodes){
            Set<UriPattern> alternatives = byCode.get(code);
            for (UriPattern alternative:alternatives){
                if (alternative.isGraphDefault){
                    if (alternative != this){
                        throw new BridgeDBException ("Unable to set " + this + " to graphDefault as sysCode " + code 
                                + " already has graphDefault " + alternative);
                    }
                }
            }
        }
        this.isGraphDefault = true;
   }*/
    
}
