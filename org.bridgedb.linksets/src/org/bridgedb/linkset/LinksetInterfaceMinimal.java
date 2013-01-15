/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.File;
import java.io.InputStream;
import org.bridgedb.IDMapperException;
import org.bridgedb.tools.metadata.validator.ValidationType;
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

    public String loadString(String source, String info, RDFFormat format, StoreType storeType, ValidationType validationType) 
            throws IDMapperException;
    
    public String saveString(String source, String info, RDFFormat format, StoreType storeType, ValidationType validationType) 
            throws IDMapperException;
    
    public String loadInputStream(String source, InputStream inputStream, RDFFormat format, StoreType storeType, 
            ValidationType validationType) throws IDMapperException;

    public String saveInputStream(String source, InputStream inputStream, RDFFormat format, StoreType storeType, 
            ValidationType validationType) throws IDMapperException;

    public void checkStringValid(String source, String info, RDFFormat format, StoreType storeType, ValidationType validationType) 
            throws IDMapperException;
        
    public void checkInputStreamValid(String source, InputStream inputStream, RDFFormat format, StoreType storeType, 
            ValidationType validationType) throws IDMapperException;
}
