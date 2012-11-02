/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.File;
import java.io.InputStream;
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
    public String validateString(String source, String info, RDFFormat format, StoreType storeType, 
            ValidationType validationType, boolean includeWarnings) throws IDMapperException;
    
    public String validateInputStream(String source, InputStream inputStream, RDFFormat format, StoreType storeType, 
            ValidationType validationType, boolean includeWarnings) throws IDMapperException;

    public String validateStringAsVoid(String source, String info, String mimeType) throws IDMapperException;
    
    public String validateInputStreamAsVoid(String source, InputStream inputStream, String mimeType) throws IDMapperException;

    //public String validateStringAsLinksetVoid(String info, String mimeType) throws IDMapperException;
    
    public String validateStringAsLinks(String source, String info, String mimeType) throws IDMapperException;
    
    public String validateInputStreamAsLinks(String source, InputStream inputStream, String mimeType) throws IDMapperException;

    public void loadString(String source, String info, RDFFormat format, StoreType storeType, ValidationType validationType) 
            throws IDMapperException;
    
    public void checkStringValid(String source, String info, RDFFormat format, StoreType storeType, ValidationType validationType) 
            throws IDMapperException;
        
}
