/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.metadata.constants.BridgeDBConstants;
import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author Christian
 */
public class UriPattern {

    private final String nameSpace;
    private final String postFix;

    private static HashMap<String,UriPattern> byNameSpaceOnly = new HashMap<String,UriPattern>();
    private static HashMap<String,HashMap<String,UriPattern>> byNameSpaceAndPostFix = 
            new HashMap<String,HashMap<String,UriPattern>> ();  
    
    private UriPattern(String namespace){
        this.nameSpace = namespace;
        this.postFix = null;
        byNameSpaceOnly.put(namespace, this);
    } 
    
    private UriPattern(String namespace, String postfix){
        this.nameSpace = namespace;
        if (postfix == null || postfix.isEmpty()){
            this.postFix = null;
            byNameSpaceOnly.put(namespace, this);    
        } else {
            this.postFix = postfix;
            HashMap<String,UriPattern> postFixMap = byNameSpaceAndPostFix.get(namespace);
            if (postFixMap == null){
                postFixMap = new HashMap<String,UriPattern>();
            }
            postFixMap.put(postfix, this);
            byNameSpaceAndPostFix.put(namespace, postFixMap);
        }
    }
   
    public static UriPattern byNameSpace(String nameSpace){
        UriPattern result = byNameSpaceOnly.get(nameSpace);
        if (result == null){
            result = new UriPattern(nameSpace);
        }
        return result;
    }
    
    private static UriPattern byNameSpaceAndPostfix(String nameSpace, String postfix) {
        HashMap<String,UriPattern> postFixMap = byNameSpaceAndPostFix.get(nameSpace);
        if (postFixMap == null){
            return new UriPattern(nameSpace, postfix);
        }
        UriPattern result = postFixMap.get(postfix);
        if (result == null){
            return new UriPattern(nameSpace, postfix);
        }
        return result;
    }
    
    public static UriPattern byUrlPattern(String urlPattern) throws BridgeDBException{
        int pos = urlPattern.indexOf("$id");
        if (pos == -1) {
            throw new BridgeDBException("Urlpattern should have $id in it");
        }
        String nameSpace = urlPattern.substring(0, pos);
        String postfix = urlPattern.substring(pos + 3);
        if (postfix.isEmpty()){
            return byNameSpace(nameSpace);
        } else {
            return byNameSpaceAndPostfix(nameSpace, postfix);
        }
    }

    public String getRdfId(){
        String name;
        if (postFix == null){
            name = DataSourceExporter.scrub(nameSpace);
        } else {
            name = DataSourceExporter.scrub(nameSpace + "_" + postFix);
        }
        return ":UrlPattern_" + name;
    }
    
    public void writeAsRDF(BufferedWriter writer) throws IOException{
        writer.write(getRdfId());
        writer.write(" a bridgeDB:");
        writer.write(BridgeDBConstants.URL_PATTERN);        
        writer.write(";");        
        writer.newLine();
    }
    
    public String getUriPattern() {
        if (postFix == null){
            return nameSpace + "$id";
        } else {
            return nameSpace + "$id" + postFix;
        }
    }

    public static void main(String[] args) throws BridgeDBException  {
        UriPattern test = new UriPattern("This is a test", "part2");
        UriPattern test2 = UriPattern.byNameSpaceAndPostfix("This is a test","part2");
        System.out.println(test);
        System.out.println(test2);
        System.out.println(test == test2);
        test2 = UriPattern.byUrlPattern("This is a test$idpart2");
        System.out.println(test);
        System.out.println(test2);
        System.out.println(test == test2);
    }

}
