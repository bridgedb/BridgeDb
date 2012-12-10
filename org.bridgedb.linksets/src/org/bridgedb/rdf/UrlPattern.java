/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import org.bridgedb.metadata.constants.BridgeDBConstants;
import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author Christian
 */
public class UrlPattern {

    private final String nameSpace;
    private final String postFix;
    private static HashMap<String,UrlPattern> byNameSpaceOnly = new HashMap<String,UrlPattern>();
    private static HashMap<String,HashMap<String,UrlPattern>> byNameSpaceAndPostFix = 
            new HashMap<String,HashMap<String,UrlPattern>> ();
            
    private UrlPattern(String namespace){
        this.nameSpace = namespace;
        this.postFix = null;
        byNameSpaceOnly.put(namespace, this);
    } 
    
    private UrlPattern(String namespace, String postfix){
        this.nameSpace = namespace;
        if (postfix == null || postfix.isEmpty()){
            this.postFix = null;
            byNameSpaceOnly.put(namespace, this);    
        } else {
            this.postFix = postfix;
            HashMap<String,UrlPattern> postFixMap = byNameSpaceAndPostFix.get(namespace);
            if (postFixMap == null){
                postFixMap = new HashMap<String,UrlPattern>();
            }
            postFixMap.put(postfix, this);
            byNameSpaceAndPostFix.put(namespace, postFixMap);
        }
    }
   
    public static UrlPattern byNameSpace(String nameSpace){
        UrlPattern result = byNameSpaceOnly.get(nameSpace);
        if (result == null){
            result = new UrlPattern(nameSpace);
        }
        return result;
    }
    
    private static UrlPattern byNameSpaceAndPostfix(String nameSpace, String postfix) {
        HashMap<String,UrlPattern> postFixMap = byNameSpaceAndPostFix.get(nameSpace);
        if (postFixMap == null){
            return new UrlPattern(nameSpace, postfix);
        }
        UrlPattern result = postFixMap.get(postfix);
        if (result == null){
            return new UrlPattern(nameSpace, postfix);
        }
        return result;
    }
    
    public static UrlPattern byUrlPattern(String urlPattern) throws BridgeDBException{
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
    
    public static void main(String[] args) throws BridgeDBException  {
        UrlPattern test = new UrlPattern("This is a test", "part2");
        UrlPattern test2 = UrlPattern.byNameSpaceAndPostfix("This is a test","part2");
        System.out.println(test);
        System.out.println(test2);
        System.out.println(test == test2);
        test2 = UrlPattern.byUrlPattern("This is a test$idpart2");
        System.out.println(test);
        System.out.println(test2);
        System.out.println(test == test2);
    }

}
