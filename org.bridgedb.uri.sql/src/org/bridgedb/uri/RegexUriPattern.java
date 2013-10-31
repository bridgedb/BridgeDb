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
package org.bridgedb.uri;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import org.bridgedb.DataSource;
import org.bridgedb.DataSourcePatterns;
import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.rdf.BridgeDBRdfHandler;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.rdf.UriPatternType;
import org.bridgedb.rdf.identifiers.org.IdentifersOrgReader;
import org.bridgedb.rdf.pairs.RdfBasedCodeMapper;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.Reporter;

/**
 *
 * @author Christian
 */
public class RegexUriPattern {

    private final String prefix;
    private final String postfix;
    private final String sysCode;
    private final Pattern regex;
    
    private static HashMap<String, Set<RegexUriPattern>> byShortNames = new HashMap<String, Set<RegexUriPattern>>();
    
    private RegexUriPattern(String prefix, String postfix, String sysCode, Pattern regex) throws BridgeDBException{
        if (prefix == null || prefix.isEmpty()){
            throw new BridgeDBException ("Illegal prefixe " + prefix);
        }
        this.prefix = prefix;
        if (postfix != null){
            this.postfix = postfix;
        } else {
            this.postfix = "";
        }
        if (sysCode == null || sysCode.isEmpty()){
            throw new BridgeDBException ("Illegal sysCode " + sysCode);
        }
        this.sysCode = sysCode;
        this.regex = regex;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @return the postfix
     */
    public String getPostfix() {
        return postfix;
    }

    /**
     * @return the sysCode
     */
    public String getSysCode() {
        return sysCode;
    }

    public Pattern getRegex() {
        return regex;
    }
    
    public String getUri(String id) {
        return prefix + id + postfix;
    }

    public String toString(){
        String result = getUri("$id");// + " -> " + sysCode;
        if (regex != null){
            result = result + " (" + regex.pattern() + ")";
        }
        result+= " for DataSource " + sysCode;
        return result;
    }
    
    public String getUriPattern() {
        //TODO handle regex
        return getUri("$id");
    }

    public Set<UriPattern> mapsTo() throws BridgeDBException{
       TreeSet<UriPattern> possibles  = new TreeSet<UriPattern>(UriPattern.byCode(sysCode));
       UriPattern asPattern = UriPattern.byPattern(getUriPattern());
       possibles.remove(asPattern);
       return possibles;
    }
    
    private static Pattern shortenRegex(Pattern regex, String sysCode) throws BridgeDBException{
        if (regex == null){
            return null;
        }
        String xrefPrefix = RdfBasedCodeMapper.getXrefPrefix(sysCode);
        String fullPattern = regex.pattern();
        if (fullPattern.startsWith(xrefPrefix)){
            String partPattern = fullPattern.substring(xrefPrefix.length());
            return Pattern.compile(partPattern);
        } else if (fullPattern.startsWith("^" + xrefPrefix)){
            String partPattern = "^" + fullPattern.substring(1 + xrefPrefix.length());
            return Pattern.compile(partPattern);
        } else {
            throw new BridgeDBException ("Unable to convert Pattern " + regex.pattern() + " for code " + sysCode 
                    + " based on xrefprefix " + xrefPrefix);
        }
    }

    public static RegexUriPattern factory(String prefix, String postfix, String sysCode) throws BridgeDBException {
        return new RegexUriPattern(prefix, postfix, sysCode, null);
    }

    public static RegexUriPattern factory(String prefix, String postfix, String sysCode, Pattern regexPattern) throws BridgeDBException {
        return new RegexUriPattern(prefix, postfix, sysCode, regexPattern);
    }

    public static RegexUriPattern factory(UriPattern uriPattern, String sysCode) throws BridgeDBException{
        DataSource dataSource = DataSource.getExistingBySystemCode(sysCode);
        Pattern regex = DataSourcePatterns.getPatterns().get(dataSource);
        String xrefPrefix = RdfBasedCodeMapper.getXrefPrefix(sysCode);
        String prefix;
        String postfix = uriPattern.getPostfix();
        if (xrefPrefix == null){
            prefix = uriPattern.getPrefix();
        } else if (uriPattern.getType() == UriPatternType.codeMapperPattern){
            //prefix should not include the xrefPrefix
            prefix = uriPattern.getPrefix();
            regex = shortenRegex(regex, sysCode);
        } else {
            //prefix should include the xrefPrefix as regex and IDs no longer do
            prefix = uriPattern.getPrefix() + xrefPrefix;
            regex = shortenRegex(regex, sysCode);
        }
        return new RegexUriPattern(prefix, postfix, sysCode, regex);
    }
    
    public static Collection<RegexUriPattern> getUriPatterns() throws BridgeDBException {
        HashSet<RegexUriPattern> results = new HashSet<RegexUriPattern>();
        for (UriPattern pattern:UriPattern.getUriPatterns()){
            for (String sysCode:pattern.getSysCodes()){
                results.add(factory(pattern, sysCode));
            }
        }
        return results;
    }

    public static Set<RegexUriPattern> byPattern(String pattern) throws BridgeDBException {
        //todo regex in pattern
        UriPattern uriPattern = UriPattern.existingByPattern(pattern);
        return byPattern(uriPattern);
    }

    public static Set<RegexUriPattern> byPattern(UriPattern uriPattern) throws BridgeDBException {
        Set<String> possibleSysCodes = uriPattern.getSysCodes();
        HashSet<RegexUriPattern> results = new HashSet<RegexUriPattern>();
        for (String possibleSysCode:possibleSysCodes){
            results.add(factory(uriPattern, possibleSysCode));
        }
        return results;
    }
 
    /**
     * @return the byShortNames
     */
    public static HashMap<String, Set<RegexUriPattern>> getByShortNames() throws BridgeDBException {
        return byShortNames;
    }

    private static String extractShortName (String full) throws BridgeDBException {
        String withoutStart;
        if (full.startsWith("ftp://")){
            withoutStart = full.substring(6);
        } else if (full.startsWith("http://")){
            withoutStart = full.substring(7);
        } else if (full.startsWith("https://")){
            withoutStart = full.substring(8);
        } else {
            withoutStart = full;
        }
        if (withoutStart.startsWith("ftp.")){
            withoutStart = withoutStart.substring(4);
        }
        if (withoutStart.startsWith("rdf.")){
            withoutStart = withoutStart.substring(4);
        }
        if (withoutStart.startsWith("www.")){
            withoutStart = withoutStart.substring(4);
        } 
        return withoutStart.substring(0, withoutStart.indexOf("/"));
    }

    public static HashMap<String, Integer> getUriGroups() throws BridgeDBException {
        Set<UriPattern> patterns = UriPattern.getUriPatterns();
        HashMap<String, Integer> results = new HashMap<String, Integer>();
        System.out.println (patterns.size() + " patterns found");
        for (UriPattern pattern:UriPattern.getUriPatterns()){
            String mid = extractShortName(pattern.getUriPattern());
            Integer count = results.get(mid);
            if (count == null){
                count = pattern.getSysCodes().size();
            } else {
                count+= pattern.getSysCodes().size();
            }
            results.put(mid, count);
         }
        return results;
    }

    public static void init() throws BridgeDBException {
        Reporter.println("RegesUriPattern init");
        Set<UriPattern> patterns = UriPattern.getUriPatterns();
        HashMap<String, Integer> results = new HashMap<String, Integer>();
        for (UriPattern pattern:UriPattern.getUriPatterns()){
            String mid = extractShortName(pattern.getUriPattern());
            Set<RegexUriPattern> byShortName = getByShortNames().get(mid);
            if (byShortName == null){
                byShortName = new HashSet<RegexUriPattern>();
            }
            byShortName.addAll(byPattern(pattern));
            getByShortNames().put(mid, byShortName);
        }
    }

    public static void main(String[] args) throws Exception {
        DataSourceTxt.init();
        UriPattern.registerUriPatterns();
        BridgeDBRdfHandler.init();
        IdentifersOrgReader.init();
        init();

/*        HashMap<String, Integer> mappings = getUriGroups();
        Set<String> groups = mappings.keySet();
        int count = 0;
        for (String group:groups){
            if (mappings.get(group) > 1){
                System.out.println(group);
//                for (RegexUriPattern pattern:byShortNames.get(group)){
//                    System.out.println("\t" + pattern);
//                }
                count++;
            }
        }
        System.out.println(groups.size());
        System.out.println(count++);

        System.out.println("by short name");
        for (RegexUriPattern pattern:byShortNames.get("bio2rdf.org")){
            System.out.println(pattern);
            for (UriPattern mapped:pattern.mapsTo()){
                System.out.println("\t" + mapped);
            }
        }        
*/
        TreeSet<String> values = new TreeSet<String>();
        for (RegexUriPattern pattern:getUriPatterns()){
            values.add("\t\t'" + pattern.getUriPattern() + "',");
        }
        for (String value:values){
            System.out.println(value);
        }
    }
    
    @Override
    public boolean equals(Object otherObject){
        if (!(otherObject instanceof RegexUriPattern)){
            return false;
        }
        RegexUriPattern other = (RegexUriPattern)otherObject;
        if (!this.prefix.equals(other.prefix)){
            return false;
        }
        if (!this.sysCode.equals(other.sysCode)){
            return false;
        }
        if (this.postfix == null){
            if (other.postfix != null){
                return false;
            }
        } else {
            if (!other.postfix.equals(other.postfix)){
                return false;
            }
        }
        if (this.regex == null){
            if (other.regex != null){
                return false;
            }
        } else {
            if (other.regex == null){
                return false;
            }
            if (!this.regex.pattern().equals(other.regex.pattern())){
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        String full = sysCode + "£" + prefix;
        if (regex != null){
            full = full + regex;
        }
        if (prefix != null){
            full = full + prefix;
        }
        return full.hashCode();
    }
}
