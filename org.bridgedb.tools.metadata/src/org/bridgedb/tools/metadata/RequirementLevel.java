/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.tools.metadata;

import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author Christian
 */
public enum RequirementLevel {
    MINIMAL, MUST, DIRECTMUST, SHOULD, MAY, IGNORE, UNSPECIFIED;
    
    public static RequirementLevel parseString(String string) throws BridgeDBException{
       string = string.trim();
       string = string.replaceAll("\"", "");
       for(RequirementLevel type:RequirementLevel.values()){
           if (type.toString().equalsIgnoreCase(string)){
               return type;
           }
       }
       throw new BridgeDBException ("Unable to parse " + string + " to a ValidationType. "
               + "Legal values are " + valuesString());
    }
    
    public static String valuesString(){
        String result = RequirementLevel.values()[0].toString();
        for (int i = 1; i< RequirementLevel.values().length; i++){
            result = result + ", " + RequirementLevel.values()[i].toString();
        }
        return result;
    }
    

}
