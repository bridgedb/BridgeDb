/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.bridgedb.linkset.constants.HasURI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public class HasURIChecker {
    
    public static boolean legalValue(Value value, Class hasUriEnumClass){
        System.out.println("checking");
        Object[] hasUris = hasUriEnumClass.getEnumConstants();
        System.out.println(hasUris.length);
        for (Object object:hasUris){
           HasURI hasUri = (HasURI)object;
           if (hasUri.getURI().equals(value)){
               return true;
           } else {
              System.out.println(hasUri.getURI() + " ~ " +  value);
           }
        }
        return false;
    }
    
}
