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
import org.bridgedb.metadata.constants.VoidConstants;
import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author Christian
 */
public class UriPattern {

    private final String nameSpace;
    private final String postfix;

    private static HashSet<UriPattern> register = new HashSet<UriPattern>();
    private static HashMap<String,UriPattern> byNameSpaceOnly = new HashMap<String,UriPattern>();
    private static HashMap<String,HashMap<String,UriPattern>> byNameSpaceAndPostFix = 
            new HashMap<String,HashMap<String,UriPattern>> ();  
    
    private UriPattern(String namespace){
        this.nameSpace = namespace;
        this.postfix = null;
        byNameSpaceOnly.put(namespace, this);
        register.add(this);
    } 
    
    private UriPattern(String namespace, String postfix){
        this.nameSpace = namespace;
        if (postfix == null || postfix.isEmpty()){
            this.postfix = null;
            byNameSpaceOnly.put(namespace, this);    
        } else {
            this.postfix = postfix;
            HashMap<String,UriPattern> postFixMap = byNameSpaceAndPostFix.get(namespace);
            if (postFixMap == null){
                postFixMap = new HashMap<String,UriPattern>();
            }
            postFixMap.put(postfix, this);
            byNameSpaceAndPostFix.put(namespace, postFixMap);
        }
        register.add(this);
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
    
    public static Set<UriPattern> getAllUriPatterns(){
       return register;
    }
            
    public static UriPattern byUrlPattern(String urlPattern) throws BridgeDBException{
        int pos = urlPattern.indexOf("$id");
        if (pos == -1) {
            throw new BridgeDBException("Urlpattern should have $id in it");
        }
        String nameSpace = urlPattern.substring(0, pos);
        String postfix = urlPattern.substring(pos + 3);
        return byNameSpaceAndPostFix(nameSpace, postfix);
    }

    public static UriPattern byNameSpaceAndPostFix(String nameSpace, String postfix) throws BridgeDBException{
        if (postfix.isEmpty()){
            return byNameSpace(nameSpace);
        } else {
            return byNameSpaceAndPostfix(nameSpace, postfix);
        }
    }
    
    public final String getRdfLabel(){
        if (postfix == null){
            return DataSourceExporter.scrub(nameSpace);
        } else {
            return DataSourceExporter.scrub(nameSpace + "_" + postfix);
        }
    }
    
    public String getRdfId(){
        return ":" + BridgeDBConstants.URI_PATTERN + "_" + getRdfLabel();
    }

    public void writeAsRDF(BufferedWriter writer) throws IOException{
        writer.write(getRdfId());
        writer.write(" a ");
        writer.write(BridgeDBConstants.PREFIX_NAME);        
        writer.write(BridgeDBConstants.URI_PATTERN);        
        writer.write(";");        
        writer.newLine();
   
        if (postfix != null){
            writer.write("         ");
            writer.write(BridgeDBConstants.PREFIX_NAME);        
            writer.write(BridgeDBConstants.POSTFIX);
            writer.write(" \"");
            writer.write(postfix);
            writer.write("\";");
            writer.newLine();
        }

        writer.write("         ");
        writer.write(VoidConstants.PREFIX_NAME);
        writer.write(VoidConstants.URI_SPACE);
        writer.write(" \"");
        writer.write(nameSpace);
        writer.write("\".");
        writer.newLine();
    }
    
    public String getUriPattern() {
        if (postfix == null){
            return nameSpace + "$id";
        } else {
            return nameSpace + "$id" + postfix;
        }
    }

    @Override
    public String toString(){
        return getUriPattern();      
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
