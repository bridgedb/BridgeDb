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
public interface LinksetInterface extends LinksetInterfaceMinimal{
    @Override
    public String validateString(String source, String info, RDFFormat format, StoreType storeType, 
            ValidationType validationType, boolean includeWarnings) throws IDMapperException;
    
    @Override
    public String validateStringAsVoid(String source, String info, String mimeType) throws IDMapperException;
    
    //@Override
    //public String validateStringAsLinksetVoid(String info, String mimeType) throws IDMapperException;
    
    @Override
    public String validateStringAsLinks(String source, String info, String mimeType) throws IDMapperException;
    
    public String validateFile(String fileName, StoreType storeType, ValidationType type, boolean includeWarnings) 
            throws IDMapperException;
    
    public String validateFileAsVoid(String fileName) throws IDMapperException;
    
    //public String validateFileAsLinksetVoid(String fileName) throws IDMapperException;
    
    public String validateFileAsLinks(String fileName) throws IDMapperException; 

    @Override
    public void loadString(String source, String info, RDFFormat format, StoreType storeType, ValidationType validationType) 
            throws IDMapperException;
    
    public void loadFile(String fileName, StoreType storeType, ValidationType type) throws IDMapperException;
    
    @Override
    public void checkStringValid(String source, String info, RDFFormat format, StoreType storeType, ValidationType validationType) 
            throws IDMapperException;
    
    public void checkFileValid(String fileName, StoreType storeType, ValidationType type) throws IDMapperException;
    
    public void clearExistingData (StoreType storeType) throws IDMapperException;
    
}
