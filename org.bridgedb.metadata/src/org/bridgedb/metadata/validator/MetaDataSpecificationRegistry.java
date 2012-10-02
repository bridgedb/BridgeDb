/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.validator;

import java.io.InputStream;
import java.util.HashMap;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.metadata.MetaDataSpecification;
import org.bridgedb.metadata.utils.InputStreamFinder;

/**
 *
 * @author Christian
 */
public class MetaDataSpecificationRegistry {
    
    private static HashMap<ValidatrionType,MetaDataSpecification>  specificationByValidationType = 
            new HashMap<ValidatrionType,MetaDataSpecification>();
    
    public static MetaDataSpecification getMetaDataSpecificationByValidatrionType(ValidatrionType type) throws MetaDataException{
        if (!specificationByValidationType.containsKey(type)){
            InputStream stream = InputStreamFinder.findByName(type.getOwlFileName(), new MetaDataSpecificationRegistry());
            MetaDataSpecification specifation = new MetaDataSpecification(stream);
            specificationByValidationType.put(type, specifation);
        }
        return specificationByValidationType.get(type);
    }
}
