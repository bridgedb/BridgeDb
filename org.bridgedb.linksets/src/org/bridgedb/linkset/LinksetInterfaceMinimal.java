/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.File;
import org.bridgedb.IDMapperException;
import org.bridgedb.metadata.validator.ValidationType;
import org.bridgedb.utils.StoreType;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Christian
 */
public interface LinksetInterfaceMinimal {
    public String validateString(String info, RDFFormat format, StoreType storeType, 
            ValidationType validationType, boolean includeWarnings) throws IDMapperException;
    
    public String validateStringAsVoid(String info, String mimeType) throws IDMapperException;
    
    //public String validateStringAsLinksetVoid(String info, String mimeType) throws IDMapperException;
    
    public String validateStringAsLinks(String info, String mimeType) throws IDMapperException;
    
    public void loadString(String info, RDFFormat format, StoreType storeType, ValidationType validationType) 
            throws IDMapperException;
    
    public void checkStringValid(String info, RDFFormat format, StoreType storeType, ValidationType validationType) 
            throws IDMapperException;
        
}
