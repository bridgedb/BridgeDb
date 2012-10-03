package org.bridgedb.metadata.validator;

import org.bridgedb.metadata.MetaDataException;

/**
 *
 * @author Christian
 */
public enum ValidationType {
    LINKSET ("shouldLinkSet.owl"),
    DATASET ("shouldDataSet.owl");
    
    private String owlFile;
    
    private ValidationType(String owlFile){
        this.owlFile = owlFile;
    }
    
    public String getOwlFileName(){
        return owlFile;
    }
    
    public ValidationType parseString(String string) throws MetaDataException{
       for(ValidationType type:ValidationType.values()){
           if (type.toString().equalsIgnoreCase(string)){
               return type;
           }
       }
       throw new MetaDataException ("Unable to parse " + string + " to a ValidationType. "
               + "Legal values are " + valuesString());
    }
    
    public static String valuesString(){
        String result = ValidationType.values()[0].toString();
        for (int i = 1; i< ValidationType.values().length; i++){
            result = result + ", " + ValidationType.values()[i].toString();
        }
        return result;
    }
}
