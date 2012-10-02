package org.bridgedb.metadata.validator;

/**
 *
 * @author Christian
 */
public enum ValidatrionType {
    LINKSET ("shouldLinkSet.owl"),
    DATASET ("shouldDataSet.owl");
    
    private String owlFile;
    
    private ValidatrionType(String owlFile){
        this.owlFile = owlFile;
    }
    
    public String getOwlFileName(){
        return owlFile;
    }
    
    //public ValidatrionType parseString(String string){
    //    
    //}
}
