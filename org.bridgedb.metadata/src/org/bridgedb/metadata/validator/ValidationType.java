package org.bridgedb.metadata.validator;

import org.bridgedb.metadata.MetaDataException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public enum ValidationType {
    ANY_RDF ("LinkSet.owl", "Rdf", "http://example.com" ,false, true),
    VOID ("LinkSet.owl", "Void", "http://rdfs.org/ns/void#Dataset", false, false),
    LINKS("LinkSet.owl", "LinkSet", "http://rdfs.org/ns/void#Linkset", true, false),
    //todo make minal set
    LINKSMINIMAL("LinkSet.owl", "Minimum", "http://rdfs.org/ns/void#Linkset", true, true);
   
    private final String owlFile;
    private final String name;
    private final URI directType;
    private final boolean linkset;
    private final boolean minimal;
    
    private ValidationType(String owlFile, String name, String type, boolean linkset, boolean isMinimal){
        this.owlFile = owlFile;
        this.name = name;
        this.directType = new URIImpl(type);
        this.linkset = linkset;
        this.minimal = isMinimal;
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
    
    public String getOwlFileName(){
        return owlFile;
    }
    
    public String getName(){
        return name;
    }
    
    public URI getDirectType(){
        return directType;
    }
    
    public boolean isLinkset(){
        return linkset;
    }
    
    public boolean isMinimal(){
        return this.minimal;
    }
}
