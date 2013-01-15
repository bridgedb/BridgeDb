/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.tools.metadata.validator;

import java.io.InputStream;
import java.util.HashMap;
import org.bridgedb.IDMapperException;
import org.bridgedb.tools.metadata.MetaDataSpecification;

/**
 *
 * @author Christian
 */
public class MetaDataSpecificationRegistry {
    
    private static HashMap<ValidationType,MetaDataSpecification>  specificationByValidationType = 
            new HashMap<ValidationType,MetaDataSpecification>();
    
    public static MetaDataSpecification getMetaDataSpecificationByValidatrionType(ValidationType type) throws IDMapperException{
        if (!specificationByValidationType.containsKey(type)){
            MetaDataSpecification specifation = new MetaDataSpecification(type);
            specificationByValidationType.put(type, specifation);
        }
        return specificationByValidationType.get(type);
    }
}
