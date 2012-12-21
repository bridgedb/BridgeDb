/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import org.bridgedb.rdf.reader.StatementReader;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public abstract class RdfBase {
    static final boolean VERSION2 = true;

    static String scrub(String original){
        String result = original.replaceAll("\\W", "_");
        while(result.contains("__")){
            result = result.replace("__", "_");
        }
        if (result.endsWith("_")){
            result = result.substring(0, result.length()-1);
        }
        return result;
    }
    
    static String convertToShortName(Value value) {
        String id = value.stringValue();
        id = id.replace(StatementReader.DEFAULT_BASE_URI, ":");
        return id;
    }


}
