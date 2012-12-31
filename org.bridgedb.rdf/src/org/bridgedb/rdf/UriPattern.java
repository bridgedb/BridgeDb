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
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.rdf.constants.VoidConstants;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class UriPattern extends RdfBase {

    private final String nameSpace;
    private final String postfix;

    private static HashMap<String, UriPattern> register = new HashMap<String, UriPattern>();
    private static HashMap<String,UriPattern> byNameSpaceOnly = new HashMap<String,UriPattern>();
    private static HashMap<String,HashMap<String,UriPattern>> byNameSpaceAndPostFix = 
            new HashMap<String,HashMap<String,UriPattern>> ();  

    private UriPattern(String namespace){
        this.nameSpace = namespace;
        this.postfix = null;
        byNameSpaceOnly.put(namespace, this);
        register.put(getRdfId(), this);
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
        register.put(getRdfId(), this);
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
        return byNameSpaceAndPostFix(nameSpace, postfix);
    }

    public static UriPattern byNameSpaceAndPostFix(String nameSpace, String postfix) throws BridgeDBException{
        if (postfix.isEmpty()){
            return byNameSpace(nameSpace);
        } else {
            return byNameSpaceAndPostfix(nameSpace, postfix);
        }
    }
    
    static UriPattern byRdfResource(Value uriPatternId) throws BridgeDBException {
        String shortName = convertToShortName(uriPatternId);
        UriPattern result =  register.get(shortName);
        if (result == null){
            throw new BridgeDBException("No UriPattern known for Id " + uriPatternId + " / " + shortName);
        }
        return result;
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

    public static void writeAllAsRDF(BufferedWriter writer) throws IOException {
        for (UriPattern uriPattern:register.values()){
            uriPattern.writeAsRDF(writer);
        }
    }
    
    public void writeAsRDF(BufferedWriter writer) throws IOException{
        writer.write(getRdfId());
        writer.write(" a ");
        writer.write(BridgeDBConstants.URI_PATTERN_SHORT);        
        writer.write(";");        
        writer.newLine();
   
        if (postfix != null){
            writer.write("         ");
            writer.write(BridgeDBConstants.POSTFIX_SHORT);
            writer.write(" \"");
            writer.write(postfix);
            writer.write("\";");
            writer.newLine();
        }

        writer.write("         ");
        writer.write(VoidConstants.URI_SPACE_SHORT);
        writer.write(" \"");
        writer.write(nameSpace);
        writer.write("\".");
        writer.newLine();
    }
    
    public static UriPattern readRdf(Resource patternId, Set<Statement> uriPatternStatements) throws BridgeDBException {
        String nameSpace = null;
        String postfix = null;
        for (Statement statement:uriPatternStatements){
            if (statement.getPredicate().equals(BridgeDBConstants.POSTFIX_URI)){
                postfix = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(VoidConstants.URI_SPACE_URI)){
                nameSpace = statement.getObject().stringValue();
            }
        }
        if (nameSpace == null){
            throw new BridgeDBException ("uriPattern " + patternId + " does not have a " + VoidConstants.URI_SPACE_URI);
        } 
        UriPattern pattern;
        if (postfix == null){
            pattern = UriPattern.byNameSpace(nameSpace);
        } else {
            pattern = UriPattern.byNameSpaceAndPostFix(nameSpace, postfix);
        }
        return pattern;
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
