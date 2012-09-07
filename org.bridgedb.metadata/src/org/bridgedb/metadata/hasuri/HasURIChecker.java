/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.hasuri;

import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public class HasURIChecker {
    
    public static boolean legalValue(Value value, Class hasUriEnumClass){
        Object[] hasUris = hasUriEnumClass.getEnumConstants();
        for (Object object:hasUris){
           HasURI hasUri = (HasURI)object;
           if (hasUri.getURI().equals(value)){
               return true;
           }
        }
        return false;
    }
    
}
