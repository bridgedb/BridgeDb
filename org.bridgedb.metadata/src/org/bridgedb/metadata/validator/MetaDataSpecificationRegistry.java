/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.validator;

import java.io.InputStream;
import java.util.HashMap;
import org.bridgedb.IDMapperException;
import org.bridgedb.metadata.MetaDataSpecification;
import org.bridgedb.utils.InputStreamFinder;

/**
 *
 * @author Christian
 */
public class MetaDataSpecificationRegistry {
    
    private static HashMap<ValidationType,MetaDataSpecification>  specificationByValidationType = 
            new HashMap<ValidationType,MetaDataSpecification>();
    
    public static MetaDataSpecification getMetaDataSpecificationByValidatrionType(ValidationType type) throws IDMapperException{
        if (!specificationByValidationType.containsKey(type)){
            System.out.println(type);
            InputStream stream = InputStreamFinder.findByName(type.getOwlFileName(), new MetaDataSpecificationRegistry());
            MetaDataSpecification specifation = new MetaDataSpecification(stream, type.isMinimal());
            specificationByValidationType.put(type, specifation);
        }
        return specificationByValidationType.get(type);
    }
}
