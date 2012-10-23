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
public interface LinksetInterface {
    public String validateString(String info, RDFFormat format, StoreType storeType, 
            ValidationType validationType, boolean includeWarnings) throws IDMapperException;
    
    public String validateStringAsDatasetVoid(String info, String mimeType) throws IDMapperException;
    
    public String validateStringAsLinksetVoid(String info, String mimeType) throws IDMapperException;
    
    public String validateStringAsLinkset(String info, String mimeType) throws IDMapperException;
    
    public String validateFile(String fileName, StoreType storeType, ValidationType type, boolean includeWarnings) 
            throws IDMapperException;
    
    public String validateFileAsDatasetVoid(String fileName) throws IDMapperException;
    
    public String validateFileAsLinksetVoid(String fileName) throws IDMapperException;
    
    public String validateFileAsLinkset(String fileName) throws IDMapperException; 

    public void load(String info, RDFFormat format, URI accessedFrom, StoreType storeType, 
            ValidationType validationType) throws IDMapperException;
    
    public void load(String fileName, StoreType storeType, ValidationType type) throws IDMapperException;
    
    public void validate(String info, RDFFormat format, URI accessedFrom, StoreType storeType, 
            ValidationType validationType) throws IDMapperException;
    
    public void validate(String fileName, StoreType storeType, ValidationType type) throws IDMapperException;
    
    public void clearExistingData (StoreType storeType) throws IDMapperException;
    
}
