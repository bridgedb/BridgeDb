package org.bridgedb.metadata.validator;

import org.bridgedb.metadata.MetaDataException;

/**
 *
 * @author Christian
 */
public enum ValidationType {
    LINKSETVOID ("shouldLinkSet.owl", false),
    DATASETVOID ("shouldDataSet.owl", false),
    LINKS("LinkSet.owl", true),
    //todo make minal set
    LINKSMINIMAL("minLinkSet.owl", true);
   
    private String owlFile;
    private boolean linkset;
    
    private ValidationType(String owlFile, boolean linkset){
        this.owlFile = owlFile;
        this.linkset = linkset;
    }
    
    public String getOwlFileName(){
        return owlFile;
    }
    
    public static ValidationType parseString(String string) throws MetaDataException{
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
    
    public boolean isLinkset(){
        return linkset;
    }
}
