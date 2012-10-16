/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

/**
 *
 * @author Christian
 */
public enum RequirementLevel {
    MINIMAL, MUST, DIRECTMUST, SHOULD, MAY, UNSPECIFIED;
    
    public static RequirementLevel parseString(String string) throws MetaDataException{
       string = string.trim();
       string = string.replaceAll("\"", "");
       for(RequirementLevel type:RequirementLevel.values()){
           if (type.toString().equalsIgnoreCase(string)){
               return type;
           }
       }
       throw new MetaDataException ("Unable to parse " + string + " to a ValidationType. "
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
