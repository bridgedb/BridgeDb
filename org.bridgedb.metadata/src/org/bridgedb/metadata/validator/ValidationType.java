package org.bridgedb.metadata.validator;

import org.bridgedb.metadata.MetaDataException;

/**
 *
 * @author Christian
 */
public enum ValidationType {
    LINKSETVOID ("LinkSet.owl", false, false),
    DATASETVOID ("shouldDataSet.owl", false, false),
    LINKS("LinkSet.owl", true, false),
    //todo make minal set
    LINKSMINIMAL("LinkSet.owl", true, true);
   
    private final String owlFile;
    private final boolean linkset;
    private final boolean minimal;
    
    private ValidationType(String owlFile, boolean linkset, boolean isMinimal){
        this.owlFile = owlFile;
        this.linkset = linkset;
        this.minimal = isMinimal;
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
    
    public boolean isMinimal(){
        return this.minimal;
    }
}
